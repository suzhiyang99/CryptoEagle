package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.mime.MediaType;
import org.apache.tika.sax.ContentHandlerFactory;
import org.apache.tika.utils.ExceptionUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RecursiveParserWrapper implements Parser {
   private static final long serialVersionUID = 9086536568120690938L;
   public static final Property TIKA_CONTENT = Property.internalText("X-TIKA:content");
   public static final Property PARSE_TIME_MILLIS = Property.internalText("X-TIKA:parse_time_millis");
   public static final Property WRITE_LIMIT_REACHED = Property.internalBoolean("X-TIKA:EXCEPTION:write_limit_reached");
   public static final Property EMBEDDED_RESOURCE_LIMIT_REACHED = Property.internalBoolean("X-TIKA:EXCEPTION:embedded_resource_limit_reached");
   public static final Property EMBEDDED_EXCEPTION = Property.internalText("X-TIKA:EXCEPTION:embedded_exception");
   public static final Property EMBEDDED_RESOURCE_PATH = Property.internalText("X-TIKA:embedded_resource_path");
   private final Parser wrappedParser;
   private final ContentHandlerFactory contentHandlerFactory;
   private final List<Metadata> metadatas;
   private final boolean catchEmbeddedExceptions;
   private int unknownCount;
   private int maxEmbeddedResources;
   private boolean hitMaxEmbeddedResources;

   public RecursiveParserWrapper(Parser wrappedParser, ContentHandlerFactory contentHandlerFactory) {
      this(wrappedParser, contentHandlerFactory, true);
   }

   public RecursiveParserWrapper(Parser wrappedParser, ContentHandlerFactory contentHandlerFactory, boolean catchEmbeddedExceptions) {
      this.metadatas = new LinkedList();
      this.unknownCount = 0;
      this.maxEmbeddedResources = -1;
      this.hitMaxEmbeddedResources = false;
      this.wrappedParser = wrappedParser;
      this.contentHandlerFactory = contentHandlerFactory;
      this.catchEmbeddedExceptions = catchEmbeddedExceptions;
   }

   public Set<MediaType> getSupportedTypes(ParseContext context) {
      return this.wrappedParser.getSupportedTypes(context);
   }

   public void parse(InputStream stream, ContentHandler ignore, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
      EmbeddedParserDecorator decorator = new EmbeddedParserDecorator("/");
      context.set(Parser.class, decorator);
      ContentHandler localHandler = this.contentHandlerFactory.getNewContentHandler();
      long started = (new Date()).getTime();
      boolean var16 = false;

      label93: {
         try {
            try {
               var16 = true;
               this.wrappedParser.parse(stream, localHandler, metadata, context);
               var16 = false;
               break label93;
            } catch (SAXException e) {
               boolean wlr = this.isWriteLimitReached(e);
               if (!wlr) {
                  throw e;
               }
            }

            metadata.set(WRITE_LIMIT_REACHED, "true");
            var16 = false;
         } finally {
            if (var16) {
               long elapsedMillis = (new Date()).getTime() - started;
               metadata.set(PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
               this.addContent(localHandler, metadata);
               if (this.hitMaxEmbeddedResources) {
                  metadata.set(EMBEDDED_RESOURCE_LIMIT_REACHED, "true");
               }

               this.metadatas.add(0, this.deepCopy(metadata));
            }
         }

         long elapsedMillis = (new Date()).getTime() - started;
         metadata.set(PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
         this.addContent(localHandler, metadata);
         if (this.hitMaxEmbeddedResources) {
            metadata.set(EMBEDDED_RESOURCE_LIMIT_REACHED, "true");
         }

         this.metadatas.add(0, this.deepCopy(metadata));
         return;
      }

      long elapsedMillis = (new Date()).getTime() - started;
      metadata.set(PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
      this.addContent(localHandler, metadata);
      if (this.hitMaxEmbeddedResources) {
         metadata.set(EMBEDDED_RESOURCE_LIMIT_REACHED, "true");
      }

      this.metadatas.add(0, this.deepCopy(metadata));
   }

   public List<Metadata> getMetadata() {
      return this.metadatas;
   }

   public void setMaxEmbeddedResources(int max) {
      this.maxEmbeddedResources = max;
   }

   public void reset() {
      this.metadatas.clear();
      this.unknownCount = 0;
      this.hitMaxEmbeddedResources = false;
   }

   private boolean isWriteLimitReached(Throwable t) {
      if (t.getMessage() != null && t.getMessage().indexOf("Your document contained more than") == 0) {
         return true;
      } else {
         return t.getCause() != null && this.isWriteLimitReached(t.getCause());
      }
   }

   private Metadata deepCopy(Metadata m) {
      Metadata clone = new Metadata();

      for(String n : m.names()) {
         if (!m.isMultiValued(n)) {
            clone.set(n, m.get(n));
         } else {
            String[] vals = m.getValues(n);

            for(int i = 0; i < vals.length; ++i) {
               clone.add(n, vals[i]);
            }
         }
      }

      return clone;
   }

   private String getResourceName(Metadata metadata) {
      String objectName = "";
      if (metadata.get("resourceName") != null) {
         objectName = metadata.get("resourceName");
      } else if (metadata.get("embeddedRelationshipId") != null) {
         objectName = metadata.get("embeddedRelationshipId");
      } else {
         objectName = "embedded-" + ++this.unknownCount;
      }

      objectName = FilenameUtils.getName(objectName);
      return objectName;
   }

   private void addContent(ContentHandler handler, Metadata metadata) {
      if (!handler.getClass().equals(DefaultHandler.class)) {
         String content = handler.toString();
         if (content != null && content.trim().length() > 0) {
            metadata.add(TIKA_CONTENT, content);
         }
      }

   }

   private class EmbeddedParserDecorator extends ParserDecorator {
      private static final long serialVersionUID = 207648200464263337L;
      private String location;

      private EmbeddedParserDecorator(String location) {
         super(RecursiveParserWrapper.this.wrappedParser);
         this.location = null;
         this.location = location;
         if (!this.location.endsWith("/")) {
            this.location = this.location + "/";
         }

      }

      public void parse(InputStream stream, ContentHandler ignore, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
         if (RecursiveParserWrapper.this.maxEmbeddedResources > -1 && RecursiveParserWrapper.this.metadatas.size() >= RecursiveParserWrapper.this.maxEmbeddedResources) {
            RecursiveParserWrapper.this.hitMaxEmbeddedResources = true;
         } else {
            String objectName = RecursiveParserWrapper.this.getResourceName(metadata);
            String objectLocation = this.location + objectName;
            metadata.add(RecursiveParserWrapper.EMBEDDED_RESOURCE_PATH, objectLocation);
            ContentHandler localHandler = RecursiveParserWrapper.this.contentHandlerFactory.getNewContentHandler();
            Parser preContextParser = (Parser)context.get(Parser.class);
            context.set(Parser.class, RecursiveParserWrapper.this.new EmbeddedParserDecorator(objectLocation));
            long started = (new Date()).getTime();
            boolean var20 = false;

            label103: {
               label102: {
                  label101: {
                     try {
                        var20 = true;
                        super.parse(stream, localHandler, metadata, context);
                        var20 = false;
                        break label102;
                     } catch (SAXException e) {
                        boolean wlr = RecursiveParserWrapper.this.isWriteLimitReached(e);
                        if (wlr) {
                           metadata.add(RecursiveParserWrapper.WRITE_LIMIT_REACHED, "true");
                           var20 = false;
                           break label101;
                        }

                        if (!RecursiveParserWrapper.this.catchEmbeddedExceptions) {
                           throw e;
                        }

                        String trace = ExceptionUtils.getStackTrace(e);
                        metadata.set(RecursiveParserWrapper.EMBEDDED_EXCEPTION, trace);
                        var20 = false;
                        break label101;
                     } catch (TikaException e) {
                        if (!RecursiveParserWrapper.this.catchEmbeddedExceptions) {
                           throw e;
                        }

                        String trace = ExceptionUtils.getStackTrace(e);
                        metadata.set(RecursiveParserWrapper.EMBEDDED_EXCEPTION, trace);
                        var20 = false;
                     } finally {
                        if (var20) {
                           context.set(Parser.class, preContextParser);
                           long elapsedMillisxx = (new Date()).getTime() - started;
                           metadata.set(RecursiveParserWrapper.PARSE_TIME_MILLIS, Long.toString(elapsedMillisxx));
                        }
                     }

                     context.set(Parser.class, preContextParser);
                     long elapsedMillis = (new Date()).getTime() - started;
                     metadata.set(RecursiveParserWrapper.PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
                     break label103;
                  }

                  context.set(Parser.class, preContextParser);
                  long elapsedMillis = (new Date()).getTime() - started;
                  metadata.set(RecursiveParserWrapper.PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
                  break label103;
               }

               context.set(Parser.class, preContextParser);
               long elapsedMillis = (new Date()).getTime() - started;
               metadata.set(RecursiveParserWrapper.PARSE_TIME_MILLIS, Long.toString(elapsedMillis));
            }

            if (RecursiveParserWrapper.this.maxEmbeddedResources > -1 && RecursiveParserWrapper.this.metadatas.size() >= RecursiveParserWrapper.this.maxEmbeddedResources) {
               RecursiveParserWrapper.this.hitMaxEmbeddedResources = true;
            } else {
               RecursiveParserWrapper.this.addContent(localHandler, metadata);
               RecursiveParserWrapper.this.metadatas.add(RecursiveParserWrapper.this.deepCopy(metadata));
            }
         }
      }
   }
}
