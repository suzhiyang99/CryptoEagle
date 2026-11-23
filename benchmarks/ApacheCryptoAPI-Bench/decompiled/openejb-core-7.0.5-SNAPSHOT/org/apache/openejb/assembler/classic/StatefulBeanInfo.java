package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class StatefulBeanInfo extends EnterpriseBeanInfo {
   public final List<CallbackInfo> postActivate = new ArrayList();
   public final List<CallbackInfo> prePassivate = new ArrayList();
   public final List<InitMethodInfo> initMethods = new ArrayList();
   public final List<RemoveMethodInfo> removeMethods = new ArrayList();
   public final List<CallbackInfo> afterBegin = new ArrayList();
   public final List<CallbackInfo> beforeCompletion = new ArrayList();
   public final List<CallbackInfo> afterCompletion = new ArrayList();

   public StatefulBeanInfo() {
      this.type = 1;
   }
}
