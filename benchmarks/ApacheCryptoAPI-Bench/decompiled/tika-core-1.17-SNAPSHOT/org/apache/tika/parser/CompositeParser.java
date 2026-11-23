package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.sax.TaggedContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class CompositeParser extends AbstractParser {
   private static final long serialVersionUID = 2192845797749627824L;
   private MediaTypeRegistry registry;
   private List<Parser> parsers;
   private Parser fallback;

   public CompositeParser(MediaTypeRegistry registry, List<Parser> parsers, Collection<Class<? extends Parser>> excludeParsers) {
      this.fallback = new EmptyParser();
      if (excludeParsers != null && !excludeParsers.isEmpty()) {
         this.parsers = new ArrayList();

         for(Parser p : parsers) {
            if (!this.isExcluded(excludeParsers, p.getClass())) {
               this.parsers.add(p);
            }
         }
      } else {
         this.parsers = parsers;
      }

      this.registry = registry;
   }

   public CompositeParser(MediaTypeRegistry registry, List<Parser> parsers) {
      this(registry, parsers, (Collection)null);
   }

   public CompositeParser(MediaTypeRegistry registry, Parser... parsers) {
      this(registry, Arrays.asList(parsers));
   }

   public CompositeParser() {
      this(new MediaTypeRegistry());
   }

   public Map<MediaType, Parser> getParsers(ParseContext context) {
      Map<MediaType, Parser> map = new HashMap();

      for(Parser parser : this.parsers) {
         for(MediaType type : parser.getSupportedTypes(context)) {
            map.put(this.registry.normalize(type), parser);
         }
      }

      return map;
   }

   private boolean isExcluded(Collection<Class<? extends Parser>> excludeParsers, Class<? extends Parser> p) {
      return excludeParsers.contains(p) || this.assignableFrom(excludeParsers, p);
   }

   private boolean assignableFrom(Collection<Class<? extends Parser>> excludeParsers, Class<? extends Parser> p) {
      for(Class<? extends Parser> e : excludeParsers) {
         if (e.isAssignableFrom(p)) {
            return true;
         }
      }

      return false;
   }

   public Map<MediaType, List<Parser>> findDuplicateParsers(ParseContext context) {
      Map<MediaType, Parser> types = new HashMap();
      Map<MediaType, List<Parser>> duplicates = new HashMap();

      for(Parser parser : this.parsers) {
         for(MediaType type : parser.getSupportedTypes(context)) {
            MediaType canonicalType = this.registry.normalize(type);
            if (types.containsKey(canonicalType)) {
               List<Parser> list = (List)duplicates.get(canonicalType);
               if (list == null) {
                  list = new ArrayList();
                  list.add(types.get(canonicalType));
                  duplicates.put(canonicalType, list);
               }

               list.add(parser);
            } else {
               types.put(canonicalType, parser);
            }
         }
      }

      return duplicates;
   }

   public MediaTypeRegistry getMediaTypeRegistry() {
      return this.registry;
   }

   public void setMediaTypeRegistry(MediaTypeRegistry registry) {
      this.registry = registry;
   }

   public List<Parser> getAllComponentParsers() {
      return Collections.unmodifiableList(this.parsers);
   }

   public Map<MediaType, Parser> getParsers() {
      return this.getParsers(new ParseContext());
   }

   public void setParsers(Map<MediaType, Parser> parsers) {
      this.parsers = new ArrayList(parsers.size());

      for(Map.Entry<MediaType, Parser> entry : parsers.entrySet()) {
         this.parsers.add(ParserDecorator.withTypes((Parser)entry.getValue(), Collections.singleton(entry.getKey())));
      }

   }

   public Parser getFallback() {
      return this.fallback;
   }

   public void setFallback(Parser fallback) {
      this.fallback = fallback;
   }

   protected Parser getParser(Metadata metadata) {
      return this.getParser(metadata, new ParseContext());
   }

   protected Parser getParser(Metadata metadata, ParseContext context) {
      Map<MediaType, Parser> map = this.getParsers(context);
      MediaType type = MediaType.parse(metadata.get("Content-Type"));
      if (type != null) {
         type = this.registry.normalize(type);
      }

      while(type != null) {
         Parser parser = (Parser)map.get(type);
         if (parser != null) {
            return parser;
         }

         type = this.registry.getSupertype(type);
      }

      return this.fallback;
   }

   public Set<MediaType> getSupportedTypes(ParseContext context) {
      return this.getParsers(context).keySet();
   }

   public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
      Parser parser = this.getParser(metadata, context);
      TemporaryResources tmp = new TemporaryResources();

      try {
         TikaInputStream taggedStream = TikaInputStream.get(stream, tmp);
         TaggedContentHandler taggedHandler = handler != null ? new TaggedContentHandler(handler) : null;
         if (parser instanceof ParserDecorator) {
            metadata.add("X-Parsed-By", ((ParserDecorator)parser).getWrappedParser().getClass().getName());
         } else {
            metadata.add("X-Parsed-By", parser.getClass().getName());
         }

         try {
            parser.parse(taggedStream, taggedHandler, metadata, context);
         } catch (RuntimeException e) {
            throw new TikaException("Unexpected RuntimeException from " + parser, e);
         } catch (IOException e) {
            taggedStream.throwIfCauseOf(e);
            throw new TikaException("TIKA-198: Illegal IOException from " + parser, e);
         } catch (SAXException var17) {
            if (taggedHandler != null) {
               taggedHandler.throwIfCauseOf(var17);
            }

            throw new TikaException("TIKA-237: Illegal SAXException from " + parser, var17);
         }
      } finally {
         tmp.dispose();
      }

   }
}
