package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface VoidFunction2<T1, T2> extends Serializable {
   void call(T1 var1, T2 var2) throws Exception;
}
