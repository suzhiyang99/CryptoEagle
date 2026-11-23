package org.spark_project.jetty.client;

import java.util.EventListener;
import org.spark_project.jetty.client.api.ContentProvider;

public interface AsyncContentProvider extends ContentProvider {
   void setListener(Listener var1);

   public interface Listener extends EventListener {
      void onContent();
   }
}
