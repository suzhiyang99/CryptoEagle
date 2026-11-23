package org.apache.spark.deploy;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple5;
import scala.runtime.AbstractFunction5;
import scala.runtime.BoxesRunTime;

public final class DriverDescription$ extends AbstractFunction5<String, Object, Object, Object, Command, DriverDescription> implements Serializable {
   public static final DriverDescription$ MODULE$;

   static {
      new DriverDescription$();
   }

   public final String toString() {
      return "DriverDescription";
   }

   public DriverDescription apply(String jarUrl, int mem, int cores, boolean supervise, Command command) {
      return new DriverDescription(jarUrl, mem, cores, supervise, command);
   }

   public Option<Tuple5<String, Object, Object, Object, Command>> unapply(DriverDescription x$0) {
      return (Option<Tuple5<String, Object, Object, Object, Command>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple5(x$0.jarUrl(), BoxesRunTime.boxToInteger(x$0.mem()), BoxesRunTime.boxToInteger(x$0.cores()), BoxesRunTime.boxToBoolean(x$0.supervise()), x$0.command())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private DriverDescription$() {
      MODULE$ = this;
   }
}
