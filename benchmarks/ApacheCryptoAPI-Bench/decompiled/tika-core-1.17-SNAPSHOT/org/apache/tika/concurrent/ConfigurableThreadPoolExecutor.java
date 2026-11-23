package org.apache.tika.concurrent;

import java.util.concurrent.ExecutorService;

public interface ConfigurableThreadPoolExecutor extends ExecutorService {
   void setMaximumPoolSize(int var1);

   void setCorePoolSize(int var1);
}
