package org.apache.openejb.spi;

import java.util.Properties;
import javax.transaction.TransactionManager;
import org.apache.openejb.OpenEJBException;

public interface Assembler {
   void init(Properties var1) throws OpenEJBException;

   void build() throws OpenEJBException;

   ContainerSystem getContainerSystem();

   TransactionManager getTransactionManager();

   SecurityService getSecurityService();

   void destroy();
}
