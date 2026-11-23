package org.apache.spark.deploy.history;

import org.apache.spark.status.api.v1.ApplicationAttemptInfo;
import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001]3Q!\u0001\u0002\u0001\u00051\u0011!#\u0011;uK6\u0004H/\u00138g_^\u0013\u0018\r\u001d9fe*\u00111\u0001B\u0001\bQ&\u001cHo\u001c:z\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011Q\u0001!Q1A\u0005\u0002Y\tA!\u001b8g_\u000e\u0001Q#A\f\u0011\u0005ayR\"A\r\u000b\u0005iY\u0012A\u0001<2\u0015\taR$A\u0002ba&T!A\b\u0004\u0002\rM$\u0018\r^;t\u0013\t\u0001\u0013D\u0001\fBaBd\u0017nY1uS>t\u0017\t\u001e;f[B$\u0018J\u001c4p\u0011!\u0011\u0003A!A!\u0002\u00139\u0012!B5oM>\u0004\u0003\u0002\u0003\u0013\u0001\u0005\u000b\u0007I\u0011A\u0013\u0002\u000f1|w\rU1uQV\ta\u0005\u0005\u0002(U9\u0011a\u0002K\u0005\u0003S=\ta\u0001\u0015:fI\u00164\u0017BA\u0016-\u0005\u0019\u0019FO]5oO*\u0011\u0011f\u0004\u0005\t]\u0001\u0011\t\u0011)A\u0005M\u0005AAn\\4QCRD\u0007\u0005\u0003\u00051\u0001\t\u0015\r\u0011\"\u00012\u0003!1\u0017\u000e\\3TSj,W#\u0001\u001a\u0011\u00059\u0019\u0014B\u0001\u001b\u0010\u0005\u0011auN\\4\t\u0011Y\u0002!\u0011!Q\u0001\nI\n\u0011BZ5mKNK'0\u001a\u0011\t\u0011a\u0002!Q1A\u0005\u0002e\n\u0011\"\u00193nS:\f5\r\\:\u0016\u0003i\u00022AD\u001e'\u0013\tatB\u0001\u0004PaRLwN\u001c\u0005\t}\u0001\u0011\t\u0011)A\u0005u\u0005Q\u0011\rZ7j]\u0006\u001bGn\u001d\u0011\t\u0011\u0001\u0003!Q1A\u0005\u0002e\n\u0001B^5fo\u0006\u001bGn\u001d\u0005\t\u0005\u0002\u0011\t\u0011)A\u0005u\u0005Ia/[3x\u0003\u000ed7\u000f\t\u0005\t\t\u0002\u0011)\u0019!C\u0001s\u0005y\u0011\rZ7j]\u0006\u001bGn]$s_V\u00048\u000f\u0003\u0005G\u0001\t\u0005\t\u0015!\u0003;\u0003A\tG-\\5o\u0003\u000ed7o\u0012:pkB\u001c\b\u0005\u0003\u0005I\u0001\t\u0015\r\u0011\"\u0001:\u000391\u0018.Z<BG2\u001cxI]8vaND\u0001B\u0013\u0001\u0003\u0002\u0003\u0006IAO\u0001\u0010m&,w/Q2mg\u001e\u0013x.\u001e9tA!)A\n\u0001C\u0001\u001b\u00061A(\u001b8jiz\"\u0002B\u0014)R%N#VK\u0016\t\u0003\u001f\u0002i\u0011A\u0001\u0005\u0006)-\u0003\ra\u0006\u0005\u0006I-\u0003\rA\n\u0005\u0006a-\u0003\rA\r\u0005\u0006q-\u0003\rA\u000f\u0005\u0006\u0001.\u0003\rA\u000f\u0005\u0006\t.\u0003\rA\u000f\u0005\u0006\u0011.\u0003\rA\u000f"
)
public class AttemptInfoWrapper {
   private final ApplicationAttemptInfo info;
   private final String logPath;
   private final long fileSize;
   private final Option<String> adminAcls;
   private final Option<String> viewAcls;
   private final Option<String> adminAclsGroups;
   private final Option<String> viewAclsGroups;

   public ApplicationAttemptInfo info() {
      return this.info;
   }

   public String logPath() {
      return this.logPath;
   }

   public long fileSize() {
      return this.fileSize;
   }

   public Option<String> adminAcls() {
      return this.adminAcls;
   }

   public Option<String> viewAcls() {
      return this.viewAcls;
   }

   public Option<String> adminAclsGroups() {
      return this.adminAclsGroups;
   }

   public Option<String> viewAclsGroups() {
      return this.viewAclsGroups;
   }

   public AttemptInfoWrapper(ApplicationAttemptInfo info, String logPath, long fileSize, Option<String> adminAcls, Option<String> viewAcls, Option<String> adminAclsGroups, Option<String> viewAclsGroups) {
      this.info = info;
      this.logPath = logPath;
      this.fileSize = fileSize;
      this.adminAcls = adminAcls;
      this.viewAcls = viewAcls;
      this.adminAclsGroups = adminAclsGroups;
      this.viewAclsGroups = viewAclsGroups;
   }
}
