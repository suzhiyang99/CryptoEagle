#!/usr/bin/env bash
# Runs four Java baselines on CryptoAPI-Bench, then CryptoGuard/CogniCrypt on the nine scored Apache projects.
# Native reports go to raw/ (CryptoAPI-Bench) and raw/apache/ (Apache).
#   Semgrep OSS 1.165.0          rule snapshots external-tools/semgrep/rules/p-java.yaml + p-secrets.yaml   -> raw/semgrep.json
#   SpotBugs 4.9.6 + FindSecBugs  (JDK 17)                                                          -> raw/spotbugs.xml
#   CogniCrypt / CryptoAnalysis   HeadlessJavaScanner 5.0.1, JCA rules (JDK 17)                      -> raw/cognicrypt/
#   CryptoGuard                   (JDK 8)                                                           -> raw/cryptoguard.csv
# SpotBugs, CogniCrypt and CryptoGuard analyse bytecode, so the benchmark is first compiled with JDK 8 (the class
# version of the original CryptoAPI-Bench). The comparison tables are built by ../summary/compare_tools.py, which
# scores these reports and CryptoEagle (../benchmarks) on the same ground truth.
#   bash run.sh [--only semgrep,spotbugs,cognicrypt,cryptoguard]
#   bash run.sh --apache-only
# JDK8_HOME / JDK17_HOME override the JDKs (macOS: found with /usr/libexec/java_home).
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ARTIFACT="$(cd "$HERE/../.." && pwd)"
if [ "${1:-}" = "--apache-only" ]; then
  exec python3 "$HERE/run_apache.py"
fi
source "$ARTIFACT/scripts/experiment_logging.sh"
BENCH="$ARTIFACT/benchmarks/CryptoAPI-Bench"
JB="$ARTIFACT/external-tools/java-baselines"; SEMGREP="$ARTIFACT/external-tools/semgrep"
RAW="$HERE/raw"; BUILD="$HERE/build"
TOOLS="semgrep,spotbugs,cognicrypt,cryptoguard"
[ "${1:-}" = "--only" ] && TOOLS="$2"
wants() { [[ ",$TOOLS," == *",$1,"* ]]; }
jdk() { local v=$1 var=$2; [ -n "${!var:-}" ] && { echo "${!var}"; return; }; /usr/libexec/java_home -v "$v" 2>/dev/null; }
JDK8_HOME="$(jdk 1.8 JDK8_HOME)"; JDK17_HOME="$(jdk 17 JDK17_HOME)"
mkdir -p "$RAW"

if wants spotbugs || wants cognicrypt || wants cryptoguard; then
  echo "== compile CryptoAPI-Bench (JDK 8)"
  rm -rf "$BUILD"; mkdir -p "$BUILD/classes"
  find "$BENCH/src" -name '*.java' > "$BUILD/sources.txt"
  experiment_run javac "$JDK8_HOME/bin/javac" -nowarn -d "$BUILD/classes" "@$BUILD/sources.txt"
  (cd "$BUILD/classes" && "$JDK8_HOME/bin/jar" cf "$BUILD/cryptoapi-bench.jar" .)
fi
if wants semgrep; then
  echo "== Semgrep"
  experiment_run semgrep-install bash "$SEMGREP/install.sh"
  # Run from the rules directory so that rule ids carry no path prefix (they match the registry ids).
  (cd "$SEMGREP/rules" && experiment_run semgrep "$SEMGREP/env/bin/semgrep" scan --config p-java.yaml --config p-secrets.yaml \
    --json --output "$RAW/semgrep.json" --no-git-ignore --metrics=off "$BENCH/src")  || true
fi
if wants spotbugs; then
  echo "== SpotBugs + FindSecBugs (JDK 17)"
  experiment_run spotbugs env JAVA_HOME="$JDK17_HOME" "$JB/spotbugs/spotbugs-4.9.6/bin/spotbugs" -textui -pluginList "$JB/spotbugs/findsecbugs-plugin.jar" \
    -effort:max -low -xml:withMessages -output "$RAW/spotbugs.xml" "$BUILD/classes"
fi
if wants cognicrypt; then
  # HeadlessJavaScanner 5.0.1 is class file version 61 and needs JDK 17.
  echo "== CogniCrypt (JDK 17)"
  rm -rf "$RAW/cognicrypt"; mkdir -p "$RAW/cognicrypt"
  experiment_run cognicrypt "$JDK17_HOME/bin/java" -Xmx8g -jar "$JB/cognicrypt/HeadlessJavaScanner-5.0.1.jar" --appPath "$BUILD/cryptoapi-bench.jar" \
    --rulesDir "$JB/cognicrypt/rules/JavaCryptographicArchitecture" --reportPath "$RAW/cognicrypt" --reportFormat CSV
fi
if wants cryptoguard; then
  # In version 04.05.03 -m selects the output format; JAVA_HOME must be JDK 8, or Soot cannot load rt.jar/jce.jar.
  echo "== CryptoGuard (JDK 8)"
  WORK="$(mktemp -d)"
  (cd "$WORK" && experiment_run cryptoguard env JAVA_HOME="$JDK8_HOME" "$JDK8_HOME/bin/java" -Xmx8g -jar "$JB/cryptoguard/cryptoguard.jar" \
    -in jar -s "$BUILD/cryptoapi-bench.jar" -m CSV -o cryptoguard.csv -n -new)
  cp "$WORK/cryptoguard.csv" "$RAW/cryptoguard.csv"; rm -rf "$WORK"
fi
echo "raw reports in $RAW"
if wants cryptoguard && wants cognicrypt; then
  python3 "$HERE/run_apache.py"
fi
