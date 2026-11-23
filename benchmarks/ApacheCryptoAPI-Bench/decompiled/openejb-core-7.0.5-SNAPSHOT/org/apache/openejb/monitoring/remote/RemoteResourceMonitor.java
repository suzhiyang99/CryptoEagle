package org.apache.openejb.monitoring.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.monitoring.ObjectNameBuilder;

public class RemoteResourceMonitor implements DynamicMBean {
   private static final String PING = "ping";
   private static final AttributeList ATTRIBUTE_LIST = new AttributeList();
   private static final MBeanAttributeInfo[] EMPTY_ATTRIBUTES = new MBeanAttributeInfo[0];
   private static final MBeanNotificationInfo[] EMPTY_NOTIFICATIONS = new MBeanNotificationInfo[0];
   private static final MBeanParameterInfo[] EMPTY_PARAMETERS = new MBeanParameterInfo[0];
   private static final MBeanOperationInfo PING_INFO = new MBeanOperationInfo("ping", "ping the parameter host", new MBeanParameterInfo[]{new MBeanParameterInfo("host", String.class.getName(), "the host to ping")}, String.class.getName(), 0);
   private final Collection<String> hosts = new CopyOnWriteArraySet();
   private ObjectName objectName;
   private MBeanInfo info;

   public synchronized void addHost(String host) {
      this.hosts.add(host);
      this.buildMBeanInfo();
   }

   public synchronized void removeHost(String host) {
      this.hosts.remove(host);
      this.buildMBeanInfo();
   }

   public void registerIfNot() {
      if (this.objectName == null) {
         ObjectNameBuilder jmxName = new ObjectNameBuilder("openejb.management");
         jmxName.set("ObjectType", "Related Hosts");
         this.objectName = jmxName.build();
         MBeanServer server = LocalMBeanServer.get();

         try {
            if (server.isRegistered(this.objectName)) {
               server.unregisterMBean(this.objectName);
            }

            server.registerMBean(this, this.objectName);
         } catch (Exception e) {
            throw new OpenEJBRuntimeException(e);
         }
      }
   }

   public void unregister() {
      try {
         LocalMBeanServer.get().unregisterMBean(this.objectName);
      } catch (Exception e) {
         throw new OpenEJBRuntimeException(e);
      }
   }

   public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
      if (this.hosts.contains(actionName)) {
         return ping(actionName);
      } else if ("ping".equals(actionName) && params != null && params.length == 1) {
         return ping((String)params[0]);
      } else {
         throw new MBeanException(new IllegalArgumentException(), actionName + " doesn't exist");
      }
   }

   public MBeanInfo getMBeanInfo() {
      if (this.info == null) {
         this.buildMBeanInfo();
      }

      return this.info;
   }

   public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
      throw new AttributeNotFoundException();
   }

   public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      throw new AttributeNotFoundException();
   }

   public AttributeList getAttributes(String[] attributes) {
      return ATTRIBUTE_LIST;
   }

   public AttributeList setAttributes(AttributeList attributes) {
      return ATTRIBUTE_LIST;
   }

   private void buildMBeanInfo() {
      List<MBeanOperationInfo> operationInfos = new ArrayList();

      for(String host : this.hosts) {
         operationInfos.add(new MBeanOperationInfo(host, "ping host " + host, EMPTY_PARAMETERS, String.class.getName(), 0));
      }

      operationInfos.add(PING_INFO);
      this.info = new MBeanInfo(RemoteResourceMonitor.class.getName(), "Monitor remote resources", EMPTY_ATTRIBUTES, (MBeanConstructorInfo[])null, (MBeanOperationInfo[])operationInfos.toArray(new MBeanOperationInfo[operationInfos.size()]), EMPTY_NOTIFICATIONS);
   }

   private static String ping(String host) {
      try {
         InetAddress address = InetAddress.getByName(host);
         boolean ok = address.isReachable(30000);
         if (ok) {
            long start = System.nanoTime();
            ok = address.isReachable(30000);
            long end = System.nanoTime();
            if (ok) {
               long duration = end - start;
               long ms = TimeUnit.NANOSECONDS.toMillis(duration);
               return "Ping done in " + ms + "." + Long.toString(duration - 1000L * ms) + " ms";
            }
         }

         return "Can't ping host, timeout (30s)";
      } catch (UnknownHostException e) {
         return "Can't find host: " + e.getMessage();
      } catch (IOException e) {
         return "Can't ping host: " + e.getMessage();
      }
   }
}
