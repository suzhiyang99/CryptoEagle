package org.apache.openejb.core.cmp;

import java.lang.reflect.Method;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.FinderException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.ThreadContext;

public interface CmpEngine {
   Object createBean(EntityBean var1, ThreadContext var2) throws CreateException;

   Object loadBean(ThreadContext var1, Object var2);

   void storeBeanIfNoTx(ThreadContext var1, Object var2);

   void removeBean(ThreadContext var1);

   List<Object> queryBeans(ThreadContext var1, Method var2, Object[] var3) throws FinderException;

   List<Object> queryBeans(BeanContext var1, String var2, Object[] var3) throws FinderException;

   int executeUpdateQuery(BeanContext var1, String var2, Object[] var3) throws FinderException;

   void deploy(BeanContext var1) throws OpenEJBException;

   void undeploy(BeanContext var1) throws OpenEJBException;
}
