package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface Function2<T1, T2, R> extends Serializable {
   R call(T1 var1, T2 var2) throws Exception;
}
