package org.apache.tika.extractor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.DelegatingParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ParsingEmbeddedDocumentExtractor implements EmbeddedDocumentExtractor {
   private static final File ABSTRACT_PATH = new File("");
   private static final Parser DELEGATING_PARSER = new DelegatingParser();
   private final ParseContext context;

   public ParsingEmbeddedDocumentExtractor(ParseContext context) {
      this.context = context;
   }

   public boolean shouldParseEmbedded(Metadata metadata) {
      DocumentSelector selector = (DocumentSelector)this.context.get(DocumentSelector.class);
      if (selector != null) {
         return selector.select(metadata);
      } else {
         FilenameFilter filter = (FilenameFilter)this.context.get(FilenameFilter.class);
         if (filter != null) {
            String name = metadata.get("resourceName");
            if (name != null) {
               return filter.accept(ABSTRACT_PATH, name);
            }
         }

         return true;
      }
   }

   public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml) throws SAXException, IOException {
      if (outputHtml) {
         AttributesImpl attributes = new AttributesImpl();
         attributes.addAttribute("", "class", "class", "CDATA", "package-entry");
         handler.startElement("http://www.w3.org/1999/xhtml", "div", "div", attributes);
      }

      String name = metadata.get("resourceName");
      if (name != null && name.length() > 0 && outputHtml) {
         handler.startElement("http://www.w3.org/1999/xhtml", "h1", "h1", new AttributesImpl());
         char[] chars = name.toCharArray();
         handler.characters(chars, 0, chars.length);
         handler.endElement("http://www.w3.org/1999/xhtml", "h1", "h1");
      }

      try {
         TemporaryResources tmp = new TemporaryResources();
         Throwable var7 = null;

         try {
            TikaInputStream newStream = TikaInputStream.get((InputStream)(new CloseShieldInputStream(stream)), (TemporaryResources)tmp);
            if (stream instanceof TikaInputStream) {
               Object container = ((TikaInputStream)stream).getOpenContainer();
               if (container != null) {
                  newStream.setOpenContainer(container);
               }
            }

            DELEGATING_PARSER.parse(newStream, new EmbeddedContentHandler(new BodyContentHandler(handler)), metadata, this.context);
         } catch (Throwable var19) {
            var7 = var19;
            throw var19;
         } finally {
            if (tmp != null) {
               if (var7 != null) {
                  try {
                     tmp.close();
                  } catch (Throwable var18) {
                     var7.addSuppressed(var18);
                  }
               } else {
                  tmp.close();
               }
            }

         }
      } catch (EncryptedDocumentException var21) {
      } catch (TikaException var22) {
      }

      if (outputHtml) {
         handler.endElement("http://www.w3.org/1999/xhtml", "div", "div");
      }

   }
}
