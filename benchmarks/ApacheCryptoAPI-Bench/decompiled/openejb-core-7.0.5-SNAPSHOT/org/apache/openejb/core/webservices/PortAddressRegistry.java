package org.apache.openejb.core.webservices;

import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.openejb.OpenEJBException;

public interface PortAddressRegistry {
   void addPort(String var1, QName var2, String var3, QName var4, String var5, String var6) throws OpenEJBException;

   void removePort(String var1, QName var2, String var3, String var4);

   Set<PortAddress> getPorts(String var1, QName var2, String var3);
}
