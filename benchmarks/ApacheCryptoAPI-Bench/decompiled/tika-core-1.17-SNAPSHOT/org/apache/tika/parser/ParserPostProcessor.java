package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.utils.RegexUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ParserPostProcessor extends ParserDecorator {
   public ParserPostProcessor(Parser parser) {
      super(parser);
   }

   public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
      ContentHandler body = new BodyContentHandler();
      ContentHandler tee = new TeeContentHandler(new ContentHandler[]{handler, body});
      super.parse(stream, tee, metadata, context);
      String content = body.toString();
      metadata.set("fulltext", content);
      int length = Math.min(content.length(), 500);
      metadata.set("summary", content.substring(0, length));

      for(String link : RegexUtils.extractLinks(content)) {
         metadata.add("outlinks", link);
      }

   }
}
