# CryptoEagle

CryptoEagle detects cryptographic API misuse in Java and Python. It uses Joern to build a cryptography-aware code property graph (CA-CPG) and checks it with the 18 CryptoQL rules (C1–C18).

## Run CryptoEagle

Install JDK 21 and sbt. The first build downloads the required libraries. From the package root, run:

```sh
cd tool/crypto-ext
sbt compile
```

See the [tool README](tool/crypto-ext/README.md) for the Java and Python analysis commands.

## Benchmarks and results

The package includes [CryptoAPI-Bench](benchmarks/CryptoAPI-Bench/), [PyCryptoBench](benchmarks/PyCryptoBench/), and [ApacheCryptoAPI-Bench](benchmarks/ApacheCryptoAPI-Bench/). See the [nine paper tables](experiments/summary/tables/) and their [per-case results](experiments/summary/cases/).

With Python and `openpyxl` installed, run these commands from the package root to rescore the saved benchmark alerts and regenerate the paper tables:

```sh
python3 experiments/benchmarks/build_tables.py
python3 experiments/summary/paper_tables.py
```

For a fresh CryptoEagle benchmark scan, run `bash experiments/benchmarks/run.sh`. See the [experiment guide](experiments/README.md) for the remaining experiments. Full reruns of comparisons, CryptoGPT scans, and CryptoLLM inference require their external tools, project snapshots, or model checkpoint.
