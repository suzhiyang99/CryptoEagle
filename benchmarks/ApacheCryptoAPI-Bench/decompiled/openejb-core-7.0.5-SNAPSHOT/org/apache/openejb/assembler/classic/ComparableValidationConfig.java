package org.apache.openejb.assembler.classic;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

public class ComparableValidationConfig implements Serializable {
   private final String providerClassName;
   private final String messageInterpolatorClass;
   private final String traversableResolverClass;
   private final String constraintFactoryClass;
   private final String parameterNameProviderClass;
   private final String version;
   private final Properties propertyTypes;
   private final List<String> constraintMappings;
   private final boolean executableValidationEnabled;
   private final List<String> validatedTypes;
   private final int hash;

   public ComparableValidationConfig(String providerClassName, String messageInterpolatorClass, String traversableResolverClass, String constraintFactoryClass, String parameterNameProviderClass, String version, Properties propertyTypes, List<String> constraintMappings, boolean executableValidationEnabled, List<String> validatedTypes) {
      this.providerClassName = providerClassName;
      this.messageInterpolatorClass = messageInterpolatorClass;
      this.traversableResolverClass = traversableResolverClass;
      this.constraintFactoryClass = constraintFactoryClass;
      this.parameterNameProviderClass = parameterNameProviderClass;
      this.version = version;
      this.propertyTypes = propertyTypes;
      this.constraintMappings = constraintMappings;
      this.executableValidationEnabled = executableValidationEnabled;
      this.validatedTypes = validatedTypes;
      int result = providerClassName != null ? providerClassName.hashCode() : 0;
      result = 31 * result + (messageInterpolatorClass != null ? messageInterpolatorClass.hashCode() : 0);
      result = 31 * result + (traversableResolverClass != null ? traversableResolverClass.hashCode() : 0);
      result = 31 * result + (constraintFactoryClass != null ? constraintFactoryClass.hashCode() : 0);
      result = 31 * result + (parameterNameProviderClass != null ? parameterNameProviderClass.hashCode() : 0);
      result = 31 * result + (version != null ? version.hashCode() : 0);
      result = 31 * result + (propertyTypes != null ? propertyTypes.hashCode() : 0);
      result = 31 * result + (constraintMappings != null ? constraintMappings.hashCode() : 0);
      result = 31 * result + (executableValidationEnabled ? 1 : 0);
      result = 31 * result + (validatedTypes != null ? validatedTypes.hashCode() : 0);
      this.hash = result;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ComparableValidationConfig that = (ComparableValidationConfig)o;
         if (this.executableValidationEnabled != that.executableValidationEnabled) {
            return false;
         } else {
            if (this.constraintFactoryClass != null) {
               if (!this.constraintFactoryClass.equals(that.constraintFactoryClass)) {
                  return false;
               }
            } else if (that.constraintFactoryClass != null) {
               return false;
            }

            if (this.constraintMappings != null) {
               if (!this.constraintMappings.equals(that.constraintMappings)) {
                  return false;
               }
            } else if (that.constraintMappings != null) {
               return false;
            }

            if (this.messageInterpolatorClass != null) {
               if (!this.messageInterpolatorClass.equals(that.messageInterpolatorClass)) {
                  return false;
               }
            } else if (that.messageInterpolatorClass != null) {
               return false;
            }

            if (this.parameterNameProviderClass != null) {
               if (!this.parameterNameProviderClass.equals(that.parameterNameProviderClass)) {
                  return false;
               }
            } else if (that.parameterNameProviderClass != null) {
               return false;
            }

            if (this.propertyTypes != null) {
               if (!this.propertyTypes.equals(that.propertyTypes)) {
                  return false;
               }
            } else if (that.propertyTypes != null) {
               return false;
            }

            if (this.providerClassName != null) {
               if (!this.providerClassName.equals(that.providerClassName)) {
                  return false;
               }
            } else if (that.providerClassName != null) {
               return false;
            }

            if (this.traversableResolverClass != null) {
               if (!this.traversableResolverClass.equals(that.traversableResolverClass)) {
                  return false;
               }
            } else if (that.traversableResolverClass != null) {
               return false;
            }

            if (this.validatedTypes != null) {
               if (!this.validatedTypes.equals(that.validatedTypes)) {
                  return false;
               }
            } else if (that.validatedTypes != null) {
               return false;
            }

            if (this.version != null) {
               if (!this.version.equals(that.version)) {
                  return false;
               }
            } else if (that.version != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.hash;
   }
}
