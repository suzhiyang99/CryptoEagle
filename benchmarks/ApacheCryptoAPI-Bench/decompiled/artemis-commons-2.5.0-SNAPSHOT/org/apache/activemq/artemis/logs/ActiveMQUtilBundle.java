package org.apache.activemq.artemis.logs;

import org.apache.activemq.artemis.api.core.ActiveMQIllegalStateException;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Message.Format;

@MessageBundle(
   projectCode = "AMQ"
)
public interface ActiveMQUtilBundle {
   ActiveMQUtilBundle BUNDLE = (ActiveMQUtilBundle)Messages.getBundle(ActiveMQUtilBundle.class);

   @Message(
      id = 209000,
      value = "invalid property: {0}",
      format = Format.MESSAGE_FORMAT
   )
   ActiveMQIllegalStateException invalidProperty(String var1);

   @Message(
      id = 209001,
      value = "Invalid type: {0}",
      format = Format.MESSAGE_FORMAT
   )
   IllegalStateException invalidType(Byte var1);

   @Message(
      id = 209002,
      value = "the specified string is too long ({0})",
      format = Format.MESSAGE_FORMAT
   )
   IllegalStateException stringTooLong(Integer var1);

   @Message(
      id = 209003,
      value = "Error instantiating codec {0}",
      format = Format.MESSAGE_FORMAT
   )
   IllegalArgumentException errorCreatingCodec(@Cause Exception var1, String var2);

   @Message(
      id = 209004,
      value = "Failed to parse long value from {0}",
      format = Format.MESSAGE_FORMAT
   )
   IllegalArgumentException failedToParseLong(String var1);
}
