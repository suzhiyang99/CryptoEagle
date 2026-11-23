package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public class ServiceReferenceInfo extends InjectableInfo {
   public String id;
   public QName serviceQName;
   public String serviceType;
   public QName portQName;
   public String referenceType;
   public String wsdlFile;
   public String jaxrpcMappingFile;
   public final List<HandlerChainInfo> handlerChains = new ArrayList();
   public final List<PortRefInfo> portRefs = new ArrayList();
}
