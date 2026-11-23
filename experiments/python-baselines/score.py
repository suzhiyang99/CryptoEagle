#!/usr/bin/env python3
"""Scores Bandit, Dlint, Semgrep, Cryptolation and CryptoEagle on PyCryptoBench at file level.

A file is flagged when the tool raises an alert in it; flagged files are compared with the file's HasVuln label by the
Artifact's scripts/eval/evaluate_filelevel.py, the scorer of the CryptoEagle file-level results.
Only the paper's in-scope view is scored, consistently for every tool:

  in-scope   only alerts of a tool rule that targets one of PyCryptoBench's 19 categories (SCOPE below). Rules about
             other subjects (shell commands, try/except/pass, request timeouts, "this library is unmaintained" import
             warnings) are not counted.

Writes tables/summary.csv (paper in-scope view only) and tables/files.csv (each file's label and each tool's flag).
Normalized inputs and scoring diagnostics use a temporary workspace, retained only on failure.
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
META = locate("PYCRYPTOBENCH_META", HERE.parents[1] / "PyCryptoBench" / "metadata.jsonl",
              ARTIFACT / "benchmarks" / "PyCryptoBench" / "metadata.jsonl")
EAGLE = locate("EAGLE_PY_FINDINGS", HERE.parent / "benchmarks" / "raw" / "pycryptobench" / "findings.json",
               ARTIFACT / "results" / "benchmarks" / "raw" / "pycryptobench" / "findings.json")
EVAL = ARTIFACT / "scripts" / "eval" / "evaluate_filelevel.py"
sys.path.insert(0, str(EVAL.parent))
from scoring_workspace import ScoringWorkspace
RAW = HERE / "raw"

# PyCryptoBench categories: 0/5 random, 1-3 certificate verification, 4 cleartext HTTP, 6 salt, 7 ECB,
# 8 PBKDF2 iterations, 9 unauthenticated or weak block cipher, 10 RSA/DSA key size, 11 weak hash, 12 JWT,
# 13 TLS version, 14 LDAP, 15 XML, 16 YAML, 17 pickle, 18 regex.
# Tool rule -> category it targets; None = a different subject, not counted in the in-scope view.
SCOPE = {
    "bandit": {
        "B311": "5", "B501": "1", "B323": "3", "B502": "13", "B503": "13", "B504": "13", "B303": "11", "B324": "11",
        "B304": "9", "B305": "7", "B505": "10", "B506": "16", "B301": "17", "B403": "17",
        **{f"B{n}": "15" for n in range(313, 321)}, **{f"B{n}": "15" for n in range(405, 412)},
        "B310": None, "B113": None, "B413": None, "B401": None, "B402": None, "B605": None, "B110": None,
    },
    "dlint": {
        "DUO102": "5", "DUO123": "1", "DUO122": "1-3", "DUO134": "7/9/11", "DUO107": "15", "DUO109": "16",
        "DUO103": "17", "DUO133": None, "DUO106": None,
    },
    "semgrep": {
        "use-defused-xml": "15", "use-defused-xmlrpc": "15", "avoid-pickle": "17", "unverified-ssl-context": "3",
        "unverified-jwt-decode": "12", "weak-ssl-version": "13", "ssl-wrap-socket-is-deprecated": "13",
        "disabled-cert-validation": "1", "insecure-hash-algorithm-md5": "11", "insecure-hash-algorithm-sha1": "11",
        "insufficient-rsa-key-size": "10", "insufficient-dsa-key-size": "10", "insecure-cipher-algorithm-des": "9",
        "dynamic-urllib-use-detected": None,
    },
    "cryptoeagle": {
        "C1": "7", "C4": "9", "C14": "9", "C5": "11", "C7": "8", "C8": "6", "C9": "5", "C13": "10",
        "C17": "1-3/12", "C18": "4/13", "C2": None, "C3": None, "C6": None, "C10": None, "C11": None, "C12": None,
        "C15": None, "C16": None,
    },
}


def bandit(path: Path):
    for r in json.loads(path.read_text())["results"]:
        yield r["filename"], r["test_id"]


def dlint(path: Path):
    for line in path.read_text().splitlines():
        file, _, code, _ = line.split("|", 3)
        yield file, code


def semgrep(path: Path):
    for r in json.loads(path.read_text())["results"]:
        yield r["path"], r["check_id"].split(".")[-1]


def cryptolation(path: Path):
    with path.open(newline="") as handle:
        for r in csv.DictReader(handle):
            if r["IsVuln"].strip() == "true":
                yield r["File"].strip(), f"rule-{r['Rule_Number'].strip()}"


def cryptoeagle(path: Path):
    for f in json.loads(path.read_text()):
        yield f["srcFile"], f["ruleId"]


# Paper-facing names only; exact versions remain pinned in the third-party tool configurations and run.sh.
# Semgrep uses the paper's p/security-audit + p/python configuration.
# (row label, scope key, parser, raw output)
TOOLS = [
    ("Bandit", "bandit", bandit, RAW / "bandit.json"),
    ("Dlint", "dlint", dlint, RAW / "dlint.txt"),
    ("Semgrep", "semgrep", semgrep, RAW / "semgrep-audit.json"),
    ("Cryptolation", "cryptolation", cryptolation, RAW / "cryptolation.csv"),
    ("CryptoEagle", "cryptoeagle", cryptoeagle, EAGLE),
]


def in_scope(scope: str, rule: str) -> bool:
    if scope == "cryptolation":
        return True  # every Cryptolation rule is numbered after the PyCryptoBench rule it targets
    if rule not in SCOPE[scope]:
        raise KeyError(f"{scope} rule {rule} has no scope decision; add it to SCOPE")
    return SCOPE[scope][rule] is not None


def score(workspace: ScoringWorkspace) -> None:
    (HERE / "tables").mkdir(exist_ok=True)
    names = {json.loads(l)["FileName"] for l in META.read_text().splitlines() if l.strip()}
    summary, flags = [], collections.defaultdict(dict)
    for label, scope, parse, raw in TOOLS:
        alerts = [(Path(file).name, rule) for file, rule in parse(raw)]
        unknown = {f for f, _ in alerts} - names
        assert not unknown, (label, sorted(unknown)[:3])
        kept = [(f, r) for f, r in alerts if in_scope(scope, r)]
        key = f"{scope}-{raw.stem}-in-scope"
        normalized = workspace.path / f"{key}.csv"
        with normalized.open("w", newline="", encoding="utf-8") as handle:
            writer = csv.writer(handle)
            writer.writerow(["file", "rule"])
            writer.writerows(sorted(set(kept)))
        metrics = workspace.path / f"{key}-metrics.json"
        workspace.run_python(EVAL, "--tool", key, "--findings", normalized, "--gt-jsonl", META,
                             "--out-detail", workspace.path / f"{key}-detail.csv", "--out-metrics", metrics,
                             "--dataset", "PyCryptoBench")
        m = json.loads(metrics.read_text())
        c = m.get("confusion", m)
        tp, fp, fn, tn = c["tp"], c["fp"], c["fn"], c["tn"]
        p = tp / (tp + fp) if tp + fp else 0.0
        r = tp / (tp + fn) if tp + fn else 0.0
        summary.append(dict(tool=label,
                            TP=tp, FP=fp, FN=fn, TN=tn, Precision=f"{p:.3f}", Recall=f"{r:.3f}",
                            F1=f"{2 * p * r / (p + r) if p + r else 0:.3f}",
                            Accuracy=f"{(tp + tn) / (tp + fp + fn + tn):.3f}"))
        for f in {f for f, _ in kept}:
            flags[f][label] = 1

    meta = [json.loads(l) for l in META.read_text().splitlines() if l.strip()]
    files = [dict(file=m["FileName"], rule=m["Rule"], label="misuse" if m["HasVuln"] else "no misuse",
                  **{label: flags[m["FileName"]].get(label, 0) for label, *_ in TOOLS}) for m in meta]
    for name, rows in (("summary.csv", summary), ("files.csv", files)):
        with (HERE / "tables" / name).open("w", newline="", encoding="utf-8-sig") as handle:
            writer = csv.DictWriter(handle, fieldnames=list(rows[0]))
            writer.writeheader()
            writer.writerows(rows)
    for row in summary:
        print(row)


def main() -> None:
    with ScoringWorkspace("python-score") as workspace:
        score(workspace)


if __name__ == "__main__":
    main()
