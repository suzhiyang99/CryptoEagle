package org.apache.openejb.core.managed;

import java.util.Map;
import java.util.Properties;
import org.apache.openejb.SystemException;

public interface PassivationStrategy {
   void init(Properties var1) throws SystemException;

   void passivate(Map var1) throws SystemException;

   Object activate(Object var1) throws SystemException;
}
