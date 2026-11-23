package org.apache.openejb;

import java.net.URI;

public interface ClusteredRPCContainer extends RpcContainer {
   URI[] getLocations(BeanContext var1);
}
