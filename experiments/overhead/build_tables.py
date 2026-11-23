#!/usr/bin/env python3
"""Builds the overhead tables from raw/<input>/run<N>.json (EvaluationMain metrics, one file per run).

  tables/runs.csv     one row per run: standard CPG, enrichment and rule times, overhead, provenance share
  tables/summary.csv  one row per project: medians over the runs, the per-run overheads, and the median over the
                      nine ApacheCryptoAPI-Bench projects
  tables/rq3.tex      the rows of the paper's RQ3 table

Standard CPG = Joern frontend + overlays + data flow. CA-CPG = standard CPG + enrichment pass. Overhead = enrichment /
standard CPG, i.e. (CA-CPG - CPG) / CPG, taken per run and then the median. Rule evaluation is reported separately and
is not part of the overhead. KLOC counts physical source lines (EvaluationMain's source.lines).
"""
from __future__ import annotations

import argparse
import csv
import hashlib
import json
import statistics
from pathlib import Path

HERE = Path(__file__).resolve().parent
RAW, TABLES = HERE / "raw", HERE / "tables"
# Short names used in the paper table, in its order (by size).
APACHE = [("deltaspike-core-impl-1.8.1-SNAPSHOT", "deltaspike-core"),
          ("meecrowave-core-1.2.0-SNAPSHOT", "meecrowave-core"),
          ("artemis-commons-2.5.0-SNAPSHOT", "artemis-commons"),
          ("taverna-perspective-myexperiment-3.1.0-incubating-SNAPSHOT", "taverna-myexp."),
          ("wicket-util-8.0.0-SNAPSHOT", "wicket-util"),
          ("tika-core-1.17-SNAPSHOT", "tika-core"),
          ("mcf-core-2.9-SNAPSHOT", "mcf-core"),
          ("apacheds-kerberos-codec-2.0.0-M25-SNAPSHOT", "apacheds-kerberos"),
          ("openejb-core-7.0.5-SNAPSHOT", "openejb-core")]


def run_row(metrics: dict) -> dict:
    t, e = metrics["timingsMillis"], metrics["enrichment"]
    standard = (t["cpgFrontend"] + t["overlays"] + t["dataflow"]) / 1000
    enrichment = t["enrichmentWall"] / 1000
    return {"standard_cpg_s": standard, "enrichment_s": enrichment, "ca_cpg_s": standard + enrichment,
            "overhead_pct": metrics["enrichmentOverheadPercent"],
            "adapter_share_pct": 100 * e["adapterMillis"] / e["runMillis"],
            "provenance_share_pct": 100 * e["provenanceMillis"] / e["runMillis"],
            "rules_s": t["coreRules"] / 1000, "total_s": t["totalWall"] / 1000,
            "peak_heap_mib": metrics["memory"]["peakHeapBytes"] / 2**20, "findings": metrics["findings"]["core"]}


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check-archive", action="store_true", help="Verify the paper run's raw files against measurement.json")
    args = parser.parse_args()
    if args.check_archive:
        expected = json.loads((HERE / "measurement.json").read_text())["raw_files_sha256"]
        actual = {str(p.relative_to(HERE)): hashlib.sha256(p.read_bytes()).hexdigest()
                  for p in (RAW / "apache").glob("*/*.json") if p.stem.startswith(("run", "findings"))}
        changed = sorted(name for name in expected.keys() | actual.keys() if expected.get(name) != actual.get(name))
        if changed:
            raise SystemExit("Files differ from the archived timing run: " + ", ".join(changed))
    TABLES.mkdir(exist_ok=True)
    inputs = [("apache", f"apache/{name}", short) for name, short in APACHE]
    runs_out, summary = [], []
    for group, key, short in inputs:
        files = sorted((RAW / key).glob("run*.json"), key=lambda p: int(p.stem[3:]))
        if not files:
            continue
        metrics = [json.loads(f.read_text()) for f in files]
        rows = [run_row(m) for m in metrics]
        for f, row in zip(files, rows):
            runs_out.append({"group": group, "input": short, "run": f.stem[3:], **{k: round(v, 3) for k, v in row.items()}})
        if len({r["findings"] for r in rows}) != 1:
            raise SystemExit(f"{short}: finding counts differ between runs: {[r['findings'] for r in rows]}")
        med = {k: statistics.median(r[k] for r in rows) for k in rows[0]}
        source = metrics[0]["source"]
        summary.append({"group": group, "input": short, "runs": len(rows), "files": source["files"],
                        "kloc": round(source["lines"] / 1000, 1),
                        "standard_cpg_s": round(med["standard_cpg_s"], 1),
                        "ca_cpg_s": round(med["standard_cpg_s"] + med["enrichment_s"], 1),
                        "enrichment_s": round(med["enrichment_s"], 2), "overhead_pct": round(med["overhead_pct"], 1),
                        "overhead_runs": " / ".join(f"{r['overhead_pct']:.1f}" for r in rows),
                        "adapter_share_pct": round(med["adapter_share_pct"]),
                        "provenance_share_pct": round(med["provenance_share_pct"]),
                        "rules_s": round(med["rules_s"], 1), "total_s": round(med["total_s"], 1),
                        "peak_heap_mib": round(med["peak_heap_mib"]), "findings": rows[0]["findings"]})
    apache = [s for s in summary if s["group"] == "apache"]
    median = round(statistics.median(s["overhead_pct"] for s in apache), 1) if apache else None
    if apache:
        summary.append({"group": "apache", "input": "Median (Apache)", "runs": "", "files": "", "kloc": "",
                        "standard_cpg_s": "", "ca_cpg_s": "", "enrichment_s": "", "overhead_pct": median,
                        "overhead_runs": "", "adapter_share_pct": "", "provenance_share_pct": "", "rules_s": "",
                        "total_s": "", "peak_heap_mib": "", "findings": ""})
    for name, rows in (("runs.csv", runs_out), ("summary.csv", summary)):
        with (TABLES / name).open("w", newline="", encoding="utf-8") as fh:
            writer = csv.DictWriter(fh, fieldnames=list(rows[0]))
            writer.writeheader()
            writer.writerows(rows)
    tex = [f"{s['input']:<17} & {s['kloc']:5.1f} & {s['standard_cpg_s']:4.1f} & {s['ca_cpg_s']:4.1f} & "
           f"{s['overhead_pct']:.1f}\\% \\\\" for s in apache]
    if apache:
        tex += ["\\midrule", f"\\textbf{{Median}}   & --    & --   & --   & \\textbf{{{median:.1f}\\%}} \\\\"]
    (TABLES / "rq3.tex").write_text("\n".join(tex) + "\n", encoding="utf-8")
    for s in summary:
        print(f"{s['input']:<20} {s['kloc']!s:>6} KLOC  CPG {s['standard_cpg_s']!s:>5}s  CA-CPG {s['ca_cpg_s']!s:>5}s  "
              f"overhead {s['overhead_pct']}%  ({s['overhead_runs']})")


if __name__ == "__main__":
    main()
