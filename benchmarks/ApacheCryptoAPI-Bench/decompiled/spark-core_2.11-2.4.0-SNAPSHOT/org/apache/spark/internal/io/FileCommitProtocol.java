package org.apache.spark.internal.io;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import scala.Option;
import scala.Serializable;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ee!B\u0001\u0003\u0003\u0003i!A\u0005$jY\u0016\u001cu.\\7jiB\u0013x\u000e^8d_2T!a\u0001\u0003\u0002\u0005%|'BA\u0003\u0007\u0003!Ig\u000e^3s]\u0006d'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001M\u0011\u0001A\u0004\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0002#\u0005)1oY1mC&\u00111\u0003\u0005\u0002\u0007\u0003:L(+\u001a4\t\u000bU\u0001A\u0011\u0001\f\u0002\rqJg.\u001b;?)\u00059\u0002C\u0001\r\u0001\u001b\u0005\u0011\u0001\"\u0002\u000e\u0001\r\u0003Y\u0012\u0001C:fiV\u0004(j\u001c2\u0015\u0005qy\u0002CA\b\u001e\u0013\tq\u0002C\u0001\u0003V]&$\b\"\u0002\u0011\u001a\u0001\u0004\t\u0013A\u00036pE\u000e{g\u000e^3yiB\u0011!eJ\u0007\u0002G)\u0011A%J\u0001\n[\u0006\u0004(/\u001a3vG\u0016T!A\n\u0005\u0002\r!\fGm\\8q\u0013\tA3E\u0001\u0006K_\n\u001cuN\u001c;fqRDQA\u000b\u0001\u0007\u0002-\n\u0011bY8n[&$(j\u001c2\u0015\u0007qaS\u0006C\u0003!S\u0001\u0007\u0011\u0005C\u0003/S\u0001\u0007q&A\u0006uCN\\7i\\7nSR\u001c\bc\u0001\u00199w9\u0011\u0011G\u000e\b\u0003eUj\u0011a\r\u0006\u0003i1\ta\u0001\u0010:p_Rt\u0014\"A\t\n\u0005]\u0002\u0012a\u00029bG.\fw-Z\u0005\u0003si\u00121aU3r\u0015\t9\u0004\u0003\u0005\u0002=\t:\u0011\u0001$P\u0004\u0006}\tA\taP\u0001\u0013\r&dWmQ8n[&$\bK]8u_\u000e|G\u000e\u0005\u0002\u0019\u0001\u001a)\u0011A\u0001E\u0001\u0003N\u0011\u0001I\u0004\u0005\u0006+\u0001#\ta\u0011\u000b\u0002\u007f\u0019!Q\t\u0011\u0001G\u0005E!\u0016m]6D_6l\u0017\u000e^'fgN\fw-Z\n\u0004\t:9\u0005CA\bI\u0013\tI\u0005C\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005L\t\n\u0015\r\u0011\"\u0001M\u0003\ry'M[\u000b\u0002\u001bB\u0011qBT\u0005\u0003\u001fB\u00111!\u00118z\u0011!\tFI!A!\u0002\u0013i\u0015\u0001B8cU\u0002BQ!\u0006#\u0005\u0002M#\"\u0001\u0016,\u0011\u0005U#U\"\u0001!\t\u000b-\u0013\u0006\u0019A'\b\u000ba\u0003\u0005\u0012A-\u0002-\u0015k\u0007\u000f^=UCN\\7i\\7nSRlUm]:bO\u0016\u0004\"!\u0016.\u0007\u000bm\u0003\u0005\u0012\u0001/\u0003-\u0015k\u0007\u000f^=UCN\\7i\\7nSRlUm]:bO\u0016\u001c\"A\u0017+\t\u000bUQF\u0011\u00010\u0015\u0003eCq\u0001\u0019.\u0002\u0002\u0013%\u0011-A\u0006sK\u0006$'+Z:pYZ,G#\u00012\u0011\u0005\rDW\"\u00013\u000b\u0005\u00154\u0017\u0001\u00027b]\u001eT\u0011aZ\u0001\u0005U\u00064\u0018-\u0003\u0002jI\n1qJ\u00196fGRDQa\u001b!\u0005\u00021\f1\"\u001b8ti\u0006tG/[1uKR)q#\u001c<yu\")aN\u001ba\u0001_\u0006I1\r\\1tg:\u000bW.\u001a\t\u0003aNt!aD9\n\u0005I\u0004\u0012A\u0002)sK\u0012,g-\u0003\u0002uk\n11\u000b\u001e:j]\u001eT!A\u001d\t\t\u000b]T\u0007\u0019A8\u0002\u000b)|'-\u00133\t\u000beT\u0007\u0019A8\u0002\u0015=,H\u000f];u!\u0006$\b\u000eC\u0004|UB\u0005\t\u0019\u0001?\u00023\u0011Lh.Y7jGB\u000b'\u000f^5uS>twJ^3soJLG/\u001a\t\u0003\u001fuL!A \t\u0003\u000f\t{w\u000e\\3b]\"I\u0011\u0011\u0001!\u0012\u0002\u0013\u0005\u00111A\u0001\u0016S:\u001cH/\u00198uS\u0006$X\r\n3fM\u0006,H\u000e\u001e\u00135+\t\t)AK\u0002}\u0003\u000fY#!!\u0003\u0011\t\u0005-\u0011QC\u0007\u0003\u0003\u001bQA!a\u0004\u0002\u0012\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003'\u0001\u0012AC1o]>$\u0018\r^5p]&!\u0011qCA\u0007\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b\u00037\u0001a\u0011AA\u000f\u0003!\t'm\u001c:u\u0015>\u0014Gc\u0001\u000f\u0002 !1\u0001%!\u0007A\u0002\u0005Bq!a\t\u0001\r\u0003\t)#A\u0005tKR,\b\u000fV1tWR\u0019A$a\n\t\u0011\u0005%\u0012\u0011\u0005a\u0001\u0003W\t1\u0002^1tW\u000e{g\u000e^3yiB\u0019!%!\f\n\u0007\u0005=2E\u0001\nUCN\\\u0017\t\u001e;f[B$8i\u001c8uKb$\bbBA\u001a\u0001\u0019\u0005\u0011QG\u0001\u0010]\u0016<H+Y:l)\u0016l\u0007OR5mKR9q.a\u000e\u0002:\u0005\r\u0003\u0002CA\u0015\u0003c\u0001\r!a\u000b\t\u0011\u0005m\u0012\u0011\u0007a\u0001\u0003{\t1\u0001Z5s!\u0011y\u0011qH8\n\u0007\u0005\u0005\u0003C\u0001\u0004PaRLwN\u001c\u0005\b\u0003\u000b\n\t\u00041\u0001p\u0003\r)\u0007\u0010\u001e\u0005\b\u0003\u0013\u0002a\u0011AA&\u0003YqWm\u001e+bg.$V-\u001c9GS2,\u0017IY:QCRDGcB8\u0002N\u0005=\u00131\u000b\u0005\t\u0003S\t9\u00051\u0001\u0002,!9\u0011\u0011KA$\u0001\u0004y\u0017aC1cg>dW\u000f^3ESJDq!!\u0012\u0002H\u0001\u0007q\u000eC\u0004\u0002X\u00011\t!!\u0017\u0002\u0015\r|W.\\5u)\u0006\u001c8\u000eF\u0002<\u00037B\u0001\"!\u000b\u0002V\u0001\u0007\u00111\u0006\u0005\b\u0003?\u0002a\u0011AA1\u0003%\t'm\u001c:u)\u0006\u001c8\u000eF\u0002\u001d\u0003GB\u0001\"!\u000b\u0002^\u0001\u0007\u00111\u0006\u0005\b\u0003O\u0002A\u0011AA5\u00035!W\r\\3uK^KG\u000f\u001b&pER9A0a\u001b\u0002z\u0005\r\u0005\u0002CA7\u0003K\u0002\r!a\u001c\u0002\u0005\u0019\u001c\b\u0003BA9\u0003kj!!a\u001d\u000b\u0007\u00055T%\u0003\u0003\u0002x\u0005M$A\u0003$jY\u0016\u001c\u0016p\u001d;f[\"A\u00111PA3\u0001\u0004\ti(\u0001\u0003qCRD\u0007\u0003BA9\u0003\u007fJA!!!\u0002t\t!\u0001+\u0019;i\u0011\u001d\t))!\u001aA\u0002q\f\u0011B]3dkJ\u001c\u0018N^3\t\u000f\u0005%\u0005\u0001\"\u0001\u0002\f\u0006aqN\u001c+bg.\u001cu.\\7jiR\u0019A$!$\t\u000f\u0005=\u0015q\u0011a\u0001w\u0005QA/Y:l\u0007>lW.\u001b;"
)
public abstract class FileCommitProtocol {
   public static boolean instantiate$default$4() {
      return FileCommitProtocol$.MODULE$.instantiate$default$4();
   }

   public static FileCommitProtocol instantiate(String var0, String var1, String var2, boolean var3) {
      return FileCommitProtocol$.MODULE$.instantiate(var0, var1, var2, var3);
   }

   public abstract void setupJob(JobContext var1);

   public abstract void commitJob(JobContext var1, Seq<TaskCommitMessage> var2);

   public abstract void abortJob(JobContext var1);

   public abstract void setupTask(TaskAttemptContext var1);

   public abstract String newTaskTempFile(TaskAttemptContext var1, Option<String> var2, String var3);

   public abstract String newTaskTempFileAbsPath(TaskAttemptContext var1, String var2, String var3);

   public abstract TaskCommitMessage commitTask(TaskAttemptContext var1);

   public abstract void abortTask(TaskAttemptContext var1);

   public boolean deleteWithJob(FileSystem fs, Path path, boolean recursive) {
      return fs.delete(path, recursive);
   }

   public void onTaskCommit(TaskCommitMessage taskCommit) {
   }

   public static class TaskCommitMessage implements Serializable {
      private final Object obj;

      public Object obj() {
         return this.obj;
      }

      public TaskCommitMessage(Object obj) {
         this.obj = obj;
      }
   }

   public static class EmptyTaskCommitMessage$ extends TaskCommitMessage {
      public static final EmptyTaskCommitMessage$ MODULE$;

      static {
         new EmptyTaskCommitMessage$();
      }

      private Object readResolve() {
         return MODULE$;
      }

      public EmptyTaskCommitMessage$() {
         super((Object)null);
         MODULE$ = this;
      }
   }
}
