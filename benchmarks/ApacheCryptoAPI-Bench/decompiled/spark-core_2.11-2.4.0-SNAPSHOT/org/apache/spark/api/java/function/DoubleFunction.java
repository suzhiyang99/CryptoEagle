package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface DoubleFunction<T> extends Serializable {
   double call(T var1) throws Exception;
}
