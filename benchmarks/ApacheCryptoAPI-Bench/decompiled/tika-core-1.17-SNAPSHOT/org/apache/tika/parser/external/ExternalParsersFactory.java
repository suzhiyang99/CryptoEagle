package org.apache.tika.parser.external;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.Parser;

public class ExternalParsersFactory {
   public static List<ExternalParser> create() throws IOException, TikaException {
      return create(new ServiceLoader());
   }

   public static List<ExternalParser> create(ServiceLoader loader) throws IOException, TikaException {
      return create("tika-external-parsers.xml", loader);
   }

   public static List<ExternalParser> create(String filename, ServiceLoader loader) throws IOException, TikaException {
      String filepath = ExternalParsersFactory.class.getPackage().getName().replace('.', '/') + "/" + filename;
      Enumeration<URL> files = loader.findServiceResources(filepath);
      ArrayList<URL> list = Collections.list(files);
      URL[] urls = (URL[])list.toArray(new URL[list.size()]);
      return create(urls);
   }

   public static List<ExternalParser> create(URL... urls) throws IOException, TikaException {
      List<ExternalParser> parsers = new ArrayList();

      for(URL url : urls) {
         InputStream stream = url.openStream();
         Throwable var7 = null;

         try {
            parsers.addAll(ExternalParsersConfigReader.read(stream));
         } catch (Throwable var16) {
            var7 = var16;
            throw var16;
         } finally {
            if (stream != null) {
               if (var7 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var15) {
                     var7.addSuppressed(var15);
                  }
               } else {
                  stream.close();
               }
            }

         }
      }

      return parsers;
   }

   public static void attachExternalParsers(TikaConfig config) throws IOException, TikaException {
      attachExternalParsers(create(), config);
   }

   public static void attachExternalParsers(List<ExternalParser> parsers, TikaConfig config) {
      Parser parser = config.getParser();
      if (parser instanceof CompositeParser) {
         CompositeParser cParser = (CompositeParser)parser;
         Map var4 = cParser.getParsers();
      }

   }
}
