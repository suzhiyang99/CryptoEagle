package org.spark_project.jetty.continuation;

import java.util.EventListener;

public interface ContinuationListener extends EventListener {
   void onComplete(Continuation var1);

   void onTimeout(Continuation var1);
}
