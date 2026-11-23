package org.apache.manifoldcf.core.interfaces;

public class LockException extends Exception {
   public static final String _rcsid = "@(#)$Id: LockException.java 988245 2010-08-23 18:39:35Z kwright $";

   public LockException(String reason) {
      super(reason);
   }
}
