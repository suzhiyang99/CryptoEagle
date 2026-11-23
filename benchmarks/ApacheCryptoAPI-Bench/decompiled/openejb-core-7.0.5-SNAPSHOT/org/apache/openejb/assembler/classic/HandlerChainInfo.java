package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public class HandlerChainInfo extends InfoObject {
   public QName serviceNamePattern;
   public QName portNamePattern;
   public final List<String> protocolBindings = new ArrayList();
   public final List<HandlerInfo> handlers = new ArrayList();
}
