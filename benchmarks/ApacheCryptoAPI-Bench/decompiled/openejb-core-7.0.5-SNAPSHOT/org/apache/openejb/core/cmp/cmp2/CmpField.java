package org.apache.openejb.core.cmp.cmp2;

import java.lang.reflect.Method;
import org.apache.xbean.asm5.Type;

public class CmpField {
   private final String name;
   private final Type type;
   private final Method getter;

   public CmpField(String name, Type type, Method getter) {
      this.name = name;
      this.type = type;
      this.getter = getter;
   }

   public String getName() {
      return this.name;
   }

   public Type getType() {
      return this.type;
   }

   public String getDescriptor() {
      return this.type.getDescriptor();
   }

   public Method getGetterMethod() {
      return this.getter;
   }
}
