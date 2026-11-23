#!/usr/bin/env python3
"""Same frozen model and ANY-slice decision for BOTH arms; exactly one decision per case."""
import hashlib
import json
import sys
from pathlib import Path

import frozen_model as inference

HERE = Path(__file__).resolve().parents[1]
ROOT = HERE.parents[1]


def main():
    import torch
    device = torch.device("cpu")
    model, tokenizer = inference.load_model(device)
    result = {}
    expected = {c["case_id"] for c in json.loads((HERE / "inputs/cases.json").read_text())["cases"]}
    for arm in ("baseline", "cacpg"):
        data = json.loads((HERE / "out" / arm / "slices.json").read_text())
        assert set(data["results"]) == expected, "Slicing is incomplete"
        assert not any(r["errors"] for r in data["results"].values()), "Fix slice errors before inference"
        assert not any(status == "slice_failed" for r in data["results"].values()
                       for status in r["file_status"].values()), "Fix graph construction failures before inference"
        predictions = {}
        for index, (cid, record) in enumerate(data["results"].items(), 1):
            codes = [s["code"] for s in record["slices"]]
            votes = inference.predict(codes, model, tokenizer, device) if codes else []
            lengths = [len(tokenizer.encode(" ".join(c.split()), truncation=False)) for c in codes]
            predictions[cid] = {"slice_preds": votes, "n_slices": len(votes), "token_lens": lengths,
                                "predicted_misuse": any(v == 1 for v in votes), "aggregation": "any"}
            if index % 25 == 0:
                print(arm, index, "/", len(expected), flush=True)
        result[arm] = {"predictions": predictions}
        print(arm, len(predictions), "cases;", sum(p["n_slices"] for p in predictions.values()), "slices", flush=True)
    (HERE / "out/predictions.json").write_text(json.dumps(result, indent=2))
    model_path = ROOT / "external-tools/cryptollm/CryptoLLM_codet5.bin"
    (HERE / "out/model.json").write_text(json.dumps({"model": str(model_path.relative_to(ROOT)),
        "sha256": hashlib.sha256(model_path.read_bytes()).hexdigest(), "retrained": False,
        "aggregation": "any", "prediction_unit": "ground-truth case"}, indent=2))


if __name__ == "__main__":
    main()
