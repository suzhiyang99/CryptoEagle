package org.apache.wicket.util.visit;

import org.apache.wicket.util.lang.Args;

public class ClassVisitFilter implements IVisitFilter {
   private final Class<?> clazz;

   public ClassVisitFilter(Class<?> clazz) {
      Args.notNull(clazz, "clazz");
      this.clazz = clazz;
   }

   public boolean visitChildren(Object object) {
      return true;
   }

   public boolean visitObject(Object object) {
      return this.clazz.isAssignableFrom(object.getClass());
   }
}
