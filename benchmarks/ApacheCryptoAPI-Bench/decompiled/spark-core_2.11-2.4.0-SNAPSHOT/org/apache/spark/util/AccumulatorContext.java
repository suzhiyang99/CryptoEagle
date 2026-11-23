package org.apache.spark.util;

import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-qAB\u0001\u0003\u0011\u0003!!\"\u0001\nBG\u000e,X.\u001e7bi>\u00148i\u001c8uKb$(BA\u0002\u0005\u0003\u0011)H/\u001b7\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u0004\"a\u0003\u0007\u000e\u0003\t1a!\u0004\u0002\t\u0002\u0011q!AE!dGVlW\u000f\\1u_J\u001cuN\u001c;fqR\u001c\"\u0001D\b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g\u0011\u00151B\u0002\"\u0001\u0019\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0006\t\u000fia!\u0019!C\u00057\u0005IqN]5hS:\fGn]\u000b\u00029A!QdI\u0013)\u001b\u0005q\"BA\u0010!\u0003)\u0019wN\\2veJ,g\u000e\u001e\u0006\u0003\u0007\u0005R\u0011AI\u0001\u0005U\u00064\u0018-\u0003\u0002%=\t\t2i\u001c8dkJ\u0014XM\u001c;ICNDW*\u00199\u0011\u0005A1\u0013BA\u0014\u0012\u0005\u0011auN\\4\u0011\u0007%r\u0003'D\u0001+\u0015\tYC&A\u0002sK\u001aT!!L\u0011\u0002\t1\fgnZ\u0005\u0003_)\u0012QbV3bWJ+g-\u001a:f]\u000e,\u0007gA\u00197\u0005B!1B\r\u001bB\u0013\t\u0019$AA\u0007BG\u000e,X.\u001e7bi>\u0014hK\r\t\u0003kYb\u0001\u0001B\u00058q\u0005\u0005\t\u0011!B\u0001u\t\u0019q\fJ\u0019\t\reb\u0001\u0015!\u0003\u001d\u0003)y'/[4j]\u0006d7\u000fI\t\u0003wy\u0002\"\u0001\u0005\u001f\n\u0005u\n\"a\u0002(pi\"Lgn\u001a\t\u0003!}J!\u0001Q\t\u0003\u0007\u0005s\u0017\u0010\u0005\u00026\u0005\u0012I1\tOA\u0001\u0002\u0003\u0015\tA\u000f\u0002\u0004?\u0012\u0012\u0004BB#\rA\u0003%a)\u0001\u0004oKb$\u0018\n\u001a\t\u0003\u000f*k\u0011\u0001\u0013\u0006\u0003\u0013z\ta!\u0019;p[&\u001c\u0017BA&I\u0005)\tEo\\7jG2{gn\u001a\u0005\u0006\u001b2!\tAT\u0001\u0006]\u0016<\u0018\n\u001a\u000b\u0002K!)\u0001\u000b\u0004C\u0001#\u0006Ia.^7BG\u000e,Xn]\u000b\u0002%B\u0011\u0001cU\u0005\u0003)F\u00111!\u00138u\u0011\u00151F\u0002\"\u0001X\u0003!\u0011XmZ5ti\u0016\u0014HC\u0001-\\!\t\u0001\u0012,\u0003\u0002[#\t!QK\\5u\u0011\u0015aV\u000b1\u0001^\u0003\u0005\t\u0007g\u00010aGB!1BM0c!\t)\u0004\rB\u0005b7\u0006\u0005\t\u0011!B\u0001u\t\u0019q\fJ\u001a\u0011\u0005U\u001aG!\u00033\\\u0003\u0003\u0005\tQ!\u0001;\u0005\ryF\u0005\u000e\u0005\u0006M2!\taZ\u0001\u0007e\u0016lwN^3\u0015\u0005aC\u0007\"B5f\u0001\u0004)\u0013AA5e\u0011\u0015YG\u0002\"\u0001m\u0003\r9W\r\u001e\u000b\u0003[b\u00042\u0001\u00058q\u0013\ty\u0017C\u0001\u0004PaRLwN\u001c\u0019\u0004cN4\b\u0003B\u00063eV\u0004\"!N:\u0005\u0013QT\u0017\u0011!A\u0001\u0006\u0003Q$aA0%oA\u0011QG\u001e\u0003\no*\f\t\u0011!A\u0003\u0002i\u00121a\u0018\u00139\u0011\u0015I'\u000e1\u0001&\u0011\u0015QH\u0002\"\u0001|\u0003\u0015\u0019G.Z1s)\u0005A\u0006\u0002C?\r\u0005\u0004%\t\u0001\u0002@\u0002)M\u000bFjX!D\u0007Vku,\u0013#F\u001dRKe)S#S+\u0005y\b\u0003BA\u0001\u0003\u0007i\u0011\u0001L\u0005\u0004\u0003\u000ba#AB*ue&tw\rC\u0004\u0002\n1\u0001\u000b\u0011B@\u0002+M\u000bFjX!D\u0007Vku,\u0013#F\u001dRKe)S#SA\u0001"
)
public final class AccumulatorContext {
   public static void clear() {
      AccumulatorContext$.MODULE$.clear();
   }

   public static Option<AccumulatorV2<?, ?>> get(long var0) {
      return AccumulatorContext$.MODULE$.get(var0);
   }

   public static void remove(long var0) {
      AccumulatorContext$.MODULE$.remove(var0);
   }

   public static void register(AccumulatorV2<?, ?> var0) {
      AccumulatorContext$.MODULE$.register(var0);
   }

   public static int numAccums() {
      return AccumulatorContext$.MODULE$.numAccums();
   }

   public static long newId() {
      return AccumulatorContext$.MODULE$.newId();
   }
}
