package org.apache.spark.api.java.function;

import java.io.Serializable;
import java.util.Iterator;

@FunctionalInterface
public interface FlatMapGroupsFunction<K, V, R> extends Serializable {
   Iterator<R> call(K var1, Iterator<V> var2) throws Exception;
}
