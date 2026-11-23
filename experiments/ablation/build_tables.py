#!/usr/bin/env python3
"""Scores the ablation variants in raw/<variant>/findings.json on CryptoAPI-Bench and writes tables/.

  tables/summary.csv  precision, recall, and F1 for the three variants in Table 7
  tables/cases.csv    one row per scored case and rule, with each variant's verdict and the case type

Scoring uses the same case and rule matching as the main CryptoAPI-Bench results.
"""
from __future__ import annotations

import collections
import csv
import json
import os
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent


def locate(env: str, *candidates: Path) -> Path:
    if os.environ.get(env):
        return Path(os.environ[env])
    return next((c for c in candidates if c.exists()), candidates[0])


ARTIFACT = locate("ARTIFACT_DIR", HERE.parents[1] / "CryptoEagle-Artifact", HERE.parents[1])
EXT = locate("EXT_DIR", HERE.parents[1] / "crypto-ext", ARTIFACT / "tool" / "crypto-ext")
XLSX = locate("CRYPTOAPI_XLSX", HERE.parents[1] / "CryptoAPI-Bench-master" / "CryptoAPI-Bench_details.xlsx",
              ARTIFACT / "benchmarks" / "CryptoAPI-Bench" / "CryptoAPI-Bench_details.xlsx")
EVAL = ARTIFACT / "scripts" / "eval"
sys.path.insert(0, str(EVAL))
from scoring_workspace import ScoringWorkspace
VARIANTS = [("string-match", "Base (string matching)"),
            ("no-provenance", "+Ontology"),
            ("full", "+Provenance (full)")]
CASE_TYPES = [("bbcase", "BB: basic, one method"), ("abicase", "ABI: interprocedural"), ("abscase", "ABS: field"),
              ("abmc", "ABMC: across classes"), ("abhcase", "ABH: helper method"), ("abpscase", "ABPS: path-sensitive")]


def metrics(tp: int, fp: int, fn: int) -> dict:
    fmt = lambda v: "" if v is None else f"{v:.3f}"
    p = tp / (tp + fp) if tp + fp else None
    r = tp / (tp + fn) if tp + fn else None
    return dict(Precision=fmt(p), Recall=fmt(r),
                F1=fmt(2 * tp / (2 * tp + fp + fn) if tp + fp + fn else None))


def case_type(files: str) -> str:
    return next((label for key, label in CASE_TYPES if key in files), "other")


def build(workspace: ScoringWorkspace) -> None:
    out = HERE / "tables"
    out.mkdir(exist_ok=True)
    summary, verdicts, files = [], collections.defaultdict(dict), {}
    tmp = workspace.path
    for key, label in VARIANTS:
        findings = HERE / "raw" / key / "findings.json"
        scored = tmp / f"{key}.json"
        workspace.run_python(EXT / "scripts" / "evaluate_benchmark.py", "java", XLSX, findings, "--scope=all", f"--output={scored}")
        result = json.loads(scored.read_text())
        for rule in result["perRule"]:
            for verdict, field in (("TP", "true_positive_cases"), ("FP", "false_positive_cases"),
                                   ("FN", "false_negative_cases"), ("TN", "true_negative_cases")):
                for case in rule[field]:
                    case_id, case_files = case.split(":", 1)
                    verdicts[(case_id, rule["rule_id"])][key] = verdict
                    files[(case_id, rule["rule_id"])] = case_files
        counts = result["summary"]
        summary.append(dict(Variant=label, **metrics(counts["tp"], counts["fp"], counts["fn"])))
    rows = [dict(case_id=case_id, rule=rule, files=files[(case_id, rule)], case_type=case_type(files[(case_id, rule)]),
                 **{key: v.get(key, "") for key, _ in VARIANTS})
            for (case_id, rule), v in sorted(verdicts.items(), key=lambda kv: (int(kv[0][0].split("-")[-1]), kv[0][1]))]
    for name, data in (("summary.csv", summary), ("cases.csv", rows)):
        with (out / name).open("w", newline="", encoding="utf-8-sig") as handle:
            writer = csv.DictWriter(handle, fieldnames=list(data[0]))
            writer.writeheader()
            writer.writerows(data)
    for row in summary:
        print(row)
    by_type = collections.defaultdict(collections.Counter)
    for row in rows:
        for key, _ in VARIANTS:
            by_type[(row["case_type"], key)][row[key]] += 1
    for case_type_label, _ in [(label, None) for _, label in CASE_TYPES] + [("other", None)]:
        line = {key: dict(by_type[(case_type_label, key)]) for key, _ in VARIANTS if by_type[(case_type_label, key)]}
        if line:
            print(case_type_label, line)


def main() -> None:
    with ScoringWorkspace("ablation-score") as workspace:
        build(workspace)


if __name__ == "__main__":
    main()
