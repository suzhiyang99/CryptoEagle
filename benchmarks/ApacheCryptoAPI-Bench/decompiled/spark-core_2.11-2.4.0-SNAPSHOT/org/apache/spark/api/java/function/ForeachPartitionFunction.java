package org.apache.spark.api.java.function;

import java.io.Serializable;
import java.util.Iterator;

@FunctionalInterface
public interface ForeachPartitionFunction<T> extends Serializable {
   void call(Iterator<T> var1) throws Exception;
}
