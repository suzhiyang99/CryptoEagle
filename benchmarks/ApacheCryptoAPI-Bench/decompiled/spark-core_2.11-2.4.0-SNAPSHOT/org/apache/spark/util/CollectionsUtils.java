package org.apache.spark.util;

import scala.Function2;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d;a!\u0001\u0002\t\u0002\u0011Q\u0011\u0001E\"pY2,7\r^5p]N,F/\u001b7t\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0011\u0005-aQ\"\u0001\u0002\u0007\r5\u0011\u0001\u0012\u0001\u0003\u000f\u0005A\u0019u\u000e\u001c7fGRLwN\\:Vi&d7o\u0005\u0002\r\u001fA\u0011\u0001cE\u0007\u0002#)\t!#A\u0003tG\u0006d\u0017-\u0003\u0002\u0015#\t1\u0011I\\=SK\u001aDQA\u0006\u0007\u0005\u0002a\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0015!)!\u0004\u0004C\u00017\u0005\u0001R.Y6f\u0005&t\u0017M]=TK\u0006\u00148\r[\u000b\u00039\u0015\"2!H\u0019@!\u0015\u0001b\u0004I\u0012/\u0013\ty\u0012CA\u0005Gk:\u001cG/[8oeA\u0019\u0001#I\u0012\n\u0005\t\n\"!B!se\u0006L\bC\u0001\u0013&\u0019\u0001!QAJ\rC\u0002\u001d\u0012\u0011aS\t\u0003Q-\u0002\"\u0001E\u0015\n\u0005)\n\"a\u0002(pi\"Lgn\u001a\t\u0003!1J!!L\t\u0003\u0007\u0005s\u0017\u0010\u0005\u0002\u0011_%\u0011\u0001'\u0005\u0002\u0004\u0013:$\bb\u0002\u001a\u001a\u0003\u0003\u0005\u001daM\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004c\u0001\u001b=G9\u0011QG\u000f\b\u0003mej\u0011a\u000e\u0006\u0003q]\ta\u0001\u0010:p_Rt\u0014\"\u0001\n\n\u0005m\n\u0012a\u00029bG.\fw-Z\u0005\u0003{y\u0012\u0001b\u0014:eKJLgn\u001a\u0006\u0003wEAq\u0001Q\r\u0002\u0002\u0003\u000f\u0011)\u0001\u0006fm&$WM\\2fII\u00022AQ#$\u001b\u0005\u0019%B\u0001#\u0012\u0003\u001d\u0011XM\u001a7fGRL!AR\"\u0003\u0011\rc\u0017m]:UC\u001e\u0004"
)
public final class CollectionsUtils {
   public static <K> Function2<Object, K, Object> makeBinarySearch(Ordering<K> var0, ClassTag<K> var1) {
      return CollectionsUtils$.MODULE$.makeBinarySearch(var0, var1);
   }
}
