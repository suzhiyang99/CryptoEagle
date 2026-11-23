package org.spark_project.jetty.util.component;

import java.util.concurrent.Future;

public interface Graceful {
   Future<Void> shutdown();
}
