package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface MapFunction<T, U> extends Serializable {
   U call(T var1) throws Exception;
}
