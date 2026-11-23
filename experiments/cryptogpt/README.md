# CryptoGPT evaluation

An alert matches a CryptoGPT report instance when it matches the expected rule and an audited API location. Java row 7 uses the Elasticsearch production locations confirmed by the upstream fix. Both calls count as one report instance.

[summary.csv](tables/summary.csv) matches Table 5. Java has **28 TP, 0 FP, and 4 FN**. Python has **21 TP, 1 FP, 6 FN, and 2 TN**. See [cases.csv](tables/cases.csv) for verdicts and reasons, and [alerts.csv](tables/alerts.csv) for individual reviewed alerts.

From the package root, rebuild the tables from the archived scans and audit notes:

```sh
python3 experiments/cryptogpt/scripts/build_tables.py
```

Fresh scans use `bash experiments/cryptogpt/run.sh` and require the source snapshots at the paths in [scope-manifest.json](inputs/scope-manifest.json). The script supports `--resume` and `--scope SCOPE_ID`. Review changed alerts against the corresponding snapshot before updating the audit notes.
