package org.apache.manifoldcf.core.interfaces;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class ManifoldCFConfiguration extends Configuration {
   public static final String _rcsid = "@(#)$Id: ManifoldCFConfiguration.java 988245 2010-08-23 18:39:35Z kwright $";
   public static final String NODE_PROPERTY = "property";
   public static final String ATTRIBUTE_NAME = "name";
   public static final String ATTRIBUTE_VALUE = "value";
   protected final Map<String, String> localProperties = new HashMap();

   public ManifoldCFConfiguration() {
      super("configuration");
   }

   public ManifoldCFConfiguration(InputStream xmlStream) throws ManifoldCFException {
      super("configuration");
      this.fromXML(xmlStream);
      this.parseProperties();
   }

   public String getProperty(String s) {
      return (String)this.localProperties.get(s);
   }

   public String getStringProperty(String s, String defaultValue) {
      String rval = this.getProperty(s);
      if (rval == null) {
         rval = defaultValue;
      }

      return rval;
   }

   public String getPossiblyObfuscatedStringProperty(String s, String defaultValue) throws ManifoldCFException {
      String obfuscatedPropertyName = s + ".obfuscated";
      String rval = this.getProperty(obfuscatedPropertyName);
      if (rval != null) {
         return ManifoldCF.deobfuscate(rval);
      } else {
         rval = this.getProperty(s);
         if (rval == null) {
            rval = defaultValue;
         }

         return rval;
      }
   }

   public boolean getBooleanProperty(String s, boolean defaultValue) throws ManifoldCFException {
      String value = this.getProperty(s);
      if (value == null) {
         return defaultValue;
      } else if (!value.equals("true") && !value.equals("yes")) {
         if (!value.equals("false") && !value.equals("no")) {
            throw new ManifoldCFException("Illegal property value for boolean property '" + s + "': '" + value + "'");
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public int getIntProperty(String s, int defaultValue) throws ManifoldCFException {
      String value = this.getProperty(s);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return Integer.parseInt(value);
         } catch (NumberFormatException e) {
            throw new ManifoldCFException("Illegal property value for integer property '" + s + "': '" + value + "': " + e.getMessage(), e, 3);
         }
      }
   }

   public long getLongProperty(String s, long defaultValue) throws ManifoldCFException {
      String value = this.getProperty(s);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return Long.parseLong(value);
         } catch (NumberFormatException e) {
            throw new ManifoldCFException("Illegal property value for long property '" + s + "': '" + value + "': " + e.getMessage(), e, 3);
         }
      }
   }

   public double getDoubleProperty(String s, double defaultValue) throws ManifoldCFException {
      String value = this.getProperty(s);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return Double.parseDouble(value);
         } catch (NumberFormatException e) {
            throw new ManifoldCFException("Illegal property value for double property '" + s + "': '" + value + "': " + e.getMessage(), e, 3);
         }
      }
   }

   protected void parseProperties() throws ManifoldCFException {
      this.localProperties.clear();

      for(int i = 0; i < this.getChildCount(); ++i) {
         ConfigurationNode cn = this.findChild(i);
         if (cn.getType().equals("property")) {
            String name = cn.getAttributeValue("name");
            String value = cn.getAttributeValue("value");
            if (name == null) {
               throw new ManifoldCFException("Node type 'property' requires a 'name' attribute");
            }

            this.localProperties.put(name, value);
         }
      }

   }

   public void fromXML(InputStream is) throws ManifoldCFException {
      super.fromXML(is);
      this.parseProperties();
   }

   protected Configuration createNew() {
      return new ManifoldCFConfiguration();
   }
}
