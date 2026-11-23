package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class MethodScheduleInfo extends InfoObject {
   public String description;
   public final List<ScheduleInfo> schedules = new ArrayList();
   public NamedMethodInfo method;
}
