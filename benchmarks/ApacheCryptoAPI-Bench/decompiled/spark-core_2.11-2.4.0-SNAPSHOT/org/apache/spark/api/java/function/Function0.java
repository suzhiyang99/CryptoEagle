package org.apache.spark.api.java.function;

import java.io.Serializable;

@FunctionalInterface
public interface Function0<R> extends Serializable {
   R call() throws Exception;
}
