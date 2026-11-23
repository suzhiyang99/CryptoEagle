#!/usr/bin/env python3
"""Builds case and alert tables for the CryptoGPT real-world projects from the C1-C18 rerun.

Ground truth: every row of CryptoGPT's report-summary.xlsx (Java 37, Python 27, Python-reject 3). The Java/Python sheets
are reported misuses; Python-reject rows were rejected by the developers and act as the only negatives.

  tables/cases.csv     one row per report row: mapped rule, strict verdict, matched alerts and review reason
  tables/alerts.csv    one row per deduplicated alert in the scanned scopes, with a TP/FP review and reason
  tables/summary.csv   paper results: TP, FP, FN, TN, recall and precision from strict report-instance verdicts

A case counts as detected only when an alert matches its rule and a specific API location. Workbook-file anchors
come from the row audit or case notes. Alternative files require explicit API anchors verified against the original issue.
"""
from __future__ import annotations

import collections
import csv
import json
from pathlib import Path

HERE = Path(__file__).resolve().parents[1]
ARTIFACT = HERE.parents[1]
# Report rows, source mappings and current manual reviews; source paths are relative to the Artifact root.
INPUTS = HERE / "inputs"
RUNS = HERE / "runs"
OUT = HERE / "tables"
CASE_NOTES = json.loads((HERE / "audit" / "case_notes.json").read_text(encoding="utf-8"))

# (sheet, workbook row) -> mapped rules, scope, and any label concern retained in the review reason.
# scope: SCORED, UNMAPPED (no C1-C18 rule for the claim), NOT_EVALUATED (reported source version unavailable).
CASES = {
    ("Java", 2): ("C9", "SCORED", "SSLContext.init(..., null) uses the JDK default SecureRandom; no weak random source is involved."),
    ("Java", 3): ("C9", "SCORED", None), ("Java", 4): ("C18", "SCORED", None), ("Java", 5): ("C14", "SCORED", None),
    ("Java", 6): ("C2", "SCORED", None), ("Java", 7): ("C18", "SCORED", None), ("Java", 8): ("C7", "SCORED", None),
    ("Java", 9): ("C7", "SCORED", None), ("Java", 10): ("C14", "SCORED", None), ("Java", 11): ("C2", "SCORED", None),
    ("Java", 12): ("C2", "SCORED", None), ("Java", 13): ("C3", "SCORED", None), ("Java", 14): ("C2", "SCORED", None),
    ("Java", 15): ("C3", "SCORED", "Second report of the same fixed IV, at the decryption call; the encryption-side misuse is row 13."),
    ("Java", 16): ("C14", "SCORED", None), ("Java", 17): ("C16", "SCORED", None), ("Java", 18): ("C1", "SCORED", None),
    ("Java", 19): ("C2|C9", "SCORED", None), ("Java", 20): ("C16", "SCORED", None), ("Java", 21): ("C13", "SCORED", None),
    ("Java", 22): ("", "UNMAPPED", None), ("Java", 23): ("C2", "SCORED", None), ("Java", 24): ("C13", "SCORED", None),
    ("Java", 25): ("C11", "SCORED", None), ("Java", 26): ("C4", "SCORED", None), ("Java", 27): ("C14", "SCORED", None),
    ("Java", 28): ("C14", "SCORED", None), ("Java", 29): ("C12", "SCORED", None), ("Java", 30): ("C4", "SCORED", None),
    ("Java", 31): ("C3", "SCORED", "Each file gets a fresh 32-byte SecureRandom secret and derived AES key, so the zero CTR IV is never reused under one key."),
    ("Java", 32): ("C14", "NOT_EVALUATED", None), ("Java", 33): ("C2", "NOT_EVALUATED", None),
    ("Java", 34): ("C14", "SCORED", None), ("Java", 35): ("C9", "SCORED", None), ("Java", 36): ("C16", "SCORED", None),
    ("Java", 37): ("", "NOT_EVALUATED", None), ("Java", 38): ("C9", "NOT_EVALUATED", None),
    ("Python", 2): ("C7", "SCORED", None),
    ("Python", 3): ("C5", "SCORED", "SHA-1 appears only as the HMAC PRF inside PBKDF2; the weak part of this KDF is the iteration count (row 2)."),
    ("Python", 4): ("C9", "SCORED", None), ("Python", 5): ("C7", "SCORED", None), ("Python", 6): ("C13", "SCORED", None),
    ("Python", 7): ("C14", "SCORED", None), ("Python", 8): ("C5", "SCORED", None),
    ("Python", 9): ("C9", "SCORED", "secure_password() draws from get_random_bytes / random.SystemRandom, both cryptographic sources."),
    ("Python", 10): ("C14|C5", "SCORED", None), ("Python", 11): ("C5|C15", "SCORED", None),
    ("Python", 12): ("C14", "SCORED", None),
    ("Python", 13): ("C1", "SCORED", "AES-ECB is applied only to the single 16-byte PDF /Perms block, as the PDF 2.0 standard requires."),
    ("Python", 14): ("C14", "SCORED", None),
    ("Python", 15): ("C1", "SCORED", "Same PDF /Perms single-block ECB in the other crypto provider (see row 13)."),
    ("Python", 16): ("C13", "SCORED", None), ("Python", 17): ("C6", "SCORED", None), ("Python", 18): ("C8", "SCORED", None),
    ("Python", 19): ("C3", "SCORED", None), ("Python", 20): ("C2", "SCORED", None), ("Python", 21): ("C5", "SCORED", None),
    ("Python", 22): ("C14", "SCORED", None), ("Python", 23): ("C1", "SCORED", None), ("Python", 24): ("C3", "SCORED", None),
    ("Python", 25): ("C8", "SCORED", "The scrypt salt is the random os.urandom(16) IV; reusing a random public value as salt is not a fixed salt."),
    ("Python", 26): ("C6", "SCORED", None), ("Python", 27): ("C15", "SCORED", None), ("Python", 28): ("C16", "SCORED", None),
    ("Python-reject", 2): ("C5", "SCORED", None),
    ("Python-reject", 3): ("C7", "SCORED", "builder.py's aes_encrypt/aes_decrypt protect build credentials (BUILD_KEYS_PATH, build tokens) with PBKDF2 at 1,000 iterations, so the developers' 'non-security script' rejection is disputable."),
    ("Python-reject", 4): ("C16|C5", "SCORED", None),
}
UNMAPPED_REASON = {
    ("Java", 22): "Deriving an AES key from an HMAC value; no C1-C18 rule covers this.",
    ("Java", 37): "Report version unavailable (2022 SmartTube history); a 2026 substitute snapshot cannot confirm the instance.",
}
NOT_EVALUATED_REASON = {
    ("Java", 32): "The report links common/.../DESCipher.java, which is not in the reported snapshot; only odpsreader/odpswriter classes of the same name exist.",
    ("Java", 33): "Same as row 32: the reported common DESCipher (empty default key) is not in the snapshot.",
    ("Java", 37): UNMAPPED_REASON[("Java", 37)],
    ("Java", 38): "Report version unavailable (2022 SmartTube history); a 2026 substitute snapshot cannot confirm the instance.",
}


def load_alerts():
    runs = json.loads((RUNS / "runs.json").read_text())
    reviews = {}
    for r in json.loads((HERE / "audit" / "alert_reviews.json").read_text()):
        key = identity(r["language"], r.get("snapshot"), r.get("repository"), r.get("commit"), r["canonical_source"], r["finding"])
        assert key not in reviews, ("Duplicate alert review", key)
        reviews[key] = r
    alerts = {}
    for run in runs:
        assert run["status"] == "SUCCESS", run["id"]
        if run["language"] == "python" and not run["final"]:
            continue  # the target-only pass is preliminary; each Python target's result is its final (with-dependencies) run
        for f in json.loads((RUNS / run["id"] / "findings.json").read_text()):
            matches = [x for x in run["source_files"] if x == f["srcFile"] or x.endswith("/" + f["srcFile"])]
            assert len(matches) == 1, (run["id"], f["srcFile"])
            rel = matches[0]
            key = identity(run["language"], run.get("snapshot"), run.get("repository"), run.get("commit"), rel, f)
            entry = alerts.setdefault(key, dict(language=run["language"], source=str(Path(run["source_root"]) / rel),
                                                project=run.get("snapshot") or f"{run['repository']}@{run['commit'][:12]}",
                                                file=rel, finding=f, runs=[]))
            entry["runs"].append(run["id"])
    rows = []
    for number, (key, a) in enumerate(sorted(alerts.items(), key=lambda kv: (kv[1]["language"], kv[1]["project"], kv[1]["file"],
                                                                            kv[1]["finding"]["lineNo"], kv[1]["finding"]["ruleId"])), 1):
        f, note = a["finding"], reviews.get(key)
        if note:
            verdict, reason, basis = note["verdict"], note["reason"], "manual review (audit/alert_reviews.json)"
        else:
            verdict, reason, basis = "", "", "NOT REVIEWED"
            print("WARNING: unreviewed alert", a["source"], f["lineNo"], f["ruleId"])
        rows.append(dict(alert_id=f"{'J' if a['language'] == 'java' else 'P'}-{number:03d}", language=a["language"],
                         project=a["project"], file=a["file"], line=f["lineNo"], source=a["source"],
                         code=line_of(a["source"], f["lineNo"]), rule=f["ruleId"], algorithm=f.get("algorithm", ""),
                         mode=f.get("mode", ""), verdict=verdict, reason=reason, review_basis=basis))
    return rows


def identity(lang, snapshot, repository, commit, rel, f):
    version = snapshot if lang == "java" else (repository, commit)
    return (lang, json.dumps(version), rel, f["lineNo"], f["ruleId"], f["operationType"], f["operation"], f["algorithm"], f["mode"])


def line_of(path, line):
    try:
        return (ARTIFACT / path).read_text(encoding="utf-8", errors="replace").splitlines()[line - 1].strip()[:200]
    except (OSError, IndexError):
        return ""


def report_rows():
    workbook = json.loads((INPUTS / "workbook-rows.json").read_text())
    audit = {(x["sheet"], x["row"]): x for x in json.loads((INPUTS / "report-row-audit.json").read_text())}
    manifest = json.loads((RUNS / "scope-manifest.json").read_text())
    for sheet, rows in workbook.items():
        for row in rows[1:]:
            values = row["values"]
            prior = audit.get((sheet, row["row"]), {})
            source = prior.get("source_file")
            if sheet == "Python-reject":
                stem = values[2].split("__")[-1]
                job = next(j for j in manifest if j["language"] == "python" and j["target_path"].endswith("/" + stem)
                           and j["report_target_row"] == str(row["row"]) and "reject" in j["id"].lower())
                source = str(Path(job["source_root"]) / job["target_path"])
            # an anchor fixes the alert's file, line, rule and, when recorded, its algorithm (one call can carry several)
            workbook_apis = [m for m in prior.get("matched_current_apis", []) if m["source"] == source]
            issue_apis = prior.get("issue_location_audit", {}).get("api_anchors", [])
            anchors = {(m["source"], m["line"], m["rule"], m.get("algorithm") or None) for m in workbook_apis + issue_apis}
            status = values[4] or ""
            yield dict(sheet=sheet, row=row["row"], file_name=values[2], issue=values[3], developer_status=status,
                       source=source, anchors=anchors)


def build_cases(alerts):
    by_file = collections.defaultdict(list)
    for a in alerts:
        by_file[a["source"]].append(a)
    rows = []
    for r in report_rows():
        rule, scope, dispute = CASES[(r["sheet"], r["row"])]
        rules = set(filter(None, rule.split("|")))
        note = CASE_NOTES.get(f"{r['sheet']}|{r['row']}", {})
        confirmed = {(r["source"], line) for line in note.get("anchor_lines", [])}
        sources = dict.fromkeys([r["source"], *sorted({anchor[0] for anchor in r["anchors"]})])
        matched = [a for source in sources for a in by_file.get(source, []) if a["rule"] in rules and
                   (any((a["source"], a["line"], a["rule"]) == anchor[:3] and anchor[3] in (None, "UNKNOWN", a["algorithm"])
                        for anchor in r["anchors"]) or (a["source"], a["line"]) in confirmed)]
        negative = r["sheet"] == "Python-reject"
        if scope == "UNMAPPED":
            strict = "UNMAPPED"
        elif scope == "NOT_EVALUATED":
            strict = "NOT EVALUATED"
        else:
            strict = ("FP" if matched else "TN") if negative else ("TP" if matched else "FN")
        reason = (note.get("reason") or UNMAPPED_REASON.get((r["sheet"], r["row"])) or
                  NOT_EVALUATED_REASON.get((r["sheet"], r["row"])) or
                  (f"Detected at the reported instance by {', '.join(a['alert_id'] for a in matched)}." if matched else ""))
        if strict == "FN" and not note.get("reason"):
            print("WARNING: FN without reason", r["sheet"], r["row"])
        if dispute:
            reason = f"[label disputed: {dispute}] {reason}".strip()
        rows.append(dict(case_id=f"{r['sheet']}-row-{r['row']}", file=r["file_name"], category=r["issue"],
                         label="no misuse (developer rejected)" if negative else f"misuse (developer: {r['developer_status'] or 'no status'})",
                         rule=rule.replace("|", " or "), verdict=strict,
                         matched_alerts=" ".join(a["alert_id"] for a in matched),
                         reason=reason))
    return rows


def ratio(n, d):
    return f"{n / d:.3f}" if d else ""


def summary(cases):
    out = []
    for lang, sheets in (("Java", ("Java",)), ("Python", ("Python", "Python-reject"))):
        rows = [c for c in cases if c["case_id"].rsplit("-row-", 1)[0] in sheets]
        c = collections.Counter(x["verdict"] for x in rows)
        out.append({"Lang.": lang, "TP": c["TP"], "FP": c["FP"], "FN": c["FN"], "TN": c["TN"],
                    "R": ratio(c["TP"], c["TP"] + c["FN"]),
                    "P": ratio(c["TP"], c["TP"] + c["FP"])})
    return out


def write_csv(path, rows):
    with path.open("w", newline="", encoding="utf-8-sig") as handle:
        writer = csv.DictWriter(handle, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)


def main():
    OUT.mkdir(exist_ok=True)
    alerts = load_alerts()
    cases = build_cases(alerts)
    write_csv(OUT / "alerts.csv", [{k: v for k, v in a.items() if k != "source"} for a in alerts])
    write_csv(OUT / "cases.csv", cases)
    rows = summary(cases)
    write_csv(OUT / "summary.csv", rows)
    for r in rows:
        print(r)
    print(collections.Counter((a["language"], a["review_basis"], a["verdict"]) for a in alerts))


if __name__ == "__main__":
    main()
