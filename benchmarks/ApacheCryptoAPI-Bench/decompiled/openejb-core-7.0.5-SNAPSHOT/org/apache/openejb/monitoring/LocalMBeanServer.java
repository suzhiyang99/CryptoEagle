package org.apache.openejb.monitoring;

import java.io.ObjectInputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.Set;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public final class LocalMBeanServer implements MBeanServer {
   private static final Logger LOGGER;
   public static final String OPENEJB_JMX_ACTIVE = "openejb.jmx.active";
   private static final LocalMBeanServer INSTANCE;
   private static boolean active;

   private LocalMBeanServer() {
   }

   public static void reset() {
      active = SystemInstance.get().getOptions().get("openejb.jmx.active", true);
   }

   public static MBeanServer get() {
      return INSTANCE;
   }

   public static boolean isJMXActive() {
      return active;
   }

   public static ObjectInstance registerSilently(Object mbean, ObjectName name) {
      try {
         if (get().isRegistered(name)) {
            get().unregisterMBean(name);
         }

         return get().registerMBean(mbean, name);
      } catch (Exception var3) {
         LOGGER.error("Cannot register MBean " + name);
         return null;
      }
   }

   public static ObjectInstance registerDynamicWrapperSilently(Object object, ObjectName name) {
      return registerSilently(new DynamicMBeanWrapper(object), name);
   }

   public static void unregisterSilently(ObjectName name) {
      try {
         get().unregisterMBean(name);
      } catch (Exception var2) {
         LOGGER.error("Cannot unregister MBean " + name);
      }

   }

   public static TabularData tabularData(String typeName, String typeDescription, String[] names, Object[] values) {
      if (names.length == 0) {
         return null;
      } else {
         OpenType<?>[] types = new OpenType[names.length];

         for(int i = 0; i < types.length; ++i) {
            types[i] = SimpleType.STRING;
         }

         try {
            CompositeType ct = new CompositeType(typeName, typeDescription, names, names, types);
            TabularType type = new TabularType(typeName, typeDescription, ct, names);
            TabularDataSupport data = new TabularDataSupport(type);
            CompositeData line = new CompositeDataSupport(ct, names, values);
            data.put(line);
            return data;
         } catch (OpenDataException var9) {
            return null;
         }
      }
   }

   public static TabularData tabularData(String typeName, String typeDescription, String description, Properties properties) {
      String[] names = (String[])properties.keySet().toArray(new String[properties.size()]);
      Object[] values = new Object[names.length];

      for(int i = 0; i < values.length; ++i) {
         values[i] = properties.get(names[i]).toString();
      }

      return tabularData(typeName, typeDescription, names, values);
   }

   private static MBeanServer s() {
      return isJMXActive() ? ManagementFactory.getPlatformMBeanServer() : LocalMBeanServer.NoOpMBeanServer.INSTANCE;
   }

   public ObjectInstance createMBean(String className, ObjectName name) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      return s().createMBean(className, name);
   }

   public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      return s().createMBean(className, name, loaderName);
   }

   public ObjectInstance createMBean(String className, ObjectName name, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      return s().createMBean(className, name, params, signature);
   }

   public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      return s().createMBean(className, name, loaderName, params, signature);
   }

   public ObjectInstance registerMBean(Object object, ObjectName name) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      return s().registerMBean(object, name);
   }

   public void unregisterMBean(ObjectName name) throws InstanceNotFoundException, MBeanRegistrationException {
      s().unregisterMBean(name);
   }

   public ObjectInstance getObjectInstance(ObjectName name) throws InstanceNotFoundException {
      return s().getObjectInstance(name);
   }

   public Set<ObjectInstance> queryMBeans(ObjectName name, QueryExp query) {
      return s().queryMBeans(name, query);
   }

   public Set<ObjectName> queryNames(ObjectName name, QueryExp query) {
      return s().queryNames(name, query);
   }

   public boolean isRegistered(ObjectName name) {
      return s().isRegistered(name);
   }

   public Integer getMBeanCount() {
      return s().getMBeanCount();
   }

   public Object getAttribute(ObjectName name, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
      return s().getAttribute(name, attribute);
   }

   public AttributeList getAttributes(ObjectName name, String[] attributes) throws InstanceNotFoundException, ReflectionException {
      return s().getAttributes(name, attributes);
   }

   public void setAttribute(ObjectName name, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      s().setAttribute(name, attribute);
   }

   public AttributeList setAttributes(ObjectName name, AttributeList attributes) throws InstanceNotFoundException, ReflectionException {
      return s().setAttributes(name, attributes);
   }

   public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException {
      return s().invoke(name, operationName, params, signature);
   }

   public String getDefaultDomain() {
      return s().getDefaultDomain();
   }

   public String[] getDomains() {
      return s().getDomains();
   }

   public void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException {
      s().addNotificationListener(name, listener, filter, handback);
   }

   public void addNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException {
      s().addNotificationListener(name, listener, filter, handback);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener) throws InstanceNotFoundException, ListenerNotFoundException {
      s().removeNotificationListener(name, listener);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException {
      s().removeNotificationListener(name, listener, filter, handback);
   }

   public void removeNotificationListener(ObjectName name, NotificationListener listener) throws InstanceNotFoundException, ListenerNotFoundException {
      s().removeNotificationListener(name, listener);
   }

   public void removeNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException {
      s().removeNotificationListener(name, listener, filter, handback);
   }

   public MBeanInfo getMBeanInfo(ObjectName name) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
      return s().getMBeanInfo(name);
   }

   public boolean isInstanceOf(ObjectName name, String className) throws InstanceNotFoundException {
      return s().isInstanceOf(name, className);
   }

   public Object instantiate(String className) throws ReflectionException, MBeanException {
      return s().instantiate(className);
   }

   public Object instantiate(String className, ObjectName loaderName) throws ReflectionException, MBeanException, InstanceNotFoundException {
      return s().instantiate(className, loaderName);
   }

   public Object instantiate(String className, Object[] params, String[] signature) throws ReflectionException, MBeanException {
      return s().instantiate(className, params, signature);
   }

   public Object instantiate(String className, ObjectName loaderName, Object[] params, String[] signature) throws ReflectionException, MBeanException, InstanceNotFoundException {
      return s().instantiate(className, loaderName, params, signature);
   }

   public ObjectInputStream deserialize(ObjectName name, byte[] data) throws InstanceNotFoundException, OperationsException {
      return s().deserialize(name, data);
   }

   public ObjectInputStream deserialize(String className, byte[] data) throws OperationsException, ReflectionException {
      return s().deserialize(className, data);
   }

   public ObjectInputStream deserialize(String className, ObjectName loaderName, byte[] data) throws InstanceNotFoundException, OperationsException, ReflectionException {
      return s().deserialize(className, loaderName, data);
   }

   public ClassLoader getClassLoaderFor(ObjectName mbeanName) throws InstanceNotFoundException {
      return s().getClassLoaderFor(mbeanName);
   }

   public ClassLoader getClassLoader(ObjectName loaderName) throws InstanceNotFoundException {
      return s().getClassLoader(loaderName);
   }

   public ClassLoaderRepository getClassLoaderRepository() {
      return s().getClassLoaderRepository();
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, LocalMBeanServer.class);
      INSTANCE = new LocalMBeanServer();
      active = SystemInstance.get().getOptions().get("openejb.jmx.active", true);
   }

   private static class NoOpMBeanServer implements MBeanServer {
      public static final MBeanServer INSTANCE = new NoOpMBeanServer();
      private static final String[] DEFAULT_DOMAINS = new String[]{"default-domain"};

      public ObjectInstance createMBean(String className, ObjectName name) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
         return null;
      }

      public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
         return null;
      }

      public ObjectInstance createMBean(String className, ObjectName name, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
         return null;
      }

      public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
         return null;
      }

      public ObjectInstance registerMBean(Object object, ObjectName name) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
         return null;
      }

      public void unregisterMBean(ObjectName name) throws InstanceNotFoundException, MBeanRegistrationException {
      }

      public ObjectInstance getObjectInstance(ObjectName name) throws InstanceNotFoundException {
         return null;
      }

      public Set<ObjectInstance> queryMBeans(ObjectName name, QueryExp query) {
         return null;
      }

      public Set<ObjectName> queryNames(ObjectName name, QueryExp query) {
         return null;
      }

      public boolean isRegistered(ObjectName name) {
         return false;
      }

      public Integer getMBeanCount() {
         return 0;
      }

      public Object getAttribute(ObjectName name, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
         return null;
      }

      public AttributeList getAttributes(ObjectName name, String[] attributes) throws InstanceNotFoundException, ReflectionException {
         return new AttributeList();
      }

      public void setAttribute(ObjectName name, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      }

      public AttributeList setAttributes(ObjectName name, AttributeList attributes) throws InstanceNotFoundException, ReflectionException {
         return new AttributeList();
      }

      public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException {
         return null;
      }

      public String getDefaultDomain() {
         return DEFAULT_DOMAINS[0];
      }

      public String[] getDomains() {
         return DEFAULT_DOMAINS;
      }

      public void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException {
      }

      public void addNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException {
      }

      public void removeNotificationListener(ObjectName name, ObjectName listener) throws InstanceNotFoundException, ListenerNotFoundException {
      }

      public void removeNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException {
      }

      public void removeNotificationListener(ObjectName name, NotificationListener listener) throws InstanceNotFoundException, ListenerNotFoundException {
      }

      public void removeNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException {
      }

      public MBeanInfo getMBeanInfo(ObjectName name) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
         return null;
      }

      public boolean isInstanceOf(ObjectName name, String className) throws InstanceNotFoundException {
         return false;
      }

      public Object instantiate(String className) throws ReflectionException, MBeanException {
         return null;
      }

      public Object instantiate(String className, ObjectName loaderName) throws ReflectionException, MBeanException, InstanceNotFoundException {
         return null;
      }

      public Object instantiate(String className, Object[] params, String[] signature) throws ReflectionException, MBeanException {
         return null;
      }

      public Object instantiate(String className, ObjectName loaderName, Object[] params, String[] signature) throws ReflectionException, MBeanException, InstanceNotFoundException {
         return null;
      }

      public ObjectInputStream deserialize(ObjectName name, byte[] data) throws InstanceNotFoundException, OperationsException {
         return null;
      }

      public ObjectInputStream deserialize(String className, byte[] data) throws OperationsException, ReflectionException {
         return null;
      }

      public ObjectInputStream deserialize(String className, ObjectName loaderName, byte[] data) throws InstanceNotFoundException, OperationsException, ReflectionException {
         return null;
      }

      public ClassLoader getClassLoaderFor(ObjectName mbeanName) throws InstanceNotFoundException {
         return null;
      }

      public ClassLoader getClassLoader(ObjectName loaderName) throws InstanceNotFoundException {
         return null;
      }

      public ClassLoaderRepository getClassLoaderRepository() {
         return null;
      }
   }
}
