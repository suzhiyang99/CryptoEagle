#!/usr/bin/env python3
"""Construct slice collections per truth case. Multi-file provenance is one slice input."""
import argparse
import json
import os
import shutil
import sys
import traceback
from collections import Counter, defaultdict
from pathlib import Path

from normalization_helpers import protected_extractor

HERE = Path(__file__).resolve().parents[1]
ROOT = HERE.parents[1]
sys.path.insert(0, str(ROOT / "external-tools/cryptollm"))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--arm", choices=["baseline", "cacpg"], required=True)
    args = ap.parse_args()
    manifest = json.loads((HERE / "inputs/cases.json").read_text())
    out = HERE / "out" / args.arm
    work = out / "work"
    cwd = out / "cwd"
    for p in (work, cwd):
        if p.exists():
            shutil.rmtree(p)
        p.mkdir(parents=True)
    os.chdir(cwd)
    by_file = defaultdict(list)
    if args.arm == "baseline":
        from extract_crypto_line import findCryptoLine
        from slicing import slicingCode
    else:
        import normalization as nm
        operations = json.loads((HERE / "out/operations.json").read_text())
        for op in operations:
            by_file[Path(op["srcFile"]).name].append(op)
        original_extractor = nm.extractFeature
        original_make_graph = nm.makeGraph
        def checked_graph(*arguments):
            graph = original_make_graph(*arguments)
            if graph is False:
                raise RuntimeError("COMEX normalization failed; raw-code fallback is not accepted in this run")
            return graph
        nm.makeGraph = checked_graph
    results = {}
    for index, case in enumerate(manifest["cases"], 1):
        cid = case["case_id"]
        record = {"label": case["label"], "vuln_type": case["vuln_type"], "source_status": case["source_status"],
                  "files": [f["name"] for f in case["files"]], "slices": [], "file_status": {}, "errors": []}
        source_map = {f["name"]: HERE / f["path"] for f in case["files"]}
        for file in case["files"]:
            name = file["name"]
            wd = work / cid / Path(name).stem
            wd.mkdir(parents=True)
            try:
                if args.arm == "baseline":
                    anchors, preprocessed = findCryptoLine(str(source_map[name]), str(wd))
                    if not anchors:
                        record["file_status"][name] = "no_anchor"
                        continue
                    nzdir = slicingCode(name, str(wd), preprocessed, anchors, None)
                    if nzdir is False:
                        record["file_status"][name] = "slice_failed"
                        continue
                    for path in sorted(Path(nzdir).iterdir()):
                        if path.is_file() and path.read_text(errors="replace").strip():
                            record["slices"].append({"file": name, "name": path.name, "code": path.read_text(errors="replace")})
                    record["file_status"][name] = "ok"
                else:
                    own_lines = source_map[name].read_text(errors="replace").splitlines(keepends=True)
                    seen = set()
                    for op in by_file[name]:
                        declarations = {d["line"]: d["replacement"] for d in op.get("declarationProjections", [])}
                        own = sorted({n for n in op["allLines"] + list(declarations) if 1 <= n <= len(own_lines)})
                        if not own:
                            continue
                        replacements = {x["definitionLine"]: x["replacement"] for x in op.get("randomOverwrites", []) if x["writerLine"] in own}
                        replacements.update(declarations)
                        parts, refs = [], {}
                        exported = op.get("sourceFiles") or [{"file": op["srcFile"], "lines": own}]
                        for section in sorted(exported, key=lambda x: Path(x["file"]).name):
                            other = Path(section["file"]).name
                            assert other in source_map, "Cross-case provenance leakage: " + other
                            source = source_map[other].read_text(errors="replace").splitlines(keepends=True)
                            lines = own if other == name else section["lines"]
                            valid = sorted({n for n in lines if 1 <= n <= len(source)})
                            if not valid:
                                continue
                            refs[other] = valid
                            # Code is kept in original per-file line order. Never insert unrelated source.
                            parts.append("".join((replacements[n] + "\n") if other == name and n in replacements else source[n-1] for n in valid))
                        raw = "\n".join(parts)
                        signature = (raw, tuple((n, tuple(v)) for n, v in sorted(refs.items())))
                        if signature in seen:
                            continue
                        seen.add(signature)
                        tag = "%s_%d" % (op["lineNo"], len(record["slices"]))
                        snippet = wd / "code_snippet" / name / (name + "_" + tag + ".java")
                        snippet.parent.mkdir(parents=True, exist_ok=True)
                        snippet.write_text(raw)
                        nm.extractFeature = protected_extractor(original_extractor, op.get("apiTypes", []))
                        try:
                            nz = Path(nm.normalization(name, str(wd), str(snippet), tag)) / snippet.name
                        finally:
                            nm.extractFeature = original_extractor
                        assert nz.exists(), "Normalization output missing"
                        record["slices"].append({"file": name, "name": snippet.name, "code": nz.read_text(), "raw_code": raw,
                                                  "source_lines": refs, "operation_id": op["operationId"]})
                    record["file_status"][name] = "ok" if by_file[name] else "no_operation"
            except Exception:
                record["errors"].append({"file": name, "error": traceback.format_exc()})
                record["file_status"][name] = "error"
        record["status"] = "ok" if record["slices"] else ("missing_source" if not source_map else "no_slice")
        results[cid] = record
        if index % 10 == 0 or index == len(manifest["cases"]):
            (out / "slices.json").write_text(json.dumps({"arm": args.arm, "unit": "ground-truth case", "results": results}, indent=2))
            print(args.arm, index, "/", len(manifest["cases"]), dict(Counter(r["status"] for r in results.values())), flush=True)
    errors = sum(len(r["errors"]) for r in results.values())
    print("errors", errors, flush=True)
    if not errors and not any(status == "slice_failed" for r in results.values()
                              for status in r["file_status"].values()):
        os.chdir(HERE)
        for path in (work, cwd):
            shutil.rmtree(path)


if __name__ == "__main__":
    main()
