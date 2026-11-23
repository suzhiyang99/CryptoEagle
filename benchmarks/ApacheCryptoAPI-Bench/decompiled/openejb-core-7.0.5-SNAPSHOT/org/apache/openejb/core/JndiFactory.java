package org.apache.openejb.core;

import java.util.Map;
import javax.naming.Context;
import org.apache.openejb.SystemException;

public interface JndiFactory {
   Context createComponentContext(Map<String, Object> var1) throws SystemException;

   Context createRootContext();
}
