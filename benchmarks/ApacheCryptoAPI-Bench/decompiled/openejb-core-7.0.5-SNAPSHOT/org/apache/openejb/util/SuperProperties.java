package org.apache.openejb.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SuperProperties extends Properties {
   private static final String PROP_DTD_NAME = "http://java.sun.com/dtd/properties.dtd";
   private static final String PROP_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>    <!ELEMENT properties (comment?, entry*) >    <!ATTLIST properties version CDATA #FIXED \"1.0\" >    <!ELEMENT comment (#PCDATA) >    <!ELEMENT entry (#PCDATA) >    <!ATTLIST entry key CDATA #REQUIRED >";
   protected LinkedHashMap<Object, Object> properties = new LinkedHashMap();
   protected LinkedHashMap<String, String> comments = new LinkedHashMap();
   protected LinkedHashMap<String, LinkedHashMap<String, String>> attributes = new LinkedHashMap();
   protected Properties defaults;
   protected boolean caseInsensitive;
   protected String keyValueSeparator = "=";
   protected String lineSeparator = JavaSecurityManagers.getSystemProperty("line.separator");
   protected String indent = "";
   protected String commentIndent = " ";
   protected boolean spaceBetweenProperties = true;
   protected boolean spaceAfterComment;
   private DocumentBuilder builder;
   private static final int EOF = -1;
   private static final int LINE_ENDING = -4200;
   private static final int ENCODED_EQUALS = -5000;
   private static final int ENCODED_COLON = -5001;
   private static final int ENCODED_SPACE = -5002;
   private static final int ENCODED_TAB = -5003;
   private static final int ENCODED_NEWLINE = -5004;
   private static final int ENCODED_CARRIAGE_RETURN = -5005;

   public SuperProperties() {
   }

   public SuperProperties(Properties properties) {
      super(properties);
      this.defaults = properties;
   }

   public boolean isCaseInsensitive() {
      return this.caseInsensitive;
   }

   public void setCaseInsensitive(boolean caseInsensitive) {
      this.caseInsensitive = caseInsensitive;
   }

   public SuperProperties caseInsensitive(boolean caseInsensitive) {
      this.setCaseInsensitive(caseInsensitive);
      return this;
   }

   public String getKeyValueSeparator() {
      return this.keyValueSeparator;
   }

   public void setKeyValueSeparator(String keyValueSeparator) {
      if (keyValueSeparator == null) {
         throw new NullPointerException("keyValueSeparator is null");
      } else if (keyValueSeparator.length() == 0) {
         throw new NullPointerException("keyValueSeparator is empty");
      } else {
         this.keyValueSeparator = keyValueSeparator;
      }
   }

   public String getLineSeparator() {
      return this.lineSeparator;
   }

   public void setLineSeparator(String lineSeparator) {
      if (lineSeparator == null) {
         throw new NullPointerException("lineSeparator is null");
      } else if (lineSeparator.length() == 0) {
         throw new NullPointerException("lineSeparator is empty");
      } else {
         this.lineSeparator = lineSeparator;
      }
   }

   public int getIndent() {
      return this.indent.length();
   }

   public void setIndent(int indent) {
      char[] chars = new char[indent];
      Arrays.fill(chars, ' ');
      this.indent = new String(chars);
   }

   public int getCommentIndent() {
      return this.commentIndent.length();
   }

   public void setCommentIndent(int commentIndent) {
      char[] chars = new char[commentIndent];
      Arrays.fill(chars, ' ');
      this.commentIndent = new String(chars);
   }

   public boolean isSpaceBetweenProperties() {
      return this.spaceBetweenProperties;
   }

   public void setSpaceBetweenProperties(boolean spaceBetweenProperties) {
      this.spaceBetweenProperties = spaceBetweenProperties;
   }

   public boolean isSpaceAfterComment() {
      return this.spaceAfterComment;
   }

   public void setSpaceAfterComment(boolean spaceAfterComment) {
      this.spaceAfterComment = spaceAfterComment;
   }

   public String getProperty(String name) {
      Object result = this.get(name);
      String property = result instanceof String ? (String)result : null;
      if (property == null && this.defaults != null) {
         property = this.defaults.getProperty(name);
      }

      return property;
   }

   public String getProperty(String name, String defaultValue) {
      Object result = this.get(name);
      String property = result instanceof String ? (String)result : null;
      if (property == null && this.defaults != null) {
         property = this.defaults.getProperty(name);
      }

      return property == null ? defaultValue : property;
   }

   public synchronized Object setProperty(String name, String value) {
      return this.put(name, value);
   }

   public String getComment(String name) {
      name = this.normalize(name);
      String comment = (String)this.comments.get(name);
      if (comment == null && this.defaults instanceof SuperProperties) {
         comment = ((SuperProperties)this.defaults).getComment(name);
      }

      return comment;
   }

   public void setComment(String name, String comment) {
      if (name == null) {
         throw new NullPointerException("name is null");
      } else if (comment == null) {
         throw new NullPointerException("comment is null");
      } else {
         name = this.normalize(name);
         this.comments.put(name, comment);
      }
   }

   public Map<String, String> getAttributes(String name) {
      if (name == null) {
         throw new NullPointerException("name is null");
      } else {
         name = this.normalize(name);
         Map<String, String> attributes = (Map)this.attributes.get(name);
         if (attributes == null && this.defaults instanceof SuperProperties) {
            attributes = ((SuperProperties)this.defaults).getAttributes(name);
         }

         return attributes;
      }
   }

   public void list(PrintStream out) {
      if (out == null) {
         throw new NullPointerException();
      } else {
         StringBuilder buffer = new StringBuilder(80);
         Enumeration<?> keys = this.propertyNames();

         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            buffer.append(key);
            buffer.append('=');
            String property = (String)this.get(key);
            if (property == null) {
               property = this.defaults.getProperty(key);
            }

            if (property.length() > 40) {
               buffer.append(property.substring(0, 37));
               buffer.append("...");
            } else {
               buffer.append(property);
            }

            out.println(buffer.toString());
            buffer.setLength(0);
         }

      }
   }

   public void list(PrintWriter writer) {
      if (writer == null) {
         throw new NullPointerException();
      } else {
         StringBuilder buffer = new StringBuilder(80);
         Enumeration<?> keys = this.propertyNames();

         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            buffer.append(key);
            buffer.append('=');

            String property;
            for(property = (String)this.get(key); property == null; property = this.defaults.getProperty(key)) {
            }

            if (property.length() > 40) {
               buffer.append(property.substring(0, 37));
               buffer.append("...");
            } else {
               buffer.append(property);
            }

            writer.println(buffer.toString());
            buffer.setLength(0);
         }

      }
   }

   public synchronized void load(InputStream in) throws IOException {
      StringBuilder key = new StringBuilder();
      StringBuilder value = null;
      StringBuilder comment = new StringBuilder();
      LinkedHashMap<String, String> attributes = new LinkedHashMap();
      int indent = 0;
      boolean globalIndentSet = false;
      int commentIndent = -1;
      boolean globalCommentIndentSet = false;
      boolean inSeparator = false;

      while(true) {
         int nextByte = this.decodeNextCharacter(in);
         if (nextByte == -1) {
            if (key.length() > 0) {
               this.put(key.toString(), value == null ? "" : value.toString());
               if (comment.length() > 0) {
                  this.setComment(key.toString(), comment.toString());
               }

               this.attributes.put(this.normalize(key.toString()), attributes);
               if (!globalIndentSet) {
                  this.setIndent(indent);
               }
            }

            return;
         }

         char nextChar = (char)(nextByte & 255);
         switch (nextByte) {
            case -4200:
               if (key.length() > 0) {
                  this.put(key.toString(), value == null ? "" : value.toString());
                  if (comment.length() > 0) {
                     this.setComment(key.toString(), comment.toString());
                     comment = new StringBuilder();
                  }

                  this.attributes.put(this.normalize(key.toString()), attributes);
                  attributes = new LinkedHashMap();
                  if (!globalIndentSet) {
                     this.setIndent(indent);
                     globalIndentSet = true;
                  }

                  indent = 0;
               }

               key = new StringBuilder();
               value = null;
               continue;
            case 9:
            case 32:
               if (key.length() > 0 && value == null) {
                  inSeparator = true;
                  value = new StringBuilder();
                  continue;
               }
               break;
            case 33:
            case 35:
               if (key.length() == 0) {
                  if (!globalIndentSet) {
                     this.setIndent(indent);
                     globalIndentSet = true;
                  }

                  indent = 0;
                  StringBuilder commentLine = new StringBuilder();
                  int commentLineIndent = 0;
                  boolean inIndent = true;

                  while(true) {
                     nextByte = in.read();
                     if (nextByte < 0) {
                        break;
                     }

                     nextChar = (char)nextByte;
                     if (inIndent && nextChar == ' ') {
                        ++commentLineIndent;
                        commentLine.append(' ');
                     } else if (inIndent && nextChar == '\t') {
                        commentLineIndent += 4;
                        commentLine.append("    ");
                     } else {
                        if (nextChar == '\r' || nextChar == '\n') {
                           break;
                        }

                        inIndent = false;
                        commentLine.append(nextChar);
                     }
                  }

                  if (comment.length() == 0) {
                     commentIndent = commentLineIndent;
                     if (!globalCommentIndentSet) {
                        this.setCommentIndent(commentLineIndent);
                        globalCommentIndentSet = true;
                     }
                  }

                  commentLineIndent = Math.min(commentIndent, commentLineIndent);
                  if (commentLine.toString().trim().startsWith("@")) {
                     String attribute = commentLine.toString().trim().substring(1);
                     String[] parts = attribute.split("=", 2);
                     String attributeName = parts[0].trim();
                     String attributeValue = parts.length == 2 ? parts[1].trim() : "";
                     attributes.put(attributeName, attributeValue);
                  } else {
                     if (comment.length() != 0) {
                        comment.append(this.lineSeparator);
                     }

                     comment.append(commentLine.toString().substring(commentLineIndent));
                  }
                  continue;
               }
               break;
            case 58:
            case 61:
               if (inSeparator) {
                  inSeparator = false;
                  continue;
               }

               if (value == null) {
                  value = new StringBuilder();
                  continue;
               }
         }

         if (nextByte >= 0 && Character.isWhitespace(nextChar)) {
            if (key.length() == 0) {
               if (nextChar == '\t') {
                  indent += 4;
               } else {
                  ++indent;
               }
            }

            if (key.length() == 0 || value == null || value.length() == 0) {
               continue;
            }
         }

         switch (nextByte) {
            case -5005:
               nextChar = '\r';
               break;
            case -5004:
               nextChar = '\n';
               break;
            case -5003:
               nextChar = '\t';
               break;
            case -5002:
               nextChar = ' ';
               break;
            case -5001:
               nextChar = ':';
               break;
            case -5000:
               nextChar = '=';
         }

         inSeparator = false;
         if (value == null) {
            key.append(nextChar);
         } else {
            value.append(nextChar);
         }
      }
   }

   private int decodeNextCharacter(InputStream in) throws IOException {
      boolean lineContinuation = false;
      boolean carriageReturnLineContinuation = false;
      boolean encoded = false;

      while(true) {
         char nextChar;
         do {
            int nextByte = in.read();
            if (nextByte < 0) {
               return -1;
            }

            nextChar = (char)(nextByte & 255);
            if (!carriageReturnLineContinuation) {
               break;
            }

            carriageReturnLineContinuation = false;
         } while(nextChar == '\n');

         if (nextChar == '\\') {
            int var7 = in.read();
            if (var7 < 0) {
               nextChar = '\u0000';
            } else {
               nextChar = (char)(var7 & 255);
            }

            switch (nextChar) {
               case '\r':
                  carriageReturnLineContinuation = true;
               case '\n':
                  lineContinuation = true;
                  continue;
               case 'u':
                  nextChar = this.readUnicode(in);
                  break;
               default:
                  encoded = true;
                  nextChar = this.decodeEscapeChar(nextChar);
            }
         } else if (nextChar == '\n' || nextChar == '\r') {
            return -4200;
         }

         if (!lineContinuation || nextChar != ' ' && nextChar != '\t') {
            if (encoded) {
               switch (nextChar) {
                  case '\t':
                     return -5003;
                  case '\n':
                     return -5004;
                  case '\r':
                     return -5005;
                  case ' ':
                     return -5002;
                  case ':':
                     return -5001;
                  case '=':
                     return -5000;
               }
            }

            return nextChar;
         }
      }
   }

   private char decodeEscapeChar(char nextChar) {
      switch (nextChar) {
         case 'b':
            return '\b';
         case 'c':
         case 'd':
         case 'e':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'l':
         case 'm':
         case 'o':
         case 'p':
         case 'q':
         case 's':
         default:
            return nextChar;
         case 'f':
            return '\f';
         case 'n':
            return '\n';
         case 'r':
            return '\r';
         case 't':
            return '\t';
         case 'u':
            throw new IllegalArgumentException("decodeEscapeChar can not decode an unicode sequence");
      }
   }

   private char readUnicode(InputStream in) throws IOException {
      char[] buf = new char[4];
      int unicode = 0;

      for(int i = 0; i < buf.length; ++i) {
         int nextByte = in.read();
         if (nextByte < 0) {
            throw new IllegalArgumentException("Invalid unicode sequence: expected format \\uxxxx, but got \\u" + new String(buf, 0, i));
         }

         char nextChar = (char)(nextByte & 255);
         buf[i] = nextChar;
         int nextDigit = Character.digit(nextChar, 16);
         if (nextDigit < 0) {
            throw new IllegalArgumentException("Illegal character " + nextChar + " in unicode sequence \\u" + new String(buf, 0, i + 1));
         }

         unicode = (unicode << 4) + nextDigit;
      }

      return (char)unicode;
   }

   public Enumeration<?> propertyNames() {
      if (this.defaults == null) {
         return this.keys();
      } else {
         Hashtable<Object, Object> set = new Hashtable(this.defaults.size() + this.size());
         Enumeration<?> keys = this.defaults.propertyNames();

         while(keys.hasMoreElements()) {
            set.put(keys.nextElement(), set);
         }

         keys = this.keys();

         while(keys.hasMoreElements()) {
            set.put(keys.nextElement(), set);
         }

         return set.keys();
      }
   }

   public void save(OutputStream out, String comment) {
      try {
         this.store(out, comment);
      } catch (IOException var4) {
      }

   }

   public synchronized void store(OutputStream out, String headComment) throws IOException {
      OutputStreamWriter writer = new OutputStreamWriter(out, "ISO8859_1");
      if (headComment != null) {
         writer.write(this.indent);
         writer.write("#");
         writer.write(this.commentIndent);
         writer.write(headComment);
         writer.write(this.lineSeparator);
      }

      boolean firstProperty = true;
      StringBuilder buffer = new StringBuilder(200);

      for(Map.Entry<Object, Object> entry : this.entrySet()) {
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         if (!firstProperty && this.spaceBetweenProperties) {
            buffer.append(this.lineSeparator);
         }

         String comment = (String)this.comments.get(key);
         Map<String, String> attributes = (Map)this.attributes.get(key);
         if (comment != null || !attributes.isEmpty()) {
            this.dumpComment(buffer, comment, attributes, "#");
            if (this.spaceAfterComment) {
               buffer.append(this.lineSeparator);
            }
         }

         buffer.append(this.indent);
         this.dumpString(buffer, key, true);
         if (value != null && value.length() > 0) {
            buffer.append(this.keyValueSeparator);
            this.dumpString(buffer, value, false);
         }

         buffer.append(this.lineSeparator);
         writer.write(buffer.toString());
         buffer.setLength(0);
         firstProperty = false;
      }

      writer.flush();
   }

   private void dumpString(StringBuilder buffer, String string, boolean key) {
      int i = 0;
      if (!key && i < string.length() && string.charAt(i) == ' ') {
         buffer.append("\\ ");
         ++i;
      }

      for(; i < string.length(); ++i) {
         char ch = string.charAt(i);
         switch (ch) {
            case '\t':
               buffer.append("\\t");
               break;
            case '\n':
               buffer.append("\\n");
               break;
            case '\u000b':
            default:
               if ("\\".indexOf(ch) >= 0 || key && "#!=: ".indexOf(ch) >= 0) {
                  buffer.append('\\');
               }

               if (ch >= ' ' && ch <= '~') {
                  buffer.append(ch);
               } else {
                  String hex = Integer.toHexString(ch);
                  buffer.append("\\u");

                  for(int j = 0; j < 4 - hex.length(); ++j) {
                     buffer.append("0");
                  }

                  buffer.append(hex);
               }
               break;
            case '\f':
               buffer.append("\\f");
               break;
            case '\r':
               buffer.append("\\r");
         }
      }

   }

   private void dumpComment(StringBuilder buffer, String comment, Map<String, String> attributes, String commentToken) {
      if (comment != null) {
         boolean startOfLine = true;
         char ch = 0;

         for(int i = 0; i < comment.length(); ++i) {
            ch = comment.charAt(i);
            if (startOfLine) {
               buffer.append(this.indent);
               buffer.append(commentToken);
               buffer.append(this.commentIndent);
               startOfLine = false;
            }

            switch (ch) {
               case '\n':
                  buffer.append(this.lineSeparator);
                  startOfLine = true;
                  break;
               case '\r':
                  if (i + 1 < comment.length() && comment.charAt(i + 1) != '\n') {
                     buffer.append(this.lineSeparator);
                     startOfLine = true;
                  }
                  break;
               default:
                  buffer.append(ch);
            }
         }

         if (ch != '\r' && ch != '\n') {
            buffer.append(this.lineSeparator);
         }
      }

      for(Map.Entry<String, String> entry : attributes.entrySet()) {
         buffer.append(this.indent);
         buffer.append("#");
         buffer.append(this.commentIndent);
         buffer.append("@");
         buffer.append((String)entry.getKey());
         if (entry.getValue() != null && ((String)entry.getValue()).length() > 0) {
            buffer.append("=");
            buffer.append((String)entry.getValue());
         }

         buffer.append(this.lineSeparator);
      }

   }

   public synchronized void loadFromXML(InputStream in) throws IOException {
      if (in == null) {
         throw new NullPointerException();
      } else {
         DocumentBuilder builder = this.getDocumentBuilder();

         try {
            Document doc = builder.parse(in);
            NodeList entries = doc.getElementsByTagName("entry");
            if (entries != null) {
               int entriesListLength = entries.getLength();

               for(int i = 0; i < entriesListLength; ++i) {
                  Element entry = (Element)entries.item(i);
                  String key = entry.getAttribute("key");
                  String value = entry.getTextContent();
                  this.put(key, value);

                  for(Node node = entry.getPreviousSibling(); node != null && !(node instanceof Element); node = node.getPreviousSibling()) {
                     if (node instanceof Comment) {
                        InputStream cin = new ByteArrayInputStream(((Comment)node).getData().getBytes());
                        StringBuilder comment = new StringBuilder();
                        LinkedHashMap<String, String> attributes = new LinkedHashMap();
                        boolean firstLine = true;
                        int commentIndent = Integer.MAX_VALUE;

                        int nextByte;
                        do {
                           StringBuilder commentLine = new StringBuilder();
                           int commentLineIndent = 0;
                           boolean inIndent = true;

                           while(true) {
                              nextByte = cin.read();
                              if (nextByte < 0) {
                                 break;
                              }

                              char nextChar = (char)nextByte;
                              if (inIndent && nextChar == ' ') {
                                 ++commentLineIndent;
                                 commentLine.append(' ');
                              } else if (inIndent && nextChar == '\t') {
                                 commentLineIndent += 4;
                                 commentLine.append("    ");
                              } else {
                                 if (nextChar == '\r' || nextChar == '\n') {
                                    break;
                                 }

                                 inIndent = false;
                                 commentLine.append(nextChar);
                              }
                           }

                           if (!firstLine && commentIndent == Integer.MAX_VALUE && commentLine.length() > 0) {
                              commentIndent = commentLineIndent;
                           }

                           commentLineIndent = Math.min(commentIndent, commentLineIndent);
                           if (commentLine.toString().trim().startsWith("@")) {
                              String attribute = commentLine.toString().trim().substring(1);
                              String[] parts = attribute.split("=", 2);
                              String attributeName = parts[0].trim();
                              String attributeValue = parts.length == 2 ? parts[1].trim() : "";
                              attributes.put(attributeName, attributeValue);
                           } else {
                              if (comment.length() != 0) {
                                 comment.append(this.lineSeparator);
                              }

                              comment.append(commentLine.toString().substring(commentLineIndent));
                           }

                           firstLine = false;
                        } while(nextByte > 0);

                        if (comment.length() > 0) {
                           this.setComment(key, comment.toString());
                        }

                        this.attributes.put(this.normalize(key), attributes);
                        break;
                     }
                  }
               }

            }
         } catch (SAXException e) {
            throw new InvalidPropertiesFormatException(e);
         }
      }
   }

   private DocumentBuilder getDocumentBuilder() {
      if (this.builder == null) {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setValidating(true);

         try {
            this.builder = factory.newDocumentBuilder();
         } catch (ParserConfigurationException e) {
            throw new Error(e);
         }

         this.builder.setErrorHandler(new ErrorHandler() {
            public void warning(SAXParseException e) throws SAXException {
               throw e;
            }

            public void error(SAXParseException e) throws SAXException {
               throw e;
            }

            public void fatalError(SAXParseException e) throws SAXException {
               throw e;
            }
         });
         this.builder.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
               if (systemId.equals("http://java.sun.com/dtd/properties.dtd")) {
                  InputSource result = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>    <!ELEMENT properties (comment?, entry*) >    <!ATTLIST properties version CDATA #FIXED \"1.0\" >    <!ELEMENT comment (#PCDATA) >    <!ELEMENT entry (#PCDATA) >    <!ATTLIST entry key CDATA #REQUIRED >"));
                  result.setSystemId("http://java.sun.com/dtd/properties.dtd");
                  return result;
               } else {
                  throw new SAXException("Invalid DOCTYPE declaration: " + systemId);
               }
            }
         });
      }

      return this.builder;
   }

   public void storeToXML(OutputStream os, String comment) throws IOException {
      this.storeToXML(os, comment, "UTF-8");
   }

   public synchronized void storeToXML(OutputStream os, String headComment, String encoding) throws IOException {
      if (os != null && encoding != null) {
         String encodingCanonicalName = "UTF-8";
         OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
         StringBuilder buf = new StringBuilder(200);
         buf.append("<?xml version=\"1.0\" encoding=\"").append("UTF-8").append("\"?>").append(this.lineSeparator);
         buf.append("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">").append(this.lineSeparator);
         buf.append("<properties>").append(this.lineSeparator);
         if (headComment != null) {
            buf.append(this.indent);
            buf.append("<comment>");
            buf.append(this.substitutePredefinedEntries(headComment));
            buf.append("</comment>");
            buf.append(this.lineSeparator);
            if (!this.isEmpty() && (this.spaceBetweenProperties || this.spaceAfterComment)) {
               buf.append(this.lineSeparator);
            }
         }

         boolean firstProperty = true;

         for(Map.Entry<Object, Object> entry : this.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (!firstProperty && this.spaceBetweenProperties) {
               buf.append(this.lineSeparator);
            }

            String comment = (String)this.comments.get(key);
            Map<String, String> attributes = (Map)this.attributes.get(key);
            if (comment != null || !attributes.isEmpty()) {
               buf.append(this.indent);
               buf.append("<!--");
               buf.append(this.lineSeparator);
               comment = comment.replaceAll("--*", "-");
               this.dumpComment(buf, comment, attributes, "");
               buf.append(this.indent);
               buf.append("-->");
               buf.append(this.lineSeparator);
               if (this.spaceAfterComment) {
                  buf.append(this.lineSeparator);
               }
            }

            buf.append(this.indent);
            buf.append("<entry key=\"");
            buf.append(this.substitutePredefinedEntries(key));
            buf.append("\">");
            buf.append(this.substitutePredefinedEntries(value));
            buf.append("</entry>");
            buf.append(this.lineSeparator);
            firstProperty = false;
         }

         buf.append("</properties>").append(this.lineSeparator);
         osw.write(buf.toString());
         osw.flush();
      } else {
         throw new NullPointerException();
      }
   }

   private String substitutePredefinedEntries(String s) {
      return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
   }

   public boolean isEmpty() {
      return this.properties.isEmpty();
   }

   public int size() {
      return this.properties.size();
   }

   public Object get(Object key) {
      key = this.normalize(key);
      return this.properties.get(key);
   }

   public Object put(Object key, Object value) {
      key = this.normalize(key);
      if (key instanceof String) {
         String name = (String)key;
         if (!this.attributes.containsKey(name)) {
            this.attributes.put(name, new LinkedHashMap());
         }
      }

      return this.properties.put(key, value);
   }

   public Object remove(Object key) {
      key = this.normalize(key);
      this.comments.remove(key);
      this.attributes.remove(key);
      return this.properties.remove(key);
   }

   public void putAll(Map<?, ?> t) {
      for(Map.Entry<?, ?> entry : t.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

      if (t instanceof SuperProperties) {
         SuperProperties superProperties = (SuperProperties)t;

         for(Map.Entry<String, String> entry : superProperties.comments.entrySet()) {
            this.comments.put(this.normalize((String)entry.getKey()), entry.getValue());
         }

         for(Map.Entry<String, LinkedHashMap<String, String>> entry : superProperties.attributes.entrySet()) {
            this.attributes.put(this.normalize((String)entry.getKey()), entry.getValue());
         }
      }

   }

   public Set<Object> keySet() {
      return Collections.unmodifiableSet(this.properties.keySet());
   }

   public Enumeration<Object> keys() {
      return Collections.enumeration(this.properties.keySet());
   }

   public Collection<Object> values() {
      return Collections.unmodifiableCollection(this.properties.values());
   }

   public Set<Map.Entry<Object, Object>> entrySet() {
      return Collections.unmodifiableSet(this.properties.entrySet());
   }

   public Enumeration<Object> elements() {
      return Collections.enumeration(this.properties.values());
   }

   public boolean containsKey(Object key) {
      key = this.normalize(key);
      return this.properties.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.properties.containsValue(value);
   }

   public boolean contains(Object value) {
      return this.properties.containsValue(value);
   }

   public void clear() {
      this.properties.clear();
      this.comments.clear();
      this.attributes.clear();
   }

   public Object clone() {
      SuperProperties clone = (SuperProperties)super.clone();
      clone.properties = (LinkedHashMap)this.properties.clone();
      clone.comments = (LinkedHashMap)this.comments.clone();
      clone.attributes = (LinkedHashMap)this.attributes.clone();

      for(Map.Entry<String, LinkedHashMap<String, String>> entry : clone.attributes.entrySet()) {
         entry.setValue((LinkedHashMap)((LinkedHashMap)entry.getValue()).clone());
      }

      return clone;
   }

   public boolean equals(Object o) {
      return this.properties.equals(o);
   }

   public int hashCode() {
      return this.properties.hashCode();
   }

   public String toString() {
      return this.properties.toString();
   }

   protected void rehash() {
   }

   private Object normalize(Object key) {
      return key instanceof String ? this.normalize((String)key) : key;
   }

   private String normalize(String property) {
      if (!this.caseInsensitive) {
         return property;
      } else if (super.containsKey(property)) {
         return property;
      } else {
         for(Object o : this.keySet()) {
            if (o instanceof String) {
               String key = (String)o;
               if (key.equalsIgnoreCase(property)) {
                  return key;
               }
            }
         }

         if (this.defaults != null) {
            for(Object o : this.defaults.keySet()) {
               if (o instanceof String) {
                  String key = (String)o;
                  if (key.equalsIgnoreCase(property)) {
                     return key;
                  }
               }
            }
         }

         return property;
      }
   }
}
