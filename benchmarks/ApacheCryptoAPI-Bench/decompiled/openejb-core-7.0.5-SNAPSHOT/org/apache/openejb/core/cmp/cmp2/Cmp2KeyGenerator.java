package org.apache.openejb.core.cmp.cmp2;

import javax.ejb.EntityBean;
import org.apache.openejb.core.cmp.KeyGenerator;

public class Cmp2KeyGenerator implements KeyGenerator {
   public Object getPrimaryKey(EntityBean entity) {
      Cmp2Entity cmp2Entity = (Cmp2Entity)entity;
      return cmp2Entity.OpenEJB_getPrimaryKey();
   }
}
