#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""对照基准真值，生成逐文件判定 CSV 和汇总指标 JSON。

用法:
    evaluate_filelevel.py --tool NAME --findings f.csv --gt-xlsx  CryptoAPI-Bench_details.xlsx \
                          --out-detail d.csv --out-metrics m.json
    evaluate_filelevel.py --tool NAME --findings f.csv --gt-jsonl PyCryptoBench/metadata.jsonl ...

输入为 file,line,rule CSV。
行号与规则 ID 不参与匹配，规则 ID 保留在逐文件明细中。真值范围外的告警文件另行记录。
PyCryptoBench 的工具对比按文件处理。CryptoAPI-Bench 的主实验和消融按用例与规则评分。
XLSX 通过 Python 标准库读取，并复用规则级评分器中的 Java 文件名别名。
"""
from __future__ import annotations

import argparse
import csv
import json
import sys
import zipfile
from collections import defaultdict
from pathlib import Path
from xml.etree import ElementTree as ET

NS = "http://schemas.openxmlformats.org/spreadsheetml/2006/main"


# ---------------------------------------------------------------- 真值：xlsx
def column_index(ref: str) -> int:
    letters = "".join(ch for ch in ref if ch.isalpha())
    index = 0
    for ch in letters:
        index = index * 26 + ord(ch) - 64
    return index - 1


def normalize_java_name(value: str) -> str | None:
    value = value.strip()
    if not value or value in ("Files", "---"):
        return None
    if value.endswith(".java"):
        return value
    if value[0].isupper() and " " not in value:
        return value + ".java"
    return None


def load_xlsx_gt(path: Path):
    """CryptoAPI-Bench_details.xlsx -> (labels, categories, extras)

    B 列列出文件名，支持逗号或换行分隔。D 列为标签，E 列为误用类别。
    合并文件名别名后，同一文件只要有一条正例标注，就计为正例。
    """
    sys.path.insert(0, str(Path(__file__).resolve().parents[2] / "tool/crypto-ext/scripts"))
    from evaluate_benchmark import CRYPTOAPI_JAVA_FILENAME_ALIASES

    archive = zipfile.ZipFile(path)
    ns = {"m": NS}
    shared = [
        "".join(node.text or "" for node in item.iter(f"{{{NS}}}t"))
        for item in ET.fromstring(archive.read("xl/sharedStrings.xml")).findall("m:si", ns)
    ]
    sheet = ET.fromstring(archive.read("xl/worksheets/sheet1.xml"))
    labels: dict[str, bool] = {}
    categories: dict[str, str] = {}
    for row in sheet.findall(".//m:row", ns):
        cells: dict[int, str] = {}
        for cell in row.findall("m:c", ns):
            value = cell.find("m:v", ns)
            if value is None:
                continue
            cells[column_index(cell.get("r"))] = shared[int(value.text)] if cell.get("t") == "s" else value.text

        raw_names = cells.get(1)
        if not raw_names:
            continue
        names = [n for n in (normalize_java_name(x) for x in str(raw_names).replace(",", "\n").split("\n")) if n]
        if not names:
            continue

        label_cell = cells.get(3)
        type_cell = cells.get(4)
        if label_cell is None:
            # 标签列缺失时退而根据类型列推断；类型也为空则该行不参与评估
            positive = bool(type_cell and str(type_cell).strip() not in ("---", ""))
            if not positive:
                continue
        else:
            positive = str(label_cell).strip().upper() in ("1", "TRUE", "YES")

        for name in names:
            name = CRYPTOAPI_JAVA_FILENAME_ALIASES.get(name, name)
            labels[name] = labels.get(name, False) or positive
            if type_cell and str(type_cell).strip() not in ("---", ""):
                categories.setdefault(name, str(type_cell).strip())
    return labels, categories, {}


# --------------------------------------------------------------- 真值：jsonl
def difficulty_of(item: dict) -> str:
    """PyCryptoBench 的难度维度，按由难到易取第一个命中的。"""
    for flag, name in (
        ("DBLInterprocedural", "InterprocViaReturn"),
        ("InterProcedural", "Interprocedural"),
        ("FieldSensitive", "FieldSensitive"),
        ("Global", "Global"),
        ("PathSensitive", "PathSensitive"),
    ):
        if int(item.get(flag, 0) or 0) == 1:
            return name
    return "Basic"


def load_jsonl_gt(path: Path):
    """PyCryptoBench metadata.jsonl -> (labels, categories, extras)"""
    labels: dict[str, bool] = {}
    categories: dict[str, str] = {}
    extras: dict[str, dict] = {}
    with path.open(encoding="utf-8") as handle:
        for line in handle:
            if not line.strip():
                continue
            item = json.loads(line)
            name = str(item["FileName"]).strip()
            labels[name] = bool(item.get("HasVuln"))
            categories[name] = f"Rule {item['Rule']}"
            extras[name] = {
                "rule": f"Rule {item['Rule']}",
                "difficulty": difficulty_of(item),
                "test_type": item.get("TestType", ""),
            }
    return labels, categories, extras


# -------------------------------------------------------------------- 评估
def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--tool", required=True)
    parser.add_argument("--findings", required=True, type=Path)
    parser.add_argument("--gt-xlsx", type=Path)
    parser.add_argument("--gt-jsonl", type=Path)
    parser.add_argument("--out-detail", required=True, type=Path)
    parser.add_argument("--out-metrics", required=True, type=Path)
    parser.add_argument("--dataset", default="", help="仅用于写进 metrics.json 的标签")
    args = parser.parse_args()

    if bool(args.gt_xlsx) == bool(args.gt_jsonl):
        parser.error("--gt-xlsx 与 --gt-jsonl 必须且只能给一个")

    labels, categories, extras = (
        load_xlsx_gt(args.gt_xlsx) if args.gt_xlsx else load_jsonl_gt(args.gt_jsonl)
    )

    reported: dict[str, set[str]] = defaultdict(set)
    with args.findings.open(newline="", encoding="utf-8") as handle:
        reader = csv.DictReader(handle)
        for row in reader:
            reported[row["file"].strip()].add(row["rule"].strip())

    positives = {f for f, v in labels.items() if v}
    negatives = {f for f, v in labels.items() if not v}
    flagged = set(reported)

    tp = len(positives & flagged)
    fn = len(positives - flagged)
    fp = len(negatives & flagged)
    tn = len(negatives - flagged)
    precision = tp / (tp + fp) if tp + fp else 0.0
    recall = tp / (tp + fn) if tp + fn else 0.0
    f1 = 2 * precision * recall / (precision + recall) if precision + recall else 0.0

    dataset = args.dataset or (args.gt_xlsx or args.gt_jsonl).stem
    print(f"===== {args.tool} on {dataset}（文件级）=====")
    print(f"  真值：正例 {len(positives)}，反例 {len(negatives)}，合计 {len(labels)}")
    print(f"  报警文件 {len(flagged)} 个，告警 {sum(len(v) for v in reported.values())} 类次")
    print(f"  TP={tp}  FP={fp}  FN={fn}  TN={tn}")
    print(f"  Precision={precision:.3f}  Recall={recall:.3f}  F1={f1:.3f}")

    outside = flagged - set(labels)
    if outside:
        print(f"  [提示] 报警但不在真值清单中的文件 {len(outside)} 个：{sorted(outside)[:5]}")

    # 逐类别召回
    by_category: dict[str, list[int]] = defaultdict(lambda: [0, 0])
    for name in positives:
        key = categories.get(name, "(未标注)")
        by_category[key][0] += 1
        if name in flagged:
            by_category[key][1] += 1
    print("\n  ----- 按类别的召回 -----")
    for key in sorted(by_category, key=lambda k: -by_category[k][0]):
        total, hit = by_category[key]
        print(f"    {key:<34}{total:>5}{hit:>5}{hit / total:>8.2f}")

    # PyCryptoBench 提供难度标签，可以按难度汇总正例召回率。
    if extras:
        by_difficulty: dict[str, list[int]] = defaultdict(lambda: [0, 0])
        for name in positives:
            key = extras.get(name, {}).get("difficulty", "?")
            by_difficulty[key][0] += 1
            if name in flagged:
                by_difficulty[key][1] += 1
        print("\n  ----- 按难度的召回 -----")
        for key in sorted(by_difficulty):
            total, hit = by_difficulty[key]
            print(f"    {key:<34}{total:>5}{hit:>5}{hit / total:>8.2f}")

    extra_fields = ["rule", "difficulty", "test_type"] if extras else []
    args.out_detail.parent.mkdir(parents=True, exist_ok=True)
    with args.out_detail.open("w", newline="", encoding="utf-8-sig") as handle:
        writer = csv.writer(handle, lineterminator="\n")
        writer.writerow(["file", "ground_truth", "reported", "verdict", "category", "reported_rules"] + extra_fields)
        for name in sorted(labels):
            positive = name in positives
            hit = name in flagged
            verdict = ("TP" if hit else "FN") if positive else ("FP" if hit else "TN")
            row = [
                name,
                "POS" if positive else "NEG",
                "Y" if hit else "N",
                verdict,
                categories.get(name, ""),
                "|".join(sorted(reported.get(name, ()))),
            ]
            row += [extras.get(name, {}).get(k, "") for k in extra_fields]
            writer.writerow(row)

    args.out_metrics.parent.mkdir(parents=True, exist_ok=True)
    args.out_metrics.write_text(
        json.dumps(
            {
                "tool": args.tool,
                "dataset": dataset,
                "granularity": "file",
                "tp": tp, "fp": fp, "fn": fn, "tn": tn,
                "precision": precision, "recall": recall, "f1": f1,
                "groundTruthPositives": len(positives),
                "groundTruthNegatives": len(negatives),
                "flaggedFiles": len(flagged),
                "flaggedOutsideGroundTruth": sorted(outside),
            },
            indent=2,
            ensure_ascii=False,
        )
        + "\n",
        encoding="utf-8",
    )
    print(f"\n  明细 -> {args.out_detail}")
    print(f"  指标 -> {args.out_metrics}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
