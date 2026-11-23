package org.apache.openejb.config.sys;

import javax.naming.InitialContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "JndiProvider"
)
public class JndiProvider extends AbstractService {
   public JndiProvider(String id) {
      super(id);
      this.type = InitialContext.class.getName();
   }

   public JndiProvider(String id, String type, String provider) {
      super(id, type, provider);
      this.type = InitialContext.class.getName();
   }

   public JndiProvider() {
      this.type = InitialContext.class.getName();
   }
}
