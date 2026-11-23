package org.apache.wicket.util.crypt;

import org.apache.wicket.util.lang.Args;

public class CryptFactoryCachingDecorator implements ICryptFactory {
   private final ICryptFactory delegate;
   private ICrypt cache;

   public CryptFactoryCachingDecorator(ICryptFactory delegate) {
      Args.notNull(delegate, "delegate");
      this.delegate = delegate;
   }

   public final ICrypt newCrypt() {
      if (this.cache == null) {
         this.cache = this.delegate.newCrypt();
      }

      return this.cache;
   }
}
