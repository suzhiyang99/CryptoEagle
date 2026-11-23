package org.apache.manifoldcf.core.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConfigurationNode implements IHierarchyParent {
   public static final String _rcsid = "@(#)$Id: ConfigurationNode.java 988245 2010-08-23 18:39:35Z kwright $";
   protected List<ConfigurationNode> children = null;
   protected Map<String, String> attributes = null;
   protected String type = null;
   protected String value = null;
   protected boolean readOnly = false;

   public ConfigurationNode(String type) {
      this.type = type;
   }

   public ConfigurationNode(ConfigurationNode source) {
      this.type = source.type;
      this.value = source.value;
      this.readOnly = source.readOnly;
      if (source.attributes != null) {
         for(String attribute : source.attributes.keySet()) {
            String attrValue = (String)source.attributes.get(attribute);
            if (this.attributes == null) {
               this.attributes = new HashMap();
            }

            this.attributes.put(attribute, attrValue);
         }
      }

      int i = 0;

      while(i < source.getChildCount()) {
         ConfigurationNode child = source.findChild(i++);
         this.addChild(this.getChildCount(), this.createNewNode(child));
      }

   }

   protected ConfigurationNode createNewNode() {
      return new ConfigurationNode(this.type);
   }

   protected ConfigurationNode createNewNode(ConfigurationNode source) {
      return new ConfigurationNode(source);
   }

   public void makeReadOnly() {
      if (!this.readOnly) {
         if (this.children != null) {
            int i = 0;

            while(i < this.children.size()) {
               ConfigurationNode child = (ConfigurationNode)this.children.get(i++);
               child.makeReadOnly();
            }
         }

         this.readOnly = true;
      }
   }

   protected ConfigurationNode createDuplicate(boolean readOnly) {
      if (readOnly && this.readOnly) {
         return this;
      } else {
         ConfigurationNode rval = this.createNewNode();
         rval.value = this.value;
         if (this.attributes != null) {
            rval.attributes = cloneAttributes(this.attributes);
         }

         if (this.children != null) {
            rval.children = new ArrayList();
            int i = 0;

            while(i < this.children.size()) {
               ConfigurationNode node = (ConfigurationNode)this.children.get(i++);
               rval.children.add(node.createDuplicate(readOnly));
            }
         }

         rval.readOnly = readOnly;
         return rval;
      }
   }

   public String getType() {
      return this.type;
   }

   public void setValue(String value) {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         this.value = value;
      }
   }

   public String getValue() {
      return this.value;
   }

   public int getChildCount() {
      return this.children == null ? 0 : this.children.size();
   }

   public ConfigurationNode findChild(int index) {
      return (ConfigurationNode)this.children.get(index);
   }

   public void removeChild(int index) {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         if (this.children != null) {
            this.children.remove(index);
            if (this.children.size() == 0) {
               this.children = null;
            }
         }

      }
   }

   public void addChild(int index, ConfigurationNode child) {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         if (this.children == null) {
            this.children = new ArrayList();
         }

         this.children.add(index, child);
      }
   }

   public void clearChildren() {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         this.children.clear();
      }
   }

   public void setAttribute(String attribute, String value) {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         if (value == null) {
            if (this.attributes != null) {
               this.attributes.remove(attribute);
               if (this.attributes.size() == 0) {
                  this.attributes = null;
               }
            }
         } else {
            if (this.attributes == null) {
               this.attributes = new HashMap();
            }

            this.attributes.put(attribute, value);
         }

      }
   }

   public int getAttributeCount() {
      return this.attributes == null ? 0 : this.attributes.size();
   }

   public Iterator<String> getAttributes() {
      return this.attributes == null ? (new HashMap()).keySet().iterator() : this.attributes.keySet().iterator();
   }

   public String getAttributeValue(String attribute) {
      return this.attributes == null ? null : (String)this.attributes.get(attribute);
   }

   public int hashCode() {
      int rval = this.type.hashCode();
      if (this.value != null) {
         rval += this.value.hashCode();
      }

      if (this.attributes != null) {
         for(String key : this.attributes.keySet()) {
            String attrValue = (String)this.attributes.get(key);
            rval += key.hashCode() + attrValue.hashCode();
         }
      }

      if (this.children != null) {
         for(int i = 0; i < this.children.size(); rval += ((ConfigurationNode)this.children.get(i++)).hashCode()) {
         }
      }

      return rval;
   }

   public boolean equals(Object o) {
      if (!(o instanceof ConfigurationNode)) {
         return false;
      } else {
         ConfigurationNode n = (ConfigurationNode)o;
         if ((this.attributes == null ? 0 : this.attributes.size()) != (n.attributes == null ? 0 : n.attributes.size())) {
            return false;
         } else if ((this.children == null ? 0 : this.children.size()) != (n.children == null ? 0 : n.children.size())) {
            return false;
         } else if (!this.type.equals(n.type)) {
            return false;
         } else {
            if (this.value != null && n.value != null) {
               if (!this.value.equals(n.value)) {
                  return false;
               }
            } else if (this.value != n.value) {
               return false;
            }

            if (this.attributes != null && n.attributes != null) {
               for(String key : this.attributes.keySet()) {
                  String attrValue = (String)this.attributes.get(key);
                  String nAttrValue = (String)n.attributes.get(key);
                  if (nAttrValue == null || !attrValue.equals(nAttrValue)) {
                     return false;
                  }
               }
            }

            if (this.children != null && n.children != null) {
               for(int i = 0; i < this.children.size(); ++i) {
                  ConfigurationNode child = (ConfigurationNode)this.children.get(i);
                  ConfigurationNode nChild = (ConfigurationNode)n.children.get(i);
                  if (!child.equals(nChild)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("(");
      sb.append(this.type);
      if (this.value != null) {
         sb.append(":").append(this.value);
      }

      if (this.attributes != null) {
         Iterator<String> iter = this.attributes.keySet().iterator();

         while(iter.hasNext()) {
            sb.append(" ");
            String key = (String)iter.next();
            String attrValue = (String)this.attributes.get(key);
            sb.append(key).append("='").append(attrValue).append("'");
         }
      }

      sb.append(" [");
      if (this.children != null) {
         int i = 0;

         while(i < this.children.size()) {
            if (i > 0) {
               sb.append(", ");
            }

            ConfigurationNode cn = (ConfigurationNode)this.children.get(i++);
            sb.append(cn.toString());
         }
      }

      sb.append("])");
      return sb.toString();
   }

   protected static Map<String, String> cloneAttributes(Map<String, String> attributes) {
      Map<String, String> rval = new HashMap();

      for(Map.Entry<String, String> entry : attributes.entrySet()) {
         rval.put(entry.getKey(), entry.getValue());
      }

      return rval;
   }
}
