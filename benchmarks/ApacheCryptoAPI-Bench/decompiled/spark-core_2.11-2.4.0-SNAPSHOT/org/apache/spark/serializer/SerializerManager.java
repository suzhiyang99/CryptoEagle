package org.apache.spark.serializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.spark.SparkConf;
import org.apache.spark.io.CompressionCodec;
import org.apache.spark.io.CompressionCodec$;
import org.apache.spark.security.CryptoStreamUtils$;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BroadcastBlockId;
import org.apache.spark.storage.RDDBlockId;
import org.apache.spark.storage.ShuffleBlockId;
import org.apache.spark.storage.StreamBlockId;
import org.apache.spark.storage.TempLocalBlockId;
import org.apache.spark.storage.TempShuffleBlockId;
import org.apache.spark.util.io.ChunkedByteBuffer;
import org.apache.spark.util.io.ChunkedByteBufferOutputStream;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.immutable.Set;
import scala.collection.immutable.Set$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\t\u001dc!B\u0001\u0003\u0001\u0011Q!!E*fe&\fG.\u001b>fe6\u000bg.Y4fe*\u00111\u0001B\u0001\u000bg\u0016\u0014\u0018.\u00197ju\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0003\u0005\u0013\u0001\t\u0005\t\u0015!\u0003\u0015\u0003E!WMZ1vYR\u001cVM]5bY&TXM]\u0002\u0001!\t)b#D\u0001\u0003\u0013\t9\"A\u0001\u0006TKJL\u0017\r\\5{KJD\u0001\"\u0007\u0001\u0003\u0002\u0003\u0006IAG\u0001\u0005G>tg\r\u0005\u0002\u001c95\tA!\u0003\u0002\u001e\t\tI1\u000b]1sW\u000e{gN\u001a\u0005\t?\u0001\u0011\t\u0011)A\u0005A\u0005iQM\\2ssB$\u0018n\u001c8LKf\u00042\u0001D\u0011$\u0013\t\u0011SB\u0001\u0004PaRLwN\u001c\t\u0004\u0019\u00112\u0013BA\u0013\u000e\u0005\u0015\t%O]1z!\taq%\u0003\u0002)\u001b\t!!)\u001f;f\u0011\u0015Q\u0003\u0001\"\u0001,\u0003\u0019a\u0014N\\5u}Q!A&\f\u00180!\t)\u0002\u0001C\u0003\u0013S\u0001\u0007A\u0003C\u0003\u001aS\u0001\u0007!\u0004C\u0003 S\u0001\u0007\u0001\u0005C\u0003+\u0001\u0011\u0005\u0011\u0007F\u0002-eMBQA\u0005\u0019A\u0002QAQ!\u0007\u0019A\u0002iAa!\u000e\u0001!\u0002\u00131\u0014AD6ss>\u001cVM]5bY&TXM\u001d\t\u0003+]J!\u0001\u000f\u0002\u0003\u001d-\u0013\u0018p\\*fe&\fG.\u001b>fe\")!\b\u0001C\u0001w\u0005)2/\u001a;EK\u001a\fW\u000f\u001c;DY\u0006\u001c8\u000fT8bI\u0016\u0014HC\u0001\u001f@!\taQ(\u0003\u0002?\u001b\t!QK\\5u\u0011\u0015\u0001\u0015\b1\u0001B\u0003-\u0019G.Y:t\u0019>\fG-\u001a:\u0011\u0005\t;U\"A\"\u000b\u0005\u0011+\u0015\u0001\u00027b]\u001eT\u0011AR\u0001\u0005U\u00064\u0018-\u0003\u0002I\u0007\nY1\t\\1tg2{\u0017\rZ3s\u0011\u0019Q\u0005\u0001)A\u0005\u0017\u0006q1\u000f\u001e:j]\u001e\u001cE.Y:t)\u0006<\u0007c\u0001'P#6\tQJ\u0003\u0002O\u001b\u00059!/\u001a4mK\u000e$\u0018B\u0001)N\u0005!\u0019E.Y:t)\u0006<\u0007C\u0001*V\u001d\ta1+\u0003\u0002U\u001b\u00051\u0001K]3eK\u001aL!AV,\u0003\rM#(/\u001b8h\u0015\t!V\u0002\u0003\u0004Z\u0001\u0001\u0006IAW\u0001$aJLW.\u001b;jm\u0016\fe\u000e\u001a)sS6LG/\u001b<f\u0003J\u0014\u0018-_\"mCN\u001cH+Y4t!\r\u00116,X\u0005\u00039^\u00131aU3ua\tq\u0016\rE\u0002M\u001f~\u0003\"\u0001Y1\r\u0001\u0011I!\rWA\u0001\u0002\u0003\u0015\ta\u0019\u0002\u0004?\u0012\n\u0014C\u00013h!\taQ-\u0003\u0002g\u001b\t9aj\u001c;iS:<\u0007C\u0001\u0007i\u0013\tIWBA\u0002B]fDaa\u001b\u0001!\u0002\u0013a\u0017!E2p[B\u0014Xm]:Ce>\fGmY1tiB\u0011A\"\\\u0005\u0003]6\u0011qAQ8pY\u0016\fg\u000e\u0003\u0004q\u0001\u0001\u0006I\u0001\\\u0001\u0010G>l\u0007O]3tgNCWO\u001a4mK\"1!\u000f\u0001Q\u0001\n1\fAbY8naJ,7o\u001d*eINDa\u0001\u001e\u0001!\u0002\u0013a\u0017\u0001F2p[B\u0014Xm]:TQV4g\r\\3Ta&dG\u000e\u0003\u0005w\u0001!\u0015\r\u0011\"\u0003x\u0003A\u0019w.\u001c9sKN\u001c\u0018n\u001c8D_\u0012,7-F\u0001y!\tIH0D\u0001{\u0015\tYH!\u0001\u0002j_&\u0011QP\u001f\u0002\u0011\u0007>l\u0007O]3tg&|gnQ8eK\u000eD\u0001b \u0001\t\u0002\u0003\u0006K\u0001_\u0001\u0012G>l\u0007O]3tg&|gnQ8eK\u000e\u0004\u0003bBA\u0002\u0001\u0011\u0005\u0011QA\u0001\u0012K:\u001c'/\u001f9uS>tWI\\1cY\u0016$W#\u00017\t\u000f\u0005%\u0001\u0001\"\u0001\u0002\f\u0005Q1-\u00198Vg\u0016\\%/_8\u0015\u00071\fi\u0001\u0003\u0005\u0002\u0010\u0005\u001d\u0001\u0019AA\t\u0003\t\u0019G\u000f\r\u0003\u0002\u0014\u0005]\u0001\u0003\u0002'P\u0003+\u00012\u0001YA\f\t-\tI\"!\u0004\u0002\u0002\u0003\u0005)\u0011A2\u0003\u0007}#3\u0007C\u0004\u0002\u001e\u0001!\t!a\b\u0002\u001b\u001d,GoU3sS\u0006d\u0017N_3s)\u0015!\u0012\u0011EA\u0017\u0011!\ty!a\u0007A\u0002\u0005\r\u0002\u0007BA\u0013\u0003S\u0001B\u0001T(\u0002(A\u0019\u0001-!\u000b\u0005\u0017\u0005-\u0012\u0011EA\u0001\u0002\u0003\u0015\ta\u0019\u0002\u0004?\u0012\"\u0004bBA\u0018\u00037\u0001\r\u0001\\\u0001\tCV$x\u000eU5dW\"9\u0011Q\u0004\u0001\u0005\u0002\u0005MB#\u0002\u000b\u00026\u0005\r\u0003\u0002CA\u001c\u0003c\u0001\r!!\u000f\u0002\u0017-,\u0017p\u00117bgN$\u0016m\u001a\u0019\u0005\u0003w\ty\u0004\u0005\u0003M\u001f\u0006u\u0002c\u00011\u0002@\u0011Y\u0011\u0011IA\u001b\u0003\u0003\u0005\tQ!\u0001d\u0005\ryF%\u000e\u0005\t\u0003\u000b\n\t\u00041\u0001\u0002H\u0005ia/\u00197vK\u000ec\u0017m]:UC\u001e\u0004D!!\u0013\u0002NA!AjTA&!\r\u0001\u0017Q\n\u0003\f\u0003\u001f\n\u0019%!A\u0001\u0002\u000b\u00051MA\u0002`IYBq!a\u0015\u0001\t\u0013\t)&\u0001\btQ>,H\u000eZ\"p[B\u0014Xm]:\u0015\u00071\f9\u0006\u0003\u0005\u0002Z\u0005E\u0003\u0019AA.\u0003\u001d\u0011Gn\\2l\u0013\u0012\u0004B!!\u0018\u0002d5\u0011\u0011q\f\u0006\u0004\u0003C\"\u0011aB:u_J\fw-Z\u0005\u0005\u0003K\nyFA\u0004CY>\u001c7.\u00133\t\u000f\u0005%\u0004\u0001\"\u0001\u0002l\u0005QqO]1q'R\u0014X-Y7\u0015\r\u00055\u0014qOA=!\u0011\ty'a\u001d\u000e\u0005\u0005E$BA>F\u0013\u0011\t)(!\u001d\u0003\u0017%s\u0007/\u001e;TiJ,\u0017-\u001c\u0005\t\u00033\n9\u00071\u0001\u0002\\!A\u00111PA4\u0001\u0004\ti'A\u0001t\u0011\u001d\tI\u0007\u0001C\u0001\u0003\u007f\"b!!!\u0002\b\u0006%\u0005\u0003BA8\u0003\u0007KA!!\"\u0002r\taq*\u001e;qkR\u001cFO]3b[\"A\u0011\u0011LA?\u0001\u0004\tY\u0006\u0003\u0005\u0002|\u0005u\u0004\u0019AAA\u0011\u001d\ti\t\u0001C\u0001\u0003\u001f\u000b\u0011c\u001e:ba\u001a{'/\u00128def\u0004H/[8o)\u0011\ti'!%\t\u0011\u0005m\u00141\u0012a\u0001\u0003[Bq!!$\u0001\t\u0003\t)\n\u0006\u0003\u0002\u0002\u0006]\u0005\u0002CA>\u0003'\u0003\r!!!\t\u000f\u0005m\u0005\u0001\"\u0001\u0002\u001e\u0006\u0011rO]1q\r>\u00148i\\7qe\u0016\u001c8/[8o)\u0019\t\t)a(\u0002\"\"A\u0011\u0011LAM\u0001\u0004\tY\u0006\u0003\u0005\u0002|\u0005e\u0005\u0019AAA\u0011\u001d\tY\n\u0001C\u0001\u0003K#b!!\u001c\u0002(\u0006%\u0006\u0002CA-\u0003G\u0003\r!a\u0017\t\u0011\u0005m\u00141\u0015a\u0001\u0003[Bq!!,\u0001\t\u0003\ty+A\neCR\f7+\u001a:jC2L'0Z*ue\u0016\fW.\u0006\u0003\u00022\u0006uF\u0003CAZ\u0003\u0003\f\u0019-a2\u0015\u0007q\n)\f\u0003\u0006\u00028\u0006-\u0016\u0011!a\u0002\u0003s\u000b!\"\u001a<jI\u0016t7-\u001a\u00132!\u0011au*a/\u0011\u0007\u0001\fi\fB\u0004\u0002@\u0006-&\u0019A2\u0003\u0003QC\u0001\"!\u0017\u0002,\u0002\u0007\u00111\f\u0005\t\u0003\u000b\fY\u000b1\u0001\u0002\u0002\u0006aq.\u001e;qkR\u001cFO]3b[\"A\u0011\u0011ZAV\u0001\u0004\tY-\u0001\u0004wC2,Xm\u001d\t\u0007\u0003\u001b\fi.a/\u000f\t\u0005=\u0017\u0011\u001c\b\u0005\u0003#\f9.\u0004\u0002\u0002T*\u0019\u0011Q[\n\u0002\rq\u0012xn\u001c;?\u0013\u0005q\u0011bAAn\u001b\u00059\u0001/Y2lC\u001e,\u0017\u0002BAp\u0003C\u0014\u0001\"\u0013;fe\u0006$xN\u001d\u0006\u0004\u00037l\u0001bBAs\u0001\u0011\u0005\u0011q]\u0001\u000eI\u0006$\u0018mU3sS\u0006d\u0017N_3\u0016\t\u0005%(1\u0001\u000b\u0007\u0003W\u0014)Aa\u0002\u0015\t\u00055\u00181 \t\u0005\u0003_\f90\u0004\u0002\u0002r*\u001910a=\u000b\u0007\u0005UH!\u0001\u0003vi&d\u0017\u0002BA}\u0003c\u0014\u0011c\u00115v].,GMQ=uK\n+hMZ3s\u0011)\ti0a9\u0002\u0002\u0003\u000f\u0011q`\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004\u0003\u0002'P\u0005\u0003\u00012\u0001\u0019B\u0002\t\u001d\ty,a9C\u0002\rD\u0001\"!\u0017\u0002d\u0002\u0007\u00111\f\u0005\t\u0003\u0013\f\u0019\u000f1\u0001\u0003\nA1\u0011QZAo\u0005\u0003AqA!\u0004\u0001\t\u0003\u0011y!A\u0011eCR\f7+\u001a:jC2L'0Z,ji\",\u0005\u0010\u001d7jG&$8\t\\1tgR\u000bw\r\u0006\u0005\u0002n\nE!1\u0003B\u0010\u0011!\tIFa\u0003A\u0002\u0005m\u0003\u0002CAe\u0005\u0017\u0001\rA!\u00061\t\t]!1\u0004\t\u0007\u0003\u001b\fiN!\u0007\u0011\u0007\u0001\u0014Y\u0002B\u0006\u0003\u001e\tM\u0011\u0011!A\u0001\u0006\u0003\u0019'aA0%o!A!\u0011\u0005B\u0006\u0001\u0004\u0011\u0019#\u0001\u0005dY\u0006\u001c8\u000fV1ha\u0011\u0011)C!\u000b\u0011\t1{%q\u0005\t\u0004A\n%Ba\u0003B\u0016\u0005?\t\t\u0011!A\u0003\u0002\r\u00141a\u0018\u00139\u0011\u001d\u0011y\u0003\u0001C\u0001\u0005c\tQ\u0003Z1uC\u0012+7/\u001a:jC2L'0Z*ue\u0016\fW.\u0006\u0003\u00034\tmBC\u0002B\u001b\u0005\u0003\u0012\u0019\u0005\u0006\u0003\u00038\tu\u0002CBAg\u0003;\u0014I\u0004E\u0002a\u0005w!q!a0\u0003.\t\u00071\r\u0003\u0005\u0003\"\t5\u0002\u0019\u0001B !\u0011auJ!\u000f\t\u0011\u0005e#Q\u0006a\u0001\u00037B\u0001B!\u0012\u0003.\u0001\u0007\u0011QN\u0001\fS:\u0004X\u000f^*ue\u0016\fW\u000e"
)
public class SerializerManager {
   private final Serializer defaultSerializer;
   public final SparkConf org$apache$spark$serializer$SerializerManager$$conf;
   private final Option<byte[]> encryptionKey;
   private final KryoSerializer kryoSerializer;
   private final ClassTag<String> stringClassTag;
   private final Set<ClassTag<?>> primitiveAndPrimitiveArrayClassTags;
   private final boolean compressBroadcast;
   private final boolean compressShuffle;
   private final boolean compressRdds;
   private final boolean compressShuffleSpill;
   private CompressionCodec compressionCodec;
   private volatile boolean bitmap$0;

   private CompressionCodec compressionCodec$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.compressionCodec = CompressionCodec$.MODULE$.createCodec(this.org$apache$spark$serializer$SerializerManager$$conf);
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.compressionCodec;
   }

   public void setDefaultClassLoader(ClassLoader classLoader) {
      this.kryoSerializer.setDefaultClassLoader(classLoader);
   }

   private CompressionCodec compressionCodec() {
      return this.bitmap$0 ? this.compressionCodec : this.compressionCodec$lzycompute();
   }

   public boolean encryptionEnabled() {
      return this.encryptionKey.isDefined();
   }

   public boolean canUseKryo(ClassTag<?> ct) {
      boolean var10000;
      if (!this.primitiveAndPrimitiveArrayClassTags.contains(ct)) {
         label29: {
            ClassTag var2 = this.stringClassTag;
            if (ct == null) {
               if (var2 == null) {
                  break label29;
               }
            } else if (ct.equals(var2)) {
               break label29;
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public Serializer getSerializer(ClassTag<?> ct, boolean autoPick) {
      return (Serializer)(autoPick && this.canUseKryo(ct) ? this.kryoSerializer : this.defaultSerializer);
   }

   public Serializer getSerializer(ClassTag<?> keyClassTag, ClassTag<?> valueClassTag) {
      return (Serializer)(this.canUseKryo(keyClassTag) && this.canUseKryo(valueClassTag) ? this.kryoSerializer : this.defaultSerializer);
   }

   private boolean shouldCompress(BlockId blockId) {
      boolean var3;
      if (blockId instanceof ShuffleBlockId) {
         var3 = this.compressShuffle;
      } else if (blockId instanceof BroadcastBlockId) {
         var3 = this.compressBroadcast;
      } else if (blockId instanceof RDDBlockId) {
         var3 = this.compressRdds;
      } else if (blockId instanceof TempLocalBlockId) {
         var3 = this.compressShuffleSpill;
      } else if (blockId instanceof TempShuffleBlockId) {
         var3 = this.compressShuffle;
      } else {
         var3 = false;
      }

      return var3;
   }

   public InputStream wrapStream(BlockId blockId, InputStream s) {
      return this.wrapForCompression(blockId, this.wrapForEncryption(s));
   }

   public OutputStream wrapStream(BlockId blockId, OutputStream s) {
      return this.wrapForCompression(blockId, this.wrapForEncryption(s));
   }

   public InputStream wrapForEncryption(InputStream s) {
      return (InputStream)this.encryptionKey.map(new Serializable(this, s) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SerializerManager $outer;
         private final InputStream s$2;

         public final InputStream apply(byte[] key) {
            return CryptoStreamUtils$.MODULE$.createCryptoInputStream(this.s$2, this.$outer.org$apache$spark$serializer$SerializerManager$$conf, key);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.s$2 = s$2;
            }
         }
      }).getOrElse(new Serializable(this, s) {
         public static final long serialVersionUID = 0L;
         private final InputStream s$2;

         public final InputStream apply() {
            return this.s$2;
         }

         public {
            this.s$2 = s$2;
         }
      });
   }

   public OutputStream wrapForEncryption(OutputStream s) {
      return (OutputStream)this.encryptionKey.map(new Serializable(this, s) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SerializerManager $outer;
         private final OutputStream s$1;

         public final OutputStream apply(byte[] key) {
            return CryptoStreamUtils$.MODULE$.createCryptoOutputStream(this.s$1, this.$outer.org$apache$spark$serializer$SerializerManager$$conf, key);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.s$1 = s$1;
            }
         }
      }).getOrElse(new Serializable(this, s) {
         public static final long serialVersionUID = 0L;
         private final OutputStream s$1;

         public final OutputStream apply() {
            return this.s$1;
         }

         public {
            this.s$1 = s$1;
         }
      });
   }

   public OutputStream wrapForCompression(BlockId blockId, OutputStream s) {
      return this.shouldCompress(blockId) ? this.compressionCodec().compressedOutputStream(s) : s;
   }

   public InputStream wrapForCompression(BlockId blockId, InputStream s) {
      return this.shouldCompress(blockId) ? this.compressionCodec().compressedInputStream(s) : s;
   }

   public <T> void dataSerializeStream(BlockId blockId, OutputStream outputStream, Iterator<T> values, ClassTag<T> evidence$1) {
      BufferedOutputStream byteStream = new BufferedOutputStream(outputStream);
      boolean autoPick = !(blockId instanceof StreamBlockId);
      SerializerInstance ser = this.getSerializer((ClassTag)Predef$.MODULE$.implicitly(evidence$1), autoPick).newInstance();
      ser.serializeStream(this.wrapForCompression(blockId, (OutputStream)byteStream)).writeAll(values, evidence$1).close();
   }

   public <T> ChunkedByteBuffer dataSerialize(BlockId blockId, Iterator<T> values, ClassTag<T> evidence$2) {
      return this.dataSerializeWithExplicitClassTag(blockId, values, (ClassTag)Predef$.MODULE$.implicitly(evidence$2));
   }

   public ChunkedByteBuffer dataSerializeWithExplicitClassTag(BlockId blockId, Iterator<?> values, ClassTag<?> classTag) {
      ChunkedByteBufferOutputStream bbos = new ChunkedByteBufferOutputStream(4194304, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ByteBuffer apply(int x$1) {
            return ByteBuffer.allocate(x$1);
         }
      });
      BufferedOutputStream byteStream = new BufferedOutputStream(bbos);
      boolean autoPick = !(blockId instanceof StreamBlockId);
      SerializerInstance ser = this.getSerializer(classTag, autoPick).newInstance();
      ser.serializeStream(this.wrapForCompression(blockId, (OutputStream)byteStream)).writeAll(values, ClassTag$.MODULE$.apply(Object.class)).close();
      return bbos.toChunkedByteBuffer();
   }

   public <T> Iterator<T> dataDeserializeStream(BlockId blockId, InputStream inputStream, ClassTag<T> classTag) {
      BufferedInputStream stream = new BufferedInputStream(inputStream);
      boolean autoPick = !(blockId instanceof StreamBlockId);
      return this.getSerializer(classTag, autoPick).newInstance().deserializeStream(this.wrapForCompression(blockId, (InputStream)stream)).asIterator();
   }

   public SerializerManager(Serializer defaultSerializer, SparkConf conf, Option<byte[]> encryptionKey) {
      this.defaultSerializer = defaultSerializer;
      this.org$apache$spark$serializer$SerializerManager$$conf = conf;
      this.encryptionKey = encryptionKey;
      this.kryoSerializer = new KryoSerializer(conf);
      this.stringClassTag = (ClassTag)Predef$.MODULE$.implicitly(ClassTag$.MODULE$.apply(String.class));
      Set primitiveClassTags = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray(new ClassTag[]{ClassTag$.MODULE$.Boolean(), ClassTag$.MODULE$.Byte(), ClassTag$.MODULE$.Char(), ClassTag$.MODULE$.Double(), ClassTag$.MODULE$.Float(), ClassTag$.MODULE$.Int(), ClassTag$.MODULE$.Long(), ClassTag$.MODULE$.Null(), ClassTag$.MODULE$.Short()}));
      Set arrayClassTags = (Set)primitiveClassTags.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ClassTag<Object> apply(ClassTag<?> x$1) {
            return x$1.wrap();
         }
      }, Set$.MODULE$.canBuildFrom());
      this.primitiveAndPrimitiveArrayClassTags = (Set)primitiveClassTags.$plus$plus(arrayClassTags);
      this.compressBroadcast = conf.getBoolean("spark.broadcast.compress", true);
      this.compressShuffle = conf.getBoolean("spark.shuffle.compress", true);
      this.compressRdds = conf.getBoolean("spark.rdd.compress", false);
      this.compressShuffleSpill = conf.getBoolean("spark.shuffle.spill.compress", true);
   }

   public SerializerManager(Serializer defaultSerializer, SparkConf conf) {
      this(defaultSerializer, conf, None$.MODULE$);
   }
}
