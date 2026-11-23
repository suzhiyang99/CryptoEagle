# Benchmark detection

CryptoEagle is evaluated on the three bundled [benchmarks](../../benchmarks/). A detection is matched to the expected CryptoQL rule. Apache cases with conflicting labels in the same file and rule are matched by call site.

| Dataset | Scored cases | TP | FP | FN | TN | F1 |
|---|---:|---:|---:|---:|---:|---:|
| CryptoAPI-Bench | 173 | 132 | 2 | 5 | 34 | 0.974 |
| PyCryptoBench | 1,547 | 132 | 0 | 11 | 1,404 | 0.960 |
| ApacheCryptoAPI-Bench | 94 | 51 | 1 | 0 | 42 | 0.990 |

The [tables](tables/) contain per-case verdicts, audit reasons, and alert matches. [summary.csv](tables/summary.csv) contains the CryptoAPI-Bench per-rule results used in the paper. The PyCryptoBench comparison on all 1,836 files uses the [file-level protocol](../python-baselines/).

From the package root, rescore the archived reports with `openpyxl` installed:

```sh
python3 experiments/benchmarks/build_tables.py
```

For a fresh CryptoEagle scan, run `bash experiments/benchmarks/run.sh`. Both commands accept `--apache-only`. The Java CryptoAPI-Bench scan enables `--flag-legacy-hmac-sha1`; the scorer applies the workbook row 176 filename correction.
