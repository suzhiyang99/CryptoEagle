package org.apache.openejb.assembler.classic;

import java.util.Properties;
import org.apache.openejb.OpenEJBException;

public interface OpenEjbConfigurationFactory {
   void init(Properties var1) throws OpenEJBException;

   OpenEjbConfiguration getOpenEjbConfiguration() throws OpenEJBException;
}
