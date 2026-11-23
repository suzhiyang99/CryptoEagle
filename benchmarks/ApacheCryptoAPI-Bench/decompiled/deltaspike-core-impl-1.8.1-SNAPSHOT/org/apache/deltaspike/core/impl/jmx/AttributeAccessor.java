package org.apache.deltaspike.core.impl.jmx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AttributeAccessor {
   private final Method getter;
   private final Method setter;
   private final boolean presentAsTabularIfPossible;

   public AttributeAccessor(Method get, Method set, boolean presentAsTabularIfPossible) {
      this.setter = set;
      this.getter = get;
      this.presentAsTabularIfPossible = presentAsTabularIfPossible;
   }

   public boolean isPresentAsTabularIfPossible() {
      return this.presentAsTabularIfPossible;
   }

   public Object get(Object instance) throws InvocationTargetException, IllegalAccessException {
      if (this.getter == null) {
         throw new IllegalAccessException("This attribute has no getter");
      } else {
         return this.getter.invoke(instance);
      }
   }

   public void set(Object instance, Object value) throws InvocationTargetException, IllegalAccessException {
      if (this.setter == null) {
         throw new IllegalAccessException("This attribute has no setter");
      } else {
         this.setter.invoke(instance, value);
      }
   }
}
