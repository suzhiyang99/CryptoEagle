package org.apache.spark.util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.hadoop.conf.Configuration;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%3Q!\u0001\u0002\u0001\t)\u0011\u0011dU3sS\u0006d\u0017N_1cY\u0016\u001cuN\u001c4jOV\u0014\u0018\r^5p]*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\"\u0005\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00051\u0011\u0012BA\n\u000e\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!)\u0002A!a\u0001\n\u00039\u0012!\u0002<bYV,7\u0001A\u000b\u00021A\u0011\u0011DH\u0007\u00025)\u00111\u0004H\u0001\u0005G>tgM\u0003\u0002\u001e\r\u00051\u0001.\u00193p_BL!a\b\u000e\u0003\u001b\r{gNZ5hkJ\fG/[8o\u0011!\t\u0003A!a\u0001\n\u0003\u0011\u0013!\u0003<bYV,w\fJ3r)\t\u0019c\u0005\u0005\u0002\rI%\u0011Q%\u0004\u0002\u0005+:LG\u000fC\u0004(A\u0005\u0005\t\u0019\u0001\r\u0002\u0007a$\u0013\u0007\u0003\u0005*\u0001\t\u0005\t\u0015)\u0003\u0019\u0003\u00191\u0018\r\\;fA!\u0012\u0001f\u000b\t\u0003\u00191J!!L\u0007\u0003\u0013Q\u0014\u0018M\\:jK:$\b\"B\u0018\u0001\t\u0003\u0001\u0014A\u0002\u001fj]&$h\b\u0006\u00022gA\u0011!\u0007A\u0007\u0002\u0005!)QC\fa\u00011!)Q\u0007\u0001C\u0005m\u0005YqO]5uK>\u0013'.Z2u)\t\u0019s\u0007C\u00039i\u0001\u0007\u0011(A\u0002pkR\u0004\"AO \u000e\u0003mR!\u0001P\u001f\u0002\u0005%|'\"\u0001 \u0002\t)\fg/Y\u0005\u0003\u0001n\u0012!c\u00142kK\u000e$x*\u001e;qkR\u001cFO]3b[\")!\t\u0001C\u0005\u0007\u0006Q!/Z1e\u001f\nTWm\u0019;\u0015\u0005\r\"\u0005\"B#B\u0001\u00041\u0015AA5o!\tQt)\u0003\u0002Iw\t\trJ\u00196fGRLe\u000e];u'R\u0014X-Y7"
)
public class SerializableConfiguration implements Serializable {
   private transient Configuration value;

   public Configuration value() {
      return this.value;
   }

   public void value_$eq(Configuration x$1) {
      this.value = x$1;
   }

   private void writeObject(ObjectOutputStream out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SerializableConfiguration $outer;
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
         private final SerializableConfiguration $outer;
         private final ObjectInputStream in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.value_$eq(new Configuration(false));
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

   public SerializableConfiguration(Configuration value) {
      this.value = value;
      super();
   }
}
