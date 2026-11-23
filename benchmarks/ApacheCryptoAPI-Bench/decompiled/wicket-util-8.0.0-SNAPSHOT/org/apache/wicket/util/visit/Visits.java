package org.apache.wicket.util.visit;

import java.util.Collections;
import java.util.Iterator;
import org.apache.wicket.util.lang.Args;

public class Visits {
   private Visits() {
   }

   public static <S, R> R visit(Iterable<? super S> container, IVisitor<S, R> visitor) {
      return (R)visitChildren(new SingletonIterable(container), visitor, IVisitFilter.ANY);
   }

   public static <S, R> R visit(Iterable<? super S> container, IVisitor<S, R> visitor, IVisitFilter filter) {
      return (R)visitChildren(new SingletonIterable(container), visitor, filter);
   }

   public static <S, R> R visitChildren(Iterable<? super S> container, IVisitor<S, R> visitor, IVisitFilter filter) {
      Visit<R> visit = new Visit<R>();
      visitChildren(container, visitor, filter, visit);
      return visit.getResult();
   }

   private static <S, R> void visitChildren(Iterable<? super S> container, IVisitor<S, R> visitor, IVisitFilter filter, Visit<R> visit) {
      Args.notNull(visitor, "visitor");

      for(Object child : container) {
         if (filter.visitObject(child)) {
            Visit<R> childTraversal = new Visit<R>();
            visitor.component(child, childTraversal);
            if (childTraversal.isStopped()) {
               visit.stop(childTraversal.getResult());
               return;
            }

            if (childTraversal.isDontGoDeeper()) {
               continue;
            }
         }

         if (!visit.isDontGoDeeper() && child instanceof Iterable && filter.visitChildren(child)) {
            visitChildren((Iterable)child, visitor, filter, visit);
            if (visit.isStopped()) {
               return;
            }
         }
      }

   }

   public static <S, R> R visitChildren(Iterable<? super S> container, IVisitor<S, R> visitor) {
      return (R)visitChildren(container, visitor, IVisitFilter.ANY);
   }

   public static <S, R> R visitPostOrder(S root, IVisitor<S, R> visitor) {
      return (R)visitPostOrder(root, visitor, IVisitFilter.ANY);
   }

   public static <S, R> R visitPostOrder(Object root, IVisitor<S, R> visitor, IVisitFilter filter) {
      Args.notNull(visitor, "visitor");
      Visit<R> visit = new Visit<R>();
      visitPostOrderHelper(root, visitor, filter, visit);
      return visit.getResult();
   }

   private static <S, R> void visitPostOrderHelper(Object component, IVisitor<S, R> visitor, IVisitFilter filter, Visit<R> visit) {
      if (component instanceof Iterable) {
         Iterable<?> container = (Iterable)component;
         if (filter.visitChildren(container)) {
            Visit<R> childTraversal = new Visit<R>();

            for(Object child : (Iterable)component) {
               visitPostOrderHelper(child, visitor, filter, childTraversal);
               if (childTraversal.isStopped()) {
                  visit.stop(childTraversal.getResult());
                  return;
               }
            }
         }
      }

      if (filter.visitObject(component)) {
         visitor.component(component, visit);
      }

   }

   private static class SingletonIterable<T> implements Iterable<T> {
      private final T singleton;

      public SingletonIterable(T singleton) {
         this.singleton = singleton;
      }

      public Iterator<T> iterator() {
         return Collections.singleton(this.singleton).iterator();
      }
   }
}
