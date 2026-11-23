#!/usr/bin/env python3
"""Validate saved RQ4 evidence and publication tables without rerunning inference."""
import ast
import csv
import hashlib
import json
import math
from collections import Counter
from datetime import datetime, timezone
from pathlib import Path

HERE = Path(__file__).resolve().parents[1]
ROOT = HERE.parents[1]


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def read_json(path):
    return json.loads(path.read_text())


def read_csv(path):
    with path.open(encoding="utf-8-sig", newline="") as handle:
        return list(csv.DictReader(handle))


def main():
    gt = read_json(HERE / "inputs/cases.json")
    cases = {c["case_id"]: c for c in gt["cases"]}
    assert len(cases) == 181
    assert Counter(c["label"] for c in cases.values()) == {True: 144, False: 37}
    verified = 0
    for case in cases.values():
        for source in case["files"]:
            assert digest(HERE / source["path"]) == source["sha256"]
            assert digest(ROOT / source["benchmark_path"]) == source["sha256"]
            verified += 1
    predictions = read_json(HERE / "out/predictions.json")
    saved = read_csv(HERE / "out/case-results.csv")
    assert list(saved[0]) == ["front_end", "code_number", "files", "missing_files", "label", "category",
                              "source_status", "slices", "prediction", "verdict"]
    case_ids_by_number = {c["code_number"]: cid for cid, c in cases.items()}
    assert len(case_ids_by_number) == len(cases), "code_number must uniquely identify a case"
    indexed = {(r["front_end"], case_ids_by_number[r["code_number"]]): r for r in saved}
    assert len(indexed) == len(saved) == 362
    metrics = read_json(HERE / "out/metrics.json")["metrics"]
    recount, cross_file, fp_cases = {}, 0, set()
    for arm, label in (("baseline", "CryptoLLM original front end"), ("cacpg", "CA-CPG front end")):
        votes = predictions[arm]["predictions"]
        slices = read_json(HERE / f"out/{arm}/slices.json")["results"]
        assert set(votes) == set(slices) == set(cases)
        counts, coverage, total_slices = Counter(), 0, 0
        for cid, case in cases.items():
            pred, record = votes[cid], slices[cid]
            assert not record["errors"]
            assert "slice_failed" not in record["file_status"].values()
            assert pred["aggregation"] == "any"
            assert pred["n_slices"] == len(pred["slice_preds"]) == len(record["slices"])
            positive = any(v == 1 for v in pred["slice_preds"])
            assert pred["predicted_misuse"] == positive
            verdict = ("T" if positive == case["label"] else "F") + ("P" if positive else "N")
            row = indexed[label, cid]
            assert row["verdict"] == verdict and int(row["prediction"]) == int(positive)
            assert int(row["slices"]) == pred["n_slices"]
            counts[verdict] += 1
            coverage += bool(case["label"] and pred["n_slices"])
            total_slices += pred["n_slices"]
            if arm == "cacpg" and verdict == "FP":
                fp_cases.add(cid)
            allowed = {s["name"] for s in case["files"]}
            for snippet in record["slices"]:
                refs = snippet.get("source_lines", {})
                assert set(refs) <= allowed, (cid, refs)
                cross_file += len(refs) > 1
        m = next(m for m in metrics if m["front_end"] == label)
        tp, fp, fn, tn = (counts[k] for k in ("TP", "FP", "FN", "TN"))
        assert all(m[k] == counts[k] for k in ("TP", "FP", "FN", "TN"))
        assert m["positive_coverage"] == coverage and m["slices"] == total_slices
        pr = tp / (tp + fp) if tp + fp else 0
        rc, f1 = tp / (tp + fn), 2 * tp / (2 * tp + fp + fn)
        assert all(math.isclose(m[k], v) for k, v in (("Precision", pr), ("Recall", rc), ("F1", f1)))
        recount[label] = {"counts": {k: counts[k] for k in ("TP", "FP", "FN", "TN")}, "P": pr, "R": rc, "F1": f1}
    notes = read_json(HERE / "out/audit-reasons.json")
    assert all(notes.get(cid) for cid in fp_cases)
    model = read_json(HERE / "out/model.json")
    assert model["retrained"] is False
    checkpoint = ROOT / "external-tools" / "cryptollm" / Path(model["model"]).name
    model_hash_matches = None
    if checkpoint.exists():
        model_hash_matches = digest(checkpoint) == model["sha256"]
        if not model_hash_matches:
            raise ValueError(f"Checkpoint hash differs from the archived run: {checkpoint}")
    comparison = read_csv(HERE / "out/comparison.csv")
    assert list(comparison[0]) == ["Front end", "TP", "FP", "FN", "TN", "R", "F1"]
    front_ends = {"Original": "CryptoLLM original front end", "CA-CPG": "CA-CPG front end"}
    assert [row["Front end"] for row in comparison] == list(front_ends)
    for row in comparison:
        m = recount[front_ends[row["Front end"]]]
        assert all(int(row[k]) == m["counts"][k] for k in ("TP", "FP", "FN", "TN"))
        assert row["R"] == f"{m['R']:.3f}" and row["F1"] == f"{m['F1']:.3f}"
    assert comparison == read_csv(HERE.parent / "summary/tables/table-rq4.csv")
    unified = read_csv(HERE.parent / "summary/cases/cryptollm-cacpg.csv")
    assert len(unified) == 181 and {r["case_id"] for r in unified} == set(cases)
    assert list(unified[0]) == ["case_id", "file", "ground_truth", "tool_reported", "verdict", "reason"]
    assert all(r["verdict"] == indexed["CA-CPG front end", r["case_id"]]["verdict"] for r in unified)
    scripts = sorted((HERE / "scripts").glob("*.py"))
    for script in scripts:
        ast.parse(script.read_text())
    report = {
        "status": "passed", "checked_at_utc": datetime.now(timezone.utc).isoformat(),
        "scope": "Saved sources, slices, predictions, metrics and tables; no model inference or PDF compilation rerun.",
        "cases": len(cases), "positive": 144, "negative": 37,
        "multi_file_cases": sum(len(c["files"]) > 1 for c in cases.values()),
        "source_files_verified": verified,
        "missing_source_cases": sum(bool(c["missing_files"]) for c in cases.values()),
        "cross_file_slices": cross_file, "aggregation_both_arms": "any",
        "model_hash_matches_recorded_run": model_hash_matches, "metrics_independent_recount": recount,
        "publication_table_columns": list(comparison[0]), "publication_tables_match": True,
        "code_sha256": {str(s.relative_to(ROOT)): digest(s) for s in scripts},
        "data_sha256": {str(p.relative_to(ROOT)): digest(p) for p in [
            HERE / "inputs/cases.json", HERE / "out/case-results.csv", HERE / "out/operations.json", HERE / "out/predictions.json",
            HERE / "out/baseline/slices.json", HERE / "out/cacpg/slices.json"]},
    }
    paper = ROOT / "提交JSS版/output/pdf/CryptoEagle-JSS.pdf"
    if paper.exists():
        report["paper"] = {"path": str(paper.relative_to(ROOT)), "sha256": digest(paper),
                           "check": "Current file fingerprint only; compilation and layout are not checked by this script."}
    (HERE / "out/validation.json").write_text(json.dumps(report, indent=2) + "\n")
    print(json.dumps({"status": report["status"], "cases": len(cases), "metrics": recount}, indent=2))


if __name__ == "__main__":
    main()
