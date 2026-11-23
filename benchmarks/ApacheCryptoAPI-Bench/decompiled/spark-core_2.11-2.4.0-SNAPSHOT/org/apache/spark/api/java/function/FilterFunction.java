package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface FilterFunction<T> extends Serializable {
   boolean call(T var1) throws Exception;
}
