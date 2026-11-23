package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ConnectorInfo extends CommonInfoObject {
   public String path;
   public String description;
   public String displayName;
   public String smallIcon;
   public String largeIcon;
   public String moduleId;
   public final List<String> libs = new ArrayList();
   public final Set<String> watchedResources = new TreeSet();
   public ResourceInfo resourceAdapter;
   public List<ResourceInfo> outbound = new ArrayList();
   public List<MdbContainerInfo> inbound = new ArrayList();
   public List<ResourceInfo> adminObject = new ArrayList();
   public Set<String> mbeans = new TreeSet();
}
