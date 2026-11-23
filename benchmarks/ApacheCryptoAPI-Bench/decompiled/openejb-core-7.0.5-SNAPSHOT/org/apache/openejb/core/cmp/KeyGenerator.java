package org.apache.openejb.core.cmp;

import javax.ejb.EntityBean;

public interface KeyGenerator {
   Object getPrimaryKey(EntityBean var1);
}
