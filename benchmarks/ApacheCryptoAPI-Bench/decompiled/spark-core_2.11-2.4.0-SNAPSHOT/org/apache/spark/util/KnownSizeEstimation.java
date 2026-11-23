package org.apache.spark.util;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001a1\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\u0014\u0017:|wO\\*ju\u0016,5\u000f^5nCRLwN\u001c\u0006\u0003\u0007\u0011\tA!\u001e;jY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001aDQA\u0005\u0001\u0007\u0002Q\tQ\"Z:uS6\fG/\u001a3TSj,7\u0001A\u000b\u0002+A\u0011ABF\u0005\u0003/5\u0011A\u0001T8oO\u0002"
)
public interface KnownSizeEstimation {
   long estimatedSize();
}
