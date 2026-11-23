#!/usr/bin/env bash
# Rebuild paper tables from saved results. Set PAPER_SOURCE to also check the LaTeX tables.
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
if [ "$#" -eq 1 ] && [ "$1" = "--apache-only" ]; then
  python3 "$HERE/../tool-comparison/score_apache.py"
  python3 "$HERE/build_unified.py" --apache-only
  python3 "$HERE/compare_tools.py" --apache-only
  python3 "$HERE/paper_tables.py" --apache-only
  exit 0
fi
if [ "$#" -eq 1 ] && [ "$1" = "--rebuild-archive" ]; then
  python3 "$HERE/../overhead/build_tables.py" --check-archive
  python3 "$HERE/../benchmarks/build_tables.py"
  python3 "$HERE/../ablation/build_tables.py"
  python3 "$HERE/../cryptogpt/scripts/build_tables.py"
  python3 "$HERE/../cryptollm-cases/scripts/04_evaluate.py"
  python3 "$HERE/../python-baselines/score.py"
  python3 "$HERE/../tool-comparison/score_apache.py"
elif [ "$#" -ne 0 ]; then
  echo "Usage: $0 [--apache-only|--rebuild-archive]" >&2
  exit 2
fi
python3 "$HERE/build_unified.py"
python3 "$HERE/compare_tools.py"
python3 "$HERE/paper_tables.py"
if [ -n "${PAPER_SOURCE:-}" ]; then
  python3 "$HERE/paper_tables.py" --check-paper --paper-source "$PAPER_SOURCE"
fi
python3 "$HERE/../cryptollm-cases/scripts/05_validate.py"
