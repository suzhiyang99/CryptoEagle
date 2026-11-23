package org.apache.openejb.core.cmp;

import javax.ejb.EntityBean;
import javax.ejb.RemoveException;

public interface CmpCallback {
   void setEntityContext(EntityBean var1);

   void unsetEntityContext(EntityBean var1);

   void ejbActivate(EntityBean var1);

   void ejbPassivate(EntityBean var1);

   void ejbLoad(EntityBean var1);

   void ejbStore(EntityBean var1);

   void ejbRemove(EntityBean var1) throws RemoveException;
}
