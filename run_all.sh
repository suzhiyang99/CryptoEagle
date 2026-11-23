#!/usr/bin/env bash
# Reproduces every experiment of the paper from this directory, in dependency order. Each step is
# experiments/<name>/run.sh and can also be run on its own; see README.md for requirements and run times.
#   bash run_all.sh                       all experiments
#   bash run_all.sh benchmarks ablation   only the named ones
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ORDER=(benchmarks ablation cryptogpt cryptollm-cases python-baselines tool-comparison overhead summary)
STEPS=("$@"); [ ${#STEPS[@]} -eq 0 ] && STEPS=("${ORDER[@]}")
for step in "${STEPS[@]}"; do
  [ -x "$HERE/experiments/$step/run.sh" ] || { echo "unknown experiment: $step (choose from: ${ORDER[*]})" >&2; exit 2; }
  echo "=== $step"
  start=$(date +%s)
  bash "$HERE/experiments/$step/run.sh"
  echo "=== $step done in $(( $(date +%s) - start ))s"
done
