package org.apache.openejb.core;

import javax.ejb.EJBHome;
import javax.ejb.EJBMetaData;
import javax.ejb.EJBObject;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.core.ivm.IntraVmServer;
import org.apache.openejb.spi.ApplicationServer;

public class ServerFederation implements ApplicationServer {
   private static final IntraVmServer localServer = new IntraVmServer();
   private static final ThreadLocal<ApplicationServer> applicationServer = new ThreadLocal();

   public Handle getHandle(ProxyInfo proxyInfo) {
      return getApplicationServer().getHandle(proxyInfo);
   }

   public EJBMetaData getEJBMetaData(ProxyInfo proxyInfo) {
      return getApplicationServer().getEJBMetaData(proxyInfo);
   }

   public HomeHandle getHomeHandle(ProxyInfo proxyInfo) {
      return getApplicationServer().getHomeHandle(proxyInfo);
   }

   public EJBObject getEJBObject(ProxyInfo proxyInfo) {
      return getApplicationServer().getEJBObject(proxyInfo);
   }

   public EJBHome getEJBHome(ProxyInfo proxyInfo) {
      return getApplicationServer().getEJBHome(proxyInfo);
   }

   public Object getBusinessObject(ProxyInfo proxyInfo) {
      return getApplicationServer().getBusinessObject(proxyInfo);
   }

   public static void setApplicationServer(ApplicationServer server) {
      if (server != null) {
         applicationServer.set(server);
      }

   }

   public static ApplicationServer getApplicationServer() {
      ApplicationServer server = (ApplicationServer)applicationServer.get();
      return (ApplicationServer)(server == null ? localServer : server);
   }
}
