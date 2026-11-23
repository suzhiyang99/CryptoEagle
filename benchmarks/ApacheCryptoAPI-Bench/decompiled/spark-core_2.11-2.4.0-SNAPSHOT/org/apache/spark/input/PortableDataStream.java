package org.apache.spark.input;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import org.spark_project.guava.io.ByteStreams;
import org.spark_project.guava.io.Closeables;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]a\u0001B\u0001\u0003\u0001-\u0011!\u0003U8si\u0006\u0014G.\u001a#bi\u0006\u001cFO]3b[*\u00111\u0001B\u0001\u0006S:\u0004X\u000f\u001e\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011c\u0001\u0001\r%A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"!D\n\n\u0005Qq!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\f\u0001\u0005\u0003\u0005\u000b\u0011B\f\u0002\r%\u001c\b\u000f\\5u!\tA\u0002%D\u0001\u001a\u0015\t\u0019!D\u0003\u0002\u001c9\u0005\u0019A.\u001b2\u000b\u0005uq\u0012!C7baJ,G-^2f\u0015\tyb!\u0001\u0004iC\u0012|w\u000e]\u0005\u0003Ce\u0011\u0001cQ8nE&tWMR5mKN\u0003H.\u001b;\t\u0011\r\u0002!\u0011!Q\u0001\n\u0011\nqaY8oi\u0016DH\u000f\u0005\u0002&M5\tA$\u0003\u0002(9\t\u0011B+Y:l\u0003R$X-\u001c9u\u0007>tG/\u001a=u\u0011!I\u0003A!A!\u0002\u0013Q\u0013!B5oI\u0016D\bCA\u00161\u001b\u0005a#BA\u0017/\u0003\u0011a\u0017M\\4\u000b\u0003=\nAA[1wC&\u0011\u0011\u0007\f\u0002\b\u0013:$XmZ3s\u0011\u0015\u0019\u0004\u0001\"\u00015\u0003\u0019a\u0014N\\5u}Q!Qg\u000e\u001d:!\t1\u0004!D\u0001\u0003\u0011\u00151\"\u00071\u0001\u0018\u0011\u0015\u0019#\u00071\u0001%\u0011\u0015I#\u00071\u0001+\u0011\u001dY\u0004A1A\u0005\nq\n\u0011bY8oM\nKH/Z:\u0016\u0003u\u00022!\u0004 A\u0013\tydBA\u0003BeJ\f\u0017\u0010\u0005\u0002\u000e\u0003&\u0011!I\u0004\u0002\u0005\u0005f$X\r\u0003\u0004E\u0001\u0001\u0006I!P\u0001\u000bG>tgMQ=uKN\u0004\u0003b\u0002$\u0001\u0005\u0004%I\u0001P\u0001\u000bgBd\u0017\u000e\u001e\"zi\u0016\u001c\bB\u0002%\u0001A\u0003%Q(A\u0006ta2LGOQ=uKN\u0004\u0003\u0002\u0003&\u0001\u0011\u000b\u0007I\u0011B&\u0002\u000bM\u0004H.\u001b;\u0016\u0003]A\u0001\"\u0014\u0001\t\u0002\u0003\u0006KaF\u0001\u0007gBd\u0017\u000e\u001e\u0011)\u00051{\u0005CA\u0007Q\u0013\t\tfBA\u0005ue\u0006t7/[3oi\"A1\u000b\u0001EC\u0002\u0013%A+\u0001\u0003d_:4W#A+\u0011\u0005YCV\"A,\u000b\u0005Ms\u0012BA-X\u00055\u0019uN\u001c4jOV\u0014\u0018\r^5p]\"A1\f\u0001E\u0001B\u0003&Q+A\u0003d_:4\u0007\u0005\u000b\u0002[\u001f\"Aa\f\u0001EC\u0002\u0013%q,\u0001\u0003qCRDW#\u00011\u0011\u0005-\n\u0017B\u00012-\u0005\u0019\u0019FO]5oO\"AA\r\u0001E\u0001B\u0003&\u0001-A\u0003qCRD\u0007\u0005\u000b\u0002d\u001f\")q\r\u0001C\u0001Q\u0006!q\u000e]3o)\u0005I\u0007C\u00016n\u001b\u0005Y'B\u00017/\u0003\tIw.\u0003\u0002oW\nyA)\u0019;b\u0013:\u0004X\u000f^*ue\u0016\fW\u000eK\u0002gaZ\u0004\"!\u001d;\u000e\u0003IT!a\u001d\u0003\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002ve\n)1+\u001b8dK\u0006\nq/A\u00032]Ir\u0003\u0007C\u0003z\u0001\u0011\u0005!0A\u0004u_\u0006\u0013(/Y=\u0015\u0003uB3\u0001\u001f9w\u0011\u0015i\b\u0001\"\u0001\u007f\u0003\u001d9W\r\u001e)bi\"$\u0012a \t\u0005\u0003\u0003\t9AD\u0002\u000e\u0003\u0007I1!!\u0002\u000f\u0003\u0019\u0001&/\u001a3fM&\u0019!-!\u0003\u000b\u0007\u0005\u0015a\u0002K\u0002}aZDa!a\u0004\u0001\t\u0003!\u0016\u0001E4fi\u000e{gNZ5hkJ\fG/[8oQ\u0015\ti\u0001]A\nC\t\t)\"A\u00033]Ir\u0003\u0007"
)
public class PortableDataStream implements Serializable {
   private final Integer index;
   private final byte[] confBytes;
   private final byte[] splitBytes;
   private transient CombineFileSplit split;
   private transient Configuration conf;
   private transient String path;
   private transient volatile byte bitmap$trans$0;

   private CombineFileSplit split$lzycompute() {
      synchronized(this) {
         if ((byte)(this.bitmap$trans$0 & 1) == 0) {
            ByteArrayInputStream bais = new ByteArrayInputStream(this.splitBytes());
            CombineFileSplit nsplit = new CombineFileSplit();
            nsplit.readFields(new DataInputStream(bais));
            this.split = nsplit;
            this.bitmap$trans$0 = (byte)(this.bitmap$trans$0 | 1);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.split;
   }

   private Configuration conf$lzycompute() {
      synchronized(this) {
         if ((byte)(this.bitmap$trans$0 & 2) == 0) {
            ByteArrayInputStream bais = new ByteArrayInputStream(this.confBytes());
            Configuration nconf = new Configuration();
            nconf.readFields(new DataInputStream(bais));
            this.conf = nconf;
            this.bitmap$trans$0 = (byte)(this.bitmap$trans$0 | 2);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.conf;
   }

   private String path$lzycompute() {
      synchronized(this) {
         if ((byte)(this.bitmap$trans$0 & 4) == 0) {
            Path pathp = this.split().getPath(Predef$.MODULE$.Integer2int(this.index));
            this.path = pathp.toString();
            this.bitmap$trans$0 = (byte)(this.bitmap$trans$0 | 4);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.path;
   }

   private byte[] confBytes() {
      return this.confBytes;
   }

   private byte[] splitBytes() {
      return this.splitBytes;
   }

   private CombineFileSplit split() {
      return (byte)(this.bitmap$trans$0 & 1) == 0 ? this.split$lzycompute() : this.split;
   }

   private Configuration conf() {
      return (byte)(this.bitmap$trans$0 & 2) == 0 ? this.conf$lzycompute() : this.conf;
   }

   private String path() {
      return (byte)(this.bitmap$trans$0 & 4) == 0 ? this.path$lzycompute() : this.path;
   }

   public DataInputStream open() {
      Path pathp = this.split().getPath(Predef$.MODULE$.Integer2int(this.index));
      FileSystem fs = pathp.getFileSystem(this.conf());
      return fs.open(pathp);
   }

   public byte[] toArray() {
      DataInputStream stream = this.open();

      byte[] var10000;
      try {
         var10000 = ByteStreams.toByteArray(stream);
      } finally {
         Closeables.close(stream, true);
      }

      return var10000;
   }

   public String getPath() {
      return this.path();
   }

   public Configuration getConfiguration() {
      return this.conf();
   }

   public PortableDataStream(CombineFileSplit isplit, TaskAttemptContext context, Integer index) {
      this.index = index;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      context.getConfiguration().write(new DataOutputStream(baos));
      this.confBytes = baos.toByteArray();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      isplit.write(new DataOutputStream(baos));
      this.splitBytes = baos.toByteArray();
   }
}
