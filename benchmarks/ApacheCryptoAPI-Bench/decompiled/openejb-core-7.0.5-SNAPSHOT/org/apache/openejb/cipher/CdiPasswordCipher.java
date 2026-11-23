package org.apache.openejb.cipher;

import java.lang.annotation.Annotation;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.BeanManagerImpl;

public final class CdiPasswordCipher implements PasswordCipher {
   public char[] encrypt(String plainPassword) {
      throw new UnsupportedOperationException("cdi password cipher only supports decryption");
   }

   public String decrypt(char[] encryptedPassword) {
      String string = new String(encryptedPassword);

      BeanManagerImpl mgr;
      try {
         WebBeansContext wbc = WebBeansContext.currentInstance();
         mgr = wbc.getBeanManagerImpl();
         if (!mgr.isInUse()) {
            return "cipher:cdi:" + string;
         }
      } catch (IllegalStateException var18) {
         return "cipher:cdi:" + string;
      }

      int split = string.indexOf(58);
      String delegate = string.substring(0, split);
      String pwdStr = string.substring(split + 1, string.length());
      char[] pwd = pwdStr.toCharArray();

      try {
         Class<?> beanType = Thread.currentThread().getContextClassLoader().loadClass(delegate);
         Bean<?> bean = mgr.resolve(mgr.getBeans(beanType, new Annotation[0]));
         if (bean == null) {
            throw new IllegalArgumentException("No bean for " + delegate);
         } else {
            CreationalContext<?> cc = mgr.createCreationalContext((Contextual)null);

            String var11;
            try {
               var11 = ((PasswordCipher)PasswordCipher.class.cast(mgr.getReference(bean, PasswordCipher.class, cc))).decrypt(pwd);
            } finally {
               if (!mgr.isNormalScope(bean.getScope())) {
                  cc.release();
               }

            }

            return var11;
         }
      } catch (ClassNotFoundException e) {
         throw new IllegalArgumentException("Can't find " + delegate, e);
      }
   }
}
