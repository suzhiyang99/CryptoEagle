package org.apache.meecrowave.logging.log4j2;

import org.apache.logging.log4j.LogManager;

public class Log4j2Shutdown {
   public void shutodwn() {
      try {
         LogManager.shutdown();
      } catch (Exception e) {
         System.out.println("A problem happened when shutting down Log4j: " + e + "\n" + e.getMessage());
      }

   }
}
