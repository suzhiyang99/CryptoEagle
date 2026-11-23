package org.apache.openejb.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

public class Enumerator implements Serializable, Enumeration {
   private static final long serialVersionUID = 8382575322402414896L;
   private final List list;
   private int index;

   public Enumerator(List list) {
      this.list = list;
      this.index = 0;
   }

   public boolean hasMoreElements() {
      return this.index < this.list.size();
   }

   public Object nextElement() {
      if (!this.hasMoreElements()) {
         throw new NoSuchElementException();
      } else {
         return this.list.get(this.index++);
      }
   }
}
