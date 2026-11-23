package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.UnsafeInput;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.apache.spark.SparkException;
import scala.Predef$;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}c!B\u0001\u0003\u0001\u0011Q!AF&ss>\u001cVM]5bY&TXM]%ogR\fgnY3\u000b\u0005\r!\u0011AC:fe&\fG.\u001b>fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011A\"D\u0007\u0002\u0005%\u0011aB\u0001\u0002\u0013'\u0016\u0014\u0018.\u00197ju\u0016\u0014\u0018J\\:uC:\u001cW\r\u0003\u0005\u0011\u0001\t\u0005\t\u0015!\u0003\u0013\u0003\tY7o\u0001\u0001\u0011\u00051\u0019\u0012B\u0001\u000b\u0003\u00059Y%/_8TKJL\u0017\r\\5{KJD\u0001B\u0006\u0001\u0003\u0002\u0003\u0006IaF\u0001\nkN,WK\\:bM\u0016\u0004\"\u0001G\u000e\u000e\u0003eQ\u0011AG\u0001\u0006g\u000e\fG.Y\u0005\u00039e\u0011qAQ8pY\u0016\fg\u000eC\u0003\u001f\u0001\u0011\u0005q$\u0001\u0004=S:LGO\u0010\u000b\u0004A\u0005\u0012\u0003C\u0001\u0007\u0001\u0011\u0015\u0001R\u00041\u0001\u0013\u0011\u00151R\u00041\u0001\u0018\u0011\u0019!\u0003\u0001)Q\u0005K\u0005Q1-Y2iK\u0012\\%/_8\u0011\u0005\u0019jS\"A\u0014\u000b\u0005!J\u0013\u0001B6ss>T!AK\u0016\u0002!\u0015\u001cx\u000e^3sS\u000e\u001cxN\u001a;xCJ,'\"\u0001\u0017\u0002\u0007\r|W.\u0003\u0002/O\t!1J]=pQ\t\u0019\u0003\u0007\u0005\u00022m5\t!G\u0003\u00024i\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\u000b\u0003U\nQA[1wCbL!a\u000e\u001a\u0003\u00119+H\u000e\\1cY\u0016Da!\u000f\u0001\u0005\u0002\tQ\u0014A\u00032peJ|wo\u0013:z_R\tQ\u0005\u0003\u0004=\u0001\u0011\u0005!!P\u0001\fe\u0016dW-Y:f\u0017JLx\u000e\u0006\u0002?\u0003B\u0011\u0001dP\u0005\u0003\u0001f\u0011A!\u00168ji\")\u0001f\u000fa\u0001K!A1\t\u0001EC\u0002\u0013%A)\u0001\u0004pkR\u0004X\u000f^\u000b\u0002\u000bB\u0011a)S\u0007\u0002\u000f*\u0011\u0001jJ\u0001\u0003S>L!AS$\u0003\r=+H\u000f];u\u0011!a\u0005\u0001#A!B\u0013)\u0015aB8viB,H\u000f\t\u0005\t\u001d\u0002A)\u0019!C\u0005\u001f\u0006)\u0011N\u001c9viV\t\u0001\u000b\u0005\u0002G#&\u0011!k\u0012\u0002\u0006\u0013:\u0004X\u000f\u001e\u0005\t)\u0002A\t\u0011)Q\u0005!\u00061\u0011N\u001c9vi\u0002BQA\u0016\u0001\u0005B]\u000b\u0011b]3sS\u0006d\u0017N_3\u0016\u0005acGCA-v)\tQ&\r\u0005\u0002\\A6\tAL\u0003\u0002^=\u0006\u0019a.[8\u000b\u0003}\u000bAA[1wC&\u0011\u0011\r\u0018\u0002\u000b\u0005f$XMQ;gM\u0016\u0014\bbB2V\u0003\u0003\u0005\u001d\u0001Z\u0001\u000bKZLG-\u001a8dK\u0012\u001a\u0004cA3iU6\taM\u0003\u0002h3\u00059!/\u001a4mK\u000e$\u0018BA5g\u0005!\u0019E.Y:t)\u0006<\u0007CA6m\u0019\u0001!Q!\\+C\u00029\u0014\u0011\u0001V\t\u0003_J\u0004\"\u0001\u00079\n\u0005EL\"a\u0002(pi\"Lgn\u001a\t\u00031ML!\u0001^\r\u0003\u0007\u0005s\u0017\u0010C\u0003w+\u0002\u0007!.A\u0001u\u0011\u0015A\b\u0001\"\u0011z\u0003-!Wm]3sS\u0006d\u0017N_3\u0016\u0005ilHcA>\u0002\u0004Q\u0011AP \t\u0003Wv$Q!\\<C\u00029D\u0001b`<\u0002\u0002\u0003\u000f\u0011\u0011A\u0001\u000bKZLG-\u001a8dK\u0012\"\u0004cA3iy\"1\u0011QA<A\u0002i\u000bQAY=uKNDa\u0001\u001f\u0001\u0005B\u0005%Q\u0003BA\u0006\u0003#!b!!\u0004\u0002\u001a\u0005mA\u0003BA\b\u0003'\u00012a[A\t\t\u0019i\u0017q\u0001b\u0001]\"Q\u0011QCA\u0004\u0003\u0003\u0005\u001d!a\u0006\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$S\u0007\u0005\u0003fQ\u0006=\u0001bBA\u0003\u0003\u000f\u0001\rA\u0017\u0005\t\u0003;\t9\u00011\u0001\u0002 \u00051An\\1eKJ\u0004B!!\t\u0002(5\u0011\u00111\u0005\u0006\u0004\u0003Kq\u0016\u0001\u00027b]\u001eLA!!\u000b\u0002$\tY1\t\\1tg2{\u0017\rZ3s\u0011\u001d\ti\u0003\u0001C!\u0003_\tqb]3sS\u0006d\u0017N_3TiJ,\u0017-\u001c\u000b\u0005\u0003c\t9\u0004E\u0002\r\u0003gI1!!\u000e\u0003\u0005M\u0019VM]5bY&T\u0018\r^5p]N#(/Z1n\u0011!\tI$a\u000bA\u0002\u0005m\u0012!A:\u0011\t\u0005u\u0012\u0011I\u0007\u0003\u0003\u007fQ!\u0001\u00130\n\t\u0005\r\u0013q\b\u0002\r\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\b\u0003\u000f\u0002A\u0011IA%\u0003E!Wm]3sS\u0006d\u0017N_3TiJ,\u0017-\u001c\u000b\u0005\u0003\u0017\n\t\u0006E\u0002\r\u0003\u001bJ1!a\u0014\u0003\u0005U!Um]3sS\u0006d\u0017N_1uS>t7\u000b\u001e:fC6D\u0001\"!\u000f\u0002F\u0001\u0007\u00111\u000b\t\u0005\u0003{\t)&\u0003\u0003\u0002X\u0005}\"aC%oaV$8\u000b\u001e:fC6Dq!a\u0017\u0001\t\u0003\ti&\u0001\u0007hKR\fU\u000f^8SKN,G\u000fF\u0001\u0018\u0001"
)
public class KryoSerializerInstance extends SerializerInstance {
   private final KryoSerializer ks;
   private final boolean useUnsafe;
   @Nullable
   private Kryo cachedKryo;
   private Output output;
   private Input input;
   private volatile byte bitmap$0;

   private Output output$lzycompute() {
      synchronized(this) {
         if ((byte)(this.bitmap$0 & 1) == 0) {
            this.output = this.ks.newKryoOutput();
            this.bitmap$0 = (byte)(this.bitmap$0 | 1);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.output;
   }

   private Input input$lzycompute() {
      synchronized(this) {
         if ((byte)(this.bitmap$0 & 2) == 0) {
            this.input = (Input)(this.useUnsafe ? new UnsafeInput() : new Input());
            this.bitmap$0 = (byte)(this.bitmap$0 | 2);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.input;
   }

   public Kryo borrowKryo() {
      Kryo var10000;
      if (this.cachedKryo == null) {
         var10000 = this.ks.newKryo();
      } else {
         Kryo kryo = this.cachedKryo;
         kryo.reset();
         this.cachedKryo = null;
         var10000 = kryo;
      }

      return var10000;
   }

   public void releaseKryo(Kryo kryo) {
      if (this.cachedKryo == null) {
         this.cachedKryo = kryo;
      }

   }

   private Output output() {
      return (byte)(this.bitmap$0 & 1) == 0 ? this.output$lzycompute() : this.output;
   }

   private Input input() {
      return (byte)(this.bitmap$0 & 2) == 0 ? this.input$lzycompute() : this.input;
   }

   public <T> ByteBuffer serialize(T t, ClassTag<T> evidence$3) {
      this.output().clear();
      Kryo kryo = this.borrowKryo();

      try {
         try {
            kryo.writeClassAndObject(this.output(), t);
         } catch (Throwable var12) {
            if (var12 instanceof KryoException) {
               KryoException var7 = (KryoException)var12;
               if (var7.getMessage().startsWith("Buffer overflow")) {
                  throw new SparkException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Kryo serialization failed: ", ". To avoid this, "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{var7.getMessage()}))).append((Object)"increase spark.kryoserializer.buffer.max value.").toString(), var7);
               }
            }

            throw var12;
         }
      } finally {
         this.releaseKryo(kryo);
      }

      return ByteBuffer.wrap(this.output().toBytes());
   }

   public <T> T deserialize(ByteBuffer bytes, ClassTag<T> evidence$4) {
      Kryo kryo = this.borrowKryo();

      Object var10000;
      try {
         this.input().setBuffer(bytes.array(), bytes.arrayOffset() + bytes.position(), bytes.remaining());
         var10000 = kryo.readClassAndObject(this.input());
      } finally {
         this.releaseKryo(kryo);
      }

      return (T)var10000;
   }

   public <T> T deserialize(ByteBuffer bytes, ClassLoader loader, ClassTag<T> evidence$5) {
      Kryo kryo = this.borrowKryo();
      ClassLoader oldClassLoader = kryo.getClassLoader();

      Object var10000;
      try {
         kryo.setClassLoader(loader);
         this.input().setBuffer(bytes.array(), bytes.arrayOffset() + bytes.position(), bytes.remaining());
         var10000 = kryo.readClassAndObject(this.input());
      } finally {
         kryo.setClassLoader(oldClassLoader);
         this.releaseKryo(kryo);
      }

      return (T)var10000;
   }

   public SerializationStream serializeStream(OutputStream s) {
      return new KryoSerializationStream(this, s, this.useUnsafe);
   }

   public DeserializationStream deserializeStream(InputStream s) {
      return new KryoDeserializationStream(this, s, this.useUnsafe);
   }

   public boolean getAutoReset() {
      Field field = Kryo.class.getDeclaredField("autoReset");
      field.setAccessible(true);
      Kryo kryo = this.borrowKryo();

      boolean var10000;
      try {
         var10000 = BoxesRunTime.unboxToBoolean(field.get(kryo));
      } finally {
         this.releaseKryo(kryo);
      }

      return var10000;
   }

   public KryoSerializerInstance(KryoSerializer ks, boolean useUnsafe) {
      this.ks = ks;
      this.useUnsafe = useUnsafe;
      this.cachedKryo = this.borrowKryo();
   }
}
