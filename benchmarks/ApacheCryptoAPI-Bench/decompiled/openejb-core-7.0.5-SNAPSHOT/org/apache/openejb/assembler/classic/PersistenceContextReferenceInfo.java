package org.apache.openejb.assembler.classic;

import java.util.Properties;

public class PersistenceContextReferenceInfo extends InjectableInfo {
   public String persistenceUnitName;
   public String unitId;
   public boolean extended;
   public String synchronizationType;
   public final Properties properties = new Properties();
}
