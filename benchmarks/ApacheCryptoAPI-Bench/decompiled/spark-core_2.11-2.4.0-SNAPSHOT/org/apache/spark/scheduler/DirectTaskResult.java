package org.apache.spark.scheduler;

import java.io.DataOutput;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import org.apache.spark.SparkEnv$;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.Utils$;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Md!B\u0001\u0003\u0001\u0011Q!\u0001\u0005#je\u0016\u001cG\u000fV1tWJ+7/\u001e7u\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x-\u0006\u0002\f1M!\u0001\u0001\u0004\n#!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u00191\u0003\u0006\f\u000e\u0003\tI!!\u0006\u0002\u0003\u0015Q\u000b7o\u001b*fgVdG\u000f\u0005\u0002\u001811\u0001A!B\r\u0001\u0005\u0004Y\"!\u0001+\u0004\u0001E\u0011Ad\b\t\u0003\u001buI!A\b\b\u0003\u000f9{G\u000f[5oOB\u0011Q\u0002I\u0005\u0003C9\u00111!\u00118z!\t\u0019\u0003&D\u0001%\u0015\t)c%\u0001\u0002j_*\tq%\u0001\u0003kCZ\f\u0017BA\u0015%\u00059)\u0005\u0010^3s]\u0006d\u0017N_1cY\u0016D\u0001b\u000b\u0001\u0003\u0002\u0004%\t\u0001L\u0001\u000bm\u0006dW/\u001a\"zi\u0016\u001cX#A\u0017\u0011\u00059\nT\"A\u0018\u000b\u0005A2\u0013a\u00018j_&\u0011!g\f\u0002\u000b\u0005f$XMQ;gM\u0016\u0014\b\u0002\u0003\u001b\u0001\u0005\u0003\u0007I\u0011A\u001b\u0002\u001dY\fG.^3CsR,7o\u0018\u0013fcR\u0011a'\u000f\t\u0003\u001b]J!\u0001\u000f\b\u0003\tUs\u0017\u000e\u001e\u0005\buM\n\t\u00111\u0001.\u0003\rAH%\r\u0005\ty\u0001\u0011\t\u0011)Q\u0005[\u0005Ya/\u00197vK\nKH/Z:!\u0011!q\u0004A!a\u0001\n\u0003y\u0014\u0001D1dGVlW\u000b\u001d3bi\u0016\u001cX#\u0001!\u0011\u0007\u0005KEJ\u0004\u0002C\u000f:\u00111IR\u0007\u0002\t*\u0011QIG\u0001\u0007yI|w\u000e\u001e \n\u0003=I!\u0001\u0013\b\u0002\u000fA\f7m[1hK&\u0011!j\u0013\u0002\u0004'\u0016\f(B\u0001%\u000fa\riE+\u0019\t\u0005\u001dF\u001b\u0006-D\u0001P\u0015\t\u0001F!\u0001\u0003vi&d\u0017B\u0001*P\u00055\t5mY;nk2\fGo\u001c:WeA\u0011q\u0003\u0016\u0003\n+Z\u000b\t\u0011!A\u0003\u0002m\u00111a\u0018\u00132\u0011!9\u0006A!A!B\u0013A\u0016!D1dGVlW\u000b\u001d3bi\u0016\u001c\b\u0005E\u0002B\u0013f\u00034A\u0017/_!\u0011q\u0015kW/\u0011\u0005]aF!C+W\u0003\u0003\u0005\tQ!\u0001\u001c!\t9b\fB\u0005`-\u0006\u0005\t\u0011!B\u00017\t\u0019q\f\n\u001a\u0011\u0005]\tG!C0W\u0003\u0003\u0005\tQ!\u0001\u001c\u0011!\u0019\u0007A!a\u0001\n\u0003!\u0017\u0001E1dGVlW\u000b\u001d3bi\u0016\u001cx\fJ3r)\t1T\rC\u0004;E\u0006\u0005\t\u0019\u00014\u0011\u0007\u0005Ku\rM\u0002iU2\u0004BAT)jWB\u0011qC\u001b\u0003\n+Z\u000b\t\u0011!A\u0003\u0002m\u0001\"a\u00067\u0005\u0013}3\u0016\u0011!A\u0001\u0006\u0003Y\u0002\"\u00028\u0001\t\u0003y\u0017A\u0002\u001fj]&$h\bF\u0002qcJ\u00042a\u0005\u0001\u0017\u0011\u0015YS\u000e1\u0001.\u0011\u0015qT\u000e1\u0001t!\r\t\u0015\n\u001e\u0019\u0004k^L\b\u0003\u0002(Rmb\u0004\"aF<\u0005\u0013U\u0013\u0018\u0011!A\u0001\u0006\u0003Y\u0002CA\fz\t%y&/!A\u0001\u0002\u000b\u00051\u0004C\u0004|\u0001\u0001\u0007I\u0011\u0002?\u0002/Y\fG.^3PE*,7\r\u001e#fg\u0016\u0014\u0018.\u00197ju\u0016$W#A?\u0011\u00055q\u0018BA@\u000f\u0005\u001d\u0011un\u001c7fC:D\u0011\"a\u0001\u0001\u0001\u0004%I!!\u0002\u00027Y\fG.^3PE*,7\r\u001e#fg\u0016\u0014\u0018.\u00197ju\u0016$w\fJ3r)\r1\u0014q\u0001\u0005\tu\u0005\u0005\u0011\u0011!a\u0001{\"9\u00111\u0002\u0001!B\u0013i\u0018\u0001\u0007<bYV,wJ\u00196fGR$Um]3sS\u0006d\u0017N_3eA!Y\u0011q\u0002\u0001A\u0002\u0003\u0007I\u0011BA\t\u0003-1\u0018\r\\;f\u001f\nTWm\u0019;\u0016\u0003YA1\"!\u0006\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u0002\u0018\u0005ya/\u00197vK>\u0013'.Z2u?\u0012*\u0017\u000fF\u00027\u00033A\u0001BOA\n\u0003\u0003\u0005\rA\u0006\u0005\b\u0003;\u0001\u0001\u0015)\u0003\u0017\u000311\u0018\r\\;f\u001f\nTWm\u0019;!\u0011\u0019q\u0007\u0001\"\u0001\u0002\"Q\t\u0001\u000fC\u0004\u0002&\u0001!\t%a\n\u0002\u001b]\u0014\u0018\u000e^3FqR,'O\\1m)\r1\u0014\u0011\u0006\u0005\t\u0003W\t\u0019\u00031\u0001\u0002.\u0005\u0019q.\u001e;\u0011\u0007\r\ny#C\u0002\u00022\u0011\u0012Ab\u00142kK\u000e$x*\u001e;qkRDq!!\u000e\u0001\t\u0003\n9$\u0001\u0007sK\u0006$W\t\u001f;fe:\fG\u000eF\u00027\u0003sA\u0001\"a\u000f\u00024\u0001\u0007\u0011QH\u0001\u0003S:\u00042aIA \u0013\r\t\t\u0005\n\u0002\f\u001f\nTWm\u0019;J]B,H\u000fC\u0004\u0002F\u0001!\t!a\u0012\u0002\u000bY\fG.^3\u0015\u0007Y\tI\u0005\u0003\u0006\u0002L\u0005\r\u0003\u0013!a\u0001\u0003\u001b\n\u0011B]3tk2$8+\u001a:\u0011\t\u0005=\u0013QK\u0007\u0003\u0003#R1!a\u0015\u0005\u0003)\u0019XM]5bY&TXM]\u0005\u0005\u0003/\n\tF\u0001\nTKJL\u0017\r\\5{KJLen\u001d;b]\u000e,\u0007\"CA.\u0001E\u0005I\u0011AA/\u0003=1\u0018\r\\;fI\u0011,g-Y;mi\u0012\nTCAA0U\u0011\ti%!\u0019,\u0005\u0005\r\u0004\u0003BA3\u0003_j!!a\u001a\u000b\t\u0005%\u00141N\u0001\nk:\u001c\u0007.Z2lK\u0012T1!!\u001c\u000f\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003c\n9GA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016\u0004"
)
public class DirectTaskResult<T> implements TaskResult<T>, Externalizable {
   private ByteBuffer valueBytes;
   private Seq<AccumulatorV2<?, ?>> accumUpdates;
   private boolean org$apache$spark$scheduler$DirectTaskResult$$valueObjectDeserialized;
   private T valueObject;

   public ByteBuffer valueBytes() {
      return this.valueBytes;
   }

   public void valueBytes_$eq(ByteBuffer x$1) {
      this.valueBytes = x$1;
   }

   public Seq<AccumulatorV2<?, ?>> accumUpdates() {
      return this.accumUpdates;
   }

   public void accumUpdates_$eq(Seq<AccumulatorV2<?, ?>> x$1) {
      this.accumUpdates = x$1;
   }

   private boolean org$apache$spark$scheduler$DirectTaskResult$$valueObjectDeserialized() {
      return this.org$apache$spark$scheduler$DirectTaskResult$$valueObjectDeserialized;
   }

   public void org$apache$spark$scheduler$DirectTaskResult$$valueObjectDeserialized_$eq(boolean x$1) {
      this.org$apache$spark$scheduler$DirectTaskResult$$valueObjectDeserialized = x$1;
   }

   private T valueObject() {
      return this.valueObject;
   }

   private void valueObject_$eq(T x$1) {
      this.valueObject = (T)x$1;
   }

   public void writeExternal(ObjectOutput out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DirectTaskResult $outer;
         public final ObjectOutput out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.out$1.writeInt(this.$outer.valueBytes().remaining());
            Utils$.MODULE$.writeByteBuffer(this.$outer.valueBytes(), (DataOutput)this.out$1);
            this.out$1.writeInt(this.$outer.accumUpdates().size());
            this.$outer.accumUpdates().foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(Object x$1) {
                  this.$outer.out$1.writeObject(x$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.out$1 = out$1;
            }
         }
      });
   }

   public void readExternal(ObjectInput in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DirectTaskResult $outer;
         public final ObjectInput in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            int blen = this.in$1.readInt();
            byte[] byteVal = new byte[blen];
            this.in$1.readFully(byteVal);
            this.$outer.valueBytes_$eq(ByteBuffer.wrap(byteVal));
            int numUpdates = this.in$1.readInt();
            if (numUpdates == 0) {
               this.$outer.accumUpdates_$eq((Seq)Seq$.MODULE$.empty());
            } else {
               ArrayBuffer _accumUpdates = new ArrayBuffer();
               RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), numUpdates).foreach(new Serializable(this, _accumUpdates) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final ArrayBuffer _accumUpdates$1;

                  public final ArrayBuffer<AccumulatorV2<?, ?>> apply(int i) {
                     return this._accumUpdates$1.$plus$eq((AccumulatorV2)this.$outer.in$1.readObject());
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this._accumUpdates$1 = _accumUpdates$1;
                     }
                  }
               });
               this.$outer.accumUpdates_$eq(_accumUpdates);
            }

            this.$outer.org$apache$spark$scheduler$DirectTaskResult$$valueObjectDeserialized_$eq(false);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   public T value(SerializerInstance resultSer) {
      Object var10000;
      if (this.org$apache$spark$scheduler$DirectTaskResult$$valueObjectDeserialized()) {
         var10000 = this.valueObject();
      } else {
         SerializerInstance ser = resultSer == null ? SparkEnv$.MODULE$.get().serializer().newInstance() : resultSer;
         this.valueObject_$eq(ser.deserialize(this.valueBytes(), ClassTag$.MODULE$.Nothing()));
         this.org$apache$spark$scheduler$DirectTaskResult$$valueObjectDeserialized_$eq(true);
         var10000 = this.valueObject();
      }

      return (T)var10000;
   }

   public SerializerInstance value$default$1() {
      return null;
   }

   public DirectTaskResult(ByteBuffer valueBytes, Seq<AccumulatorV2<?, ?>> accumUpdates) {
      this.valueBytes = valueBytes;
      this.accumUpdates = accumUpdates;
      super();
      this.org$apache$spark$scheduler$DirectTaskResult$$valueObjectDeserialized = false;
   }

   public DirectTaskResult() {
      this((ByteBuffer)null, (Seq)null);
   }
}
