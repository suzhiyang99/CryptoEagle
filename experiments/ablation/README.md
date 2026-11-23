# Ablation study

The three variants are string matching on the standard CPG, the ontology with same-method definitions, and full CryptoEagle with provenance tracking. They share the 173 scored CryptoAPI-Bench cases and the same rule policy.

[summary.csv](tables/summary.csv) matches Table 7. The three F1 scores are **0.365**, **0.440**, and **0.974**. [cases.csv](tables/cases.csv) retains the verdicts used to calculate the scores and analyze gains by case type.

From the package root, rebuild the tables from saved findings:

```sh
python3 experiments/ablation/build_tables.py
```

Run `bash experiments/ablation/run.sh` to rescan all three variants. Raw findings go to [raw/](raw/) and scan timings to [metrics/](metrics/).
