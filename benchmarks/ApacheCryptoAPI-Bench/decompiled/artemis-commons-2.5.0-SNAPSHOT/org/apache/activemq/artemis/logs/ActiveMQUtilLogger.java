package org.apache.activemq.artemis.logs;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.Message.Format;

@MessageLogger(
   projectCode = "AMQ"
)
public interface ActiveMQUtilLogger extends BasicLogger {
   ActiveMQUtilLogger LOGGER = (ActiveMQUtilLogger)Logger.getMessageLogger(ActiveMQUtilLogger.class, ActiveMQUtilLogger.class.getPackage().getName());

   @LogMessage(
      level = Level.INFO
   )
   @Message(
      id = 201000,
      value = "Network is healthy, starting service {0}",
      format = Format.MESSAGE_FORMAT
   )
   void startingService(String var1);

   @LogMessage(
      level = Level.INFO
   )
   @Message(
      id = 201001,
      value = "Network is unhealthy, stopping service {0}",
      format = Format.MESSAGE_FORMAT
   )
   void stoppingService(String var1);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202000,
      value = "Missing privileges to set Thread Context Class Loader on Thread Factory. Using current Thread Context Class Loader",
      format = Format.MESSAGE_FORMAT
   )
   void missingPrivsForClassloader();

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202001,
      value = "{0} is a loopback address and will be discarded.",
      format = Format.MESSAGE_FORMAT
   )
   void addressloopback(String var1);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202002,
      value = "Ping Address {0} wasn't reacheable.",
      format = Format.MESSAGE_FORMAT
   )
   void addressWasntReacheable(String var1);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202003,
      value = "Ping Url {0} wasn't reacheable.",
      format = Format.MESSAGE_FORMAT
   )
   void urlWasntReacheable(String var1);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202004,
      value = "Error starting component {0} ",
      format = Format.MESSAGE_FORMAT
   )
   void errorStartingComponent(@Cause Exception var1, String var2);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202005,
      value = "Error stopping component {0} ",
      format = Format.MESSAGE_FORMAT
   )
   void errorStoppingComponent(@Cause Exception var1, String var2);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202006,
      value = "Failed to check Url {0}.",
      format = Format.MESSAGE_FORMAT
   )
   void failedToCheckURL(@Cause Exception var1, String var2);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202007,
      value = "Failed to check Address {0}.",
      format = Format.MESSAGE_FORMAT
   )
   void failedToCheckAddress(@Cause Exception var1, String var2);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202008,
      value = "Failed to check Address list {0}.",
      format = Format.MESSAGE_FORMAT
   )
   void failedToParseAddressList(@Cause Exception var1, String var2);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202009,
      value = "Failed to check Url list {0}.",
      format = Format.MESSAGE_FORMAT
   )
   void failedToParseUrlList(@Cause Exception var1, String var2);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202010,
      value = "Failed to set NIC {0}.",
      format = Format.MESSAGE_FORMAT
   )
   void failedToSetNIC(@Cause Exception var1, String var2);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202011,
      value = "Failed to read from stream {0}.",
      format = Format.MESSAGE_FORMAT
   )
   void failedToReadFromStream(String var1);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202012,
      value = "Object cannot be serialized.",
      format = Format.MESSAGE_FORMAT
   )
   void failedToSerializeObject(@Cause Exception var1);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202013,
      value = "Unable to deserialize object.",
      format = Format.MESSAGE_FORMAT
   )
   void failedToDeserializeObject(@Cause Exception var1);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202014,
      value = "Unable to encode byte array into Base64 notation.",
      format = Format.MESSAGE_FORMAT
   )
   void failedToEncodeByteArrayToBase64Notation(@Cause Exception var1);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202015,
      value = "Failed to clean up file {0}",
      format = Format.MESSAGE_FORMAT
   )
   void failedToCleanupFile(String var1);

   @LogMessage(
      level = Level.WARN
   )
   @Message(
      id = 202016,
      value = "Could not list files to clean up in {0}",
      format = Format.MESSAGE_FORMAT
   )
   void failedListFilesToCleanup(String var1);
}
