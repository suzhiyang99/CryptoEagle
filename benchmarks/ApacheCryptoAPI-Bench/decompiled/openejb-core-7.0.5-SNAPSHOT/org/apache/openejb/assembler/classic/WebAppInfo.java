package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class WebAppInfo extends CommonInfoObject {
   public String path;
   public String description;
   public String displayName;
   public String smallIcon;
   public String largeIcon;
   public String moduleId;
   public String host;
   public String contextRoot;
   public int sessionTimeout;
   public final Set<String> watchedResources = new TreeSet();
   public final Set<String> restClass = new TreeSet();
   public final Set<String> restApplications = new TreeSet();
   public final Set<String> ejbWebServices = new TreeSet();
   public final Set<String> ejbRestServices = new TreeSet();
   public final Set<ClassListInfo> webAnnotatedClasses = new LinkedHashSet();
   public final List<PortInfo> portInfos = new ArrayList();
   public final JndiEncInfo jndiEnc = new JndiEncInfo();
   public final List<ServletInfo> servlets = new ArrayList();
   public final List<ClassListInfo> jsfAnnotatedClasses = new ArrayList();
   public final Set<String> jaxRsProviders = new TreeSet();
   public final List<ListenerInfo> listeners = new ArrayList();
   public final List<FilterInfo> filters = new ArrayList();
}
