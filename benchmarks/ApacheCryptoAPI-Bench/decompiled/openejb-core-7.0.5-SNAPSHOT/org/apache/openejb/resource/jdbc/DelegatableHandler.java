package org.apache.openejb.resource.jdbc;

import java.lang.reflect.InvocationHandler;
import javax.sql.CommonDataSource;

public interface DelegatableHandler extends InvocationHandler {
   CommonDataSource getDelegate();
}
