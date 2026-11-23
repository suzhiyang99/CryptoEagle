#!/usr/bin/env python3
"""Evaluate CryptoEagle finding JSON against file/case-level benchmark labels.

The evaluator intentionally scores only benchmark categories with a direct semantic mapping to a CryptoQL or
ExtendedRules identifier. Unsupported categories and findings outside a rule's labelled universe are reported, not
silently counted as false positives. The implementation uses only the Python standard library, including direct XLSX
XML reading, so evaluation does not depend on a particular spreadsheet package.
"""

from __future__ import annotations

import argparse
import json
import re
import zipfile
from collections import defaultdict
from dataclasses import asdict, dataclass
from pathlib import Path
from typing import Iterable
from xml.etree import ElementTree
import sys
sys.path.insert(0, str(Path(__file__).resolve().parent))
from rule_schema import CORE_IDS, LEGACY_TO_CORE, RULE_SET, migrate_findings


JAVA_TYPE_RULES = {
    "usage of ecb": "C1",
    "static/contant key": "C2",
    "static/contant password": "C2",
    "static/constant password": "C2",
    "static/constant iv": "C3",
    "md2 used": "C5",
    "md4 used": "C5",
    "md5 used": "C5",
    "sha1 used": "C5",
    "pbe iteration < 1000": "C7",
    "static/constant salt": "C8",
    "constant seed": "C9",
    "usage of random method from library": "C9",
    "rsa keysize 1024 bits": "C13",
    "http": "X1",
    "dummy certificate": "X3",
    "dummy verifier": "X3",
    "socket hostname w/o verification": "X3",
    "blowfish used": "X7",
    "des used": "X7",
    "idea used": "X7",
    "rc2 used": "X7",
    "rc4 used": "X7",
    "hmacmd5": "X10",
    "hmacsha1": "X10",
}

JAVA_PREFIX_RULES = {
    "ecbinsymmcrypto": "C1",
    "predictablecryptographickey": "C2",
    "predictablepbepassword": "C2",
    "staticinitializationvector": "C3",
    "brokenhash": "C5",
    "lessthan1000iterationpbe": "C7",
    "staticsalts": "C8",
    "predictableseeds": "C9",
    "untrustedprng": "C9",
    "insecureasymmetriccipher": "C13",
    "httpprotocol": "X1",
    "dummyhostnameverifier": "X3",
    "dummycertvalidation": "X3",
    "impropersocketmanualhost": "X3",
    "predictablekeystorepassword": "X6",
    "brokencrypto": "X7",
    "brokenmac": "X10",
}

# CryptoAPI-Bench names these two files with ABPM in the same fixed-seed case.
# The checkout uses ABMC; resolve the names when reading the truth, before matching any tool.
CRYPTOAPI_JAVA_FILENAME_ALIASES = {
    "PredictableSeedsABPMCase2.java": "PredictableSeedsABMCCase2.java",
    "PredictableSeedsABPM2.java": "PredictableSeedsABMC2.java",
}

# PyCryptoBench rules are defined by that benchmark, not by the CryptoEagle paper.
PYTHON_RULES = {
    1: "X3",   # requests verify=False
    2: "X3",   # disabled CA bundle
    3: "X3",   # unverified SSL context
    4: "X1",   # cleartext HTTP
    # Rule 5 reports every use of Python's general-purpose random module. Crypto-Aware X8 is intentionally narrower:
    # it requires the value to define or flow into a cryptographic asset. Treating the two labels as equivalent would
    # reward the broad same-method heuristic that caused the real-project false positives removed in phase 7.
    6: "C8",   # static/insufficient salt
    7: "C1",   # ECB
    8: "C7",   # insufficient PBKDF2 iterations
    9: "C14",  # unauthenticated AES-CFB/DES-OFB encryption
    10: "C13", # short RSA key
    11: "C5",  # weak hash
    12: "X4",  # JWT verification disabled
    13: "X2",  # deprecated TLS
    14: "X5",  # anonymous LDAP bind
}


# Truth mappings are migrated explicitly; retired categories remain visible as unmapped.
JAVA_TYPE_RULES = {k: LEGACY_TO_CORE[v] for k, v in JAVA_TYPE_RULES.items() if v in LEGACY_TO_CORE}
JAVA_PREFIX_RULES = {k: LEGACY_TO_CORE[v] for k, v in JAVA_PREFIX_RULES.items() if v in LEGACY_TO_CORE}
PYTHON_RULES = {k: LEGACY_TO_CORE[v] for k, v in PYTHON_RULES.items() if v in LEGACY_TO_CORE}


@dataclass(frozen=True)
class Record:
    record_id: str
    files: frozenset[str]
    applicable_rules: frozenset[str]
    expected_rules: frozenset[str]
    ground_truth: str = "NEG"
    source_category: str = ""


@dataclass(frozen=True)
class RuleMetrics:
    rule_id: str
    cases: int
    positives: int
    negatives: int
    tp: int
    fp: int
    fn: int
    tn: int
    precision: float | None
    recall: float | None
    f1: float | None
    true_positive_cases: tuple[str, ...]
    false_positive_cases: tuple[str, ...]
    false_negative_cases: tuple[str, ...]
    true_negative_cases: tuple[str, ...]


def normalized_text(value: object) -> str:
    return re.sub(r"\s+", " ", str(value or "").strip()).lower()


def file_key(value: object) -> str:
    name = Path(str(value or "").replace("\\", "/")).name.strip().lower()
    return name[:-5] if name.endswith(".java") else name[:-3] if name.endswith(".py") else name


def split_excel_files(value: object) -> frozenset[str]:
    return frozenset(file_key(part) for part in re.split(r"[\r\n]+", str(value or "")) if part.strip())


def read_xlsx_rows(path: Path) -> list[list[object]]:
    namespace = "{http://schemas.openxmlformats.org/spreadsheetml/2006/main}"
    with zipfile.ZipFile(path) as archive:
        shared: list[str] = []
        if "xl/sharedStrings.xml" in archive.namelist():
            root = ElementTree.fromstring(archive.read("xl/sharedStrings.xml"))
            for item in root.findall(f"{namespace}si"):
                shared.append("".join(node.text or "" for node in item.iter(f"{namespace}t")))
        sheet_name = sorted(
            name for name in archive.namelist() if re.fullmatch(r"xl/worksheets/sheet\d+\.xml", name)
        )[0]
        root = ElementTree.fromstring(archive.read(sheet_name))
        result: list[list[object]] = []
        for row in root.iter(f"{namespace}row"):
            values: dict[int, object] = {}
            for cell in row.findall(f"{namespace}c"):
                reference = cell.attrib.get("r", "A1")
                letters = re.match(r"[A-Z]+", reference).group(0)
                index = 0
                for letter in letters:
                    index = index * 26 + ord(letter) - ord("A") + 1
                index -= 1
                kind = cell.attrib.get("t")
                value_node = cell.find(f"{namespace}v")
                inline = cell.find(f"{namespace}is")
                raw = value_node.text if value_node is not None else ""
                if kind == "s" and raw:
                    value: object = shared[int(raw)]
                elif kind == "inlineStr" and inline is not None:
                    value = "".join(node.text or "" for node in inline.iter(f"{namespace}t"))
                elif kind == "b":
                    value = raw == "1"
                else:
                    value = raw
                values[index] = value
            if values:
                result.append([values.get(index, "") for index in range(max(values) + 1)])
        return result


def java_records(path: Path) -> tuple[list[Record], dict[str, int]]:
    rows = read_xlsx_rows(path)
    aliases = {file_key(original): file_key(actual) for original, actual in CRYPTOAPI_JAVA_FILENAME_ALIASES.items()}
    records: list[Record] = []
    counts = {"rows": 0, "vulnerable": 0, "safe": 0, "unlabelled": 0, "mappedPositive": 0}
    for row_index, row in enumerate(rows[1:], start=2):
        file_cell = row[1] if len(row) > 1 else ""
        if not str(file_cell).strip():
            continue
        counts["rows"] += 1
        files = frozenset(aliases.get(name, name) for name in split_excel_files(file_cell))
        label_value = row[3] if len(row) > 3 else ""
        label = label_value if isinstance(label_value, bool) else normalized_text(label_value) in {"1", "true"}
        is_unlabelled = label_value in (None, "")
        if is_unlabelled:
            counts["unlabelled"] += 1
        elif label:
            counts["vulnerable"] += 1
        else:
            counts["safe"] += 1
        source_category = str(row[4] if len(row) > 4 else "").strip()
        vulnerability_type = normalized_text(source_category).rstrip()
        expected = JAVA_TYPE_RULES.get(vulnerability_type)
        applicable = {
            rule
            for prefix, rule in JAVA_PREFIX_RULES.items()
            if any(name.startswith(prefix) for name in files)
        }
        if expected:
            applicable.add(expected)
        expected_rules = frozenset(applicable if label and not is_unlabelled else set())
        if expected_rules:
            counts["mappedPositive"] += 1
        ground_truth = "UNLABELLED" if is_unlabelled else "POS" if label else "NEG"
        records.append(
            Record(
                f"xlsx-row-{row_index}",
                files,
                frozenset(applicable),
                expected_rules,
                ground_truth,
                source_category,
            )
        )
    return records, counts


def python_records(path: Path) -> tuple[list[Record], dict[str, int]]:
    records: list[Record] = []
    counts = {"rows": 0, "vulnerable": 0, "safe": 0, "unlabelled": 0, "mappedPositive": 0}
    with path.open(encoding="utf-8") as source:
        for line_number, line in enumerate(source, start=1):
            if not line.strip():
                continue
            item = json.loads(line)
            counts["rows"] += 1
            label = bool(item.get("HasVuln"))
            counts["vulnerable" if label else "safe"] += 1
            source_rule = int(item["Rule"])
            target = PYTHON_RULES.get(source_rule)
            imports = normalized_text(item.get("Imports", ""))
            source_text = ""
            source_path = item.get("source_path")
            if source_path:
                try:
                    source_text = (path.parent / source_path).read_text(encoding="utf-8")
                except (OSError, UnicodeError):
                    source_text = ""
            # Unix crypt has no PBKDF2 iteration parameter; DSA is covered by the current C13.
            if source_rule == 8 and (
                re.search(r"(?:^|:)crypt(?:$|:)", imports) or re.search(r"\bcrypt\.crypt\s*\(", source_text)
            ):
                target = None
            applicable = frozenset({target} if target else set())
            expected = applicable if label else frozenset()
            if expected:
                counts["mappedPositive"] += 1
            records.append(
                Record(
                    f"jsonl-{line_number}",
                    frozenset({file_key(item["FileName"])}),
                    applicable,
                    expected,
                    "POS" if label else "NEG",
                    f"PyCryptoBench Rule {source_rule}",
                )
            )
    return records, counts


def score(findings: Iterable[dict], records: list[Record], scope: str) -> tuple[list[RuleMetrics], dict]:
    supported = sorted({rule for record in records for rule in record.applicable_rules}, key=rule_sort_key)
    if scope == "core":
        supported = [rule for rule in supported if rule.startswith("C")]
    file_records: dict[str, set[str]] = defaultdict(set)
    by_id = {record.record_id: record for record in records}
    for record in records:
        for name in record.files:
            file_records[name].add(record.record_id)

    predicted: dict[str, set[str]] = defaultdict(set)
    ignored = 0
    duplicate_findings = 0
    observed_pairs: set[tuple[str, str]] = set()
    for finding in migrate_findings(findings):
        rule = str(finding.get("ruleId", ""))
        if rule not in supported:
            continue
        matches = {
            record_id
            for record_id in file_records.get(file_key(finding.get("srcFile", "")), set())
            if rule in by_id[record_id].applicable_rules
        }
        if not matches:
            ignored += 1
        for record_id in matches:
            pair = (rule, record_id)
            if pair in observed_pairs:
                duplicate_findings += 1
            observed_pairs.add(pair)
            predicted[rule].add(record_id)

    metrics: list[RuleMetrics] = []
    for rule in supported:
        universe = {record.record_id for record in records if rule in record.applicable_rules}
        expected = {record.record_id for record in records if rule in record.expected_rules}
        actual = predicted[rule]
        tp = len(expected & actual)
        fp = len(actual - expected)
        fn = len(expected - actual)
        tn = len(universe - expected - actual)
        precision = ratio(tp, tp + fp)
        recall = ratio(tp, tp + fn)
        f1 = None if precision is None or recall is None or precision + recall == 0 else 2 * precision * recall / (precision + recall)
        case_name = lambda record_id: f"{record_id}:{','.join(sorted(by_id[record_id].files))}"
        metrics.append(
            RuleMetrics(
                rule,
                len(universe),
                len(expected),
                len(universe - expected),
                tp,
                fp,
                fn,
                tn,
                precision,
                recall,
                f1,
                tuple(case_name(record_id) for record_id in sorted(expected & actual)),
                tuple(case_name(record_id) for record_id in sorted(actual - expected)),
                tuple(case_name(record_id) for record_id in sorted(expected - actual)),
                tuple(case_name(record_id) for record_id in sorted(universe - expected - actual)),
            )
        )
    return metrics, {"ignoredOutOfUniverse": ignored, "duplicateFindingsCollapsed": duplicate_findings}


def ratio(numerator: int, denominator: int) -> float | None:
    return None if denominator == 0 else numerator / denominator


def rule_sort_key(rule: str) -> tuple[str, int]:
    match = re.fullmatch(r"([A-Z]+)(\d+)", rule)
    return (match.group(1), int(match.group(2))) if match else (rule, 0)


def aggregate(metrics: list[RuleMetrics]) -> dict:
    totals = {name: sum(getattr(item, name) for item in metrics) for name in ("tp", "fp", "fn", "tn")}
    precision = ratio(totals["tp"], totals["tp"] + totals["fp"])
    recall = ratio(totals["tp"], totals["tp"] + totals["fn"])
    f1 = None if precision is None or recall is None or precision + recall == 0 else 2 * precision * recall / (precision + recall)
    return {**totals, "precision": precision, "recall": recall, "f1": f1}


def case_inventory(records: list[Record], findings: Iterable[dict]) -> list[dict]:
    reported_by_file: dict[str, set[str]] = defaultdict(set)
    for finding in migrate_findings(findings):
        rule = str(finding.get("ruleId", ""))
        if rule:
            reported_by_file[file_key(finding.get("srcFile", ""))].add(rule)
    return [
        {
            "caseId": record.record_id,
            "files": sorted(record.files),
            "groundTruth": record.ground_truth,
            "sourceCategory": record.source_category,
            "applicableRules": sorted(record.applicable_rules, key=rule_sort_key),
            "expectedRules": sorted(record.expected_rules, key=rule_sort_key),
            "reportedRules": sorted(
                set().union(*(reported_by_file.get(file, set()) for file in record.files)), key=rule_sort_key
            ),
        }
        for record in records
    ]


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("dataset", choices=("java", "python"))
    parser.add_argument("ground_truth", type=Path)
    parser.add_argument("findings", type=Path)
    parser.add_argument("--scope", choices=("core", "all"), default="core")
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    records, source_counts = java_records(args.ground_truth) if args.dataset == "java" else python_records(args.ground_truth)
    findings = json.loads(args.findings.read_text(encoding="utf-8"))
    metrics, diagnostics = score(findings, records, args.scope)
    supported = [item.rule_id for item in metrics]
    declared = list(CORE_IDS)
    result = {
        "schemaVersion": 3,
        "ruleSet": RULE_SET,
        "dataset": args.dataset,
        "scope": args.scope,
        "matchingUnit": "benchmark-case+rule",
        "groundTruth": source_counts,
        "supportedRules": supported,
        "unsupportedPaperRules": [rule for rule in declared if rule not in supported],
        "summary": aggregate(metrics),
        "perRule": [asdict(item) for item in metrics],
        "cases": case_inventory(records, findings),
        "diagnostics": diagnostics,
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    summary = result["summary"]
    print(
        f"dataset={args.dataset} scope={args.scope} rules={','.join(supported)} "
        f"tp={summary['tp']} fp={summary['fp']} fn={summary['fn']} "
        f"precision={summary['precision']} recall={summary['recall']} f1={summary['f1']}"
    )
    print(f"evaluationReport={args.output.resolve()}")


if __name__ == "__main__":
    main()
