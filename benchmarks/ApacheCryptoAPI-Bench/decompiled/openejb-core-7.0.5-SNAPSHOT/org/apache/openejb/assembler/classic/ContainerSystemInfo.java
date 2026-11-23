package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class ContainerSystemInfo extends InfoObject {
   public final List<ContainerInfo> containers = new ArrayList();
   public final List<AppInfo> applications = new ArrayList();
   public final List<String> autoDeploy = new ArrayList();
}
