package org.apache.wicket.util.crypt;

import java.security.GeneralSecurityException;

public class TrivialCrypt extends AbstractCrypt {
   protected byte[] crypt(byte[] input, int mode) throws GeneralSecurityException {
      byte[] result = new byte[input.length];

      for(int i = 0; i < input.length; ++i) {
         result[i] = (byte)(input[i] ^ 255);
      }

      return result;
   }
}
