package org.apache.spark.deploy;

import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.util.MutableURLClassLoader;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001]<a!\u0001\u0002\t\u0002\tQ\u0011a\u0004#fa\u0016tG-\u001a8dsV#\u0018\u000e\\:\u000b\u0005\r!\u0011A\u00023fa2|\u0017P\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h!\tYA\"D\u0001\u0003\r\u0019i!\u0001#\u0001\u0003\u001d\tyA)\u001a9f]\u0012,gnY=Vi&d7o\u0005\u0002\r\u001fA\u0011\u0001cE\u0007\u0002#)\t!#A\u0003tG\u0006d\u0017-\u0003\u0002\u0015#\t1\u0011I\\=SK\u001aDQA\u0006\u0007\u0005\u0002a\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0015!)!\u0004\u0004C\u00017\u0005A\"/Z:pYZ,W*\u0019<f]\u0012+\u0007/\u001a8eK:\u001c\u0017.Z:\u0015\u000bq\u0019SeJ\u0015\u0011\u0005u\u0001cB\u0001\t\u001f\u0013\ty\u0012#\u0001\u0004Qe\u0016$WMZ\u0005\u0003C\t\u0012aa\u0015;sS:<'BA\u0010\u0012\u0011\u0015!\u0013\u00041\u0001\u001d\u0003I\u0001\u0018mY6bO\u0016\u001cX\t_2mkNLwN\\:\t\u000b\u0019J\u0002\u0019\u0001\u000f\u0002\u0011A\f7m[1hKNDQ\u0001K\rA\u0002q\tAB]3q_NLGo\u001c:jKNDQAK\rA\u0002q\t1\"\u001b<z%\u0016\u0004x\u000eU1uQ\")A\u0006\u0004C\u0001[\u00051\"/Z:pYZ,\u0017I\u001c3E_^tGn\\1e\u0015\u0006\u00148\u000f\u0006\u0004\u001d]A\u0012\u0004H\u0011\u0005\u0006_-\u0002\r\u0001H\u0001\u0005U\u0006\u00148\u000fC\u00032W\u0001\u0007A$A\u0004vg\u0016\u0014(*\u0019:\t\u000bMZ\u0003\u0019\u0001\u001b\u0002\u0013M\u0004\u0018M]6D_:4\u0007CA\u001b7\u001b\u0005!\u0011BA\u001c\u0005\u0005%\u0019\u0006/\u0019:l\u0007>tg\rC\u0003:W\u0001\u0007!(\u0001\u0006iC\u0012|w\u000e]\"p]\u001a\u0004\"a\u000f!\u000e\u0003qR!!\u0010 \u0002\t\r|gN\u001a\u0006\u0003\u007f\u0019\ta\u0001[1e_>\u0004\u0018BA!=\u00055\u0019uN\u001c4jOV\u0014\u0018\r^5p]\")1i\u000ba\u0001\t\u000611/Z2NOJ\u0004\"!N#\n\u0005\u0019#!aD*fGV\u0014\u0018\u000e^=NC:\fw-\u001a:\t\u000b!cA\u0011A%\u0002%\u0005$GMS1sgR{7\t\\1tgB\u000bG\u000f\u001b\u000b\u0004\u00156s\u0005C\u0001\tL\u0013\ta\u0015C\u0001\u0003V]&$\b\"B\u0018H\u0001\u0004a\u0002\"B(H\u0001\u0004\u0001\u0016A\u00027pC\u0012,'\u000f\u0005\u0002R)6\t!K\u0003\u0002T\t\u0005!Q\u000f^5m\u0013\t)&KA\u000bNkR\f'\r\\3V%2\u001bE.Y:t\u0019>\fG-\u001a:\t\u000b]cA\u0011\u0001-\u0002!\u0011|wO\u001c7pC\u00124\u0015\u000e\\3MSN$HC\u0002\u000fZ7\u00164w\rC\u0003[-\u0002\u0007A$\u0001\u0005gS2,G*[:u\u0011\u0015af\u000b1\u0001^\u0003%!\u0018M]4fi\u0012K'\u000f\u0005\u0002_G6\tqL\u0003\u0002aC\u0006\u0011\u0011n\u001c\u0006\u0002E\u0006!!.\u0019<b\u0013\t!wL\u0001\u0003GS2,\u0007\"B\u001aW\u0001\u0004!\u0004\"B\u001dW\u0001\u0004Q\u0004\"B\"W\u0001\u0004!\u0005\"B5\r\t\u0003Q\u0017\u0001\u00043po:dw.\u00193GS2,GC\u0002\u000fl[:|\u0007\u000fC\u0003mQ\u0002\u0007A$\u0001\u0003qCRD\u0007\"\u0002/i\u0001\u0004i\u0006\"B\u001ai\u0001\u0004!\u0004\"B\u001di\u0001\u0004Q\u0004\"B\"i\u0001\u0004!\u0005\"\u0002:\r\t\u0003\u0019\u0018\u0001\u0005:fg>dg/Z$m_\n\u0004\u0016\r\u001e5t)\raBO\u001e\u0005\u0006kF\u0004\r\u0001H\u0001\u0006a\u0006$\bn\u001d\u0005\u0006sE\u0004\rA\u000f"
)
public final class DependencyUtils {
   public static String resolveGlobPaths(String var0, Configuration var1) {
      return DependencyUtils$.MODULE$.resolveGlobPaths(var0, var1);
   }

   public static String downloadFile(String var0, File var1, SparkConf var2, Configuration var3, SecurityManager var4) {
      return DependencyUtils$.MODULE$.downloadFile(var0, var1, var2, var3, var4);
   }

   public static String downloadFileList(String var0, File var1, SparkConf var2, Configuration var3, SecurityManager var4) {
      return DependencyUtils$.MODULE$.downloadFileList(var0, var1, var2, var3, var4);
   }

   public static void addJarsToClassPath(String var0, MutableURLClassLoader var1) {
      DependencyUtils$.MODULE$.addJarsToClassPath(var0, var1);
   }

   public static String resolveAndDownloadJars(String var0, String var1, SparkConf var2, Configuration var3, SecurityManager var4) {
      return DependencyUtils$.MODULE$.resolveAndDownloadJars(var0, var1, var2, var3, var4);
   }

   public static String resolveMavenDependencies(String var0, String var1, String var2, String var3) {
      return DependencyUtils$.MODULE$.resolveMavenDependencies(var0, var1, var2, var3);
   }
}
