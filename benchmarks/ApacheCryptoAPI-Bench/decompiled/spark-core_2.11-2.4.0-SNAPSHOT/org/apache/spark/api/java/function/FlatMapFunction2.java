package org.apache.spark.api.java.function;

import java.io.Serializable;
import java.util.Iterator;

@FunctionalInterface
public interface FlatMapFunction2<T1, T2, R> extends Serializable {
   Iterator<R> call(T1 var1, T2 var2) throws Exception;
}
