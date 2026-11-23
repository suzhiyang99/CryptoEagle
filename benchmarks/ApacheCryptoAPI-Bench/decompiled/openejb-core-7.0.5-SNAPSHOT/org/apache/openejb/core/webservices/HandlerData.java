package org.apache.openejb.core.webservices;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.namespace.QName;

public class HandlerData {
   private final Class<?> handlerClass;
   private final Properties initParams = new Properties();
   private final List<QName> soapHeaders = new ArrayList();
   private final List<String> soapRoles = new ArrayList();
   private final List<Method> postConstruct = new ArrayList();
   private final List<Method> preDestroy = new ArrayList();

   public HandlerData(Class<?> handlerClass) {
      if (handlerClass == null) {
         throw new NullPointerException("handlerClass is null");
      } else {
         this.handlerClass = handlerClass;
      }
   }

   public Properties getInitParams() {
      return this.initParams;
   }

   public List<QName> getSoapHeaders() {
      return this.soapHeaders;
   }

   public List<String> getSoapRoles() {
      return this.soapRoles;
   }

   public Class<?> getHandlerClass() {
      return this.handlerClass;
   }

   public List<Method> getPostConstruct() {
      return this.postConstruct;
   }

   public List<Method> getPreDestroy() {
      return this.preDestroy;
   }
}
