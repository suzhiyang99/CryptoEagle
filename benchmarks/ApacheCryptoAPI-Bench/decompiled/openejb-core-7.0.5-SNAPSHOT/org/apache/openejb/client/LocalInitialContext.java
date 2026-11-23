package org.apache.openejb.client;

import java.util.Hashtable;
import javax.naming.NamingException;

/** @deprecated */
public class LocalInitialContext extends org.apache.openejb.core.LocalInitialContext {
   public LocalInitialContext(Hashtable env, org.apache.openejb.core.LocalInitialContextFactory factory) throws NamingException {
      super(env, factory);
   }
}
