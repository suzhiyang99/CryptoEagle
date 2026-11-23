package org.apache.openejb.core;

import java.util.Map;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class MailSessionFactory {
   private final Properties properties = new Properties();
   private boolean useDefault;

   public Session create() {
      String password = this.properties.getProperty("password");
      Authenticator auth = null;
      if (password != null) {
         String protocol = this.properties.getProperty("mail.transport.protocol", "smtp");
         String user = this.properties.getProperty("mail." + protocol + ".user");
         if (user == null) {
            user = this.properties.getProperty("mail.user");
         }

         if (user != null) {
            final PasswordAuthentication pa = new PasswordAuthentication(user, password);
            auth = new Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return pa;
               }
            };
         }
      }

      if (this.useDefault) {
         return auth != null ? Session.getDefaultInstance(this.properties, auth) : Session.getDefaultInstance(this.properties);
      } else {
         return auth != null ? Session.getInstance(this.properties, auth) : Session.getInstance(this.properties);
      }
   }

   public Properties getProperties() {
      return this.properties;
   }

   public void setProperties(Properties properties) {
      this.properties.clear();

      for(Map.Entry<Object, Object> entry : properties.entrySet()) {
         if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
            this.properties.put(entry.getKey(), entry.getValue());
         }
      }

   }

   public void setUseDefault(boolean useDefault) {
      this.useDefault = useDefault;
   }
}
