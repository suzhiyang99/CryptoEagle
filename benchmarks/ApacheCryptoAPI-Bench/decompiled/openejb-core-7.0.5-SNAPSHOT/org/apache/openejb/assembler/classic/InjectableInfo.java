package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class InjectableInfo extends InfoObject {
   public final List<InjectionInfo> targets = new ArrayList();
   public String referenceName;
   public ReferenceLocationInfo location;
}
