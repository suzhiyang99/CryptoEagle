package org.apache.wicket.util;

import java.util.function.Supplier;

public class NullProvider<T> implements Supplier<T> {
   public T get() {
      return null;
   }
}
