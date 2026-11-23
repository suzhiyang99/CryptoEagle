package org.apache.spark.api.java.function;

import java.io.Serializable;
import scala.Tuple2;

@FunctionalInterface
public interface PairFunction<T, K, V> extends Serializable {
   Tuple2<K, V> call(T var1) throws Exception;
}
