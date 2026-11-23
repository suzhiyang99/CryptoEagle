package org.apache.spark.api.python;

import java.util.List;
import java.util.Map;
import org.apache.spark.broadcast.Broadcast;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple7;
import scala.runtime.AbstractFunction7;

public final class PythonFunction$ extends AbstractFunction7<byte[], Map<String, String>, List<String>, String, String, List<Broadcast<PythonBroadcast>>, PythonAccumulatorV2, PythonFunction> implements Serializable {
   public static final PythonFunction$ MODULE$;

   static {
      new PythonFunction$();
   }

   public final String toString() {
      return "PythonFunction";
   }

   public PythonFunction apply(byte[] command, Map<String, String> envVars, List<String> pythonIncludes, String pythonExec, String pythonVer, List<Broadcast<PythonBroadcast>> broadcastVars, PythonAccumulatorV2 accumulator) {
      return new PythonFunction(command, envVars, pythonIncludes, pythonExec, pythonVer, broadcastVars, accumulator);
   }

   public Option<Tuple7<byte[], Map<String, String>, List<String>, String, String, List<Broadcast<PythonBroadcast>>, PythonAccumulatorV2>> unapply(PythonFunction x$0) {
      return (Option<Tuple7<byte[], Map<String, String>, List<String>, String, String, List<Broadcast<PythonBroadcast>>, PythonAccumulatorV2>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple7(x$0.command(), x$0.envVars(), x$0.pythonIncludes(), x$0.pythonExec(), x$0.pythonVer(), x$0.broadcastVars(), x$0.accumulator())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private PythonFunction$() {
      MODULE$ = this;
   }
}
