# Java baseline comparison

Archived comparison reports cover CryptoAPI-Bench and the nine scored Apache projects. Apache uses CryptoGuard and CogniCrypt. The scorer maps native warnings to CryptoQL categories and matches them by outer class and rule.

See the [raw reports](raw/), [Apache baseline verdicts](inputs/apache-baseline-verdicts.csv), and [Tables 3](../summary/tables/table-cryptoapi-bench.csv) and [4](../summary/tables/table-apache.csv).

From the package root, rescore the saved Apache reports and rebuild Table 4:

```sh
bash experiments/summary/run.sh --apache-only
```

For fresh scans, use `bash experiments/tool-comparison/run.sh` or pass `--apache-only`. The scripts expect the Java comparison tools under `external-tools/java-baselines/`, Semgrep and its rule snapshots under `external-tools/semgrep/`, and JDK 8 and 17 for the Java baselines. The nine Apache input JARs are listed in [apache-jars.json](inputs/apache-jars.json).
