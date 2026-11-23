package org.apache.openejb.config.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.loader.SystemInstance;

public class CheckIncorrectPropertyNames extends ValidationBase {
   Map incorrectAndCorrectPropNames = new HashMap();

   public CheckIncorrectPropertyNames() {
      this.incorrectAndCorrectPropNames.put("java.persistence.provider", "javax.persistence.provider");
      this.incorrectAndCorrectPropNames.put("java.persistence.transactionType", "javax.persistence.transactionType");
      this.incorrectAndCorrectPropNames.put("java.persistence.jtaDataSource", "javax.persistence.jtaDataSource");
      this.incorrectAndCorrectPropNames.put("java.persistence.nonJtaDataSource", "javax.persistence.nonJtaDataSource");
      this.incorrectAndCorrectPropNames.put("java.net.ssl.keyStore", "javax.net.ssl.keyStore");
      this.incorrectAndCorrectPropNames.put("java.net.ssl.keyStorePassword", "javax.net.ssl.keyStorePassword");
      this.incorrectAndCorrectPropNames.put("java.net.ssl.trustStore", "javax.net.ssl.trustStore");
      this.incorrectAndCorrectPropNames.put("java.security.jacc.PolicyConfigurationFactory.provider", "javax.security.jacc.PolicyConfigurationFactory.provider");
      this.incorrectAndCorrectPropNames.put("java.security.jacc.policy.provider", "javax.security.jacc.policy.provider");
      this.incorrectAndCorrectPropNames.put("java.xml.ws.spi.Provider", "javax.xml.ws.spi.Provider");
      this.incorrectAndCorrectPropNames.put("java.xml.soap.MessageFactory", "javax.xml.soap.MessageFactory");
      this.incorrectAndCorrectPropNames.put("java.xml.soap.SOAPFactory", "javax.xml.soap.SOAPFactory");
      this.incorrectAndCorrectPropNames.put("java.xml.soap.SOAPConnectionFactory", "javax.xml.soap.SOAPConnectionFactory");
      this.incorrectAndCorrectPropNames.put("java.xml.soap.MetaFactory", "javax.xml.soap.MetaFactory");
      this.incorrectAndCorrectPropNames.put("java.persistence.sharedCache.mode", "javax.persistence.sharedCache.mode");
      this.incorrectAndCorrectPropNames.put("java.persistence.validation.mode", "javax.persistence.validation.mode");
      this.incorrectAndCorrectPropNames.put("java.persistence.transactionType", "javax.persistence.transactionType");
      this.incorrectAndCorrectPropNames.put("javax.naming.applet", "java.naming.applet");
      this.incorrectAndCorrectPropNames.put("javax.naming.authoritative", "java.naming.authoritative");
      this.incorrectAndCorrectPropNames.put("javax.naming.batchsize", "java.naming.batchsize");
      this.incorrectAndCorrectPropNames.put("javax.naming.dns.url", "java.naming.dns.url");
      this.incorrectAndCorrectPropNames.put("javax.naming.factory.initial", "java.naming.factory.initial");
      this.incorrectAndCorrectPropNames.put("javax.naming.factory.object", "java.naming.factory.object");
      this.incorrectAndCorrectPropNames.put("javax.naming.factory.state", "java.naming.factory.state");
      this.incorrectAndCorrectPropNames.put("javax.naming.factory.url.pkgs", "java.naming.factory.url.pkgs");
      this.incorrectAndCorrectPropNames.put("javax.naming.language", "java.naming.language");
      this.incorrectAndCorrectPropNames.put("javax.naming.provider.url", "java.naming.provider.url");
      this.incorrectAndCorrectPropNames.put("javax.naming.referral", "java.naming.referral");
      this.incorrectAndCorrectPropNames.put("javax.naming.security.authentication", "java.naming.security.authentication");
      this.incorrectAndCorrectPropNames.put("javax.naming.security.credentials", "java.naming.security.credentials");
      this.incorrectAndCorrectPropNames.put("javax.naming.security.principal", "java.naming.security.principal");
      this.incorrectAndCorrectPropNames.put("javax.naming.security.protocol", "java.naming.security.protocol");
   }

   public void validate(AppModule appModule) {
      this.module = appModule;
      Properties systemProperties = SystemInstance.get().getProperties();

      for(Map.Entry<String, String> entry : this.incorrectAndCorrectPropNames.entrySet()) {
         if (systemProperties.containsKey(entry.getKey())) {
            this.warn(appModule.toString(), "incorrect.property.name", new Object[]{entry.getKey(), entry.getValue()});
         }
      }

   }
}
