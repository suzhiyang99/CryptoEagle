#!/usr/bin/env python3
"""One explicitly labelled spreadsheet row is one sample; never split multi-file cases."""
import hashlib
import json
import re
import shutil
import sys
from collections import Counter
from pathlib import Path

HERE = Path(__file__).resolve().parents[1]
ROOT = HERE.parents[1]
BENCH = ROOT / "benchmarks/CryptoAPI-Bench"
sys.path.insert(0, str(ROOT / "tool/crypto-ext/scripts"))
from evaluate_benchmark import read_xlsx_rows

# The spreadsheet calls this pair ABPM; both classes, the matching main/go
# calls and the supplied byte seed are present under ABMC in the checkout.
ALIASES = {"PredictableSeedsABPMCase2.java": "PredictableSeedsABMCCase2.java",
           "PredictableSeedsABPM2.java": "PredictableSeedsABMC2.java"}


def main():
    available = {p.name: p for p in (BENCH / "src/main/java").rglob("*.java")}
    rows = read_xlsx_rows(BENCH / "CryptoAPI-Bench_details.xlsx")
    cases, excluded, membership = [], [], {}
    scope = HERE / "inputs/sources"
    if scope.exists():
        shutil.rmtree(scope)
    scope.mkdir(parents=True)
    for index, row in enumerate(rows[1:], 2):
        if len(row) < 4 or not str(row[1]).strip():
            continue
        raw_label = str(row[3]).strip().lower()
        if raw_label not in ("true", "false", "1", "0", "yes", "no"):
            excluded.append({"row": index, "files": str(row[1]), "reason": "empty or invalid label"})
            continue
        case = {"case_id": "xlsx-row-%d" % index, "xlsx_row": index,
                "code_number": str(row[2]), "label": raw_label in ("true", "1", "yes"),
                "vuln_type": str(row[4]), "method": str(row[5]), "line_raw": str(row[6]),
                "files": [], "missing_files": []}
        for raw in re.split(r"[,\s]+", str(row[1]).strip()):
            name = raw if raw.endswith(".java") else raw + ".java"
            resolved = ALIASES.get(name, name)
            source = available.get(resolved)
            if source is None:
                case["missing_files"].append(name)
                continue
            assert resolved not in membership, "A source file belongs to multiple cases: " + resolved
            membership[resolved] = case["case_id"]
            target = scope / case["case_id"] / source.relative_to(BENCH / "src/main/java")
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(source, target)
            case["files"].append({"name": resolved, "truth_name": raw,
                                  "path": str(target.relative_to(HERE)),
                                  "benchmark_path": str(source.relative_to(ROOT)),
                                  "sha256": hashlib.sha256(source.read_bytes()).hexdigest(),
                                  "alias": name != resolved})
        case["source_status"] = "missing_source" if case["missing_files"] else "complete"
        cases.append(case)
    assert len(cases) == 181, len(cases)
    assert Counter(c["label"] for c in cases) == {True: 144, False: 37}
    payload = {"meta": {"unit": "ground-truth case", "cases": len(cases), "positive": 144, "negative": 37,
                        "source_files": len(membership), "multi_file_cases": sum(len(c["files"]) > 1 for c in cases),
                        "missing_source_cases": sum(bool(c["missing_files"]) for c in cases),
                        "spreadsheet_sha256": hashlib.sha256((BENCH / "CryptoAPI-Bench_details.xlsx").read_bytes()).hexdigest()},
               "excluded": excluded, "cases": cases}
    (HERE / "inputs/cases.json").write_text(json.dumps(payload, ensure_ascii=False, indent=2))
    (HERE / "inputs/case-files.tsv").write_text("".join(cid + "\t" + name + "\n" for name, cid in sorted(membership.items())))
    print(json.dumps(payload["meta"], indent=2))
    for c in cases:
        if c["missing_files"]:
            print("UNAVAILABLE", c["case_id"], c["missing_files"], "retained in denominator; no source is invented")


if __name__ == "__main__":
    main()
