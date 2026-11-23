package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface VoidFunction<T> extends Serializable {
   void call(T var1) throws Exception;
}
