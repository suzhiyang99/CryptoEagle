package org.apache.spark.api.r;

import scala.Option;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u00015;a!\u0001\u0002\t\u0002\u0019a\u0011A\u0002*Vi&d7O\u0003\u0002\u0004\t\u0005\t!O\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u0004\"!\u0004\b\u000e\u0003\t1aa\u0004\u0002\t\u0002\u0019\u0001\"A\u0002*Vi&d7o\u0005\u0002\u000f#A\u0011!#F\u0007\u0002')\tA#A\u0003tG\u0006d\u0017-\u0003\u0002\u0017'\t1\u0011I\\=SK\u001aDQ\u0001\u0007\b\u0005\u0002i\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0019!9AD\u0004a\u0001\n\u0003i\u0012!\u0003:QC\u000e\\\u0017mZ3t+\u0005q\u0002c\u0001\n C%\u0011\u0001e\u0005\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005\t*cB\u0001\n$\u0013\t!3#\u0001\u0004Qe\u0016$WMZ\u0005\u0003M\u001d\u0012aa\u0015;sS:<'B\u0001\u0013\u0014\u0011\u001dIc\u00021A\u0005\u0002)\nQB\u001d)bG.\fw-Z:`I\u0015\fHCA\u0016/!\t\u0011B&\u0003\u0002.'\t!QK\\5u\u0011\u001dy\u0003&!AA\u0002y\t1\u0001\u001f\u00132\u0011\u0019\td\u0002)Q\u0005=\u0005Q!\u000fU1dW\u0006<Wm\u001d\u0011\t\u000bMrA\u0011A\u000f\u0002-1|7-\u00197Ta\u0006\u00148N\u0015)bG.\fw-\u001a)bi\"DQ!\u000e\b\u0005\u0002Y\n\u0011#[:Ta\u0006\u00148NU%ogR\fG\u000e\\3e+\u00059\u0004C\u0001\n9\u0013\tI4CA\u0004C_>dW-\u00198\t\u000bmrA\u0011\u0001\u001f\u0002#M\u0004\u0018M]6S!\u0006\u001c7.Y4f!\u0006$\b\u000e\u0006\u0002>\u0013B\u0019aHR\u0011\u000f\u0005}\"eB\u0001!D\u001b\u0005\t%B\u0001\"\u001a\u0003\u0019a$o\\8u}%\tA#\u0003\u0002F'\u00059\u0001/Y2lC\u001e,\u0017BA$I\u0005\r\u0019V-\u001d\u0006\u0003\u000bNAQA\u0013\u001eA\u0002]\n\u0001\"[:Ee&4XM\u001d\u0005\u0006\u0019:!\tAN\u0001\rSN\u0014\u0016J\\:uC2dW\r\u001a"
)
public final class RUtils {
   public static boolean isRInstalled() {
      return RUtils$.MODULE$.isRInstalled();
   }

   public static Seq<String> sparkRPackagePath(boolean var0) {
      return RUtils$.MODULE$.sparkRPackagePath(var0);
   }

   public static boolean isSparkRInstalled() {
      return RUtils$.MODULE$.isSparkRInstalled();
   }

   public static Option<String> localSparkRPackagePath() {
      return RUtils$.MODULE$.localSparkRPackagePath();
   }

   public static void rPackages_$eq(Option<String> var0) {
      RUtils$.MODULE$.rPackages_$eq(var0);
   }

   public static Option<String> rPackages() {
      return RUtils$.MODULE$.rPackages();
   }
}
