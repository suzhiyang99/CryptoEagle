#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""将原始报告统一为 C1–C18 告警 JSON，供规则级评分使用。

用法:
    map_to_cryptoql.py <tool> <raw_output> <out_findings.json>

支持 Semgrep、SpotBugs/FindSecBugs、CogniCrypt、CryptoGuard 和 CryptoEagle。
基线映射表使用归档编号，输出前由 rule_schema.LEGACY_TO_CORE 转换为 C1–C18。
一条原生告警可以生成多条映射后告警，例如 CryptoGuard Rule 1 对应 C1 和 C14。
无映射的记录跳过。CryptoEagle 的历史报告按自身规则版本迁移。
输出包含文件名、行号、映射后规则、原生规则和规则集版本，缺少行号时写 0。
CryptoAPI-Bench 的匹配由 evaluate_benchmark.py 执行。Apache 的独立评分器复用下方
CryptoGuard 和 CogniCrypt 的映射，按基准用例的外层类和预期规则评分。
"""
from __future__ import annotations

import argparse
import csv
import json
import os
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]/"tool/crypto-ext/scripts"))
from rule_schema import RULE_SET, LEGACY_TO_CORE, migrate_findings

# ---------------------------------------------------------------- Semgrep
# 按 check_id 中匹配到的第一个规则片段取归档编号。
SEMGREP_MAP = {
    "ecb-cipher": ["C1"],
    "use-of-aes-ecb": ["C1"],
    "cbc-padding-oracle": ["C4"],
    "des-is-deprecated": ["X7"],
    "desede-is-deprecated": ["X7"],
    "use-of-blowfish": ["X7"],
    "use-of-rc2": ["X7"],
    "use-of-rc4": ["X7"],
    "use-of-idea": ["X7"],
    "use-of-md5": ["C5"],
    "use-of-md4": ["C5"],
    "use-of-md2": ["C5"],
    "use-of-sha1": ["C5"],
    "weak-rsa": ["C13"],
    "use-of-weak-rsa-key": ["C13"],
    "insecure-hash-function": ["C5"],
    "hardcoded-secret": ["C2"],
    "use-of-default-aes": ["C1"],
    "unencrypted-socket": ["X1"],
    "no-null-cipher": ["X7"],
}

# --------------------------------------------------- SpotBugs + FindSecBugs
# 按 FindSecBugs 的 bug type 取一个或多个归档编号。
SPOTBUGS_MAP = {
    "ECB_MODE": ["C1"],
    "PADDING_ORACLE": ["C4"],
    "CIPHER_INTEGRITY": ["C14"],
    "STATIC_IV": ["C3"],
    "DES_USAGE": ["X7"],
    "TDES_USAGE": ["X7"],
    "BLOWFISH_KEY_SIZE": ["X7"],
    "NULL_CIPHER": ["X7"],
    "WEAK_MESSAGE_DIGEST_MD5": ["C5"],
    "WEAK_MESSAGE_DIGEST_SHA1": ["C5"],
    "CUSTOM_MESSAGE_DIGEST": ["C5"],
    "HARD_CODE_KEY": ["C2"],
    "HARD_CODE_PASSWORD": ["C2", "X6"],
    "PREDICTABLE_RANDOM": ["C9"],
    "DMI_RANDOM_USED_ONLY_ONCE": ["C9"],
    "RSA_KEY_SIZE": ["C13"],
    "RSA_NO_PADDING": ["C11"],
    "WEAK_TRUST_MANAGER": ["X3"],
    "WEAK_HOSTNAME_VERIFIER": ["X3"],
    "SSL_CONTEXT": ["X3"],
    "DEFAULT_HTTP_CLIENT": ["X1"],
    "UNENCRYPTED_SOCKET": ["X1"],
    "UNENCRYPTED_SERVER_SOCKET": ["X1"],
}

# ------------------------------------------------------------- CryptoGuard
# 按 CSVDefault 的 IssueRuleNumber 选择映射，描述用于注明来源。
CRYPTOGUARD_MAP = {
    "1":  ["X7", "C1"],   # Found broken crypto schemes —— 检查整个 transformation，含废弃算法与 ECB
    "2":  ["C5"],         # Found broken hash functions
    "3":  ["C2"],         # Used constant keys in code
    "4":  ["X3"],         # Uses untrusted TrustManager
    "5":  ["C13"],        # Used export grade public Key
    "6":  ["X3"],         # Used untrusted HostNameVerifier
    "7":  ["X1"],         # Used HTTP Protocol
    "8":  ["C7"],         # Used < 1000 iteration for PBE
    "9":  ["C8"],         # Found constant salts in code
    "10": ["C3"],         # Found constant IV in code
    "11": ["C9"],         # Found predictable seeds in code
    "12": ["X3"],         # Should check HostnameVerification manually
    "13": ["C9"],         # Used untrusted PRNG
    "14": ["X6"],         # Used Predictable KeyStore Password
    "15": ["X10"],        # Broken MAC
    "16": ["C2"],         # Predictable PBE password
}


def cognicrypt_rules(error_type: str, violated_rule: str, message: str) -> list[str]:
    """结合 CrySL 错误种类、违反的 API 规则和约束描述，返回归档编号。"""
    klass = violated_rule.split(".")[-1]
    text = message or ""

    if error_type in ("RequiredPredicateError", "AlternativeReqPredicateError"):
        # "X was not properly generated as generatedKey/generatedRandom/..." —— 参数来源不可信
        return {
            "SecureRandom": ["C9"],
            "IvParameterSpec": ["C3"],
            "PBEParameterSpec": ["C8"],
            "SecretKey": ["C2"],
            "SecretKeySpec": ["C2"],
            "Cipher": ["C2"],
            "KeyPair": ["C2"],
            "PublicKey": ["C2"],
            "PrivateKey": ["C2"],
        }.get(klass, [])

    if error_type == "ConstraintError":
        if "iterationCount" in text:
            return ["C7"]
        if "keysize" in text:
            return ["C13"]
        if "mode(transformation)" in text:
            return ["C1"]
        if klass == "MessageDigest" or "SHA-256" in text:
            return ["C5"]
        if klass == "Mac":
            return ["X10"]
        if klass == "KeyStore":
            return ["X6"]
        if "notHardCoded" in text or "neverTypeOf" in text:
            return ["C2"]
        if "alg(transformation)" in text or "algorithm in {AES" in text:
            # 算法白名单约束：既覆盖废弃算法，也覆盖 AES 之外的取值
            return ["X7", "C1"]
        return []

    # 操作未完成和取值精度提示在本映射中没有对应编号。
    return []


def basename(value: str) -> str:
    return os.path.basename(str(value or "").replace("\\", "/")).strip()


def class_to_file(fqcn: str) -> str:
    return fqcn.split(".")[-1].split("$")[0] + ".java"


def from_semgrep(path: Path):
    for item in json.loads(path.read_text(encoding="utf-8")).get("results", []):
        name = basename(item.get("path", ""))
        if not name.endswith(".java"):
            continue
        check_id = item.get("check_id", "")
        rules: list[str] = []
        for key, targets in SEMGREP_MAP.items():
            if key in check_id:
                rules = targets
                break
        yield name, (item.get("start") or {}).get("line", 0), rules, check_id


def from_spotbugs(path: Path):
    for bug in ET.parse(path).getroot().iter("BugInstance"):
        bug_type = bug.get("type", "")
        if bug_type not in SPOTBUGS_MAP:
            continue
        for source_line in bug.iter("SourceLine"):
            name = source_line.get("sourcefile") or ""
            if name.endswith(".java"):
                yield name, source_line.get("start", 0), SPOTBUGS_MAP[bug_type], bug_type
                break


def from_cognicrypt(path: Path):
    with path.open(newline="", encoding="utf-8", errors="replace") as handle:
        for row in csv.DictReader(handle, delimiter=";"):
            fqcn = (row.get("Class") or "").strip()
            if not fqcn:
                continue
            error_type = (row.get("ErrorType") or "").strip()
            rules = cognicrypt_rules(error_type, (row.get("ViolatedRule") or "").strip(), row.get("Message") or "")
            yield class_to_file(fqcn), (row.get("LineNumber") or 0), rules, error_type


def from_cryptoguard(path: Path):
    with path.open(newline="", encoding="utf-8", errors="replace") as handle:
        reader = csv.reader(handle)
        next(reader, None)
        for row in reader:
            rule_number = row[8].strip() if len(row) > 8 else ""
            name = ""
            for cell in row:
                if "org/cryptoapi/bench" in cell or "org.cryptoapi.bench" in cell:
                    for token in cell.replace(",", " ").split():
                        if "org/cryptoapi/bench" in token or "org.cryptoapi.bench" in token:
                            token = token.strip(".,;()")
                            if token.endswith(".class"):
                                token = token[: -len(".class")]
                            name = class_to_file(token.replace("/", "."))
                            break
                    break
            if name:
                yield name, 0, CRYPTOGUARD_MAP.get(rule_number, []), f"Rule {rule_number}"


def from_cryptoeagle(path: Path):
    """先按报告版本迁移规则 ID，再提取 Java 和 Python 告警。"""
    for item in migrate_findings(json.loads(path.read_text(encoding="utf-8"))):
        name = basename(item.get("srcFile", ""))
        if name.endswith((".java", ".py")):
            yield name, item.get("lineNo", 0), [str(item.get("ruleId", ""))], str(item.get("ruleId", ""))


READERS = {
    "semgrep": from_semgrep,
    "spotbugs": from_spotbugs,
    "cognicrypt": from_cognicrypt,
    "cryptoguard": from_cryptoguard,
    "cryptoeagle": from_cryptoeagle,
}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("tool", choices=sorted(READERS))
    parser.add_argument("raw", type=Path)
    parser.add_argument("out", type=Path)
    args = parser.parse_args()

    if not args.raw.exists():
        print(f"[错误] 原始输出不存在: {args.raw}", file=sys.stderr)
        return 1

    findings, unmapped = [], {}
    total = 0
    for name, line, rules, native in READERS[args.tool](args.raw):
        total += 1
        if not rules:
            unmapped[native] = unmapped.get(native, 0) + 1
            continue
        for rule_id in rules:
            if args.tool != "cryptoeagle":
                rule_id = LEGACY_TO_CORE.get(rule_id)
                if rule_id is None: continue
            findings.append({"srcFile": name, "lineNo": int(line or 0), "ruleId": rule_id,
                             "nativeRule": native, "ruleSet": RULE_SET})

    args.out.parent.mkdir(parents=True, exist_ok=True)
    args.out.write_text(json.dumps(findings, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"  {args.tool}: 原生告警 {total} 条 -> CryptoQL 告警 {len(findings)} 条 -> {args.out}")
    if unmapped:
        detail = ", ".join(f"{k}×{v}" for k, v in sorted(unmapped.items(), key=lambda x: -x[1])[:6])
        print(f"  {args.tool}: 无对应误用类型、未映射 {sum(unmapped.values())} 条（{detail}）")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
