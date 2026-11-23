package org.apache.tika.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultParser extends CompositeParser {
   private static final long serialVersionUID = 3612324825403757520L;
   private final transient ServiceLoader loader;

   private static List<Parser> getDefaultParsers(ServiceLoader loader, EncodingDetector encodingDetector) {
      List<Parser> parsers = loader.<Parser>loadStaticServiceProviders(Parser.class);
      if (encodingDetector != null) {
         for(Parser p : parsers) {
            setEncodingDetector(p, encodingDetector);
         }
      }

      ServiceLoaderUtils.sortLoadedClasses(parsers);
      return parsers;
   }

   private static void setEncodingDetector(Parser p, EncodingDetector encodingDetector) {
      if (p instanceof AbstractEncodingDetectorParser) {
         ((AbstractEncodingDetectorParser)p).setEncodingDetector(encodingDetector);
      } else if (p instanceof CompositeParser) {
         for(Parser child : ((CompositeParser)p).getAllComponentParsers()) {
            setEncodingDetector(child, encodingDetector);
         }
      } else if (p instanceof ParserDecorator) {
         setEncodingDetector(((ParserDecorator)p).getWrappedParser(), encodingDetector);
      }

   }

   public DefaultParser(MediaTypeRegistry registry, ServiceLoader loader, Collection<Class<? extends Parser>> excludeParsers, EncodingDetector encodingDetector) {
      super(registry, getDefaultParsers(loader, encodingDetector), excludeParsers);
      this.loader = loader;
   }

   public DefaultParser(MediaTypeRegistry registry, ServiceLoader loader, Collection<Class<? extends Parser>> excludeParsers) {
      super(registry, getDefaultParsers(loader, new DefaultEncodingDetector(loader)), excludeParsers);
      this.loader = loader;
   }

   public DefaultParser(MediaTypeRegistry registry, ServiceLoader loader, EncodingDetector encodingDetector) {
      this(registry, loader, (Collection)null, encodingDetector);
   }

   public DefaultParser(MediaTypeRegistry registry, ServiceLoader loader) {
      this(registry, loader, (Collection)null, new DefaultEncodingDetector(loader));
   }

   public DefaultParser(MediaTypeRegistry registry, ClassLoader loader) {
      this(registry, new ServiceLoader(loader));
   }

   public DefaultParser(ClassLoader loader) {
      this(MediaTypeRegistry.getDefaultRegistry(), new ServiceLoader(loader));
   }

   public DefaultParser(MediaTypeRegistry registry) {
      this(registry, new ServiceLoader());
   }

   public DefaultParser() {
      this(MediaTypeRegistry.getDefaultRegistry());
   }

   public Map<MediaType, Parser> getParsers(ParseContext context) {
      Map<MediaType, Parser> map = super.getParsers(context);
      if (this.loader != null) {
         MediaTypeRegistry registry = this.getMediaTypeRegistry();
         List<Parser> parsers = this.loader.<Parser>loadDynamicServiceProviders(Parser.class);
         Collections.reverse(parsers);

         for(Parser parser : parsers) {
            for(MediaType type : parser.getSupportedTypes(context)) {
               map.put(registry.normalize(type), parser);
            }
         }
      }

      return map;
   }

   public List<Parser> getAllComponentParsers() {
      List<Parser> parsers = super.getAllComponentParsers();
      if (this.loader != null) {
         parsers = new ArrayList(parsers);
         parsers.addAll(this.loader.loadDynamicServiceProviders(Parser.class));
      }

      return parsers;
   }
}
