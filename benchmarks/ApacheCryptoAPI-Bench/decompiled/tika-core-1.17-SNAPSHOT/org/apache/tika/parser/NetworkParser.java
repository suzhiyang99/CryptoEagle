package org.apache.tika.parser;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.sax.OfflineContentHandler;
import org.apache.tika.sax.TaggedContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NetworkParser extends AbstractParser {
   private final URI uri;
   private final Set<MediaType> supportedTypes;

   public NetworkParser(URI uri, Set<MediaType> supportedTypes) {
      this.uri = uri;
      this.supportedTypes = supportedTypes;
   }

   public NetworkParser(URI uri) {
      this(uri, Collections.singleton(MediaType.OCTET_STREAM));
   }

   public Set<MediaType> getSupportedTypes(ParseContext context) {
      return this.supportedTypes;
   }

   public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
      TemporaryResources tmp = new TemporaryResources();

      try {
         TikaInputStream tis = TikaInputStream.get(stream, tmp);
         this.parse(tis, handler, metadata, context);
      } finally {
         tmp.dispose();
      }

   }

   private void parse(TikaInputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
      if ("telnet".equals(this.uri.getScheme())) {
         final Socket socket = new Socket(this.uri.getHost(), this.uri.getPort());
         Throwable var6 = null;

         try {
            (new ParsingTask(stream, new FilterOutputStream(socket.getOutputStream()) {
               public void close() throws IOException {
                  socket.shutdownOutput();
               }
            })).parse(socket.getInputStream(), handler, metadata, context);
         } catch (Throwable var30) {
            var6 = var30;
            throw var30;
         } finally {
            if (socket != null) {
               if (var6 != null) {
                  try {
                     socket.close();
                  } catch (Throwable var28) {
                     var6.addSuppressed(var28);
                  }
               } else {
                  socket.close();
               }
            }

         }
      } else {
         URL url = this.uri.toURL();
         URLConnection connection = url.openConnection();
         connection.setDoOutput(true);
         connection.connect();
         InputStream input = connection.getInputStream();
         Throwable var8 = null;

         try {
            (new ParsingTask(stream, connection.getOutputStream())).parse(new CloseShieldInputStream(input), handler, metadata, context);
         } catch (Throwable var29) {
            var8 = var29;
            throw var29;
         } finally {
            if (input != null) {
               if (var8 != null) {
                  try {
                     input.close();
                  } catch (Throwable var27) {
                     var8.addSuppressed(var27);
                  }
               } else {
                  input.close();
               }
            }

         }
      }

   }

   private static class ParsingTask implements Runnable {
      private final TikaInputStream input;
      private final OutputStream output;
      private volatile Exception exception = null;

      public ParsingTask(TikaInputStream input, OutputStream output) {
         this.input = input;
         this.output = output;
      }

      public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
         Thread thread = new Thread(this, "Tika network parser");
         thread.start();
         TaggedContentHandler tagged = new TaggedContentHandler(new OfflineContentHandler(handler));

         try {
            context.getSAXParser().parse(stream, new TeeContentHandler(new ContentHandler[]{tagged, new MetaHandler(metadata)}));
         } catch (SAXException e) {
            tagged.throwIfCauseOf(e);
            throw new TikaException("Invalid network parser output", e);
         } catch (IOException e) {
            throw new TikaException("Unable to read network parser output", e);
         } finally {
            try {
               thread.join(1000L);
            } catch (InterruptedException e) {
               throw new TikaException("Network parser interrupted", e);
            }

            if (this.exception != null) {
               this.input.throwIfCauseOf(this.exception);
               throw new TikaException("Unexpected network parser error", this.exception);
            }

         }

      }

      public void run() {
         try {
            try {
               IOUtils.copy((InputStream)this.input, (OutputStream)this.output);
            } finally {
               this.output.close();
            }
         } catch (Exception e) {
            this.exception = e;
         }

      }
   }

   private static class MetaHandler extends DefaultHandler {
      private final Metadata metadata;

      public MetaHandler(Metadata metadata) {
         this.metadata = metadata;
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if ("http://www.w3.org/1999/xhtml".equals(uri) && "meta".equals(localName)) {
            String name = attributes.getValue("", "name");
            String content = attributes.getValue("", "content");
            if (name != null && content != null) {
               this.metadata.add(name, content);
            }
         }

      }
   }
}
