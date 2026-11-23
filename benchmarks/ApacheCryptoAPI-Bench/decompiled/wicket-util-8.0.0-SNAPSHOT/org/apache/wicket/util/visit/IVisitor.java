package org.apache.wicket.util.visit;

@FunctionalInterface
public interface IVisitor<T, R> {
   void component(T var1, IVisit<R> var2);
}
