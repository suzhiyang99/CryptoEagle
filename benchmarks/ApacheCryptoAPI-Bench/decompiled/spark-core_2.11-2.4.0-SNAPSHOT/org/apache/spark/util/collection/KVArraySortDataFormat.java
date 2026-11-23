package org.apache.spark.util.collection;

import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00194Q!\u0001\u0002\u0001\r1\u0011Qc\u0013,BeJ\f\u0017pU8si\u0012\u000bG/\u0019$pe6\fGO\u0003\u0002\u0004\t\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\u000b\u0005\u00151\u0011\u0001B;uS2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u000b\u0004\u001bQ)3C\u0001\u0001\u000f!\u0011y\u0001CE\u0011\u000e\u0003\tI!!\u0005\u0002\u0003\u001dM{'\u000f\u001e#bi\u00064uN]7biB\u00111\u0003\u0006\u0007\u0001\t\u0015)\u0002A1\u0001\u0018\u0005\u0005Y5\u0001A\t\u00031y\u0001\"!\u0007\u000f\u000e\u0003iQ\u0011aG\u0001\u0006g\u000e\fG.Y\u0005\u0003;i\u0011qAT8uQ&tw\r\u0005\u0002\u001a?%\u0011\u0001E\u0007\u0002\u0004\u0003:L\bcA\r#I%\u00111E\u0007\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003'\u0015\"QA\n\u0001C\u0002\u001d\u0012\u0011\u0001V\t\u00031!\u0002\"!G\u0015\n\u0005)R\"AB!osJ+g\r\u0003\u0005-\u0001\t\r\t\u0015a\u0003.\u0003))g/\u001b3f]\u000e,G%\r\t\u0004]E\"S\"A\u0018\u000b\u0005AR\u0012a\u0002:fM2,7\r^\u0005\u0003e=\u0012\u0001b\u00117bgN$\u0016m\u001a\u0005\u0006i\u0001!\t!N\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003Y\"\"a\u000e\u001d\u0011\t=\u0001!\u0003\n\u0005\u0006YM\u0002\u001d!\f\u0005\u0006u\u0001!\teO\u0001\u0007O\u0016$8*Z=\u0015\u0007Iad\bC\u0003>s\u0001\u0007\u0011%\u0001\u0003eCR\f\u0007\"B :\u0001\u0004\u0001\u0015a\u00019pgB\u0011\u0011$Q\u0005\u0003\u0005j\u00111!\u00138u\u0011\u0015!\u0005\u0001\"\u0011F\u0003\u0011\u0019x/\u00199\u0015\t\u0019K%\n\u0014\t\u00033\u001dK!\u0001\u0013\u000e\u0003\tUs\u0017\u000e\u001e\u0005\u0006{\r\u0003\r!\t\u0005\u0006\u0017\u000e\u0003\r\u0001Q\u0001\u0005a>\u001c\b\u0007C\u0003N\u0007\u0002\u0007\u0001)\u0001\u0003q_N\f\u0004\"B(\u0001\t\u0003\u0002\u0016aC2paf,E.Z7f]R$RAR)T+^CQA\u0015(A\u0002\u0005\n1a\u001d:d\u0011\u0015!f\n1\u0001A\u0003\u0019\u0019(o\u0019)pg\")aK\u0014a\u0001C\u0005\u0019Am\u001d;\t\u000bas\u0005\u0019\u0001!\u0002\r\u0011\u001cH\u000fU8t\u0011\u0015Q\u0006\u0001\"\u0011\\\u0003%\u0019w\u000e]=SC:<W\r\u0006\u0004G9vsv\f\u0019\u0005\u0006%f\u0003\r!\t\u0005\u0006)f\u0003\r\u0001\u0011\u0005\u0006-f\u0003\r!\t\u0005\u00061f\u0003\r\u0001\u0011\u0005\u0006Cf\u0003\r\u0001Q\u0001\u0007Y\u0016tw\r\u001e5\t\u000b\r\u0004A\u0011\t3\u0002\u0011\u0005dGn\\2bi\u0016$\"!I3\t\u000b\u0005\u0014\u0007\u0019\u0001!"
)
public class KVArraySortDataFormat<K, T> extends SortDataFormat<K, T[]> {
   private final ClassTag<T> evidence$1;

   public K getKey(T[] data, int pos) {
      return (K)data[2 * pos];
   }

   public void swap(T[] data, int pos0, int pos1) {
      Object tmpKey = data[2 * pos0];
      Object tmpVal = data[2 * pos0 + 1];
      data[2 * pos0] = data[2 * pos1];
      data[2 * pos0 + 1] = data[2 * pos1 + 1];
      data[2 * pos1] = tmpKey;
      data[2 * pos1 + 1] = tmpVal;
   }

   public void copyElement(T[] src, int srcPos, T[] dst, int dstPos) {
      dst[2 * dstPos] = src[2 * srcPos];
      dst[2 * dstPos + 1] = src[2 * srcPos + 1];
   }

   public void copyRange(T[] src, int srcPos, T[] dst, int dstPos, int length) {
      System.arraycopy(src, 2 * srcPos, dst, 2 * dstPos, 2 * length);
   }

   public T[] allocate(int length) {
      return (T[])((Object[])this.evidence$1.newArray(2 * length));
   }

   public KVArraySortDataFormat(ClassTag<T> evidence$1) {
      this.evidence$1 = evidence$1;
   }
}
