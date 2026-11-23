package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface Parser extends Serializable {
   Set<MediaType> getSupportedTypes(ParseContext var1);

   void parse(InputStream var1, ContentHandler var2, Metadata var3, ParseContext var4) throws IOException, SAXException, TikaException;
}
