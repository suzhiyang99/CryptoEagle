package org.apache.spark.storage;

import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.spark.util.io.ChunkedByteBuffer;
import scala.Function1;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000153\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\n\u00052|7m\u001b#bi\u0006T!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001aDQA\u0005\u0001\u0007\u0002Q\tQ\u0002^8J]B,Ho\u0015;sK\u0006l7\u0001\u0001\u000b\u0002+A\u0011acG\u0007\u0002/)\u0011\u0001$G\u0001\u0003S>T\u0011AG\u0001\u0005U\u00064\u0018-\u0003\u0002\u001d/\tY\u0011J\u001c9viN#(/Z1n\u0011\u0015q\u0002A\"\u0001 \u0003\u001d!xNT3uif$\u0012\u0001\t\t\u0003C\u0011j\u0011A\t\u0006\u0003Ge\tA\u0001\\1oO&\u0011QE\t\u0002\u0007\u001f\nTWm\u0019;\t\u000b\u001d\u0002a\u0011\u0001\u0015\u0002'Q|7\t[;oW\u0016$')\u001f;f\u0005V4g-\u001a:\u0015\u0005%\u0002\u0004C\u0001\u0016/\u001b\u0005Y#B\u0001\r-\u0015\tiC!\u0001\u0003vi&d\u0017BA\u0018,\u0005E\u0019\u0005.\u001e8lK\u0012\u0014\u0015\u0010^3Ck\u001a4WM\u001d\u0005\u0006c\u0019\u0002\rAM\u0001\nC2dwnY1u_J\u0004B\u0001D\u001a6q%\u0011A'\u0004\u0002\n\rVt7\r^5p]F\u0002\"\u0001\u0004\u001c\n\u0005]j!aA%oiB\u0011\u0011\bP\u0007\u0002u)\u00111(G\u0001\u0004]&|\u0017BA\u001f;\u0005)\u0011\u0015\u0010^3Ck\u001a4WM\u001d\u0005\u0006\u007f\u00011\t\u0001Q\u0001\ri>\u0014\u0015\u0010^3Ck\u001a4WM\u001d\u000b\u0002q!)!\t\u0001D\u0001\u0007\u0006!1/\u001b>f+\u0005!\u0005C\u0001\u0007F\u0013\t1UB\u0001\u0003M_:<\u0007\"\u0002%\u0001\r\u0003I\u0015a\u00023jgB|7/\u001a\u000b\u0002\u0015B\u0011AbS\u0005\u0003\u00196\u0011A!\u00168ji\u0002"
)
public interface BlockData {
   InputStream toInputStream();

   Object toNetty();

   ChunkedByteBuffer toChunkedByteBuffer(Function1<Object, ByteBuffer> var1);

   ByteBuffer toByteBuffer();

   long size();

   void dispose();
}
