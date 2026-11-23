package org.apache.spark.storage;

import scala.Enumeration;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00193Q!\u0001\u0002\u0001\t)\u00111B\u00117pG.\u0014Vm];mi*\u00111\u0001B\u0001\bgR|'/Y4f\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fM\"A!\u0003\u0001BC\u0002\u0013\u0005A#\u0001\u0003eCR\f7\u0001A\u000b\u0002+A\u0019aCH\u0011\u000f\u0005]abB\u0001\r\u001c\u001b\u0005I\"B\u0001\u000e\u0014\u0003\u0019a$o\\8u}%\ta\"\u0003\u0002\u001e\u001b\u00059\u0001/Y2lC\u001e,\u0017BA\u0010!\u0005!IE/\u001a:bi>\u0014(BA\u000f\u000e!\ta!%\u0003\u0002$\u001b\t\u0019\u0011I\\=\t\u0011\u0015\u0002!\u0011!Q\u0001\nU\tQ\u0001Z1uC\u0002B\u0001b\n\u0001\u0003\u0006\u0004%\t\u0001K\u0001\u000be\u0016\fG-T3uQ>$W#A\u0015\u0011\u0005)\u0002dBA\u0016/\u001b\u0005a#BA\u0017\u0005\u0003!)\u00070Z2vi>\u0014\u0018BA\u0018-\u00039!\u0015\r^1SK\u0006$W*\u001a;i_\u0012L!!\r\u001a\u0003\u000bY\u000bG.^3\n\u0005Mj!aC#ok6,'/\u0019;j_:D\u0001\"\u000e\u0001\u0003\u0002\u0003\u0006I!K\u0001\fe\u0016\fG-T3uQ>$\u0007\u0005\u0003\u00058\u0001\t\u0015\r\u0011\"\u00019\u0003\u0015\u0011\u0017\u0010^3t+\u0005I\u0004C\u0001\u0007;\u0013\tYTB\u0001\u0003M_:<\u0007\u0002C\u001f\u0001\u0005\u0003\u0005\u000b\u0011B\u001d\u0002\r\tLH/Z:!\u0011\u0015y\u0004\u0001\"\u0001A\u0003\u0019a\u0014N\\5u}Q!\u0011i\u0011#F!\t\u0011\u0005!D\u0001\u0003\u0011\u0015\u0011b\b1\u0001\u0016\u0011\u00159c\b1\u0001*\u0011\u00159d\b1\u0001:\u0001"
)
public class BlockResult {
   private final Iterator<Object> data;
   private final Enumeration.Value readMethod;
   private final long bytes;

   public Iterator<Object> data() {
      return this.data;
   }

   public Enumeration.Value readMethod() {
      return this.readMethod;
   }

   public long bytes() {
      return this.bytes;
   }

   public BlockResult(Iterator<Object> data, Enumeration.Value readMethod, long bytes) {
      this.data = data;
      this.readMethod = readMethod;
      this.bytes = bytes;
   }
}
