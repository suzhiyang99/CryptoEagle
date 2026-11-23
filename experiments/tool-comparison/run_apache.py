#!/usr/bin/env python3
"""Rerun CryptoGuard/CogniCrypt on the nine scored Apache project JARs.

Use original bytecode, fixed installed tool versions, and no dependency JARs,
matching the previous benchmark configuration. No text logs by default.
"""
import argparse
import hashlib
import json
import os
import shutil
import signal
import subprocess
import sys
import time
from datetime import datetime, timezone
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
RAW = HERE / "raw/apache"


def digest(p):
    return hashlib.sha256(p.read_bytes()).hexdigest()


def main():
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--timeout", type=int, default=900)
    args = ap.parse_args()
    manifest = json.loads((HERE / "inputs/apache-jars.json").read_text())
    jdk8 = os.environ.get("JDK8_HOME") or subprocess.check_output(["/usr/libexec/java_home", "-v", "1.8"], text=True).strip()
    jdk17 = os.environ.get("JDK17_HOME") or subprocess.check_output(["/usr/libexec/java_home", "-v", "17"], text=True).strip()
    tools = ROOT / "external-tools/java-baselines"
    rules = tools / "cognicrypt/rules/JavaCryptographicArchitecture"
    rules_hash = hashlib.sha256()
    for p in sorted(p for p in rules.rglob("*") if p.is_file()):
        rules_hash.update(str(p.relative_to(rules)).encode() + b"\0" + p.read_bytes())
    report = {"started_utc": datetime.now(timezone.utc).isoformat(),
              "scope": "9 Apache projects; Spark excluded as in the paper; project bytecode only",
              "tool_sha256": {name: digest(tools / path) for name, path in {
                  "cryptoguard": "cryptoguard/cryptoguard.jar",
                  "cognicrypt": "cognicrypt/HeadlessJavaScanner-5.0.1.jar"}.items()},
              "crysl_sha256": rules_hash.hexdigest(), "jars": manifest, "runs": []}
    RAW.mkdir(parents=True, exist_ok=True)
    for project, item in manifest.items():
        jar = ROOT / item["jar"]
        assert digest(jar) == item["sha256"], jar
        for tool, home in (("cryptoguard", jdk8), ("cognicrypt", jdk17)):
            dest = RAW / project / tool
            if dest.exists():
                shutil.rmtree(dest)
            dest.mkdir(parents=True)
            if tool == "cryptoguard":
                command = [str(Path(home) / "bin/java"), "-Xmx8g", "-jar", str(tools / "cryptoguard/cryptoguard.jar"),
                           "-in", "jar", "-s", str(jar), "-m", "CSV", "-o", "cryptoguard.csv", "-n", "-new"]
                output = dest / "cryptoguard.csv"
            else:
                command = [str(Path(home) / "bin/java"), "-Xmx8g", "-Xss1g", "-jar", str(tools / "cognicrypt/HeadlessJavaScanner-5.0.1.jar"),
                           "--appPath", str(jar), "--rulesDir", str(rules), "--reportPath", str(dest), "--reportFormat", "CSV"]
                output = dest / "CryptoAnalysis-Report.csv"
            print(f"START {tool} {project}", flush=True)
            start = time.monotonic()
            log = (dest / "run.log").open("w") if os.environ.get("KEEP_LOGS") == "1" else None
            try:
                proc = subprocess.Popen(command, cwd=dest, env=dict(os.environ, JAVA_HOME=home),
                                        stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
                try:
                    rc = proc.wait(timeout=args.timeout)
                    status = "SUCCESS" if rc == 0 and output.exists() else f"FAILED_{rc}"
                except subprocess.TimeoutExpired:
                    os.killpg(proc.pid, signal.SIGKILL)
                    proc.wait()
                    rc, status = None, "TIMEOUT"
            finally:
                if log:
                    log.close()
            row = {"tool": tool, "project": project, "status": status, "exit_code": rc,
                   "seconds": round(time.monotonic() - start, 3), "command": command,
                   "report": str(output.relative_to(ROOT)), "sha256": digest(output) if output.exists() else None,
                   "jar_sha256_after": digest(jar)}
            assert row["jar_sha256_after"] == item["sha256"]
            report["runs"].append(row)
            (RAW / "run-metadata.json").write_text(json.dumps(report, indent=2) + "\n")
            print(f"END {tool} {project} {status} {row['seconds']}s", flush=True)
    if any(r["status"] != "SUCCESS" for r in report["runs"]):
        raise SystemExit("Apache baseline run failed; previous verdict input has not been replaced.")
    subprocess.run([sys.executable, str(HERE / "score_apache.py")], check=True)


if __name__ == "__main__":
    main()
