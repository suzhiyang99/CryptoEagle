package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import java.io.FilterInputStream;
import java.io.ObjectInput;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005b!B\u0001\u0003\u0001\u0011Q!AG&ss>Le\u000e];u\u001f\nTWm\u0019;J]B,HO\u0011:jI\u001e,'BA\u0002\u0005\u0003)\u0019XM]5bY&TXM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\n\u0011\u00051\tR\"A\u0007\u000b\u00059y\u0011AA5p\u0015\u0005\u0001\u0012\u0001\u00026bm\u0006L!AE\u0007\u0003#\u0019KG\u000e^3s\u0013:\u0004X\u000f^*ue\u0016\fW\u000e\u0005\u0002\r)%\u0011Q#\u0004\u0002\f\u001f\nTWm\u0019;J]B,H\u000f\u0003\u0005\u0018\u0001\t\u0005\t\u0015!\u0003\u001a\u0003\u0011Y'/_8\u0004\u0001A\u0011!\u0004I\u0007\u00027)\u0011q\u0003\b\u0006\u0003;y\t\u0001#Z:pi\u0016\u0014\u0018nY:pMR<\u0018M]3\u000b\u0003}\t1aY8n\u0013\t\t3D\u0001\u0003Lef|\u0007\u0002C\u0012\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0013\u0002\u000b%t\u0007/\u001e;\u0011\u0005\u0015:S\"\u0001\u0014\u000b\u00059Y\u0012B\u0001\u0015'\u0005\u0015Ie\u000e];u\u0011\u0015Q\u0003\u0001\"\u0001,\u0003\u0019a\u0014N\\5u}Q\u0019AFL\u0018\u0011\u00055\u0002Q\"\u0001\u0002\t\u000b]I\u0003\u0019A\r\t\u000b\rJ\u0003\u0019\u0001\u0013\t\u000bE\u0002A\u0011\t\u001a\u0002\u0011I,\u0017\r\u001a'p]\u001e$\u0012a\r\t\u0003i]j\u0011!\u000e\u0006\u0002m\u0005)1oY1mC&\u0011\u0001(\u000e\u0002\u0005\u0019>tw\rC\u0003;\u0001\u0011\u00053(\u0001\u0005sK\u0006$7\t[1s)\u0005a\u0004C\u0001\u001b>\u0013\tqTG\u0001\u0003DQ\u0006\u0014\b\"\u0002!\u0001\t\u0003\n\u0015!\u0003:fC\u00124En\\1u)\u0005\u0011\u0005C\u0001\u001bD\u0013\t!UGA\u0003GY>\fG\u000fC\u0003G\u0001\u0011\u0005s)\u0001\u0005sK\u0006$')\u001f;f)\u0005A\u0005C\u0001\u001bJ\u0013\tQUG\u0001\u0003CsR,\u0007\"\u0002'\u0001\t\u0003j\u0015!\u0003:fC\u0012\u001c\u0006n\u001c:u)\u0005q\u0005C\u0001\u001bP\u0013\t\u0001VGA\u0003TQ>\u0014H\u000fC\u0003S\u0001\u0011\u00053+A\u0004sK\u0006$W\u000b\u0016$\u0015\u0003Q\u0003\"!\u0016-\u000f\u0005Q2\u0016BA,6\u0003\u0019\u0001&/\u001a3fM&\u0011\u0011L\u0017\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005]+\u0004\"\u0002/\u0001\t\u0003j\u0016a\u0002:fC\u0012Le\u000e\u001e\u000b\u0002=B\u0011AgX\u0005\u0003AV\u00121!\u00138u\u0011\u0015\u0011\u0007\u0001\"\u0011^\u0003E\u0011X-\u00193V]NLwM\\3e'\"|'\u000f\u001e\u0005\u0006I\u0002!\t%Z\u0001\ng.L\u0007OQ=uKN$\"A\u00184\t\u000b\u001d\u001c\u0007\u0019\u00010\u0002\u00039DQ!\u001b\u0001\u0005B)\f\u0011B]3bI\u001a+H\u000e\\=\u0015\u0005-t\u0007C\u0001\u001bm\u0013\tiWG\u0001\u0003V]&$\b\"B8i\u0001\u0004\u0001\u0018!\u00012\u0011\u0007Q\n\b*\u0003\u0002sk\t)\u0011I\u001d:bs\")\u0011\u000e\u0001C!iR!1.\u001e<y\u0011\u0015y7\u000f1\u0001q\u0011\u001598\u000f1\u0001_\u0003\rygM\u001a\u0005\u0006sN\u0004\rAX\u0001\u0004Y\u0016t\u0007\"B>\u0001\t\u0003\u001a\u0016\u0001\u0003:fC\u0012d\u0015N\\3\t\u000bu\u0004A\u0011\t@\u0002\u0017I,\u0017\r\u001a\"p_2,\u0017M\u001c\u000b\u0002\u007fB\u0019A'!\u0001\n\u0007\u0005\rQGA\u0004C_>dW-\u00198\t\r\u0005\u001d\u0001\u0001\"\u0011^\u0003A\u0011X-\u00193V]NLwM\\3e\u0005f$X\rC\u0004\u0002\f\u0001!\t%!\u0004\u0002\u0015I,\u0017\r\u001a#pk\ndW\r\u0006\u0002\u0002\u0010A\u0019A'!\u0005\n\u0007\u0005MQG\u0001\u0004E_V\u0014G.\u001a\u0005\b\u0003/\u0001A\u0011IA\r\u0003)\u0011X-\u00193PE*,7\r\u001e\u000b\u0003\u00037\u00012\u0001NA\u000f\u0013\r\ty\"\u000e\u0002\u0007\u0003:L(+\u001a4"
)
public class KryoInputObjectInputBridge extends FilterInputStream implements ObjectInput {
   private final Kryo kryo;
   private final Input input;

   public long readLong() {
      return this.input.readLong();
   }

   public char readChar() {
      return this.input.readChar();
   }

   public float readFloat() {
      return this.input.readFloat();
   }

   public byte readByte() {
      return this.input.readByte();
   }

   public short readShort() {
      return this.input.readShort();
   }

   public String readUTF() {
      return this.input.readString();
   }

   public int readInt() {
      return this.input.readInt();
   }

   public int readUnsignedShort() {
      return this.input.readShortUnsigned();
   }

   public int skipBytes(int n) {
      this.input.skip(n);
      return n;
   }

   public void readFully(byte[] b) {
      this.input.read(b);
   }

   public void readFully(byte[] b, int off, int len) {
      this.input.read(b, off, len);
   }

   public String readLine() {
      throw new UnsupportedOperationException("readLine");
   }

   public boolean readBoolean() {
      return this.input.readBoolean();
   }

   public int readUnsignedByte() {
      return this.input.readByteUnsigned();
   }

   public double readDouble() {
      return this.input.readDouble();
   }

   public Object readObject() {
      return this.kryo.readClassAndObject(this.input);
   }

   public KryoInputObjectInputBridge(Kryo kryo, Input input) {
      super(input);
      this.kryo = kryo;
      this.input = input;
   }
}
