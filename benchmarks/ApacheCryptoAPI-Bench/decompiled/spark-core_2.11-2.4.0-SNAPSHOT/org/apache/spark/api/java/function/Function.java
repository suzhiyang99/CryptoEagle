package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface Function<T1, R> extends Serializable {
   R call(T1 var1) throws Exception;
}
