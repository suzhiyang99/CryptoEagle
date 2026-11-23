package org.apache.openejb.assembler.classic;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.transaction.TransactionManager;
import org.apache.openejb.Container;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.SafeToolkit;
import org.apache.openejb.util.proxy.ProxyFactory;

public class AssemblerTool {
   public static final Map<String, Class> serviceInterfaces = new HashMap();
   protected static final SafeToolkit toolkit;
   protected Properties props = new Properties();

   protected static void checkImplementation(Class intrfce, Class factory, String serviceType, String serviceName) throws OpenEJBException {
      if (!intrfce.isAssignableFrom(factory)) {
         throw new OpenEJBException((new Messages("org.apache.openejb.util.resources")).format("init.0100", serviceType, serviceName, factory.getName(), intrfce.getName()));
      }
   }

   static {
      serviceInterfaces.put("ProxyFactory", ProxyFactory.class);
      serviceInterfaces.put("SecurityService", SecurityService.class);
      serviceInterfaces.put("TransactionManager", TransactionManager.class);
      serviceInterfaces.put("ConnectionManager", ConnectionManager.class);
      serviceInterfaces.put("Connector", ManagedConnectionFactory.class);
      serviceInterfaces.put("Resource", ResourceAdapter.class);
      serviceInterfaces.put("Container", Container.class);
      toolkit = SafeToolkit.getToolkit("AssemblerTool");
      JavaSecurityManagers.setSystemProperty("noBanner", "true");
   }
}
