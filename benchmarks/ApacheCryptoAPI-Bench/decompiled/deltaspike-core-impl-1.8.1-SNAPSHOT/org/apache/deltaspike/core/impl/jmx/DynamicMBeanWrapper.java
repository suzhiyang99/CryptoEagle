package org.apache.deltaspike.core.impl.jmx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.ImmutableDescriptor;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.api.jmx.JmxBroadcaster;
import org.apache.deltaspike.core.api.jmx.JmxManaged;
import org.apache.deltaspike.core.api.jmx.JmxParameter;
import org.apache.deltaspike.core.api.jmx.MBean;
import org.apache.deltaspike.core.api.jmx.NotificationInfo;
import org.apache.deltaspike.core.api.jmx.Table;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.ParameterUtil;

public class DynamicMBeanWrapper extends NotificationBroadcasterSupport implements DynamicMBean, JmxBroadcaster {
   public static final Logger LOGGER = Logger.getLogger(DynamicMBeanWrapper.class.getName());
   private final MBeanInfo info;
   private final Map<String, AttributeAccessor> fields = new HashMap();
   private final Map<String, Operation> operations = new HashMap();
   private final ClassLoader classloader;
   private final Class<?> clazz;
   private final boolean normalScope;
   private final Annotation[] qualifiers;
   private Object instance = null;

   public DynamicMBeanWrapper(Class<?> annotatedMBean, boolean normalScope, Annotation[] qualifiers) {
      this.clazz = annotatedMBean;
      this.classloader = Thread.currentThread().getContextClassLoader();
      this.normalScope = normalScope;
      this.qualifiers = qualifiers;
      List<MBeanAttributeInfo> attributeInfos = new ArrayList();
      List<MBeanOperationInfo> operationInfos = new ArrayList();
      List<MBeanNotificationInfo> notificationInfos = new ArrayList();
      String description = this.getDescription(((MBean)annotatedMBean.getAnnotation(MBean.class)).description(), annotatedMBean.getName());
      NotificationInfo notification = (NotificationInfo)annotatedMBean.getAnnotation(NotificationInfo.class);
      if (notification != null) {
         notificationInfos.add(this.getNotificationInfo(notification, annotatedMBean.getName()));
      }

      NotificationInfo.List notifications = (NotificationInfo.List)annotatedMBean.getAnnotation(NotificationInfo.List.class);
      if (notifications != null) {
         for(NotificationInfo notificationInfo : notifications.value()) {
            notificationInfos.add(this.getNotificationInfo(notificationInfo, annotatedMBean.getName()));
         }
      }

      for(Method method : annotatedMBean.getMethods()) {
         int modifiers = method.getModifiers();
         JmxManaged annotation = (JmxManaged)method.getAnnotation(JmxManaged.class);
         if (!method.getDeclaringClass().equals(Object.class) && Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers) && annotation != null) {
            this.operations.put(method.getName(), new Operation(method, annotation.convertToTabularData()));
            String operationDescr = this.getDescription(annotation.description(), method.getName());
            Annotation[][] parametersAnnotations = method.getParameterAnnotations();
            Class<?>[] parameterTypes = method.getParameterTypes();
            MBeanParameterInfo[] parameterInfos = new MBeanParameterInfo[parameterTypes.length];

            for(int i = 0; i < parametersAnnotations.length; ++i) {
               String parameterDescription = null;
               String parameterName = "p" + (i + 1);
               String java8ParameterName = ParameterUtil.getName(method, i);
               if (java8ParameterName != null) {
                  parameterName = java8ParameterName;
               }

               for(int j = 0; j < parametersAnnotations[i].length; ++j) {
                  if (parametersAnnotations[i][j] instanceof JmxParameter) {
                     JmxParameter jmxParameter = (JmxParameter)parametersAnnotations[i][j];
                     if (!"".equals(jmxParameter.name())) {
                        parameterName = jmxParameter.name();
                     }

                     if (!"".equals(jmxParameter.description())) {
                        parameterDescription = jmxParameter.description();
                     }
                  }
               }

               parameterInfos[i] = new MBeanParameterInfo(parameterName, parameterTypes[i].getName(), parameterDescription);
            }

            operationInfos.add(new MBeanOperationInfo(method.getName(), operationDescr, parameterInfos, method.getReturnType().getName(), 3));
         }
      }

      for(Class<?> clazz = annotatedMBean; !Object.class.equals(clazz) && clazz != null; clazz = clazz.getSuperclass()) {
         for(Field field : clazz.getDeclaredFields()) {
            JmxManaged annotation = (JmxManaged)field.getAnnotation(JmxManaged.class);
            if (annotation != null) {
               field.setAccessible(true);
               String fieldName = field.getName();
               String fieldDescription = this.getDescription(annotation.description(), fieldName);
               Class<?> type = field.getType();
               String methodName;
               if (fieldName.length() > 1) {
                  methodName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
               } else {
                  methodName = Character.toString(Character.toUpperCase(fieldName.charAt(0)));
               }

               Method setter = null;
               Method getter = null;

               try {
                  getter = clazz.getMethod("get" + methodName);
               } catch (NoSuchMethodException var28) {
                  try {
                     getter = clazz.getMethod("is" + methodName);
                  } catch (NoSuchMethodException var27) {
                  }
               }

               try {
                  setter = clazz.getMethod("set" + methodName, field.getType());
               } catch (NoSuchMethodException var26) {
               }

               attributeInfos.add(new MBeanAttributeInfo(fieldName, this.toMBeanType(type).getName(), fieldDescription, getter != null, setter != null, false));
               this.fields.put(fieldName, new AttributeAccessor(getter, setter, annotation.convertToTabularData()));
            }
         }
      }

      Collections.sort(attributeInfos, DynamicMBeanWrapper.MBeanFeatureInfoSorter.INSTANCE);
      Collections.sort(operationInfos, DynamicMBeanWrapper.MBeanFeatureInfoSorter.INSTANCE);
      Collections.sort(notificationInfos, DynamicMBeanWrapper.MBeanFeatureInfoSorter.INSTANCE);
      this.info = new MBeanInfo(annotatedMBean.getName(), description, (MBeanAttributeInfo[])attributeInfos.toArray(new MBeanAttributeInfo[attributeInfos.size()]), (MBeanConstructorInfo[])null, (MBeanOperationInfo[])operationInfos.toArray(new MBeanOperationInfo[operationInfos.size()]), (MBeanNotificationInfo[])notificationInfos.toArray(new MBeanNotificationInfo[notificationInfos.size()]));
   }

   private Class<?> toMBeanType(Class<?> type) {
      return !Map.class.isAssignableFrom(type) && !Table.class.isAssignableFrom(type) ? type : TabularData.class;
   }

   private MBeanNotificationInfo getNotificationInfo(NotificationInfo notificationInfo, String sourceInfo) {
      return new MBeanNotificationInfo(notificationInfo.types(), notificationInfo.notificationClass().getName(), this.getDescription(notificationInfo.description(), sourceInfo), new ImmutableDescriptor(notificationInfo.descriptorFields()));
   }

   private String getDescription(String description, String defaultDescription) {
      if (description.isEmpty()) {
         return defaultDescription;
      } else {
         String descriptionValue = description.trim();
         return descriptionValue.startsWith("{") && descriptionValue.endsWith("}") ? ConfigResolver.getPropertyValue(descriptionValue.substring(1, descriptionValue.length() - 1), defaultDescription) : description;
      }
   }

   public MBeanInfo getMBeanInfo() {
      return this.info;
   }

   public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
      if (this.fields.containsKey(attribute)) {
         ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(this.classloader);

         Object var5;
         try {
            AttributeAccessor attributeAccessor = (AttributeAccessor)this.fields.get(attribute);
            Object value = attributeAccessor.get(this.instance());
            var5 = attributeAccessor.isPresentAsTabularIfPossible() ? this.toResult(attribute, value) : value;
         } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "can't get " + attribute + " value", e);
            throw new AttributeNotFoundException();
         } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "can't get " + attribute + " value", e);
            throw new AttributeNotFoundException();
         } catch (InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "can't get " + attribute + " value", e);
            throw new AttributeNotFoundException();
         } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
         }

         return var5;
      } else {
         throw new AttributeNotFoundException();
      }
   }

   private Object toResult(String attribute, Object value) throws InvocationTargetException, IllegalAccessException {
      if (Map.class.isInstance(value)) {
         Map map = (Map)Map.class.cast(value);
         return this.toTabularData(attribute, attribute, (new Table()).withColumns(map.keySet()).withLine(map.values()));
      } else {
         return Table.class.isInstance(value) ? this.toTabularData(attribute, attribute, (Table)Table.class.cast(value)) : value;
      }
   }

   private TabularData toTabularData(String typeName, String description, Table table) {
      OpenType<?>[] types = new OpenType[table.getColumnNames().size()];

      for(int i = 0; i < types.length; ++i) {
         types[i] = SimpleType.STRING;
      }

      try {
         String[] keys = (String[])table.getColumnNames().toArray(new String[table.getColumnNames().size()]);
         CompositeType ct = new CompositeType(typeName, description, keys, keys, types);
         TabularType type = new TabularType(typeName, description, ct, keys);
         TabularDataSupport data = new TabularDataSupport(type);

         for(Collection<String> line : table.getLines()) {
            data.put(new CompositeDataSupport(ct, keys, line.toArray(new Object[line.size()])));
         }

         return data;
      } catch (OpenDataException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      if (this.fields.containsKey(attribute.getName())) {
         ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(this.classloader);

         try {
            ((AttributeAccessor)this.fields.get(attribute.getName())).set(this.instance(), attribute.getValue());
         } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "can't set " + attribute + " value", e);
         } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "can't set " + attribute + " value", e);
         } catch (InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "can't set " + attribute + " value", e);
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

         Object var7;
         try {
            Operation operation = (Operation)this.operations.get(actionName);
            Object result = operation.getOperation().invoke(this.instance(), params);
            var7 = operation.isPresentAsTabularIfPossible() ? this.toResult(actionName, result) : result;
         } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Error) {
               throw (Error)cause;
            }

            if (cause instanceof MBeanException) {
               throw (MBeanException)cause;
            }

            throw new MBeanException((Exception)cause, actionName + " failed with exception");
         } catch (IllegalAccessException e) {
            throw new ReflectionException(e, actionName + " could not be invoked");
         } catch (IllegalArgumentException e) {
            throw new ReflectionException(e, actionName + " could not be invoked");
         } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
         }

         return var7;
      } else {
         throw new ReflectionException(new NoSuchMethodException(actionName + " doesn't exist"));
      }
   }

   private synchronized Object instance() {
      ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(this.classloader);

      Object bm;
      try {
         if (this.instance == null) {
            if (this.normalScope) {
               this.instance = BeanProvider.getContextualReference(this.clazz, this.qualifiers);
            } else {
               BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
               Set<Bean<?>> beans = bm.getBeans(this.clazz, this.qualifiers);
               if (beans == null || beans.isEmpty()) {
                  throw new IllegalStateException("Could not find beans for Type=" + this.clazz + " and qualifiers:" + Arrays.toString(this.qualifiers));
               }

               Bean<?> resolvedBean = bm.resolve(beans);
               CreationalContext<?> creationalContext = bm.createCreationalContext(resolvedBean);
               this.instance = bm.getReference(resolvedBean, this.clazz, creationalContext);
               creationalContext.release();
            }

            bm = this.instance;
            return bm;
         }

         bm = this.instance;
      } finally {
         Thread.currentThread().setContextClassLoader(oldCl);
      }

      return bm;
   }

   public void send(Notification notification) {
      this.sendNotification(notification);
   }

   private static class MBeanFeatureInfoSorter<T extends MBeanFeatureInfo> implements Comparator<T> {
      private static final Comparator<? super MBeanFeatureInfo> INSTANCE = new MBeanFeatureInfoSorter<MBeanFeatureInfo>();

      public int compare(T o1, T o2) {
         return o1.getName().compareTo(o2.getName());
      }
   }
}
