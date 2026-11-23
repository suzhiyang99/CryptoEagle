package org.apache.spark.metrics.source;

import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%:a!\u0001\u0002\t\u0002\u0019a\u0011!D*uCRL7mU8ve\u000e,7O\u0003\u0002\u0004\t\u000511o\\;sG\u0016T!!\u0002\u0004\u0002\u000f5,GO]5dg*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x\r\u0005\u0002\u000e\u001d5\t!A\u0002\u0004\u0010\u0005!\u0005a\u0001\u0005\u0002\u000e'R\fG/[2T_V\u00148-Z:\u0014\u00059\t\u0002C\u0001\n\u0016\u001b\u0005\u0019\"\"\u0001\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Y\u0019\"AB!osJ+g\rC\u0003\u0019\u001d\u0011\u0005!$\u0001\u0004=S:LGOP\u0002\u0001)\u0005a\u0001b\u0002\u000f\u000f\u0005\u0004%\t!H\u0001\u000bC2d7k\\;sG\u0016\u001cX#\u0001\u0010\u0011\u0007}\u0011C%D\u0001!\u0015\t\t3#\u0001\u0006d_2dWm\u0019;j_:L!a\t\u0011\u0003\u0007M+\u0017\u000f\u0005\u0002\u000eK%\u0011aE\u0001\u0002\u0007'>,(oY3\t\r!r\u0001\u0015!\u0003\u001f\u0003-\tG\u000e\\*pkJ\u001cWm\u001d\u0011"
)
public final class StaticSources {
   public static Seq<Source> allSources() {
      return StaticSources$.MODULE$.allSources();
   }
}
