package org.apache.openejb.resource.jdbc.dbcp;

import java.io.ObjectStreamException;
import java.io.Serializable;
import javax.naming.NamingException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class DataSourceSerialization implements Serializable {
   private final String name;

   public DataSourceSerialization(String name) {
      this.name = name;
   }

   Object readResolve() throws ObjectStreamException {
      try {
         return ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup("openejb:Resource/" + this.name);
      } catch (NamingException e) {
         throw new IllegalStateException(e);
      }
   }
}
