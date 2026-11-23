package org.apache.openejb.spi;

import java.util.List;
import javax.naming.Context;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.core.WebContext;

public interface ContainerSystem {
   BeanContext getBeanContext(Object var1);

   BeanContext[] deployments();

   Container getContainer(Object var1);

   Container[] containers();

   WebContext getWebContextByHost(String var1, String var2);

   /** @deprecated */
   @Deprecated
   WebContext getWebContext(String var1);

   Context getJNDIContext();

   List<AppContext> getAppContexts();

   AppContext getAppContext(Object var1);
}
