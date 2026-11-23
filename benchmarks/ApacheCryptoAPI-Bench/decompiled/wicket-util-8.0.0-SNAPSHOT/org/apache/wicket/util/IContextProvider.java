package org.apache.wicket.util;

import java.util.function.Function;

/** @deprecated */
@Deprecated
public interface IContextProvider<T, C> extends Function<C, T> {
   T get(C var1);

   default T apply(C context) {
      return (T)this.get(context);
   }
}
