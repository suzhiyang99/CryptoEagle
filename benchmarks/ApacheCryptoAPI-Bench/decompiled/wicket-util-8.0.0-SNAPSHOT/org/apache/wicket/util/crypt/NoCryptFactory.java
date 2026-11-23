package org.apache.wicket.util.crypt;

public class NoCryptFactory implements ICryptFactory {
   private static final ICrypt crypt = new NoCrypt();

   public ICrypt newCrypt() {
      return crypt;
   }
}
