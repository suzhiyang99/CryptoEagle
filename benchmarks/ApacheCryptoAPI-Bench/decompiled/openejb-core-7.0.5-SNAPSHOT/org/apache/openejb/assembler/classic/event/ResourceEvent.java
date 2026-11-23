package org.apache.openejb.assembler.classic.event;

import javax.naming.NamingException;
import org.apache.openejb.assembler.classic.Assembler;

public abstract class ResourceEvent {
   protected final String name;
   protected final Object resource;
   protected Object replacement;

   protected ResourceEvent(String name, Object resource) {
      this.name = name;
      this.resource = resource;
   }

   public String getName() {
      return this.name;
   }

   public Object getRawResource() {
      return this.resource;
   }

   public Object getResource() {
      try {
         return !Assembler.ResourceInstance.class.isInstance(this.resource) ? this.resource : ((Assembler.ResourceInstance)Assembler.ResourceInstance.class.cast(this.resource)).getObject();
      } catch (NamingException e) {
         throw new IllegalStateException(e);
      }
   }

   public boolean is(Class<?> type) {
      try {
         return type.isInstance(this.resource) || Assembler.ResourceInstance.class.isInstance(this.resource) && type.isInstance(((Assembler.ResourceInstance)Assembler.ResourceInstance.class.cast(this.resource)).getObject());
      } catch (NamingException var3) {
         return false;
      }
   }

   public void replaceBy(Object newResource) {
      this.replacement = newResource;
   }

   public Object getReplacement() {
      return this.replacement;
   }

   public String toString() {
      return this.getClass().getSimpleName() + "{" + this.name + "}";
   }
}
