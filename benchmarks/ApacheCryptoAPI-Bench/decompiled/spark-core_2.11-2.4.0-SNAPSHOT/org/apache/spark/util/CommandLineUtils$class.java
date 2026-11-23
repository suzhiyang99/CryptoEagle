package org.apache.spark.util;

import org.apache.spark.SparkException;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.mutable.StringBuilder;

public abstract class CommandLineUtils$class {
   public static void printWarning(CommandLineUtils $this, String str) {
      $this.printStream().println((new StringBuilder()).append((Object)"Warning: ").append((Object)str).toString());
   }

   public static void printErrorAndExit(CommandLineUtils $this, String str) {
      $this.printStream().println((new StringBuilder()).append((Object)"Error: ").append((Object)str).toString());
      $this.printStream().println("Run with --help for usage help or --verbose for debug output");
      $this.exitFn().apply$mcVI$sp(1);
   }

   public static Tuple2 parseSparkConfProperty(CommandLineUtils $this, String pair) {
      Seq var2 = Predef$.MODULE$.refArrayOps((Object[])pair.split("=", 2)).toSeq();
      Some var3 = Seq$.MODULE$.unapplySeq(var2);
      if (!var3.isEmpty() && var3.get() != null && ((SeqLike)var3.get()).lengthCompare(2) == 0) {
         String k = (String)((SeqLike)var3.get()).apply(0);
         String v = (String)((SeqLike)var3.get()).apply(1);
         Tuple2 var6 = new Tuple2(k, v);
         return var6;
      } else {
         $this.printErrorAndExit((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Spark config without '=': ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{pair})));
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Spark config without '=': ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{pair})));
      }
   }

   public static void $init$(CommandLineUtils $this) {
      $this.exitFn_$eq(new Serializable($this) {
         public static final long serialVersionUID = 0L;

         public final void apply(int exitCode) {
            this.apply$mcVI$sp(exitCode);
         }

         public void apply$mcVI$sp(int exitCode) {
            System.exit(exitCode);
         }
      });
      $this.printStream_$eq(System.err);
   }
}
