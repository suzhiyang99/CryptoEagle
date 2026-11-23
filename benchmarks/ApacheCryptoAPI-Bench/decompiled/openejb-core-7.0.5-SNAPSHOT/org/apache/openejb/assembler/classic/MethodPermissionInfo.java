package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class MethodPermissionInfo extends MethodAttributeInfo {
   public String description;
   public final List<String> roleNames = new ArrayList();
   public boolean excluded;
   public boolean unchecked;
}
