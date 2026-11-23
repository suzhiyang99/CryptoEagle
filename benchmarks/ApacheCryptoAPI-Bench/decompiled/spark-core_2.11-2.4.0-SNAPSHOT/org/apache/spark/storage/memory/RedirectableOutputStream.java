package org.apache.spark.storage.memory;

import java.io.OutputStream;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00153Q!\u0001\u0002\u0001\t1\u0011\u0001DU3eSJ,7\r^1cY\u0016|U\u000f\u001e9viN#(/Z1n\u0015\t\u0019A!\u0001\u0004nK6|'/\u001f\u0006\u0003\u000b\u0019\tqa\u001d;pe\u0006<WM\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\t\u0001Q\u0002\u0005\u0002\u000f'5\tqB\u0003\u0002\u0011#\u0005\u0011\u0011n\u001c\u0006\u0002%\u0005!!.\u0019<b\u0013\t!rB\u0001\u0007PkR\u0004X\u000f^*ue\u0016\fW\u000eC\u0003\u0017\u0001\u0011\u0005\u0001$\u0001\u0004=S:LGOP\u0002\u0001)\u0005I\u0002C\u0001\u000e\u0001\u001b\u0005\u0011\u0001\"\u0003\u000f\u0001\u0001\u0004\u0005\t\u0015)\u0003\u000e\u0003\ty7\u000fC\u0003\u001f\u0001\u0011\u0005q$A\btKR|U\u000f\u001e9viN#(/Z1n)\t\u0001c\u0005\u0005\u0002\"I5\t!EC\u0001$\u0003\u0015\u00198-\u00197b\u0013\t)#E\u0001\u0003V]&$\b\"B\u0014\u001e\u0001\u0004i\u0011!A:\t\u000b%\u0002A\u0011\t\u0016\u0002\u000b]\u0014\u0018\u000e^3\u0015\u0005\u0001Z\u0003\"\u0002\u0017)\u0001\u0004i\u0013!\u00012\u0011\u0005\u0005r\u0013BA\u0018#\u0005\rIe\u000e\u001e\u0005\u0006S\u0001!\t%\r\u000b\u0003AIBQ\u0001\f\u0019A\u0002M\u00022!\t\u001b7\u0013\t)$EA\u0003BeJ\f\u0017\u0010\u0005\u0002\"o%\u0011\u0001H\t\u0002\u0005\u0005f$X\rC\u0003*\u0001\u0011\u0005#\b\u0006\u0003!wqr\u0004\"\u0002\u0017:\u0001\u0004\u0019\u0004\"B\u001f:\u0001\u0004i\u0013aA8gM\")q(\u000fa\u0001[\u0005\u0019A.\u001a8\t\u000b\u0005\u0003A\u0011\t\"\u0002\u000b\u0019dWo\u001d5\u0015\u0003\u0001BQ\u0001\u0012\u0001\u0005B\t\u000bQa\u00197pg\u0016\u0004"
)
public class RedirectableOutputStream extends OutputStream {
   private OutputStream os;

   public void setOutputStream(OutputStream s) {
      this.os = s;
   }

   public void write(int b) {
      this.os.write(b);
   }

   public void write(byte[] b) {
      this.os.write(b);
   }

   public void write(byte[] b, int off, int len) {
      this.os.write(b, off, len);
   }

   public void flush() {
      this.os.flush();
   }

   public void close() {
      this.os.close();
   }
}
