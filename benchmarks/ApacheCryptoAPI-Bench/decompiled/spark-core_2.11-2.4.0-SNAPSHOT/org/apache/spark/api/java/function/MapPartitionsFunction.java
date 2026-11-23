package org.apache.spark.api.java.function;

import java.io.Serializable;
import java.util.Iterator;

@FunctionalInterface
public interface MapPartitionsFunction<T, U> extends Serializable {
   Iterator<U> call(Iterator<T> var1) throws Exception;
}
