package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DigestingParser extends ParserDecorator {
   private final Digester digester;

   public DigestingParser(Parser parser, Digester digester) {
      super(parser);
      this.digester = digester;
   }

   public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
      TemporaryResources tmp = new TemporaryResources();
      TikaInputStream tis = TikaInputStream.get(stream, tmp);

      try {
         if (this.digester != null) {
            this.digester.digest(tis, metadata, context);
         }

         super.parse(tis, handler, metadata, context);
      } finally {
         tmp.dispose();
      }

   }

   public interface Digester {
      void digest(InputStream var1, Metadata var2, ParseContext var3) throws IOException;
   }

   public interface Encoder {
      String encode(byte[] var1);
   }
}
