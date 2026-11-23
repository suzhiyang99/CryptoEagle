#!/usr/bin/env python3
"""Recompute every case verdict and metric without a model or hard-coded coverage denominator."""
import csv
import json
from collections import Counter
from pathlib import Path

HERE = Path(__file__).resolve().parents[1]


def main():
    gt = json.loads((HERE / "inputs/cases.json").read_text())
    predictions = json.loads((HERE / "out/predictions.json").read_text())
    names = {"baseline": "CryptoLLM original front end", "cacpg": "CA-CPG front end"}
    table, metrics, checks = [], [], {}
    for arm in names:
        assert set(predictions[arm]["predictions"]) == {c["case_id"] for c in gt["cases"]}
        slices = json.loads((HERE / "out" / arm / "slices.json").read_text())["results"]
        counts = Counter()
        pos_covered = neg_covered = total_slices = 0
        lengths = []
        for case in gt["cases"]:
            cid = case["case_id"]
            p = predictions[arm]["predictions"][cid]
            assert p["aggregation"] == "any"
            assert p["predicted_misuse"] == any(v == 1 for v in p["slice_preds"])
            assert len(slices[cid]["slices"]) == p["n_slices"]
            positive = p["predicted_misuse"]
            verdict = ("T" if positive == case["label"] else "F") + ("P" if positive else "N")
            counts[verdict] += 1
            pos_covered += bool(case["label"] and p["n_slices"])
            neg_covered += bool(not case["label"] and p["n_slices"])
            total_slices += p["n_slices"]
            lengths.extend(p["token_lens"])
            table.append({"front_end": names[arm], "code_number": case["code_number"],
                          "files": " | ".join(f["name"] for f in case["files"]), "missing_files": " | ".join(case["missing_files"]),
                          "label": "misuse" if case["label"] else "safe", "category": case["vuln_type"],
                          "source_status": case["source_status"], "slices": p["n_slices"], "prediction": int(positive),
                          "verdict": verdict})
        tp, fp, fn, tn = (counts[k] for k in ("TP", "FP", "FN", "TN"))
        assert tp + fn == 144 and fp + tn == 37 and sum(counts.values()) == 181
        pr = tp / (tp + fp) if tp + fp else 0
        rc = tp / (tp + fn)
        f1 = 2 * tp / (2 * tp + fp + fn)
        metrics.append({"front_end": names[arm], "cases": 181, "TP": tp, "FP": fp, "FN": fn, "TN": tn,
                        "Precision": pr, "Recall": rc, "F1": f1, "positive_coverage": pos_covered,
                        "positive_total": 144, "negative_coverage": neg_covered, "negative_total": 37,
                        "slices": total_slices, "max_tokens": max(lengths, default=0),
                        "slices_over_512": sum(n > 512 for n in lengths)})
        checks[arm] = {"errors": sum(len(r["errors"]) for r in slices.values()),
                       "cross_file_slices": sum(len(s.get("source_lines", {})) > 1 for r in slices.values() for s in r["slices"])}
    # Publication-facing outputs share exactly the manuscript's seven columns.
    # Full-precision metrics and coverage remain in metrics.json.
    short_names = {"CryptoLLM original front end": "Original", "CA-CPG front end": "CA-CPG"}
    comparison = [{"Front end": short_names[m["front_end"]],
                   **{k: m[k] for k in ("TP", "FP", "FN", "TN")},
                   "R": f"{m['Recall']:.3f}", "F1": f"{m['F1']:.3f}"} for m in metrics]
    for name, rows in (("case-results.csv", table), ("comparison.csv", comparison)):
        with (HERE / "out" / name).open("w", encoding="utf-8-sig", newline="") as h:
            w = csv.DictWriter(h, fieldnames=list(rows[0]))
            w.writeheader()
            w.writerows(rows)
    (HERE / "out/metrics.json").write_text(json.dumps({"unit": "ground-truth case", "metrics": metrics, "validation": checks}, indent=2))
    text = ["# RQ4：按真值用例评估", "", "181 个有明确标签的用例：144 个阳性、37 个阴性；一个多文件用例只产生一个预测。两臂统一按任意切片阳性即用例阳性。", "",
            "| Front end | TP | FP | FN | TN | R | F1 |", "|---|---:|---:|---:|---:|---:|---:|"]
    for row in comparison:
        text.append("| " + " | ".join(str(v) for v in row.values()) + " |")
    text += ["", "阳性用例切片覆盖：" + "；".join(
        f"{short_names[m['front_end']]} {m['positive_coverage']}/{m['positive_total']} ({m['positive_coverage']/m['positive_total']:.2%})"
        for m in metrics) + "。"]
    text += ["", "原表空标签记录排除；xlsx-row-4 的 CredentialInStringBBCase2.java 源码缺失，保留在181分母中，不产生切片，阳性计FN，并明确标记missing_source。不能将其解释为模型已分析该用例但判断错误。",
             "", "随机种子用例的 ABPM→ABMC 名称映射及省略.java后缀的修复见 inputs/cases.json。本目录为当前正式用例级实验。",
             "", "CA-CPG保留既有API类型保护及失效定义处理；原前端保留原作者归一化。此次统一用例和聚合方式，不声称所有前处理完全一致。"]
    (HERE / "RESULTS.md").write_text("\n".join(text) + "\n")
    print(json.dumps({"metrics": metrics, "validation": checks}, indent=2))


if __name__ == "__main__":
    main()
