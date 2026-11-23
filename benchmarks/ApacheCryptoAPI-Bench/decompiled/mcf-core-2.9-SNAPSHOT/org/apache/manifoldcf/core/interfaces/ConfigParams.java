package org.apache.manifoldcf.core.interfaces;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class ConfigParams extends Configuration {
   public static final String _rcsid = "@(#)$Id: ConfigParams.java 988245 2010-08-23 18:39:35Z kwright $";
   protected static final String PARAMETER_TYPE = "_PARAMETER_";
   protected static final String ATTR_NAME = "name";
   protected Map<String, String> params = new HashMap();

   public ConfigParams() {
      super("configuration");
   }

   public ConfigParams(Map<String, String> map) {
      super("configuration");

      for(String key : map.keySet()) {
         String value = (String)map.get(key);
         ConfigNode cn = new ConfigNode("_PARAMETER_");
         cn.setAttribute("name", key);
         cn.setValue(value);
         this.addChild(this.getChildCount(), cn);
      }

   }

   public ConfigParams(String xml) throws ManifoldCFException {
      super("configuration");
      this.fromXML(xml);
   }

   public ConfigParams(InputStream xmlstream) throws ManifoldCFException {
      super("configuration");
      this.fromXML(xmlstream);
   }

   protected Configuration createNew() {
      return new ConfigParams();
   }

   protected ConfigurationNode createNewNode(String type) {
      return new ConfigNode(type);
   }

   protected void clearOuterNodes() {
      this.params.clear();
   }

   protected void addOuterNode(ConfigurationNode node) {
      if (node.getType().equals("_PARAMETER_")) {
         String name = node.getAttributeValue("name");
         String value = node.getValue();
         if (name != null && value != null) {
            this.params.put(name, value);
         }
      }

   }

   protected void removeOuterNode(ConfigurationNode node) {
      if (node.getType().equals("_PARAMETER_")) {
         String name = node.getAttributeValue("name");
         if (name != null) {
            this.params.remove(name);
         }
      }

   }

   public String getParameter(String key) {
      return (String)this.params.get(key);
   }

   public String getObfuscatedParameter(String key) {
      String rval = this.getParameter(key);
      if (rval == null) {
         return rval;
      } else {
         try {
            return ManifoldCF.deobfuscate(rval);
         } catch (ManifoldCFException var4) {
            return "";
         }
      }
   }

   public void setParameter(String key, String value) {
      if (this.params.get(key) != null) {
         for(int i = 0; i < this.children.size(); ++i) {
            ConfigNode node = (ConfigNode)this.children.get(i);
            if (node.getType().equals("_PARAMETER_")) {
               String name = node.getAttributeValue("name");
               if (name.equals(key)) {
                  this.removeChild(i);
                  break;
               }
            }
         }
      }

      if (value != null) {
         ConfigNode cn = new ConfigNode("_PARAMETER_");
         cn.setAttribute("name", key);
         cn.setValue(value);
         this.addChild(this.getChildCount(), cn);
      }

   }

   public void setObfuscatedParameter(String key, String value) {
      if (value != null) {
         try {
            value = ManifoldCF.obfuscate(value);
         } catch (ManifoldCFException var4) {
            value = "";
         }
      }

      this.setParameter(key, value);
   }

   public Iterator listParameters() {
      return this.params.keySet().iterator();
   }

   public ConfigParams duplicate() {
      return (ConfigParams)this.createDuplicate(false);
   }

   public ConfigNode getChild(int index) {
      return (ConfigNode)this.findChild(index);
   }
}
