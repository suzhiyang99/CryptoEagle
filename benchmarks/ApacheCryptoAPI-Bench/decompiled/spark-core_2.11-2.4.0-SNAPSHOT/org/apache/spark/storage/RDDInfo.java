package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDDOperationScope;
import org.apache.spark.util.Utils$;
import scala.Option;
import scala.Predef$;
import scala.collection.Seq;
import scala.collection.immutable.StringOps;
import scala.math.Ordered;
import scala.math.Ordered$class;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001df\u0001B\u0001\u0003\u0001-\u0011qA\u0015#E\u0013:4wN\u0003\u0002\u0004\t\u000591\u000f^8sC\u001e,'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0019\u0001\u0001\u0004\n\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\r\u00192D\b\b\u0003)eq!!\u0006\r\u000e\u0003YQ!a\u0006\u0006\u0002\rq\u0012xn\u001c;?\u0013\u0005y\u0011B\u0001\u000e\u000f\u0003\u001d\u0001\u0018mY6bO\u0016L!\u0001H\u000f\u0003\u000f=\u0013H-\u001a:fI*\u0011!D\u0004\t\u0003?\u0001i\u0011A\u0001\u0005\tC\u0001\u0011)\u0019!C\u0001E\u0005\u0011\u0011\u000eZ\u000b\u0002GA\u0011Q\u0002J\u0005\u0003K9\u00111!\u00138u\u0011!9\u0003A!A!\u0002\u0013\u0019\u0013aA5eA!A\u0011\u0006\u0001BA\u0002\u0013\u0005!&\u0001\u0003oC6,W#A\u0016\u0011\u00051zcBA\u0007.\u0013\tqc\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003aE\u0012aa\u0015;sS:<'B\u0001\u0018\u000f\u0011!\u0019\u0004A!a\u0001\n\u0003!\u0014\u0001\u00038b[\u0016|F%Z9\u0015\u0005UB\u0004CA\u00077\u0013\t9dB\u0001\u0003V]&$\bbB\u001d3\u0003\u0003\u0005\raK\u0001\u0004q\u0012\n\u0004\u0002C\u001e\u0001\u0005\u0003\u0005\u000b\u0015B\u0016\u0002\u000b9\fW.\u001a\u0011\t\u0011u\u0002!Q1A\u0005\u0002\t\nQB\\;n!\u0006\u0014H/\u001b;j_:\u001c\b\u0002C \u0001\u0005\u0003\u0005\u000b\u0011B\u0012\u0002\u001d9,X\u000eU1si&$\u0018n\u001c8tA!A\u0011\t\u0001BA\u0002\u0013\u0005!)\u0001\u0007ti>\u0014\u0018mZ3MKZ,G.F\u0001D!\tyB)\u0003\u0002F\u0005\ta1\u000b^8sC\u001e,G*\u001a<fY\"Aq\t\u0001BA\u0002\u0013\u0005\u0001*\u0001\tti>\u0014\u0018mZ3MKZ,Gn\u0018\u0013fcR\u0011Q'\u0013\u0005\bs\u0019\u000b\t\u00111\u0001D\u0011!Y\u0005A!A!B\u0013\u0019\u0015!D:u_J\fw-\u001a'fm\u0016d\u0007\u0005\u0003\u0005N\u0001\t\u0015\r\u0011\"\u0001O\u0003%\u0001\u0018M]3oi&#7/F\u0001P!\r\u0019\u0002kI\u0005\u0003#v\u00111aU3r\u0011!\u0019\u0006A!A!\u0002\u0013y\u0015A\u00039be\u0016tG/\u00133tA!AQ\u000b\u0001BC\u0002\u0013\u0005!&\u0001\u0005dC2d7+\u001b;f\u0011!9\u0006A!A!\u0002\u0013Y\u0013!C2bY2\u001c\u0016\u000e^3!\u0011!I\u0006A!b\u0001\n\u0003Q\u0016!B:d_B,W#A.\u0011\u00075af,\u0003\u0002^\u001d\t1q\n\u001d;j_:\u0004\"a\u00182\u000e\u0003\u0001T!!\u0019\u0003\u0002\u0007I$G-\u0003\u0002dA\n\t\"\u000b\u0012#Pa\u0016\u0014\u0018\r^5p]N\u001bw\u000e]3\t\u0011\u0015\u0004!\u0011!Q\u0001\nm\u000baa]2pa\u0016\u0004\u0003\"B4\u0001\t\u0003A\u0017A\u0002\u001fj]&$h\b\u0006\u0005\u001fS*\\G.\u001c8p\u0011\u0015\tc\r1\u0001$\u0011\u0015Ic\r1\u0001,\u0011\u0015id\r1\u0001$\u0011\u0015\te\r1\u0001D\u0011\u0015ie\r1\u0001P\u0011\u001d)f\r%AA\u0002-Bq!\u00174\u0011\u0002\u0003\u00071\fC\u0004r\u0001\u0001\u0007I\u0011\u0001\u0012\u0002'9,XnQ1dQ\u0016$\u0007+\u0019:uSRLwN\\:\t\u000fM\u0004\u0001\u0019!C\u0001i\u00069b.^7DC\u000eDW\r\u001a)beRLG/[8og~#S-\u001d\u000b\u0003kUDq!\u000f:\u0002\u0002\u0003\u00071\u0005\u0003\u0004x\u0001\u0001\u0006KaI\u0001\u0015]Vl7)Y2iK\u0012\u0004\u0016M\u001d;ji&|gn\u001d\u0011\t\u000fe\u0004\u0001\u0019!C\u0001u\u00069Q.Z7TSj,W#A>\u0011\u00055a\u0018BA?\u000f\u0005\u0011auN\\4\t\u0011}\u0004\u0001\u0019!C\u0001\u0003\u0003\t1\"\\3n'&TXm\u0018\u0013fcR\u0019Q'a\u0001\t\u000fer\u0018\u0011!a\u0001w\"9\u0011q\u0001\u0001!B\u0013Y\u0018\u0001C7f[NK'0\u001a\u0011\t\u0011\u0005-\u0001\u00011A\u0005\u0002i\f\u0001\u0002Z5tWNK'0\u001a\u0005\n\u0003\u001f\u0001\u0001\u0019!C\u0001\u0003#\tA\u0002Z5tWNK'0Z0%KF$2!NA\n\u0011!I\u0014QBA\u0001\u0002\u0004Y\bbBA\f\u0001\u0001\u0006Ka_\u0001\nI&\u001c8nU5{K\u0002B\u0001\"a\u0007\u0001\u0001\u0004%\tA_\u0001\u0017Kb$XM\u001d8bY\ncwnY6Ti>\u0014XmU5{K\"I\u0011q\u0004\u0001A\u0002\u0013\u0005\u0011\u0011E\u0001\u001bKb$XM\u001d8bY\ncwnY6Ti>\u0014XmU5{K~#S-\u001d\u000b\u0004k\u0005\r\u0002\u0002C\u001d\u0002\u001e\u0005\u0005\t\u0019A>\t\u000f\u0005\u001d\u0002\u0001)Q\u0005w\u00069R\r\u001f;fe:\fGN\u00117pG.\u001cFo\u001c:f'&TX\r\t\u0005\b\u0003W\u0001A\u0011AA\u0017\u0003!I7oQ1dQ\u0016$WCAA\u0018!\ri\u0011\u0011G\u0005\u0004\u0003gq!a\u0002\"p_2,\u0017M\u001c\u0005\b\u0003o\u0001A\u0011IA\u001d\u0003!!xn\u0015;sS:<G#A\u0016\t\u000f\u0005u\u0002\u0001\"\u0011\u0002@\u000591m\\7qCJ,GcA\u0012\u0002B!9\u00111IA\u001e\u0001\u0004q\u0012\u0001\u0002;iCRD3\u0001AA$!\u0011\tI%a\u0014\u000e\u0005\u0005-#bAA'\t\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005E\u00131\n\u0002\r\t\u00164X\r\\8qKJ\f\u0005/[\u0004\t\u0003+\u0012\u0001\u0012\u0001\u0003\u0002X\u00059!\u000b\u0012#J]\u001a|\u0007cA\u0010\u0002Z\u00199\u0011A\u0001E\u0001\t\u0005m3cAA-\u0019!9q-!\u0017\u0005\u0002\u0005}CCAA,\u0011!\t\u0019'!\u0017\u0005\u0002\u0005\u0015\u0014a\u00024s_6\u0014F\r\u001a\u000b\u0004=\u0005\u001d\u0004bB1\u0002b\u0001\u0007\u0011\u0011\u000e\u0019\u0005\u0003W\n)\bE\u0003`\u0003[\n\t(C\u0002\u0002p\u0001\u00141A\u0015#E!\u0011\t\u0019(!\u001e\r\u0001\u0011a\u0011qOA4\u0003\u0003\u0005\tQ!\u0001\u0002z\t\u0019q\fJ\u0019\u0012\t\u0005m\u0014\u0011\u0011\t\u0004\u001b\u0005u\u0014bAA@\u001d\t9aj\u001c;iS:<\u0007cA\u0007\u0002\u0004&\u0019\u0011Q\u0011\b\u0003\u0007\u0005s\u0017\u0010\u0003\u0006\u0002\n\u0006e\u0013\u0013!C\u0001\u0003\u0017\u000b1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u00122TCAAGU\rY\u0013qR\u0016\u0003\u0003#\u0003B!a%\u0002\u001c6\u0011\u0011Q\u0013\u0006\u0005\u0003/\u000bI*A\u0005v]\u000eDWmY6fI*\u0019\u0011Q\n\b\n\t\u0005u\u0015Q\u0013\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007BCAQ\u00033\n\n\u0011\"\u0001\u0002$\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uI]*\"!!*+\u0007m\u000by\t"
)
public class RDDInfo implements Ordered<RDDInfo> {
   private final int id;
   private String name;
   private final int numPartitions;
   private StorageLevel storageLevel;
   private final Seq<Object> parentIds;
   private final String callSite;
   private final Option<RDDOperationScope> scope;
   private int numCachedPartitions;
   private long memSize;
   private long diskSize;
   private long externalBlockStoreSize;

   public static Option<RDDOperationScope> $lessinit$greater$default$7() {
      return RDDInfo$.MODULE$.$lessinit$greater$default$7();
   }

   public static String $lessinit$greater$default$6() {
      return RDDInfo$.MODULE$.$lessinit$greater$default$6();
   }

   public static RDDInfo fromRdd(RDD<?> var0) {
      return RDDInfo$.MODULE$.fromRdd(var0);
   }

   public boolean $less(Object that) {
      return Ordered$class.$less(this, that);
   }

   public boolean $greater(Object that) {
      return Ordered$class.$greater(this, that);
   }

   public boolean $less$eq(Object that) {
      return Ordered$class.$less$eq(this, that);
   }

   public boolean $greater$eq(Object that) {
      return Ordered$class.$greater$eq(this, that);
   }

   public int compareTo(Object that) {
      return Ordered$class.compareTo(this, that);
   }

   public int id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }

   public void name_$eq(String x$1) {
      this.name = x$1;
   }

   public int numPartitions() {
      return this.numPartitions;
   }

   public StorageLevel storageLevel() {
      return this.storageLevel;
   }

   public void storageLevel_$eq(StorageLevel x$1) {
      this.storageLevel = x$1;
   }

   public Seq<Object> parentIds() {
      return this.parentIds;
   }

   public String callSite() {
      return this.callSite;
   }

   public Option<RDDOperationScope> scope() {
      return this.scope;
   }

   public int numCachedPartitions() {
      return this.numCachedPartitions;
   }

   public void numCachedPartitions_$eq(int x$1) {
      this.numCachedPartitions = x$1;
   }

   public long memSize() {
      return this.memSize;
   }

   public void memSize_$eq(long x$1) {
      this.memSize = x$1;
   }

   public long diskSize() {
      return this.diskSize;
   }

   public void diskSize_$eq(long x$1) {
      this.diskSize = x$1;
   }

   public long externalBlockStoreSize() {
      return this.externalBlockStoreSize;
   }

   public void externalBlockStoreSize_$eq(long x$1) {
      this.externalBlockStoreSize = x$1;
   }

   public boolean isCached() {
      return this.memSize() + this.diskSize() > 0L && this.numCachedPartitions() > 0;
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString("RDD \"%s\" (%d) StorageLevel: %s; CachedPartitions: %d; TotalPartitions: %d; MemorySize: %s; DiskSize: %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.name(), BoxesRunTime.boxToInteger(this.id()), this.storageLevel().toString(), BoxesRunTime.boxToInteger(this.numCachedPartitions()), BoxesRunTime.boxToInteger(this.numPartitions()), Utils$.MODULE$.bytesToString(this.memSize()), Utils$.MODULE$.bytesToString(this.diskSize())}));
   }

   public int compare(RDDInfo that) {
      return this.id() - that.id();
   }

   public RDDInfo(int id, String name, int numPartitions, StorageLevel storageLevel, Seq<Object> parentIds, String callSite, Option<RDDOperationScope> scope) {
      this.id = id;
      this.name = name;
      this.numPartitions = numPartitions;
      this.storageLevel = storageLevel;
      this.parentIds = parentIds;
      this.callSite = callSite;
      this.scope = scope;
      super();
      Ordered$class.$init$(this);
      this.numCachedPartitions = 0;
      this.memSize = 0L;
      this.diskSize = 0L;
      this.externalBlockStoreSize = 0L;
   }
}
