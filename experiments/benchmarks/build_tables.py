#!/usr/bin/env python3
"""Builds the reviewer-facing alert and case tables for the benchmark evaluation (rule set C1-C18).

For each dataset it writes:
  tables/<dataset>-alerts.csv   one row per alert the tool emitted, with the benchmark verdict and the reason
  tables/<dataset>-cases.csv    one row per (benchmark case, mapped rule), including the misses (FN)
For CryptoAPI-Bench it also writes tables/summary.csv with per-rule and overall scores.

Manual audit notes (written after reading the source) live in audit_notes.json and are merged in by key.

Matching unit: (benchmark case, CryptoQL rule). Alerts match by file and mapped rule. For Apache file/rule groups
with both misuse and non-misuse labels, resolve the workbook's code and method to a site in the decompiled source
and match the alert to that site.
"""
from __future__ import annotations

import argparse
import collections
import csv
import json
import os
import re
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent


def locate(env: str, marker: str, *candidates: Path) -> Path:
    """Use an environment override or the first directory containing the expected input file."""
    if os.environ.get(env):
        return Path(os.environ[env]).resolve()
    return next((c for c in candidates if (c / marker).exists()), candidates[0])


ARTIFACT = HERE.parent.parent
EXT = locate("EXT_DIR", "build.sbt", HERE.parents[1] / "crypto-ext", ARTIFACT / "tool" / "crypto-ext")
CRYPTOAPI = locate("CRYPTOAPI_BENCH", "CryptoAPI-Bench_details.xlsx", HERE.parents[1] / "CryptoAPI-Bench-master", ARTIFACT / "benchmarks" / "CryptoAPI-Bench")
PYCRYPTO = locate("PYCRYPTOBENCH", "metadata.jsonl", HERE.parents[1] / "PyCryptoBench", ARTIFACT / "benchmarks" / "PyCryptoBench")
APACHE = locate("APACHE_BENCH", "ApacheCryptoAPI-Bench_detail_list.xlsx", ARTIFACT / "benchmarks" / "ApacheCryptoAPI-Bench", HERE.parents[1] / "ApacheCryptoAPI-Bench-main")
sys.path.insert(0, str(EXT / "scripts"))
import evaluate_benchmark as eb  # noqa: E402
from rule_schema import LEGACY_TO_CORE  # noqa: E402

RAW = HERE / "raw"
OUT = HERE / "tables"
USED: set[str] = set()
NOTES = json.loads((HERE / "audit_notes.json").read_text(encoding="utf-8")) if (HERE / "audit_notes.json").exists() else {}

RULE_NAMES = {
    "C1": "ECB mode", "C2": "Hardcoded or predictable key", "C3": "Static or predictable IV/nonce",
    "C4": "Encryption without authentication", "C5": "Weak hash", "C6": "Password hashed without KDF",
    "C7": "Insufficient PBKDF2/PBE iterations", "C8": "Missing or insecure salt", "C9": "Insecure randomness for crypto",
    "C10": "Hardcoded HMAC key", "C11": "RSA without secure padding", "C12": "Raw password as key",
    "C13": "Insufficient asymmetric parameter strength", "C14": "Weak symmetric cipher", "C15": "Weak MAC algorithm",
    "C16": "Weak signature digest", "C17": "Verification disabled", "C18": "Insecure transport",
}

# ApacheCryptoAPI-Bench TypeID -> C1-C16 rules (historical X-rule mappings migrated with rule_schema.py).
APACHE_TYPE_RULES_LEGACY = {
    "random": {"C9", "X8"}, "messagedigest": {"C5"}, "mac": {"X10"}, "cipheralgo": {"X7"}, "modeofoperation": {"C1"},
    "static iv": {"C3"}, "constantkey": {"C2", "C10"}, "http": {"X1"}, "pbesalt": {"C8"}, "pbeiteration": {"C7"},
    "hostnameverifier": {"X3"}, "trustmanager": {"X3"},
}
APACHE_TYPE_RULES = {k: {LEGACY_TO_CORE[r] for r in v if r in LEGACY_TO_CORE} for k, v in APACHE_TYPE_RULES_LEGACY.items()}
APACHE_PROJECTS = {
    "directoryserver": "apacheds-kerberos-codec-2.0.0-M25-SNAPSHOT", "spark": "spark-core_2.11-2.4.0-SNAPSHOT",
    "tomee": "openejb-core-7.0.5-SNAPSHOT", "activemq": "artemis-commons-2.5.0-SNAPSHOT",
    "artimis": "artemis-commons-2.5.0-SNAPSHOT", "traverna": "taverna-perspective-myexperiment-3.1.0-incubating-SNAPSHOT",
    "deltaspike": "deltaspike-core-impl-1.8.1-SNAPSHOT", "wicket": "wicket-util-8.0.0-SNAPSHOT",
    "manifold": "mcf-core-2.9-SNAPSHOT", "manifoldcf": "mcf-core-2.9-SNAPSHOT",
    "meecrowave": "meecrowave-core-1.2.0-SNAPSHOT", "tika": "tika-core-1.17-SNAPSHOT",
}
APACHE_CLASS_FIX = {
    "org.apache.manifoldcf.core.connectorpool/ConnectorPoolConnectorPool.java": "org.apache.manifoldcf.core.connectorpool.ConnectorPool",
    "org.apache.manifoldcf.core.database/Database.java": "org.apache.manifoldcf.core.database.Database",
    "org.apache.activemq.artemis.utils/DefaultSensitiveStringCodec": "org.apache.activemq.artemis.utils.DefaultSensitiveStringCodec",
    "org.spark_project.jetty.util.ssl.SslContextFactory: javax.net.ssl.SSLSocket": "org.spark_project.jetty.util.ssl.SslContextFactory",
}
APACHE_NOT_RUN = {"spark-core_2.11-2.4.0-SNAPSHOT": "not run: Fernflower output of Scala bytecode; 506 of 2006 files are not valid Java"}


class Case:
    def __init__(self, case_id, files, category, label, rules, gt_line="", gt_code="", gt_note="", project="", status="",
                 gt_method=""):
        self.case_id, self.files, self.category, self.label = case_id, files, category, label
        self.rules, self.gt_line, self.gt_code, self.gt_note, self.project, self.status = rules, gt_line, gt_code, gt_note, project, status
        self.gt_method = gt_method


JAVA_TOKEN = re.compile(r"""//[^\n]*|/\*[\s\S]*?\*/|"(?:\\.|[^"\\])*"|'(?:\\.|[^'\\])*'|[\w$]+|[^\s]""")
JAVA_METHOD = re.compile(r"^\s*(?:[\w.$<>\[\],]+\s+)+([\w$]+)\s*\([^;{}]*\)\s*(?:throws\s+[^;{}]+)?\{")


def java_source_rows(path: Path) -> list[tuple[int, str, tuple[str, ...]]]:
    source = path.read_text(encoding="utf-8", errors="replace")
    # Preserve source line numbers while dropping comments. Quoted strings are single tokens.
    source = JAVA_TOKEN.sub(lambda m: " " + "\n" * m[0].count("\n") if m[0].startswith(("//", "/*")) else m[0], source)
    rows, methods, depth = [], [], 0
    for line_no, line in enumerate(source.splitlines(), 1):
        if header := JAVA_METHOD.match(line):
            methods.append((depth, header[1]))
        tokens = tuple(JAVA_TOKEN.findall(line))
        rows.append((line_no, methods[-1][1] if methods else "", tokens))
        depth += tokens.count("{") - tokens.count("}")
        while methods and depth <= methods[-1][0]:
            methods.pop()
    return rows


def apache_site(case: Case, rows: list[tuple[int, str, tuple[str, ...]]]) -> int | None:
    snippet = tuple(JAVA_TOKEN.findall(case.gt_code.rstrip("; ")))
    if not snippet:
        return None
    variants = [snippet]
    # The decompiler makes the boxing before byteValue() explicit.
    if snippet[-4:] == (".", "byteValue", "(", ")") and len(snippet) > 4:
        variants.append(("Integer", ".", "valueOf", "(") + snippet[:-4] + (")",) + snippet[-4:])
    candidates = [(line, method) for line, method, tokens in rows if any(
        tokens[i:i + len(code)] == code for code in variants for i in range(len(tokens) - len(code) + 1))]
    if len(candidates) == 1:
        return candidates[0][0]
    in_method = [line for line, method in candidates if method and method == case.gt_method]
    return in_method[0] if len(in_method) == 1 else None


def apache_mixed_sites(by_file: dict[str, list[Case]]) -> dict[tuple[str, str], int | None]:
    sites = {}
    for file, cases in by_file.items():
        by_rule = collections.defaultdict(list)
        for case in cases:
            if not case.status:
                for rule in case.rules:
                    by_rule[rule].append(case)
        mixed = {rule: group for rule, group in by_rule.items() if len({case.label for case in group}) > 1}
        if mixed:
            rows = java_source_rows(APACHE / "decompiled" / file)
            for rule, group in mixed.items():
                resolved = [(case, apache_site(case, rows)) for case in group]
                labels_by_line = collections.defaultdict(set)
                for case, line in resolved:
                    labels_by_line[line].add(case.label)
                for case, line in resolved:
                    sites[case.case_id, rule] = line if len(labels_by_line[line]) == 1 else None
    return sites


def source_line(base: Path, rel: str, line: int) -> str:
    path = base / rel
    try:
        lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
        return lines[line - 1].strip() if 0 < line <= len(lines) else ""
    except OSError:
        return ""


def evidence_text(finding: dict) -> str:
    parts = []
    for item in finding.get("evidence", []) or []:
        code = re.sub(r"\s+", " ", str(item.get("code", ""))).strip()
        where = f"L{item['lineNo']}" if item.get("lineNo") else ""
        parts.append(" ".join(p for p in (item.get("kind", ""), where, code[:160]) if p))
    return " | ".join(parts)


def alert_category(cases: list[Case]) -> str:
    return "; ".join(sorted({c.category for c in cases if c.label == "misuse"}))


def describe(finding: dict) -> str:
    detail = ", ".join(p for p in (finding.get("algorithm", ""), finding.get("mode", "")) if p and p != "UNKNOWN")
    return f"{finding.get('operationType', '').lower().replace('_', ' ')}" + (f" ({detail})" if detail else "") + \
        f": {finding.get('message', '').rstrip('.')}"


def case_reason(verdict: str, case: Case, rule: str, matched: list[str], site: int | None = None) -> str:
    location = f"at decompiled source line {site}" if site is not None else "in this file"
    if verdict == "TP":
        return f"Detected: {', '.join(matched)} report {rule.replace('|', ' or ')} {location}."
    if verdict == "TN":
        return f"No {rule.replace('|', ' or ')} alert {location}, as expected."
    if verdict == "UNMAPPED":
        return (f"Category '{case.category}' has no counterpart among CryptoQL C1-C18 (anonymous LDAP, credentials kept in strings, "
                "general-purpose randomness, or non-crypto categories); not scored.")
    if verdict == "UNLABELLED":
        return "The ground-truth label is empty; not scored."
    return ""


def java_cases() -> list[Case]:
    rows = eb.read_xlsx_rows(CRYPTOAPI / "CryptoAPI-Bench_details.xlsx")
    records, _ = eb.java_records(CRYPTOAPI / "CryptoAPI-Bench_details.xlsx")
    cases = []
    for record in records:
        row = rows[int(record.record_id.split("-")[-1]) - 1]
        label = {"POS": "misuse", "NEG": "no misuse", "UNLABELLED": "unlabelled"}[record.ground_truth]
        cases.append(Case(record.record_id, sorted(record.files), record.source_category or "---", label,
                          set(record.applicable_rules), str(row[6]) if len(row) > 6 else "", "",
                          str(row[7]) if len(row) > 7 else ""))
    return cases


def python_cases() -> list[Case]:
    records, _ = eb.python_records(PYCRYPTO / "metadata.jsonl")
    return [Case(r.record_id, sorted(r.files), r.source_category, "misuse" if r.ground_truth == "POS" else "no misuse",
                 set(r.applicable_rules)) for r in records]


def apache_cases() -> list[Case]:
    import openpyxl
    sheet = openpyxl.load_workbook(APACHE / "ApacheCryptoAPI-Bench_detail_list.xlsx")["all cases"]
    cases = []
    for index, row in enumerate(sheet.iter_rows(values_only=True), 1):
        if index == 1 or not row[6]:
            continue
        project = APACHE_PROJECTS[str(row[0]).strip().lower()]
        cls = APACHE_CLASS_FIX.get(str(row[6]).strip(), str(row[6]).strip()).split("$")[0]
        rel = cls.replace(".", "/") + ".java"
        status = APACHE_NOT_RUN.get(project, "")
        if not status and not (APACHE / "decompiled" / project / rel).exists():
            status = "class missing from decompiled sources"
        label = "no misuse" if "Non" in str(row[3]) else "misuse"
        cases.append(Case(f"xlsx-row-{index}", [f"{project}/{rel}"], str(row[1]).strip(), label,
                          set(APACHE_TYPE_RULES[str(row[1]).strip().lower()]), str(row[8] or ""),
                          re.sub(r"\s+", " ", str(row[2] or "")), re.sub(r"\s+", " ", str(row[4] or "")).strip(),
                          project, status, gt_method=str(row[7] or "").split("(", 1)[0].strip()))
    return cases


def load_findings(dataset: str) -> list[tuple[str, Path, dict]]:
    """Returns (file key used for matching, source root, finding)."""
    result = []
    if dataset == "apache":
        for project in sorted(set(APACHE_PROJECTS.values()) - APACHE_NOT_RUN.keys()):
            base = APACHE / "decompiled" / project
            report = RAW / "apache" / project / "findings.json"
            for finding in json.loads(report.read_text(encoding="utf-8")):
                result.append((f"{project}/{finding['srcFile']}", base, finding))
    else:
        base = CRYPTOAPI if dataset == "cryptoapi-bench" else PYCRYPTO
        for finding in json.loads((RAW / dataset / "findings.json").read_text(encoding="utf-8")):
            result.append((eb.file_key(finding["srcFile"]), base, finding))
    return result


def case_file_key(dataset: str, name: str) -> str:
    return name if dataset == "apache" else eb.file_key(name)


def build(dataset: str, prefix: str, cases: list[Case]):
    findings = load_findings(dataset)
    findings.sort(key=lambda x: (x[0], x[2].get("lineNo", 0), x[2]["ruleId"]))
    by_file = collections.defaultdict(list)
    for case in cases:
        for name in case.files:
            by_file[case_file_key(dataset, name)].append(case)
    sites = apache_mixed_sites(by_file) if dataset == "apache" else {}

    hits = collections.defaultdict(list)  # (case_id, rule) -> alert ids
    alert_rows = []
    for number, (key, base, f) in enumerate(findings, 1):
        alert_id = f"{prefix}-A{number:03d}"
        rule = f["ruleId"]
        in_file = by_file.get(key, [])
        eligible = [c for c in in_file if rule in c.rules and not c.status]
        line = int(f.get("lineNo") or 0)
        scored = [c for c in eligible if sites.get((c.case_id, rule), line) == line]
        if not in_file:
            verdict, basis = "NOT IN BENCHMARK", "file has no ground-truth case"
        elif eligible and not scored:
            verdict, basis = "UNMATCHED SITE", "no labelled source site at the alert line for this rule"
        elif not scored:
            labels = "; ".join(sorted({f"{c.category} ({c.label})" for c in in_file}))
            verdict, basis = "UNLABELLED RULE", f"file's cases are labelled only for: {labels}"
        elif any(c.label == "misuse" for c in scored):
            verdict, basis = "TP", "case labelled misuse for this rule"
        else:
            verdict, basis = "FP", "case labelled no misuse for this rule"
        for c in scored:
            hits[(c.case_id, rule)].append(alert_id)
        note_key = f"alert|{dataset}|{key}|{f.get('lineNo')}|{rule}"
        note = NOTES.get(note_key, {})
        USED.add(note_key)
        if verdict != "TP" and not note:
            print("WARNING: no audit note for", alert_id, note_key)
        gt = scored if eligible else in_file
        alert_rows.append({
            "alert_id": alert_id, "file": f["srcFile"] if dataset != "apache" else key, "line": f.get("lineNo", ""),
            "source_code_at_line": source_line(base, f["srcFile"], line),
            "rule": rule, "rule_name": RULE_NAMES.get(rule, f.get("title", "")),
            "tool_message": f.get("message", ""), "operation": f"{f.get('operationType', '')}/{f.get('operation', '')}",
            "algorithm": f.get("algorithm", ""), "mode": f.get("mode", ""), "tool_evidence": evidence_text(f),
            "benchmark_cases": " ".join(c.case_id for c in gt),
            "benchmark_category": "; ".join(sorted({c.category for c in gt})),
            "benchmark_label": "; ".join(sorted({c.label for c in gt})),
            "benchmark_line": "; ".join(sorted({c.gt_line for c in gt if c.gt_line})),
            "benchmark_verdict": verdict, "verdict_basis": basis,
            "audit_verdict": note.get("verdict", "Agrees with label" if verdict == "TP" else ""),
            "audit_reason": note.get("reason", "") or (
                f"Benchmark labels this case '{alert_category(gt)}' as a misuse; the tool reports {rule} "
                f"({RULE_NAMES[rule]}) for {describe(f)}." if verdict == "TP" else ""),
        })

    case_rows = []
    for case in cases:
        ordered = sorted(case.rules, key=eb.rule_sort_key)
        # ApacheCryptoAPI-Bench labels one case per TypeID, so its rules form one group (Constantkey = C2 or C10).
        groups = ["|".join(ordered)] if dataset == "apache" and ordered else ordered or [""]
        for rule in groups:
            entries = [(case.case_id, r) for r in rule.split("|") if r]
            matched = [a for entry in entries for a in hits.get(entry, [])]
            site = next((sites[entry] for entry in entries if entry in sites), None)
            unresolved = not matched and any(entry in sites and sites[entry] is None for entry in entries)
            if case.status:
                verdict = "NOT EVALUATED"
            elif unresolved:
                verdict = "NOT EVALUATED"
            elif not rule:
                verdict = "UNMAPPED" if case.label != "unlabelled" else "UNLABELLED"
            elif case.label == "unlabelled":
                verdict = "UNLABELLED"
            else:
                verdict = ("TP" if matched else "FN") if case.label == "misuse" else ("FP" if matched else "TN")
            note_key = f"case|{dataset}|{case.case_id}|{rule}"
            note = NOTES.get(note_key, {})
            USED.add(note_key)
            reason = note.get("reason", "") if dataset != "apache" or verdict in ("FP", "FN") else ""
            if verdict in ("FN", "FP") and not note:
                print("WARNING: no audit note for case", note_key)
            row = {"case_id": case.case_id, "file": " ".join(case.files)}
            if dataset == "apache":
                # several Apache cases share one class; the ground-truth code line tells them apart
                row["benchmark_code"] = case.gt_code
            row.update({
                "category": case.category, "label": case.label, "rule": rule.replace("|", " or "),
                "verdict": verdict, "matched_alerts": " ".join(matched),
                "reason": reason or case.status or ("ambiguous benchmark source location" if unresolved else
                                                     case_reason(verdict, case, rule, matched, site)),
                "fn_category": note.get("fn_category", "") if verdict == "FN" else "",
            })
            case_rows.append(row)
    return alert_rows, case_rows


def metrics(rows: list[dict]) -> dict:
    c = collections.Counter(r["verdict"] for r in rows)
    tp, fp, fn, tn = c["TP"], c["FP"], c["FN"], c["TN"]
    p = tp / (tp + fp) if tp + fp else None
    r = tp / (tp + fn) if tp + fn else None
    f1 = 2 * tp / (2 * tp + fp + fn) if tp + fp + fn else None
    fmt = lambda v: "" if v is None else f"{v:.3f}"
    return {"TP": tp, "FP": fp, "FN": fn, "TN": tn, "Precision": fmt(p), "Recall": fmt(r), "F1": fmt(f1)}


def write_csv(path: Path, rows: list[dict]):
    with path.open("w", newline="", encoding="utf-8-sig") as handle:
        writer = csv.DictWriter(handle, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apache-only", action="store_true", help="Rebuild only the Apache benchmark tables")
    args = parser.parse_args()
    OUT.mkdir(exist_ok=True)
    datasets = [("apache", "A", apache_cases)]
    if not args.apache_only:
        datasets[:0] = [("cryptoapi-bench", "J", java_cases), ("pycryptobench", "P", python_cases)]
    for dataset, prefix, loader in datasets:
        alerts, cases = build(dataset, prefix, loader())
        write_csv(OUT / f"{dataset}-alerts.csv", alerts)
        write_csv(OUT / f"{dataset}-cases.csv", cases)
        scored = [r for r in cases if r["verdict"] in ("TP", "FP", "FN", "TN")]
        if dataset == "cryptoapi-bench":
            summary = []
            for rule in sorted({row["rule"] for row in scored}, key=eb.rule_sort_key):
                summary.append({"dataset": dataset, "rule": rule, "rule_name": RULE_NAMES[rule],
                                **metrics([row for row in scored if row["rule"] == rule])})
            summary.append({"dataset": dataset, "rule": "ALL", "rule_name": "all mapped rules", **metrics(scored)})
            write_csv(OUT / "summary.csv", summary)
        verdicts = collections.Counter(a["benchmark_verdict"] for a in alerts)
        print(dataset, "alerts", len(alerts), dict(verdicts), "cases", dict(collections.Counter(r["verdict"] for r in cases)),
              metrics(scored))
    for key in sorted(set(NOTES) - USED):
        if not args.apache_only or key.split("|")[1] == "apache":
            print("WARNING: unused audit note", key)


if __name__ == "__main__":
    main()
