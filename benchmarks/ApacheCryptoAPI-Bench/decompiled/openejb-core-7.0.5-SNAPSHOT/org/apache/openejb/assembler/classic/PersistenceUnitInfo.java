package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class PersistenceUnitInfo extends InfoObject {
   public String id;
   public String name;
   public String provider;
   public String transactionType;
   public String jtaDataSource;
   public String nonJtaDataSource;
   public final List<String> mappingFiles = new ArrayList();
   public final List<String> jarFiles = new ArrayList();
   public final List<String> classes = new ArrayList();
   public boolean excludeUnlistedClasses;
   public final Properties properties = new Properties();
   public String persistenceUnitRootUrl;
   public final Set<String> watchedResources = new TreeSet();
   public String persistenceXMLSchemaVersion;
   public String sharedCacheMode;
   public String validationMode;
   public String webappName;
}
