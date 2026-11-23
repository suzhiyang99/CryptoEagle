package org.apache.tika.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.tika.exception.TikaException;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Param<T> implements Serializable {
   private static final Map<Class<?>, String> map = new HashMap();
   private static final Map<String, Class<?>> reverseMap = new HashMap();
   private Class<T> type;
   private String name;
   private String value;
   private T actualValue;

   public Param() {
   }

   public Param(String name, Class<T> type, T value) {
      this.name = name;
      this.type = type;
      this.value = value.toString();
   }

   public Param(String name, T value) {
      this(name, value.getClass(), value);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Class<T> getType() {
      return this.type;
   }

   public void setType(Class<T> type) {
      this.type = type;
   }

   public String getTypeString() {
      if (this.type == null) {
         return null;
      } else {
         return map.containsKey(this.type) ? (String)map.get(this.type) : this.type.getName();
      }
   }

   public void setTypeString(String type) {
      if (type != null && !type.isEmpty()) {
         this.type = classFromType(type);
         this.actualValue = null;
      }
   }

   public T getValue() {
      if (this.actualValue == null) {
         this.actualValue = (T)getTypedValue(this.type, this.value);
      }

      return this.actualValue;
   }

   public String toString() {
      return "Param{name='" + this.name + '\'' + ", value='" + this.value + '\'' + ", actualValue=" + this.actualValue + '}';
   }

   public void save(OutputStream stream) throws TransformerException, TikaException {
      DocumentBuilder builder = XMLReaderUtils.getDocumentBuilder();
      Document doc = builder.newDocument();
      Element paramEl = doc.createElement("param");
      doc.appendChild(paramEl);
      this.save((Node)paramEl);
      Transformer transformer = XMLReaderUtils.getTransformer();
      transformer.transform(new DOMSource(paramEl), new StreamResult(stream));
   }

   public void save(Node node) {
      if (!(node instanceof Element)) {
         throw new IllegalArgumentException("Not an Element : " + node);
      } else {
         Element el = (Element)node;
         el.setAttribute("name", this.getName());
         el.setAttribute("type", this.getTypeString());
         el.setTextContent(this.value);
      }
   }

   public static <T> Param<T> load(InputStream stream) throws SAXException, IOException, TikaException {
      DocumentBuilder db = XMLReaderUtils.getDocumentBuilder();
      Document document = db.parse(stream);
      return load(document.getFirstChild());
   }

   public static <T> Param<T> load(Node node) {
      Node nameAttr = node.getAttributes().getNamedItem("name");
      Node typeAttr = node.getAttributes().getNamedItem("type");
      Node value = node.getFirstChild();
      Param<T> ret = new Param<T>();
      ret.name = nameAttr.getTextContent();
      ret.setTypeString(typeAttr.getTextContent());
      ret.value = value.getTextContent();
      return ret;
   }

   private static <T> Class<T> classFromType(String type) {
      if (reverseMap.containsKey(type)) {
         return (Class)reverseMap.get(type);
      } else {
         try {
            return Class.forName(type);
         } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
         }
      }
   }

   private static <T> T getTypedValue(Class<T> type, String value) {
      try {
         Constructor<T> constructor = type.getConstructor(String.class);
         constructor.setAccessible(true);
         return (T)constructor.newInstance(value);
      } catch (NoSuchMethodException e) {
         throw new RuntimeException(type + " doesnt have a constructor that takes String arg", e);
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }

   static {
      map.put(Boolean.class, "bool");
      map.put(String.class, "string");
      map.put(Byte.class, "byte");
      map.put(Short.class, "short");
      map.put(Integer.class, "int");
      map.put(Long.class, "long");
      map.put(BigInteger.class, "bigint");
      map.put(Float.class, "float");
      map.put(Double.class, "double");
      map.put(File.class, "file");
      map.put(URI.class, "uri");
      map.put(URL.class, "url");

      for(Map.Entry<Class<?>, String> entry : map.entrySet()) {
         reverseMap.put(entry.getValue(), entry.getKey());
      }

   }
}
