package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import java.io.FilterOutputStream;
import java.io.ObjectOutput;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=b!B\u0001\u0003\u0001\u0011Q!\u0001H&ss>|U\u000f\u001e9vi>\u0013'.Z2u\u001fV$\b/\u001e;Ce&$w-\u001a\u0006\u0003\u0007\u0011\t!b]3sS\u0006d\u0017N_3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7c\u0001\u0001\f'A\u0011A\"E\u0007\u0002\u001b)\u0011abD\u0001\u0003S>T\u0011\u0001E\u0001\u0005U\u00064\u0018-\u0003\u0002\u0013\u001b\t\u0011b)\u001b7uKJ|U\u000f\u001e9viN#(/Z1n!\taA#\u0003\u0002\u0016\u001b\taqJ\u00196fGR|U\u000f\u001e9vi\"Aq\u0003\u0001B\u0001B\u0003%\u0011$\u0001\u0003lef|7\u0001\u0001\t\u00035\u0001j\u0011a\u0007\u0006\u0003/qQ!!\b\u0010\u0002!\u0015\u001cx\u000e^3sS\u000e\u001cxN\u001a;xCJ,'\"A\u0010\u0002\u0007\r|W.\u0003\u0002\"7\t!1J]=p\u0011!\u0019\u0003A!A!\u0002\u0013!\u0013AB8viB,H\u000f\u0005\u0002&O5\taE\u0003\u0002\u000f7%\u0011\u0001F\n\u0002\u0007\u001fV$\b/\u001e;\t\u000b)\u0002A\u0011A\u0016\u0002\rqJg.\u001b;?)\racf\f\t\u0003[\u0001i\u0011A\u0001\u0005\u0006/%\u0002\r!\u0007\u0005\u0006G%\u0002\r\u0001\n\u0005\u0006c\u0001!\tEM\u0001\u000boJLG/\u001a$m_\u0006$HCA\u001a:!\t!t'D\u00016\u0015\u00051\u0014!B:dC2\f\u0017B\u0001\u001d6\u0005\u0011)f.\u001b;\t\u000bi\u0002\u0004\u0019A\u001e\u0002\u0003Y\u0004\"\u0001\u000e\u001f\n\u0005u*$!\u0002$m_\u0006$\b\"B \u0001\t\u0003\u0002\u0015AC<sSR,7\t[1sgR\u00111'\u0011\u0005\u0006\u0005z\u0002\raQ\u0001\u0002gB\u0011Ai\u0012\b\u0003i\u0015K!AR\u001b\u0002\rA\u0013X\rZ3g\u0013\tA\u0015J\u0001\u0004TiJLgn\u001a\u0006\u0003\rVBQa\u0013\u0001\u0005B1\u000b1b\u001e:ji\u0016$u.\u001e2mKR\u00111'\u0014\u0005\u0006u)\u0003\rA\u0014\t\u0003i=K!\u0001U\u001b\u0003\r\u0011{WO\u00197f\u0011\u0015\u0011\u0006\u0001\"\u0011T\u0003!9(/\u001b;f+R3ECA\u001aU\u0011\u0015\u0011\u0015\u000b1\u0001D\u0011\u00151\u0006\u0001\"\u0011X\u0003)9(/\u001b;f'\"|'\u000f\u001e\u000b\u0003gaCQAO+A\u0002e\u0003\"\u0001\u000e.\n\u0005m+$aA%oi\")Q\f\u0001C!=\u0006AqO]5uK&sG\u000f\u0006\u00024?\")!\b\u0018a\u00013\")\u0011\r\u0001C!E\u0006aqO]5uK\n{w\u000e\\3b]R\u00111g\u0019\u0005\u0006u\u0001\u0004\r\u0001\u001a\t\u0003i\u0015L!AZ\u001b\u0003\u000f\t{w\u000e\\3b]\")\u0001\u000e\u0001C!S\u0006)qO]5uKR\u00111G\u001b\u0005\u0006W\u001e\u0004\r!W\u0001\u0002E\")\u0001\u000e\u0001C![R\u00111G\u001c\u0005\u0006W2\u0004\ra\u001c\t\u0004iA\u0014\u0018BA96\u0005\u0015\t%O]1z!\t!4/\u0003\u0002uk\t!!)\u001f;f\u0011\u0015A\u0007\u0001\"\u0011w)\u0011\u0019t\u000f\u001f>\t\u000b-,\b\u0019A8\t\u000be,\b\u0019A-\u0002\u0007=4g\rC\u0003|k\u0002\u0007\u0011,A\u0002mK:DQ! \u0001\u0005By\f!b\u001e:ji\u0016\u0014\u0015\u0010^3t)\t\u0019t\u0010C\u0003Cy\u0002\u00071\tC\u0004\u0002\u0004\u0001!\t%!\u0002\u0002\u0013]\u0014\u0018\u000e^3DQ\u0006\u0014HcA\u001a\u0002\b!1!(!\u0001A\u0002eCq!a\u0003\u0001\t\u0003\ni!A\u0005xe&$X\rT8oOR\u00191'a\u0004\t\u000fi\nI\u00011\u0001\u0002\u0012A\u0019A'a\u0005\n\u0007\u0005UQG\u0001\u0003M_:<\u0007bBA\r\u0001\u0011\u0005\u00131D\u0001\noJLG/\u001a\"zi\u0016$2aMA\u000f\u0011\u0019Q\u0014q\u0003a\u00013\"9\u0011\u0011\u0005\u0001\u0005B\u0005\r\u0012aC<sSR,wJ\u00196fGR$2aMA\u0013\u0011!\t9#a\bA\u0002\u0005%\u0012aA8cUB\u0019A'a\u000b\n\u0007\u00055RG\u0001\u0004B]f\u0014VM\u001a"
)
public class KryoOutputObjectOutputBridge extends FilterOutputStream implements ObjectOutput {
   private final Kryo kryo;
   private final Output output;

   public void writeFloat(float v) {
      this.output.writeFloat(v);
   }

   public void writeChars(String s) {
      throw new UnsupportedOperationException("writeChars");
   }

   public void writeDouble(double v) {
      this.output.writeDouble(v);
   }

   public void writeUTF(String s) {
      this.output.writeString(s);
   }

   public void writeShort(int v) {
      this.output.writeShort(v);
   }

   public void writeInt(int v) {
      this.output.writeInt(v);
   }

   public void writeBoolean(boolean v) {
      this.output.writeBoolean(v);
   }

   public void write(int b) {
      this.output.write(b);
   }

   public void write(byte[] b) {
      this.output.write(b);
   }

   public void write(byte[] b, int off, int len) {
      this.output.write(b, off, len);
   }

   public void writeBytes(String s) {
      this.output.writeString(s);
   }

   public void writeChar(int v) {
      this.output.writeChar((char)v);
   }

   public void writeLong(long v) {
      this.output.writeLong(v);
   }

   public void writeByte(int v) {
      this.output.writeByte(v);
   }

   public void writeObject(Object obj) {
      this.kryo.writeClassAndObject(this.output, obj);
   }

   public KryoOutputObjectOutputBridge(Kryo kryo, Output output) {
      super(output);
      this.kryo = kryo;
      this.output = output;
   }
}
