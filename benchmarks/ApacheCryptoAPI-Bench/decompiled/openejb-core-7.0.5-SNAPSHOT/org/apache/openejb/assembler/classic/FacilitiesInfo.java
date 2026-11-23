package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class FacilitiesInfo extends InfoObject {
   public ProxyFactoryInfo intraVmServer;
   public final List<JndiContextInfo> remoteJndiContexts = new ArrayList();
   public final List<ResourceInfo> resources = new ArrayList();
   public final List<ConnectionManagerInfo> connectionManagers = new ArrayList();
   public TransactionServiceInfo transactionService;
   public SecurityServiceInfo securityService;
   public final List<ServiceInfo> services = new ArrayList();
   public final List<String> serverObservers = new ArrayList();
}
