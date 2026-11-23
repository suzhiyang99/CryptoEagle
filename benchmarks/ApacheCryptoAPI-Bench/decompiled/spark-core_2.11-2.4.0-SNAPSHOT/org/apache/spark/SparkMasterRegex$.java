package org.apache.spark;

import scala.Predef$;
import scala.collection.immutable.StringOps;
import scala.util.matching.Regex;

public final class SparkMasterRegex$ {
   public static final SparkMasterRegex$ MODULE$;
   private final Regex LOCAL_N_REGEX;
   private final Regex LOCAL_N_FAILURES_REGEX;
   private final Regex LOCAL_CLUSTER_REGEX;
   private final Regex SPARK_REGEX;

   static {
      new SparkMasterRegex$();
   }

   public Regex LOCAL_N_REGEX() {
      return this.LOCAL_N_REGEX;
   }

   public Regex LOCAL_N_FAILURES_REGEX() {
      return this.LOCAL_N_FAILURES_REGEX;
   }

   public Regex LOCAL_CLUSTER_REGEX() {
      return this.LOCAL_CLUSTER_REGEX;
   }

   public Regex SPARK_REGEX() {
      return this.SPARK_REGEX;
   }

   private SparkMasterRegex$() {
      MODULE$ = this;
      this.LOCAL_N_REGEX = (new StringOps(Predef$.MODULE$.augmentString("local\\[([0-9]+|\\*)\\]"))).r();
      this.LOCAL_N_FAILURES_REGEX = (new StringOps(Predef$.MODULE$.augmentString("local\\[([0-9]+|\\*)\\s*,\\s*([0-9]+)\\]"))).r();
      this.LOCAL_CLUSTER_REGEX = (new StringOps(Predef$.MODULE$.augmentString("local-cluster\\[\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*]"))).r();
      this.SPARK_REGEX = (new StringOps(Predef$.MODULE$.augmentString("spark://(.*)"))).r();
   }
}
