package org.apache.spark.broadcast;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Adler32;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.io.CompressionCodec;
import org.apache.spark.io.CompressionCodec$;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.storage.BlockData;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.storage.BlockResult;
import org.apache.spark.storage.BroadcastBlockId;
import org.apache.spark.storage.BroadcastBlockId$;
import org.apache.spark.storage.ByteBufferBlockData;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.storage.StorageLevel$;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.io.ChunkedByteBuffer;
import scala.Array$;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.util.Random$;

@ScalaSignature(
   bytes = "\u0006\u0001\teb!B\u0001\u0003\u0001\u0011Q!\u0001\u0005+peJ,g\u000e\u001e\"s_\u0006$7-Y:u\u0015\t\u0019A!A\u0005ce>\fGmY1ti*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x-\u0006\u0002\f%M!\u0001\u0001D\u0010&!\ria\u0002E\u0007\u0002\u0005%\u0011qB\u0001\u0002\n\u0005J|\u0017\rZ2bgR\u0004\"!\u0005\n\r\u0001\u0011)1\u0003\u0001b\u0001+\t\tAk\u0001\u0001\u0012\u0005Ya\u0002CA\f\u001b\u001b\u0005A\"\"A\r\u0002\u000bM\u001c\u0017\r\\1\n\u0005mA\"a\u0002(pi\"Lgn\u001a\t\u0003/uI!A\b\r\u0003\u0007\u0005s\u0017\u0010\u0005\u0002!G5\t\u0011E\u0003\u0002#\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002%C\t9Aj\\4hS:<\u0007C\u0001\u0014,\u001b\u00059#B\u0001\u0015*\u0003\tIwNC\u0001+\u0003\u0011Q\u0017M^1\n\u00051:#\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\u0018\u0001\u0005\u0003\u0005\u000b\u0011\u0002\t\u0002\u0007=\u0014'\u000eC\u00051\u0001\t\u0005\t\u0015!\u00032i\u0005\u0011\u0011\u000e\u001a\t\u0003/IJ!a\r\r\u0003\t1{gnZ\u0005\u0003a9A\u0001B\u000e\u0001\u0003\u0004\u0003\u0006YaN\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004c\u0001\u001d<!5\t\u0011H\u0003\u0002;1\u00059!/\u001a4mK\u000e$\u0018B\u0001\u001f:\u0005!\u0019E.Y:t)\u0006<\u0007\"\u0002 \u0001\t\u0003y\u0014A\u0002\u001fj]&$h\bF\u0002A\u0007\u0012#\"!\u0011\"\u0011\u00075\u0001\u0001\u0003C\u00037{\u0001\u000fq\u0007C\u0003/{\u0001\u0007\u0001\u0003C\u00031{\u0001\u0007\u0011\u0007\u0003\u0005G\u0001!\u0015\r\u0011\"\u0003H\u0003\u0019yf/\u00197vKV\t\u0001\u0003\u0003\u0005J\u0001!\u0005\t\u0015)\u0003\u0011\u0003\u001dyf/\u00197vK\u0002B#\u0001S&\u0011\u0005]a\u0015BA'\u0019\u0005%!(/\u00198tS\u0016tG\u000fC\u0005P\u0001\u0001\u0007\t\u0019!C\u0005!\u0006\u00012m\\7qe\u0016\u001c8/[8o\u0007>$WmY\u000b\u0002#B\u0019qC\u0015+\n\u0005MC\"AB(qi&|g\u000e\u0005\u0002V/6\taK\u0003\u0002)\t%\u0011\u0001L\u0016\u0002\u0011\u0007>l\u0007O]3tg&|gnQ8eK\u000eD\u0011B\u0017\u0001A\u0002\u0003\u0007I\u0011B.\u0002)\r|W\u000e\u001d:fgNLwN\\\"pI\u0016\u001cw\fJ3r)\tav\f\u0005\u0002\u0018;&\u0011a\f\u0007\u0002\u0005+:LG\u000fC\u0004a3\u0006\u0005\t\u0019A)\u0002\u0007a$\u0013\u0007\u0003\u0004c\u0001\u0001\u0006K!U\u0001\u0012G>l\u0007O]3tg&|gnQ8eK\u000e\u0004\u0003FA1L\u0011%)\u0007\u00011AA\u0002\u0013%a-A\u0005cY>\u001c7nU5{KV\tq\r\u0005\u0002\u0018Q&\u0011\u0011\u000e\u0007\u0002\u0004\u0013:$\b\"C6\u0001\u0001\u0004\u0005\r\u0011\"\u0003m\u00035\u0011Gn\\2l'&TXm\u0018\u0013fcR\u0011A,\u001c\u0005\bA*\f\t\u00111\u0001h\u0011\u0019y\u0007\u0001)Q\u0005O\u0006Q!\r\\8dWNK'0\u001a\u0011)\u00059\\\u0005\"\u0002:\u0001\t\u0013\u0019\u0018aB:fi\u000e{gN\u001a\u000b\u00039RDQ!^9A\u0002Y\fAaY8oMB\u0011q\u000f_\u0007\u0002\t%\u0011\u0011\u0010\u0002\u0002\n'B\f'o[\"p]\u001aDqa\u001f\u0001C\u0002\u0013%A0A\u0006ce>\fGmY1ti&#W#A?\u0011\u0007y\f\u0019!D\u0001\u0000\u0015\r\t\t\u0001B\u0001\bgR|'/Y4f\u0013\r\t)a \u0002\u0011\u0005J|\u0017\rZ2bgR\u0014En\\2l\u0013\u0012Dq!!\u0003\u0001A\u0003%Q0\u0001\u0007ce>\fGmY1ti&#\u0007\u0005\u0003\u0005\u0002\u000e\u0001\u0011\r\u0011\"\u0003g\u0003%qW/\u001c\"m_\u000e\\7\u000fC\u0004\u0002\u0012\u0001\u0001\u000b\u0011B4\u0002\u00159,XN\u00117pG.\u001c\b\u0005C\u0005\u0002\u0016\u0001\u0001\r\u0011\"\u0003\u0002\u0018\u0005y1\r[3dWN,X.\u00128bE2,G-\u0006\u0002\u0002\u001aA\u0019q#a\u0007\n\u0007\u0005u\u0001DA\u0004C_>dW-\u00198\t\u0013\u0005\u0005\u0002\u00011A\u0005\n\u0005\r\u0012aE2iK\u000e\\7/^7F]\u0006\u0014G.\u001a3`I\u0015\fHc\u0001/\u0002&!I\u0001-a\b\u0002\u0002\u0003\u0007\u0011\u0011\u0004\u0005\t\u0003S\u0001\u0001\u0015)\u0003\u0002\u001a\u0005\u00012\r[3dWN,X.\u00128bE2,G\r\t\u0005\f\u0003[\u0001\u0001\u0019!a\u0001\n\u0013\ty#A\u0005dQ\u0016\u001c7n];ngV\u0011\u0011\u0011\u0007\t\u0005/\u0005Mr-C\u0002\u00026a\u0011Q!\u0011:sCfD1\"!\u000f\u0001\u0001\u0004\u0005\r\u0011\"\u0003\u0002<\u0005i1\r[3dWN,Xn]0%KF$2\u0001XA\u001f\u0011%\u0001\u0017qGA\u0001\u0002\u0004\t\t\u0004\u0003\u0005\u0002B\u0001\u0001\u000b\u0015BA\u0019\u0003)\u0019\u0007.Z2lgVl7\u000f\t\u0005\b\u0003\u000b\u0002A\u0011KA$\u0003!9W\r\u001e,bYV,G#\u0001\t\t\u000f\u0005-\u0003\u0001\"\u0003\u0002N\u0005a1-\u00197d\u0007\",7m[:v[R\u0019q-a\u0014\t\u0011\u0005E\u0013\u0011\na\u0001\u0003'\nQA\u00197pG.\u0004B!!\u0016\u0002\\5\u0011\u0011q\u000b\u0006\u0004\u00033J\u0013a\u00018j_&!\u0011QLA,\u0005)\u0011\u0015\u0010^3Ck\u001a4WM\u001d\u0005\b\u0003C\u0002A\u0011BA2\u0003-9(/\u001b;f\u00052|7m[:\u0015\u0007\u001d\f)\u0007C\u0004\u0002h\u0005}\u0003\u0019\u0001\t\u0002\u000bY\fG.^3\t\u000f\u0005-\u0004\u0001\"\u0003\u0002n\u0005Q!/Z1e\u00052|7m[:\u0015\u0005\u0005=\u0004#B\f\u00024\u0005E\u0004c\u0001@\u0002t%\u0019\u0011QO@\u0003\u0013\tcwnY6ECR\f\u0007bBA=\u0001\u0011E\u00131P\u0001\fI>,f\u000e]3sg&\u001cH\u000fF\u0002]\u0003{B\u0001\"a \u0002x\u0001\u0007\u0011\u0011D\u0001\tE2|7m[5oO\"9\u00111\u0011\u0001\u0005R\u0005\u0015\u0015!\u00033p\t\u0016\u001cHO]8z)\ra\u0016q\u0011\u0005\t\u0003\u007f\n\t\t1\u0001\u0002\u001a!9\u00111\u0012\u0001\u0005\n\u00055\u0015aC<sSR,wJ\u00196fGR$2\u0001XAH\u0011!\t\t*!#A\u0002\u0005M\u0015aA8viB\u0019a%!&\n\u0007\u0005]uE\u0001\nPE*,7\r^(viB,Ho\u0015;sK\u0006l\u0007bBAN\u0001\u0011%\u0011qI\u0001\u0013e\u0016\fGM\u0011:pC\u0012\u001c\u0017m\u001d;CY>\u001c7\u000eC\u0004\u0002 \u0002!I!!)\u0002\u0017I,G.Z1tK2{7m\u001b\u000b\u00049\u0006\r\u0006\u0002CAS\u0003;\u0003\r!a*\u0002\u000f\tdwnY6JIB\u0019a0!+\n\u0007\u0005-vPA\u0004CY>\u001c7.\u00133\t\u001b\u0005=\u0006\u0001%A\u0002\u0002\u0003%I!!-5\u0003!\u0019X\u000f]3sI%$W#A\u0019\b\u000f\u0005U&\u0001#\u0003\u00028\u0006\u0001Bk\u001c:sK:$(I]8bI\u000e\f7\u000f\u001e\t\u0004\u001b\u0005efAB\u0001\u0003\u0011\u0013\tYlE\u0004\u0002:\u0006uv$a1\u0011\u0007]\ty,C\u0002\u0002Bb\u0011a!\u00118z%\u00164\u0007cA\f\u0002F&\u0011A\u0006\u0007\u0005\b}\u0005eF\u0011AAe)\t\t9\f\u0003\u0005\u0002N\u0006eF\u0011AAh\u00039\u0011Gn\\2lS\u001aLxJ\u00196fGR,B!!5\u0002`RQ\u00111[Aq\u0003G\f)/a=\u0015\t\u0005U\u0017q\u001b\t\u0006/\u0005M\u00121\u000b\u0005\u000b\u00033\fY-!AA\u0004\u0005m\u0017AC3wS\u0012,gnY3%eA!\u0001hOAo!\r\t\u0012q\u001c\u0003\u0007'\u0005-'\u0019A\u000b\t\u000f9\nY\r1\u0001\u0002^\"1Q-a3A\u0002\u001dD\u0001\"a:\u0002L\u0002\u0007\u0011\u0011^\u0001\u000bg\u0016\u0014\u0018.\u00197ju\u0016\u0014\b\u0003BAv\u0003_l!!!<\u000b\u0007\u0005\u001dH!\u0003\u0003\u0002r\u00065(AC*fe&\fG.\u001b>fe\"1q*a3A\u0002EC\u0001\"a>\u0002:\u0012\u0005\u0011\u0011`\u0001\u0011k:\u0014En\\2lS\u001aLxJ\u00196fGR,B!a?\u0003\u0002QA\u0011Q B\u0005\u0005+\u00119\u0002\u0006\u0003\u0002\u0000\n\r\u0001cA\t\u0003\u0002\u001111#!>C\u0002UA!B!\u0002\u0002v\u0006\u0005\t9\u0001B\u0004\u0003))g/\u001b3f]\u000e,Ge\r\t\u0005qm\ny\u0010\u0003\u0005\u0003\f\u0005U\b\u0019\u0001B\u0007\u0003\u0019\u0011Gn\\2lgB)q#a\r\u0003\u0010A\u0019aE!\u0005\n\u0007\tMqEA\u0006J]B,Ho\u0015;sK\u0006l\u0007\u0002CAt\u0003k\u0004\r!!;\t\r=\u000b)\u00101\u0001R\u0011!\u0011Y\"!/\u0005\u0002\tu\u0011!C;oa\u0016\u00148/[:u)\u001da&q\u0004B\u0011\u0005KAa\u0001\rB\r\u0001\u0004\t\u0004\u0002\u0003B\u0012\u00053\u0001\r!!\u0007\u0002!I,Wn\u001c<f\rJ|W\u000e\u0012:jm\u0016\u0014\b\u0002CA@\u00053\u0001\r!!\u0007\t\u0015\t%\u0012\u0011XA\u0001\n\u0013\u0011Y#A\u0006sK\u0006$'+Z:pYZ,GC\u0001B\u0017!\u0011\u0011yC!\u000e\u000e\u0005\tE\"b\u0001B\u001aS\u0005!A.\u00198h\u0013\u0011\u00119D!\r\u0003\r=\u0013'.Z2u\u0001"
)
public class TorrentBroadcast<T> extends Broadcast<T> {
   public final ClassTag<T> org$apache$spark$broadcast$TorrentBroadcast$$evidence$1;
   private transient T _value;
   private transient Option<CompressionCodec> org$apache$spark$broadcast$TorrentBroadcast$$compressionCodec;
   private transient int blockSize;
   private final BroadcastBlockId org$apache$spark$broadcast$TorrentBroadcast$$broadcastId;
   private final int numBlocks;
   private boolean org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled;
   private int[] org$apache$spark$broadcast$TorrentBroadcast$$checksums;
   private transient volatile boolean bitmap$trans$0;

   public static <T> T unBlockifyObject(InputStream[] var0, Serializer var1, Option<CompressionCodec> var2, ClassTag<T> var3) {
      return (T)TorrentBroadcast$.MODULE$.unBlockifyObject(var0, var1, var2, var3);
   }

   public static <T> ByteBuffer[] blockifyObject(T var0, int var1, Serializer var2, Option<CompressionCodec> var3, ClassTag<T> var4) {
      return TorrentBroadcast$.MODULE$.blockifyObject(var0, var1, var2, var3, var4);
   }

   private Object _value$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$trans$0) {
            this._value = (T)this.readBroadcastBlock();
            this.bitmap$trans$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this._value;
   }

   // $FF: synthetic method
   public long org$apache$spark$broadcast$TorrentBroadcast$$super$id() {
      return super.id();
   }

   private T _value() {
      return (T)(this.bitmap$trans$0 ? this._value : this._value$lzycompute());
   }

   public Option<CompressionCodec> org$apache$spark$broadcast$TorrentBroadcast$$compressionCodec() {
      return this.org$apache$spark$broadcast$TorrentBroadcast$$compressionCodec;
   }

   private void org$apache$spark$broadcast$TorrentBroadcast$$compressionCodec_$eq(Option<CompressionCodec> x$1) {
      this.org$apache$spark$broadcast$TorrentBroadcast$$compressionCodec = x$1;
   }

   private int blockSize() {
      return this.blockSize;
   }

   private void blockSize_$eq(int x$1) {
      this.blockSize = x$1;
   }

   public void org$apache$spark$broadcast$TorrentBroadcast$$setConf(SparkConf conf) {
      this.org$apache$spark$broadcast$TorrentBroadcast$$compressionCodec_$eq((Option)(conf.getBoolean("spark.broadcast.compress", true) ? new Some(CompressionCodec$.MODULE$.createCodec(conf)) : None$.MODULE$));
      this.blockSize_$eq((int)conf.getSizeAsKb("spark.broadcast.blockSize", "4m") * 1024);
      this.org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled_$eq(conf.getBoolean("spark.broadcast.checksum", true));
   }

   public BroadcastBlockId org$apache$spark$broadcast$TorrentBroadcast$$broadcastId() {
      return this.org$apache$spark$broadcast$TorrentBroadcast$$broadcastId;
   }

   private int numBlocks() {
      return this.numBlocks;
   }

   public boolean org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled() {
      return this.org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled;
   }

   private void org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled_$eq(boolean x$1) {
      this.org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled = x$1;
   }

   public int[] org$apache$spark$broadcast$TorrentBroadcast$$checksums() {
      return this.org$apache$spark$broadcast$TorrentBroadcast$$checksums;
   }

   private void org$apache$spark$broadcast$TorrentBroadcast$$checksums_$eq(int[] x$1) {
      this.org$apache$spark$broadcast$TorrentBroadcast$$checksums = x$1;
   }

   public T getValue() {
      return (T)this._value();
   }

   public int org$apache$spark$broadcast$TorrentBroadcast$$calcChecksum(ByteBuffer block) {
      Adler32 adler = new Adler32();
      if (block.hasArray()) {
         adler.update(block.array(), block.arrayOffset() + block.position(), block.limit() - block.position());
      } else {
         byte[] bytes = new byte[block.remaining()];
         block.duplicate().get(bytes);
         adler.update(bytes);
      }

      return (int)adler.getValue();
   }

   private int writeBlocks(T value) {
      BlockManager blockManager = SparkEnv$.MODULE$.get().blockManager();
      if (blockManager.putSingle(this.org$apache$spark$broadcast$TorrentBroadcast$$broadcastId(), value, StorageLevel$.MODULE$.MEMORY_AND_DISK(), false, this.org$apache$spark$broadcast$TorrentBroadcast$$evidence$1)) {
         ByteBuffer[] blocks = TorrentBroadcast$.MODULE$.blockifyObject(value, this.blockSize(), SparkEnv$.MODULE$.get().serializer(), this.org$apache$spark$broadcast$TorrentBroadcast$$compressionCodec(), this.org$apache$spark$broadcast$TorrentBroadcast$$evidence$1);
         if (this.org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled()) {
            this.org$apache$spark$broadcast$TorrentBroadcast$$checksums_$eq(new int[blocks.length]);
         }

         Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])blocks).zipWithIndex(Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).foreach(new Serializable(this, blockManager) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TorrentBroadcast $outer;
            private final BlockManager blockManager$1;

            public final void apply(Tuple2<ByteBuffer, Object> x0$1) {
               if (x0$1 != null) {
                  ByteBuffer block = (ByteBuffer)x0$1._1();
                  int i = x0$1._2$mcI$sp();
                  if (this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled()) {
                     this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$checksums()[i] = this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$calcChecksum(block);
                  }

                  BroadcastBlockId pieceId = new BroadcastBlockId(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$super$id(), (new StringBuilder()).append((Object)"piece").append((Object)BoxesRunTime.boxToInteger(i)).toString());
                  ChunkedByteBuffer bytes = new ChunkedByteBuffer(block.duplicate());
                  if (this.blockManager$1.putBytes(pieceId, bytes, StorageLevel$.MODULE$.MEMORY_AND_DISK_SER(), true, this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$evidence$1)) {
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  } else {
                     throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to store ", " of ", " in local BlockManager"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{pieceId, this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$broadcastId()})));
                  }
               } else {
                  throw new MatchError(x0$1);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.blockManager$1 = blockManager$1;
               }
            }
         });
         return blocks.length;
      } else {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to store ", " in BlockManager"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$broadcast$TorrentBroadcast$$broadcastId()})));
      }
   }

   public BlockData[] org$apache$spark$broadcast$TorrentBroadcast$$readBlocks() {
      BlockData[] blocks = new BlockData[this.numBlocks()];
      BlockManager bm = SparkEnv$.MODULE$.get().blockManager();
      ((IterableLike)Random$.MODULE$.shuffle((TraversableOnce)Seq$.MODULE$.range(BoxesRunTime.boxToInteger(0), BoxesRunTime.boxToInteger(this.numBlocks()), Numeric.IntIsIntegral$.MODULE$), Seq$.MODULE$.canBuildFrom())).foreach(new Serializable(this, blocks, bm) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TorrentBroadcast $outer;
         private final BlockData[] blocks$1;
         private final BlockManager bm$1;

         public final void apply(int pid) {
            this.apply$mcVI$sp(pid);
         }

         public void apply$mcVI$sp(int pid) {
            BroadcastBlockId pieceId = new BroadcastBlockId(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$super$id(), (new StringBuilder()).append((Object)"piece").append((Object)BoxesRunTime.boxToInteger(pid)).toString());
            this.$outer.logDebug(new Serializable(this, pieceId) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final BroadcastBlockId pieceId$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Reading piece ", " of ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.pieceId$1, this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$broadcastId()}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.pieceId$1 = pieceId$1;
                  }
               }
            });
            Option var3 = this.bm$1.getLocalBytes(pieceId);
            if (var3 instanceof Some) {
               Some var4 = (Some)var3;
               BlockData block = (BlockData)var4.x();
               this.blocks$1[pid] = block;
               this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$releaseLock(pieceId);
               BoxedUnit var6 = BoxedUnit.UNIT;
            } else {
               if (!None$.MODULE$.equals(var3)) {
                  throw new MatchError(var3);
               }

               Option var7 = this.bm$1.getRemoteBytes(pieceId);
               if (!(var7 instanceof Some)) {
                  if (None$.MODULE$.equals(var7)) {
                     throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to get ", " of ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{pieceId, this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$broadcastId()})));
                  }

                  throw new MatchError(var7);
               }

               Some var8 = (Some)var7;
               ChunkedByteBuffer b = (ChunkedByteBuffer)var8.x();
               if (this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled()) {
                  int sum = this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$calcChecksum(b.chunks()[0]);
                  if (sum != this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$checksums()[pid]) {
                     throw new SparkException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"corrupt remote block ", " of ", ":"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{pieceId, this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$broadcastId()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" ", " != ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(sum), BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$checksums()[pid])}))).toString());
                  }
               }

               if (!this.bm$1.putBytes(pieceId, b, StorageLevel$.MODULE$.MEMORY_AND_DISK_SER(), true, this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$evidence$1)) {
                  throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to store ", " of ", " in local BlockManager"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{pieceId, this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$broadcastId()})));
               }

               this.blocks$1[pid] = new ByteBufferBlockData(b, true);
               BoxedUnit var10 = BoxedUnit.UNIT;
               BoxedUnit var12 = BoxedUnit.UNIT;
            }

         }

         // $FF: synthetic method
         public TorrentBroadcast org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.blocks$1 = blocks$1;
               this.bm$1 = bm$1;
            }
         }
      });
      return blocks;
   }

   public void doUnpersist(boolean blocking) {
      TorrentBroadcast$.MODULE$.unpersist(super.id(), false, blocking);
   }

   public void doDestroy(boolean blocking) {
      TorrentBroadcast$.MODULE$.unpersist(super.id(), true, blocking);
   }

   private void writeObject(ObjectOutputStream out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TorrentBroadcast $outer;
         private final ObjectOutputStream out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.$outer.assertValid();
            this.out$1.defaultWriteObject();
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

   private T readBroadcastBlock() {
      return (T)Utils$.MODULE$.tryOrIOException(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TorrentBroadcast $outer;

         public final T apply() {
            synchronized(TorrentBroadcast$.MODULE$) {
               ReferenceMap broadcastCache = SparkEnv$.MODULE$.get().broadcastManager().cachedValues();
               Object var2 = Option$.MODULE$.apply(broadcastCache.get(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$broadcastId())).map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final T apply(Object x$1) {
                     return (T)x$1;
                  }
               }).getOrElse(new Serializable(this, broadcastCache) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final ReferenceMap broadcastCache$1;

                  public final T apply() {
                     this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$setConf(SparkEnv$.MODULE$.get().conf());
                     BlockManager blockManager = SparkEnv$.MODULE$.get().blockManager();
                     Option var2 = blockManager.getLocalValues(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$broadcastId());
                     Object var5;
                     if (var2 instanceof Some) {
                        Some var3 = (Some)var2;
                        BlockResult blockResult = (BlockResult)var3.x();
                        if (!blockResult.data().hasNext()) {
                           throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to get locally stored broadcast data: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$broadcastId()})));
                        }

                        Object x = blockResult.data().next();
                        this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$releaseLock(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$broadcastId());
                        if (x == null) {
                           BoxedUnit var16 = BoxedUnit.UNIT;
                        } else {
                           this.broadcastCache$1.put(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$broadcastId(), x);
                        }

                        var5 = x;
                     } else {
                        if (!None$.MODULE$.equals(var2)) {
                           throw new MatchError(var2);
                        }

                        this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().logInfo(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"Started reading broadcast variable ").append((Object)BoxesRunTime.boxToLong(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$super$id())).toString();
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                        long startTimeMs = System.currentTimeMillis();
                        BlockData[] blocks = this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$readBlocks();
                        this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().logInfo(new Serializable(this, startTimeMs) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final long startTimeMs$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"Reading broadcast variable ").append((Object)BoxesRunTime.boxToLong(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$super$id())).append((Object)" took").append((Object)Utils$.MODULE$.getUsedTimeMs(this.startTimeMs$1)).toString();
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.startTimeMs$1 = startTimeMs$1;
                              }
                           }
                        });

                        Object var15;
                        try {
                           Object obj = TorrentBroadcast$.MODULE$.unBlockifyObject((InputStream[])Predef$.MODULE$.refArrayOps(blocks).map(new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final InputStream apply(BlockData x$2) {
                                 return x$2.toInputStream();
                              }
                           }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(InputStream.class))), SparkEnv$.MODULE$.get().serializer(), this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$compressionCodec(), this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$evidence$1);
                           StorageLevel storageLevel = StorageLevel$.MODULE$.MEMORY_AND_DISK();
                           if (!blockManager.putSingle(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$broadcastId(), obj, storageLevel, false, this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$evidence$1)) {
                              throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to store ", " in BlockManager"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$broadcastId()})));
                           }

                           if (obj == null) {
                              BoxedUnit var10000 = BoxedUnit.UNIT;
                           } else {
                              this.broadcastCache$1.put(this.$outer.org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer().org$apache$spark$broadcast$TorrentBroadcast$$broadcastId(), obj);
                           }

                           var15 = obj;
                        } finally {
                           Predef$.MODULE$.refArrayOps(blocks).foreach(new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final void apply(BlockData x$3) {
                                 x$3.dispose();
                              }
                           });
                        }

                        var5 = var15;
                     }

                     return (T)var5;
                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.broadcastCache$1 = broadcastCache$1;
                     }
                  }
               });
               return (T)var2;
            }
         }

         // $FF: synthetic method
         public TorrentBroadcast org$apache$spark$broadcast$TorrentBroadcast$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public void org$apache$spark$broadcast$TorrentBroadcast$$releaseLock(BlockId blockId) {
      BlockManager blockManager = SparkEnv$.MODULE$.get().blockManager();
      Option var3 = Option$.MODULE$.apply(TaskContext$.MODULE$.get());
      if (var3 instanceof Some) {
         Some var4 = (Some)var3;
         TaskContext taskContext = (TaskContext)var4.x();
         taskContext.addTaskCompletionListener((Function1)(new Serializable(this, blockId, blockManager) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$1;
            private final BlockManager blockManager$2;

            public final void apply(TaskContext x$4) {
               this.blockManager$2.releaseLock(this.blockId$1, this.blockManager$2.releaseLock$default$2());
            }

            public {
               this.blockId$1 = blockId$1;
               this.blockManager$2 = blockManager$2;
            }
         }));
         BoxedUnit var6 = BoxedUnit.UNIT;
      } else {
         if (!None$.MODULE$.equals(var3)) {
            throw new MatchError(var3);
         }

         blockManager.releaseLock(blockId, blockManager.releaseLock$default$2());
         BoxedUnit var7 = BoxedUnit.UNIT;
      }

   }

   public TorrentBroadcast(T obj, long id, ClassTag<T> evidence$1) {
      super(id, evidence$1);
      this.org$apache$spark$broadcast$TorrentBroadcast$$evidence$1 = evidence$1;
      this.org$apache$spark$broadcast$TorrentBroadcast$$setConf(SparkEnv$.MODULE$.get().conf());
      this.org$apache$spark$broadcast$TorrentBroadcast$$broadcastId = new BroadcastBlockId(super.id(), BroadcastBlockId$.MODULE$.apply$default$2());
      this.numBlocks = this.writeBlocks(obj);
      this.org$apache$spark$broadcast$TorrentBroadcast$$checksumEnabled = false;
   }
}
