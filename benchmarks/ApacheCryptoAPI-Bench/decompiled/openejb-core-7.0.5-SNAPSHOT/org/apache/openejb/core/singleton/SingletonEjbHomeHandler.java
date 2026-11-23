package org.apache.openejb.core.singleton;

import java.lang.reflect.Method;
import java.util.List;
import javax.ejb.RemoveException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.core.ivm.EjbHomeProxyHandler;
import org.apache.openejb.core.ivm.EjbObjectProxyHandler;

public class SingletonEjbHomeHandler extends EjbHomeProxyHandler {
   public SingletonEjbHomeHandler(BeanContext beanContext, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      super(beanContext, interfaceType, interfaces, mainInterface);
   }

   protected Object findX(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      throw new UnsupportedOperationException("Stateful beans may not have find methods");
   }

   protected Object removeByPrimaryKey(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      throw new RemoveException("Session objects are private resources and do not have primary keys");
   }

   protected Object removeWithHandle(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      return null;
   }

   protected EjbObjectProxyHandler newEjbObjectHandler(BeanContext beanContext, Object pk, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      return new SingletonEjbObjectHandler(this.getBeanContext(), pk, interfaceType, interfaces, mainInterface);
   }
}
