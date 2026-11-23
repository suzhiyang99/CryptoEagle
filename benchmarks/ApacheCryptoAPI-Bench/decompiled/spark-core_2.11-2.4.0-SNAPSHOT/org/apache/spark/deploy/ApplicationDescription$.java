package org.apache.spark.deploy;

import java.net.URI;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple10;
import scala.runtime.AbstractFunction10;
import scala.runtime.BoxesRunTime;

public final class ApplicationDescription$ extends AbstractFunction10<String, Option<Object>, Object, Command, String, Option<URI>, Option<String>, Option<Object>, Option<Object>, String, ApplicationDescription> implements Serializable {
   public static final ApplicationDescription$ MODULE$;

   static {
      new ApplicationDescription$();
   }

   public final String toString() {
      return "ApplicationDescription";
   }

   public ApplicationDescription apply(String name, Option<Object> maxCores, int memoryPerExecutorMB, Command command, String appUiUrl, Option<URI> eventLogDir, Option<String> eventLogCodec, Option<Object> coresPerExecutor, Option<Object> initialExecutorLimit, String user) {
      return new ApplicationDescription(name, maxCores, memoryPerExecutorMB, command, appUiUrl, eventLogDir, eventLogCodec, coresPerExecutor, initialExecutorLimit, user);
   }

   public Option<Tuple10<String, Option<Object>, Object, Command, String, Option<URI>, Option<String>, Option<Object>, Option<Object>, String>> unapply(ApplicationDescription x$0) {
      return (Option<Tuple10<String, Option<Object>, Object, Command, String, Option<URI>, Option<String>, Option<Object>, Option<Object>, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple10(x$0.name(), x$0.maxCores(), BoxesRunTime.boxToInteger(x$0.memoryPerExecutorMB()), x$0.command(), x$0.appUiUrl(), x$0.eventLogDir(), x$0.eventLogCodec(), x$0.coresPerExecutor(), x$0.initialExecutorLimit(), x$0.user())));
   }

   public Option<URI> $lessinit$greater$default$6() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$7() {
      return None$.MODULE$;
   }

   public Option<Object> $lessinit$greater$default$8() {
      return None$.MODULE$;
   }

   public Option<Object> $lessinit$greater$default$9() {
      return None$.MODULE$;
   }

   public String $lessinit$greater$default$10() {
      return System.getProperty("user.name", "<unknown>");
   }

   public Option<URI> apply$default$6() {
      return None$.MODULE$;
   }

   public Option<String> apply$default$7() {
      return None$.MODULE$;
   }

   public Option<Object> apply$default$8() {
      return None$.MODULE$;
   }

   public Option<Object> apply$default$9() {
      return None$.MODULE$;
   }

   public String apply$default$10() {
      return System.getProperty("user.name", "<unknown>");
   }

   private Object readResolve() {
      return MODULE$;
   }

   private ApplicationDescription$() {
      MODULE$ = this;
   }
}
