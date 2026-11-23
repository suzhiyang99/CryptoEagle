package org.apache.openejb.osgi.client;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.openejb.core.LocalInitialContext;

public class LocalInitialContextFactory extends org.apache.openejb.core.LocalInitialContextFactory {
   public Context getInitialContext(Hashtable env) throws NamingException {
      this.init(env);
      return new LocalInitialContext(env, this);
   }
}
