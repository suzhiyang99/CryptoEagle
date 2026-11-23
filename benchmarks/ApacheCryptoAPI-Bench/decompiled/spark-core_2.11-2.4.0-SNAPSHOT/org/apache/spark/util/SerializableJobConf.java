package org.apache.spark.util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.hadoop.mapred.JobConf;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%3Q!\u0001\u0002\u0001\t)\u00111cU3sS\u0006d\u0017N_1cY\u0016TuNY\"p]\u001aT!a\u0001\u0003\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\t\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\ta!#\u0003\u0002\u0014\u001b\ta1+\u001a:jC2L'0\u00192mK\"AQ\u0003\u0001BA\u0002\u0013\u0005q#A\u0003wC2,Xm\u0001\u0001\u0016\u0003a\u0001\"!\u0007\u0010\u000e\u0003iQ!a\u0007\u000f\u0002\r5\f\u0007O]3e\u0015\tib!\u0001\u0004iC\u0012|w\u000e]\u0005\u0003?i\u0011qAS8c\u0007>tg\r\u0003\u0005\"\u0001\t\u0005\r\u0011\"\u0001#\u0003%1\u0018\r\\;f?\u0012*\u0017\u000f\u0006\u0002$MA\u0011A\u0002J\u0005\u0003K5\u0011A!\u00168ji\"9q\u0005IA\u0001\u0002\u0004A\u0012a\u0001=%c!A\u0011\u0006\u0001B\u0001B\u0003&\u0001$\u0001\u0004wC2,X\r\t\u0015\u0003Q-\u0002\"\u0001\u0004\u0017\n\u00055j!!\u0003;sC:\u001c\u0018.\u001a8u\u0011\u0015y\u0003\u0001\"\u00011\u0003\u0019a\u0014N\\5u}Q\u0011\u0011g\r\t\u0003e\u0001i\u0011A\u0001\u0005\u0006+9\u0002\r\u0001\u0007\u0005\u0006k\u0001!IAN\u0001\foJLG/Z(cU\u0016\u001cG\u000f\u0006\u0002$o!)\u0001\b\u000ea\u0001s\u0005\u0019q.\u001e;\u0011\u0005izT\"A\u001e\u000b\u0005qj\u0014AA5p\u0015\u0005q\u0014\u0001\u00026bm\u0006L!\u0001Q\u001e\u0003%=\u0013'.Z2u\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\u0006\u0005\u0002!IaQ\u0001\u000be\u0016\fGm\u00142kK\u000e$HCA\u0012E\u0011\u0015)\u0015\t1\u0001G\u0003\tIg\u000e\u0005\u0002;\u000f&\u0011\u0001j\u000f\u0002\u0012\u001f\nTWm\u0019;J]B,Ho\u0015;sK\u0006l\u0007"
)
public class SerializableJobConf implements Serializable {
   private transient JobConf value;

   public JobConf value() {
      return this.value;
   }

   public void value_$eq(JobConf x$1) {
      this.value = x$1;
   }

   private void writeObject(ObjectOutputStream out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SerializableJobConf $outer;
         private final ObjectOutputStream out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.out$1.defaultWriteObject();
            this.$outer.value().write(this.out$1);
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

   private void readObject(ObjectInputStream in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SerializableJobConf $outer;
         private final ObjectInputStream in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.value_$eq(new JobConf(false));
            this.$outer.value().readFields(this.in$1);
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

   public SerializableJobConf(JobConf value) {
      this.value = value;
      super();
   }
}
