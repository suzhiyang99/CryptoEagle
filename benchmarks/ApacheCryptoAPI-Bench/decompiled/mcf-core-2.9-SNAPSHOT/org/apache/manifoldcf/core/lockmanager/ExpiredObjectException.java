package org.apache.manifoldcf.core.lockmanager;

public class ExpiredObjectException extends Exception {
   public static final String _rcsid = "@(#)$Id: ExpiredObjectException.java 988245 2010-08-23 18:39:35Z kwright $";

   public ExpiredObjectException(String reason) {
      super(reason);
   }
}
