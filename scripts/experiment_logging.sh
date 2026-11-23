# Source from a runner that sets HERE to its experiment directory.
# With KEEP_LOGS=1, append diagnostics to $HERE/logs/<name>.log.
# experiment_run sends both streams to the log or to the caller's stderr.
experiment_run() {
  local name="${1//\//_}"
  shift
  if [ "${KEEP_LOGS:-0}" = 1 ]; then
    mkdir -p "$HERE/logs"
    "$@" >>"$HERE/logs/$name.log" 2>&1
  else
    "$@" >&2
  fi
}

# Log stderr only when enabled. The caller can redirect stdout as report data,
# as the Python baseline runner does for Dlint.
experiment_stderr() {
  local name="${1//\//_}"
  shift
  if [ "${KEEP_LOGS:-0}" = 1 ]; then
    mkdir -p "$HERE/logs"
    "$@" 2>>"$HERE/logs/$name.log"
  else
    "$@"
  fi
}

# Use the configured JVM or discover Java 21 on macOS.
experiment_java() {
  if [ -n "${JAVA:-}" ]; then
    printf '%s\n' "$JAVA"
  elif [ -n "${JAVA_HOME:-}" ]; then
    printf '%s\n' "$JAVA_HOME/bin/java"
  elif [ -x /usr/libexec/java_home ]; then
    local java_home
    java_home="$(/usr/libexec/java_home -v 21)" || return
    printf '%s\n' "$java_home/bin/java"
  else
    command -v java
  fi
}
