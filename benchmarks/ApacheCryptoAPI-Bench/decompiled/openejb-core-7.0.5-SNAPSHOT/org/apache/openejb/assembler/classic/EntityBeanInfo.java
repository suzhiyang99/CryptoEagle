package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class EntityBeanInfo extends EnterpriseBeanInfo {
   public String abstractSchemaName;
   public String primKeyClass;
   public String primKeyField;
   public String persistenceType;
   public String reentrant;
   public final List<String> cmpFieldNames = new ArrayList();
   public final List<CmrFieldInfo> cmrFields = new ArrayList();
   public int cmpVersion;
   public final List<QueryInfo> queries = new ArrayList();

   public EntityBeanInfo() {
      this.type = 0;
   }
}
