# Python baseline comparison

This experiment compares CryptoEagle, Semgrep, Bandit, Dlint, and Cryptolation on all **1,836 PyCryptoBench files**. A file counts as flagged when it contains a warning from a rule that targets a benchmark category.

The [per-file verdicts](tables/files.csv) and [tool summary](tables/summary.csv) support [Table 9](../summary/tables/table-pycryptobench.csv). CryptoEagle has **133 TP, 0 FP, 95 FN**, and **F1 0.737** under this protocol. Its rule-matched F1 of 0.960 uses the 1,547 files with a mapped CryptoQL rule.

From the package root, rebuild the scores from saved reports:

```sh
python3 experiments/python-baselines/score.py
```

Fresh baseline scans use `bash experiments/python-baselines/run.sh`. The script expects Bandit 1.9.4, Dlint 0.17.0, and Cryptolation 0.1.0 in the environments under `external-tools/python-baselines/envs/`, and Semgrep 1.165.0 with its rule snapshots under `external-tools/semgrep/`.
