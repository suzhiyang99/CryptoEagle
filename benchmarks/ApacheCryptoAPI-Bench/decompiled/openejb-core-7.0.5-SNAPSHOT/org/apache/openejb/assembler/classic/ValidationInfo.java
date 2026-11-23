package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ValidationInfo extends InfoObject {
   public String providerClassName;
   public String messageInterpolatorClass;
   public String traversableResolverClass;
   public String constraintFactoryClass;
   public String parameterNameProviderClass;
   public String version;
   public final Properties propertyTypes = new Properties();
   public final List<String> constraintMappings = new ArrayList();
   public boolean executableValidationEnabled;
   public final List<String> validatedTypes = new ArrayList();
}
