package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ParserDecorator extends AbstractParser {
   private static final long serialVersionUID = -3861669115439125268L;
   private final Parser parser;

   public static final Parser withTypes(Parser parser, final Set<MediaType> types) {
      return new ParserDecorator(parser) {
         private static final long serialVersionUID = -7345051519565330731L;

         public Set<MediaType> getSupportedTypes(ParseContext context) {
            return types;
         }

         public String getDecorationName() {
            return "With Types";
         }
      };
   }

   public static final Parser withoutTypes(Parser parser, final Set<MediaType> excludeTypes) {
      return new ParserDecorator(parser) {
         private static final long serialVersionUID = 7979614774021768609L;

         public Set<MediaType> getSupportedTypes(ParseContext context) {
            Set<MediaType> parserTypes = new HashSet(super.getSupportedTypes(context));
            parserTypes.removeAll(excludeTypes);
            return parserTypes;
         }

         public String getDecorationName() {
            return "Without Types";
         }
      };
   }

   /** @deprecated */
   public static final Parser withFallbacks(final Collection<? extends Parser> parsers, final Set<MediaType> types) {
      Parser parser = EmptyParser.INSTANCE;
      if (!parsers.isEmpty()) {
         parser = (Parser)parsers.iterator().next();
      }

      return new ParserDecorator(parser) {
         private static final long serialVersionUID = 1625187131782069683L;

         public Set<MediaType> getSupportedTypes(ParseContext context) {
            return types;
         }

         public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
            TemporaryResources tmp = TikaInputStream.isTikaInputStream(stream) ? null : new TemporaryResources();

            try {
               TikaInputStream tstream = TikaInputStream.get(stream, tmp);
               tstream.getFile();

               for(Parser p : parsers) {
                  tstream.mark(-1);

                  try {
                     p.parse(tstream, handler, metadata, context);
                     return;
                  } catch (Exception var13) {
                     tstream.reset();
                  }
               }

            } finally {
               if (tmp != null) {
                  tmp.dispose();
               }

            }
         }

         public String getDecorationName() {
            return "With Fallback";
         }
      };
   }

   public ParserDecorator(Parser parser) {
      this.parser = parser;
   }

   public Set<MediaType> getSupportedTypes(ParseContext context) {
      return this.parser.getSupportedTypes(context);
   }

   public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
      this.parser.parse(stream, handler, metadata, context);
   }

   public String getDecorationName() {
      return null;
   }

   public Parser getWrappedParser() {
      return this.parser;
   }
}
