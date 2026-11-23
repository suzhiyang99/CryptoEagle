package org.apache.openejb.assembler.classic;

import java.util.List;
import java.util.Properties;

public class FilterInfo extends InfoObject {
   public String classname;
   public List<String> mappings;
   public Properties initParams = new Properties();
   public String name;
}
