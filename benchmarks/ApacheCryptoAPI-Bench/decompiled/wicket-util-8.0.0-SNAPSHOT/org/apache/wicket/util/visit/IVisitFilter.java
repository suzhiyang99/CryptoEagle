package org.apache.wicket.util.visit;

public interface IVisitFilter {
   IVisitFilter ANY = new IVisitFilter() {
      public boolean visitObject(Object object) {
         return true;
      }

      public boolean visitChildren(Object object) {
         return true;
      }
   };

   boolean visitObject(Object var1);

   boolean visitChildren(Object var1);
}
