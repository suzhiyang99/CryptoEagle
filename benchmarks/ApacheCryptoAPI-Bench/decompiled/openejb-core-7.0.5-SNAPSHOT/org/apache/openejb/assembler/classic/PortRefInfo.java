package org.apache.openejb.assembler.classic;

import java.util.Properties;
import javax.xml.namespace.QName;

public class PortRefInfo extends InfoObject {
   public QName qname;
   public String serviceEndpointInterface;
   public boolean enableMtom;
   public final Properties properties = new Properties();
}
