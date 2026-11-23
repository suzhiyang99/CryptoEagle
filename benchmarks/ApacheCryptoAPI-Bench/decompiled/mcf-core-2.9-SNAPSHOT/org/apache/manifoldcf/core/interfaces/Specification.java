package org.apache.manifoldcf.core.interfaces;

public class Specification extends Configuration {
   public static final String _rcsid = "@(#)$Id: Specification.java 988245 2010-08-23 18:39:35Z kwright $";

   public Specification() {
      super("specification");
   }

   public Specification(String xml) throws ManifoldCFException {
      super("specification");
      this.fromXML(xml);
   }

   protected Configuration createNew() {
      return new Specification();
   }

   protected ConfigurationNode createNewNode(String type) {
      return new SpecificationNode(type);
   }

   public SpecificationNode getChild(int index) {
      return (SpecificationNode)this.findChild(index);
   }

   public Specification duplicate(boolean readOnly) {
      if (readOnly && this.readOnly) {
         return this;
      } else {
         Specification rval = new Specification();
         int i = 0;

         while(i < this.children.size()) {
            SpecificationNode node = (SpecificationNode)this.children.get(i++);
            rval.children.add(node.duplicate(readOnly));
         }

         rval.readOnly = readOnly;
         return rval;
      }
   }
}
