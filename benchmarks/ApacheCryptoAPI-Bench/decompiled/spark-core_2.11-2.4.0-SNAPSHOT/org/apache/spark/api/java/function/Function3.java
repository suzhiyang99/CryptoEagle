package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface Function3<T1, T2, T3, R> extends Serializable {
   R call(T1 var1, T2 var2, T3 var3) throws Exception;
}
