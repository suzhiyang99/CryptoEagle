package org.apache.taverna.ui.perspectives.myexperiment.model;

import org.apache.log4j.Logger;
import org.apache.taverna.workbench.ShutdownSPI;

public class MyExperimentClientShutdownHook implements ShutdownSPI {
   private MyExperimentClient myExperimentClient;
   private Logger logger = Logger.getLogger(MyExperimentClientShutdownHook.class);

   public int positionHint() {
      return 100;
   }

   public boolean shutdown() {
      if (this.myExperimentClient == null) {
         return true;
      } else {
         this.logger.debug("Starting shutdown operations for myExperiment plugin");

         try {
            this.myExperimentClient.storeHistoryAndSettings();
         } catch (Exception e) {
            this.logger.error("Failed while serializing myExperiment plugin settings:\n" + e);
         }

         this.logger.debug("myExperiment plugin shutdown is completed; terminated...");
         return true;
      }
   }

   public void setMyExperimentClient(MyExperimentClient myExperimentClient) {
      this.myExperimentClient = myExperimentClient;
   }

   public MyExperimentClient getMyExperimentClient() {
      return this.myExperimentClient;
   }
}
