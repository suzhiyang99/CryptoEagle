#!/usr/bin/env bash
# Measures CA-CPG construction overhead (paper RQ3, Table "Runtime of standard CPG vs. CA-CPG construction").
# Each of the 9 runnable ApacheCryptoAPI-Bench projects is analysed RUNS times (default 3), every run in a fresh JVM,
# one at a time.
# EvaluationMain records the standard CPG time (frontend, overlays, data flow), the enrichment pass and the rules
# separately in raw/<input>/run<N>.json; build_tables.py takes the median per input.
#   bash run.sh
# EXT_DIR, APACHE_BENCH and RUNS override the defaults.
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ARTIFACT="$(cd "$HERE/../.." && pwd)"
source "$ARTIFACT/scripts/experiment_logging.sh"
pick() { local m=$1; shift; for c in "$@"; do [ -e "$c/$m" ] && { echo "$c"; return; }; done; echo "$1"; }
EXT="${EXT_DIR:-$(pick build.sbt "$HERE/../../crypto-ext" "$ARTIFACT/tool/crypto-ext")}"
APACHE="${APACHE_BENCH:-$(pick ApacheCryptoAPI-Bench_detail_list.xlsx "$ARTIFACT/benchmarks/ApacheCryptoAPI-Bench" "$HERE/../../ApacheCryptoAPI-Bench-main")}/decompiled"
JAVA="$(experiment_java)"
RUNS="${RUNS:-3}"
PROJECTS=(apacheds-kerberos-codec-2.0.0-M25-SNAPSHOT artemis-commons-2.5.0-SNAPSHOT deltaspike-core-impl-1.8.1-SNAPSHOT
          mcf-core-2.9-SNAPSHOT meecrowave-core-1.2.0-SNAPSHOT openejb-core-7.0.5-SNAPSHOT
          taverna-perspective-myexperiment-3.1.0-incubating-SNAPSHOT tika-core-1.17-SNAPSHOT wicket-util-8.0.0-SNAPSHOT)
CP="$(cd "$EXT" && sbt -error compile "export Runtime / fullClasspath" | tail -1)"

measure() { # name lang input run
  local name=$1 lang=$2 input=$3 run=$4 out="$HERE/raw/$1"
  mkdir -p "$out"
  (cd "$EXT" && experiment_run "$name-run$run" "$JAVA" -Xmx8g -Xss1g -cp "$CP" org.codeminers.standalone.EvaluationMain "$lang" "$input" \
      --metrics-output="$out/run$run.json" --report-output="$out/findings$run.json")
  echo "run $run $name rc=$?"
}

for run in $(seq 1 "$RUNS"); do
  for p in "${PROJECTS[@]}"; do measure "apache/$p" java "$APACHE/$p" "$run"; done
done
python3 "$HERE/build_tables.py"
