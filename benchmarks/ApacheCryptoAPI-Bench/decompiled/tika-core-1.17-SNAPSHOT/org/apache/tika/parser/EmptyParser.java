package org.apache.tika.parser;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EmptyParser extends AbstractParser {
   private static final long serialVersionUID = -4218649699095732123L;
   public static final EmptyParser INSTANCE = new EmptyParser();

   public Set<MediaType> getSupportedTypes(ParseContext context) {
      return Collections.emptySet();
   }

   public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws SAXException {
      XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
      xhtml.startDocument();
      xhtml.endDocument();
   }
}
