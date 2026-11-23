package org.apache.spark.api.java;

import java.util.List;
import java.util.concurrent.Future;

public interface JavaFutureAction<T> extends Future<T> {
   List<Integer> jobIds();
}
