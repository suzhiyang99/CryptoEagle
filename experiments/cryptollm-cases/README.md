# CryptoLLM slicing comparison

RQ4 compares the original and CA-CPG slicing front ends using the same frozen CryptoLLM CodeT5 classifier on 181 CryptoAPI-Bench cases. A case is positive when any of its slices is classified as a misuse.

[comparison.csv](out/comparison.csv) matches Table 11. F1 is **0.435** for the original front end and **0.729** for CA-CPG. See [case-results.csv](out/case-results.csv) for per-case verdicts and [predictions.json](out/predictions.json) for slice-level predictions.

From the package root, recalculate the scores from the saved predictions:

```sh
python3 experiments/cryptollm-cases/scripts/04_evaluate.py
```

Run `bash experiments/cryptollm-cases/run.sh` to rebuild slices and rerun inference. Place the CryptoLLM code and frozen checkpoint under `external-tools/cryptollm/` and prepare its Python environment and COMEX parsers. The default environment path is `external-tools/cryptollm/env` and can be overridden with `CRYPTOLLM_ENV`.
