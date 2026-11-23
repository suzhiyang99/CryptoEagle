package org.apache.spark.deploy;

import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.Tuple6;
import scala.collection.Map;
import scala.collection.Seq;
import scala.runtime.AbstractFunction6;

public final class Command$ extends AbstractFunction6<String, Seq<String>, Map<String, String>, Seq<String>, Seq<String>, Seq<String>, Command> implements Serializable {
   public static final Command$ MODULE$;

   static {
      new Command$();
   }

   public final String toString() {
      return "Command";
   }

   public Command apply(String mainClass, Seq<String> arguments, Map<String, String> environment, Seq<String> classPathEntries, Seq<String> libraryPathEntries, Seq<String> javaOpts) {
      return new Command(mainClass, arguments, environment, classPathEntries, libraryPathEntries, javaOpts);
   }

   public Option<Tuple6<String, Seq<String>, Map<String, String>, Seq<String>, Seq<String>, Seq<String>>> unapply(Command x$0) {
      return (Option<Tuple6<String, Seq<String>, Map<String, String>, Seq<String>, Seq<String>, Seq<String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple6(x$0.mainClass(), x$0.arguments(), x$0.environment(), x$0.classPathEntries(), x$0.libraryPathEntries(), x$0.javaOpts())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private Command$() {
      MODULE$ = this;
   }
}
