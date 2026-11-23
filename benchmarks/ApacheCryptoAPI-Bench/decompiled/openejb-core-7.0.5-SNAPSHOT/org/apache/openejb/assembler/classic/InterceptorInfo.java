package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class InterceptorInfo extends InfoObject {
   public String clazz;
   public final List<CallbackInfo> aroundInvoke = new ArrayList();
   public final List<CallbackInfo> postConstruct = new ArrayList();
   public final List<CallbackInfo> preDestroy = new ArrayList();
   public final List<CallbackInfo> postActivate = new ArrayList();
   public final List<CallbackInfo> prePassivate = new ArrayList();
   public final List<CallbackInfo> afterBegin = new ArrayList();
   public final List<CallbackInfo> beforeCompletion = new ArrayList();
   public final List<CallbackInfo> afterCompletion = new ArrayList();
   public final List<CallbackInfo> aroundTimeout = new ArrayList();
}
