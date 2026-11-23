#!/usr/bin/env bash
# Runs the four Python baselines of the PyCryptoBench paper (Table II) on all 1,836 PyCryptoBench files, then scores
# them together with CryptoEagle at file level (score.py).
#   Bandit 1.9.4, Dlint 0.17.0, Cryptolation 0.1.0   external-tools/python-baselines (install.sh builds envs/)
#   Semgrep 1.165.0                                     external-tools/semgrep (install.sh builds env/), with the rule
#                                                       snapshots external-tools/semgrep/rules/p-python.yaml and p-security-audit.yaml
# Cryptolation's console script points to a module the package does not ship (src.cryptoguard4py), so
# src.cryptolation.main is called directly, on a copy of the sources in a temporary directory.
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ARTIFACT="$(cd "$HERE/../.." && pwd)"
source "$ARTIFACT/scripts/experiment_logging.sh"
BENCH="$ARTIFACT/benchmarks/PyCryptoBench"
PY="$ARTIFACT/external-tools/python-baselines"; SEMGREP="$ARTIFACT/external-tools/semgrep"
RAW="$HERE/raw"
mkdir -p "$RAW"
experiment_run install bash "$PY/install.sh"
experiment_run install bash "$SEMGREP/install.sh"

experiment_run bandit "$PY/envs/bandit/bin/bandit" -r "$BENCH/source" -f json -o "$RAW/bandit.json" -q  || true
experiment_stderr dlint "$PY/envs/dlint/bin/flake8" --select=DUO --format='%(path)s|%(row)d|%(code)s|%(text)s' "$BENCH/source" \
  >"$RAW/dlint.txt"  || true
# --no-git-ignore: inside a git work tree Semgrep would otherwise scan only tracked files.
# Run from the rules directory so that rule ids carry no path prefix (they match the registry ids).
(cd "$SEMGREP/rules" && experiment_run semgrep-audit "$SEMGREP/env/bin/semgrep" scan --config p-security-audit.yaml --config p-python.yaml \
  --metrics off --no-git-ignore --json --quiet -o "$RAW/semgrep-audit.json" "$BENCH/source")  || true

COPY="$(mktemp -d)"
cp -R "$BENCH/source" "$COPY/source"
# Cryptolation iterates over Python sets/dicts, so its output depends on the hash seed (125 or 127 alerts over seeds
# 0-7); a fixed seed makes reruns repeatable.
(cd "$COPY" && experiment_run cryptolation env PYTHONHASHSEED=0 "$PY/envs/cryptolation/bin/python" -c \
  "import sys; sys.argv=['cryptolation','-s','source','-o','cryptolation.csv']; from src import cryptolation; sys.exit(cryptolation.main(sys.argv))")
cp "$COPY/cryptolation.csv" "$RAW/cryptolation.csv"
rm -rf "$COPY"

python3 "$HERE/score.py"
