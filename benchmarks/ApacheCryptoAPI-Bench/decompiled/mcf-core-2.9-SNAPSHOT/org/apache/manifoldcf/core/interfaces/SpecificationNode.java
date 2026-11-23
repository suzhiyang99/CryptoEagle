package org.apache.manifoldcf.core.interfaces;

public class SpecificationNode extends ConfigurationNode {
   public static final String _rcsid = "@(#)$Id: SpecificationNode.java 988245 2010-08-23 18:39:35Z kwright $";

   public SpecificationNode(String type) {
      super(type);
   }

   public SpecificationNode(ConfigurationNode source) {
      super(source);
   }

   protected ConfigurationNode createNewNode() {
      return new SpecificationNode(this.type);
   }

   protected ConfigurationNode createNewNode(ConfigurationNode source) {
      return new SpecificationNode(source);
   }

   public SpecificationNode duplicate(boolean readOnly) {
      return (SpecificationNode)this.createDuplicate(readOnly);
   }

   public SpecificationNode getChild(int index) {
      return (SpecificationNode)this.findChild(index);
   }
}
