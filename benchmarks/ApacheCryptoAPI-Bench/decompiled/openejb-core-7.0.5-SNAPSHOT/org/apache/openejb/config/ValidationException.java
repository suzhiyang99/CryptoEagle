package org.apache.openejb.config;

import org.apache.openejb.util.Messages;

public class ValidationException extends Exception {
   protected static final Messages messages = new Messages("org.apache.openejb.config.rules");
   protected Object[] details;
   protected String message;
   protected String prefix;
   protected String componentName;

   public ValidationException(String message) {
      this.message = message;
   }

   public void setDetails(Object... details) {
      this.details = details;
      if (details == null) {
         details = new Object[0];
      }

   }

   public String getMessageKey() {
      return this.message;
   }

   public Object[] getDetails() {
      return this.details;
   }

   public String getSummary() {
      return this.getMessage(1);
   }

   public String getMessage() {
      return this.getMessage(2);
   }

   public String getMessage(int level) {
      return messages.format(level + "." + this.message, this.details);
   }

   public String getComponentName() {
      return this.componentName;
   }

   public void setComponentName(String componentName) {
      this.componentName = componentName;
   }

   public String getPrefix() {
      return "";
   }

   public String getCategory() {
      return "";
   }
}
