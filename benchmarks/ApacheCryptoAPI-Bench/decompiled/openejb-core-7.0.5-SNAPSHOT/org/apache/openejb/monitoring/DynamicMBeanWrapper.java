package org.apache.openejb.monitoring;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.DynamicMBean;
import javax.management.ImmutableDescriptor;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.openejb.api.internal.Internal;
import org.apache.openejb.api.jmx.Description;
import org.apache.openejb.api.jmx.MBean;
import org.apache.openejb.api.jmx.ManagedAttribute;
import org.apache.openejb.api.jmx.ManagedOperation;
import org.apache.openejb.api.jmx.NotificationInfo;
import org.apache.openejb.api.jmx.NotificationInfos;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.config.WebBeansContext;

public class DynamicMBeanWrapper implements DynamicMBean, MBeanRegistration {
   public static final Logger logger;
   private static final Map<Class<?>, CacheInfo> CACHE;
   private static final Map<Class<?>, Class<? extends Annotation>> OPENEJB_API_TO_JAVAX;
   private final MBeanInfo info;
   private final Map<String, Method> getters;
   private final Map<String, Method> setters;
   private final Map<String, Method> operations;
   private final Object instance;
   private final ClassLoader classloader;

   public DynamicMBeanWrapper(Object givenInstance) {
      this((WebBeansContext)null, givenInstance);
   }

   public DynamicMBeanWrapper(WebBeansContext wc, Object givenInstance) {
      this.getters = new HashMap();
      this.setters = new HashMap();
      this.operations = new HashMap();
      Class<?> annotatedMBean = givenInstance.getClass();
      if (wc != null && givenInstance.getClass().getName().contains("$Owb")) {
         annotatedMBean = annotatedMBean.getSuperclass();
      }

      this.classloader = annotatedMBean.getClassLoader();
      this.instance = givenInstance;
      CacheInfo cache = (CacheInfo)CACHE.get(annotatedMBean);
      if (cache == null) {
         List<MBeanAttributeInfo> attributeInfos = new ArrayList();
         List<MBeanOperationInfo> operationInfos = new ArrayList();
         List<MBeanNotificationInfo> notificationInfos = new ArrayList();
         Description classDescription = (Description)this.findAnnotation(annotatedMBean, Description.class);
         String description = getDescription(classDescription, "a MBean built by OpenEJB");
         NotificationInfo notification = (NotificationInfo)this.findAnnotation(annotatedMBean, NotificationInfo.class);
         if (notification != null) {
            MBeanNotificationInfo notificationInfo = getNotificationInfo(notification);
            notificationInfos.add(notificationInfo);
         }

         NotificationInfos notifications = (NotificationInfos)this.findAnnotation(annotatedMBean, NotificationInfos.class);
         if (notifications != null && notifications.value() != null) {
            for(NotificationInfo n : notifications.value()) {
               MBeanNotificationInfo notificationInfo = getNotificationInfo(n);
               notificationInfos.add(notificationInfo);
            }
         }

         for(Method m : annotatedMBean.getMethods()) {
            int modifiers = m.getModifiers();
            if (!m.getDeclaringClass().equals(Object.class) && Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers)) {
               if (this.findAnnotation(m, ManagedAttribute.class) != null) {
                  String methodName = m.getName();
                  String attrName = methodName;
                  if ((methodName.startsWith("get") && m.getParameterTypes().length == 0 || methodName.startsWith("set") && m.getParameterTypes().length == 1) && methodName.length() > 3) {
                     attrName = methodName.substring(3);
                     if (attrName.length() > 1) {
                        attrName = Character.toLowerCase(attrName.charAt(0)) + attrName.substring(1);
                     } else {
                        attrName = attrName.toLowerCase();
                     }
                  } else {
                     logger.warning("ignoring attribute " + m.getName() + " for " + annotatedMBean.getName());
                  }

                  if (methodName.startsWith("get")) {
                     this.getters.put(attrName, m);
                  } else if (methodName.startsWith("set")) {
                     this.setters.put(attrName, m);
                  }
               } else if (this.findAnnotation(m, ManagedOperation.class) != null) {
                  this.operations.put(m.getName(), m);
                  String operationDescr = "";
                  Description descr = (Description)this.findAnnotation(m, Description.class);
                  if (descr != null) {
                     operationDescr = getDescription(descr, "-");
                  }

                  operationInfos.add(this.newMethodDescriptor(operationDescr, m));
               }
            }
         }

         for(Map.Entry<String, Method> e : this.getters.entrySet()) {
            String key = (String)e.getKey();
            Method mtd = (Method)e.getValue();
            String attrDescr = "";
            Description descr = (Description)this.findAnnotation(mtd, Description.class);
            if (descr != null) {
               attrDescr = getDescription(descr, "-");
            }

            try {
               attributeInfos.add(new MBeanAttributeInfo(key, attrDescr, mtd, (Method)this.setters.get(key)));
            } catch (IntrospectionException ex) {
               logger.warning("can't manage " + key + " for " + mtd.getName(), (Throwable)ex);
            }
         }

         for(Map.Entry<String, Method> e : this.setters.entrySet()) {
            String key = (String)e.getKey();
            if (this.getters.get(key) == null) {
               Method mtd = (Method)e.getValue();
               String attrDescr = "";
               Description descr = (Description)this.findAnnotation(mtd, Description.class);
               if (descr != null) {
                  attrDescr = getDescription(descr, "-");
               }

               try {
                  attributeInfos.add(new MBeanAttributeInfo(key, attrDescr, (Method)null, (Method)this.setters.get(key)));
               } catch (IntrospectionException ex) {
                  logger.warning("can't manage " + key + " for " + mtd.getName(), (Throwable)ex);
               }
            }
         }

         this.info = new MBeanInfo(annotatedMBean.getName(), description, (MBeanAttributeInfo[])attributeInfos.toArray(new MBeanAttributeInfo[attributeInfos.size()]), (MBeanConstructorInfo[])null, (MBeanOperationInfo[])operationInfos.toArray(new MBeanOperationInfo[operationInfos.size()]), (MBeanNotificationInfo[])notificationInfos.toArray(new MBeanNotificationInfo[notificationInfos.size()]));
         if (annotatedMBean.getAnnotation(Internal.class) != null) {
            CACHE.put(annotatedMBean, new CacheInfo(this.info, this.getters, this.setters, this.operations));
         }
      } else {
         this.info = cache.mBeanInfo;
         this.getters.putAll(cache.getters);
         this.setters.putAll(cache.setters);
         this.operations.putAll(cache.operations);
      }

   }

   private MBeanOperationInfo newMethodDescriptor(String operationDescr, Method m) {
      MBeanOperationInfo jvmInfo = new MBeanOperationInfo(operationDescr, m);
      return new MBeanOperationInfo(m.getName(), operationDescr, methodSignature(jvmInfo, m), m.getReturnType().getName(), 3, jvmInfo.getDescriptor());
   }

   private static MBeanParameterInfo[] methodSignature(MBeanOperationInfo jvmInfo, Method method) {
      Class<?>[] classes = method.getParameterTypes();
      Annotation[][] annots = method.getParameterAnnotations();
      return parameters(jvmInfo, classes, annots);
   }

   static MBeanParameterInfo[] parameters(MBeanOperationInfo jvmInfo, Class<?>[] classes, Annotation[][] annots) {
      MBeanParameterInfo[] params = new MBeanParameterInfo[classes.length];

      assert classes.length == annots.length;

      String desc = "";

      for(int i = 0; i < classes.length; ++i) {
         Descriptor d = jvmInfo.getSignature()[i].getDescriptor();
         String pn = "arg" + i;

         for(Annotation a : annots[i]) {
            Class<? extends Annotation> type = a.annotationType();
            if (type.equals(Description.class) || type.equals(OPENEJB_API_TO_JAVAX.get(Description.class))) {
               desc = getDescription((Description)annotationProxy(a, Description.class), desc);
               break;
            }
         }

         params[i] = new MBeanParameterInfo(pn, classes[i].getName(), desc, d);
      }

      return params;
   }

   private <T extends Annotation> T findAnnotation(Method method, Class<T> searchedAnnotation) {
      T annotation = method.getAnnotation(searchedAnnotation);
      if (annotation != null) {
         return annotation;
      } else {
         if (OPENEJB_API_TO_JAVAX.containsKey(searchedAnnotation)) {
            Class<? extends Annotation> clazz = (Class)OPENEJB_API_TO_JAVAX.get(searchedAnnotation);
            Object javaxAnnotation = method.getAnnotation(clazz);
            if (javaxAnnotation != null) {
               return (T)annotationProxy(javaxAnnotation, searchedAnnotation);
            }
         }

         return null;
      }
   }

   private <T extends Annotation> T findAnnotation(Class<?> annotatedMBean, Class<T> searchedAnnotation) {
      T annotation = annotatedMBean.getAnnotation(searchedAnnotation);
      if (annotation != null) {
         return annotation;
      } else {
         if (OPENEJB_API_TO_JAVAX.containsKey(searchedAnnotation)) {
            Class<? extends Annotation> clazz = (Class)OPENEJB_API_TO_JAVAX.get(searchedAnnotation);
            Object javaxAnnotation = annotatedMBean.getAnnotation(clazz);
            if (javaxAnnotation != null) {
               return (T)annotationProxy(javaxAnnotation, searchedAnnotation);
            }
         }

         return null;
      }
   }

   private static <T extends Annotation> T annotationProxy(Object javaxAnnotation, Class<T> clazz) {
      return (T)(Proxy.newProxyInstance(DynamicMBeanWrapper.class.getClassLoader(), new Class[]{clazz}, new AnnotationHandler(javaxAnnotation)));
   }

   private static MBeanNotificationInfo getNotificationInfo(NotificationInfo n) {
      String description = getDescription(n.description(), "-");
      return new MBeanNotificationInfo(n.types(), n.notificationClass().getName(), description, new ImmutableDescriptor(n.descriptorFields()));
   }

   private static String getDescription(Description d, String defaultValue) {
      if (d != null) {
         if (d.bundleBaseName() != null && d.key() != null) {
            try {
               return ResourceBundle.getBundle(d.bundleBaseName()).getString(d.key());
            } catch (RuntimeException var3) {
               return d.value();
            }
         } else {
            return d.value();
         }
      } else {
         return defaultValue;
      }
   }

   public MBeanInfo getMBeanInfo() {
      return this.info;
   }

   public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
      if (this.getters.containsKey(attribute)) {
         ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(this.classloader);

         Object var3;
         try {
            var3 = ((Method)this.getters.get(attribute)).invoke(this.instance);
         } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
            logger.error("can't get " + attribute + " value", (Throwable)e);
            throw new AttributeNotFoundException();
         } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
         }

         return var3;
      } else {
         throw new AttributeNotFoundException();
      }
   }

   public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      if (this.setters.containsKey(attribute.getName())) {
         ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(this.classloader);

         try {
            ((Method)this.setters.get(attribute.getName())).invoke(this.instance, attribute.getValue());
         } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
            logger.error("can't set " + attribute + " value", (Throwable)e);
         } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
         }

      } else {
         throw new AttributeNotFoundException();
      }
   }

   public AttributeList getAttributes(String[] attributes) {
      AttributeList list = new AttributeList();

      for(String n : attributes) {
         try {
            list.add(new Attribute(n, this.getAttribute(n)));
         } catch (Exception var8) {
         }
      }

      return list;
   }

   public AttributeList setAttributes(AttributeList attributes) {
      AttributeList list = new AttributeList();

      for(Object o : attributes) {
         Attribute attr = (Attribute)o;

         try {
            this.setAttribute(attr);
            list.add(attr);
         } catch (Exception var7) {
         }
      }

      return list;
   }

   public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
      if (this.operations.containsKey(actionName)) {
         ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(this.classloader);

         Object var5;
         try {
            var5 = ((Method)this.operations.get(actionName)).invoke(this.instance, params);
         } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
            logger.error(actionName + "can't be invoked", (Throwable)e);
            throw new MBeanException(new IllegalArgumentException(), actionName + " doesn't exist");
         } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
         }

         return var5;
      } else {
         throw new MBeanException(new IllegalArgumentException(), actionName + " doesn't exist");
      }
   }

   public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
      Thread thread = Thread.currentThread();
      ClassLoader oldCl = thread.getContextClassLoader();
      thread.setContextClassLoader(this.classloader);

      ObjectName var5;
      try {
         if (!MBeanRegistration.class.isInstance(this.instance)) {
            var5 = name;
            return var5;
         }

         var5 = ((MBeanRegistration)MBeanRegistration.class.cast(this.instance)).preRegister(server, name);
      } finally {
         thread.setContextClassLoader(oldCl);
      }

      return var5;
   }

   public void postRegister(Boolean registrationDone) {
      Thread thread = Thread.currentThread();
      ClassLoader oldCl = thread.getContextClassLoader();
      thread.setContextClassLoader(this.classloader);

      try {
         if (MBeanRegistration.class.isInstance(this.instance)) {
            ((MBeanRegistration)MBeanRegistration.class.cast(this.instance)).postRegister(registrationDone);
         }
      } finally {
         thread.setContextClassLoader(oldCl);
      }

   }

   public void preDeregister() throws Exception {
      Thread thread = Thread.currentThread();
      ClassLoader oldCl = thread.getContextClassLoader();
      thread.setContextClassLoader(this.classloader);

      try {
         if (MBeanRegistration.class.isInstance(this.instance)) {
            ((MBeanRegistration)MBeanRegistration.class.cast(this.instance)).preDeregister();
         }
      } finally {
         thread.setContextClassLoader(oldCl);
      }

   }

   public void postDeregister() {
      Thread thread = Thread.currentThread();
      ClassLoader oldCl = thread.getContextClassLoader();
      thread.setContextClassLoader(this.classloader);

      try {
         if (MBeanRegistration.class.isInstance(this.instance)) {
            ((MBeanRegistration)MBeanRegistration.class.cast(this.instance)).postDeregister();
         }
      } finally {
         thread.setContextClassLoader(oldCl);
      }

   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_DEPLOY, DynamicMBeanWrapper.class);
      CACHE = new HashMap();
      OPENEJB_API_TO_JAVAX = new HashMap();
      ClassLoader loader = DynamicMBeanWrapper.class.getClassLoader();

      try {
         OPENEJB_API_TO_JAVAX.put(MBean.class, loader.loadClass("javax.management.MBean"));
         OPENEJB_API_TO_JAVAX.put(Description.class, loader.loadClass("javax.management.Description"));
         OPENEJB_API_TO_JAVAX.put(ManagedOperation.class, loader.loadClass("javax.management.ManagedOperation"));
         OPENEJB_API_TO_JAVAX.put(ManagedAttribute.class, loader.loadClass("javax.management.ManagedAttribute"));
         OPENEJB_API_TO_JAVAX.put(NotificationInfo.class, loader.loadClass("javax.management.NotificationInfo"));
         OPENEJB_API_TO_JAVAX.put(NotificationInfos.class, loader.loadClass("javax.management.NotificationInfos"));
      } catch (NoClassDefFoundError | ClassNotFoundException var2) {
      }

   }

   private static class AnnotationHandler implements InvocationHandler {
      private final Object delegate;

      public AnnotationHandler(Object javaxAnnotation) {
         this.delegate = javaxAnnotation;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         Object result = null;

         for(Method mtd : this.delegate.getClass().getMethods()) {
            if (mtd.getName().equals(method.getName())) {
               result = mtd.invoke(this.delegate, args);
               break;
            }
         }

         if (result == null) {
            return null;
         } else {
            if (result.getClass().isArray()) {
               Object[] array = result;
               if (array.length == 0 || !DynamicMBeanWrapper.OPENEJB_API_TO_JAVAX.containsValue(array[0].getClass())) {
                  return array;
               }

               Object[] translated = new Object[array.length];

               for(int i = 0; i < translated.length; ++i) {
                  translated[i] = DynamicMBeanWrapper.annotationProxy(array[i], (Class)DynamicMBeanWrapper.OPENEJB_API_TO_JAVAX.get(array[i].getClass()));
               }
            }

            return result;
         }
      }
   }

   private static final class CacheInfo {
      public final MBeanInfo mBeanInfo;
      public final Map<String, Method> getters;
      public final Map<String, Method> setters;
      public final Map<String, Method> operations;

      private CacheInfo(MBeanInfo mBeanInfo, Map<String, Method> getters, Map<String, Method> setters, Map<String, Method> operations) {
         this.mBeanInfo = mBeanInfo;
         this.getters = getters;
         this.setters = setters;
         this.operations = operations;
      }
   }
}
