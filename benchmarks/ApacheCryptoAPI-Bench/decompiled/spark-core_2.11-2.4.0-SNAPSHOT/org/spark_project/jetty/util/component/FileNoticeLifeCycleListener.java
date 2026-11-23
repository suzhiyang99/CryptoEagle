package org.spark_project.jetty.util.component;

import java.io.FileWriter;
import java.io.Writer;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class FileNoticeLifeCycleListener implements LifeCycle.Listener {
   private static final Logger LOG = Log.getLogger(FileNoticeLifeCycleListener.class);
   private final String _filename;

   public FileNoticeLifeCycleListener(String filename) {
      this._filename = filename;
   }

   private void writeState(String action, LifeCycle lifecycle) {
      try {
         Writer out = new FileWriter(this._filename, true);
         Throwable var4 = null;

         try {
            out.append(action).append(" ").append(lifecycle.toString()).append("\n");
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (out != null) {
               if (var4 != null) {
                  try {
                     out.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  out.close();
               }
            }

         }
      } catch (Exception e) {
         LOG.warn(e);
      }

   }

   public void lifeCycleStarting(LifeCycle event) {
      this.writeState("STARTING", event);
   }

   public void lifeCycleStarted(LifeCycle event) {
      this.writeState("STARTED", event);
   }

   public void lifeCycleFailure(LifeCycle event, Throwable cause) {
      this.writeState("FAILED", event);
   }

   public void lifeCycleStopping(LifeCycle event) {
      this.writeState("STOPPING", event);
   }

   public void lifeCycleStopped(LifeCycle event) {
      this.writeState("STOPPED", event);
   }
}
