package org.apache.tika;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.translate.Translator;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParsingReader;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.xml.sax.SAXException;

public class Tika {
   private final Detector detector;
   private final Parser parser;
   private final Translator translator;
   private int maxStringLength;

   public Tika(Detector detector, Parser parser) {
      this.maxStringLength = 100000;
      this.detector = detector;
      this.parser = parser;
      this.translator = TikaConfig.getDefaultConfig().getTranslator();
   }

   public Tika(Detector detector, Parser parser, Translator translator) {
      this.maxStringLength = 100000;
      this.detector = detector;
      this.parser = parser;
      this.translator = translator;
   }

   public Tika(TikaConfig config) {
      this(config.getDetector(), new AutoDetectParser(config), config.getTranslator());
   }

   public Tika() {
      this(TikaConfig.getDefaultConfig());
   }

   public Tika(Detector detector) {
      this(detector, new AutoDetectParser(detector));
   }

   public String detect(InputStream stream, Metadata metadata) throws IOException {
      return stream != null && !stream.markSupported() ? this.detector.detect(new BufferedInputStream(stream), metadata).toString() : this.detector.detect(stream, metadata).toString();
   }

   public String detect(InputStream stream, String name) throws IOException {
      Metadata metadata = new Metadata();
      metadata.set("resourceName", name);
      return this.detect(stream, metadata);
   }

   public String detect(InputStream stream) throws IOException {
      return this.detect(stream, new Metadata());
   }

   public String detect(byte[] prefix, String name) {
      try {
         InputStream stream = TikaInputStream.get(prefix);
         Throwable var4 = null;

         String var5;
         try {
            var5 = this.detect(stream, name);
         } catch (Throwable var15) {
            var4 = var15;
            throw var15;
         } finally {
            if (stream != null) {
               if (var4 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var14) {
                     var4.addSuppressed(var14);
                  }
               } else {
                  stream.close();
               }
            }

         }

         return var5;
      } catch (IOException e) {
         throw new IllegalStateException("Unexpected IOException", e);
      }
   }

   public String detect(byte[] prefix) {
      try {
         InputStream stream = TikaInputStream.get(prefix);
         Throwable var3 = null;

         String var4;
         try {
            var4 = this.detect(stream);
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (stream != null) {
               if (var3 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  stream.close();
               }
            }

         }

         return var4;
      } catch (IOException e) {
         throw new IllegalStateException("Unexpected IOException", e);
      }
   }

   public String detect(Path path) throws IOException {
      Metadata metadata = new Metadata();
      InputStream stream = TikaInputStream.get(path, metadata);
      Throwable var4 = null;

      String var5;
      try {
         var5 = this.detect(stream, metadata);
      } catch (Throwable var14) {
         var4 = var14;
         throw var14;
      } finally {
         if (stream != null) {
            if (var4 != null) {
               try {
                  stream.close();
               } catch (Throwable var13) {
                  var4.addSuppressed(var13);
               }
            } else {
               stream.close();
            }
         }

      }

      return var5;
   }

   public String detect(File file) throws IOException {
      Metadata metadata = new Metadata();
      InputStream stream = TikaInputStream.get(file, metadata);
      Throwable var4 = null;

      String var5;
      try {
         var5 = this.detect(stream, metadata);
      } catch (Throwable var14) {
         var4 = var14;
         throw var14;
      } finally {
         if (stream != null) {
            if (var4 != null) {
               try {
                  stream.close();
               } catch (Throwable var13) {
                  var4.addSuppressed(var13);
               }
            } else {
               stream.close();
            }
         }

      }

      return var5;
   }

   public String detect(URL url) throws IOException {
      Metadata metadata = new Metadata();
      InputStream stream = TikaInputStream.get(url, metadata);
      Throwable var4 = null;

      String var5;
      try {
         var5 = this.detect(stream, metadata);
      } catch (Throwable var14) {
         var4 = var14;
         throw var14;
      } finally {
         if (stream != null) {
            if (var4 != null) {
               try {
                  stream.close();
               } catch (Throwable var13) {
                  var4.addSuppressed(var13);
               }
            } else {
               stream.close();
            }
         }

      }

      return var5;
   }

   public String detect(String name) {
      try {
         return this.detect((InputStream)null, name);
      } catch (IOException e) {
         throw new IllegalStateException("Unexpected IOException", e);
      }
   }

   public String translate(String text, String sourceLanguage, String targetLanguage) {
      try {
         return this.translator.translate(text, sourceLanguage, targetLanguage);
      } catch (Exception e) {
         throw new IllegalStateException("Error translating data.", e);
      }
   }

   public String translate(String text, String targetLanguage) {
      try {
         return this.translator.translate(text, targetLanguage);
      } catch (Exception e) {
         throw new IllegalStateException("Error translating data.", e);
      }
   }

   public String translate(InputStream text, String sourceLanguage, String targetLanguage) {
      try {
         return this.translator.translate(IOUtils.toString(text), sourceLanguage, targetLanguage);
      } catch (Exception e) {
         throw new IllegalStateException("Error translating data.", e);
      }
   }

   public String translate(InputStream text, String targetLanguage) {
      try {
         return this.translator.translate(IOUtils.toString(text), targetLanguage);
      } catch (Exception e) {
         throw new IllegalStateException("Error translating data.", e);
      }
   }

   public Reader parse(InputStream stream, Metadata metadata) throws IOException {
      ParseContext context = new ParseContext();
      context.set(Parser.class, this.parser);
      return new ParsingReader(this.parser, stream, metadata, context);
   }

   public Reader parse(InputStream stream) throws IOException {
      return this.parse(stream, new Metadata());
   }

   public Reader parse(Path path, Metadata metadata) throws IOException {
      InputStream stream = TikaInputStream.get(path, metadata);
      return this.parse(stream, metadata);
   }

   public Reader parse(Path path) throws IOException {
      return this.parse(path, new Metadata());
   }

   public Reader parse(File file, Metadata metadata) throws IOException {
      InputStream stream = TikaInputStream.get(file, metadata);
      return this.parse(stream, metadata);
   }

   public Reader parse(File file) throws IOException {
      return this.parse(file, new Metadata());
   }

   public Reader parse(URL url) throws IOException {
      Metadata metadata = new Metadata();
      InputStream stream = TikaInputStream.get(url, metadata);
      return this.parse(stream, metadata);
   }

   public String parseToString(InputStream stream, Metadata metadata) throws IOException, TikaException {
      WriteOutContentHandler handler = new WriteOutContentHandler(this.maxStringLength);

      try {
         ParseContext context = new ParseContext();
         context.set(Parser.class, this.parser);
         this.parser.parse(stream, new BodyContentHandler(handler), metadata, context);
      } catch (SAXException e) {
         if (!handler.isWriteLimitReached(e)) {
            throw new TikaException("Unexpected SAX processing failure", e);
         }
      } finally {
         stream.close();
      }

      return handler.toString();
   }

   public String parseToString(InputStream stream, Metadata metadata, int maxLength) throws IOException, TikaException {
      WriteOutContentHandler handler = new WriteOutContentHandler(maxLength);

      try {
         ParseContext context = new ParseContext();
         context.set(Parser.class, this.parser);
         this.parser.parse(stream, new BodyContentHandler(handler), metadata, context);
      } catch (SAXException e) {
         if (!handler.isWriteLimitReached(e)) {
            throw new TikaException("Unexpected SAX processing failure", e);
         }
      } finally {
         stream.close();
      }

      return handler.toString();
   }

   public String parseToString(InputStream stream) throws IOException, TikaException {
      return this.parseToString(stream, new Metadata());
   }

   public String parseToString(Path path) throws IOException, TikaException {
      Metadata metadata = new Metadata();
      InputStream stream = TikaInputStream.get(path, metadata);
      return this.parseToString(stream, metadata);
   }

   public String parseToString(File file) throws IOException, TikaException {
      Metadata metadata = new Metadata();
      InputStream stream = TikaInputStream.get(file, metadata);
      return this.parseToString(stream, metadata);
   }

   public String parseToString(URL url) throws IOException, TikaException {
      Metadata metadata = new Metadata();
      InputStream stream = TikaInputStream.get(url, metadata);
      return this.parseToString(stream, metadata);
   }

   public int getMaxStringLength() {
      return this.maxStringLength;
   }

   public void setMaxStringLength(int maxStringLength) {
      this.maxStringLength = maxStringLength;
   }

   public Parser getParser() {
      return this.parser;
   }

   public Detector getDetector() {
      return this.detector;
   }

   public Translator getTranslator() {
      return this.translator;
   }

   public String toString() {
      String version = null;

      try {
         InputStream stream = Tika.class.getResourceAsStream("/META-INF/maven/org.apache.tika/tika-core/pom.properties");
         Throwable var3 = null;

         try {
            if (stream != null) {
               Properties properties = new Properties();
               properties.load(stream);
               version = properties.getProperty("version");
            }
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (stream != null) {
               if (var3 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  stream.close();
               }
            }

         }
      } catch (Exception var15) {
      }

      return version != null ? "Apache Tika " + version : "Apache Tika";
   }
}
