package org.codeminers.standalone.enrichment

/** Opt-in stage timings for local profiling; disabled in ordinary measurements. */
private[enrichment] object OperationModelTiming {
  def measure[A](name: String)(body: => A): A = {
    if (!java.lang.Boolean.getBoolean("crypto.profile")) body
    else {
      val start = System.nanoTime()
      try body finally System.err.println(f"crypto.profile $name ${(System.nanoTime() - start) / 1e6}%.3f ms")
    }
  }
}
