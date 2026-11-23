package org.apache.spark.status.api.v1;

import java.util.Date;
import scala.Option;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}d\u0001B\u0001\u0003\u0001=\u0011q\"\u0012=fGV$xN]*v[6\f'/\u001f\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001!A\u0011\u0011\u0003F\u0007\u0002%)\t1#A\u0003tG\u0006d\u0017-\u0003\u0002\u0016%\t1\u0011I\\=SK\u001aD\u0001b\u0006\u0001\u0003\u0006\u0004%\t\u0001G\u0001\u0003S\u0012,\u0012!\u0007\t\u00035uq!!E\u000e\n\u0005q\u0011\u0012A\u0002)sK\u0012,g-\u0003\u0002\u001f?\t11\u000b\u001e:j]\u001eT!\u0001\b\n\t\u0011\u0005\u0002!\u0011!Q\u0001\ne\t1!\u001b3!\u0011!\u0019\u0003A!b\u0001\n\u0003A\u0012\u0001\u00035pgR\u0004vN\u001d;\t\u0011\u0015\u0002!\u0011!Q\u0001\ne\t\u0011\u0002[8tiB{'\u000f\u001e\u0011\t\u0011\u001d\u0002!Q1A\u0005\u0002!\n\u0001\"[:BGRLg/Z\u000b\u0002SA\u0011\u0011CK\u0005\u0003WI\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005.\u0001\t\u0005\t\u0015!\u0003*\u0003%I7/Q2uSZ,\u0007\u0005\u0003\u00050\u0001\t\u0015\r\u0011\"\u00011\u0003%\u0011H\r\u001a\"m_\u000e\\7/F\u00012!\t\t\"'\u0003\u00024%\t\u0019\u0011J\u001c;\t\u0011U\u0002!\u0011!Q\u0001\nE\n!B\u001d3e\u00052|7m[:!\u0011!9\u0004A!b\u0001\n\u0003A\u0014AC7f[>\u0014\u00180V:fIV\t\u0011\b\u0005\u0002\u0012u%\u00111H\u0005\u0002\u0005\u0019>tw\r\u0003\u0005>\u0001\t\u0005\t\u0015!\u0003:\u0003-iW-\\8ssV\u001bX\r\u001a\u0011\t\u0011}\u0002!Q1A\u0005\u0002a\n\u0001\u0002Z5tWV\u001bX\r\u001a\u0005\t\u0003\u0002\u0011\t\u0011)A\u0005s\u0005IA-[:l+N,G\r\t\u0005\t\u0007\u0002\u0011)\u0019!C\u0001a\u0005QAo\u001c;bY\u000e{'/Z:\t\u0011\u0015\u0003!\u0011!Q\u0001\nE\n1\u0002^8uC2\u001cuN]3tA!Aq\t\u0001BC\u0002\u0013\u0005\u0001'\u0001\u0005nCb$\u0016m]6t\u0011!I\u0005A!A!\u0002\u0013\t\u0014!C7bqR\u000b7o[:!\u0011!Y\u0005A!b\u0001\n\u0003\u0001\u0014aC1di&4X\rV1tWND\u0001\"\u0014\u0001\u0003\u0002\u0003\u0006I!M\u0001\rC\u000e$\u0018N^3UCN\\7\u000f\t\u0005\t\u001f\u0002\u0011)\u0019!C\u0001a\u0005Ya-Y5mK\u0012$\u0016m]6t\u0011!\t\u0006A!A!\u0002\u0013\t\u0014\u0001\u00044bS2,G\rV1tWN\u0004\u0003\u0002C*\u0001\u0005\u000b\u0007I\u0011\u0001\u0019\u0002\u001d\r|W\u000e\u001d7fi\u0016$G+Y:lg\"AQ\u000b\u0001B\u0001B\u0003%\u0011'A\bd_6\u0004H.\u001a;fIR\u000b7o[:!\u0011!9\u0006A!b\u0001\n\u0003\u0001\u0014A\u0003;pi\u0006dG+Y:lg\"A\u0011\f\u0001B\u0001B\u0003%\u0011'A\u0006u_R\fG\u000eV1tWN\u0004\u0003\u0002C.\u0001\u0005\u000b\u0007I\u0011\u0001\u001d\u0002\u001bQ|G/\u00197EkJ\fG/[8o\u0011!i\u0006A!A!\u0002\u0013I\u0014A\u0004;pi\u0006dG)\u001e:bi&|g\u000e\t\u0005\t?\u0002\u0011)\u0019!C\u0001q\u0005YAo\u001c;bY\u001e\u001bE+[7f\u0011!\t\u0007A!A!\u0002\u0013I\u0014\u0001\u0004;pi\u0006dwi\u0011+j[\u0016\u0004\u0003\u0002C2\u0001\u0005\u000b\u0007I\u0011\u0001\u001d\u0002\u001fQ|G/\u00197J]B,HOQ=uKND\u0001\"\u001a\u0001\u0003\u0002\u0003\u0006I!O\u0001\u0011i>$\u0018\r\\%oaV$()\u001f;fg\u0002B\u0001b\u001a\u0001\u0003\u0006\u0004%\t\u0001O\u0001\u0011i>$\u0018\r\\*ik\u001a4G.\u001a*fC\u0012D\u0001\"\u001b\u0001\u0003\u0002\u0003\u0006I!O\u0001\u0012i>$\u0018\r\\*ik\u001a4G.\u001a*fC\u0012\u0004\u0003\u0002C6\u0001\u0005\u000b\u0007I\u0011\u0001\u001d\u0002#Q|G/\u00197TQV4g\r\\3Xe&$X\r\u0003\u0005n\u0001\t\u0005\t\u0015!\u0003:\u0003I!x\u000e^1m'\",hM\u001a7f/JLG/\u001a\u0011\t\u0011=\u0004!Q1A\u0005\u0002!\nQ\"[:CY\u0006\u001c7\u000e\\5ti\u0016$\u0007\u0002C9\u0001\u0005\u0003\u0005\u000b\u0011B\u0015\u0002\u001d%\u001c(\t\\1dW2L7\u000f^3eA!A1\u000f\u0001BC\u0002\u0013\u0005\u0001(A\u0005nCblU-\\8ss\"AQ\u000f\u0001B\u0001B\u0003%\u0011(\u0001\u0006nCblU-\\8ss\u0002B\u0001b\u001e\u0001\u0003\u0006\u0004%\t\u0001_\u0001\bC\u0012$G+[7f+\u0005I\bC\u0001>\u0000\u001b\u0005Y(B\u0001?~\u0003\u0011)H/\u001b7\u000b\u0003y\fAA[1wC&\u0019\u0011\u0011A>\u0003\t\u0011\u000bG/\u001a\u0005\n\u0003\u000b\u0001!\u0011!Q\u0001\ne\f\u0001\"\u00193e)&lW\r\t\u0005\u000b\u0003\u0013\u0001!Q1A\u0005\u0002\u0005-\u0011A\u0003:f[>4X\rV5nKV\u0011\u0011Q\u0002\t\u0005#\u0005=\u00110C\u0002\u0002\u0012I\u0011aa\u00149uS>t\u0007BCA\u000b\u0001\t\u0005\t\u0015!\u0003\u0002\u000e\u0005Y!/Z7pm\u0016$\u0016.\\3!\u0011)\tI\u0002\u0001BC\u0002\u0013\u0005\u00111D\u0001\re\u0016lwN^3SK\u0006\u001cxN\\\u000b\u0003\u0003;\u0001B!EA\b3!Q\u0011\u0011\u0005\u0001\u0003\u0002\u0003\u0006I!!\b\u0002\u001bI,Wn\u001c<f%\u0016\f7o\u001c8!\u0011)\t)\u0003\u0001BC\u0002\u0013\u0005\u0011qE\u0001\rKb,7-\u001e;pe2{wm]\u000b\u0003\u0003S\u0001RAGA\u00163eI1!!\f \u0005\ri\u0015\r\u001d\u0005\u000b\u0003c\u0001!\u0011!Q\u0001\n\u0005%\u0012!D3yK\u000e,Ho\u001c:M_\u001e\u001c\b\u0005\u0003\u0006\u00026\u0001\u0011)\u0019!C\u0001\u0003o\tQ\"\\3n_JLX*\u001a;sS\u000e\u001cXCAA\u001d!\u0015\t\u0012qBA\u001e!\u0011\ti$a\u0010\u000e\u0003\tI1!!\u0011\u0003\u00055iU-\\8ss6+GO]5dg\"Q\u0011Q\t\u0001\u0003\u0002\u0003\u0006I!!\u000f\u0002\u001d5,Wn\u001c:z\u001b\u0016$(/[2tA!A\u0011\u0011\n\u0001\u0005\u0002!\tY%\u0001\u0004=S:LGO\u0010\u000b3\u0003\u001b\ny%!\u0015\u0002T\u0005U\u0013qKA-\u00037\ni&a\u0018\u0002b\u0005\r\u0014QMA4\u0003S\nY'!\u001c\u0002p\u0005E\u00141OA;\u0003o\nI(a\u001f\u0002~A\u0019\u0011Q\b\u0001\t\r]\t9\u00051\u0001\u001a\u0011\u0019\u0019\u0013q\ta\u00013!1q%a\u0012A\u0002%BaaLA$\u0001\u0004\t\u0004BB\u001c\u0002H\u0001\u0007\u0011\b\u0003\u0004@\u0003\u000f\u0002\r!\u000f\u0005\u0007\u0007\u0006\u001d\u0003\u0019A\u0019\t\r\u001d\u000b9\u00051\u00012\u0011\u0019Y\u0015q\ta\u0001c!1q*a\u0012A\u0002EBaaUA$\u0001\u0004\t\u0004BB,\u0002H\u0001\u0007\u0011\u0007\u0003\u0004\\\u0003\u000f\u0002\r!\u000f\u0005\u0007?\u0006\u001d\u0003\u0019A\u001d\t\r\r\f9\u00051\u0001:\u0011\u00199\u0017q\ta\u0001s!11.a\u0012A\u0002eBaa\\A$\u0001\u0004I\u0003BB:\u0002H\u0001\u0007\u0011\b\u0003\u0004x\u0003\u000f\u0002\r!\u001f\u0005\t\u0003\u0013\t9\u00051\u0001\u0002\u000e!A\u0011\u0011DA$\u0001\u0004\ti\u0002\u0003\u0005\u0002&\u0005\u001d\u0003\u0019AA\u0015\u0011!\t)$a\u0012A\u0002\u0005e\u0002"
)
public class ExecutorSummary {
   private final String id;
   private final String hostPort;
   private final boolean isActive;
   private final int rddBlocks;
   private final long memoryUsed;
   private final long diskUsed;
   private final int totalCores;
   private final int maxTasks;
   private final int activeTasks;
   private final int failedTasks;
   private final int completedTasks;
   private final int totalTasks;
   private final long totalDuration;
   private final long totalGCTime;
   private final long totalInputBytes;
   private final long totalShuffleRead;
   private final long totalShuffleWrite;
   private final boolean isBlacklisted;
   private final long maxMemory;
   private final Date addTime;
   private final Option<Date> removeTime;
   private final Option<String> removeReason;
   private final Map<String, String> executorLogs;
   private final Option<MemoryMetrics> memoryMetrics;

   public String id() {
      return this.id;
   }

   public String hostPort() {
      return this.hostPort;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public int rddBlocks() {
      return this.rddBlocks;
   }

   public long memoryUsed() {
      return this.memoryUsed;
   }

   public long diskUsed() {
      return this.diskUsed;
   }

   public int totalCores() {
      return this.totalCores;
   }

   public int maxTasks() {
      return this.maxTasks;
   }

   public int activeTasks() {
      return this.activeTasks;
   }

   public int failedTasks() {
      return this.failedTasks;
   }

   public int completedTasks() {
      return this.completedTasks;
   }

   public int totalTasks() {
      return this.totalTasks;
   }

   public long totalDuration() {
      return this.totalDuration;
   }

   public long totalGCTime() {
      return this.totalGCTime;
   }

   public long totalInputBytes() {
      return this.totalInputBytes;
   }

   public long totalShuffleRead() {
      return this.totalShuffleRead;
   }

   public long totalShuffleWrite() {
      return this.totalShuffleWrite;
   }

   public boolean isBlacklisted() {
      return this.isBlacklisted;
   }

   public long maxMemory() {
      return this.maxMemory;
   }

   public Date addTime() {
      return this.addTime;
   }

   public Option<Date> removeTime() {
      return this.removeTime;
   }

   public Option<String> removeReason() {
      return this.removeReason;
   }

   public Map<String, String> executorLogs() {
      return this.executorLogs;
   }

   public Option<MemoryMetrics> memoryMetrics() {
      return this.memoryMetrics;
   }

   public ExecutorSummary(String id, String hostPort, boolean isActive, int rddBlocks, long memoryUsed, long diskUsed, int totalCores, int maxTasks, int activeTasks, int failedTasks, int completedTasks, int totalTasks, long totalDuration, long totalGCTime, long totalInputBytes, long totalShuffleRead, long totalShuffleWrite, boolean isBlacklisted, long maxMemory, Date addTime, Option<Date> removeTime, Option<String> removeReason, Map<String, String> executorLogs, Option<MemoryMetrics> memoryMetrics) {
      this.id = id;
      this.hostPort = hostPort;
      this.isActive = isActive;
      this.rddBlocks = rddBlocks;
      this.memoryUsed = memoryUsed;
      this.diskUsed = diskUsed;
      this.totalCores = totalCores;
      this.maxTasks = maxTasks;
      this.activeTasks = activeTasks;
      this.failedTasks = failedTasks;
      this.completedTasks = completedTasks;
      this.totalTasks = totalTasks;
      this.totalDuration = totalDuration;
      this.totalGCTime = totalGCTime;
      this.totalInputBytes = totalInputBytes;
      this.totalShuffleRead = totalShuffleRead;
      this.totalShuffleWrite = totalShuffleWrite;
      this.isBlacklisted = isBlacklisted;
      this.maxMemory = maxMemory;
      this.addTime = addTime;
      this.removeTime = removeTime;
      this.removeReason = removeReason;
      this.executorLogs = executorLogs;
      this.memoryMetrics = memoryMetrics;
   }
}
