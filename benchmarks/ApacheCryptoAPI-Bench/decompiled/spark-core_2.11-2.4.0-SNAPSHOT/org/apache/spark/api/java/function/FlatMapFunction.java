package org.apache.spark.api.java.function;

import java.io.Serializable;
import java.util.Iterator;

@FunctionalInterface
public interface FlatMapFunction<T, R> extends Serializable {
   Iterator<R> call(T var1) throws Exception;
}
