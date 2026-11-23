package org.spark_project.jetty.plus.annotation;

import java.util.HashMap;
import org.spark_project.jetty.servlet.ServletHolder;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class RunAsCollection {
   private static final Logger LOG = Log.getLogger(RunAsCollection.class);
   public static final String RUNAS_COLLECTION = "org.spark_project.jetty.runAsCollection";
   private HashMap<String, RunAs> _runAsMap = new HashMap();

   public void add(RunAs runAs) {
      if (runAs != null && runAs.getTargetClassName() != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Adding run-as for class=" + runAs.getTargetClassName());
         }

         this._runAsMap.put(runAs.getTargetClassName(), runAs);
      }
   }

   public RunAs getRunAs(Object o) {
      return o == null ? null : (RunAs)this._runAsMap.get(o.getClass().getCanonicalName());
   }

   public void setRunAs(Object o) {
      if (o != null) {
         if (ServletHolder.class.isAssignableFrom(o.getClass())) {
            RunAs runAs = (RunAs)this._runAsMap.get(o.getClass().getName());
            if (runAs != null) {
               runAs.setRunAs((ServletHolder)o);
            }
         }
      }
   }
}
