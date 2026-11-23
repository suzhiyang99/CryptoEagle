package org.apache.tika.parser.external;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class ExternalParsersConfigReader implements ExternalParsersConfigReaderMetKeys {
   public static List<ExternalParser> read(InputStream stream) throws TikaException, IOException {
      try {
         DocumentBuilder builder = XMLReaderUtils.getDocumentBuilder();
         Document document = builder.parse(new InputSource(stream));
         return read(document);
      } catch (SAXException e) {
         throw new TikaException("Invalid parser configuration", e);
      }
   }

   public static List<ExternalParser> read(Document document) throws TikaException, IOException {
      return read(document.getDocumentElement());
   }

   public static List<ExternalParser> read(Element element) throws TikaException, IOException {
      List<ExternalParser> parsers = new ArrayList();
      if (element != null && element.getTagName().equals("external-parsers")) {
         NodeList nodes = element.getChildNodes();

         for(int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
               Element child = (Element)node;
               if (child.getTagName().equals("parser")) {
                  ExternalParser p = readParser(child);
                  if (p != null) {
                     parsers.add(p);
                  }
               }
            }
         }

         return parsers;
      } else {
         throw new MimeTypeException("Not a <external-parsers/> configuration document: " + element.getTagName());
      }
   }

   private static ExternalParser readParser(Element parserDef) throws TikaException {
      ExternalParser parser = new ExternalParser();
      NodeList children = parserDef.getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         Node node = children.item(i);
         if (node.getNodeType() == 1) {
            Element child = (Element)node;
            if (child.getTagName().equals("check")) {
               boolean present = readCheckTagAndCheck(child);
               if (!present) {
                  return null;
               }
            } else if (child.getTagName().equals("command")) {
               parser.setCommand(getString(child));
            } else if (child.getTagName().equals("mime-types")) {
               parser.setSupportedTypes(readMimeTypes(child));
            } else if (child.getTagName().equals("metadata")) {
               parser.setMetadataExtractionPatterns(readMetadataPatterns(child));
            }
         }
      }

      return parser;
   }

   private static Set<MediaType> readMimeTypes(Element mimeTypes) {
      Set<MediaType> types = new HashSet();
      NodeList children = mimeTypes.getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         Node node = children.item(i);
         if (node.getNodeType() == 1) {
            Element child = (Element)node;
            if (child.getTagName().equals("mime-type")) {
               types.add(MediaType.parse(getString(child)));
            }
         }
      }

      return types;
   }

   private static Map<Pattern, String> readMetadataPatterns(Element metadataDef) {
      Map<Pattern, String> metadata = new HashMap();
      NodeList children = metadataDef.getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         Node node = children.item(i);
         if (node.getNodeType() == 1) {
            Element child = (Element)node;
            if (child.getTagName().equals("match")) {
               String metadataKey = child.getAttribute("key");
               Pattern pattern = Pattern.compile(getString(child));
               metadata.put(pattern, metadataKey);
            }
         }
      }

      return metadata;
   }

   private static boolean readCheckTagAndCheck(Element checkDef) {
      String command = null;
      List<Integer> errorVals = new ArrayList();
      NodeList children = checkDef.getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         Node node = children.item(i);
         if (node.getNodeType() == 1) {
            Element child = (Element)node;
            if (child.getTagName().equals("command")) {
               command = getString(child);
            }

            if (child.getTagName().equals("error-codes")) {
               String errs = getString(child);
               StringTokenizer st = new StringTokenizer(errs, ",");

               while(st.hasMoreElements()) {
                  try {
                     String s = st.nextToken();
                     errorVals.add(Integer.parseInt(s));
                  } catch (NumberFormatException var10) {
                  }
               }
            }
         }
      }

      if (command == null) {
         return true;
      } else {
         String[] theCommand = command.split(" ");
         int[] errVals = new int[errorVals.size()];

         for(int i = 0; i < errVals.length; ++i) {
            errVals[i] = (Integer)errorVals.get(i);
         }

         return ExternalParser.check(theCommand, errVals);
      }
   }

   private static String getString(Element element) {
      StringBuffer s = new StringBuffer();
      NodeList children = element.getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         Node node = children.item(i);
         if (node.getNodeType() == 3) {
            s.append(node.getNodeValue());
         }
      }

      return s.toString();
   }
}
