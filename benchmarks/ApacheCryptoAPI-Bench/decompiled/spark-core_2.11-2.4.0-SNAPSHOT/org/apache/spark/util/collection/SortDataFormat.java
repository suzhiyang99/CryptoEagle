package org.apache.spark.util.collection;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001y3a!\u0001\u0002\u0002\u0002\u0019a!AD*peR$\u0015\r^1G_Jl\u0017\r\u001e\u0006\u0003\u0007\u0011\t!bY8mY\u0016\u001cG/[8o\u0015\t)a!\u0001\u0003vi&d'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0016\u00075abe\u0005\u0002\u0001\u001dA\u0011qBE\u0007\u0002!)\t\u0011#A\u0003tG\u0006d\u0017-\u0003\u0002\u0014!\t1\u0011I\\=SK\u001aDQ!\u0006\u0001\u0005\u0002]\ta\u0001P5oSRt4\u0001\u0001\u000b\u00021A!\u0011\u0004\u0001\u000e&\u001b\u0005\u0011\u0001CA\u000e\u001d\u0019\u0001!Q!\b\u0001C\u0002y\u0011\u0011aS\t\u0003?\t\u0002\"a\u0004\u0011\n\u0005\u0005\u0002\"a\u0002(pi\"Lgn\u001a\t\u0003\u001f\rJ!\u0001\n\t\u0003\u0007\u0005s\u0017\u0010\u0005\u0002\u001cM\u0011)q\u0005\u0001b\u0001=\t1!)\u001e4gKJDQ!\u000b\u0001\u0005\u0002)\naA\\3x\u0017\u0016LH#\u0001\u000e\t\u000b1\u0002a\u0011C\u0017\u0002\r\u001d,GoS3z)\rQb\u0006\r\u0005\u0006_-\u0002\r!J\u0001\u0005I\u0006$\u0018\rC\u00032W\u0001\u0007!'A\u0002q_N\u0004\"aD\u001a\n\u0005Q\u0002\"aA%oi\")A\u0006\u0001C\u0001mQ!!d\u000e\u001d:\u0011\u0015yS\u00071\u0001&\u0011\u0015\tT\u00071\u00013\u0011\u0015QT\u00071\u0001\u001b\u0003\u0015\u0011X-^:f\u0011\u0015a\u0004A\"\u0001>\u0003\u0011\u0019x/\u00199\u0015\ty\n%\t\u0012\t\u0003\u001f}J!\u0001\u0011\t\u0003\tUs\u0017\u000e\u001e\u0005\u0006_m\u0002\r!\n\u0005\u0006\u0007n\u0002\rAM\u0001\u0005a>\u001c\b\u0007C\u0003Fw\u0001\u0007!'\u0001\u0003q_N\f\u0004\"B$\u0001\r\u0003A\u0015aC2paf,E.Z7f]R$RAP%L\u001b>CQA\u0013$A\u0002\u0015\n1a\u001d:d\u0011\u0015ae\t1\u00013\u0003\u0019\u0019(o\u0019)pg\")aJ\u0012a\u0001K\u0005\u0019Am\u001d;\t\u000bA3\u0005\u0019\u0001\u001a\u0002\r\u0011\u001cH\u000fU8t\u0011\u0015\u0011\u0006A\"\u0001T\u0003%\u0019w\u000e]=SC:<W\r\u0006\u0004?)V3v\u000b\u0017\u0005\u0006\u0015F\u0003\r!\n\u0005\u0006\u0019F\u0003\rA\r\u0005\u0006\u001dF\u0003\r!\n\u0005\u0006!F\u0003\rA\r\u0005\u00063F\u0003\rAM\u0001\u0007Y\u0016tw\r\u001e5\t\u000bm\u0003a\u0011\u0001/\u0002\u0011\u0005dGn\\2bi\u0016$\"!J/\t\u000beS\u0006\u0019\u0001\u001a"
)
public abstract class SortDataFormat<K, Buffer> {
   public K newKey() {
      return null;
   }

   public abstract K getKey(Buffer var1, int var2);

   public K getKey(Buffer data, int pos, K reuse) {
      return (K)this.getKey(data, pos);
   }

   public abstract void swap(Buffer var1, int var2, int var3);

   public abstract void copyElement(Buffer var1, int var2, Buffer var3, int var4);

   public abstract void copyRange(Buffer var1, int var2, Buffer var3, int var4, int var5);

   public abstract Buffer allocate(int var1);
}
