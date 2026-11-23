package org.apache.tika.mime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MimeTypesReader extends DefaultHandler implements MimeTypesReaderMetKeys {
   protected final MimeTypes types;
   protected MimeType type = null;
   protected int priority;
   protected StringBuilder characters = null;
   private ClauseRecord current = new ClauseRecord((Clause)null);

   protected MimeTypesReader(MimeTypes types) {
      this.types = types;
   }

   public void read(InputStream stream) throws IOException, MimeTypeException {
      try {
         SAXParserFactory factory = SAXParserFactory.newInstance();
         factory.setNamespaceAware(false);
         factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
         SAXParser parser = factory.newSAXParser();
         parser.parse(stream, this);
      } catch (ParserConfigurationException e) {
         throw new MimeTypeException("Unable to create an XML parser", e);
      } catch (SAXException e) {
         throw new MimeTypeException("Invalid type configuration", e);
      }
   }

   public void read(Document document) throws MimeTypeException {
      try {
         TransformerFactory factory = TransformerFactory.newInstance();
         Transformer transformer = factory.newTransformer();
         transformer.transform(new DOMSource(document), new SAXResult(this));
      } catch (TransformerException e) {
         throw new MimeTypeException("Failed to parse type registry", e);
      }
   }

   public InputSource resolveEntity(String publicId, String systemId) {
      return new InputSource(new ByteArrayInputStream(new byte[0]));
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (this.type == null) {
         if ("mime-type".equals(qName)) {
            String name = attributes.getValue("type");

            try {
               this.type = this.types.forName(name);
            } catch (MimeTypeException e) {
               this.handleMimeError(name, e, qName, attributes);
            }
         }
      } else if ("alias".equals(qName)) {
         String alias = attributes.getValue("type");
         this.types.addAlias(this.type, MediaType.parse(alias));
      } else if ("sub-class-of".equals(qName)) {
         String parent = attributes.getValue("type");
         this.types.setSuperType(this.type, MediaType.parse(parent));
      } else if (!"acronym".equals(qName) && !"_comment".equals(qName) && !"tika:link".equals(qName) && !"tika:uti".equals(qName)) {
         if ("glob".equals(qName)) {
            String pattern = attributes.getValue("pattern");
            String isRegex = attributes.getValue("isregex");
            if (pattern != null) {
               try {
                  this.types.addPattern(this.type, pattern, Boolean.valueOf(isRegex));
               } catch (MimeTypeException e) {
                  this.handleGlobError(this.type, pattern, e, qName, attributes);
               }
            }
         } else if ("root-XML".equals(qName)) {
            String namespace = attributes.getValue("namespaceURI");
            String name = attributes.getValue("localName");
            this.type.addRootXML(namespace, name);
         } else if ("match".equals(qName)) {
            String kind = attributes.getValue("type");
            String offset = attributes.getValue("offset");
            String value = attributes.getValue("value");
            String mask = attributes.getValue("mask");
            if (kind == null) {
               kind = "string";
            }

            this.current = new ClauseRecord(new MagicMatch(this.type.getType(), kind, offset, value, mask));
         } else if ("magic".equals(qName)) {
            String value = attributes.getValue("priority");
            if (value != null && value.length() > 0) {
               this.priority = Integer.parseInt(value);
            } else {
               this.priority = 50;
            }

            this.current = new ClauseRecord((Clause)null);
         }
      } else {
         this.characters = new StringBuilder();
      }

   }

   public void endElement(String uri, String localName, String qName) {
      if (this.type != null) {
         if ("mime-type".equals(qName)) {
            this.type = null;
         } else if ("_comment".equals(qName)) {
            this.type.setDescription(this.characters.toString().trim());
            this.characters = null;
         } else if ("acronym".equals(qName)) {
            this.type.setAcronym(this.characters.toString().trim());
            this.characters = null;
         } else if ("tika:uti".equals(qName)) {
            this.type.setUniformTypeIdentifier(this.characters.toString().trim());
            this.characters = null;
         } else if ("tika:link".equals(qName)) {
            try {
               this.type.addLink(new URI(this.characters.toString().trim()));
            } catch (URISyntaxException e) {
               throw new IllegalArgumentException("unable to parse link: " + this.characters, e);
            }

            this.characters = null;
         } else if ("match".equals(qName)) {
            this.current.stop();
         } else if ("magic".equals(qName)) {
            for(Clause clause : this.current.getClauses()) {
               this.type.addMagic(new Magic(this.type, this.priority, clause));
            }

            this.current = null;
         }
      }

   }

   public void characters(char[] ch, int start, int length) {
      if (this.characters != null) {
         this.characters.append(ch, start, length);
      }

   }

   protected void handleMimeError(String input, MimeTypeException ex, String qName, Attributes attributes) throws SAXException {
      throw new SAXException(ex);
   }

   protected void handleGlobError(MimeType type, String pattern, MimeTypeException ex, String qName, Attributes attributes) throws SAXException {
      throw new SAXException(ex);
   }

   private class ClauseRecord {
      private ClauseRecord parent;
      private Clause clause;
      private List<Clause> subclauses = null;

      public ClauseRecord(Clause clause) {
         this.parent = MimeTypesReader.this.current;
         this.clause = clause;
      }

      public void stop() {
         if (this.subclauses != null) {
            Clause subclause;
            if (this.subclauses.size() == 1) {
               subclause = (Clause)this.subclauses.get(0);
            } else {
               subclause = new OrClause(this.subclauses);
            }

            this.clause = new AndClause(new Clause[]{this.clause, subclause});
         }

         if (this.parent.subclauses == null) {
            this.parent.subclauses = Collections.singletonList(this.clause);
         } else {
            if (this.parent.subclauses.size() == 1) {
               this.parent.subclauses = new ArrayList(this.parent.subclauses);
            }

            this.parent.subclauses.add(this.clause);
         }

         MimeTypesReader.this.current = MimeTypesReader.this.current.parent;
      }

      public List<Clause> getClauses() {
         return this.subclauses;
      }
   }
}
