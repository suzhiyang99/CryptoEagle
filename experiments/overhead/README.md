# CA-CPG construction overhead

RQ3 measures nine ApacheCryptoAPI-Bench projects, with three fresh JVM runs per project. Overhead is enrichment time divided by standard CPG construction time. The median of the nine project medians is **13.6%**. Rule evaluation is timed separately.

See [runs.csv](tables/runs.csv) for the 27 runs, [summary.csv](tables/summary.csv) for the project medians, and [Table 10](../summary/tables/table-rq3.csv) for the paper results.

From the package root, verify the saved report hashes and rebuild the timing tables:

```sh
python3 experiments/overhead/build_tables.py --check-archive
```

Run `bash experiments/overhead/run.sh` in a separate copy to collect new timings. A fresh run overwrites the archived reports in that copy and can produce different timings.
