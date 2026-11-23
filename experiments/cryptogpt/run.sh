#!/usr/bin/env bash
# Scan the 63 configured scopes, then score current findings using saved manual reviews.
#   bash run.sh [--resume] [--scope SCOPE_ID ...]
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ARTIFACT="$(cd "$HERE/../.." && pwd)"
source "$ARTIFACT/scripts/experiment_logging.sh"
experiment_run scans python3 "$HERE/scripts/run_scans.py" "$@"
python3 "$HERE/scripts/build_tables.py"
