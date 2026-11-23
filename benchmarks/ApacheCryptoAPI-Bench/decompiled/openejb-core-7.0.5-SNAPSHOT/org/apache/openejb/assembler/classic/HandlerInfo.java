package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.namespace.QName;

public class HandlerInfo extends InfoObject {
   public String handlerName;
   public String handlerClass;
   public final Properties initParams = new Properties();
   public final List<QName> soapHeaders = new ArrayList();
   public final List<String> soapRoles = new ArrayList();
}
