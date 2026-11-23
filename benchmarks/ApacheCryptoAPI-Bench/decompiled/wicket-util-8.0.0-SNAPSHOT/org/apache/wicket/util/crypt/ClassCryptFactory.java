package org.apache.wicket.util.crypt;

import java.lang.ref.WeakReference;
import org.apache.wicket.util.lang.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassCryptFactory implements ICryptFactory {
   private static final Logger log = LoggerFactory.getLogger(ClassCryptFactory.class);
   private final WeakReference<Class<?>> cryptClass;
   private final String encryptionKey;

   public ClassCryptFactory(Class<?> cryptClass, String encryptionKey) {
      Args.notNull(cryptClass, "cryptClass");
      if (!ICrypt.class.isAssignableFrom(cryptClass)) {
         throw new IllegalArgumentException("cryptClass must implement ICrypt interface");
      } else {
         this.cryptClass = new WeakReference(cryptClass);
         this.encryptionKey = encryptionKey;
      }
   }

   public ICrypt newCrypt() {
      try {
         ICrypt crypt = (ICrypt)((Class)this.cryptClass.get()).newInstance();
         log.info("using encryption/decryption object {}", crypt);
         crypt.setKey(this.encryptionKey);
         return crypt;
      } catch (Exception e) {
         log.warn("************************** WARNING **************************");
         log.warn("As the instantiation of encryption/decryption class:");
         log.warn("\t" + this.cryptClass);
         log.warn("failed, Wicket will fallback on a dummy implementation");
         log.warn("\t(" + NoCrypt.class.getName() + ")");
         log.warn("This is NOT recommended for production systems.");
         log.warn("Please override method org.apache.wicket.util.crypt.ICryptFactory.newCrypt()");
         log.warn("to provide a custom encryption/decryption implementation.");
         log.warn("The cause of the instantiation failure: ");
         log.warn("\t" + e.getMessage());
         if (log.isDebugEnabled()) {
            log.debug("exception: ", e);
         } else {
            log.warn("Set log level to DEBUG to display the stack trace.");
         }

         log.warn("*************************************************************");
         return new NoCrypt();
      }
   }
}
