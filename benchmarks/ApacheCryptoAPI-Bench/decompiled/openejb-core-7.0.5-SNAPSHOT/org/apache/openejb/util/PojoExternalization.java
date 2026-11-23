package org.apache.openejb.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

public class PojoExternalization extends PojoSerialization implements Externalizable {
   public PojoExternalization() {
   }

   public PojoExternalization(Object object) {
      super(object);
   }

   protected Object readResolve() throws ObjectStreamException {
      return super.readResolve();
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.read(in);
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      this.write(out);
   }
}
