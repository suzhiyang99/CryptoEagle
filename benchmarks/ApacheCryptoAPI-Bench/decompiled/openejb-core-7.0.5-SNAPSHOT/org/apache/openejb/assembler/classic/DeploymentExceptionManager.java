package org.apache.openejb.assembler.classic;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.openejb.loader.SystemInstance;

public class DeploymentExceptionManager {
   private static final int MAX_SIZE = SystemInstance.get().getOptions().get("tomee.deployement-exception-max-size", 10);
   private final Map<AppInfo, Exception> deploymentException = new LinkedHashMap<AppInfo, Exception>() {
      protected boolean removeEldestEntry(Map.Entry<AppInfo, Exception> eldest) {
         return this.size() > DeploymentExceptionManager.MAX_SIZE;
      }
   };
   private Exception lastException;

   public synchronized boolean hasDeploymentFailed() {
      return this.lastException != null;
   }

   public synchronized Exception getDeploymentException(AppInfo appInfo) {
      return (Exception)this.deploymentException.get(appInfo);
   }

   public synchronized Exception saveDeploymentException(AppInfo appInfo, Exception exception) {
      this.lastException = exception;
      return (Exception)this.deploymentException.put(appInfo, exception);
   }

   public synchronized void clearLastException(AppInfo info) {
      if (info != null && this.deploymentException.get(info) == this.lastException) {
         this.deploymentException.remove(info);
      }

      this.lastException = null;
   }

   public Exception getLastException() {
      return this.lastException;
   }

   public void pushDelpoymentException(Exception exception) {
      this.lastException = exception;
   }
}
