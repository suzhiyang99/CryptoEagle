package org.apache.openejb.core.cmp;

public class CmpUtil {
   public static String getCmpImplClassName(String abstractSchemaName, String ejbClass) {
      int packageEnd = ejbClass.lastIndexOf(46);
      return packageEnd != -1 ? "openejb." + ejbClass.substring(0, packageEnd + 1) + abstractSchemaName : "openejb." + abstractSchemaName;
   }
}
