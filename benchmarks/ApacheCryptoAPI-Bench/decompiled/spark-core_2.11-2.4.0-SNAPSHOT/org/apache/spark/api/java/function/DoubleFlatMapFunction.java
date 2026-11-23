package org.apache.spark.api.java.function;

import java.io.Serializable;
import java.util.Iterator;

@FunctionalInterface
public interface DoubleFlatMapFunction<T> extends Serializable {
   Iterator<Double> call(T var1) throws Exception;
}
