package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class ManagedBeanInfo extends EnterpriseBeanInfo {
   public final List<CallbackInfo> postActivate = new ArrayList();
   public final List<CallbackInfo> prePassivate = new ArrayList();
   public final List<RemoveMethodInfo> removeMethods = new ArrayList();
   public boolean hidden;

   public ManagedBeanInfo() {
      this.type = 5;
   }
}
