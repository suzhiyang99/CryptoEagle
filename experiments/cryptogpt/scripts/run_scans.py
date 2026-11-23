#!/usr/bin/env python3
"""Runs CryptoEagle (tool/crypto-ext) on the 63 CryptoGPT real-world scopes listed in inputs/scope-manifest.json.

Java scopes run on a project snapshot under benchmarks/cryptogpt-realworld-java-full (in place, or on a copy of the
scoped files); Python scopes run on the reported file together with the dependencies it needs, copied from
benchmarks/cryptogpt-realworld-python-full. Source files are checked for changes during each scan. Output: runs/<scope id>/findings.json and run.json, runs/runs.json, runs/scope-manifest.json.

  python3 run_scans.py [--resume] [--scope SCOPE_ID ...]

JAVA overrides the JDK 21 java binary. CRYPTOEAGLE_CLASSPATH can supply the classpath from a current build.
Otherwise sbt compiles the tool and exports the classpath. Text logs are written only when KEEP_LOGS=1.
"""
import argparse, collections, json, os, shutil, signal, subprocess, sys, time
from pathlib import Path
from contextlib import nullcontext

HERE = Path(__file__).resolve().parents[1]
ARTIFACT = HERE.parents[1]
TOOL = ARTIFACT / "tool" / "crypto-ext"
SIX_SSL = "six related SSL source files"
KEEP_LOGS = os.environ.get("KEEP_LOGS") == "1"


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n")


def classpath():
    out = os.environ.get("CRYPTOEAGLE_CLASSPATH") or subprocess.run(
        ["sbt", "-error", "compile", "export Runtime / fullClasspath"], cwd=TOOL,
        stdout=subprocess.PIPE, text=True, check=True).stdout.strip().splitlines()[-1]
    if KEEP_LOGS:
        saved = HERE / "logs" / "classpath.txt"
        saved.parent.mkdir(parents=True, exist_ok=True)
        saved.write_text(out + "\n")
    return out


def java_binary():
    if os.environ.get("JAVA"):
        return os.environ["JAVA"]
    home = os.environ.get("JAVA_HOME")
    if not home and sys.platform == "darwin":
        home = subprocess.check_output(["/usr/libexec/java_home", "-v", "21"], text=True).strip()
    return str(Path(home) / "bin" / "java") if home else "java"


def scoped(job, rel):
    """Path of a recorded source file inside the scanned input directory."""
    if job["language"] == "java" and job["scope"] == SIX_SSL:
        return Path(rel).name
    if job["language"] == "java" and job["scope"] not in (".", SIX_SSL) and ";" not in job["scope"]:
        return Path(rel).relative_to(job["scope"])
    return Path(rel)


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--resume", action="store_true", help="keep scopes that already finished successfully")
    parser.add_argument("--scope", action="append", help="rerun this scope and retain the remaining archived scopes")
    args = parser.parse_args()
    out = HERE / "runs"
    manifest = json.loads((HERE / "inputs" / "scope-manifest.json").read_text())
    known = {job["id"] for job in manifest}
    selected = set(args.scope or known)
    if selected - known:
        parser.error("unknown scope: " + ", ".join(sorted(selected - known)))
    results = {}
    if args.scope:
        index = out / "runs.json"
        if index.is_file():
            results = {record["id"]: record for record in json.loads(index.read_text())}
        if not known - selected <= results.keys():
            parser.error("selective runs require an archive of the other scopes")
    out.mkdir(exist_ok=True)
    (out / ".gitignore").write_text("_inputs/\n*.log\n")
    cp, java = classpath(), java_binary()
    jobs = []
    for job in manifest:
        root = ARTIFACT / job["source_root"]
        for rel in job["source_files"]:
            assert (root / rel).is_file(), (root, rel)
        if job["input"]:
            inputs = ARTIFACT / job["input"]
        else:
            inputs = out / "_inputs" / job["id"]
            for rel in job["source_files"]:
                dest = inputs / (Path(rel).name if job["language"] == "java" and job["scope"] == SIX_SSL else rel)
                dest.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(root / rel, dest)
        if job["language"] == "java":
            assert sum(1 for _ in inputs.rglob("*.java")) == job["java_files"], (inputs, job["java_files"])
        jobs.append(dict(job, input=str(inputs.relative_to(ARTIFACT))))
    write(out / "scope-manifest.json", jobs)
    def save_record(record):
        results[record["id"]] = record
        write(out / "runs.json", [results[job["id"]] for job in jobs if job["id"] in results])

    selected_jobs = [job for job in jobs if job["id"] in selected]
    for index, job in enumerate(selected_jobs, 1):
        dest = out / job["id"]
        dest.mkdir(parents=True, exist_ok=True)
        report, meta, start = dest / "findings.json", dest / "run.json", time.monotonic()
        print(f"START [{index}/{len(selected_jobs)}] {job['id']} {len(job['source_files'])} files", flush=True)
        if args.resume and report.exists() and meta.exists():
            saved = json.loads(meta.read_text())
            if saved["status"] == "SUCCESS" and saved["source_files"] == job["source_files"]:
                save_record(saved)
                continue
        command = ["-Xmx8g", "-Xss128m"]
        if job["language"] == "java":
            command += ["org.codeminers.standalone.JavaEnrichmentMain", str(ARTIFACT / job["input"]), "--no-delombok",
                        "--max-flow-call-depth=2", "--summary"]
        else:
            command += ["org.codeminers.standalone.EvaluationMain", "python", str(ARTIFACT / job["input"]),
                        f"--metrics-output={dest}/performance.json"]
        command += ["--report=json", f"--report-output={report}"]
        source_contents = {rel: (ARTIFACT / job["source_root"] / rel).read_bytes()
                           for rel in job["source_files"]}
        with ((dest / "scan.log").open("w") if KEEP_LOGS else nullcontext(None)) as log:
            proc = subprocess.Popen([java, command[0], command[1], "-cp", cp, *command[2:]], cwd=TOOL, stdout=log,
                                    stderr=subprocess.STDOUT, start_new_session=True)
            try:
                rc = proc.wait(timeout=600)
                status = "SUCCESS" if rc == 0 and report.exists() else f"FAILED_{rc}"
            except subprocess.TimeoutExpired:
                os.killpg(proc.pid, signal.SIGKILL)
                proc.wait()
                status = "TIMEOUT"
        findings = json.loads(report.read_text()) if status == "SUCCESS" else []
        assert all(f.get("ruleSet") == "C1-C18" and f["ruleId"] in {f"C{i}" for i in range(1, 19)} for f in findings)
        unchanged = all((ARTIFACT / job["source_root"] / rel).read_bytes() == content and
                        (ARTIFACT / job["input"] / scoped(job, rel)).read_bytes() == content
                        for rel, content in source_contents.items())
        assert unchanged, job["id"]
        record = dict(job, status=status, seconds=round(time.monotonic() - start, 3), raw_findings=len(findings),
                      rules=dict(collections.Counter(f["ruleId"] for f in findings)), source_unchanged=unchanged,
                      report=str(report.relative_to(ARTIFACT)),
                      command=["java", *command[:2], "-cp", "<tool/crypto-ext runtime classpath>",
                               *[c.replace(str(ARTIFACT) + "/", "") for c in command[2:]]])
        write(meta, record)
        save_record(record)
        print(f"END {job['id']} {status}: {len(findings)} findings, {record['seconds']}s", flush=True)
        if status != "SUCCESS":
            print("Failure log:" if KEEP_LOGS else "Failure output was written to the terminal:",
                  dest / "scan.log" if KEEP_LOGS else job["id"], flush=True)
    if any(results[job["id"]]["status"] != "SUCCESS" for job in jobs):
        raise SystemExit(1)


if __name__ == "__main__":
    main()
