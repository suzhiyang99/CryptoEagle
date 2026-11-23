package org.apache.wicket.util.listener;

@FunctionalInterface
public interface IChangeListener<T> {
   void onChange(T var1);
}
