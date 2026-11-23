package org.apache.spark.util;

import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple2$mcII$sp;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.util.matching.Regex;

public final class VersionUtils$ {
   public static final VersionUtils$ MODULE$;
   private final Regex majorMinorRegex;

   static {
      new VersionUtils$();
   }

   private Regex majorMinorRegex() {
      return this.majorMinorRegex;
   }

   public int majorVersion(String sparkVersion) {
      return this.majorMinorVersion(sparkVersion)._1$mcI$sp();
   }

   public int minorVersion(String sparkVersion) {
      return this.majorMinorVersion(sparkVersion)._2$mcI$sp();
   }

   public Tuple2<Object, Object> majorMinorVersion(String sparkVersion) {
      Option var2 = this.majorMinorRegex().findFirstMatchIn(sparkVersion);
      if (var2 instanceof Some) {
         Some var3 = (Some)var2;
         Regex.Match m = (Regex.Match)var3.x();
         Tuple2$mcII$sp var5 = new Tuple2$mcII$sp((new StringOps(Predef$.MODULE$.augmentString(m.group(1)))).toInt(), (new StringOps(Predef$.MODULE$.augmentString(m.group(2)))).toInt());
         return var5;
      } else if (None$.MODULE$.equals(var2)) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Spark tried to parse '", "' as a Spark"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{sparkVersion}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" version string, but it could not find the major and minor version numbers."})))).s(Nil$.MODULE$)).toString());
      } else {
         throw new MatchError(var2);
      }
   }

   private VersionUtils$() {
      MODULE$ = this;
      this.majorMinorRegex = (new StringOps(Predef$.MODULE$.augmentString("^(\\d+)\\.(\\d+)(\\..*)?$"))).r();
   }
}
