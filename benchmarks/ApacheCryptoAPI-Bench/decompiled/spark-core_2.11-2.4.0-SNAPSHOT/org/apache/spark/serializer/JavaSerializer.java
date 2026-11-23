package org.apache.spark.serializer;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.spark.SparkConf;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.util.Utils$;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u00054A!\u0001\u0002\u0001\u0017\tq!*\u0019<b'\u0016\u0014\u0018.\u00197ju\u0016\u0014(BA\u0002\u0005\u0003)\u0019XM]5bY&TXM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011c\u0001\u0001\r!A\u0011QBD\u0007\u0002\u0005%\u0011qB\u0001\u0002\u000b'\u0016\u0014\u0018.\u00197ju\u0016\u0014\bCA\t\u0017\u001b\u0005\u0011\"BA\n\u0015\u0003\tIwNC\u0001\u0016\u0003\u0011Q\u0017M^1\n\u0005]\u0011\"AD#yi\u0016\u0014h.\u00197ju\u0006\u0014G.\u001a\u0005\t3\u0001\u0011\t\u0011)A\u00055\u0005!1m\u001c8g!\tYB$D\u0001\u0005\u0013\tiBAA\u0005Ta\u0006\u00148nQ8oM\")q\u0004\u0001C\u0001A\u00051A(\u001b8jiz\"\"!\t\u0012\u0011\u00055\u0001\u0001\"B\r\u001f\u0001\u0004Q\u0002b\u0002\u0013\u0001\u0001\u0004%I!J\u0001\rG>,h\u000e^3s%\u0016\u001cX\r^\u000b\u0002MA\u0011qEK\u0007\u0002Q)\t\u0011&A\u0003tG\u0006d\u0017-\u0003\u0002,Q\t\u0019\u0011J\u001c;\t\u000f5\u0002\u0001\u0019!C\u0005]\u0005\u00012m\\;oi\u0016\u0014(+Z:fi~#S-\u001d\u000b\u0003_I\u0002\"a\n\u0019\n\u0005EB#\u0001B+oSRDqa\r\u0017\u0002\u0002\u0003\u0007a%A\u0002yIEBa!\u000e\u0001!B\u00131\u0013!D2pk:$XM\u001d*fg\u0016$\b\u0005C\u00048\u0001\u0001\u0007I\u0011\u0002\u001d\u0002\u001d\u0015DHO]1EK\n,x-\u00138g_V\t\u0011\b\u0005\u0002(u%\u00111\b\u000b\u0002\b\u0005>|G.Z1o\u0011\u001di\u0004\u00011A\u0005\ny\n!#\u001a=ue\u0006$UMY;h\u0013:4wn\u0018\u0013fcR\u0011qf\u0010\u0005\bgq\n\t\u00111\u0001:\u0011\u0019\t\u0005\u0001)Q\u0005s\u0005yQ\r\u001f;sC\u0012+'-^4J]\u001a|\u0007\u0005C\u0003 \u0001\u0011E1\tF\u0001\"\u0011\u0015)\u0005\u0001\"\u0011G\u0003-qWm^%ogR\fgnY3\u0015\u0003\u001d\u0003\"!\u0004%\n\u0005%\u0013!AE*fe&\fG.\u001b>fe&s7\u000f^1oG\u0016DQa\u0013\u0001\u0005B1\u000bQb\u001e:ji\u0016,\u0005\u0010^3s]\u0006dGCA\u0018N\u0011\u0015q%\n1\u0001P\u0003\ryW\u000f\u001e\t\u0003#AK!!\u0015\n\u0003\u0019=\u0013'.Z2u\u001fV$\b/\u001e;\t\u000bM\u0003A\u0011\t+\u0002\u0019I,\u0017\rZ#yi\u0016\u0014h.\u00197\u0015\u0005=*\u0006\"\u0002,S\u0001\u00049\u0016AA5o!\t\t\u0002,\u0003\u0002Z%\tYqJ\u00196fGRLe\u000e];uQ\t\u00011\f\u0005\u0002]?6\tQL\u0003\u0002_\t\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005\u0001l&\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007"
)
public class JavaSerializer extends Serializer implements Externalizable {
   private int org$apache$spark$serializer$JavaSerializer$$counterReset;
   private boolean org$apache$spark$serializer$JavaSerializer$$extraDebugInfo;

   public int org$apache$spark$serializer$JavaSerializer$$counterReset() {
      return this.org$apache$spark$serializer$JavaSerializer$$counterReset;
   }

   public void org$apache$spark$serializer$JavaSerializer$$counterReset_$eq(int x$1) {
      this.org$apache$spark$serializer$JavaSerializer$$counterReset = x$1;
   }

   public boolean org$apache$spark$serializer$JavaSerializer$$extraDebugInfo() {
      return this.org$apache$spark$serializer$JavaSerializer$$extraDebugInfo;
   }

   public void org$apache$spark$serializer$JavaSerializer$$extraDebugInfo_$eq(boolean x$1) {
      this.org$apache$spark$serializer$JavaSerializer$$extraDebugInfo = x$1;
   }

   public SerializerInstance newInstance() {
      ClassLoader classLoader = (ClassLoader)this.defaultClassLoader().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ClassLoader apply() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
      return new JavaSerializerInstance(this.org$apache$spark$serializer$JavaSerializer$$counterReset(), this.org$apache$spark$serializer$JavaSerializer$$extraDebugInfo(), classLoader);
   }

   public void writeExternal(ObjectOutput out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final JavaSerializer $outer;
         private final ObjectOutput out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.out$1.writeInt(this.$outer.org$apache$spark$serializer$JavaSerializer$$counterReset());
            this.out$1.writeBoolean(this.$outer.org$apache$spark$serializer$JavaSerializer$$extraDebugInfo());
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
         private final JavaSerializer $outer;
         private final ObjectInput in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.org$apache$spark$serializer$JavaSerializer$$counterReset_$eq(this.in$1.readInt());
            this.$outer.org$apache$spark$serializer$JavaSerializer$$extraDebugInfo_$eq(this.in$1.readBoolean());
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

   public JavaSerializer(SparkConf conf) {
      this.org$apache$spark$serializer$JavaSerializer$$counterReset = conf.getInt("spark.serializer.objectStreamReset", 100);
      this.org$apache$spark$serializer$JavaSerializer$$extraDebugInfo = conf.getBoolean("spark.serializer.extraDebugInfo", true);
   }

   public JavaSerializer() {
      this(new SparkConf());
   }
}
