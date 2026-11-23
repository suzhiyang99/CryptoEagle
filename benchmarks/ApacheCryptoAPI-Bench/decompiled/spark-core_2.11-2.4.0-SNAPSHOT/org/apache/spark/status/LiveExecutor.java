package org.apache.spark.status;

import java.util.Date;
import org.apache.spark.status.api.v1.ExecutorSummary;
import org.apache.spark.status.api.v1.MemoryMetrics;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Some;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\t=c\u0001B\u0001\u0003\t-\u0011A\u0002T5wK\u0016CXmY;u_JT!a\u0001\u0003\u0002\rM$\u0018\r^;t\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0003\u00011\u0001\"!\u0004\b\u000e\u0003\tI!a\u0004\u0002\u0003\u00151Kg/Z#oi&$\u0018\u0010\u0003\u0005\u0012\u0001\t\u0015\r\u0011\"\u0001\u0013\u0003))\u00070Z2vi>\u0014\u0018\nZ\u000b\u0002'A\u0011AC\u0007\b\u0003+ai\u0011A\u0006\u0006\u0002/\u0005)1oY1mC&\u0011\u0011DF\u0001\u0007!J,G-\u001a4\n\u0005ma\"AB*ue&twM\u0003\u0002\u001a-!Aa\u0004\u0001B\u0001B\u0003%1#A\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012\u0004\u0003\u0002\u0003\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\u0011\u0002\u0011}\u000bG\r\u001a+j[\u0016\u0004\"!\u0006\u0012\n\u0005\r2\"\u0001\u0002'p]\u001eDQ!\n\u0001\u0005\u0002\u0019\na\u0001P5oSRtDcA\u0014)SA\u0011Q\u0002\u0001\u0005\u0006#\u0011\u0002\ra\u0005\u0005\u0006A\u0011\u0002\r!\t\u0005\bW\u0001\u0001\r\u0011\"\u0001\u0013\u0003!Awn\u001d;Q_J$\bbB\u0017\u0001\u0001\u0004%\tAL\u0001\rQ>\u001cH\u000fU8si~#S-\u001d\u000b\u0003_I\u0002\"!\u0006\u0019\n\u0005E2\"\u0001B+oSRDqa\r\u0017\u0002\u0002\u0003\u00071#A\u0002yIEBa!\u000e\u0001!B\u0013\u0019\u0012!\u00035pgR\u0004vN\u001d;!\u0011\u001d9\u0004\u00011A\u0005\u0002I\tA\u0001[8ti\"9\u0011\b\u0001a\u0001\n\u0003Q\u0014\u0001\u00035pgR|F%Z9\u0015\u0005=Z\u0004bB\u001a9\u0003\u0003\u0005\ra\u0005\u0005\u0007{\u0001\u0001\u000b\u0015B\n\u0002\u000b!|7\u000f\u001e\u0011\t\u000f}\u0002\u0001\u0019!C\u0001\u0001\u0006A\u0011n]!di&4X-F\u0001B!\t)\")\u0003\u0002D-\t9!i\\8mK\u0006t\u0007bB#\u0001\u0001\u0004%\tAR\u0001\rSN\f5\r^5wK~#S-\u001d\u000b\u0003_\u001dCqa\r#\u0002\u0002\u0003\u0007\u0011\t\u0003\u0004J\u0001\u0001\u0006K!Q\u0001\nSN\f5\r^5wK\u0002Bqa\u0013\u0001A\u0002\u0013\u0005A*\u0001\u0006u_R\fGnQ8sKN,\u0012!\u0014\t\u0003+9K!a\u0014\f\u0003\u0007%sG\u000fC\u0004R\u0001\u0001\u0007I\u0011\u0001*\u0002\u001dQ|G/\u00197D_J,7o\u0018\u0013fcR\u0011qf\u0015\u0005\bgA\u000b\t\u00111\u0001N\u0011\u0019)\u0006\u0001)Q\u0005\u001b\u0006YAo\u001c;bY\u000e{'/Z:!\u0011\u001d9\u0006A1A\u0005\u0002a\u000bq!\u00193e)&lW-F\u0001Z!\tQv,D\u0001\\\u0015\taV,\u0001\u0003vi&d'\"\u00010\u0002\t)\fg/Y\u0005\u0003An\u0013A\u0001R1uK\"1!\r\u0001Q\u0001\ne\u000b\u0001\"\u00193e)&lW\r\t\u0005\bI\u0002\u0001\r\u0011\"\u0001Y\u0003)\u0011X-\\8wKRKW.\u001a\u0005\bM\u0002\u0001\r\u0011\"\u0001h\u00039\u0011X-\\8wKRKW.Z0%KF$\"a\f5\t\u000fM*\u0017\u0011!a\u00013\"1!\u000e\u0001Q!\ne\u000b1B]3n_Z,G+[7fA!9A\u000e\u0001a\u0001\n\u0003\u0011\u0012\u0001\u0004:f[>4XMU3bg>t\u0007b\u00028\u0001\u0001\u0004%\ta\\\u0001\u0011e\u0016lwN^3SK\u0006\u001cxN\\0%KF$\"a\f9\t\u000fMj\u0017\u0011!a\u0001'!1!\u000f\u0001Q!\nM\tQB]3n_Z,'+Z1t_:\u0004\u0003b\u0002;\u0001\u0001\u0004%\t\u0001T\u0001\ne\u0012$'\t\\8dWNDqA\u001e\u0001A\u0002\u0013\u0005q/A\u0007sI\u0012\u0014En\\2lg~#S-\u001d\u000b\u0003_aDqaM;\u0002\u0002\u0003\u0007Q\n\u0003\u0004{\u0001\u0001\u0006K!T\u0001\u000be\u0012$'\t\\8dWN\u0004\u0003b\u0002?\u0001\u0001\u0004%\t!`\u0001\u000b[\u0016lwN]=Vg\u0016$W#A\u0011\t\u0011}\u0004\u0001\u0019!C\u0001\u0003\u0003\ta\"\\3n_JLXk]3e?\u0012*\u0017\u000fF\u00020\u0003\u0007Aqa\r@\u0002\u0002\u0003\u0007\u0011\u0005C\u0004\u0002\b\u0001\u0001\u000b\u0015B\u0011\u0002\u00175,Wn\u001c:z+N,G\r\t\u0005\t\u0003\u0017\u0001\u0001\u0019!C\u0001{\u0006AA-[:l+N,G\rC\u0005\u0002\u0010\u0001\u0001\r\u0011\"\u0001\u0002\u0012\u0005aA-[:l+N,Gm\u0018\u0013fcR\u0019q&a\u0005\t\u0011M\ni!!AA\u0002\u0005Bq!a\u0006\u0001A\u0003&\u0011%A\u0005eSN\\Wk]3eA!A\u00111\u0004\u0001A\u0002\u0013\u0005A*\u0001\u0005nCb$\u0016m]6t\u0011%\ty\u0002\u0001a\u0001\n\u0003\t\t#\u0001\u0007nCb$\u0016m]6t?\u0012*\u0017\u000fF\u00020\u0003GA\u0001bMA\u000f\u0003\u0003\u0005\r!\u0014\u0005\b\u0003O\u0001\u0001\u0015)\u0003N\u0003%i\u0017\r\u001f+bg.\u001c\b\u0005\u0003\u0005\u0002,\u0001\u0001\r\u0011\"\u0001~\u0003%i\u0017\r_'f[>\u0014\u0018\u0010C\u0005\u00020\u0001\u0001\r\u0011\"\u0001\u00022\u0005iQ.\u0019=NK6|'/_0%KF$2aLA\u001a\u0011!\u0019\u0014QFA\u0001\u0002\u0004\t\u0003bBA\u001c\u0001\u0001\u0006K!I\u0001\u000b[\u0006DX*Z7pef\u0004\u0003\u0002CA\u001e\u0001\u0001\u0007I\u0011\u0001'\u0002\u0015Q|G/\u00197UCN\\7\u000fC\u0005\u0002@\u0001\u0001\r\u0011\"\u0001\u0002B\u0005qAo\u001c;bYR\u000b7o[:`I\u0015\fHcA\u0018\u0002D!A1'!\u0010\u0002\u0002\u0003\u0007Q\nC\u0004\u0002H\u0001\u0001\u000b\u0015B'\u0002\u0017Q|G/\u00197UCN\\7\u000f\t\u0005\t\u0003\u0017\u0002\u0001\u0019!C\u0001\u0019\u0006Y\u0011m\u0019;jm\u0016$\u0016m]6t\u0011%\ty\u0005\u0001a\u0001\n\u0003\t\t&A\bbGRLg/\u001a+bg.\u001cx\fJ3r)\ry\u00131\u000b\u0005\tg\u00055\u0013\u0011!a\u0001\u001b\"9\u0011q\u000b\u0001!B\u0013i\u0015\u0001D1di&4X\rV1tWN\u0004\u0003\u0002CA.\u0001\u0001\u0007I\u0011\u0001'\u0002\u001d\r|W\u000e\u001d7fi\u0016$G+Y:lg\"I\u0011q\f\u0001A\u0002\u0013\u0005\u0011\u0011M\u0001\u0013G>l\u0007\u000f\\3uK\u0012$\u0016m]6t?\u0012*\u0017\u000fF\u00020\u0003GB\u0001bMA/\u0003\u0003\u0005\r!\u0014\u0005\b\u0003O\u0002\u0001\u0015)\u0003N\u0003=\u0019w.\u001c9mKR,G\rV1tWN\u0004\u0003\u0002CA6\u0001\u0001\u0007I\u0011\u0001'\u0002\u0017\u0019\f\u0017\u000e\\3e)\u0006\u001c8n\u001d\u0005\n\u0003_\u0002\u0001\u0019!C\u0001\u0003c\nqBZ1jY\u0016$G+Y:lg~#S-\u001d\u000b\u0004_\u0005M\u0004\u0002C\u001a\u0002n\u0005\u0005\t\u0019A'\t\u000f\u0005]\u0004\u0001)Q\u0005\u001b\u0006aa-Y5mK\u0012$\u0016m]6tA!A\u00111\u0010\u0001A\u0002\u0013\u0005Q0A\u0007u_R\fG\u000eR;sCRLwN\u001c\u0005\n\u0003\u007f\u0002\u0001\u0019!C\u0001\u0003\u0003\u000b\u0011\u0003^8uC2$UO]1uS>tw\fJ3r)\ry\u00131\u0011\u0005\tg\u0005u\u0014\u0011!a\u0001C!9\u0011q\u0011\u0001!B\u0013\t\u0013A\u0004;pi\u0006dG)\u001e:bi&|g\u000e\t\u0005\t\u0003\u0017\u0003\u0001\u0019!C\u0001{\u0006YAo\u001c;bY\u001e\u001bG+[7f\u0011%\ty\t\u0001a\u0001\n\u0003\t\t*A\bu_R\fGnR2US6,w\fJ3r)\ry\u00131\u0013\u0005\tg\u00055\u0015\u0011!a\u0001C!9\u0011q\u0013\u0001!B\u0013\t\u0013\u0001\u0004;pi\u0006dwi\u0019+j[\u0016\u0004\u0003\u0002CAN\u0001\u0001\u0007I\u0011A?\u0002\u001fQ|G/\u00197J]B,HOQ=uKND\u0011\"a(\u0001\u0001\u0004%\t!!)\u0002'Q|G/\u00197J]B,HOQ=uKN|F%Z9\u0015\u0007=\n\u0019\u000b\u0003\u00054\u0003;\u000b\t\u00111\u0001\"\u0011\u001d\t9\u000b\u0001Q!\n\u0005\n\u0001\u0003^8uC2Le\u000e];u\u0005f$Xm\u001d\u0011\t\u0011\u0005-\u0006\u00011A\u0005\u0002u\f\u0001\u0003^8uC2\u001c\u0006.\u001e4gY\u0016\u0014V-\u00193\t\u0013\u0005=\u0006\u00011A\u0005\u0002\u0005E\u0016\u0001\u0006;pi\u0006d7\u000b[;gM2,'+Z1e?\u0012*\u0017\u000fF\u00020\u0003gC\u0001bMAW\u0003\u0003\u0005\r!\t\u0005\b\u0003o\u0003\u0001\u0015)\u0003\"\u0003E!x\u000e^1m'\",hM\u001a7f%\u0016\fG\r\t\u0005\t\u0003w\u0003\u0001\u0019!C\u0001{\u0006\tBo\u001c;bYNCWO\u001a4mK^\u0013\u0018\u000e^3\t\u0013\u0005}\u0006\u00011A\u0005\u0002\u0005\u0005\u0017!\u0006;pi\u0006d7\u000b[;gM2,wK]5uK~#S-\u001d\u000b\u0004_\u0005\r\u0007\u0002C\u001a\u0002>\u0006\u0005\t\u0019A\u0011\t\u000f\u0005\u001d\u0007\u0001)Q\u0005C\u0005\u0011Bo\u001c;bYNCWO\u001a4mK^\u0013\u0018\u000e^3!\u0011!\tY\r\u0001a\u0001\n\u0003\u0001\u0015!D5t\u00052\f7m\u001b7jgR,G\rC\u0005\u0002P\u0002\u0001\r\u0011\"\u0001\u0002R\u0006\t\u0012n\u001d\"mC\u000e\\G.[:uK\u0012|F%Z9\u0015\u0007=\n\u0019\u000e\u0003\u00054\u0003\u001b\f\t\u00111\u0001B\u0011\u001d\t9\u000e\u0001Q!\n\u0005\u000ba\"[:CY\u0006\u001c7\u000e\\5ti\u0016$\u0007\u0005C\u0005\u0002\\\u0002\u0001\r\u0011\"\u0001\u0002^\u0006aQ\r_3dkR|'\u000fT8hgV\u0011\u0011q\u001c\t\u0007\u0003C\fYoE\n\u000e\u0005\u0005\r(\u0002BAs\u0003O\f\u0011\"[7nkR\f'\r\\3\u000b\u0007\u0005%h#\u0001\u0006d_2dWm\u0019;j_:LA!!<\u0002d\n\u0019Q*\u00199\t\u0013\u0005E\b\u00011A\u0005\u0002\u0005M\u0018\u0001E3yK\u000e,Ho\u001c:M_\u001e\u001cx\fJ3r)\ry\u0013Q\u001f\u0005\ng\u0005=\u0018\u0011!a\u0001\u0003?D\u0001\"!?\u0001A\u0003&\u0011q\\\u0001\u000eKb,7-\u001e;pe2{wm\u001d\u0011\t\u0011\u0005u\b\u00011A\u0005\u0002u\f1\u0002^8uC2|e\u000eS3ba\"I!\u0011\u0001\u0001A\u0002\u0013\u0005!1A\u0001\u0010i>$\u0018\r\\(o\u0011\u0016\f\u0007o\u0018\u0013fcR\u0019qF!\u0002\t\u0011M\ny0!AA\u0002\u0005BqA!\u0003\u0001A\u0003&\u0011%\u0001\u0007u_R\fGn\u00148IK\u0006\u0004\b\u0005\u0003\u0005\u0003\u000e\u0001\u0001\r\u0011\"\u0001~\u00031!x\u000e^1m\u001f\u001a4\u0007*Z1q\u0011%\u0011\t\u0002\u0001a\u0001\n\u0003\u0011\u0019\"\u0001\tu_R\fGn\u00144g\u0011\u0016\f\u0007o\u0018\u0013fcR\u0019qF!\u0006\t\u0011M\u0012y!!AA\u0002\u0005BqA!\u0007\u0001A\u0003&\u0011%A\u0007u_R\fGn\u00144g\u0011\u0016\f\u0007\u000f\t\u0005\t\u0005;\u0001\u0001\u0019!C\u0001{\u0006QQo]3e\u001f:DU-\u00199\t\u0013\t\u0005\u0002\u00011A\u0005\u0002\t\r\u0012AD;tK\u0012|e\u000eS3ba~#S-\u001d\u000b\u0004_\t\u0015\u0002\u0002C\u001a\u0003 \u0005\u0005\t\u0019A\u0011\t\u000f\t%\u0002\u0001)Q\u0005C\u0005YQo]3e\u001f:DU-\u00199!\u0011!\u0011i\u0003\u0001a\u0001\n\u0003i\u0018aC;tK\u0012|eM\u001a%fCBD\u0011B!\r\u0001\u0001\u0004%\tAa\r\u0002\u001fU\u001cX\rZ(gM\"+\u0017\r]0%KF$2a\fB\u001b\u0011!\u0019$qFA\u0001\u0002\u0004\t\u0003b\u0002B\u001d\u0001\u0001\u0006K!I\u0001\rkN,Gm\u00144g\u0011\u0016\f\u0007\u000f\t\u0005\u0007\u0005{\u0001A\u0011\u0001!\u0002\u001b!\f7/T3n_JL\u0018J\u001c4p\u0011\u0019\u0011\t\u0005\u0001C\u0001%\u0005A\u0001n\\:u]\u0006lW\rC\u0004\u0003F\u0001!\tFa\u0012\u0002\u0011\u0011|W\u000b\u001d3bi\u0016$\"A!\u0013\u0011\u0007U\u0011Y%C\u0002\u0003NY\u00111!\u00118z\u0001"
)
public class LiveExecutor extends LiveEntity {
   private final String executorId;
   private String hostPort;
   private String host;
   private boolean isActive;
   private int totalCores;
   private final Date addTime;
   private Date removeTime;
   private String removeReason;
   private int rddBlocks;
   private long memoryUsed;
   private long diskUsed;
   private int maxTasks;
   private long maxMemory;
   private int totalTasks;
   private int activeTasks;
   private int completedTasks;
   private int failedTasks;
   private long totalDuration;
   private long totalGcTime;
   private long totalInputBytes;
   private long totalShuffleRead;
   private long totalShuffleWrite;
   private boolean isBlacklisted;
   private Map<String, String> executorLogs;
   private long totalOnHeap;
   private long totalOffHeap;
   private long usedOnHeap;
   private long usedOffHeap;

   public String executorId() {
      return this.executorId;
   }

   public String hostPort() {
      return this.hostPort;
   }

   public void hostPort_$eq(String x$1) {
      this.hostPort = x$1;
   }

   public String host() {
      return this.host;
   }

   public void host_$eq(String x$1) {
      this.host = x$1;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public void isActive_$eq(boolean x$1) {
      this.isActive = x$1;
   }

   public int totalCores() {
      return this.totalCores;
   }

   public void totalCores_$eq(int x$1) {
      this.totalCores = x$1;
   }

   public Date addTime() {
      return this.addTime;
   }

   public Date removeTime() {
      return this.removeTime;
   }

   public void removeTime_$eq(Date x$1) {
      this.removeTime = x$1;
   }

   public String removeReason() {
      return this.removeReason;
   }

   public void removeReason_$eq(String x$1) {
      this.removeReason = x$1;
   }

   public int rddBlocks() {
      return this.rddBlocks;
   }

   public void rddBlocks_$eq(int x$1) {
      this.rddBlocks = x$1;
   }

   public long memoryUsed() {
      return this.memoryUsed;
   }

   public void memoryUsed_$eq(long x$1) {
      this.memoryUsed = x$1;
   }

   public long diskUsed() {
      return this.diskUsed;
   }

   public void diskUsed_$eq(long x$1) {
      this.diskUsed = x$1;
   }

   public int maxTasks() {
      return this.maxTasks;
   }

   public void maxTasks_$eq(int x$1) {
      this.maxTasks = x$1;
   }

   public long maxMemory() {
      return this.maxMemory;
   }

   public void maxMemory_$eq(long x$1) {
      this.maxMemory = x$1;
   }

   public int totalTasks() {
      return this.totalTasks;
   }

   public void totalTasks_$eq(int x$1) {
      this.totalTasks = x$1;
   }

   public int activeTasks() {
      return this.activeTasks;
   }

   public void activeTasks_$eq(int x$1) {
      this.activeTasks = x$1;
   }

   public int completedTasks() {
      return this.completedTasks;
   }

   public void completedTasks_$eq(int x$1) {
      this.completedTasks = x$1;
   }

   public int failedTasks() {
      return this.failedTasks;
   }

   public void failedTasks_$eq(int x$1) {
      this.failedTasks = x$1;
   }

   public long totalDuration() {
      return this.totalDuration;
   }

   public void totalDuration_$eq(long x$1) {
      this.totalDuration = x$1;
   }

   public long totalGcTime() {
      return this.totalGcTime;
   }

   public void totalGcTime_$eq(long x$1) {
      this.totalGcTime = x$1;
   }

   public long totalInputBytes() {
      return this.totalInputBytes;
   }

   public void totalInputBytes_$eq(long x$1) {
      this.totalInputBytes = x$1;
   }

   public long totalShuffleRead() {
      return this.totalShuffleRead;
   }

   public void totalShuffleRead_$eq(long x$1) {
      this.totalShuffleRead = x$1;
   }

   public long totalShuffleWrite() {
      return this.totalShuffleWrite;
   }

   public void totalShuffleWrite_$eq(long x$1) {
      this.totalShuffleWrite = x$1;
   }

   public boolean isBlacklisted() {
      return this.isBlacklisted;
   }

   public void isBlacklisted_$eq(boolean x$1) {
      this.isBlacklisted = x$1;
   }

   public Map<String, String> executorLogs() {
      return this.executorLogs;
   }

   public void executorLogs_$eq(Map<String, String> x$1) {
      this.executorLogs = x$1;
   }

   public long totalOnHeap() {
      return this.totalOnHeap;
   }

   public void totalOnHeap_$eq(long x$1) {
      this.totalOnHeap = x$1;
   }

   public long totalOffHeap() {
      return this.totalOffHeap;
   }

   public void totalOffHeap_$eq(long x$1) {
      this.totalOffHeap = x$1;
   }

   public long usedOnHeap() {
      return this.usedOnHeap;
   }

   public void usedOnHeap_$eq(long x$1) {
      this.usedOnHeap = x$1;
   }

   public long usedOffHeap() {
      return this.usedOffHeap;
   }

   public void usedOffHeap_$eq(long x$1) {
      this.usedOffHeap = x$1;
   }

   public boolean hasMemoryInfo() {
      return this.totalOnHeap() >= 0L;
   }

   public String hostname() {
      return this.host() == null ? this.hostPort().split(":")[0] : this.host();
   }

   public Object doUpdate() {
      Option memoryMetrics = (Option)(this.totalOnHeap() >= 0L ? new Some(new MemoryMetrics(this.usedOnHeap(), this.usedOffHeap(), this.totalOnHeap(), this.totalOffHeap())) : None$.MODULE$);
      ExecutorSummary info = new ExecutorSummary(this.executorId(), this.hostPort() == null ? this.host() : this.hostPort(), this.isActive(), this.rddBlocks(), this.memoryUsed(), this.diskUsed(), this.totalCores(), this.maxTasks(), this.activeTasks(), this.failedTasks(), this.completedTasks(), this.totalTasks(), this.totalDuration(), this.totalGcTime(), this.totalInputBytes(), this.totalShuffleRead(), this.totalShuffleWrite(), this.isBlacklisted(), this.maxMemory(), this.addTime(), Option$.MODULE$.apply(this.removeTime()), Option$.MODULE$.apply(this.removeReason()), this.executorLogs(), memoryMetrics);
      return new ExecutorSummaryWrapper(info);
   }

   public LiveExecutor(String executorId, long _addTime) {
      this.executorId = executorId;
      this.hostPort = null;
      this.host = null;
      this.isActive = true;
      this.totalCores = 0;
      this.addTime = new Date(_addTime);
      this.removeTime = null;
      this.removeReason = null;
      this.rddBlocks = 0;
      this.memoryUsed = 0L;
      this.diskUsed = 0L;
      this.maxTasks = 0;
      this.maxMemory = 0L;
      this.totalTasks = 0;
      this.activeTasks = 0;
      this.completedTasks = 0;
      this.failedTasks = 0;
      this.totalDuration = 0L;
      this.totalGcTime = 0L;
      this.totalInputBytes = 0L;
      this.totalShuffleRead = 0L;
      this.totalShuffleWrite = 0L;
      this.isBlacklisted = false;
      this.executorLogs = (Map)Predef$.MODULE$.Map().apply(Nil$.MODULE$);
      this.totalOnHeap = -1L;
      this.totalOffHeap = 0L;
      this.usedOnHeap = 0L;
      this.usedOffHeap = 0L;
   }
}
