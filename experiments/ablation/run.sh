#!/usr/bin/env bash
# Runs the three ablation variants of crypto-ext on CryptoAPI-Bench (Java):
#   full           the complete tool (C1-C18)
#   no-provenance  CryptoOperation nodes and ontology; values only from definitions in the same method
#   string-match   standard CPG, no enrichment; string patterns over call text (rules/StringMatchBaseline.scala)
# EXT_DIR and CRYPTOAPI_BENCH override the detected tool and benchmark locations.
# CRYPTOAPI_XLSX optionally overrides the labels used for scoring.
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ARTIFACT="$(cd "$HERE/../.." && pwd)"
source "$ARTIFACT/scripts/experiment_logging.sh"
pick() { local m=$1; shift; for c in "$@"; do [ -e "$c/$m" ] && { echo "$c"; return; }; done; echo "$1"; }
EXT="${EXT_DIR:-$(pick build.sbt "$HERE/../../crypto-ext" "$ARTIFACT/tool/crypto-ext")}"
CRYPTOAPI="${CRYPTOAPI_BENCH:-$(pick CryptoAPI-Bench_details.xlsx "$HERE/../../CryptoAPI-Bench-master" "$ARTIFACT/benchmarks/CryptoAPI-Bench")}"
JAVA="$(experiment_java)"
CP="$(cd "$EXT" && sbt -error compile "export Runtime / fullClasspath" | tail -1)"
for variant in string-match no-provenance full; do
  out="$HERE/raw/$variant"; mkdir -p "$out" "$HERE/metrics/$variant"; start=$(date +%s)
  (cd "$EXT" && experiment_run "$variant" "$JAVA" -Xmx8g -Xss1g -cp "$CP" org.codeminers.standalone.EvaluationMain java "$CRYPTOAPI" \
      --ablation="$variant" --flag-legacy-hmac-sha1 \
      --metrics-output="$HERE/metrics/$variant/perf-metrics.json" --report-output="$out/findings.json")
  echo "$variant rc=$? $(( $(date +%s) - start ))s"
done
EXT_DIR="$EXT" CRYPTOAPI_XLSX="${CRYPTOAPI_XLSX:-$CRYPTOAPI/CryptoAPI-Bench_details.xlsx}" python3 "$HERE/build_tables.py"
