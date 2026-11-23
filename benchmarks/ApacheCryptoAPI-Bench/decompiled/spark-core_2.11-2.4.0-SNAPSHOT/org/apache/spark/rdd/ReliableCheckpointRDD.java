package org.apache.spark.rdd;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkException;
import org.apache.spark.TaskContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.util.SerializableConfiguration;
import scala.Array$;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.TraversableLike;
import scala.collection.immutable.List$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayOps;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\t%d!B\u0001\u0003\u0001\u0011Q!!\u0006*fY&\f'\r\\3DQ\u0016\u001c7\u000e]8j]R\u0014F\t\u0012\u0006\u0003\u0007\u0011\t1A\u001d3e\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<WCA\u0006\u0013'\t\u0001A\u0002E\u0002\u000e\u001dAi\u0011AA\u0005\u0003\u001f\t\u0011Qb\u00115fG.\u0004x.\u001b8u%\u0012#\u0005CA\t\u0013\u0019\u0001!Qa\u0005\u0001C\u0002U\u0011\u0011\u0001V\u0002\u0001#\t1B\u0004\u0005\u0002\u001855\t\u0001DC\u0001\u001a\u0003\u0015\u00198-\u00197b\u0013\tY\u0002DA\u0004O_RD\u0017N\\4\u0011\u0005]i\u0012B\u0001\u0010\u0019\u0005\r\te.\u001f\u0005\tA\u0001\u0011\t\u0011)A\u0005C\u0005\u00111o\u0019\t\u0003E\rj\u0011\u0001B\u0005\u0003I\u0011\u0011Ab\u00159be.\u001cuN\u001c;fqRD\u0001B\n\u0001\u0003\u0006\u0004%\taJ\u0001\u000fG\",7m\u001b9pS:$\b+\u0019;i+\u0005A\u0003CA\u0015-\u001d\t9\"&\u0003\u0002,1\u00051\u0001K]3eK\u001aL!!\f\u0018\u0003\rM#(/\u001b8h\u0015\tY\u0003\u0004\u0003\u00051\u0001\t\u0005\t\u0015!\u0003)\u0003=\u0019\u0007.Z2la>Lg\u000e\u001e)bi\"\u0004\u0003\u0002\u0003\u001a\u0001\u0005\u0003\u0005\u000b\u0011B\u001a\u0002\u0019}\u0003\u0018M\u001d;ji&|g.\u001a:\u0011\u0007]!d'\u0003\u000261\t1q\n\u001d;j_:\u0004\"AI\u001c\n\u0005a\"!a\u0003)beRLG/[8oKJD\u0001B\u000f\u0001\u0003\u0004\u0003\u0006YaO\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004c\u0001\u001f@!5\tQH\u0003\u0002?1\u00059!/\u001a4mK\u000e$\u0018B\u0001!>\u0005!\u0019E.Y:t)\u0006<\u0007\"\u0002\"\u0001\t\u0003\u0019\u0015A\u0002\u001fj]&$h\b\u0006\u0003E\u000f\"KECA#G!\ri\u0001\u0001\u0005\u0005\u0006u\u0005\u0003\u001da\u000f\u0005\u0006A\u0005\u0003\r!\t\u0005\u0006M\u0005\u0003\r\u0001\u000b\u0005\be\u0005\u0003\n\u00111\u00014\u0011\u001dY\u0005A1A\u0005\n1\u000b!\u0002[1e_>\u00048i\u001c8g+\u0005i\u0005C\u0001(T\u001b\u0005y%B\u0001)R\u0003\u0011\u0019wN\u001c4\u000b\u0005I3\u0011A\u00025bI>|\u0007/\u0003\u0002U\u001f\ni1i\u001c8gS\u001e,(/\u0019;j_:DaA\u0016\u0001!\u0002\u0013i\u0015a\u00035bI>|\u0007oQ8oM\u0002B#!\u0016-\u0011\u0005]I\u0016B\u0001.\u0019\u0005%!(/\u00198tS\u0016tG\u000fC\u0004]\u0001\t\u0007I\u0011B/\u0002\u000b\r\u0004\u0018\r\u001e5\u0016\u0003y\u0003\"a\u00182\u000e\u0003\u0001T!!Y)\u0002\u0005\u0019\u001c\u0018BA2a\u0005\u0011\u0001\u0016\r\u001e5\t\r\u0015\u0004\u0001\u0015!\u0003_\u0003\u0019\u0019\u0007/\u0019;iA!\u0012A\r\u0017\u0005\bC\u0002\u0011\r\u0011\"\u0003i+\u0005I\u0007CA0k\u0013\tY\u0007M\u0001\u0006GS2,7+_:uK6Da!\u001c\u0001!\u0002\u0013I\u0017a\u00014tA!\u0012A\u000e\u0017\u0005\ba\u0002\u0011\r\u0011\"\u0003r\u0003=\u0011'o\\1eG\u0006\u001cH/\u001a3D_:4W#\u0001:\u0011\u0007M4\b0D\u0001u\u0015\t)H!A\u0005ce>\fGmY1ti&\u0011q\u000f\u001e\u0002\n\u0005J|\u0017\rZ2bgR\u0004\"!\u001f?\u000e\u0003iT!a\u001f\u0003\u0002\tU$\u0018\u000e\\\u0005\u0003{j\u0014\u0011dU3sS\u0006d\u0017N_1cY\u0016\u001cuN\u001c4jOV\u0014\u0018\r^5p]\"1q\u0010\u0001Q\u0001\nI\f\u0001C\u0019:pC\u0012\u001c\u0017m\u001d;fI\u000e{gN\u001a\u0011\t\u0013\u0005\r\u0001A1A\u0005B\u0005\u0015\u0011!E4fi\u000eCWmY6q_&tGOR5mKV\u0011\u0011q\u0001\t\u0004/QB\u0003\u0002CA\u0006\u0001\u0001\u0006I!a\u0002\u0002%\u001d,Go\u00115fG.\u0004x.\u001b8u\r&dW\r\t\u0005\n\u0003\u001f\u0001!\u0019!C!\u0003#\t1\u0002]1si&$\u0018n\u001c8feV\t1\u0007C\u0004\u0002\u0016\u0001\u0001\u000b\u0011B\u001a\u0002\u0019A\f'\u000f^5uS>tWM\u001d\u0011\t\u000f\u0005e\u0001\u0001\"\u0015\u0002\u001c\u0005iq-\u001a;QCJ$\u0018\u000e^5p]N,\"!!\b\u0011\u000b]\ty\"a\t\n\u0007\u0005\u0005\u0002DA\u0003BeJ\f\u0017\u0010E\u0002#\u0003KI1!a\n\u0005\u0005%\u0001\u0016M\u001d;ji&|g\u000eC\u0004\u0002,\u0001!\t&!\f\u0002+\u001d,G\u000f\u0015:fM\u0016\u0014(/\u001a3M_\u000e\fG/[8ogR!\u0011qFA$!\u0015\t\t$!\u0011)\u001d\u0011\t\u0019$!\u0010\u000f\t\u0005U\u00121H\u0007\u0003\u0003oQ1!!\u000f\u0015\u0003\u0019a$o\\8u}%\t\u0011$C\u0002\u0002@a\tq\u0001]1dW\u0006<W-\u0003\u0003\u0002D\u0005\u0015#aA*fc*\u0019\u0011q\b\r\t\u0011\u0005%\u0013\u0011\u0006a\u0001\u0003G\tQa\u001d9mSRDq!!\u0014\u0001\t\u0003\ny%A\u0004d_6\u0004X\u000f^3\u0015\r\u0005E\u0013qKA-!\u0015\t\t$a\u0015\u0011\u0013\u0011\t)&!\u0012\u0003\u0011%#XM]1u_JD\u0001\"!\u0013\u0002L\u0001\u0007\u00111\u0005\u0005\t\u00037\nY\u00051\u0001\u0002^\u000591m\u001c8uKb$\bc\u0001\u0012\u0002`%\u0019\u0011\u0011\r\u0003\u0003\u0017Q\u000b7o[\"p]R,\u0007\u0010^\u0004\t\u0003K\u0012\u0001\u0012\u0001\u0003\u0002h\u0005)\"+\u001a7jC\ndWm\u00115fG.\u0004x.\u001b8u%\u0012#\u0005cA\u0007\u0002j\u00199\u0011A\u0001E\u0001\t\u0005-4\u0003CA5\u0003[\n\u0019(a \u0011\u0007]\ty'C\u0002\u0002ra\u0011a!\u00118z%\u00164\u0007\u0003BA;\u0003wj!!a\u001e\u000b\u0007\u0005eD!\u0001\u0005j]R,'O\\1m\u0013\u0011\ti(a\u001e\u0003\u000f1{wmZ5oOB\u0019q#!!\n\u0007\u0005\r\u0005D\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0004C\u0003S\"\t!a\"\u0015\u0005\u0005\u001d\u0004\u0002CAF\u0003S\"I!!$\u0002%\rDWmY6q_&tGOR5mK:\u000bW.\u001a\u000b\u0004Q\u0005=\u0005\u0002CAI\u0003\u0013\u0003\r!a%\u0002\u001dA\f'\u000f^5uS>t\u0017J\u001c3fqB\u0019q#!&\n\u0007\u0005]\u0005DA\u0002J]RD\u0001\"a'\u0002j\u0011%\u0011QT\u0001\u001eG\",7m\u001b9pS:$\b+\u0019:uSRLwN\\3s\r&dWMT1nKR\t\u0001\u0006\u0003\u0005\u0002\"\u0006%D\u0011AAR\u0003u9(/\u001b;f%\u0012#Ek\\\"iK\u000e\\\u0007o\\5oi\u0012K'/Z2u_JLX\u0003BAS\u0003[#\u0002\"a*\u00026\u0006}\u00161\u0019\u000b\u0005\u0003S\u000by\u000b\u0005\u0003\u000e\u0001\u0005-\u0006cA\t\u0002.\u001211#a(C\u0002UA!\"!-\u0002 \u0006\u0005\t9AAZ\u0003))g/\u001b3f]\u000e,GE\r\t\u0005y}\nY\u000b\u0003\u0005\u00028\u0006}\u0005\u0019AA]\u0003-y'/[4j]\u0006d'\u000b\u0012#\u0011\u000b5\tY,a+\n\u0007\u0005u&AA\u0002S\t\u0012Cq!!1\u0002 \u0002\u0007\u0001&A\u0007dQ\u0016\u001c7\u000e]8j]R$\u0015N\u001d\u0005\u000b\u0003\u000b\fy\n%AA\u0002\u0005M\u0015!\u00032m_\u000e\\7+\u001b>f\u0011!\tI-!\u001b\u0005\u0002\u0005-\u0017AH<sSR,\u0007+\u0019:uSRLwN\u001c+p\u0007\",7m\u001b9pS:$h)\u001b7f+\u0011\ti-!9\u0015\u0011\u0005=\u0017Q^Ay\u0003g$b!!5\u0002d\u0006\u001dH\u0003BAj\u00033\u00042aFAk\u0013\r\t9\u000e\u0007\u0002\u0005+:LG\u000f\u0003\u0006\u0002\\\u0006\u001d\u0017\u0011!a\u0002\u0003;\f!\"\u001a<jI\u0016t7-\u001a\u00134!\u0011at(a8\u0011\u0007E\t\t\u000f\u0002\u0004\u0014\u0003\u000f\u0014\r!\u0006\u0005\t\u0003K\f9\r1\u0001\u0002^\u0005\u00191\r\u001e=\t\u0011\u0005%\u0018q\u0019a\u0001\u0003W\f\u0001\"\u001b;fe\u0006$xN\u001d\t\u0007\u0003c\t\u0019&a8\t\u000f\u0005=\u0018q\u0019a\u0001Q\u0005!\u0001/\u0019;i\u0011\u0019\u0001\u0018q\u0019a\u0001e\"Q\u0011QYAd!\u0003\u0005\r!a%\t\u0011\u0005]\u0018\u0011\u000eC\u0005\u0003s\fqd\u001e:ji\u0016\u0004\u0016M\u001d;ji&|g.\u001a:U_\u000eCWmY6q_&tG\u000fR5s)!\t\u0019.a?\u0002~\u0006}\bB\u0002\u0011\u0002v\u0002\u0007\u0011\u0005C\u0004\u0002\u0010\u0005U\b\u0019\u0001\u001c\t\u000f\t\u0005\u0011Q\u001fa\u0001=\u0006\t2\r[3dWB|\u0017N\u001c;ESJ\u0004\u0016\r\u001e5\t\u0011\t\u0015\u0011\u0011\u000eC\u0005\u0005\u000f\tqD]3bI\u000eCWmY6q_&tG/\u001a3QCJ$\u0018\u000e^5p]\u0016\u0014h)\u001b7f)\u0015\u0019$\u0011\u0002B\u0006\u0011\u0019\u0001#1\u0001a\u0001C!9!\u0011\u0001B\u0002\u0001\u0004A\u0003\u0002\u0003B\b\u0003S\"\tA!\u0005\u0002%I,\u0017\rZ\"iK\u000e\\\u0007o\\5oi\u001aKG.Z\u000b\u0005\u0005'\u0011I\u0002\u0006\u0005\u0003\u0016\tm!Q\u0004B\u0010!\u0019\t\t$a\u0015\u0003\u0018A\u0019\u0011C!\u0007\u0005\rM\u0011iA1\u0001\u0016\u0011\u001d\tyO!\u0004A\u0002yCa\u0001\u001dB\u0007\u0001\u0004\u0011\b\u0002CA.\u0005\u001b\u0001\r!!\u0018\t\u0015\t\r\u0012\u0011NI\u0001\n\u0003\u0011)#A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HeM\u000b\u0005\u0005O\u0011i$\u0006\u0002\u0003*)\u001a1Ga\u000b,\u0005\t5\u0002\u0003\u0002B\u0018\u0005si!A!\r\u000b\t\tM\"QG\u0001\nk:\u001c\u0007.Z2lK\u0012T1Aa\u000e\u0019\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0005w\u0011\tDA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016$aa\u0005B\u0011\u0005\u0004)\u0002B\u0003B!\u0003S\n\n\u0011\"\u0001\u0003D\u00059sO]5uKJ#E\tV8DQ\u0016\u001c7\u000e]8j]R$\u0015N]3di>\u0014\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00134+\u0011\u0011)E!\u0013\u0016\u0005\t\u001d#\u0006BAJ\u0005W!aa\u0005B \u0005\u0004)\u0002B\u0003B'\u0003S\n\n\u0011\"\u0001\u0003P\u0005AsO]5uKB\u000b'\u000f^5uS>tGk\\\"iK\u000e\\\u0007o\\5oi\u001aKG.\u001a\u0013eK\u001a\fW\u000f\u001c;%gU!!Q\tB)\t\u0019\u0019\"1\nb\u0001+!Q!QKA5\u0003\u0003%IAa\u0016\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u00053\u0002BAa\u0017\u0003f5\u0011!Q\f\u0006\u0005\u0005?\u0012\t'\u0001\u0003mC:<'B\u0001B2\u0003\u0011Q\u0017M^1\n\t\t\u001d$Q\f\u0002\u0007\u001f\nTWm\u0019;"
)
public class ReliableCheckpointRDD<T> extends CheckpointRDD<T> {
   private final String checkpointPath;
   private final transient Configuration hadoopConf;
   private final transient Path cpath;
   private final transient FileSystem fs;
   private final Broadcast<SerializableConfiguration> broadcastedConf;
   private final Option<String> getCheckpointFile;
   private final Option<Partitioner> partitioner;

   public static <T> int writePartitionToCheckpointFile$default$3() {
      return ReliableCheckpointRDD$.MODULE$.writePartitionToCheckpointFile$default$3();
   }

   public static <T> int writeRDDToCheckpointDirectory$default$3() {
      return ReliableCheckpointRDD$.MODULE$.writeRDDToCheckpointDirectory$default$3();
   }

   public static <T> Option<Partitioner> $lessinit$greater$default$3() {
      return ReliableCheckpointRDD$.MODULE$.$lessinit$greater$default$3();
   }

   public static <T> Iterator<T> readCheckpointFile(Path var0, Broadcast<SerializableConfiguration> var1, TaskContext var2) {
      return ReliableCheckpointRDD$.MODULE$.<T>readCheckpointFile(var0, var1, var2);
   }

   public static <T> void writePartitionToCheckpointFile(String var0, Broadcast<SerializableConfiguration> var1, int var2, TaskContext var3, Iterator<T> var4, ClassTag<T> var5) {
      ReliableCheckpointRDD$.MODULE$.writePartitionToCheckpointFile(var0, var1, var2, var3, var4, var5);
   }

   public static <T> ReliableCheckpointRDD<T> writeRDDToCheckpointDirectory(RDD<T> var0, String var1, int var2, ClassTag<T> var3) {
      return ReliableCheckpointRDD$.MODULE$.<T>writeRDDToCheckpointDirectory(var0, var1, var2, var3);
   }

   public String checkpointPath() {
      return this.checkpointPath;
   }

   private Configuration hadoopConf() {
      return this.hadoopConf;
   }

   private Path cpath() {
      return this.cpath;
   }

   private FileSystem fs() {
      return this.fs;
   }

   private Broadcast<SerializableConfiguration> broadcastedConf() {
      return this.broadcastedConf;
   }

   public Option<String> getCheckpointFile() {
      return this.getCheckpointFile;
   }

   public Option<Partitioner> partitioner() {
      return this.partitioner;
   }

   public Partition[] getPartitions() {
      Path[] inputFiles = (Path[])Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])this.fs().listStatus(this.cpath())).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Path apply(FileStatus x$1) {
            return x$1.getPath();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Path.class)))).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Path x$2) {
            return x$2.getName().startsWith("part-");
         }
      })).sortBy(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Path x$3) {
            return (new StringOps(Predef$.MODULE$.augmentString((new StringOps(Predef$.MODULE$.augmentString(x$3.getName()))).stripPrefix("part-")))).toInt();
         }
      }, Ordering.Int$.MODULE$);
      Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])inputFiles).zipWithIndex(Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(Tuple2<Path, Object> x0$1) {
            if (x0$1 == null) {
               throw new MatchError(x0$1);
            } else {
               label18: {
                  Path path = (Path)x0$1._1();
                  int i = x0$1._2$mcI$sp();
                  String var10000 = path.getName();
                  String var6 = ReliableCheckpointRDD$.MODULE$.org$apache$spark$rdd$ReliableCheckpointRDD$$checkpointFileName(i);
                  if (var10000 == null) {
                     if (var6 == null) {
                        break label18;
                     }
                  } else if (var10000.equals(var6)) {
                     break label18;
                  }

                  throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid checkpoint file: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{path})));
               }

               BoxedUnit var5 = BoxedUnit.UNIT;
            }
         }
      });
      return (Partition[])Array$.MODULE$.tabulate(inputFiles.length, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final CheckpointRDDPartition apply(int i) {
            return new CheckpointRDDPartition(i);
         }
      }, ClassTag$.MODULE$.apply(Partition.class));
   }

   public Seq<String> getPreferredLocations(Partition split) {
      FileStatus status = this.fs().getFileStatus(new Path(this.checkpointPath(), ReliableCheckpointRDD$.MODULE$.org$apache$spark$rdd$ReliableCheckpointRDD$$checkpointFileName(split.index())));
      BlockLocation[] locations = this.fs().getFileBlockLocations(status, 0L, status.getLen());
      return (Seq)((TraversableLike)Predef$.MODULE$.refArrayOps((Object[])locations).headOption().toList().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayOps<String> apply(BlockLocation x$4) {
            return Predef$.MODULE$.<String>refArrayOps((Object[])x$4.getHosts());
         }
      }, List$.MODULE$.canBuildFrom())).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$5) {
            boolean var10000;
            label23: {
               String var2 = "localhost";
               if (x$5 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (x$5.equals(var2)) {
                  break label23;
               }

               var10000 = true;
               return var10000;
            }

            var10000 = false;
            return var10000;
         }
      });
   }

   public Iterator<T> compute(Partition split, TaskContext context) {
      Path file = new Path(this.checkpointPath(), ReliableCheckpointRDD$.MODULE$.org$apache$spark$rdd$ReliableCheckpointRDD$$checkpointFileName(split.index()));
      return ReliableCheckpointRDD$.MODULE$.<T>readCheckpointFile(file, this.broadcastedConf(), context);
   }

   public ReliableCheckpointRDD(SparkContext sc, String checkpointPath, Option<Partitioner> _partitioner, ClassTag<T> evidence$1) {
      super(sc, evidence$1);
      this.checkpointPath = checkpointPath;
      this.hadoopConf = sc.hadoopConfiguration();
      this.cpath = new Path(checkpointPath);
      this.fs = this.cpath().getFileSystem(this.hadoopConf());
      this.broadcastedConf = sc.<SerializableConfiguration>broadcast(new SerializableConfiguration(this.hadoopConf()), ClassTag$.MODULE$.apply(SerializableConfiguration.class));
      Predef$.MODULE$.require(this.fs().exists(this.cpath()), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ReliableCheckpointRDD $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Checkpoint directory does not exist: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.checkpointPath()}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.getCheckpointFile = new Some<String>(checkpointPath);
      this.partitioner = _partitioner.<Partitioner>orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ReliableCheckpointRDD $outer;

         public final Option<Partitioner> apply() {
            return ReliableCheckpointRDD$.MODULE$.org$apache$spark$rdd$ReliableCheckpointRDD$$readCheckpointedPartitionerFile(this.$outer.context(), this.$outer.checkpointPath());
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
