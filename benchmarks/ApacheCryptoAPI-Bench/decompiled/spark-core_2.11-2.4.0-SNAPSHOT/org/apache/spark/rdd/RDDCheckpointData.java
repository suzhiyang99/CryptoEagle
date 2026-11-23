package org.apache.spark.rdd;

import org.apache.spark.Partition;
import scala.Array$;
import scala.Enumeration;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005UaAB\u0001\u0003\u0003\u0003!!BA\tS\t\u0012\u001b\u0005.Z2la>Lg\u000e\u001e#bi\u0006T!a\u0001\u0003\u0002\u0007I$GM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h+\tYadE\u0002\u0001\u0019I\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\u0007\u0014\u0013\t!bB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u0004\u0001\t\u0015\r\u0011\"\u0003\u0018\u0007\u0001)\u0012\u0001\u0007\t\u00043iaR\"\u0001\u0002\n\u0005m\u0011!a\u0001*E\tB\u0011QD\b\u0007\u0001\t\u0015y\u0002A1\u0001!\u0005\u0005!\u0016CA\u0011%!\ti!%\u0003\u0002$\u001d\t9aj\u001c;iS:<\u0007CA\u0007&\u0013\t1cBA\u0002B]fD\u0001\u0002\u000b\u0001\u0003\u0002\u0003\u0006I\u0001G\u0001\u0005e\u0012$\u0007\u0005\u000b\u0002(UA\u0011QbK\u0005\u0003Y9\u0011\u0011\u0002\u001e:b]NLWM\u001c;\t\u00119\u0002!1!Q\u0001\f=\n!\"\u001a<jI\u0016t7-\u001a\u00132!\r\u00014\u0007H\u0007\u0002c)\u0011!GD\u0001\be\u00164G.Z2u\u0013\t!\u0014G\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011\u00151\u0004\u0001\"\u00018\u0003\u0019a\u0014N\\5u}Q\u0011\u0001h\u000f\u000b\u0003si\u00022!\u0007\u0001\u001d\u0011\u0015qS\u0007q\u00010\u0011\u0015\u0019Q\u00071\u0001\u0019\u0011\u001di\u0004\u00011A\u0005\u0012y\nqa\u00199Ti\u0006$X-F\u0001@!\t\u00015I\u0004\u0002\u001a\u0003&\u0011!IA\u0001\u0010\u0007\",7m\u001b9pS:$8\u000b^1uK&\u0011A)\u0012\u0002\u0006-\u0006dW/Z\u0005\u0003\r:\u00111\"\u00128v[\u0016\u0014\u0018\r^5p]\"9\u0001\n\u0001a\u0001\n#I\u0015aC2q'R\fG/Z0%KF$\"AS'\u0011\u00055Y\u0015B\u0001'\u000f\u0005\u0011)f.\u001b;\t\u000f9;\u0015\u0011!a\u0001\u007f\u0005\u0019\u0001\u0010J\u0019\t\rA\u0003\u0001\u0015)\u0003@\u0003!\u0019\u0007o\u0015;bi\u0016\u0004\u0003b\u0002*\u0001\u0001\u0004%IaU\u0001\u0006GB\u0014F\tR\u000b\u0002)B\u0019Q\"V,\n\u0005Ys!AB(qi&|g\u000eE\u0002\u001a1rI!!\u0017\u0002\u0003\u001b\rCWmY6q_&tGO\u0015#E\u0011\u001dY\u0006\u00011A\u0005\nq\u000b\u0011b\u00199S\t\u0012{F%Z9\u0015\u0005)k\u0006b\u0002([\u0003\u0003\u0005\r\u0001\u0016\u0005\u0007?\u0002\u0001\u000b\u0015\u0002+\u0002\r\r\u0004(\u000b\u0012#!\u0011\u0015\t\u0007\u0001\"\u0001c\u00039I7o\u00115fG.\u0004x.\u001b8uK\u0012,\u0012a\u0019\t\u0003\u001b\u0011L!!\u001a\b\u0003\u000f\t{w\u000e\\3b]\")q\r\u0001C\u0003Q\u0006Q1\r[3dWB|\u0017N\u001c;\u0015\u0003)CQA\u001b\u0001\u0007\u0012-\fA\u0002Z8DQ\u0016\u001c7\u000e]8j]R$\u0012a\u0016\u0005\u0006[\u0002!\taU\u0001\u000eG\",7m\u001b9pS:$(\u000b\u0012#\t\u000b=\u0004A\u0011\u00019\u0002\u001b\u001d,G\u000fU1si&$\u0018n\u001c8t+\u0005\t\bcA\u0007si&\u00111O\u0004\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003kZl\u0011\u0001B\u0005\u0003o\u0012\u0011\u0011\u0002U1si&$\u0018n\u001c8\b\re\u0014\u0001\u0012\u0001\u0003{\u0003E\u0011F\tR\"iK\u000e\\\u0007o\\5oi\u0012\u000bG/\u0019\t\u00033m4a!\u0001\u0002\t\u0002\u0011a8cA>\r%!)ag\u001fC\u0001}R\t!\u0010C\u0005\u0002\u0002m\f\t\u0011\"\u0003\u0002\u0004\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t)\u0001\u0005\u0003\u0002\b\u0005EQBAA\u0005\u0015\u0011\tY!!\u0004\u0002\t1\fgn\u001a\u0006\u0003\u0003\u001f\tAA[1wC&!\u00111CA\u0005\u0005\u0019y%M[3di\u0002"
)
public abstract class RDDCheckpointData<T> implements Serializable {
   private final transient RDD<T> rdd;
   private Enumeration.Value cpState;
   private Option<CheckpointRDD<T>> cpRDD;

   private RDD<T> rdd() {
      return this.rdd;
   }

   public Enumeration.Value cpState() {
      return this.cpState;
   }

   public void cpState_$eq(Enumeration.Value x$1) {
      this.cpState = x$1;
   }

   private Option<CheckpointRDD<T>> cpRDD() {
      return this.cpRDD;
   }

   private void cpRDD_$eq(Option<CheckpointRDD<T>> x$1) {
      this.cpRDD = x$1;
   }

   public boolean isCheckpointed() {
      Boolean var2;
      synchronized(RDDCheckpointData$.MODULE$) {
         boolean var5;
         label22: {
            label21: {
               Enumeration.Value var10000 = this.cpState();
               Enumeration.Value var3 = CheckpointState$.MODULE$.Checkpointed();
               if (var10000 == null) {
                  if (var3 == null) {
                     break label21;
                  }
               } else if (var10000.equals(var3)) {
                  break label21;
               }

               var5 = false;
               break label22;
            }

            var5 = true;
         }

         var2 = BoxesRunTime.boxToBoolean(var5);
      }

      return BoxesRunTime.unboxToBoolean(var2);
   }

   public final void checkpoint() {
      synchronized(RDDCheckpointData$.MODULE$) {
         label27: {
            Enumeration.Value var10000 = this.cpState();
            Enumeration.Value var2 = CheckpointState$.MODULE$.Initialized();
            if (var10000 == null) {
               if (var2 == null) {
                  break label27;
               }
            } else if (var10000.equals(var2)) {
               break label27;
            }

            return;
         }

         this.cpState_$eq(CheckpointState$.MODULE$.CheckpointingInProgress());
         BoxedUnit var7 = BoxedUnit.UNIT;
      }

      CheckpointRDD newRDD = this.doCheckpoint();
      synchronized(RDDCheckpointData$.MODULE$) {
         this.cpRDD_$eq(new Some(newRDD));
         this.cpState_$eq(CheckpointState$.MODULE$.Checkpointed());
         this.rdd().markCheckpointed();
         BoxedUnit var8 = BoxedUnit.UNIT;
      }
   }

   public abstract CheckpointRDD<T> doCheckpoint();

   public Option<CheckpointRDD<T>> checkpointRDD() {
      Option var2;
      synchronized(RDDCheckpointData$.MODULE$) {
         var2 = this.cpRDD();
      }

      return var2;
   }

   public Partition[] getPartitions() {
      Object var2;
      synchronized(RDDCheckpointData$.MODULE$) {
         var2 = this.cpRDD().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Partition[] apply(CheckpointRDD<T> x$1) {
               return x$1.partitions();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Partition[] apply() {
               return (Partition[])Array$.MODULE$.empty(ClassTag$.MODULE$.apply(Partition.class));
            }
         });
      }

      return (Partition[])var2;
   }

   public RDDCheckpointData(RDD<T> rdd, ClassTag<T> evidence$1) {
      this.rdd = rdd;
      this.cpState = CheckpointState$.MODULE$.Initialized();
      this.cpRDD = None$.MODULE$;
   }
}
