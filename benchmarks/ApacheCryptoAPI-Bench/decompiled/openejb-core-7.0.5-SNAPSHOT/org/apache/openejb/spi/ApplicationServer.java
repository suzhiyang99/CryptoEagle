package org.apache.openejb.spi;

import javax.ejb.EJBHome;
import javax.ejb.EJBMetaData;
import javax.ejb.EJBObject;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import org.apache.openejb.ProxyInfo;

public interface ApplicationServer {
   EJBMetaData getEJBMetaData(ProxyInfo var1);

   Handle getHandle(ProxyInfo var1);

   HomeHandle getHomeHandle(ProxyInfo var1);

   EJBObject getEJBObject(ProxyInfo var1);

   Object getBusinessObject(ProxyInfo var1);

   EJBHome getEJBHome(ProxyInfo var1);
}
