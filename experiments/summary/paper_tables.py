#!/usr/bin/env python3
"""Writes the result tables of the paper to tables/, each computed from the experiment outputs.

  tables/table-cryptoapi-bench.csv  Detection results on CryptoAPI-Bench (173 scored cases)
  tables/table-apache.csv           Detection results on ApacheCryptoAPI-Bench (94 scored cases)
  tables/table-cryptogpt.csv        Detection results on CryptoGPT report instances (all metrics use report instances)
  tables/table-per-rule.csv         Per-rule detection results on CryptoAPI-Bench
  tables/table-ablation.csv         Ablation study on CryptoAPI-Bench
  tables/table-rq2.csv              CryptoQL rule applicability across languages
  tables/table-pycryptobench.csv    File-level detection results on PyCryptoBench (1,836 files)
  tables/table-rq3.csv              Runtime of standard CPG vs. CA-CPG construction on ApacheCryptoAPI-Bench projects
  tables/table-rq4.csv              Frozen CryptoLLM on 181 ground-truth cases with different front ends

Rows and columns are those of the paper, in its order and rounding. Inputs: cases/ (build_unified.py, compare_tools.py)
and the sibling experiments benchmarks, ablation, cryptogpt, python-baselines, cryptollm-cases and overhead.
"""
from __future__ import annotations

import argparse
import collections
import csv
import re
import statistics
from pathlib import Path

HERE = Path(__file__).resolve().parent
EXP = HERE.parent
CASES, OUT = HERE / "cases", HERE / "tables"
EAGLE = "CryptoEagle (this work)"
# Short rule names as printed in the paper's per-rule and RQ2 tables.
RULE_NAMES = {"C1": "ECB mode", "C2": "Hardcoded key", "C3": "Static IV", "C4": "No authentication", "C5": "Weak hash",
              "C6": "No KDF for password", "C7": "Low PBKDF2", "C8": "Missing salt", "C9": "Non-CSPRNG",
              "C10": "Hardcoded HMAC key", "C11": "RSA w/o secure padding", "C12": "Raw password as key",
              "C13": "Short RSA key", "C14": "Weak cipher", "C15": "Weak MAC", "C16": "Weak sig. digest",
              "C17": "Verif. disabled", "C18": "Insecure transport"}
RQ2_NAMES = dict(RULE_NAMES, C7="Low PBKDF2 iters")


def read(path: Path) -> list[dict]:
    with path.open(encoding="utf-8-sig") as handle:
        return list(csv.DictReader(handle))


def write(name: str, rows: list[dict]) -> None:
    with (OUT / name).open("w", newline="", encoding="utf-8-sig") as handle:
        writer = csv.DictWriter(handle, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)


def f3(value: float) -> str:
    return f"{value:.3f}"


def prf(c: collections.Counter) -> dict:
    tp, fp, fn = c["TP"], c["FP"], c["FN"]
    return {"P": f3(tp / (tp + fp)), "R": f3(tp / (tp + fn)), "F1": f3(2 * tp / (2 * tp + fp + fn))}


def tool_tables() -> None:
    bench = read(CASES / "tools-cryptoapi-bench.csv")
    tools = [("SpotBugs", "SpotBugs+FindSecBugs"), ("CogniCrypt", "CogniCrypt"), ("CryptoGuard", "CryptoGuard"),
             ("CryptoEagle", EAGLE)]
    counts = {name: collections.Counter(r[col] for r in bench) for name, col in tools}
    write("table-cryptoapi-bench.csv", [{"Tool": name, "TP": c["TP"], "FP": c["FP"], "FN": c["FN"], **prf(c)}
                                        for name, c in counts.items()])


def apache_table() -> None:
    apache = read(CASES / "tools-apache.csv")
    a_counts = {name: collections.Counter(r[col] for r in apache)
                for name, col in (("CogniCrypt", "CogniCrypt"), ("CryptoGuard", "CryptoGuard"), ("CryptoEagle", EAGLE))}
    write("table-apache.csv", [{"Tool": name, "TP": c["TP"], "FP": c["FP"], "FN": c["FN"], **prf(c)}
                               for name, c in a_counts.items()])


def cryptogpt() -> None:
    rows = []
    for lang, name in (("java", "Java"), ("python", "Python")):
        c = collections.Counter(r["verdict"] for r in read(CASES / f"cryptogpt-{lang}.csv"))
        rows.append({"Lang.": name, "TP": c["TP"], "FP": c["FP"], "FN": c["FN"], "TN": c["TN"],
                     "R": prf(c)["R"], "P": prf(c)["P"]})
    write("table-cryptogpt.csv", rows)


def per_rule() -> None:
    rows = [r for r in read(EXP / "benchmarks" / "tables" / "summary.csv") if r["dataset"] == "cryptoapi-bench"]
    rules = [r for r in rows if r["rule"] != "ALL"]
    total = next(r for r in rows if r["rule"] == "ALL")
    out = [{"ID": r["rule"], "Rule": RULE_NAMES[r["rule"]], "TP": r["TP"], "FP": r["FP"], "FN": r["FN"], "F1": r["F1"]}
           for r in rules]
    out.append({"ID": "Total", "Rule": "", "TP": total["TP"], "FP": total["FP"], "FN": total["FN"], "F1": total["F1"]})
    write("table-per-rule.csv", out)


def ablation() -> None:
    rows = read(EXP / "ablation" / "tables" / "summary.csv")
    labels = ["Base (string matching)", "+Ontology", "+Provenance (full)"]
    if any(list(row) != ["Variant", "Precision", "Recall", "F1"] for row in rows) or [row.get("Variant") for row in rows] != labels:
        raise ValueError("Ablation summary must match the variants and columns of Table 7")
    write("table-ablation.csv", rows)


def rq2() -> None:
    rules = [f"C{i}" for i in range(1, 19)]
    # Applicability as stated in the paper: every rule runs on both languages; on Python, C11 covers only PKCS#1 v1.5
    # encryption because the cryptography library has no unpadded RSA.
    write("table-rq2.csv", [{"ID": r, "Rule": RQ2_NAMES[r], "Java": "✓", "Python": "partial" if r == "C11" else "✓"}
                            for r in rules])
    tools = read(EXP / "python-baselines" / "tables" / "summary.csv")
    expected_tools = {"Bandit", "Dlint", "Semgrep", "Cryptolation", "CryptoEagle"}
    if len(tools) != 5 or {r["tool"] for r in tools} != expected_tools or any("view" in r for r in tools):
        raise ValueError("Python baseline summary must contain exactly the five paper tools, without a view column")
    inscope = tools
    pick = lambda name: next(r for r in inscope if r["tool"] == name)
    order = [("CryptoEagle", "CryptoEagle"), ("Cryptolation", "Cryptolation"), ("Dlint", "Dlint"), ("Bandit", "Bandit"),
             ("Semgrep", "Semgrep")]
    write("table-pycryptobench.csv", [
        {"Tool": name, "TP": r["TP"], "FP": r["FP"], "FN": r["FN"], "P": r["Precision"], "R": r["Recall"], "F1": r["F1"]}
        for name, r in ((name, pick(prefix)) for name, prefix in sorted(order, key=lambda o: float(pick(o[1])["F1"])))])
    assert all(int(r["TP"]) + int(r["FP"]) + int(r["FN"]) + int(r["TN"]) == 1836 for r in inscope)
    eagle = pick("CryptoEagle")
    assert float(eagle["Recall"]) == max(float(r["Recall"]) for r in inscope)


def rq3() -> None:
    rows = [r for r in read(EXP / "overhead" / "tables" / "summary.csv") if r["group"] == "apache" and r["runs"]]
    out = [{"Project": r["input"], "KLOC": r["kloc"], "CPG": r["standard_cpg_s"], "CA-CPG": r["ca_cpg_s"],
            "Overhead": f"{float(r['overhead_pct']):.1f}%"} for r in rows]
    median = statistics.median(float(r["overhead_pct"]) for r in rows)
    out.append({"Project": "Median", "KLOC": "", "CPG": "", "CA-CPG": "", "Overhead": f"{median:.1f}%"})
    write("table-rq3.csv", out)


def discussion() -> None:
    comparison = read(EXP / "cryptollm-cases/out/comparison.csv")
    if list(comparison[0]) != ["Front end", "TP", "FP", "FN", "TN", "R", "F1"]:
        raise ValueError("RQ4 comparison columns must match the manuscript")
    write("table-rq4.csv", comparison)


def check_paper(source: Path, apache_only: bool = False) -> None:
    """Compare archived tables with the manuscript evaluation section."""
    text = source.read_text(encoding="utf-8")
    labels = {
        "tab:rq1-bench": "table-cryptoapi-bench.csv",
        "tab:rq1-apache": "table-apache.csv",
        "tab:rq1-real": "table-cryptogpt.csv",
        "tab:per-rule": "table-per-rule.csv",
        "tab:ablation": "table-ablation.csv",
        "tab:rq2": "table-rq2.csv",
        "tab:rq2-py": "table-pycryptobench.csv",
        "tab:rq3": "table-rq3.csv",
        "tab:rq4": "table-rq4.csv",
    }

    for label, name in labels.items():
        if apache_only and label != "tab:rq1-apache":
            continue
        start = text.index("\\label{" + label + "}")
        body = text[start:text.index("\\end{table}", start)].split("\\toprule", 1)[1]
        actual = []
        for line in body.splitlines():
            if "&" not in line:
                continue
            line = re.sub(r"\\(?:textbf|textsc)\{([^{}]*)\}", r"\1", line)
            # The per-rule total spans the ID and label columns in LaTeX.
            line = re.sub(r"\\multicolumn\{2\}\{l\}\{([^{}]*)\}", r"\1 & ", line)
            cells = [cell.strip() for cell in line.strip().removesuffix("\\\\").split("&")]
            actual.append([cell.replace("\\%", "%").replace("\\checkmark", "✓") if cell != "--" else ""
                           for cell in cells])
        with (OUT / name).open(encoding="utf-8-sig", newline="") as handle:
            expected = list(csv.reader(handle))
        if actual != expected:
            first = next((i for i, (tex, archived) in enumerate(zip(actual, expected), 1) if tex != archived), None)
            detail = (f"row {first}: LaTeX {actual[first - 1]} vs CSV {expected[first - 1]}" if first else
                      f"{len(actual)} LaTeX rows vs {len(expected)} CSV rows")
            raise ValueError(f"{source}: {label} differs from {OUT / name} ({detail})")
        print(f"Matches manuscript: {name}")

    if apache_only:
        return
    print(f"All nine archived tables match {source}.")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check-paper", action="store_true", help="Check the nine archived tables against the current manuscript without rebuilding")
    parser.add_argument("--apache-only", action="store_true", help="Limit generation or validation to table-apache.csv")
    parser.add_argument("--paper-source", type=Path, help="Path to the manuscript evaluation.tex")
    args = parser.parse_args()
    if args.check_paper:
        source = args.paper_source or EXP.parent / "提交JSS版/提交JSS版/texes/evaluation.tex"
        if not source.is_file():
            parser.error("Pass --paper-source with the path to evaluation.tex")
        check_paper(source, args.apache_only)
        return
    OUT.mkdir(exist_ok=True)
    if args.apache_only:
        apache_table()
        print("table-apache.csv")
        return
    tool_tables()
    apache_table()
    cryptogpt()
    per_rule()
    ablation()
    rq2()
    rq3()
    discussion()
    for name in sorted(p.name for p in OUT.glob("*.csv")):
        print(name)


if __name__ == "__main__":
    main()
