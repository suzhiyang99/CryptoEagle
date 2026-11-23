package org.apache.spark.deploy;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class OptionAssigner$ extends AbstractFunction5<String, Object, Object, String, String, OptionAssigner> implements Serializable {
   public static final OptionAssigner$ MODULE$;

   static {
      new OptionAssigner$();
   }

   public final String toString() {
      return "OptionAssigner";
   }

   public OptionAssigner apply(String value, int clusterManager, int deployMode, String clOption, String confKey) {
      return new OptionAssigner(value, clusterManager, deployMode, clOption, confKey);
   }

   public Option<Tuple5<String, Object, Object, String, String>> unapply(OptionAssigner x$0) {
      return (Option<Tuple5<String, Object, Object, String, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(x$0.value(), BoxesRunTime.boxToInteger(x$0.clusterManager()), BoxesRunTime.boxToInteger(x$0.deployMode()), x$0.clOption(), x$0.confKey())));
   }

   public String apply$default$4() {
      return null;
   }

   public String apply$default$5() {
      return null;
   }

   public String $lessinit$greater$default$4() {
      return null;
   }

   public String $lessinit$greater$default$5() {
      return null;
   }

   private Object readResolve() {
      return MODULE$;
   }

   private OptionAssigner$() {
      MODULE$ = this;
   }
}
