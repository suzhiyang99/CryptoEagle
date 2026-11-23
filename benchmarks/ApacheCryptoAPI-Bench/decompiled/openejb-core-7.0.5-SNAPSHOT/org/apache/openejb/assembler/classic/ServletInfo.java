package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class ServletInfo extends InfoObject {
   public String servletName;
   public String servletClass;
   public String runAs;
   public List<String> mappings = new ArrayList();
   public List<ParamValueInfo> initParams = new ArrayList();
}
