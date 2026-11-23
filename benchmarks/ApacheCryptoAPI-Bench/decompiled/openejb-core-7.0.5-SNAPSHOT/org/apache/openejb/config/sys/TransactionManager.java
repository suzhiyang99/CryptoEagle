package org.apache.openejb.config.sys;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = ""
)
@XmlRootElement(
   name = "TransactionManager"
)
public class TransactionManager extends AbstractService {
   public TransactionManager(String id) {
      super(id);
   }

   public TransactionManager(String id, String type, String provider) {
      super(id, type, provider);
   }

   public TransactionManager() {
   }
}
