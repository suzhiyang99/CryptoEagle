package org.apache.meecrowave;

public class MeecrowaveExplosion extends RuntimeException {
   public MeecrowaveExplosion(String msg, Exception e) {
      super(msg, e);
   }
}
