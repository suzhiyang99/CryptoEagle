package org.apache.activemq.artemis.utils.uri;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Locale;
import org.apache.activemq.artemis.api.core.Pair;
import org.apache.activemq.artemis.utils.collections.ConcurrentHashSet;
import org.apache.commons.beanutils.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils.IntrospectionContext;
import org.jboss.logging.Logger;

public class FluentPropertyBeanIntrospectorWithIgnores extends FluentPropertyBeanIntrospector {
   static Logger logger = Logger.getLogger(FluentPropertyBeanIntrospectorWithIgnores.class);
   private static ConcurrentHashSet<Pair<String, String>> ignores = new ConcurrentHashSet<Pair<String, String>>();

   public static void addIgnore(String className, String propertyName) {
      logger.trace("Adding ignore on " + className + "/" + propertyName);
      ignores.add(new Pair(className, propertyName));
   }

   public static boolean isIgnored(String className, String propertyName) {
      return ignores.contains(new Pair(className, propertyName));
   }

   public void introspect(IntrospectionContext icontext) throws IntrospectionException {
      for(Method m : icontext.getTargetClass().getMethods()) {
         if (m.getName().startsWith(this.getWriteMethodPrefix())) {
            String propertyName = this.propertyName(m);
            PropertyDescriptor pd = icontext.getPropertyDescriptor(propertyName);
            if (isIgnored(icontext.getTargetClass().getName(), m.getName())) {
               logger.trace(m.getName() + " Ignored for " + icontext.getTargetClass().getName());
            } else {
               try {
                  if (pd == null) {
                     icontext.addPropertyDescriptor(this.createFluentPropertyDescritor(m, propertyName));
                  } else if (pd.getWriteMethod() == null) {
                     pd.setWriteMethod(m);
                  }
               } catch (IntrospectionException e) {
                  logger.debug(e.getMessage(), e);
               }
            }
         }
      }

   }

   private PropertyDescriptor createFluentPropertyDescritor(Method m, String propertyName) throws IntrospectionException {
      return new PropertyDescriptor(this.propertyName(m), (Method)null, m);
   }

   private String propertyName(Method m) {
      String methodName = m.getName().substring(this.getWriteMethodPrefix().length());
      return methodName.length() > 1 ? Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1) : methodName.toLowerCase(Locale.ENGLISH);
   }
}
