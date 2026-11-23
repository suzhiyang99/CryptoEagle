package org.apache.activemq.artemis.logs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;

public class AssertionLoggerHandler extends ExtHandler {
   private static final Map<String, ExtLogRecord> messages = new ConcurrentHashMap();
   private static boolean capture = false;

   public void flush() {
   }

   public void close() throws SecurityException {
   }

   protected void doPublish(ExtLogRecord record) {
      if (capture) {
         messages.put(record.getFormattedMessage(), record);
      }

   }

   public static boolean hasLevel(Level level) {
      for(ExtLogRecord record : messages.values()) {
         if (record.getLevel().equals(level)) {
            return true;
         }
      }

      return false;
   }

   public static boolean findText(long mstimeout, String... text) {
      long timeMax = System.currentTimeMillis() + mstimeout;

      while(!findText(text)) {
         if (timeMax <= System.currentTimeMillis()) {
            return false;
         }
      }

      return true;
   }

   public static boolean findText(String... text) {
      for(Map.Entry<String, ExtLogRecord> entry : messages.entrySet()) {
         String key = (String)entry.getKey();
         boolean found = true;

         for(String txtCheck : text) {
            found = key.contains(txtCheck);
            if (!found) {
               Throwable throwable = ((ExtLogRecord)entry.getValue()).getThrown();
               if (throwable == null || throwable.getMessage() == null) {
                  break;
               }

               found = throwable.getMessage().contains(txtCheck);
               if (!found) {
                  break;
               }
            }
         }

         if (found) {
            return true;
         }
      }

      return false;
   }

   public static final void clear() {
      messages.clear();
   }

   public static final void startCapture() {
      clear();
      capture = true;
   }

   public static final void stopCapture() {
      capture = false;
      clear();
   }
}
