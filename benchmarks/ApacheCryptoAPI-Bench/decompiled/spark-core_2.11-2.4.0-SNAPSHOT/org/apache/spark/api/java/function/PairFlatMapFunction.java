package org.apache.spark.api.java.function;

import java.io.Serializable;
import java.util.Iterator;
import scala.Tuple2;

@FunctionalInterface
public interface PairFlatMapFunction<T, K, V> extends Serializable {
   Iterator<Tuple2<K, V>> call(T var1) throws Exception;
}
