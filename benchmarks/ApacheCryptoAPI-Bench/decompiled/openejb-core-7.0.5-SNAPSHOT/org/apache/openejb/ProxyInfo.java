package org.apache.openejb;

import java.util.ArrayList;
import java.util.List;

public class ProxyInfo {
   protected BeanContext beanContext;
   protected Object primaryKey;
   protected List<Class> proxyInterfaces;
   protected RpcContainer beanContainer;
   protected InterfaceType interfaceType;
   protected Class mainInterface;

   protected ProxyInfo() {
   }

   public ProxyInfo(BeanContext beanContext, Object primaryKey, List<Class> interfaces, InterfaceType proxyType, Class mainInterface) {
      this.beanContext = beanContext;
      this.primaryKey = primaryKey;
      this.proxyInterfaces = interfaces;
      this.interfaceType = proxyType;
      this.beanContainer = (RpcContainer)beanContext.getContainer();
      this.mainInterface = mainInterface;
   }

   public ProxyInfo(BeanContext depInfo, Object pk) {
      this(depInfo, pk, new ArrayList(), InterfaceType.UNKNOWN, (Class)null);
   }

   public InterfaceType getInterfaceType() {
      return this.interfaceType;
   }

   public BeanContext getBeanContext() {
      return this.beanContext;
   }

   public Object getPrimaryKey() {
      return this.primaryKey;
   }

   public Class getInterface() {
      return this.mainInterface == null && this.proxyInterfaces != null && !this.proxyInterfaces.isEmpty() ? (Class)this.proxyInterfaces.get(0) : this.mainInterface;
   }

   public List<Class> getInterfaces() {
      return this.proxyInterfaces;
   }

   public RpcContainer getBeanContainer() {
      return this.beanContainer;
   }
}
