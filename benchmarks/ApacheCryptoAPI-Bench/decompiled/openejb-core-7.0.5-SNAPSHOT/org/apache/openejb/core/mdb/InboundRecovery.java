package org.apache.openejb.core.mdb;

import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ResourceAdapter;
import org.apache.openejb.OpenEJBException;

public interface InboundRecovery {
   void recover(ResourceAdapter var1, ActivationSpec var2, String var3) throws OpenEJBException;
}
