package org.apache.tika.extractor;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface EmbeddedDocumentExtractor {
   boolean shouldParseEmbedded(Metadata var1);

   void parseEmbedded(InputStream var1, ContentHandler var2, Metadata var3, boolean var4) throws SAXException, IOException;
}
