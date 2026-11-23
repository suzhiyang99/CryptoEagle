package org.apache.spark.api.java.function;

import java.io.Serializable;
import java.util.Iterator;

@FunctionalInterface
public interface CoGroupFunction<K, V1, V2, R> extends Serializable {
   Iterator<R> call(K var1, Iterator<V1> var2, Iterator<V2> var3) throws Exception;
}
