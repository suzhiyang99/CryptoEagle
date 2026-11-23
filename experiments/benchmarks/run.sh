#!/usr/bin/env bash
# Runs the current crypto-ext (C1-C18) on CryptoAPI-Bench, PyCryptoBench and the 9 runnable
# ApacheCryptoAPI-Bench projects. Uses the runtime classpath exported by sbt in memory.
# With --apache-only, refreshes only Apache scans and benchmark tables.
# EXT_DIR, CRYPTOAPI_BENCH, PYCRYPTOBENCH and APACHE_BENCH override the detected locations.
set -euo pipefail
case "${1:-}" in
  ""|--apache-only) ;;
  *) echo "Usage: $0 [--apache-only]" >&2; exit 2 ;;
esac
if [ "$#" -gt 1 ]; then echo "Usage: $0 [--apache-only]" >&2; exit 2; fi
HERE="$(cd "$(dirname "$0")" && pwd)"
ARTIFACT="$(cd "$HERE/../.." && pwd)"
source "$ARTIFACT/scripts/experiment_logging.sh"
pick() { # marker candidate... -> first candidate containing marker (case-insensitive file systems need the marker)
  local m=$1; shift; for c in "$@"; do [ -e "$c/$m" ] && { echo "$c"; return; }; done; echo "$1"; }
EXT="${EXT_DIR:-$(pick build.sbt "$HERE/../../crypto-ext" "$ARTIFACT/tool/crypto-ext")}"
CRYPTOAPI="${CRYPTOAPI_BENCH:-$(pick CryptoAPI-Bench_details.xlsx "$HERE/../../CryptoAPI-Bench-master" "$ARTIFACT/benchmarks/CryptoAPI-Bench")}"
PYCRYPTO="${PYCRYPTOBENCH:-$(pick metadata.jsonl "$HERE/../../PyCryptoBench" "$ARTIFACT/benchmarks/PyCryptoBench")}"
APACHE="${APACHE_BENCH:-$(pick ApacheCryptoAPI-Bench_detail_list.xlsx "$ARTIFACT/benchmarks/ApacheCryptoAPI-Bench" "$HERE/../../ApacheCryptoAPI-Bench-main")}/decompiled"
JAVA="$(experiment_java)"
RAW="$HERE/raw"
CP="$(cd "$EXT" && sbt -error compile "export Runtime / fullClasspath" | tail -1)"

run() { # name lang input [scan options]
  local name=$1 lang=$2 input=$3 start
  shift 3
  mkdir -p "$RAW/$name" "$HERE/metrics/$name"
  start=$(date +%s)
  (cd "$EXT" && experiment_run "$name" "$JAVA" -Xmx8g -Xss1g -cp "$CP" org.codeminers.standalone.EvaluationMain "$lang" "$input" \
      --metrics-output="$HERE/metrics/$name/perf-metrics.json" --report-output="$RAW/$name/findings.json" "$@")
  (cd "$EXT" && experiment_run "$name" "$JAVA" -Xmx8g -Xss1g -cp "$CP" org.codeminers.standalone.EvaluationMain "$lang" "$input" \
      --metrics-output="$HERE/metrics/$name/perf-metrics-csv-pass.json" --report-output="$RAW/$name/findings.csv" "$@")
  echo "$name $(( $(date +%s) - start ))s"
}

if [ "$#" -eq 0 ]; then
  run cryptoapi-bench java "$CRYPTOAPI" --flag-legacy-hmac-sha1
  run pycryptobench python "$PYCRYPTO"
fi
for p in apacheds-kerberos-codec-2.0.0-M25-SNAPSHOT artemis-commons-2.5.0-SNAPSHOT deltaspike-core-impl-1.8.1-SNAPSHOT \
         mcf-core-2.9-SNAPSHOT meecrowave-core-1.2.0-SNAPSHOT openejb-core-7.0.5-SNAPSHOT \
         taverna-perspective-myexperiment-3.1.0-incubating-SNAPSHOT tika-core-1.17-SNAPSHOT wicket-util-8.0.0-SNAPSHOT; do
  run "apache/$p" java "$APACHE/$p"
done
python3 "$HERE/make_audit_notes.py"
python3 "$HERE/build_tables.py" "$@"
