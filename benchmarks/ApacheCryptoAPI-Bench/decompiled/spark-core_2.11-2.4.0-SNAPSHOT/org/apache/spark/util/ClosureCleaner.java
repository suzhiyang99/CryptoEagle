package org.apache.spark.util;

import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}wAB\u0001\u0003\u0011\u0003!!\"\u0001\bDY>\u001cXO]3DY\u0016\fg.\u001a:\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'o\u001a\t\u0003\u00171i\u0011A\u0001\u0004\u0007\u001b\tA\t\u0001\u0002\b\u0003\u001d\rcwn];sK\u000ecW-\u00198feN\u0019AbD\u000b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g!\t1\u0012$D\u0001\u0018\u0015\tAB!\u0001\u0005j]R,'O\\1m\u0013\tQrCA\u0004M_\u001e<\u0017N\\4\t\u000bqaA\u0011\u0001\u0010\u0002\rqJg.\u001b;?\u0007\u0001!\u0012A\u0003\u0005\u0007A1!\tAA\u0011\u0002\u001d\u001d,Go\u00117bgN\u0014V-\u00193feR\u0011!E\u000b\t\u0003G!j\u0011\u0001\n\u0006\u0003K\u0019\nA!Y:nk)\u0011qEB\u0001\u0006q\n,\u0017M\\\u0005\u0003S\u0011\u00121b\u00117bgN\u0014V-\u00193fe\")1f\ba\u0001Y\u0005\u00191\r\\:1\u000552\u0004c\u0001\u00182i9\u0011\u0001cL\u0005\u0003aE\ta\u0001\u0015:fI\u00164\u0017B\u0001\u001a4\u0005\u0015\u0019E.Y:t\u0015\t\u0001\u0014\u0003\u0005\u00026m1\u0001A!C\u001c+\u0003\u0003\u0005\tQ!\u00019\u0005\ryF%M\t\u0003sq\u0002\"\u0001\u0005\u001e\n\u0005m\n\"a\u0002(pi\"Lgn\u001a\t\u0003!uJ!AP\t\u0003\u0007\u0005s\u0017\u0010C\u0003A\u0019\u0011%\u0011)A\u0005jg\u000ecwn];sKR\u0011!)\u0012\t\u0003!\rK!\u0001R\t\u0003\u000f\t{w\u000e\\3b]\")1f\u0010a\u0001\rB\u0012q)\u0013\t\u0004]EB\u0005CA\u001bJ\t%QU)!A\u0001\u0002\u000b\u0005\u0001HA\u0002`IIBQ\u0001\u0014\u0007\u0005\n5\u000b\u0011dZ3u\u001fV$XM]\"mCN\u001cXm]!oI>\u0013'.Z2ugR\u0011aj\u0019\t\u0005!=\u000b&-\u0003\u0002Q#\t1A+\u001e9mKJ\u00022A\u0015.^\u001d\t\u0019\u0006L\u0004\u0002U/6\tQK\u0003\u0002W;\u00051AH]8pizJ\u0011AE\u0005\u00033F\tq\u0001]1dW\u0006<W-\u0003\u0002\\9\n!A*[:u\u0015\tI\u0016\u0003\r\u0002_AB\u0019a&M0\u0011\u0005U\u0002G!C1L\u0003\u0003\u0005\tQ!\u00019\u0005\ryFe\r\t\u0004%j{\u0001\"\u00023L\u0001\u0004y\u0011aA8cU\")a\r\u0004C\u0005O\u00061r-\u001a;J]:,'o\u00117pgV\u0014Xm\u00117bgN,7\u000f\u0006\u0002i]B\u0019!KW51\u0005)d\u0007c\u0001\u00182WB\u0011Q\u0007\u001c\u0003\n[\u0016\f\t\u0011!A\u0003\u0002a\u00121a\u0018\u00135\u0011\u0015!W\r1\u0001\u0010\u0011\u0015\u0001H\u0002\"\u0003r\u0003IIg.\u001b;BG\u000e,7o]3e\r&,G\u000eZ:\u0015\tI,\u0018Q\u0003\t\u0003!ML!\u0001^\t\u0003\tUs\u0017\u000e\u001e\u0005\u0006m>\u0004\ra^\u0001\u000fC\u000e\u001cWm]:fI\u001aKW\r\u001c3t!\u0015AXp`A\u0005\u001b\u0005I(B\u0001>|\u0003\u001diW\u000f^1cY\u0016T!\u0001`\t\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002\u007fs\n\u0019Q*\u001991\t\u0005\u0005\u0011Q\u0001\t\u0005]E\n\u0019\u0001E\u00026\u0003\u000b!!\"a\u0002v\u0003\u0003\u0005\tQ!\u00019\u0005\ryF\u0005\u000f\t\u0006q\u0006-\u0011qB\u0005\u0004\u0003\u001bI(aA*fiB\u0019a&!\u0005\n\u0007\u0005M1G\u0001\u0004TiJLgn\u001a\u0005\b\u0003/y\u0007\u0019AA\r\u00031yW\u000f^3s\u00072\f7o]3t!\u0015\u0011\u00161DA\u0010\u0013\r\ti\u0002\u0018\u0002\u0004'\u0016\f\b\u0007BA\u0011\u0003K\u0001BAL\u0019\u0002$A\u0019Q'!\n\u0005\u0017\u0005\u001d\u0012QCA\u0001\u0002\u0003\u0015\t\u0001\u000f\u0002\u0004?\u0012J\u0004bBA\u0016\u0019\u0011%\u0011QF\u0001\u0012g\u0016$\u0018iY2fgN,GMR5fY\u0012\u001cH#\u0003:\u00020\u0005u\u0012\u0011IA\"\u0011!\t\t$!\u000bA\u0002\u0005M\u0012AC8vi\u0016\u00148\t\\1tgB\"\u0011QGA\u001d!\u0011q\u0013'a\u000e\u0011\u0007U\nI\u0004B\u0006\u0002<\u0005=\u0012\u0011!A\u0001\u0006\u0003A$\u0001B0%cABq!a\u0010\u0002*\u0001\u0007q\"A\u0003dY>tW\r\u0003\u0004e\u0003S\u0001\ra\u0004\u0005\bm\u0006%\u0002\u0019AA#!\u0019AX0a\u0012\u0002\nA\"\u0011\u0011JA'!\u0011q\u0013'a\u0013\u0011\u0007U\ni\u0005B\u0006\u0002P\u0005\r\u0013\u0011!A\u0001\u0006\u0003A$\u0001B0%cEBq!a\u0015\r\t\u0013\t)&A\tdY>tW-\u00118e'\u0016$h)[3mIN$\u0012bDA,\u00037\ni&!\u001b\t\u000f\u0005e\u0013\u0011\u000ba\u0001\u001f\u00051\u0001/\u0019:f]RDa\u0001ZA)\u0001\u0004y\u0001\u0002CA\u0019\u0003#\u0002\r!a\u00181\t\u0005\u0005\u0014Q\r\t\u0005]E\n\u0019\u0007E\u00026\u0003K\"1\"a\u001a\u0002^\u0005\u0005\t\u0011!B\u0001q\t!q\fJ\u00193\u0011\u001d1\u0018\u0011\u000ba\u0001\u0003W\u0002b\u0001_?\u0002n\u0005%\u0001\u0007BA8\u0003g\u0002BAL\u0019\u0002rA\u0019Q'a\u001d\u0005\u0017\u0005U\u0014\u0011NA\u0001\u0002\u0003\u0015\t\u0001\u000f\u0002\u0005?\u0012\n4\u0007C\u0004\u0002z1!\t!a\u001f\u0002\u000b\rdW-\u00198\u0015\u000fI\fi(!!\u0002\u0006\"9\u0011qPA<\u0001\u0004y\u0011aB2m_N,(/\u001a\u0005\n\u0003\u0007\u000b9\b%AA\u0002\t\u000b\u0011c\u00195fG.\u001cVM]5bY&T\u0018M\u00197f\u0011%\t9)a\u001e\u0011\u0002\u0003\u0007!)A\tdY\u0016\fg\u000e\u0016:b]NLG/\u001b<fYfDq!!\u001f\r\t\u0013\tY\tF\u0005s\u0003\u001b\u000b\t*a%\u0002\u0016\"9\u0011qRAE\u0001\u0004y\u0011\u0001\u00024v]\u000eDq!a!\u0002\n\u0002\u0007!\tC\u0004\u0002\b\u0006%\u0005\u0019\u0001\"\t\u000fY\fI\t1\u0001\u0002\u0018B1\u00010`AM\u0003\u0013\u0001D!a'\u0002 B!a&MAO!\r)\u0014q\u0014\u0003\f\u0003C\u000b)*!A\u0001\u0002\u000b\u0005\u0001H\u0001\u0003`IE\"\u0004bBAS\u0019\u0011%\u0011qU\u0001\u0013K:\u001cXO]3TKJL\u0017\r\\5{C\ndW\rF\u0002s\u0003SCq!a$\u0002$\u0002\u0007q\u0002C\u0004\u0002.2!I!a,\u0002!%t7\u000f^1oi&\fG/Z\"mCN\u001cH#B\b\u00022\u0006u\u0006bB\u0016\u0002,\u0002\u0007\u00111\u0017\u0019\u0005\u0003k\u000bI\f\u0005\u0003/c\u0005]\u0006cA\u001b\u0002:\u0012Y\u00111XAY\u0003\u0003\u0005\tQ!\u00019\u0005\u0011yF%\r\u001c\t\u000f\u0005}\u00161\u0016a\u0001\u001f\u0005yQM\\2m_NLgnZ(cU\u0016\u001cG\u000fC\u0005\u0002D2\t\n\u0011\"\u0001\u0002F\u0006y1\r\\3b]\u0012\"WMZ1vYR$#'\u0006\u0002\u0002H*\u001a!)!3,\u0005\u0005-\u0007\u0003BAg\u0003/l!!a4\u000b\t\u0005E\u00171[\u0001\nk:\u001c\u0007.Z2lK\u0012T1!!6\u0012\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u00033\fyMA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016D\u0011\"!8\r#\u0003%\t!!2\u0002\u001f\rdW-\u00198%I\u00164\u0017-\u001e7uIM\u0002"
)
public final class ClosureCleaner {
   public static boolean initializeLogIfNecessary$default$2() {
      return ClosureCleaner$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return ClosureCleaner$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      ClosureCleaner$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return ClosureCleaner$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      ClosureCleaner$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      ClosureCleaner$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      ClosureCleaner$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      ClosureCleaner$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      ClosureCleaner$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      ClosureCleaner$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      ClosureCleaner$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      ClosureCleaner$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      ClosureCleaner$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      ClosureCleaner$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return ClosureCleaner$.MODULE$.log();
   }

   public static String logName() {
      return ClosureCleaner$.MODULE$.logName();
   }

   public static boolean clean$default$3() {
      return ClosureCleaner$.MODULE$.clean$default$3();
   }

   public static boolean clean$default$2() {
      return ClosureCleaner$.MODULE$.clean$default$2();
   }

   public static void clean(Object var0, boolean var1, boolean var2) {
      ClosureCleaner$.MODULE$.clean(var0, var1, var2);
   }
}
