package org.apache.spark.deploy.history;

import java.util.zip.ZipOutputStream;
import org.apache.spark.SparkException;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.ui.SparkUI;
import scala.Option;
import scala.Predef$;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.reflect.ScalaSignature;
import scala.xml.Node;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055bAB\u0001\u0003\u0003\u0003\u0011AB\u0001\u000eBaBd\u0017nY1uS>t\u0007*[:u_JL\bK]8wS\u0012,'O\u0003\u0002\u0004\t\u00059\u0001.[:u_JL(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001aDQ\u0001\u0006\u0001\u0005\u0002Y\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002/A\u0011\u0001\u0004A\u0007\u0002\u0005!)!\u0004\u0001C\u00017\u0005Ar-\u001a;Fm\u0016tG\u000fT8hgVsG-\u001a:Qe>\u001cWm]:\u0015\u0003q\u0001\"AD\u000f\n\u0005yy!aA%oi\")\u0001\u0005\u0001C\u0001C\u0005\u0011r-\u001a;MCN$X\u000b\u001d3bi\u0016$G+[7f)\u0005\u0011\u0003C\u0001\b$\u0013\t!sB\u0001\u0003M_:<\u0007\"\u0002\u0014\u0001\r\u00039\u0013AC4fi2K7\u000f^5oOR\t\u0001\u0006E\u0002*cQr!AK\u0018\u000f\u0005-rS\"\u0001\u0017\u000b\u00055*\u0012A\u0002\u001fs_>$h(C\u0001\u0011\u0013\t\u0001t\"A\u0004qC\u000e\\\u0017mZ3\n\u0005I\u001a$\u0001C%uKJ\fGo\u001c:\u000b\u0005Az\u0001CA\u001b=\u001b\u00051$BA\u001c9\u0003\t1\u0018G\u0003\u0002:u\u0005\u0019\u0011\r]5\u000b\u0005m2\u0011AB:uCR,8/\u0003\u0002>m\ty\u0011\t\u001d9mS\u000e\fG/[8o\u0013:4w\u000eC\u0003@\u0001\u0019\u0005\u0001)\u0001\u0005hKR\f\u0005\u000f]+J)\r\tu\t\u0015\t\u0004\u001d\t#\u0015BA\"\u0010\u0005\u0019y\u0005\u000f^5p]B\u0011\u0001$R\u0005\u0003\r\n\u00111\u0002T8bI\u0016$\u0017\t\u001d9V\u0013\")\u0001J\u0010a\u0001\u0013\u0006)\u0011\r\u001d9JIB\u0011!*\u0014\b\u0003\u001d-K!\u0001T\b\u0002\rA\u0013X\rZ3g\u0013\tquJ\u0001\u0004TiJLgn\u001a\u0006\u0003\u0019>AQ!\u0015 A\u0002I\u000b\u0011\"\u0019;uK6\u0004H/\u00133\u0011\u00079\u0011\u0015\nC\u0003U\u0001\u0011\u0005Q+\u0001\u0003ti>\u0004H#\u0001,\u0011\u000599\u0016B\u0001-\u0010\u0005\u0011)f.\u001b;\t\u000bi\u0003A\u0011A.\u0002\u0013\u001d,GoQ8oM&<G#\u0001/\u0011\t)k\u0016*S\u0005\u0003=>\u00131!T1q\u0011\u0015\u0001\u0007A\"\u0001b\u000399(/\u001b;f\u000bZ,g\u000e\u001e'pON$BA\u00162dI\")\u0001j\u0018a\u0001\u0013\")\u0011k\u0018a\u0001%\")Qm\u0018a\u0001M\u0006I!0\u001b9TiJ,\u0017-\u001c\t\u0003O:l\u0011\u0001\u001b\u0006\u0003S*\f1A_5q\u0015\tYG.\u0001\u0003vi&d'\"A7\u0002\t)\fg/Y\u0005\u0003_\"\u0014qBW5q\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0015\u0004?FD\bc\u0001\bsi&\u00111o\u0004\u0002\u0007i\"\u0014xn^:\u0011\u0005U4X\"\u0001\u0004\n\u0005]4!AD*qCJ\\W\t_2faRLwN\\\u0012\u0002i\")!\u0010\u0001D\u0001w\u0006\u0011r-\u001a;BaBd\u0017nY1uS>t\u0017J\u001c4p)\taX\u0010E\u0002\u000f\u0005RBQ\u0001S=A\u0002%Caa \u0001\u0005\u0002\u0005\u0005\u0011aE4fi\u0016k\u0007\u000f^=MSN$\u0018N\\4Ii6dGCAA\u0002!\u0015I\u0013QAA\u0005\u0013\r\t9a\r\u0002\u0004'\u0016\f\b\u0003BA\u0006\u0003#i!!!\u0004\u000b\u0007\u0005=q\"A\u0002y[2LA!a\u0005\u0002\u000e\t!aj\u001c3f\u0011\u001d\t9\u0002\u0001C\u0001\u00033\tAb\u001c8V\u0013\u0012+G/Y2iK\u0012$rAVA\u000e\u0003;\ty\u0002\u0003\u0004I\u0003+\u0001\r!\u0013\u0005\u0007#\u0006U\u0001\u0019\u0001*\t\u0011\u0005\u0005\u0012Q\u0003a\u0001\u0003G\t!!^5\u0011\t\u0005\u0015\u0012\u0011F\u0007\u0003\u0003OQ1!!\t\u0007\u0013\u0011\tY#a\n\u0003\u000fM\u0003\u0018M]6V\u0013\u0002"
)
public abstract class ApplicationHistoryProvider {
   public int getEventLogsUnderProcess() {
      return 0;
   }

   public long getLastUpdatedTime() {
      return 0L;
   }

   public abstract Iterator<ApplicationInfo> getListing();

   public abstract Option<LoadedAppUI> getAppUI(String var1, Option<String> var2);

   public void stop() {
   }

   public Map<String, String> getConfig() {
      return (Map)Predef$.MODULE$.Map().apply(Nil$.MODULE$);
   }

   public abstract void writeEventLogs(String var1, Option<String> var2, ZipOutputStream var3) throws SparkException;

   public abstract Option<ApplicationInfo> getApplicationInfo(String var1);

   public Seq<Node> getEmptyListingHtml() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public void onUIDetached(String appId, Option<String> attemptId, SparkUI ui) {
   }
}
