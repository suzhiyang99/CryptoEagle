package org.apache.spark;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.util.Utils$;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\t4A!\u0001\u0002\u0001\u0013\t!2+\u001a:jC2L'0\u00192mK^\u0013\u0018\u000e^1cY\u0016T!a\u0001\u0003\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u00151\u0011AB1qC\u000eDWMC\u0001\b\u0003\ry'oZ\u0002\u0001+\tQadE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0018\u001b\u0005\u0019\"B\u0001\u000b\u0016\u0003\tIwNC\u0001\u0017\u0003\u0011Q\u0017M^1\n\u0005a\u0019\"\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\u000e\u0001\u0005\u0003\u0007I\u0011A\u000e\u0002\u0003Q,\u0012\u0001\b\t\u0003;ya\u0001\u0001B\u0003 \u0001\t\u0007\u0001EA\u0001U#\t\tC\u0005\u0005\u0002\rE%\u00111%\u0004\u0002\b\u001d>$\b.\u001b8h!\t)\u0013&D\u0001'\u0015\t!rE\u0003\u0002)\t\u00051\u0001.\u00193p_BL!A\u000b\u0014\u0003\u0011]\u0013\u0018\u000e^1cY\u0016D\u0001\u0002\f\u0001\u0003\u0002\u0004%\t!L\u0001\u0006i~#S-\u001d\u000b\u0003]E\u0002\"\u0001D\u0018\n\u0005Aj!\u0001B+oSRDqAM\u0016\u0002\u0002\u0003\u0007A$A\u0002yIEB\u0001\u0002\u000e\u0001\u0003\u0002\u0003\u0006K\u0001H\u0001\u0003i\u0002B#a\r\u001c\u0011\u000519\u0014B\u0001\u001d\u000e\u0005%!(/\u00198tS\u0016tG\u000fC\u0003;\u0001\u0011\u00051(\u0001\u0004=S:LGO\u0010\u000b\u0003yy\u00022!\u0010\u0001\u001d\u001b\u0005\u0011\u0001\"\u0002\u000e:\u0001\u0004a\u0002\"\u0002!\u0001\t\u0003Y\u0012!\u0002<bYV,\u0007\"\u0002\"\u0001\t\u0003\u001a\u0015\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\u0011\u0003\"!\u0012%\u000f\u000511\u0015BA$\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011\u0011J\u0013\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\u001dk\u0001\"\u0002'\u0001\t\u0013i\u0015aC<sSR,wJ\u00196fGR$\"A\f(\t\u000b=[\u0005\u0019\u0001)\u0002\u0007=,H\u000f\u0005\u0002\u0013#&\u0011!k\u0005\u0002\u0013\u001f\nTWm\u0019;PkR\u0004X\u000f^*ue\u0016\fW\u000eC\u0003U\u0001\u0011%Q+\u0001\u0006sK\u0006$wJ\u00196fGR$\"A\f,\t\u000b]\u001b\u0006\u0019\u0001-\u0002\u0005%t\u0007C\u0001\nZ\u0013\tQ6CA\tPE*,7\r^%oaV$8\u000b\u001e:fC6D#\u0001\u0001/\u0011\u0005u\u0003W\"\u00010\u000b\u0005}\u0013\u0011AC1o]>$\u0018\r^5p]&\u0011\u0011M\u0018\u0002\r\t\u00164X\r\\8qKJ\f\u0005/\u001b"
)
public class SerializableWritable<T extends Writable> implements Serializable {
   private transient T t;

   public T t() {
      return this.t;
   }

   public void t_$eq(T x$1) {
      this.t = x$1;
   }

   public T value() {
      return (T)this.t();
   }

   public String toString() {
      return this.t().toString();
   }

   private void writeObject(ObjectOutputStream out) {
      Utils$.MODULE$.tryOrIOException(new scala.Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SerializableWritable $outer;
         private final ObjectOutputStream out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.out$1.defaultWriteObject();
            (new ObjectWritable(this.$outer.t())).write(this.out$1);
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
      Utils$.MODULE$.tryOrIOException(new scala.Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SerializableWritable $outer;
         private final ObjectInputStream in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.in$1.defaultReadObject();
            ObjectWritable ow = new ObjectWritable();
            ow.setConf(new Configuration(false));
            ow.readFields(this.in$1);
            this.$outer.t_$eq((Writable)ow.get());
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

   public SerializableWritable(T t) {
      this.t = t;
      super();
   }
}
