package org.apache.activemq.artemis.utils;

import java.util.Arrays;

public class NoHashProcessor implements HashProcessor {
   public String hash(String plainText) throws Exception {
      return plainText;
   }

   public boolean compare(char[] inputValue, String storedHash) {
      return Arrays.equals(inputValue, storedHash.toCharArray());
   }
}
