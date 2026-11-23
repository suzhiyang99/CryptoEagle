package org.apache.spark.internal.config;

import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;
import org.apache.spark.network.util.ByteUnit;
import org.apache.spark.network.util.JavaUtils;
import scala.Array$;
import scala.Function1;
import scala.MatchError;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;
import scala.util.matching.Regex;

public final class ConfigHelpers$ {
   public static final ConfigHelpers$ MODULE$;

   static {
      new ConfigHelpers$();
   }

   public <T> T toNumber(String s, Function1<String, T> converter, String key, String configType) {
      try {
         return (T)converter.apply(s.trim());
      } catch (NumberFormatException var5) {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " should be ", ", but was ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{key, configType, s})));
      }
   }

   public boolean toBoolean(String s, String key) {
      try {
         return (new StringOps(Predef$.MODULE$.augmentString(s.trim()))).toBoolean();
      } catch (IllegalArgumentException var3) {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " should be boolean, but was ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{key, s})));
      }
   }

   public <T> Seq<T> stringToSeq(String str, Function1<String, T> converter) {
      return (Seq)Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])str.split(",")).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$1) {
            return x$1.trim();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$2) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$2))).nonEmpty();
         }
      })).map(converter, Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
   }

   public <T> String seqToString(Seq<T> v, Function1<T, String> stringConverter) {
      return ((TraversableOnce)v.map(stringConverter, Seq$.MODULE$.canBuildFrom())).mkString(",");
   }

   public long timeFromString(String str, TimeUnit unit) {
      return JavaUtils.timeStringAs(str, unit);
   }

   public String timeToString(long v, TimeUnit unit) {
      return (new StringBuilder()).append(TimeUnit.MILLISECONDS.convert(v, unit)).append((Object)"ms").toString();
   }

   public long byteFromString(String str, ByteUnit unit) {
      Tuple2 var4 = str.length() > 0 && str.charAt(0) == '-' ? new Tuple2(str.substring(1), BoxesRunTime.boxToInteger(-1)) : new Tuple2(str, BoxesRunTime.boxToInteger(1));
      if (var4 != null) {
         String input = (String)var4._1();
         int multiplier = var4._2$mcI$sp();
         Tuple2 var7 = new Tuple2(input, BoxesRunTime.boxToInteger(multiplier));
         String input = (String)var7._1();
         int multiplier = var7._2$mcI$sp();
         return (long)multiplier * JavaUtils.byteStringAs(input, unit);
      } else {
         throw new MatchError(var4);
      }
   }

   public String byteToString(long v, ByteUnit unit) {
      return (new StringBuilder()).append(unit.convertTo(v, ByteUnit.BYTE)).append((Object)"b").toString();
   }

   public Regex regexFromString(String str, String key) {
      try {
         return (new StringOps(Predef$.MODULE$.augmentString(str))).r();
      } catch (PatternSyntaxException var4) {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " should be a regex, but was ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{key, str})), var4);
      }
   }

   private ConfigHelpers$() {
      MODULE$ = this;
   }
}
