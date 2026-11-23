package org.apache.openejb.resource;

import javax.transaction.xa.XAResource;

public interface XAResourceWrapper {
   XAResource wrap(XAResource var1, String var2);
}
