package org.apache.spark.ui;

import scala.Predef$;
import scala.StringContext;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001%3a!\u0001\u0002\u0002\u0002\tQ!a\u0004)bO\u0016$G)\u0019;b'>,(oY3\u000b\u0005\r!\u0011AA;j\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<WCA\u0006#'\t\u0001A\u0002\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\u0005\t'\u0001\u0011)\u0019!C\u0001+\u0005A\u0001/Y4f'&TXm\u0001\u0001\u0016\u0003Y\u0001\"!D\f\n\u0005aq!aA%oi\"A!\u0004\u0001B\u0001B\u0003%a#A\u0005qC\u001e,7+\u001b>fA!)A\u0004\u0001C\u0001;\u00051A(\u001b8jiz\"\"AH\u0016\u0011\u0007}\u0001\u0001%D\u0001\u0003!\t\t#\u0005\u0004\u0001\u0005\u000b\r\u0002!\u0019\u0001\u0013\u0003\u0003Q\u000b\"!\n\u0015\u0011\u000551\u0013BA\u0014\u000f\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"!D\u0015\n\u0005)r!aA!os\")1c\u0007a\u0001-!)Q\u0006\u0001D\t+\u0005AA-\u0019;b'&TX\rC\u00030\u0001\u0019E\u0001'A\u0005tY&\u001cW\rR1uCR\u0019\u0011'P \u0011\u0007IR\u0004E\u0004\u00024q9\u0011AgN\u0007\u0002k)\u0011a\u0007F\u0001\u0007yI|w\u000e\u001e \n\u0003=I!!\u000f\b\u0002\u000fA\f7m[1hK&\u00111\b\u0010\u0002\u0004'\u0016\f(BA\u001d\u000f\u0011\u0015qd\u00061\u0001\u0017\u0003\u00111'o\\7\t\u000b\u0001s\u0003\u0019\u0001\f\u0002\u0005Q|\u0007\"\u0002\"\u0001\t\u0003\u0019\u0015\u0001\u00039bO\u0016$\u0015\r^1\u0015\u0005\u0011;\u0005cA\u0010FA%\u0011aI\u0001\u0002\t!\u0006<W\rR1uC\")\u0001*\u0011a\u0001-\u0005!\u0001/Y4f\u0001"
)
public abstract class PagedDataSource<T> {
   private final int pageSize;

   public int pageSize() {
      return this.pageSize;
   }

   public abstract int dataSize();

   public abstract Seq<T> sliceData(int var1, int var2);

   public PageData<T> pageData(int page) {
      int totalPages = (this.dataSize() + this.pageSize() - 1) / this.pageSize();
      if (page > 0 && page <= totalPages) {
         int from = (page - 1) * this.pageSize();
         int to = RichInt$.MODULE$.min$extension(Predef$.MODULE$.intWrapper(this.dataSize()), page * this.pageSize());
         return new PageData<T>(totalPages, this.sliceData(from, to));
      } else {
         throw new IndexOutOfBoundsException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Page ", " is out of range. Please select a page number between 1 and ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(page), BoxesRunTime.boxToInteger(totalPages)})));
      }
   }

   public PagedDataSource(int pageSize) {
      this.pageSize = pageSize;
      if (pageSize <= 0) {
         throw new IllegalArgumentException("Page size must be positive");
      }
   }
}
