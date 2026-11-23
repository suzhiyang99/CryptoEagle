package org.apache.manifoldcf.core.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.Logging;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLDoc {
   public static final String _rcsid = "@(#)$Id: XMLDoc.java 988245 2010-08-23 18:39:35Z kwright $";
   private static final String _wildchar = "*";
   private static final String _slash = "/";
   private static int _blocksiz = 1024;
   private Document _doc = null;

   protected Object getDocument() {
      return this._doc;
   }

   protected void setDocument(Object d) {
      this._doc = (Document)d;
   }

   public ArrayList processPath(String path, Object o) {
      ArrayList l = new ArrayList();
      this.processPath(l, path, o);
      return l;
   }

   public void processPath(ArrayList returnList, String path, Object currentRoot) {
      Object element = currentRoot;
      StringBuilder bf = new StringBuilder();
      boolean bWild = false;
      ArrayList working = new ArrayList();
      if (path.endsWith("/")) {
         path = path + "*";
      }

      StringTokenizer tokenizer = new StringTokenizer(path, "/", false);
      int depth = 0;
      int pathDepth = tokenizer.countTokens();
      String attribute = null;
      String value = null;

      while(tokenizer.hasMoreTokens()) {
         ++depth;
         String s = tokenizer.nextToken().trim();
         if (s != null && s.length() > 0) {
            value = null;
            attribute = null;
            s = s.trim();
            String elementName;
            if (s.indexOf(61) > -1) {
               bWild = true;
               int i = s.indexOf(32);
               elementName = s.substring(0, i);
               s = s.substring(i).trim();
               i = s.indexOf(61);
               attribute = s.substring(0, i);
               value = s.substring(i + 1);
               bf.append('/').append(elementName).append(attribute);
               bf.append('=').append(value);
            } else {
               elementName = s;
               if (s.equals("*")) {
                  elementName = null;
               } else {
                  bf.append("/").append(s);
               }
            }

            ArrayList l = this.getElements(element, elementName);
            element = null;
            if (depth == pathDepth) {
               working.addAll(l);
            } else {
               int i = this.searchArrayForAttribute(l, 0, attribute, value);
               if (i != -1) {
                  element = l.get(i);
               }
            }

            if (element == null) {
               break;
            }
         }
      }

      if (bWild) {
         for(int i = 0; i < working.size(); ++i) {
            int j = this.searchArrayForAttribute(working, i, attribute, value);
            if (j <= -1) {
               break;
            }

            returnList.add(working.get(j));
         }
      } else {
         returnList.addAll(working);
      }

   }

   protected int searchArrayForAttribute(ArrayList l, int i, String attribute, String value) {
      int index;
      for(index = -1; i < l.size(); ++i) {
         Object element = l.get(i);
         if (attribute == null || attribute.length() == 0) {
            index = i;
            break;
         }

         if (value.equals(this.getValue(element, attribute))) {
            index = i;
            break;
         }
      }

      return index;
   }

   public String getXML() throws ManifoldCFException {
      return new String(this.toByteArray(), StandardCharsets.UTF_8);
   }

   public String getXMLNoEntityPreamble() throws ManifoldCFException {
      String initial = this.getXML();
      int index = initial.indexOf(">");
      return initial.substring(index + 1);
   }

   public byte[] toByteArray() throws ManifoldCFException {
      ByteArrayOutputStream os = new ByteArrayOutputStream(_blocksiz);
      this.dumpOutput(os);
      return os.toByteArray();
   }

   public XMLDoc() throws ManifoldCFException {
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         builder.setEntityResolver(new MyEntityResolver());
         this._doc = builder.newDocument();
      } catch (Exception e) {
         throw new ManifoldCFException("Error setting up parser: " + e.getMessage(), e);
      }
   }

   public XMLDoc(String data) throws ManifoldCFException {
      ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
      this._doc = this.init(bis);
   }

   public XMLDoc(StringBuilder data) throws ManifoldCFException {
      ByteArrayInputStream bis = new ByteArrayInputStream(data.toString().getBytes(StandardCharsets.UTF_8));
      this._doc = this.init(bis);
   }

   public XMLDoc(InputStream is) throws ManifoldCFException {
      this._doc = this.init(is);
   }

   public XMLDoc(XMLDoc oldDoc, Object parent) throws ManifoldCFException {
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setValidating(false);
         DocumentBuilder builder = factory.newDocumentBuilder();
         builder.setEntityResolver(new MyEntityResolver());
         this._doc = builder.newDocument();
         NodeList nodes;
         if (parent == null) {
            nodes = oldDoc._doc.getChildNodes();
         } else {
            nodes = ((Node)parent).getChildNodes();
         }

         int sz = nodes.getLength();

         for(int index = 0; index < sz; ++index) {
            Node node = nodes.item(index);
            if (node.getNodeType() == 1) {
               this._doc.appendChild(this.duplicateNode(node));
            }
         }

      } catch (Exception e) {
         throw new ManifoldCFException("Error setting up parser: " + e.getMessage(), e);
      }
   }

   private Document init(InputStream is) throws ManifoldCFException {
      Document doc = null;

      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setValidating(false);
         DocumentBuilder builder = factory.newDocumentBuilder();
         builder.setEntityResolver(new MyEntityResolver());
         doc = builder.parse(is);
         return doc;
      } catch (Exception var8) {
         if (Logging.misc.isDebugEnabled()) {
            StringWriter sw = new StringWriter();

            try {
               is.reset();
               byte[] buf = new byte[65536];
               int len = is.read(buf);
               if (len != -1) {
                  sw.append(new String(buf, 0, len, StandardCharsets.UTF_8));
                  if (len == buf.length) {
                     sw.append("...");
                  }
               }
            } catch (Exception var7) {
            }

            Logging.misc.debug(sw.toString(), var8);
         }

         throw new ManifoldCFException("XML parsing error: " + var8.getMessage(), var8);
      }
   }

   public final String getValue(Object elo, String a) {
      Element el = (Element)elo;
      return el.getAttribute(a);
   }

   public final String getNodeName(Object el) {
      String name = null;
      Node node = (Node)el;
      if (node.getNodeType() == 1) {
         name = node.getNodeName();
      }

      return name;
   }

   public final String getData(Object obj) {
      Node enode = (Node)obj;
      StringBuilder data = new StringBuilder();
      NodeList cdata = enode.getChildNodes();
      int sz = cdata.getLength();

      for(int j = 0; j < sz; ++j) {
         Node node = cdata.item(j);
         if (node.getNodeType() == 3) {
            Text sec = (Text)node;
            sec.normalize();
            data.append(sec.getData().trim());
         } else if (node.getNodeType() == 4) {
            CDATASection sec = (CDATASection)node;
            data.append(sec.getData().trim());
         }
      }

      return data.toString();
   }

   public Object getRoot() {
      return this.getRoot(this._doc);
   }

   public Object getRoot(Object obj) {
      NodeList nodes = ((Document)obj).getChildNodes();
      return nodes.item(0);
   }

   private final ArrayList getElements(Object n) {
      return this.getElements(n, (String)null);
   }

   public final ArrayList getAttributes(Object n) {
      ArrayList atts = new ArrayList();
      NamedNodeMap map = ((Node)n).getAttributes();

      for(int i = 0; i < map.getLength(); ++i) {
         Attr att = (Attr)map.item(i);
         atts.add(att.getName());
      }

      return atts;
   }

   public Object getElement(Object parent, String tagname) {
      ArrayList l = this.getElements(parent, tagname);
      return l.size() < 1 ? null : l.get(0);
   }

   private final ArrayList getElements(Object parent, String tagname) {
      ArrayList list = new ArrayList();
      NodeList nodes = parent == null ? this._doc.getChildNodes() : ((Node)parent).getChildNodes();
      int sz = nodes.getLength();
      ArrayList tags = new ArrayList();
      int tagsz = 0;
      if (tagname != null) {
         StringTokenizer st = new StringTokenizer(tagname, ",");

         while(st.hasMoreTokens()) {
            tags.add(st.nextToken());
         }
      }

      tagsz = tags.size();

      for(int index = 0; index < sz; ++index) {
         Node node = nodes.item(index);
         if (node.getNodeType() == 1) {
            String theTag = node.getNodeName();
            if (tagsz == 0) {
               list.add(node);
            } else {
               for(int j = 0; j < tagsz; ++j) {
                  if (theTag.equalsIgnoreCase((String)tags.get(j))) {
                     list.add(node);
                     break;
                  }
               }
            }
         }
      }

      return list;
   }

   public Object createElement(Object who, String ename) {
      Element element = this._doc.createElement(ename);
      if (who == null) {
         this._doc.appendChild(element);
      } else {
         ((Element)who).appendChild(element);
      }

      return element;
   }

   public void addDocumentElement(Object where, XMLDoc oldDoc, Object parent) {
      NodeList nodes;
      if (parent == null) {
         nodes = oldDoc._doc.getChildNodes();
      } else {
         nodes = ((Node)parent).getChildNodes();
      }

      int sz = nodes.getLength();

      for(int index = 0; index < sz; ++index) {
         Node node = nodes.item(index);
         if (where == null) {
            this._doc.appendChild(this.duplicateNode(node));
         } else {
            ((Element)where).appendChild(this.duplicateNode(node));
         }
      }

   }

   public void setAttribute(Object e, String sName, String sValue) {
      ((Element)e).setAttribute(sName, sValue);
   }

   public Object createText(Object who, String data) {
      Text element = this._doc.createTextNode(data);
      if (who == null) {
         this._doc.appendChild(element);
      } else {
         ((Element)who).appendChild(element);
      }

      return element;
   }

   protected Node duplicateNode(Node node) {
      int type = node.getNodeType();
      Node rval;
      switch (type) {
         case 1:
            rval = this._doc.createElement(node.getNodeName());
            NamedNodeMap nmap = node.getAttributes();
            int i = 0;

            while(i < nmap.getLength()) {
               Attr attribute = (Attr)nmap.item(i++);
               ((Element)rval).setAttribute(attribute.getName(), attribute.getValue());
            }

            NodeList children = node.getChildNodes();
            i = 0;

            while(i < children.getLength()) {
               rval.appendChild(this.duplicateNode(children.item(i++)));
            }
            break;
         case 2:
         case 5:
         case 6:
         case 7:
         default:
            return null;
         case 3:
            rval = this._doc.createTextNode(((Text)node).getData());
            break;
         case 4:
            rval = this._doc.createCDATASection(((CDATASection)node).getNodeValue());
            break;
         case 8:
            rval = this._doc.createComment(((Comment)node).getNodeValue());
      }

      return rval;
   }

   private void dumpOutput(OutputStream os) throws ManifoldCFException {
      try {
         StreamResult res = new StreamResult(os);
         TransformerFactory tFactory = TransformerFactory.newInstance();
         Transformer transformer = tFactory.newTransformer();
         DOMSource source = new DOMSource(this._doc);
         transformer.transform(source, res);
      } catch (Exception e) {
         throw new ManifoldCFException("Error dumping output: " + e.getMessage(), e);
      }
   }

   protected static class MyEntityResolver implements EntityResolver {
      public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
         return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes(StandardCharsets.UTF_8)));
      }
   }
}
