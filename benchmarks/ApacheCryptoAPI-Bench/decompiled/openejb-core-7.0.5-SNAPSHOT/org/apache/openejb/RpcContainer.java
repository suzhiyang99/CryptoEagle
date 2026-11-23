package org.apache.openejb;

import java.lang.reflect.Method;

public interface RpcContainer extends Container {
   Object invoke(Object var1, InterfaceType var2, Class var3, Method var4, Object[] var5, Object var6) throws OpenEJBException;
}
