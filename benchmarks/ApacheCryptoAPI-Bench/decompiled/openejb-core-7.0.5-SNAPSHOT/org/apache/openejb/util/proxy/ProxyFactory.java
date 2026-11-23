package org.apache.openejb.util.proxy;

import java.lang.reflect.InvocationHandler;
import java.util.Properties;
import org.apache.openejb.OpenEJBException;

public interface ProxyFactory {
   void init(Properties var1) throws OpenEJBException;

   InvocationHandler getInvocationHandler(Object var1) throws IllegalArgumentException;

   Class getProxyClass(Class var1) throws IllegalArgumentException;

   Class getProxyClass(Class[] var1) throws IllegalArgumentException;

   boolean isProxyClass(Class var1);

   Object newProxyInstance(Class var1, InvocationHandler var2) throws IllegalArgumentException;

   Object newProxyInstance(Class[] var1, InvocationHandler var2) throws IllegalArgumentException;
}
