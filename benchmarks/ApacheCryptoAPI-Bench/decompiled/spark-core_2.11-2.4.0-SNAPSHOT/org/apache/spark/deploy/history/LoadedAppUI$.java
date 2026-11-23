package org.apache.spark.deploy.history;

import org.apache.spark.ui.SparkUI;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.runtime.AbstractFunction1;

public final class LoadedAppUI$ extends AbstractFunction1<SparkUI, LoadedAppUI> implements Serializable {
   public static final LoadedAppUI$ MODULE$;

   static {
      new LoadedAppUI$();
   }

   public final String toString() {
      return "LoadedAppUI";
   }

   public LoadedAppUI apply(SparkUI ui) {
      return new LoadedAppUI(ui);
   }

   public Option<SparkUI> unapply(LoadedAppUI x$0) {
      return (Option<SparkUI>)(x$0 == null ? None$.MODULE$ : new Some(x$0.ui()));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private LoadedAppUI$() {
      MODULE$ = this;
   }
}
