package org.apache.spark.util.collection;

import java.util.Comparator;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001Y3Q!\u0001\u0002\u0001\r1\u0011aaU8si\u0016\u0014(BA\u0002\u0005\u0003)\u0019w\u000e\u001c7fGRLwN\u001c\u0006\u0003\u000b\u0019\tA!\u001e;jY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x-F\u0002\u000e=!\u001a\"\u0001\u0001\b\u0011\u0005=\u0011R\"\u0001\t\u000b\u0003E\tQa]2bY\u0006L!a\u0005\t\u0003\r\u0005s\u0017PU3g\u0011!)\u0002A!b\u0001\n\u00139\u0012!A:\u0004\u0001U\t\u0001\u0004\u0005\u0003\u001a5q9S\"\u0001\u0002\n\u0005m\u0011!AD*peR$\u0015\r^1G_Jl\u0017\r\u001e\t\u0003;ya\u0001\u0001B\u0003 \u0001\t\u0007\u0001EA\u0001L#\t\tC\u0005\u0005\u0002\u0010E%\u00111\u0005\u0005\u0002\b\u001d>$\b.\u001b8h!\tyQ%\u0003\u0002'!\t\u0019\u0011I\\=\u0011\u0005uAC!B\u0015\u0001\u0005\u0004\u0001#A\u0002\"vM\u001a,'\u000f\u0003\u0005,\u0001\t\u0005\t\u0015!\u0003\u0019\u0003\t\u0019\b\u0005C\u0003.\u0001\u0011\u0005a&\u0001\u0004=S:LGO\u0010\u000b\u0003_A\u0002B!\u0007\u0001\u001dO!)Q\u0003\fa\u00011!9!\u0007\u0001b\u0001\n\u0013\u0019\u0014a\u0002;j[N{'\u000f^\u000b\u0002iA!\u0011$\u000e\u000f(\u0013\t1$AA\u0004US6\u001cvN\u001d;\t\ra\u0002\u0001\u0015!\u00035\u0003!!\u0018.\\*peR\u0004\u0003\"\u0002\u001e\u0001\t\u0003Y\u0014\u0001B:peR$R\u0001P B\r\"\u0003\"aD\u001f\n\u0005y\u0002\"\u0001B+oSRDQ\u0001Q\u001dA\u0002\u001d\n\u0011!\u0019\u0005\u0006\u0005f\u0002\raQ\u0001\u0003Y>\u0004\"a\u0004#\n\u0005\u0015\u0003\"aA%oi\")q)\u000fa\u0001\u0007\u0006\u0011\u0001.\u001b\u0005\u0006\u0013f\u0002\rAS\u0001\u0002GB\u00121j\u0015\t\u0004\u0019B\u0013V\"A'\u000b\u0005\u0015q%\"A(\u0002\t)\fg/Y\u0005\u0003#6\u0013!bQ8na\u0006\u0014\u0018\r^8s!\ti2\u000bB\u0005U\u0011\u0006\u0005\t\u0011!B\u0001+\n\u0019q\fJ\u0019\u0012\u0005q!\u0003"
)
public class Sorter<K, Buffer> {
   private final SortDataFormat<K, Buffer> s;
   private final TimSort<K, Buffer> timSort;

   private SortDataFormat<K, Buffer> s() {
      return this.s;
   }

   private TimSort<K, Buffer> timSort() {
      return this.timSort;
   }

   public void sort(Buffer a, int lo, int hi, Comparator<? super K> c) {
      this.timSort().sort(a, lo, hi, c);
   }

   public Sorter(SortDataFormat<K, Buffer> s) {
      this.s = s;
      this.timSort = new TimSort<K, Buffer>(s);
   }
}
