#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""将工具报告整理为文件级评分所需的 file,line,rule CSV。

用法:
    normalize_findings.py <tool> <raw_output> <out.csv> [--exclude-rules R1,R2]

tool ∈ {semgrep, spotbugs, cognicrypt, cryptoguard, cryptoeagle}

输出使用源文件名和报告中的规则 ID，无法取得行号时保留空值。
--exclude-rules 按输入报告的规则 ID 过滤。消融实验用该脚本整理 CryptoEagle 的告警。
需要将基线规则映射为 C1–C18 时，使用 map_to_cryptoql.py 及对应的规则级评分器。
"""
from __future__ import annotations

import argparse
import csv
import json
import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

# SpotBugs XML 只纳入下列 FindSecBugs 密码学与传输安全规则。
CRYPTO_BUG_TYPES = {
    "CIPHER_INTEGRITY", "PADDING_ORACLE", "ECB_MODE", "STATIC_IV",
    "DES_USAGE", "TDES_USAGE", "RSA_NO_PADDING", "RSA_KEY_SIZE",
    "BLOWFISH_KEY_SIZE", "HARD_CODE_PASSWORD", "HARD_CODE_KEY",
    "WEAK_MESSAGE_DIGEST_MD5", "WEAK_MESSAGE_DIGEST_SHA1",
    "CUSTOM_MESSAGE_DIGEST", "PREDICTABLE_RANDOM", "DMI_RANDOM_USED_ONLY_ONCE",
    "NULL_CIPHER", "WEAK_TRUST_MANAGER", "WEAK_HOSTNAME_VERIFIER",
    "SSL_CONTEXT", "DEFAULT_HTTP_CLIENT", "HAZELCAST_SYMMETRIC_ENCRYPTION",
    "ESAPI_ENCRYPTOR", "UNENCRYPTED_SOCKET", "UNENCRYPTED_SERVER_SOCKET",
}

SOURCE_SUFFIXES = (".java", ".py", ".c", ".cpp", ".cc", ".h", ".hpp")


def basename(value: str) -> str:
    return os.path.basename(str(value or "").replace("\\", "/")).strip()


def class_to_file(fqcn: str) -> str:
    """全限定类名 -> 源文件名；去掉包名与内部类后缀。"""
    return fqcn.split(".")[-1].split("$")[0] + ".java"


def parse_semgrep(path: Path):
    data = json.loads(path.read_text(encoding="utf-8"))
    for item in data.get("results", []):
        name = basename(item.get("path", ""))
        if name.endswith(SOURCE_SUFFIXES):
            line = (item.get("start") or {}).get("line", "")
            yield name, line, item.get("check_id", "unknown")


def parse_spotbugs(path: Path):
    root = ET.parse(path).getroot()
    for bug in root.iter("BugInstance"):
        bug_type = bug.get("type", "")
        if bug_type not in CRYPTO_BUG_TYPES:
            continue
        for source_line in bug.iter("SourceLine"):
            name = source_line.get("sourcefile") or ""
            if name.endswith(".java"):
                yield name, source_line.get("start", ""), bug_type
                break


def parse_cognicrypt(path: Path):
    with path.open(newline="", encoding="utf-8", errors="replace") as handle:
        for row in csv.DictReader(handle, delimiter=";"):
            fqcn = (row.get("Class") or "").strip()
            if fqcn:
                yield class_to_file(fqcn), (row.get("Line") or "").strip(), (row.get("ErrorType") or "unknown").strip()


def parse_cryptoguard(path: Path):
    """读取 CryptoAPI-Bench 的 CSVDefault 报告。

    从报告中识别 org.cryptoapi.bench 包的类，兼容斜杠路径和点分全名。
    规则取 IssueRuleNumber，行号留空。
    """
    with path.open(newline="", encoding="utf-8", errors="replace") as handle:
        reader = csv.reader(handle)
        next(reader, None)
        for row in reader:
            rule = row[8] if len(row) > 8 else (row[0][:40] if row else "unknown")
            for cell in row:
                if "org/cryptoapi/bench" not in cell and "org.cryptoapi.bench" not in cell:
                    continue
                for token in cell.replace(",", " ").split():
                    if "org/cryptoapi/bench" not in token and "org.cryptoapi.bench" not in token:
                        continue
                    token = token.strip(".,;()")
                    if token.endswith(".class"):
                        token = token[: -len(".class")]
                    yield class_to_file(token.replace("/", ".")), "", rule
                    break
                break


def parse_cryptoeagle(path: Path):
    """读取 EvaluationMain 用 --report-output=<path.json> 写出的告警字段。"""
    for item in json.loads(path.read_text(encoding="utf-8")):
        yield basename(item.get("srcFile", "")), item.get("lineNo", ""), str(item.get("ruleId", ""))


PARSERS = {
    "semgrep": parse_semgrep,
    "spotbugs": parse_spotbugs,
    "cognicrypt": parse_cognicrypt,
    "cryptoguard": parse_cryptoguard,
    "cryptoeagle": parse_cryptoeagle,
}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("tool", choices=sorted(PARSERS))
    parser.add_argument("raw", type=Path)
    parser.add_argument("out", type=Path)
    parser.add_argument("--exclude-rules", default="", help="逗号分隔的规则 ID，命中的告警不写入")
    args = parser.parse_args()

    if not args.raw.exists():
        print(f"[错误] 原始输出不存在: {args.raw}", file=sys.stderr)
        return 1

    excluded = {r.strip() for r in args.exclude_rules.split(",") if r.strip()}
    rows, dropped, skipped = [], 0, 0
    for name, line, rule in PARSERS[args.tool](args.raw):
        if rule in excluded:
            skipped += 1
            continue
        # 过滤解析器产出的无文件名或不带受支持源码后缀的记录。
        if not name or not name.endswith(SOURCE_SUFFIXES):
            dropped += 1
            continue
        rows.append((name, line, rule))

    args.out.parent.mkdir(parents=True, exist_ok=True)
    with args.out.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.writer(handle, lineterminator="\n")
        writer.writerow(["file", "line", "rule"])
        writer.writerows(rows)

    files = len({r[0] for r in rows})
    print(f"  {args.tool}: 告警 {len(rows)} 条，涉及文件 {files} 个 -> {args.out}")
    if dropped:
        print(f"  {args.tool}: 丢弃无源文件定位的告警 {dropped} 条")
    if skipped:
        print(f"  {args.tool}: 按 --exclude-rules 跳过 {skipped} 条")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
