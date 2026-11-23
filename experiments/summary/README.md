# Paper result tables

[tables/](tables/) contains Tables 3–11 with the paper's row order and rounding. [cases/](cases/) contains the underlying per-case verdicts.

| Research question | Tables |
|---|---|
| RQ1 | Java benchmark and CryptoGPT results, per-rule detection, and ablation |
| RQ2 | Rule applicability by language and the PyCryptoBench comparison |
| RQ3 | Runtime overhead on nine Apache projects |
| RQ4 | Frozen CryptoLLM results on 181 cases |

From the package root, build all nine tables from the saved verdicts and experiment summaries:

```sh
python3 experiments/summary/paper_tables.py
```

To re-score saved Apache baseline reports and rebuild the Apache case comparisons and paper table, run `bash experiments/summary/run.sh --apache-only`. For fresh scans or rescoring other archived outputs, use the commands in the [individual experiment guides](../README.md).

Run the complete archive rebuild with Python and openpyxl installed:

```sh
bash experiments/summary/run.sh --rebuild-archive
```

Set PAPER_SOURCE to the path to evaluation.tex to also compare the tables with the manuscript. Archived RQ4 validation checks the model hash when the external checkpoint is present.
