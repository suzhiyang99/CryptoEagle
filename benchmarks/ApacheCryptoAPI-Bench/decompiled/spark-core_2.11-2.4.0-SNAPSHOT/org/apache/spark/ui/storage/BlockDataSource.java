package org.apache.spark.ui.storage;

import org.apache.spark.status.api.v1.RDDPartitionInfo;
import org.apache.spark.ui.PagedDataSource;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.package$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.math.Ordering;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001=4Q!\u0001\u0002\u0001\t1\u0011qB\u00117pG.$\u0015\r^1T_V\u00148-\u001a\u0006\u0003\u0007\u0011\tqa\u001d;pe\u0006<WM\u0003\u0002\u0006\r\u0005\u0011Q/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0004\u001d=\tR\"\u0001\u0003\n\u0005A!!a\u0004)bO\u0016$G)\u0019;b'>,(oY3\u0011\u0005I\u0019R\"\u0001\u0002\n\u0005Q\u0011!!\u0005\"m_\u000e\\G+\u00192mKJ{w\u000fR1uC\"Aa\u0003\u0001B\u0001B\u0003%\u0001$A\u0007sI\u0012\u0004\u0016M\u001d;ji&|gn]\u0002\u0001!\rI2E\n\b\u00035\u0001r!a\u0007\u0010\u000e\u0003qQ!!H\f\u0002\rq\u0012xn\u001c;?\u0013\u0005y\u0012!B:dC2\f\u0017BA\u0011#\u0003\u001d\u0001\u0018mY6bO\u0016T\u0011aH\u0005\u0003I\u0015\u00121aU3r\u0015\t\t#\u0005\u0005\u0002(]5\t\u0001F\u0003\u0002*U\u0005\u0011a/\r\u0006\u0003W1\n1!\u00199j\u0015\tic!\u0001\u0004ti\u0006$Xo]\u0005\u0003_!\u0012\u0001C\u0015#E!\u0006\u0014H/\u001b;j_:LeNZ8\t\u0013E\u0002!\u0011!Q\u0001\nI2\u0014\u0001\u00039bO\u0016\u001c\u0016N_3\u0011\u0005M\"T\"\u0001\u0012\n\u0005U\u0012#aA%oi&\u0011\u0011g\u0004\u0005\tq\u0001\u0011\t\u0011)A\u0005s\u0005Q1o\u001c:u\u0007>dW/\u001c8\u0011\u0005ijdBA\u001a<\u0013\ta$%\u0001\u0004Qe\u0016$WMZ\u0005\u0003}}\u0012aa\u0015;sS:<'B\u0001\u001f#\u0011!\t\u0005A!A!\u0002\u0013\u0011\u0015\u0001\u00023fg\u000e\u0004\"aM\"\n\u0005\u0011\u0013#a\u0002\"p_2,\u0017M\u001c\u0005\u0006\r\u0002!\taR\u0001\u0007y%t\u0017\u000e\u001e \u0015\u000b!K%j\u0013'\u0011\u0005I\u0001\u0001\"\u0002\fF\u0001\u0004A\u0002\"B\u0019F\u0001\u0004\u0011\u0004\"\u0002\u001dF\u0001\u0004I\u0004\"B!F\u0001\u0004\u0011\u0005b\u0002(\u0001\u0005\u0004%IaT\u0001\u0005I\u0006$\u0018-F\u0001Q!\r\tF+E\u0007\u0002%*\u00111KI\u0001\u000bG>dG.Z2uS>t\u0017B\u0001\u0013S\u0011\u00191\u0006\u0001)A\u0005!\u0006)A-\u0019;bA!)\u0001\f\u0001C!3\u0006AA-\u0019;b'&TX-F\u00013\u0011\u0015Y\u0006\u0001\"\u0011]\u0003%\u0019H.[2f\t\u0006$\u0018\rF\u0002^=\u0002\u00042!G\u0012\u0012\u0011\u0015y&\f1\u00013\u0003\u00111'o\\7\t\u000b\u0005T\u0006\u0019\u0001\u001a\u0002\u0005Q|\u0007\"B2\u0001\t\u0013!\u0017\u0001\u00032m_\u000e\\'k\\<\u0015\u0005E)\u0007\"\u00024c\u0001\u00041\u0013\u0001\u0004:eIB\u000b'\u000f^5uS>t\u0007\"\u00025\u0001\t\u0013I\u0017\u0001C8sI\u0016\u0014\u0018N\\4\u0015\u0007)lg\u000eE\u0002\u001aWFI!\u0001\\\u0013\u0003\u0011=\u0013H-\u001a:j]\u001eDQ\u0001O4A\u0002eBQ!Q4A\u0002\t\u0003"
)
public class BlockDataSource extends PagedDataSource<BlockTableRowData> {
   private final Seq<BlockTableRowData> data;

   private Seq<BlockTableRowData> data() {
      return this.data;
   }

   public int dataSize() {
      return this.data().size();
   }

   public Seq<BlockTableRowData> sliceData(int from, int to) {
      return (Seq)this.data().slice(from, to);
   }

   public BlockTableRowData org$apache$spark$ui$storage$BlockDataSource$$blockRow(RDDPartitionInfo rddPartition) {
      return new BlockTableRowData(rddPartition.blockName(), rddPartition.storageLevel(), rddPartition.memoryUsed(), rddPartition.diskUsed(), rddPartition.executors().mkString(" "));
   }

   private Ordering<BlockTableRowData> ordering(String sortColumn, boolean desc) {
      Ordering ordering;
      if ("Block Name".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(BlockTableRowData x$7) {
               return x$7.blockName();
            }
         }, Ordering.String$.MODULE$);
      } else if ("Storage Level".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(BlockTableRowData x$8) {
               return x$8.storageLevel();
            }
         }, Ordering.String$.MODULE$);
      } else if ("Size in Memory".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(BlockTableRowData x$9) {
               return x$9.memoryUsed();
            }
         }, Ordering.Long$.MODULE$);
      } else if ("Size on Disk".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(BlockTableRowData x$10) {
               return x$10.diskUsed();
            }
         }, Ordering.Long$.MODULE$);
      } else {
         if (!"Executors".equals(sortColumn)) {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{sortColumn})));
         }

         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(BlockTableRowData x$11) {
               return x$11.executors();
            }
         }, Ordering.String$.MODULE$);
      }

      return desc ? ordering.reverse() : ordering;
   }

   public BlockDataSource(Seq<RDDPartitionInfo> rddPartitions, int pageSize, String sortColumn, boolean desc) {
      super(pageSize);
      this.data = (Seq)((SeqLike)rddPartitions.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BlockDataSource $outer;

         public final BlockTableRowData apply(RDDPartitionInfo rddPartition) {
            return this.$outer.org$apache$spark$ui$storage$BlockDataSource$$blockRow(rddPartition);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom())).sorted(this.ordering(sortColumn, desc));
   }
}
