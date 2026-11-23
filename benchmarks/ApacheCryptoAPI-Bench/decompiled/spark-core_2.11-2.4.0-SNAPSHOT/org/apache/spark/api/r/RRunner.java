package org.apache.spark.api.r;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.BufferedIterator;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.StringOps;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.util.Try$;

@ScalaSignature(
   bytes = "\u0006\u0001\t%b!B\u0001\u0003\u0001\u0019a!a\u0002*Sk:tWM\u001d\u0006\u0003\u0007\u0011\t\u0011A\u001d\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<WCA\u0007X'\r\u0001a\u0002\u0006\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0002#\u0005)1oY1mC&\u00111\u0003\u0005\u0002\u0007\u0003:L(+\u001a4\u0011\u0005UAR\"\u0001\f\u000b\u0005]1\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005e1\"a\u0002'pO\u001eLgn\u001a\u0005\t7\u0001\u0011\t\u0011)A\u0005;\u0005!a-\u001e8d\u0007\u0001\u00012a\u0004\u0010!\u0013\ty\u0002CA\u0003BeJ\f\u0017\u0010\u0005\u0002\u0010C%\u0011!\u0005\u0005\u0002\u0005\u0005f$X\r\u0003\u0005%\u0001\t\u0005\t\u0015!\u0003&\u00031!Wm]3sS\u0006d\u0017N_3s!\t1\u0013F\u0004\u0002\u0010O%\u0011\u0001\u0006E\u0001\u0007!J,G-\u001a4\n\u0005)Z#AB*ue&twM\u0003\u0002)!!AQ\u0006\u0001B\u0001B\u0003%Q%\u0001\u0006tKJL\u0017\r\\5{KJD\u0001b\f\u0001\u0003\u0002\u0003\u0006I!H\u0001\ra\u0006\u001c7.Y4f\u001d\u0006lWm\u001d\u0005\tc\u0001\u0011\t\u0011)A\u0005e\u0005i!M]8bI\u000e\f7\u000f\u001e,beN\u00042a\u0004\u00104!\r!t'O\u0007\u0002k)\u0011aGB\u0001\nEJ|\u0017\rZ2bgRL!\u0001O\u001b\u0003\u0013\t\u0013x.\u00193dCN$\bC\u0001\u001e@\u001b\u0005Y$B\u0001\u001f>\u0003\u0011a\u0017M\\4\u000b\u0003y\nAA[1wC&\u0011\u0001i\u000f\u0002\u0007\u001f\nTWm\u0019;\t\u0011\t\u0003!\u0011!Q\u0001\n\r\u000bQB\\;n!\u0006\u0014H/\u001b;j_:\u001c\bCA\bE\u0013\t)\u0005CA\u0002J]RD\u0001b\u0012\u0001\u0003\u0002\u0003\u0006I\u0001S\u0001\fSN$\u0015\r^1Ge\u0006lW\r\u0005\u0002\u0010\u0013&\u0011!\n\u0005\u0002\b\u0005>|G.Z1o\u0011!a\u0005A!A!\u0002\u0013i\u0015\u0001C2pY:\u000bW.Z:\u0011\u0007=qR\u0005\u0003\u0005P\u0001\t\u0005\t\u0015!\u0003D\u0003\u0011iw\u000eZ3\t\u000bE\u0003A\u0011\u0001*\u0002\rqJg.\u001b;?))\u0019\u0006-\u00192dI\u00164w\r\u001b\t\u0004)\u0002)V\"\u0001\u0002\u0011\u0005Y;F\u0002\u0001\u0003\u00061\u0002\u0011\r!\u0017\u0002\u0002+F\u0011!,\u0018\t\u0003\u001fmK!\u0001\u0018\t\u0003\u000f9{G\u000f[5oOB\u0011qBX\u0005\u0003?B\u00111!\u00118z\u0011\u0015Y\u0002\u000b1\u0001\u001e\u0011\u0015!\u0003\u000b1\u0001&\u0011\u0015i\u0003\u000b1\u0001&\u0011\u0015y\u0003\u000b1\u0001\u001e\u0011\u0015\t\u0004\u000b1\u00013\u0011\u001d\u0011\u0005\u000b%AA\u0002\rCqa\u0012)\u0011\u0002\u0003\u0007\u0001\nC\u0004M!B\u0005\t\u0019A'\t\u000f=\u0003\u0006\u0013!a\u0001\u0007\"I!\u000e\u0001a\u0001\u0002\u0004%Ia[\u0001\tE>|G\u000fV5nKV\tA\u000e\u0005\u0002\u0010[&\u0011a\u000e\u0005\u0002\u0007\t>,(\r\\3\t\u0013A\u0004\u0001\u0019!a\u0001\n\u0013\t\u0018\u0001\u00042p_R$\u0016.\\3`I\u0015\fHC\u0001:v!\ty1/\u0003\u0002u!\t!QK\\5u\u0011\u001d1x.!AA\u00021\f1\u0001\u001f\u00132\u0011\u0019A\b\u0001)Q\u0005Y\u0006I!m\\8u)&lW\r\t\u0005\nu\u0002\u0001\r\u00111A\u0005\nm\f!\u0002Z1uCN#(/Z1n+\u0005a\bcA?\u0002\u00025\taP\u0003\u0002\u0000{\u0005\u0011\u0011n\\\u0005\u0004\u0003\u0007q(a\u0004#bi\u0006Le\u000e];u'R\u0014X-Y7\t\u0017\u0005\u001d\u0001\u00011AA\u0002\u0013%\u0011\u0011B\u0001\u000fI\u0006$\u0018m\u0015;sK\u0006lw\fJ3r)\r\u0011\u00181\u0002\u0005\tm\u0006\u0015\u0011\u0011!a\u0001y\"9\u0011q\u0002\u0001!B\u0013a\u0018a\u00033bi\u0006\u001cFO]3b[\u0002B\u0011\"a\u0005\u0001\u0005\u0004%\t!!\u0006\u0002\u0011I,\u0017\r\u001a#bi\u0006,\"!a\u0006\u0011\r=\tIbQA\u000f\u0013\r\tY\u0002\u0005\u0002\n\rVt7\r^5p]F\u0012R!a\b:\u0003O1a!!\t\u0001\u0001\u0005u!\u0001\u0004\u001fsK\u001aLg.Z7f]Rt$bAA\u00139\u00051AH]8piz\u00022!`A\u0015\u0013\r\tYC \u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t\u0003_\u0001\u0001\u0015!\u0003\u0002\u0018\u0005I!/Z1e\t\u0006$\u0018\r\t\u0005\b\u0003g\u0001A\u0011AA\u001b\u0003\u001d\u0019w.\u001c9vi\u0016$b!a\u000e\u0002L\u0005e\u0003#BA\u001d\u0003\u000b*f\u0002BA\u001e\u0003\u0003rA!!\u0010\u0002@5\u0011\u00111E\u0005\u0002#%\u0019\u00111\t\t\u0002\u000fA\f7m[1hK&!\u0011qIA%\u0005!IE/\u001a:bi>\u0014(bAA\"!!A\u0011QJA\u0019\u0001\u0004\ty%A\u0007j]B,H/\u0013;fe\u0006$xN\u001d\u0019\u0005\u0003#\n)\u0006\u0005\u0004\u0002:\u0005\u0015\u00131\u000b\t\u0004-\u0006UCaCA,\u0003\u0017\n\t\u0011!A\u0003\u0002e\u00131a\u0018\u00132\u0011\u001d\tY&!\rA\u0002\r\u000ba\u0002]1si&$\u0018n\u001c8J]\u0012,\u0007\u0010C\u0004\u0002`\u0001!I!!\u0019\u0002!M$\u0018M\u001d;Ti\u0012Lg\u000e\u00165sK\u0006$Gc\u0002:\u0002d\u00055\u00141\u0010\u0005\t\u0003K\ni\u00061\u0001\u0002h\u00051q.\u001e;qkR\u00042!`A5\u0013\r\tYG \u0002\r\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\t\u0003_\ni\u00061\u0001\u0002r\u0005!\u0011\u000e^3sa\u0011\t\u0019(a\u001e\u0011\r\u0005e\u0012QIA;!\r1\u0016q\u000f\u0003\f\u0003s\ni'!A\u0001\u0002\u000b\u0005\u0011LA\u0002`IIBq!a\u0017\u0002^\u0001\u00071\tC\u0004\u0002\u0000\u0001!I!!!\u0002\tI,\u0017\r\u001a\u000b\u0002+\"9\u0011Q\u0011\u0001\u0005\n\u0005\u001d\u0015\u0001\u0005:fC\u0012\u001c\u0006.\u001e4gY\u0016$G)\u0019;b)\u0011\tI)a$\u0011\u000b=\tYiQ\u000f\n\u0007\u00055\u0005C\u0001\u0004UkBdWM\r\u0005\b\u0003#\u000b\u0019\t1\u0001D\u0003\u0019aWM\\4uQ\"9\u0011Q\u0013\u0001\u0005\n\u0005]\u0015!\u0005:fC\u0012\u0014\u0015\u0010^3BeJ\f\u0017\u0010R1uCR\u0019Q$!'\t\u000f\u0005E\u00151\u0013a\u0001\u0007\"9\u0011Q\u0014\u0001\u0005\n\u0005}\u0015A\u0004:fC\u0012\u001cFO]5oO\u0012\u000bG/\u0019\u000b\u0004K\u0005\u0005\u0006bBAI\u00037\u0003\raQ\u0004\t\u0003K\u0013\u0001\u0012\u0001\u0002\u0002(\u00069!KU;o]\u0016\u0014\bc\u0001+\u0002*\u001a9\u0011A\u0001E\u0001\u0005\u0005-6cAAU\u001d!9\u0011+!+\u0005\u0002\u0005=FCAAT\u00111\t\u0019,!+A\u0002\u0003\u0005\u000b\u0015BA[\u0003%)'O\u001d+ie\u0016\fG\rE\u0002U\u0003oK1!!/\u0003\u0005Q\u0011UO\u001a4fe\u0016$7\u000b\u001e:fC6$\u0006N]3bI\"a\u0011QXAU\u0001\u0004\u0005\t\u0015)\u0003\u0002@\u0006iA-Y3n_:\u001c\u0005.\u00198oK2\u00042!`Aa\u0013\r\t\u0019M \u0002\u0011\t\u0006$\u0018mT;uaV$8\u000b\u001e:fC6D\u0001\"a2\u0002*\u0012%\u0011\u0011Z\u0001\u0012gR\f'\u000f^*uI>,H\u000f\u00165sK\u0006$G\u0003BA[\u0003\u0017D\u0001\"!4\u0002F\u0002\u0007\u0011qZ\u0001\u0005aJ|7\rE\u0002;\u0003#L1!a5<\u0005\u001d\u0001&o\\2fgND\u0001\"a6\u0002*\u0012%\u0011\u0011\\\u0001\u000fGJ,\u0017\r^3S!J|7-Z:t)\u0019\t),a7\u0002`\"9\u0011Q\\Ak\u0001\u0004\u0019\u0015\u0001\u00029peRDq!!9\u0002V\u0002\u0007Q%\u0001\u0004tGJL\u0007\u000f\u001e\u0005\t\u0003K\fI\u000b\"\u0001\u0002h\u0006i1M]3bi\u0016\u0014vk\u001c:lKJ$B!!.\u0002j\"9\u0011Q\\Ar\u0001\u0004\u0019\u0005BCAw\u0003S\u000b\n\u0011\"\u0001\u0002p\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIY*B!!=\u0003\bU\u0011\u00111\u001f\u0016\u0004\u0007\u0006U8FAA|!\u0011\tIPa\u0001\u000e\u0005\u0005m(\u0002BA\u007f\u0003\u007f\f\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\t\u0005\u0001#\u0001\u0006b]:|G/\u0019;j_:LAA!\u0002\u0002|\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\u0005\ra\u000bYO1\u0001Z\u0011)\u0011Y!!+\u0012\u0002\u0013\u0005!QB\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001c\u0016\t\t=!1C\u000b\u0003\u0005#Q3\u0001SA{\t\u0019A&\u0011\u0002b\u00013\"Q!qCAU#\u0003%\tA!\u0007\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00139+\u0011\u0011YBa\b\u0016\u0005\tu!fA'\u0002v\u00121\u0001L!\u0006C\u0002eC!Ba\t\u0002*F\u0005I\u0011\u0001B\u0013\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%sU!\u0011\u0011\u001fB\u0014\t\u0019A&\u0011\u0005b\u00013\u0002"
)
public class RRunner<U> implements Logging {
   public final byte[] org$apache$spark$api$r$RRunner$$func;
   public final String org$apache$spark$api$r$RRunner$$deserializer;
   public final String org$apache$spark$api$r$RRunner$$serializer;
   public final byte[] org$apache$spark$api$r$RRunner$$packageNames;
   public final Broadcast<Object>[] org$apache$spark$api$r$RRunner$$broadcastVars;
   public final int org$apache$spark$api$r$RRunner$$numPartitions;
   public final boolean org$apache$spark$api$r$RRunner$$isDataFrame;
   public final String[] org$apache$spark$api$r$RRunner$$colNames;
   public final int org$apache$spark$api$r$RRunner$$mode;
   private double bootTime;
   private DataInputStream org$apache$spark$api$r$RRunner$$dataStream;
   private final Function1<Object, Object> readData;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static <U> int $lessinit$greater$default$9() {
      return RRunner$.MODULE$.$lessinit$greater$default$9();
   }

   public static <U> String[] $lessinit$greater$default$8() {
      return RRunner$.MODULE$.$lessinit$greater$default$8();
   }

   public static <U> boolean $lessinit$greater$default$7() {
      return RRunner$.MODULE$.$lessinit$greater$default$7();
   }

   public static <U> int $lessinit$greater$default$6() {
      return RRunner$.MODULE$.$lessinit$greater$default$6();
   }

   public static BufferedStreamThread createRWorker(int var0) {
      return RRunner$.MODULE$.createRWorker(var0);
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   private double bootTime() {
      return this.bootTime;
   }

   private void bootTime_$eq(double x$1) {
      this.bootTime = x$1;
   }

   public DataInputStream org$apache$spark$api$r$RRunner$$dataStream() {
      return this.org$apache$spark$api$r$RRunner$$dataStream;
   }

   private void org$apache$spark$api$r$RRunner$$dataStream_$eq(DataInputStream x$1) {
      this.org$apache$spark$api$r$RRunner$$dataStream = x$1;
   }

   public Function1<Object, Object> readData() {
      return this.readData;
   }

   public Iterator<U> compute(Iterator<?> inputIterator, int partitionIndex) {
      this.bootTime_$eq((double)System.currentTimeMillis() / (double)1000.0F);
      ServerSocket serverSocket = new ServerSocket(0, 2, InetAddress.getByName("localhost"));
      int listenPort = serverSocket.getLocalPort();
      BufferedStreamThread errThread = RRunner$.MODULE$.createRWorker(listenPort);
      serverSocket.setSoTimeout(10000);
      Socket inSocket = serverSocket.accept();
      this.startStdinThread(inSocket.getOutputStream(), inputIterator, partitionIndex);
      Socket outSocket = serverSocket.accept();
      BufferedInputStream inputStream = new BufferedInputStream(outSocket.getInputStream());
      this.org$apache$spark$api$r$RRunner$$dataStream_$eq(new DataInputStream(inputStream));
      serverSocket.close();

      try {
         return new Iterator<U>(this) {
            private U _nextObj;
            // $FF: synthetic field
            private final RRunner $outer;

            public Iterator<U> seq() {
               return Iterator$class.seq(this);
            }

            public boolean isEmpty() {
               return Iterator$class.isEmpty(this);
            }

            public boolean isTraversableAgain() {
               return Iterator$class.isTraversableAgain(this);
            }

            public boolean hasDefiniteSize() {
               return Iterator$class.hasDefiniteSize(this);
            }

            public Iterator<U> take(int n) {
               return Iterator$class.take(this, n);
            }

            public Iterator<U> drop(int n) {
               return Iterator$class.drop(this, n);
            }

            public Iterator<U> slice(int from, int until) {
               return Iterator$class.slice(this, from, until);
            }

            public <B> Iterator<B> map(Function1<U, B> f) {
               return Iterator$class.map(this, f);
            }

            public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
               return Iterator$class.$plus$plus(this, that);
            }

            public <B> Iterator<B> flatMap(Function1<U, GenTraversableOnce<B>> f) {
               return Iterator$class.flatMap(this, f);
            }

            public Iterator<U> filter(Function1<U, Object> p) {
               return Iterator$class.filter(this, p);
            }

            public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<U, B, Object> p) {
               return Iterator$class.corresponds(this, that, p);
            }

            public Iterator<U> withFilter(Function1<U, Object> p) {
               return Iterator$class.withFilter(this, p);
            }

            public Iterator<U> filterNot(Function1<U, Object> p) {
               return Iterator$class.filterNot(this, p);
            }

            public <B> Iterator<B> collect(PartialFunction<U, B> pf) {
               return Iterator$class.collect(this, pf);
            }

            public <B> Iterator<B> scanLeft(B z, Function2<B, U, B> op) {
               return Iterator$class.scanLeft(this, z, op);
            }

            public <B> Iterator<B> scanRight(B z, Function2<U, B, B> op) {
               return Iterator$class.scanRight(this, z, op);
            }

            public Iterator<U> takeWhile(Function1<U, Object> p) {
               return Iterator$class.takeWhile(this, p);
            }

            public Tuple2<Iterator<U>, Iterator<U>> partition(Function1<U, Object> p) {
               return Iterator$class.partition(this, p);
            }

            public Tuple2<Iterator<U>, Iterator<U>> span(Function1<U, Object> p) {
               return Iterator$class.span(this, p);
            }

            public Iterator<U> dropWhile(Function1<U, Object> p) {
               return Iterator$class.dropWhile(this, p);
            }

            public <B> Iterator<Tuple2<U, B>> zip(Iterator<B> that) {
               return Iterator$class.zip(this, that);
            }

            public <A1> Iterator<A1> padTo(int len, A1 elem) {
               return Iterator$class.padTo(this, len, elem);
            }

            public Iterator<Tuple2<U, Object>> zipWithIndex() {
               return Iterator$class.zipWithIndex(this);
            }

            public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
               return Iterator$class.zipAll(this, that, thisElem, thatElem);
            }

            public <U> void foreach(Function1<U, U> f) {
               Iterator$class.foreach(this, f);
            }

            public boolean forall(Function1<U, Object> p) {
               return Iterator$class.forall(this, p);
            }

            public boolean exists(Function1<U, Object> p) {
               return Iterator$class.exists(this, p);
            }

            public boolean contains(Object elem) {
               return Iterator$class.contains(this, elem);
            }

            public Option<U> find(Function1<U, Object> p) {
               return Iterator$class.find(this, p);
            }

            public int indexWhere(Function1<U, Object> p) {
               return Iterator$class.indexWhere(this, p);
            }

            public <B> int indexOf(B elem) {
               return Iterator$class.indexOf(this, elem);
            }

            public BufferedIterator<U> buffered() {
               return Iterator$class.buffered(this);
            }

            public <B> Iterator<U>.GroupedIterator<B> grouped(int size) {
               return Iterator$class.grouped(this, size);
            }

            public <B> Iterator<U>.GroupedIterator<B> sliding(int size, int step) {
               return Iterator$class.sliding(this, size, step);
            }

            public int length() {
               return Iterator$class.length(this);
            }

            public Tuple2<Iterator<U>, Iterator<U>> duplicate() {
               return Iterator$class.duplicate(this);
            }

            public <B> Iterator<B> patch(int from, Iterator<B> patchElems, int replaced) {
               return Iterator$class.patch(this, from, patchElems, replaced);
            }

            public <B> void copyToArray(Object xs, int start, int len) {
               Iterator$class.copyToArray(this, xs, start, len);
            }

            public boolean sameElements(Iterator<?> that) {
               return Iterator$class.sameElements(this, that);
            }

            public Traversable<U> toTraversable() {
               return Iterator$class.toTraversable(this);
            }

            public Iterator<U> toIterator() {
               return Iterator$class.toIterator(this);
            }

            public Stream<U> toStream() {
               return Iterator$class.toStream(this);
            }

            public String toString() {
               return Iterator$class.toString(this);
            }

            public <B> int sliding$default$2() {
               return Iterator$class.sliding$default$2(this);
            }

            public List<U> reversed() {
               return TraversableOnce$class.reversed(this);
            }

            public int size() {
               return TraversableOnce$class.size(this);
            }

            public boolean nonEmpty() {
               return TraversableOnce$class.nonEmpty(this);
            }

            public int count(Function1<U, Object> p) {
               return TraversableOnce$class.count(this, p);
            }

            public <B> Option<B> collectFirst(PartialFunction<U, B> pf) {
               return TraversableOnce$class.collectFirst(this, pf);
            }

            public <B> B $div$colon(B z, Function2<B, U, B> op) {
               return (B)TraversableOnce$class.$div$colon(this, z, op);
            }

            public <B> B $colon$bslash(B z, Function2<U, B, B> op) {
               return (B)TraversableOnce$class.$colon$bslash(this, z, op);
            }

            public <B> B foldLeft(B z, Function2<B, U, B> op) {
               return (B)TraversableOnce$class.foldLeft(this, z, op);
            }

            public <B> B foldRight(B z, Function2<U, B, B> op) {
               return (B)TraversableOnce$class.foldRight(this, z, op);
            }

            public <B> B reduceLeft(Function2<B, U, B> op) {
               return (B)TraversableOnce$class.reduceLeft(this, op);
            }

            public <B> B reduceRight(Function2<U, B, B> op) {
               return (B)TraversableOnce$class.reduceRight(this, op);
            }

            public <B> Option<B> reduceLeftOption(Function2<B, U, B> op) {
               return TraversableOnce$class.reduceLeftOption(this, op);
            }

            public <B> Option<B> reduceRightOption(Function2<U, B, B> op) {
               return TraversableOnce$class.reduceRightOption(this, op);
            }

            public <A1> A1 reduce(Function2<A1, A1, A1> op) {
               return (A1)TraversableOnce$class.reduce(this, op);
            }

            public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
               return TraversableOnce$class.reduceOption(this, op);
            }

            public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
               return (A1)TraversableOnce$class.fold(this, z, op);
            }

            public <B> B aggregate(Function0<B> z, Function2<B, U, B> seqop, Function2<B, B, B> combop) {
               return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
            }

            public <B> B sum(Numeric<B> num) {
               return (B)TraversableOnce$class.sum(this, num);
            }

            public <B> B product(Numeric<B> num) {
               return (B)TraversableOnce$class.product(this, num);
            }

            public <B> U min(Ordering<B> cmp) {
               return (U)TraversableOnce$class.min(this, cmp);
            }

            public <B> U max(Ordering<B> cmp) {
               return (U)TraversableOnce$class.max(this, cmp);
            }

            public <B> U maxBy(Function1<U, B> f, Ordering<B> cmp) {
               return (U)TraversableOnce$class.maxBy(this, f, cmp);
            }

            public <B> U minBy(Function1<U, B> f, Ordering<B> cmp) {
               return (U)TraversableOnce$class.minBy(this, f, cmp);
            }

            public <B> void copyToBuffer(Buffer<B> dest) {
               TraversableOnce$class.copyToBuffer(this, dest);
            }

            public <B> void copyToArray(Object xs, int start) {
               TraversableOnce$class.copyToArray(this, xs, start);
            }

            public <B> void copyToArray(Object xs) {
               TraversableOnce$class.copyToArray(this, xs);
            }

            public <B> Object toArray(ClassTag<B> evidence$1) {
               return TraversableOnce$class.toArray(this, evidence$1);
            }

            public List<U> toList() {
               return TraversableOnce$class.toList(this);
            }

            public Iterable<U> toIterable() {
               return TraversableOnce$class.toIterable(this);
            }

            public Seq<U> toSeq() {
               return TraversableOnce$class.toSeq(this);
            }

            public IndexedSeq<U> toIndexedSeq() {
               return TraversableOnce$class.toIndexedSeq(this);
            }

            public <B> Buffer<B> toBuffer() {
               return TraversableOnce$class.toBuffer(this);
            }

            public <B> Set<B> toSet() {
               return TraversableOnce$class.toSet(this);
            }

            public Vector<U> toVector() {
               return TraversableOnce$class.toVector(this);
            }

            public <Col> Col to(CanBuildFrom<Nothing$, U, Col> cbf) {
               return (Col)TraversableOnce$class.to(this, cbf);
            }

            public <T, U> Map<T, U> toMap(Predef.$less$colon$less<U, Tuple2<T, U>> ev) {
               return TraversableOnce$class.toMap(this, ev);
            }

            public String mkString(String start, String sep, String end) {
               return TraversableOnce$class.mkString(this, start, sep, end);
            }

            public String mkString(String sep) {
               return TraversableOnce$class.mkString(this, sep);
            }

            public String mkString() {
               return TraversableOnce$class.mkString(this);
            }

            public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
               return TraversableOnce$class.addString(this, b, start, sep, end);
            }

            public StringBuilder addString(StringBuilder b, String sep) {
               return TraversableOnce$class.addString(this, b, sep);
            }

            public StringBuilder addString(StringBuilder b) {
               return TraversableOnce$class.addString(this, b);
            }

            public U next() {
               Object obj = this._nextObj();
               if (this.hasNext()) {
                  this._nextObj_$eq(this.$outer.org$apache$spark$api$r$RRunner$$read());
               }

               return (U)obj;
            }

            private U _nextObj() {
               return this._nextObj;
            }

            private void _nextObj_$eq(U x$1) {
               this._nextObj = (U)x$1;
            }

            public boolean hasNext() {
               boolean hasMore = this._nextObj() != null;
               if (!hasMore) {
                  this.$outer.org$apache$spark$api$r$RRunner$$dataStream().close();
               }

               return hasMore;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  TraversableOnce$class.$init$(this);
                  Iterator$class.$init$(this);
                  this._nextObj = (U)$outer.org$apache$spark$api$r$RRunner$$read();
               }
            }
         };
      } catch (Exception var10) {
         throw new SparkException((new StringBuilder()).append((Object)"R computation failed with\n ").append((Object)errThread.getLines()).toString());
      }
   }

   private void startStdinThread(OutputStream output, Iterator<?> iter, int partitionIndex) {
      SparkEnv env = SparkEnv$.MODULE$.get();
      TaskContext taskContext = TaskContext$.MODULE$.get();
      int bufferSize = (new StringOps(Predef$.MODULE$.augmentString(System.getProperty("spark.buffer.size", "65536")))).toInt();
      BufferedOutputStream stream = new BufferedOutputStream(output, bufferSize);
      (new Thread(this, output, iter, partitionIndex, env, taskContext, stream) {
         // $FF: synthetic field
         private final RRunner $outer;
         public final OutputStream output$1;
         private final Iterator iter$1;
         private final int partitionIndex$1;
         private final SparkEnv env$1;
         private final TaskContext taskContext$1;
         private final BufferedOutputStream stream$1;

         public void run() {
            try {
               SparkEnv$.MODULE$.set(this.env$1);
               TaskContext$.MODULE$.setTaskContext(this.taskContext$1);
               DataOutputStream dataOut = new DataOutputStream(this.stream$1);
               dataOut.writeInt(this.partitionIndex$1);
               SerDe$.MODULE$.writeString(dataOut, this.$outer.org$apache$spark$api$r$RRunner$$deserializer);
               SerDe$.MODULE$.writeString(dataOut, this.$outer.org$apache$spark$api$r$RRunner$$serializer);
               dataOut.writeInt(this.$outer.org$apache$spark$api$r$RRunner$$packageNames.length);
               dataOut.write(this.$outer.org$apache$spark$api$r$RRunner$$packageNames);
               dataOut.writeInt(this.$outer.org$apache$spark$api$r$RRunner$$func.length);
               dataOut.write(this.$outer.org$apache$spark$api$r$RRunner$$func);
               dataOut.writeInt(this.$outer.org$apache$spark$api$r$RRunner$$broadcastVars.length);
               Predef$.MODULE$.refArrayOps(this.$outer.org$apache$spark$api$r$RRunner$$broadcastVars).foreach(new Serializable(this, dataOut) {
                  public static final long serialVersionUID = 0L;
                  private final DataOutputStream dataOut$1;

                  public final void apply(Broadcast<Object> broadcast) {
                     this.dataOut$1.writeInt((int)broadcast.id());
                     byte[] broadcastByteArr = (byte[])broadcast.value();
                     this.dataOut$1.writeInt(broadcastByteArr.length);
                     this.dataOut$1.write(broadcastByteArr);
                  }

                  public {
                     this.dataOut$1 = dataOut$1;
                  }
               });
               dataOut.writeInt(this.$outer.org$apache$spark$api$r$RRunner$$numPartitions);
               dataOut.writeInt(this.$outer.org$apache$spark$api$r$RRunner$$mode);
               if (this.$outer.org$apache$spark$api$r$RRunner$$isDataFrame) {
                  SerDe$.MODULE$.writeObject(dataOut, this.$outer.org$apache$spark$api$r$RRunner$$colNames, (JVMObjectTracker)null);
               }

               if (this.iter$1.hasNext()) {
                  dataOut.writeInt(1);
               } else {
                  dataOut.writeInt(0);
               }

               PrintStream printOut = new PrintStream(this.stream$1);
               this.iter$1.foreach(new Serializable(this, dataOut, printOut) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  public final DataOutputStream dataOut$1;
                  public final PrintStream printOut$1;

                  public final void apply(Object elem) {
                     boolean var2 = false;
                     Tuple2 var3 = null;
                     if (elem instanceof Tuple2) {
                        var2 = true;
                        var3 = (Tuple2)elem;
                        Object key = var3._1();
                        Object innerIter = var3._2();
                        if (innerIter instanceof Iterator) {
                           Iterator var7 = (Iterator)innerIter;
                           var7.foreach(new Serializable(this) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;

                              public final void apply(Object innerElem) {
                                 this.$outer.org$apache$spark$api$r$RRunner$$anon$$anonfun$$$outer().org$apache$spark$api$r$RRunner$$anon$$writeElem$1(innerElem, this.$outer.dataOut$1, this.$outer.printOut$1);
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                 }
                              }
                           });
                           this.dataOut$1.writeByte(114);
                           this.$outer.org$apache$spark$api$r$RRunner$$anon$$writeElem$1(key, this.dataOut$1, this.printOut$1);
                           BoxedUnit var12 = BoxedUnit.UNIT;
                           return;
                        }
                     }

                     if (var2) {
                        Object key = var3._1();
                        Object value = var3._2();
                        this.$outer.org$apache$spark$api$r$RRunner$$anon$$writeElem$1(key, this.dataOut$1, this.printOut$1);
                        this.$outer.org$apache$spark$api$r$RRunner$$anon$$writeElem$1(value, this.dataOut$1, this.printOut$1);
                        BoxedUnit var8 = BoxedUnit.UNIT;
                     } else {
                        this.$outer.org$apache$spark$api$r$RRunner$$anon$$writeElem$1(elem, this.dataOut$1, this.printOut$1);
                        BoxedUnit var11 = BoxedUnit.UNIT;
                     }

                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$api$r$RRunner$$anon$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.dataOut$1 = dataOut$1;
                        this.printOut$1 = printOut$1;
                     }
                  }
               });
               this.stream$1.flush();
            } catch (Exception var7) {
               this.$outer.logError(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "R Writer thread got an exception";
                  }
               }, var7);
            } finally {
               Try$.MODULE$.apply(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     this.$outer.output$1.close();
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

         }

         public final void org$apache$spark$api$r$RRunner$$anon$$writeElem$1(Object elem, DataOutputStream dataOut$1, PrintStream printOut$1) {
            label53: {
               String var10000 = this.$outer.org$apache$spark$api$r$RRunner$$deserializer;
               String var4 = SerializationFormats$.MODULE$.BYTE();
               if (var10000 == null) {
                  if (var4 == null) {
                     break label53;
                  }
               } else if (var10000.equals(var4)) {
                  break label53;
               }

               label54: {
                  var10000 = this.$outer.org$apache$spark$api$r$RRunner$$deserializer;
                  String var6 = SerializationFormats$.MODULE$.ROW();
                  if (var10000 == null) {
                     if (var6 == null) {
                        break label54;
                     }
                  } else if (var10000.equals(var6)) {
                     break label54;
                  }

                  var10000 = this.$outer.org$apache$spark$api$r$RRunner$$deserializer;
                  String var7 = SerializationFormats$.MODULE$.STRING();
                  if (var10000 == null) {
                     if (var7 != null) {
                        return;
                     }
                  } else if (!var10000.equals(var7)) {
                     return;
                  }

                  printOut$1.println(elem);
                  return;
               }

               dataOut$1.write((byte[])elem);
               return;
            }

            byte[] elemArr = (byte[])elem;
            dataOut$1.writeInt(elemArr.length);
            dataOut$1.write(elemArr);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.output$1 = output$1;
               this.iter$1 = iter$1;
               this.partitionIndex$1 = partitionIndex$1;
               this.env$1 = env$1;
               this.taskContext$1 = taskContext$1;
               this.stream$1 = stream$1;
            }
         }
      }).start();
   }

   public U org$apache$spark$api$r$RRunner$$read() {
      try {
         int length = this.org$apache$spark$api$r$RRunner$$dataStream().readInt();
         Object var4;
         if (SpecialLengths$.MODULE$.TIMING_DATA() == length) {
            double boot = this.org$apache$spark$api$r$RRunner$$dataStream().readDouble() - this.bootTime();
            double init = this.org$apache$spark$api$r$RRunner$$dataStream().readDouble();
            double broadcast = this.org$apache$spark$api$r$RRunner$$dataStream().readDouble();
            double input = this.org$apache$spark$api$r$RRunner$$dataStream().readDouble();
            double compute = this.org$apache$spark$api$r$RRunner$$dataStream().readDouble();
            double output = this.org$apache$spark$api$r$RRunner$$dataStream().readDouble();
            this.logInfo(new Serializable(this, boot, init, broadcast, input, compute, output) {
               public static final long serialVersionUID = 0L;
               private final double boot$1;
               private final double init$1;
               private final double broadcast$1;
               private final double input$1;
               private final double compute$1;
               private final double output$2;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Times: boot = %.3f s, init = %.3f s, broadcast = %.3f s, read-input = %.3f s, compute = %.3f s, write-output = %.3f s, total = %.3f s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.boot$1), BoxesRunTime.boxToDouble(this.init$1), BoxesRunTime.boxToDouble(this.broadcast$1), BoxesRunTime.boxToDouble(this.input$1), BoxesRunTime.boxToDouble(this.compute$1), BoxesRunTime.boxToDouble(this.output$2), BoxesRunTime.boxToDouble(this.boot$1 + this.init$1 + this.broadcast$1 + this.input$1 + this.compute$1 + this.output$2)}));
               }

               public {
                  this.boot$1 = boot$1;
                  this.init$1 = init$1;
                  this.broadcast$1 = broadcast$1;
                  this.input$1 = input$1;
                  this.compute$1 = compute$1;
                  this.output$2 = output$2;
               }
            });
            var4 = this.org$apache$spark$api$r$RRunner$$read();
         } else {
            if (length < 0) {
               throw new MatchError(BoxesRunTime.boxToInteger(length));
            }

            var4 = this.readData().apply(BoxesRunTime.boxToInteger(length));
         }

         return (U)var4;
      } catch (EOFException var17) {
         throw new SparkException("R worker exited unexpectedly (cranshed)", var17);
      }
   }

   public Tuple2<Object, byte[]> org$apache$spark$api$r$RRunner$$readShuffledData(int length) {
      switch (length) {
         default:
            Tuple2 var10000;
            if (length == 2) {
               int hashedKey = this.org$apache$spark$api$r$RRunner$$dataStream().readInt();
               int contentPairsLength = this.org$apache$spark$api$r$RRunner$$dataStream().readInt();
               byte[] contentPairs = new byte[contentPairsLength];
               this.org$apache$spark$api$r$RRunner$$dataStream().readFully(contentPairs);
               var10000 = new Tuple2(BoxesRunTime.boxToInteger(hashedKey), contentPairs);
            } else {
               var10000 = null;
            }

            return var10000;
      }
   }

   public byte[] org$apache$spark$api$r$RRunner$$readByteArrayData(int length) {
      switch (length) {
         default:
            byte[] var10000;
            if (length > 0) {
               byte[] obj = new byte[length];
               this.org$apache$spark$api$r$RRunner$$dataStream().readFully(obj);
               var10000 = obj;
            } else {
               var10000 = null;
            }

            return var10000;
      }
   }

   public String org$apache$spark$api$r$RRunner$$readStringData(int length) {
      switch (length) {
         default:
            return length > 0 ? SerDe$.MODULE$.readStringBytes(this.org$apache$spark$api$r$RRunner$$dataStream(), length) : null;
      }
   }

   public RRunner(byte[] func, String deserializer, String serializer, byte[] packageNames, Broadcast<Object>[] broadcastVars, int numPartitions, boolean isDataFrame, String[] colNames, int mode) {
      this.org$apache$spark$api$r$RRunner$$func = func;
      this.org$apache$spark$api$r$RRunner$$deserializer = deserializer;
      this.org$apache$spark$api$r$RRunner$$serializer = serializer;
      this.org$apache$spark$api$r$RRunner$$packageNames = packageNames;
      this.org$apache$spark$api$r$RRunner$$broadcastVars = broadcastVars;
      this.org$apache$spark$api$r$RRunner$$numPartitions = numPartitions;
      this.org$apache$spark$api$r$RRunner$$isDataFrame = isDataFrame;
      this.org$apache$spark$api$r$RRunner$$colNames = colNames;
      this.org$apache$spark$api$r$RRunner$$mode = mode;
      Logging$class.$init$(this);
      Serializable var14;
      switch (numPartitions) {
         case -1:
            Serializable var13;
            label20: {
               label19: {
                  String var10001 = SerializationFormats$.MODULE$.STRING();
                  if (var10001 == null) {
                     if (serializer == null) {
                        break label19;
                     }
                  } else if (var10001.equals(serializer)) {
                     break label19;
                  }

                  var13 = new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final RRunner $outer;

                     public final byte[] apply(int length) {
                        return this.$outer.org$apache$spark$api$r$RRunner$$readByteArrayData(length);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  };
                  break label20;
               }

               var13 = new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final RRunner $outer;

                  public final String apply(int length) {
                     return this.$outer.org$apache$spark$api$r$RRunner$$readStringData(length);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               };
            }

            var14 = var13;
            break;
         default:
            var14 = new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final RRunner $outer;

               public final Tuple2<Object, byte[]> apply(int length) {
                  return this.$outer.org$apache$spark$api$r$RRunner$$readShuffledData(length);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            };
      }

      this.readData = var14;
   }
}
