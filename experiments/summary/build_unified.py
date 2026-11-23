#!/usr/bin/env python3
"""One compact case table per dataset (cases/<dataset>.csv), from crypto-ext with the C1-C18 rule set.

Every row is one ground-truth case and answers: what the truth is, which rule it maps to, what the tool reported for it,
the verdict, and why. Inputs are the per-dataset evaluations already built from the same tool version:

  ../benchmarks/tables/         CryptoAPI-Bench, PyCryptoBench, ApacheCryptoAPI-Bench
  ../cryptogpt/tables/          CryptoGPT Java and Python report-instance verdicts
  ../cryptollm-cases/out/        frozen CryptoLLM on 181 ground-truth cases; multi-file cases are scored once
"""
from __future__ import annotations

import argparse
import collections
import csv
import json
import os
import re
from pathlib import Path

HERE = Path(__file__).resolve().parent


def locate(env: str, *candidates: Path) -> Path:
    if os.environ.get(env):
        return Path(os.environ[env])
    return next((c for c in candidates if c.is_dir()), candidates[0])


RESULTS = HERE.parent
BENCH = locate("BENCH_EVAL", RESULTS / "benchmarks")
GPT = locate("CRYPTOGPT_EVAL", RESULTS / "cryptogpt")
LLM = locate("CRYPTOLLM_EVAL", RESULTS / "cryptollm-cases")
OUT = HERE / "cases"
SCORED = ("TP", "FP", "FN", "TN")


def read(path: Path) -> list[dict]:
    with path.open(encoding="utf-8-sig") as handle:
        return list(csv.DictReader(handle))


def short(code: str, width: int = 90) -> str:
    code = re.sub(r"\s+", " ", code or "").strip()
    return code if len(code) <= width else code[: width - 3] + "..."


def describe(alerts: dict[str, dict], ids: str, line_key: str, code_key: str) -> str:
    parts = [f"{alerts[i]['rule']} L{alerts[i][line_key]}: {short(alerts[i][code_key])}" for i in ids.split() if i in alerts]
    return " | ".join(parts)


def source_names() -> dict[str, str]:
    """Matching keys are lower-cased file stems; map them back to the real file names for display."""
    roots = [RESULTS.parent / "CryptoAPI-Bench-master", RESULTS.parent / "PyCryptoBench",
             RESULTS.parent / "benchmarks" / "CryptoAPI-Bench", RESULTS.parent / "benchmarks" / "PyCryptoBench"]
    return {p.stem.lower(): p.name for root in roots if root.is_dir() for p in root.rglob("*") if p.suffix in (".java", ".py")}


NAMES = source_names()


def benchmark(name: str) -> list[dict]:
    alerts = {a["alert_id"]: a for a in read(BENCH / "tables" / f"{name}-alerts.csv")}
    rows = []
    for c in read(BENCH / "tables" / f"{name}-cases.csv"):
        files = " ".join(NAMES.get(f, f) for f in c["file"].split())
        reason = c["reason"]
        if c["verdict"] == "TP" and reason.startswith("Detected:"):
            reason = f"The tool reports {c['rule']} in this case's file, matching the labelled misuse type."
        truth = f"{c['category']}: {c['label']}"
        if c.get("benchmark_code"):
            truth += f" (`{short(c['benchmark_code'], 60)}`)"
        reported = describe(alerts, c["matched_alerts"], "line", "source_code_at_line")
        if not reported and c["verdict"] in ("FN", "TN"):
            reported = f"no {c['rule']} alert"
        rows.append(dict(case_id=c["case_id"], file=files, ground_truth=truth, expected_rule=c["rule"] or "-",
                         tool_reported=reported or "-", verdict=c["verdict"], reason=reason))
    return rows


def cryptogpt(language: str) -> list[dict]:
    alerts = {a["alert_id"]: a for a in read(GPT / "tables" / "alerts.csv")}
    rows = []
    for c in read(GPT / "tables" / "cases.csv"):
        sheet = c["case_id"].rsplit("-row-", 1)[0]
        if (sheet == "Java") != (language == "java"):
            continue
        reported = describe(alerts, c["matched_alerts"], "line", "code")
        if not reported and c["verdict"] in ("FN", "TN"):
            reported = f"no {c['rule']} alert at the reported instance"
        reason = c["reason"]
        if reason.startswith("Detected at the reported instance by"):
            reason = "The alert is at the API call the report names, with the report's misuse type."
        rows.append(dict(case_id=c["case_id"], file=c["file"], ground_truth=f"{c['category']}: {c['label']}",
                         expected_rule=c["rule"] or "-", tool_reported=reported or "-", verdict=c["verdict"],
                         reason=reason))
    return rows


def cryptollm() -> list[dict]:
    cases = read(LLM / "out/case-results.csv")
    manifest = json.loads((LLM / "inputs/cases.json").read_text())["cases"]
    case_ids_by_number = {c["code_number"]: c["case_id"] for c in manifest}
    assert len(case_ids_by_number) == len(manifest), "code_number must uniquely identify a case"
    predictions = json.loads((LLM / "out/predictions.json").read_text())["cacpg"]["predictions"]
    notes_path = LLM / "out/audit-reasons.json"
    notes = json.loads(notes_path.read_text()) if notes_path.exists() else {}
    rows = []
    for case in cases:
        if case["front_end"] != "CA-CPG front end":
            continue
        cid, verdict = case_ids_by_number[case["code_number"]], case["verdict"]
        votes = predictions[cid]["slice_preds"]
        reported = f"{case['slices']} slice(s), {votes.count(1)} classified as misuse -> case " + ("misuse" if case["prediction"] == "1" else "benign")
        if case["source_status"] == "missing_source":
            reason = "Source unavailable: " + case["missing_files"] + "; retained in the 181-case denominator, no model input, counted as FN."
        elif verdict == "FN":
            reason = "No slice reaches the model." if not votes else "Every case slice is classified benign by the frozen model."
        elif verdict == "TP":
            reason = "At least one slice from this case is classified as misuse; one binary verdict for the whole case."
        elif verdict == "TN":
            reason = "No case slice is classified as misuse."
        else:
            reason = notes.get(cid, "")
            if not reason:
                raise ValueError("FP without audited reason: " + cid)
        rows.append(dict(case_id=cid, file=case["files"] or case["missing_files"],
                         ground_truth=case["category"] + ": " + case["label"],
                         tool_reported=reported, verdict=verdict, reason=reason))
    return rows


def metrics(rows: list[dict], column: str = "verdict") -> dict:
    c = collections.Counter(r[column] for r in rows)
    tp, fp, fn, tn = (c[k] for k in SCORED)
    p = tp / (tp + fp) if tp + fp else None
    r = tp / (tp + fn) if tp + fn else None
    f1 = 2 * tp / (2 * tp + fp + fn) if tp + fp + fn else None
    fmt = lambda v: "" if v is None else f"{v:.3f}"
    return dict(cases=len(rows), scored=tp + fp + fn + tn, TP=tp, FP=fp, FN=fn, TN=tn,
                Precision=fmt(p), Recall=fmt(r), F1=fmt(f1))


def write(path: Path, rows: list[dict]) -> None:
    with path.open("w", newline="", encoding="utf-8-sig") as handle:
        writer = csv.DictWriter(handle, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apache-only", action="store_true", help="Rebuild only cases/apache.csv")
    args = parser.parse_args()
    OUT.mkdir(exist_ok=True)
    if args.apache_only:
        rows = benchmark("apache")
        write(OUT / "apache.csv", rows)
        print("apache", metrics(rows))
        return
    datasets = [("cryptoapi-bench", benchmark("cryptoapi-bench")), ("pycryptobench", benchmark("pycryptobench")),
                ("apache", benchmark("apache")), ("cryptogpt-java", cryptogpt("java")),
                ("cryptogpt-python", cryptogpt("python")), ("cryptollm-cacpg", cryptollm())]
    for key, rows in datasets:
        write(OUT / f"{key}.csv", rows)
        print(key, metrics(rows))


if __name__ == "__main__":
    main()
