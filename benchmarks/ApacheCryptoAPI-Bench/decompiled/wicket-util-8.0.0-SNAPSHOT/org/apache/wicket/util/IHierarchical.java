package org.apache.wicket.util;

@FunctionalInterface
public interface IHierarchical<T> {
   T getParent();
}
