package org.apache.manifoldcf.core.interfaces;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.manifoldcf.core.common.XMLDoc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Configuration implements IHierarchyParent {
   public static final String _rcsid = "@(#)$Id: Configuration.java 988245 2010-08-23 18:39:35Z kwright $";
   protected static final String JSON_ATTRIBUTE = "_attribute_";
   protected static final String JSON_VALUE = "_value_";
   protected static final String JSON_CHILDREN = "_children_";
   protected static final String JSON_TYPE = "_type_";
   protected String rootNodeLabel;
   protected List<ConfigurationNode> children = new ArrayList();
   protected boolean readOnly = false;

   public Configuration() {
      this.rootNodeLabel = "data";
   }

   public Configuration(String rootNodeLabel) {
      this.rootNodeLabel = rootNodeLabel;
   }

   protected Configuration createNew() {
      return new Configuration();
   }

   protected ConfigurationNode createNewNode(String type) {
      return new ConfigurationNode(type);
   }

   protected void clearOuterNodes() {
   }

   protected void addOuterNode(ConfigurationNode node) {
   }

   protected void removeOuterNode(ConfigurationNode node) {
   }

   protected Configuration createDuplicate(boolean readOnly) {
      if (readOnly && this.readOnly) {
         return this;
      } else {
         Configuration rval = this.createNew();
         rval.readOnly = readOnly;
         if (this.children != null) {
            int i = 0;

            while(i < this.children.size()) {
               ConfigurationNode child = (ConfigurationNode)this.children.get(i++);
               ConfigurationNode newChild = child.createDuplicate(readOnly);
               rval.addChild(rval.getChildCount(), newChild);
            }
         }

         return rval;
      }
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

   public String toXML() throws ManifoldCFException {
      XMLDoc doc = new XMLDoc();
      Object top = doc.createElement((Object)null, this.rootNodeLabel);
      int i = 0;

      while(i < this.children.size()) {
         ConfigurationNode node = (ConfigurationNode)this.children.get(i++);
         writeNode(doc, top, node);
      }

      return doc.getXML();
   }

   public String toJSON() throws ManifoldCFException {
      JSONWriter writer = new JSONWriter();
      writer.startObject();
      Map<String, List<ConfigurationNode>> childMap = new HashMap();
      List<String> childList = new ArrayList();
      String lastChildType = null;
      boolean needAlternate = false;

      String key;
      for(int i = 0; i < this.getChildCount(); lastChildType = key) {
         ConfigurationNode child = this.findChild(i++);
         key = child.getType();
         List<ConfigurationNode> list = (List)childMap.get(key);
         if (list == null) {
            list = new ArrayList();
            childMap.put(key, list);
            childList.add(key);
         } else if (!lastChildType.equals(key)) {
            needAlternate = true;
            break;
         }

         list.add(child);
      }

      if (needAlternate) {
         writer.key("_children_");
         writer.startArray();
         int var11 = 0;

         while(var11 < this.getChildCount()) {
            ConfigurationNode child = this.findChild(var11++);
            writeNode(writer, child, false, true);
         }

         writer.endArray();
      } else {
         int q = 0;

         while(q < childList.size()) {
            key = (String)childList.get(q++);
            List<ConfigurationNode> list = (List)childMap.get(key);
            if (list.size() > 1) {
               writer.key(key);
               writer.startArray();
               int var12 = 0;

               while(var12 < list.size()) {
                  ConfigurationNode child = (ConfigurationNode)list.get(var12++);
                  writeNode(writer, child, false, false);
               }

               writer.endArray();
            } else {
               writeNode(writer, (ConfigurationNode)list.get(0), true, false);
            }
         }
      }

      writer.endObject();
      return writer.toString();
   }

   protected static void writeNode(XMLDoc doc, Object parent, ConfigurationNode node) throws ManifoldCFException {
      String type = node.getType();
      String value = node.getValue();
      Object o = doc.createElement(parent, type);
      Iterator iter = node.getAttributes();

      while(iter.hasNext()) {
         String attribute = (String)iter.next();
         String attrValue = node.getAttributeValue(attribute);
         doc.setAttribute(o, attribute, attrValue);
      }

      if (value != null) {
         doc.createText(o, value);
      }

      int i = 0;

      while(i < node.getChildCount()) {
         ConfigurationNode child = node.findChild(i++);
         writeNode(doc, o, child);
      }

   }

   protected static void writeNode(JSONWriter writer, ConfigurationNode node, boolean writeKey, boolean writeSpecialKey) throws ManifoldCFException {
      if (writeKey) {
         String type = node.getType();
         writer.key(type);
      } else if (writeSpecialKey) {
         writer.startObject();
         writer.key("_type_");
         writer.value(node.getType());
      }

      String value = node.getValue();
      if (value != null && node.getAttributeCount() == 0 && node.getChildCount() == 0) {
         writer.value(value);
      } else {
         if (!writeSpecialKey) {
            writer.startObject();
         }

         if (value != null) {
            writer.key("_value_");
            writer.value(value);
         }

         Iterator<String> iter = node.getAttributes();

         while(iter.hasNext()) {
            String attribute = (String)iter.next();
            String attrValue = node.getAttributeValue(attribute);
            writer.key("_attribute_" + attribute);
            writer.value(attrValue);
         }

         Map<String, List<ConfigurationNode>> childMap = new HashMap();
         List<String> childList = new ArrayList();
         String lastChildType = null;
         boolean needAlternate = false;

         String key;
         for(int i = 0; i < node.getChildCount(); lastChildType = key) {
            ConfigurationNode child = node.findChild(i++);
            key = child.getType();
            List<ConfigurationNode> list = (List)childMap.get(key);
            if (list == null) {
               list = new ArrayList();
               childMap.put(key, list);
               childList.add(key);
            } else if (!lastChildType.equals(key)) {
               needAlternate = true;
               break;
            }

            list.add(child);
         }

         if (needAlternate) {
            writer.key("_children_");
            writer.startArray();
            int var19 = 0;

            while(var19 < node.getChildCount()) {
               ConfigurationNode child = node.findChild(var19++);
               writeNode(writer, child, false, true);
            }

            writer.endArray();
         } else {
            int q = 0;

            while(q < childList.size()) {
               key = (String)childList.get(q++);
               List<ConfigurationNode> list = (List)childMap.get(key);
               if (list.size() > 1) {
                  writer.key(key);
                  writer.startArray();
                  int var18 = 0;

                  while(var18 < list.size()) {
                     ConfigurationNode child = (ConfigurationNode)list.get(var18++);
                     writeNode(writer, child, false, false);
                  }

                  writer.endArray();
               } else {
                  writeNode(writer, (ConfigurationNode)list.get(0), true, false);
               }
            }
         }

         if (!writeSpecialKey) {
            writer.endObject();
         }
      }

      if (writeSpecialKey) {
         writer.endObject();
      }

   }

   public void fromXML(String xml) throws ManifoldCFException {
      XMLDoc doc = new XMLDoc(xml);
      this.initializeFromDoc(doc);
   }

   public void fromJSON(String json) throws ManifoldCFException {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         this.clearChildren();
         JSONReader object = new JSONReader(json);
         object.startObject();
         Iterator<String> iter = object.getKeys();

         while(iter.hasNext()) {
            String key = (String)iter.next();
            object.valueForKey(key);
            if (object.isArray()) {
               object.startArray();

               while(object.nextElement()) {
                  this.processObject(key, object);
               }

               object.endArray();
            } else {
               this.processObject(key, object);
            }
         }

         object.endObject();
      }
   }

   protected void processObject(String key, JSONReader x) throws ManifoldCFException {
      if (x.isObject()) {
         ConfigurationNode cn = this.readNode(key, x);
         this.addChild(this.getChildCount(), cn);
      } else if (!x.isNull()) {
         if (key.equals("_children_")) {
            if (!x.isArray()) {
               throw new ManifoldCFException("Expected array contents for '_children_' node");
            }

            x.startArray();

            while(x.nextElement()) {
               ConfigurationNode nestedCn = this.readNode((String)null, x);
               this.addChild(this.getChildCount(), nestedCn);
            }

            x.endArray();
         } else {
            String value = x.readValue();
            ConfigurationNode cn = this.createNewNode(key);
            cn.setValue(value);
            this.addChild(this.getChildCount(), cn);
         }
      }

   }

   protected ConfigurationNode readNode(String key, JSONReader object) throws ManifoldCFException {
      object.startObject();
      if (object.valueForKey("_type_")) {
         if (!object.isValue()) {
            throw new ManifoldCFException("JSON_TYPE key does not have a string value");
         }

         key = object.readValue();
      }

      if (key == null) {
         throw new ManifoldCFException("No type found for node");
      } else {
         ConfigurationNode rval = this.createNewNode(key);
         Iterator<String> iter = object.getKeys();

         while(iter.hasNext()) {
            String nestedKey = (String)iter.next();
            if (!nestedKey.equals("_type_")) {
               object.valueForKey(nestedKey);
               if (object.isArray()) {
                  object.startArray();

                  while(object.nextElement()) {
                     this.processObject(rval, nestedKey, object);
                  }

                  object.endArray();
               } else {
                  this.processObject(rval, nestedKey, object);
               }
            }
         }

         object.endObject();
         return rval;
      }
   }

   protected void processObject(ConfigurationNode cn, String key, JSONReader x) throws ManifoldCFException {
      if (x.isObject()) {
         ConfigurationNode nestedCn = this.readNode(key, x);
         cn.addChild(cn.getChildCount(), nestedCn);
      } else if (!x.isNull()) {
         String value = x.readValue();
         if (key.startsWith("_attribute_")) {
            cn.setAttribute(key.substring("_attribute_".length()), value);
         } else if (key.equals("_value_")) {
            cn.setValue(value);
         } else if (key.equals("_children_")) {
            if (!x.isArray()) {
               throw new ManifoldCFException("Expected array contents for '_children_' node");
            }

            x.startArray();

            while(x.nextElement()) {
               ConfigurationNode nestedCn = this.readNode((String)null, x);
               cn.addChild(cn.getChildCount(), nestedCn);
            }

            x.endArray();
         } else {
            ConfigurationNode nestedCn = this.createNewNode(key);
            nestedCn.setValue(value);
            cn.addChild(cn.getChildCount(), nestedCn);
         }
      }

   }

   public void fromXML(InputStream xmlstream) throws ManifoldCFException {
      XMLDoc doc = new XMLDoc(xmlstream);
      this.initializeFromDoc(doc);
   }

   protected void initializeFromDoc(XMLDoc doc) throws ManifoldCFException {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         this.clearChildren();
         ArrayList list = new ArrayList();
         doc.processPath(list, "*", (Object)null);
         if (list.size() != 1) {
            throw new ManifoldCFException("Bad xml - missing outer '" + this.rootNodeLabel + "' node - there are " + Integer.toString(list.size()) + " nodes");
         } else {
            Object parent = list.get(0);
            if (!doc.getNodeName(parent).equals(this.rootNodeLabel)) {
               throw new ManifoldCFException("Bad xml - outer node is not '" + this.rootNodeLabel + "'");
            } else {
               list.clear();
               doc.processPath(list, "*", parent);
               int i = 0;

               while(i < list.size()) {
                  Object o = list.get(i++);
                  ConfigurationNode node = this.readNode(doc, o);
                  this.addChild(this.getChildCount(), node);
               }

            }
         }
      }
   }

   protected ConfigurationNode readNode(XMLDoc doc, Object object) throws ManifoldCFException {
      String type = doc.getNodeName(object);
      ConfigurationNode rval = this.createNewNode(type);
      String value = doc.getData(object);
      rval.setValue(value);
      ArrayList list = doc.getAttributes(object);
      int i = 0;

      while(i < list.size()) {
         String attribute = (String)list.get(i++);
         String attrValue = doc.getValue(object, attribute);
         rval.setAttribute(attribute, attrValue);
      }

      list.clear();
      doc.processPath(list, "*", object);
      i = 0;

      while(i < list.size()) {
         Object o = list.get(i);
         ConfigurationNode node = this.readNode(doc, o);
         rval.addChild(i++, node);
      }

      return rval;
   }

   public int getChildCount() {
      return this.children.size();
   }

   public ConfigurationNode findChild(int index) {
      return (ConfigurationNode)this.children.get(index);
   }

   public void removeChild(int index) {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         ConfigurationNode node = (ConfigurationNode)this.children.remove(index);
         this.removeOuterNode(node);
      }
   }

   public void addChild(int index, ConfigurationNode child) {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         this.children.add(index, child);
         this.addOuterNode(child);
      }
   }

   public void clearChildren() {
      if (this.readOnly) {
         throw new IllegalStateException("Attempt to change read-only object");
      } else {
         this.children.clear();
         this.clearOuterNodes();
      }
   }

   public int hashCode() {
      int rval = 0;

      for(int i = 0; i < this.children.size(); rval += ((ConfigurationNode)this.children.get(i++)).hashCode()) {
      }

      return rval;
   }

   public boolean equals(Object o) {
      if (!(o instanceof Configuration)) {
         return false;
      } else {
         Configuration p = (Configuration)o;
         if (this.children.size() != p.children.size()) {
            return false;
         } else {
            for(int i = 0; i < this.children.size(); ++i) {
               if (!((ConfigurationNode)this.children.get(i)).equals(p.children.get(i))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
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

      sb.append("]");
      return sb.toString();
   }

   protected static class JSONWriter {
      private final List<JSONObject> objectStack = new ArrayList();
      private final List<String> keyStack = new ArrayList();
      private final List<JSONArray> arrayStack = new ArrayList();
      private JSONObject currentObject = null;
      private JSONArray currentArray = null;
      private String currentKey = null;
      private JSONObject finalObject = null;
      private JSONArray finalArray = null;

      public JSONWriter() {
      }

      public void startObject() {
         this.pushState();
         this.currentObject = new JSONObject();
         this.currentKey = null;
         this.currentArray = null;
      }

      public void key(String key) {
         this.currentKey = key;
      }

      public void value(String value) {
         if (this.currentKey != null) {
            this.currentObject.put(this.currentKey, value);
         } else {
            if (this.currentArray == null) {
               throw new RuntimeException("Naked value found with no context!");
            }

            this.currentArray.add(value);
         }

      }

      public void endObject() {
         JSONObject object = this.currentObject;
         this.popState();
         if (this.currentObject != null) {
            this.currentObject.put(this.currentKey, object);
         } else if (this.currentArray != null) {
            this.currentArray.add(object);
         } else {
            this.finalObject = object;
         }

      }

      public void startArray() {
         this.pushState();
         this.currentObject = null;
         this.currentKey = null;
         this.currentArray = new JSONArray();
      }

      public void endArray() {
         JSONArray array = this.currentArray;
         this.popState();
         if (this.currentObject != null) {
            this.currentObject.put(this.currentKey, array);
         } else if (this.currentArray != null) {
            this.currentArray.add(array);
         } else {
            this.finalArray = array;
         }

      }

      public String toString() {
         if (this.finalObject != null) {
            return this.finalObject.toJSONString();
         } else {
            return this.finalArray != null ? this.finalArray.toJSONString() : "";
         }
      }

      protected void pushState() {
         this.objectStack.add(this.currentObject);
         this.keyStack.add(this.currentKey);
         this.arrayStack.add(this.currentArray);
      }

      protected void popState() {
         this.currentObject = (JSONObject)this.objectStack.remove(this.objectStack.size() - 1);
         this.currentKey = (String)this.keyStack.remove(this.keyStack.size() - 1);
         this.currentArray = (JSONArray)this.arrayStack.remove(this.arrayStack.size() - 1);
      }
   }

   protected static class JSONReader {
      private final List<JSONObject> objectStack = new ArrayList();
      private final List<Iterator> arrayStack = new ArrayList();
      private JSONObject currentObject = null;
      private Iterator currentArrayIterator = null;
      private Object next = null;

      public JSONReader(String json) throws ManifoldCFException {
         JSONParser parser = new JSONParser();

         try {
            this.next = parser.parse(new StringReader(json));
         } catch (Exception e) {
            throw new ManifoldCFException("Bad json: " + e.getMessage(), e);
         }
      }

      public boolean isObject() {
         return this.next != null && this.next instanceof JSONObject;
      }

      public void startObject() {
         this.pushState();
         this.currentObject = (JSONObject)this.next;
         this.currentArrayIterator = null;
      }

      public Iterator<String> getKeys() {
         return this.currentObject.keySet().iterator();
      }

      public boolean valueForKey(String key) {
         this.next = this.currentObject.get(key);
         return this.next != null;
      }

      public void endObject() {
         this.popState();
      }

      public boolean isArray() {
         return this.next != null && this.next instanceof JSONArray;
      }

      public void startArray() {
         this.pushState();
         this.currentObject = null;
         this.currentArrayIterator = ((JSONArray)this.next).iterator();
      }

      public boolean nextElement() {
         if (this.currentArrayIterator.hasNext()) {
            this.next = this.currentArrayIterator.next();
            return true;
         } else {
            return false;
         }
      }

      public void endArray() {
         this.popState();
      }

      public boolean isNull() {
         return this.next == null;
      }

      public boolean isValue() {
         return this.next != null && !(this.next instanceof JSONObject) && !(this.next instanceof JSONArray);
      }

      public String readValue() {
         return this.next.toString();
      }

      protected void pushState() {
         this.objectStack.add(this.currentObject);
         this.arrayStack.add(this.currentArrayIterator);
      }

      protected void popState() {
         this.currentObject = (JSONObject)this.objectStack.remove(this.objectStack.size() - 1);
         this.currentArrayIterator = (Iterator)this.arrayStack.remove(this.arrayStack.size() - 1);
      }
   }
}
