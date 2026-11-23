#!/usr/bin/env python3
"""Score fresh Apache native reports with current C1-C18 mappings.

Unit: current benchmark case (outer class/file + expected mapped rule).
Line/method information is retained for audit, not required for matching.
No saved historical verdicts are read by this scorer.
"""
import csv
import hashlib
import json
import re
import sys
from collections import Counter
from datetime import datetime, timezone
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
sys.path.insert(0, str(ROOT / "scripts/eval"))
from map_to_cryptoql import CRYPTOGUARD_MAP, cognicrypt_rules
from rule_schema import CORE_IDS, LEGACY_TO_CORE, RULE_SET


def read(path, delimiter=","):
    with path.open(encoding="utf-8-sig", errors="replace", newline="") as h:
        return list(csv.DictReader(h, delimiter=delimiter))


def write(path, rows):
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8-sig", newline="") as h:
        writer = csv.DictWriter(h, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main():
    raw = HERE / "raw/apache"
    run = json.loads((raw / "run-metadata.json").read_text())
    assert len(run["runs"]) == 18
    assert len({(r["tool"], r["project"]) for r in run["runs"]}) == 18
    assert all(r["status"] == "SUCCESS" for r in run["runs"])
    cases_path = HERE.parent / "benchmarks/tables/apache-cases.csv"
    cases = read(cases_path)
    assert len(cases) == 121
    alarms, mapped_rows = {}, []
    for result in run["runs"]:
        path = ROOT / result["report"]
        assert digest(path) == result["sha256"], path
        tool, project = result["tool"], result["project"]
        rows = read(path, ";" if tool == "cognicrypt" else ",")
        alarms[tool, project] = []
        for number, row in enumerate(rows, 2):
            if tool == "cryptoguard":
                location = row["IssueFullPath"]
                assert ".jar/" in location and location.endswith(".class"), location
                cls = location.split(".jar/", 1)[1][:-6].replace("/", ".")
                native = "Rule " + row["IssueRuleNumber"].strip()
                old_rules = CRYPTOGUARD_MAP.get(row["IssueRuleNumber"].strip(), [])
                line, message = "", row["IssueMessage"]
            else:
                cls, line = row["Class"].strip(), row["LineNumber"]
                native = row["ErrorType"].strip() + " " + row["ViolatedRule"].strip()
                message = row["Message"] or ""
                old_rules = cognicrypt_rules(row["ErrorType"].strip(), row["ViolatedRule"].strip(), message)
            rules = sorted({LEGACY_TO_CORE[r] for r in old_rules if r in LEGACY_TO_CORE})
            assert set(rules) <= set(CORE_IDS)
            alarm = {"tool": tool, "project": project, "raw_row": number, "class": cls,
                     "outer_class": cls.split("$")[0], "line": line, "native_rule": native,
                     "mapped_rules": " ".join(rules), "message": message, "rule_set": RULE_SET}
            alarms[tool, project].append(alarm)
            mapped_rows.append(alarm)
    output, counts = [], {t: Counter() for t in ("cryptoguard", "cognicrypt")}
    for case in cases:
        project, file = case["file"].split("/", 1)
        assert file.endswith(".java")
        cls = file[:-5].replace("/", ".").split("$")[0]
        expected = set(re.findall(r"C\d+", case["rule"]))
        assert expected <= set(CORE_IDS)
        record = {"case_id": case["case_id"], "project": project, "file": case["file"],
                  "ground_truth": case["label"], "expected_rules": " ".join(sorted(expected))}
        for tool in counts:
            if case["verdict"] not in {"TP", "FP", "FN", "TN"}:
                verdict, matched = ("NOT_RUN" if project not in run["jars"] else "CLASS_MISSING"), []
            else:
                assert (tool, project) in alarms
                matched = [a for a in alarms[tool, project] if a["outer_class"] == cls and expected & set(a["mapped_rules"].split())]
                positive = case["label"] == "misuse"
                verdict = ("TP" if matched else "FN") if positive else ("FP" if matched else "TN")
            record[tool] = verdict
            record[tool + "_matched"] = " ; ".join(
                f"row {a['raw_row']}: {a['native_rule']} -> {a['mapped_rules']}" for a in matched)
            counts[tool][verdict] += 1
        output.append(record)
    for tool, c in counts.items():
        assert sum(c[k] for k in ("TP", "FP", "FN", "TN")) == 94
    # Publish only after every report, case and rule mapping has been checked.
    write(raw / "mapped-alarms.csv", mapped_rows)
    write(HERE / "inputs/apache-baseline-verdicts.csv", output)
    metadata = {"generated_utc": datetime.now(timezone.utc).isoformat(), "rule_set": RULE_SET,
                "matching_unit": "ground-truth case: outer class/file + mapped rule; no line/method requirement",
                "cases": 121, "scored_cases": 94, "counts": {t: dict(c) for t, c in counts.items()},
                "source_cases_sha256": digest(cases_path),
                "mapping_sha256": digest(ROOT / "scripts/eval/map_to_cryptoql.py"),
                "rule_schema_sha256": digest(ROOT / "tool/crypto-ext/scripts/rule_schema.py"),
                "run_metadata_sha256": digest(raw / "run-metadata.json"),
                "verdicts_sha256": digest(HERE / "inputs/apache-baseline-verdicts.csv")}
    (raw / "evaluation.json").write_text(json.dumps(metadata, indent=2) + "\n")
    print(json.dumps(metadata["counts"], indent=2))


if __name__ == "__main__":
    main()
