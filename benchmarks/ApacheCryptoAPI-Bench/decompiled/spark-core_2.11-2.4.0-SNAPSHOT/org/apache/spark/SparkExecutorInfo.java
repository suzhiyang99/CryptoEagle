package org.apache.spark;

import java.io.Serializable;

public interface SparkExecutorInfo extends Serializable {
   String host();

   int port();

   long cacheSize();

   int numRunningTasks();
}
