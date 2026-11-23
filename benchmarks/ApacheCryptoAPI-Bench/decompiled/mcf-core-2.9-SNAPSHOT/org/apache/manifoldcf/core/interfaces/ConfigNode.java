package org.apache.manifoldcf.core.interfaces;

public class ConfigNode extends ConfigurationNode {
   public static final String _rcsid = "@(#)$Id: ConfigNode.java 988245 2010-08-23 18:39:35Z kwright $";

   public ConfigNode(String type) {
      super(type);
   }

   public ConfigNode(ConfigurationNode source) {
      super(source);
   }

   protected ConfigurationNode createNewNode() {
      return new ConfigNode(this.type);
   }

   protected ConfigurationNode createNewNode(ConfigurationNode source) {
      return new ConfigNode(source);
   }

   public ConfigNode duplicate(boolean readOnly) {
      return (ConfigNode)this.createDuplicate(readOnly);
   }

   public ConfigNode getChild(int index) {
      return (ConfigNode)this.findChild(index);
   }
}
