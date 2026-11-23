package org.apache.openejb.mgmt;

import java.util.List;
import java.util.Set;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.RemoteHome;
import javax.ejb.Singleton;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.j2ee.ListenerRegistration;
import javax.management.j2ee.ManagementHome;

@Singleton(
   name = "MEJB"
)
@Lock(LockType.READ)
@RemoteHome(ManagementHome.class)
public class MEJBBean {
   MBeanServer mbeanServer;

   public MEJBBean() {
      List mbeanServers = MBeanServerFactory.findMBeanServer((String)null);
      if (mbeanServers.size() > 0) {
         this.mbeanServer = (MBeanServer)mbeanServers.get(0);
      } else {
         this.mbeanServer = MBeanServerFactory.createMBeanServer();
      }

   }

   public Object getAttribute(ObjectName objectName, String string) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
      return this.mbeanServer.getAttribute(objectName, string);
   }

   public AttributeList getAttributes(ObjectName objectName, String[] strings) throws InstanceNotFoundException, ReflectionException {
      return this.mbeanServer.getAttributes(objectName, strings);
   }

   public String getDefaultDomain() {
      return this.mbeanServer.getDefaultDomain();
   }

   public Integer getMBeanCount() {
      return this.mbeanServer.getMBeanCount();
   }

   public MBeanInfo getMBeanInfo(ObjectName objectName) throws IntrospectionException, InstanceNotFoundException, ReflectionException {
      return this.mbeanServer.getMBeanInfo(objectName);
   }

   public Object invoke(ObjectName objectName, String string, Object[] objects, String[] strings) throws InstanceNotFoundException, MBeanException, ReflectionException {
      return this.mbeanServer.invoke(objectName, string, objects, strings);
   }

   public boolean isRegistered(ObjectName objectName) {
      return this.mbeanServer.isRegistered(objectName);
   }

   public Set queryNames(ObjectName objectName, QueryExp queryExp) {
      return this.mbeanServer.queryNames(objectName, queryExp);
   }

   public void setAttribute(ObjectName objectName, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      this.mbeanServer.setAttribute(objectName, attribute);
   }

   public AttributeList setAttributes(ObjectName objectName, AttributeList attributeList) throws InstanceNotFoundException, ReflectionException {
      return this.mbeanServer.setAttributes(objectName, attributeList);
   }

   public ListenerRegistration getListenerRegistry() {
      return null;
   }
}
