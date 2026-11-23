package org.apache.spark.util;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001u1\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\u0006\u00072|7m\u001b\u0006\u0003\u0007\u0011\tA!\u001e;jY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001aDQA\u0005\u0001\u0007\u0002Q\tQbZ3u)&lW-T5mY&\u001c8\u0001\u0001\u000b\u0002+A\u0011ABF\u0005\u0003/5\u0011A\u0001T8oO\")\u0011\u0004\u0001D\u00015\u0005aq/Y5u)&dG\u000eV5nKR\u0011Qc\u0007\u0005\u00069a\u0001\r!F\u0001\u000bi\u0006\u0014x-\u001a;US6,\u0007"
)
public interface Clock {
   long getTimeMillis();

   long waitTillTime(long var1);
}
