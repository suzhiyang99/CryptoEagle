package org.apache.wicket.util.crypt;

public class CachingSunJceCryptFactory extends CryptFactoryCachingDecorator {
   public CachingSunJceCryptFactory(String encryptionKey) {
      super(new ClassCryptFactory(SunJceCrypt.class, encryptionKey));
   }
}
