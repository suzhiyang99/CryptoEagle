# Experiments

The [paper tables](summary/tables/) and [per-case results](summary/cases/) are included. Each experiment below describes its inputs, outputs, and how to rebuild its tables from archived results.

| Experiment | What it evaluates |
|---|---|
| [Benchmarks](benchmarks/) | CryptoEagle on CryptoAPI-Bench, PyCryptoBench, and ApacheCryptoAPI-Bench |
| [Ablation](ablation/) | The effects of adding the ontology and provenance tracking on CryptoAPI-Bench |
| [CryptoGPT](cryptogpt/) | Detection of reported misuses in Java and Python projects |
| [CryptoLLM](cryptollm-cases/) | The original and CA-CPG slicing front ends with the same frozen classifier |
| [Runtime overhead](overhead/) | Additional time to construct the CA-CPG |
| [Python baselines](python-baselines/) | Comparison on PyCryptoBench |
| [Java baselines](tool-comparison/) | Baseline reports and comparisons on the Java benchmarks |
| [Paper tables](summary/) | Generation of Tables 3–11 |

Run commands from the package root. Archive scoring uses Python 3 and, for workflows that read the benchmark workbooks, `openpyxl`. Fresh CryptoEagle scans use JDK 21 and sbt.
