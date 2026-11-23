package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class AppInfo extends InfoObject {
   public final Properties properties = new Properties();
   public String appId;
   public String path;
   public Set<String> paths = new LinkedHashSet();
   public boolean autoDeploy = true;
   public boolean delegateFirst = true;
   public boolean standaloneModule;
   public final List<ClientInfo> clients = new ArrayList();
   public final List<EjbJarInfo> ejbJars = new ArrayList();
   public final List<IdPropertiesInfo> pojoConfigurations = new ArrayList();
   public final List<ConnectorInfo> connectors = new ArrayList();
   public final List<WebAppInfo> webApps = new ArrayList();
   public final List<PersistenceUnitInfo> persistenceUnits = new ArrayList();
   public List<ServiceInfo> services = new ArrayList();
   public final List<String> libs = new ArrayList();
   public final Set<String> watchedResources = new TreeSet();
   public final Set<String> containerIds = new TreeSet();
   public final Set<String> resourceIds = new TreeSet();
   public final Set<String> resourceAliases = new TreeSet();
   public final JndiEncInfo globalJndiEnc = new JndiEncInfo();
   public final JndiEncInfo appJndiEnc = new JndiEncInfo();
   public String cmpMappingsXml;
   public final Properties jmx = new Properties();
   public final Set<String> mbeans = new TreeSet();
   public final Set<String> jaxRsProviders = new TreeSet();
   public final Set<String> jsfClasses = new TreeSet();
   public final Set<String> eventClassesNeedingAppClassloader = new TreeSet();
   public boolean webAppAlone;
}
