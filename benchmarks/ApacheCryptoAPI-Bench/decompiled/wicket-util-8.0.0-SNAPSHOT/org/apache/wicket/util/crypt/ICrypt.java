package org.apache.wicket.util.crypt;

public interface ICrypt {
   String decryptUrlSafe(String var1);

   String encryptUrlSafe(String var1);

   void setKey(String var1);
}
