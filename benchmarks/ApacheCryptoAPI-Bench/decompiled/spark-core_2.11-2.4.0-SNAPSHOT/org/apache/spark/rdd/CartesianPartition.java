package org.apache.spark.rdd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import org.apache.spark.util.Utils$;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]a!B\u0001\u0003\u0001\u0011Q!AE\"beR,7/[1o!\u0006\u0014H/\u001b;j_:T!a\u0001\u0003\u0002\u0007I$GM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\"\u0005\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u0005I\u0019R\"\u0001\u0003\n\u0005Q!!!\u0003)beRLG/[8o\u0011!1\u0002A!A!\u0002\u0013A\u0012aA5eq\u000e\u0001\u0001C\u0001\u0007\u001a\u0013\tQRBA\u0002J]RD\u0001\u0002\b\u0001\u0003\u0006\u0004%I!H\u0001\u0005e\u0012$\u0017'F\u0001\u001fa\tyR\u0005E\u0002!C\rj\u0011AA\u0005\u0003E\t\u00111A\u0015#E!\t!S\u0005\u0004\u0001\u0005\u0013\u0019:\u0013\u0011!A\u0001\u0006\u0003i#aA0%c!A\u0001\u0006\u0001B\u0001B\u0003%\u0011&A\u0003sI\u0012\f\u0004\u0005\r\u0002+YA\u0019\u0001%I\u0016\u0011\u0005\u0011bC!\u0003\u0014(\u0003\u0003\u0005\tQ!\u0001.#\tq\u0013\u0007\u0005\u0002\r_%\u0011\u0001'\u0004\u0002\b\u001d>$\b.\u001b8h!\ta!'\u0003\u00024\u001b\t\u0019\u0011I\\=)\u0005\u001d*\u0004C\u0001\u00077\u0013\t9TBA\u0005ue\u0006t7/[3oi\"A\u0011\b\u0001BC\u0002\u0013%!(\u0001\u0003sI\u0012\u0014T#A\u001e1\u0005qr\u0004c\u0001\u0011\"{A\u0011AE\u0010\u0003\n\u007f\u0001\u000b\t\u0011!A\u0003\u00025\u00121a\u0018\u00133\u0011!\t\u0005A!A!\u0002\u0013\u0011\u0015!\u0002:eIJ\u0002\u0003GA\"F!\r\u0001\u0013\u0005\u0012\t\u0003I\u0015#\u0011b\u0010!\u0002\u0002\u0003\u0005)\u0011A\u0017)\u0005\u0001+\u0004\u0002\u0003%\u0001\u0005\u0003\u0005\u000b\u0011\u0002\r\u0002\u000fM\f\u0014J\u001c3fq\"A!\n\u0001B\u0001B\u0003%\u0001$A\u0004te%sG-\u001a=\t\u000b1\u0003A\u0011A'\u0002\rqJg.\u001b;?)\u0019qu\nU+[7B\u0011\u0001\u0005\u0001\u0005\u0006--\u0003\r\u0001\u0007\u0005\u00069-\u0003\r!\u0015\u0019\u0003%R\u00032\u0001I\u0011T!\t!C\u000bB\u0005'!\u0006\u0005\t\u0011!B\u0001[!)\u0011h\u0013a\u0001-B\u0012q+\u0017\t\u0004A\u0005B\u0006C\u0001\u0013Z\t%yT+!A\u0001\u0002\u000b\u0005Q\u0006C\u0003I\u0017\u0002\u0007\u0001\u0004C\u0003K\u0017\u0002\u0007\u0001\u0004C\u0004^\u0001\u0001\u0007I\u0011\u00010\u0002\u0005M\fT#A\t\t\u000f\u0001\u0004\u0001\u0019!C\u0001C\u000611/M0%KF$\"AY3\u0011\u00051\u0019\u0017B\u00013\u000e\u0005\u0011)f.\u001b;\t\u000f\u0019|\u0016\u0011!a\u0001#\u0005\u0019\u0001\u0010J\u0019\t\r!\u0004\u0001\u0015)\u0003\u0012\u0003\r\u0019\u0018\u0007\t\u0005\bU\u0002\u0001\r\u0011\"\u0001_\u0003\t\u0019(\u0007C\u0004m\u0001\u0001\u0007I\u0011A7\u0002\rM\u0014t\fJ3r)\t\u0011g\u000eC\u0004gW\u0006\u0005\t\u0019A\t\t\rA\u0004\u0001\u0015)\u0003\u0012\u0003\r\u0019(\u0007\t\u0005\be\u0002\u0011\r\u0011\"\u0011t\u0003\u0015Ig\u000eZ3y+\u0005A\u0002BB;\u0001A\u0003%\u0001$\u0001\u0004j]\u0012,\u0007\u0010\t\u0005\u0006o\u0002!I\u0001_\u0001\foJLG/Z(cU\u0016\u001cG\u000f\u0006\u0002cs\")!P\u001ea\u0001w\u0006\u0019qn\\:\u0011\u0007q\f\u0019!D\u0001~\u0015\tqx0\u0001\u0002j_*\u0011\u0011\u0011A\u0001\u0005U\u00064\u0018-C\u0002\u0002\u0006u\u0014!c\u00142kK\u000e$x*\u001e;qkR\u001cFO]3b[\"*a/!\u0003\u0002\u0016A)A\"a\u0003\u0002\u0010%\u0019\u0011QB\u0007\u0003\rQD'o\\<t!\ra\u0018\u0011C\u0005\u0004\u0003'i(aC%P\u000bb\u001cW\r\u001d;j_:\u001c#!a\u0004"
)
public class CartesianPartition implements Partition {
   private final transient RDD<?> org$apache$spark$rdd$CartesianPartition$$rdd1;
   private final transient RDD<?> org$apache$spark$rdd$CartesianPartition$$rdd2;
   public final int org$apache$spark$rdd$CartesianPartition$$s1Index;
   public final int org$apache$spark$rdd$CartesianPartition$$s2Index;
   private Partition s1;
   private Partition s2;
   private final int index;

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public int hashCode() {
      return Partition$class.hashCode(this);
   }

   public boolean equals(Object other) {
      return Partition$class.equals(this, other);
   }

   public RDD<?> org$apache$spark$rdd$CartesianPartition$$rdd1() {
      return this.org$apache$spark$rdd$CartesianPartition$$rdd1;
   }

   public RDD<?> org$apache$spark$rdd$CartesianPartition$$rdd2() {
      return this.org$apache$spark$rdd$CartesianPartition$$rdd2;
   }

   public Partition s1() {
      return this.s1;
   }

   public void s1_$eq(Partition x$1) {
      this.s1 = x$1;
   }

   public Partition s2() {
      return this.s2;
   }

   public void s2_$eq(Partition x$1) {
      this.s2 = x$1;
   }

   public int index() {
      return this.index;
   }

   private void writeObject(ObjectOutputStream oos) throws IOException {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, oos) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final CartesianPartition $outer;
         private final ObjectOutputStream oos$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.s1_$eq(this.$outer.org$apache$spark$rdd$CartesianPartition$$rdd1().partitions()[this.$outer.org$apache$spark$rdd$CartesianPartition$$s1Index]);
            this.$outer.s2_$eq(this.$outer.org$apache$spark$rdd$CartesianPartition$$rdd2().partitions()[this.$outer.org$apache$spark$rdd$CartesianPartition$$s2Index]);
            this.oos$1.defaultWriteObject();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.oos$1 = oos$1;
            }
         }
      });
   }

   public CartesianPartition(int idx, RDD<?> rdd1, RDD<?> rdd2, int s1Index, int s2Index) {
      this.org$apache$spark$rdd$CartesianPartition$$rdd1 = rdd1;
      this.org$apache$spark$rdd$CartesianPartition$$rdd2 = rdd2;
      this.org$apache$spark$rdd$CartesianPartition$$s1Index = s1Index;
      this.org$apache$spark$rdd$CartesianPartition$$s2Index = s2Index;
      Partition$class.$init$(this);
      this.s1 = rdd1.partitions()[s1Index];
      this.s2 = rdd2.partitions()[s2Index];
      this.index = idx;
   }
}
