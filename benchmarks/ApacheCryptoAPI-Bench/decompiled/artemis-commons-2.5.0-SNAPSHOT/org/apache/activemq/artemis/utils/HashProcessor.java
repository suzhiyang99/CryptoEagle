package org.apache.activemq.artemis.utils;

public interface HashProcessor {
   String hash(String var1) throws Exception;

   boolean compare(char[] var1, String var2);
}
