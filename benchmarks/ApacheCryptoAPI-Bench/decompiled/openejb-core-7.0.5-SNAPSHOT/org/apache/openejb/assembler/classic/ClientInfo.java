package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClientInfo extends CommonInfoObject {
   public String path;
   public String description;
   public String displayName;
   public String smallIcon;
   public String largeIcon;
   public String moduleId;
   public String mainClass;
   public final List<String> localClients = new ArrayList();
   public final List<String> remoteClients = new ArrayList();
   public String callbackHandler;
   public final Set<String> watchedResources = new TreeSet();
   public final JndiEncInfo jndiEnc = new JndiEncInfo();
   public final List<CallbackInfo> postConstruct = new ArrayList();
   public final List<CallbackInfo> preDestroy = new ArrayList();
}
