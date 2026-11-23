package org.apache.openejb.config;

import java.util.Map;

public interface PersistenceContextAnn {
   String name();

   String synchronization();

   String unitName();

   String type();

   Map<String, String> properties();
}
