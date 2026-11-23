package org.apache.openejb.resource.activemq.jms2;

import javax.jms.BytesMessage;
import javax.jms.IllegalStateException;
import javax.jms.IllegalStateRuntimeException;
import javax.jms.InvalidClientIDException;
import javax.jms.InvalidClientIDRuntimeException;
import javax.jms.InvalidDestinationException;
import javax.jms.InvalidDestinationRuntimeException;
import javax.jms.InvalidSelectorException;
import javax.jms.InvalidSelectorRuntimeException;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.JMSSecurityException;
import javax.jms.JMSSecurityRuntimeException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageFormatRuntimeException;
import javax.jms.MessageNotWriteableException;
import javax.jms.MessageNotWriteableRuntimeException;
import javax.jms.ObjectMessage;
import javax.jms.ResourceAllocationException;
import javax.jms.ResourceAllocationRuntimeException;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.jms.TransactionInProgressException;
import javax.jms.TransactionInProgressRuntimeException;
import javax.jms.TransactionRolledBackException;
import javax.jms.TransactionRolledBackRuntimeException;

public final class JMS2 {
   private JMS2() {
   }

   public static JMSRuntimeException toRuntimeException(JMSException e) {
      if (e instanceof IllegalStateException) {
         return new IllegalStateRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } else if (e instanceof InvalidClientIDException) {
         return new InvalidClientIDRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } else if (e instanceof InvalidDestinationException) {
         return new InvalidDestinationRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } else if (e instanceof InvalidSelectorException) {
         return new InvalidSelectorRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } else if (e instanceof JMSSecurityException) {
         return new JMSSecurityRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } else if (e instanceof MessageFormatException) {
         return new MessageFormatRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } else if (e instanceof MessageNotWriteableException) {
         return new MessageNotWriteableRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } else if (e instanceof ResourceAllocationException) {
         return new ResourceAllocationRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } else if (e instanceof TransactionInProgressException) {
         return new TransactionInProgressRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } else {
         return (JMSRuntimeException)(e instanceof TransactionRolledBackException ? new TransactionRolledBackRuntimeException(e.getMessage(), e.getErrorCode(), e) : new JMSRuntimeException(e.getMessage(), e.getErrorCode(), e));
      }
   }

   public static <T extends Message> T wrap(T message10) {
      if (message10 == null) {
         return null;
      } else if (message10.getClass().getName().startsWith(JMS2.class.getPackage().getName())) {
         return message10;
      } else if (TextMessage.class.isInstance(message10)) {
         return (T)(new WrappingTextMessage((TextMessage)TextMessage.class.cast(message10)));
      } else if (ObjectMessage.class.isInstance(message10)) {
         return (T)(new WrappingObjectMessage((ObjectMessage)ObjectMessage.class.cast(message10)));
      } else if (MapMessage.class.isInstance(message10)) {
         return (T)(new WrappingMapMessage((MapMessage)MapMessage.class.cast(message10)));
      } else if (BytesMessage.class.isInstance(message10)) {
         return (T)(new WrappingByteMessage((BytesMessage)BytesMessage.class.cast(message10)));
      } else {
         return (T)(StreamMessage.class.isInstance(message10) ? new WrappingStreamMessage((StreamMessage)StreamMessage.class.cast(message10)) : new DelegateMessage(message10));
      }
   }
}
