package org.spark_project.jetty.server;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.MultiException;
import org.spark_project.jetty.util.MultiPartInputStreamParser;

public class MultiPartCleanerListener implements ServletRequestListener {
   public static final MultiPartCleanerListener INSTANCE = new MultiPartCleanerListener();

   protected MultiPartCleanerListener() {
   }

   public void requestDestroyed(ServletRequestEvent sre) {
      MultiPartInputStreamParser mpis = (MultiPartInputStreamParser)sre.getServletRequest().getAttribute("org.spark_project.jetty.multiPartInputStream");
      if (mpis != null) {
         ContextHandler.Context context = (ContextHandler.Context)sre.getServletRequest().getAttribute("org.spark_project.jetty.multiPartContext");
         if (context == sre.getServletContext()) {
            try {
               mpis.deleteParts();
            } catch (MultiException e) {
               sre.getServletContext().log("Errors deleting multipart tmp files", e);
            }
         }
      }

   }

   public void requestInitialized(ServletRequestEvent sre) {
   }
}
