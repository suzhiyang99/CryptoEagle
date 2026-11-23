package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface ReduceFunction<T> extends Serializable {
   T call(T var1, T var2) throws Exception;
}
