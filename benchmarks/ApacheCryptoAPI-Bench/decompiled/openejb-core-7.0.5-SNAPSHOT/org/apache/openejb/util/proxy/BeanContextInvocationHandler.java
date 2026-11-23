package org.apache.openejb.util.proxy;

import java.lang.reflect.InvocationHandler;
import org.apache.openejb.BeanContext;

public interface BeanContextInvocationHandler extends InvocationHandler {
   BeanContext getBeanContext();
}
