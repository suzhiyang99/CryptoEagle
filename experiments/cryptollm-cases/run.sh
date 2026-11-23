#!/usr/bin/env bash
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ARTIFACT="$(cd "$HERE/../.." && pwd)"
source "$ARTIFACT/scripts/experiment_logging.sh"
EXT="$ARTIFACT/tool/crypto-ext"
CRYPTOLLM_ENV="${CRYPTOLLM_ENV:-$ARTIFACT/external-tools/cryptollm/env}"
export PATH="$CRYPTOLLM_ENV/bin:$PATH"
export HF_HUB_OFFLINE=1 TRANSFORMERS_OFFLINE=1
experiment_run parsers python -c 'from comex import get_language_map; get_language_map()'
mkdir -p "$HERE/out"
experiment_run cases python "$HERE/scripts/01_cases.py"
CP="$(cd "$EXT" && sbt -error compile "export Runtime / fullClasspath" | tail -1)"
JAVA="$(experiment_java)"
(cd "$EXT" && experiment_run export "$JAVA" -Xmx8g -Xss16m -cp "$CP" org.codeminers.standalone.SliceExportMain \
  "$HERE/inputs/sources" --case-groups="$HERE/inputs/case-files.tsv" --out="$HERE/out/operations.json" \
  --verification-context="${VERIFICATION_CONTEXT:-true}" \
  --dependency-context="${DEPENDENCY_CONTEXT:-true}" \
  --effective-overwrite-context="${EFFECTIVE_OVERWRITE_CONTEXT:-true}")
experiment_run baseline python "$HERE/scripts/02_slices.py" --arm baseline
experiment_run cacpg python "$HERE/scripts/02_slices.py" --arm cacpg
experiment_run predict python "$HERE/scripts/03_predict.py"
python "$HERE/scripts/04_evaluate.py"
