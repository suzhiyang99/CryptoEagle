package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DelegatingParser extends AbstractParser {
   protected Parser getDelegateParser(ParseContext context) {
      return (Parser)context.get(Parser.class, EmptyParser.INSTANCE);
   }

   public Set<MediaType> getSupportedTypes(ParseContext context) {
      return this.getDelegateParser(context).getSupportedTypes(context);
   }

   public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws SAXException, IOException, TikaException {
      this.getDelegateParser(context).parse(stream, handler, metadata, context);
   }
}
