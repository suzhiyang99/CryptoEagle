#!/usr/bin/env python3
"""Per-case verdicts of every tool on CryptoAPI-Bench and ApacheCryptoAPI-Bench, on the ground truth of the case tables.

  cases/tools-cryptoapi-bench.csv  173 case x rule units: CryptoEagle (crypto-ext, C1-C18) and the saved raw outputs of
                                   CryptoGuard, CogniCrypt and SpotBugs+FindSecBugs (../tool-comparison/raw). Native
                                   alerts are mapped to C1-C18 by scripts/eval/map_to_cryptoql.py and scored by
                                   crypto-ext/scripts/evaluate_benchmark.py, exactly like CryptoEagle.
  cases/tools-apache.csv           94 scored cases: CryptoEagle from cases/apache.csv; CryptoGuard and CogniCrypt from
                                   fresh Apache native reports scored with the current rule mapping, restricted to the same cases.
paper_tables.py counts these verdicts for the paper's tool-comparison tables.
"""
from __future__ import annotations

import argparse
import csv
import hashlib
import json
import os
import re
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent
OUT = HERE / "cases"


def locate(env: str, *candidates: Path) -> Path:
    if os.environ.get(env):
        return Path(os.environ[env])
    return next((c for c in candidates if c.exists()), candidates[0])


ARTIFACT = locate("ARTIFACT_DIR", HERE.parents[1] / "CryptoEagle-Artifact", HERE.parents[1])
EXT = locate("EXT_DIR", HERE.parents[1] / "crypto-ext", ARTIFACT / "tool" / "crypto-ext")
XLSX = locate("CRYPTOAPI_XLSX", HERE.parents[1] / "CryptoAPI-Bench-master" / "CryptoAPI-Bench_details.xlsx",
              ARTIFACT / "benchmarks" / "CryptoAPI-Bench" / "CryptoAPI-Bench_details.xlsx")
EAGLE = locate("EAGLE_FINDINGS", HERE.parent / "benchmarks" / "raw" / "cryptoapi-bench" / "findings.json",
               ARTIFACT / "results" / "benchmarks" / "raw" / "cryptoapi-bench" / "findings.json")
# Saved raw outputs of the four baselines and their Apache per-case verdicts (../tool-comparison).
APACHE_BASELINE = HERE.parent / "tool-comparison" / "inputs" / "apache-baseline-verdicts.csv"
RAW = HERE.parent / "tool-comparison" / "raw"
EVAL = ARTIFACT / "scripts" / "eval"
sys.path.insert(0, str(EVAL))
from scoring_workspace import ScoringWorkspace
TOOLS = [("cryptoeagle", "CryptoEagle (this work)", EAGLE), ("cryptoguard", "CryptoGuard", RAW / "cryptoguard.csv"),
         ("cognicrypt", "CogniCrypt", RAW / "cognicrypt" / "CryptoAnalysis-Report.csv"),
         ("spotbugs", "SpotBugs+FindSecBugs", RAW / "spotbugs.xml")]


def read(path: Path) -> list[dict]:
    with path.open(encoding="utf-8-sig") as handle:
        return list(csv.DictReader(handle))


def write(path: Path, rows: list[dict]) -> None:
    with path.open("w", newline="", encoding="utf-8-sig") as handle:
        writer = csv.DictWriter(handle, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)


def cryptoapi_bench(work: ScoringWorkspace) -> list[dict]:
    verdicts = {}
    for key, label, raw in TOOLS:
        mapped, scored = work.path / f"{key}-mapped.json", work.path / f"{key}-eval.json"
        work.run_python(EVAL / "map_to_cryptoql.py", key, raw, mapped)
        work.run_python(EXT / "scripts" / "evaluate_benchmark.py", "java", XLSX, mapped, "--scope=all", f"--output={scored}")
        result = json.loads(scored.read_text())
        for rule in result["perRule"]:
            for verdict, field in (("TP", "true_positive_cases"), ("FP", "false_positive_cases"),
                                   ("FN", "false_negative_cases"), ("TN", "true_negative_cases")):
                for case in rule[field]:
                    verdicts[(case.split(":")[0], rule["rule_id"], key)] = verdict
    cases = []
    for row in read(OUT / "cryptoapi-bench.csv"):
        if row["verdict"] not in ("TP", "FP", "FN", "TN"):
            continue
        cases.append(dict(case_id=row["case_id"], file=row["file"], ground_truth=row["ground_truth"], rule=row["expected_rule"],
                          **{label: verdicts.get((row["case_id"], row["expected_rule"], key), "?") for key, label, _ in TOOLS}))
    missing = [c for c in cases if "?" in c.values()]
    assert not missing, missing[:3]
    assert all(c["CryptoEagle (this work)"] == r["verdict"] for c, r in
               zip(cases, [r for r in read(OUT / "cryptoapi-bench.csv") if r["verdict"] in ("TP", "FP", "FN", "TN")]))
    return cases


def apache():
    eagle = {r["case_id"]: r for r in read(OUT / "apache.csv") if r["verdict"] in ("TP", "FP", "FN", "TN")}
    evaluation = HERE.parent / "tool-comparison/raw/apache/evaluation.json"
    if not evaluation.exists():
        raise ValueError("Fresh Apache baseline evaluation is missing; run tool-comparison/run_apache.py first")
    provenance = json.loads(evaluation.read_text())
    checks = {"mapping_sha256": EVAL / "map_to_cryptoql.py",
              "rule_schema_sha256": EXT / "scripts/rule_schema.py",
              "verdicts_sha256": APACHE_BASELINE,
              "source_cases_sha256": HERE.parent / "benchmarks/tables/apache-cases.csv",
              "run_metadata_sha256": evaluation.parent / "run-metadata.json"}
    for field, path in checks.items():
        if hashlib.sha256(path.read_bytes()).hexdigest() != provenance[field]:
            raise ValueError(f"Apache baseline evaluation is stale ({field}); rerun score_apache.py")
    baseline = {r["case_id"]: r for r in read(APACHE_BASELINE)}
    cases = []
    for case_id, r in eagle.items():
        b = baseline[case_id]
        assert (b["ground_truth"] == "misuse") == r["ground_truth"].endswith(": misuse") or "(`" in r["ground_truth"]
        assert b["file"] == r["file"] and set(b["expected_rules"].split()) == set(re.findall(r"C\d+", r["expected_rule"]))
        cases.append({"case_id": case_id, "file": r["file"], "ground_truth": r["ground_truth"], "rule": r["expected_rule"],
                      "CryptoEagle (this work)": r["verdict"], "CryptoGuard": b["cryptoguard"], "CogniCrypt": b["cognicrypt"]})
    for tool in ("CryptoEagle (this work)", "CryptoGuard", "CogniCrypt"):
        assert {x[tool] for x in cases} <= {"TP", "FP", "FN", "TN"}, tool
    return cases


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apache-only", action="store_true", help="Rebuild only cases/tools-apache.csv")
    args = parser.parse_args()
    if not args.apache_only:
        with ScoringWorkspace("java-tool-score") as workspace:
            bench_cases = cryptoapi_bench(workspace)
            write(OUT / "tools-cryptoapi-bench.csv", bench_cases)
            print(f"tools-cryptoapi-bench.csv ({len(bench_cases)} units)")
    apache_cases = apache()
    write(OUT / "tools-apache.csv", apache_cases)
    print(f"tools-apache.csv ({len(apache_cases)} units)")


if __name__ == "__main__":
    main()
