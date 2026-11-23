package org.apache.openejb.assembler.classic;

import java.util.HashMap;
import java.util.Map;

public class MessageDrivenBeanInfo extends EnterpriseBeanInfo {
   public String mdbInterface;
   public String destinationId;
   public final Map<String, String> activationProperties = new HashMap();

   public MessageDrivenBeanInfo() {
      this.type = 3;
   }
}
