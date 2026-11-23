package org.apache.activemq.artemis.logs;

import java.io.Serializable;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import org.apache.activemq.artemis.api.core.ActiveMQIllegalStateException;

public class ActiveMQUtilBundle_$bundle implements ActiveMQUtilBundle, Serializable {
   private static final long serialVersionUID = 1L;
   public static final ActiveMQUtilBundle_$bundle INSTANCE = new ActiveMQUtilBundle_$bundle();
   private static final Locale LOCALE;
   private static final String invalidProperty = "AMQ209000: invalid property: {0}";
   private static final String invalidType = "AMQ209001: Invalid type: {0}";
   private static final String stringTooLong = "AMQ209002: the specified string is too long ({0})";
   private static final String errorCreatingCodec = "AMQ209003: Error instantiating codec {0}";
   private static final String failedToParseLong = "AMQ209004: Failed to parse long value from {0}";

   protected ActiveMQUtilBundle_$bundle() {
   }

   protected Object readResolve() {
      return INSTANCE;
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   protected String invalidProperty$str() {
      return "AMQ209000: invalid property: {0}";
   }

   public final ActiveMQIllegalStateException invalidProperty(String part) {
      ActiveMQIllegalStateException result = new ActiveMQIllegalStateException(this._formatMessage(this.invalidProperty$str(), part));
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   private String _formatMessage(String format, Object... args) {
      MessageFormat formatter = new MessageFormat(format, this.getLoggingLocale());
      return formatter.format(args, new StringBuffer(), new FieldPosition(0)).toString();
   }

   protected String invalidType$str() {
      return "AMQ209001: Invalid type: {0}";
   }

   public final IllegalStateException invalidType(Byte type) {
      IllegalStateException result = new IllegalStateException(this._formatMessage(this.invalidType$str(), type));
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String stringTooLong$str() {
      return "AMQ209002: the specified string is too long ({0})";
   }

   public final IllegalStateException stringTooLong(Integer length) {
      IllegalStateException result = new IllegalStateException(this._formatMessage(this.stringTooLong$str(), length));
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String errorCreatingCodec$str() {
      return "AMQ209003: Error instantiating codec {0}";
   }

   public final IllegalArgumentException errorCreatingCodec(Exception e, String codecClassName) {
      IllegalArgumentException result = new IllegalArgumentException(this._formatMessage(this.errorCreatingCodec$str(), codecClassName), e);
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   protected String failedToParseLong$str() {
      return "AMQ209004: Failed to parse long value from {0}";
   }

   public final IllegalArgumentException failedToParseLong(String value) {
      IllegalArgumentException result = new IllegalArgumentException(this._formatMessage(this.failedToParseLong$str(), value));
      StackTraceElement[] st = result.getStackTrace();
      result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
      return result;
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
