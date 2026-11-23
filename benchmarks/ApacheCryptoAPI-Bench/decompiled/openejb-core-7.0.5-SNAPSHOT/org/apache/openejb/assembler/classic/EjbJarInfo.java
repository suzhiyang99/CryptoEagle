package org.apache.openejb.assembler.classic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class EjbJarInfo extends CommonInfoObject {
   public final Properties properties = new Properties();
   public String moduleName;
   public String moduleId;
   public URI moduleUri;
   public String path;
   public boolean webapp;
   @XmlElements({@XmlElement(
   name = "stateless",
   type = StatelessBeanInfo.class
), @XmlElement(
   name = "entity",
   type = EntityBeanInfo.class
), @XmlElement(
   name = "stateful",
   type = StatefulBeanInfo.class
), @XmlElement(
   name = "singleton",
   type = SingletonBeanInfo.class
), @XmlElement(
   name = "message-driven",
   type = MessageDrivenBeanInfo.class
), @XmlElement(
   name = "managed-bean",
   type = ManagedBeanInfo.class
)})
   public final List<EnterpriseBeanInfo> enterpriseBeans = new ArrayList();
   public final List<SecurityRoleInfo> securityRoles = new ArrayList();
   public final List<MethodPermissionInfo> methodPermissions = new ArrayList();
   public final List<MethodTransactionInfo> methodTransactions = new ArrayList();
   public final List<MethodConcurrencyInfo> methodConcurrency = new ArrayList();
   public final List<InterceptorInfo> interceptors = new ArrayList();
   public final List<InterceptorBindingInfo> interceptorBindings = new ArrayList();
   public final List<MethodInfo> excludeList = new ArrayList();
   public final List<ApplicationExceptionInfo> applicationException = new ArrayList();
   public final List<PortInfo> portInfos = new ArrayList();
   public final Set<String> watchedResources = new TreeSet();
   public final JndiEncInfo moduleJndiEnc = new JndiEncInfo();
   public BeansInfo beans;
   public Set<String> mbeans = new TreeSet();
   public final List<IdPropertiesInfo> pojoConfigurations = new ArrayList();
}
