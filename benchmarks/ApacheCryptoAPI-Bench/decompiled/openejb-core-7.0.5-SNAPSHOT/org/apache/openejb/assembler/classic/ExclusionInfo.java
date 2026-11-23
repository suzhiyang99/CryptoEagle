package org.apache.openejb.assembler.classic;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class ExclusionInfo extends InfoObject {
   public final List<String> availableClasses = new LinkedList();
   public final List<String> notAvailableClasses = new LinkedList();
   public final List<String> systemPropertiesPresence = new LinkedList();
   public final Properties systemProperties = new Properties();
}
