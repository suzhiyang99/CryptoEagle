package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.namespace.QName;

public class PortInfo extends InfoObject {
   public String serviceId;
   public QName wsdlService;
   public String serviceName;
   public String portId;
   public QName wsdlPort;
   public String portName;
   public String seiInterfaceName;
   public String wsdlFile;
   public String serviceLink;
   public final List<HandlerChainInfo> handlerChains = new ArrayList();
   public boolean mtomEnabled;
   public String binding;
   public String location;
   public String authMethod;
   public String realmName;
   public String transportGuarantee;
   public String securityRealmName;
   public Properties properties;
}
