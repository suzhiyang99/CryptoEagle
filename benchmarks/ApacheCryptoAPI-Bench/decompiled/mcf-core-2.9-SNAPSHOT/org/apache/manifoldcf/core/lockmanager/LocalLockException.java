package org.apache.manifoldcf.core.lockmanager;

public class LocalLockException extends Exception {
   public static final String _rcsid = "@(#)$Id: LocalLockException.java 988245 2010-08-23 18:39:35Z kwright $";

   public LocalLockException(String reason) {
      super(reason);
   }
}
