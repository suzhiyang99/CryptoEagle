package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class InterceptorBindingInfo extends InfoObject {
   public String ejbName;
   public String className;
   public final List<String> interceptors = new ArrayList();
   public final List<String> interceptorOrder = new ArrayList();
   public boolean excludeDefaultInterceptors;
   public boolean excludeClassInterceptors;
   public NamedMethodInfo method;
}
