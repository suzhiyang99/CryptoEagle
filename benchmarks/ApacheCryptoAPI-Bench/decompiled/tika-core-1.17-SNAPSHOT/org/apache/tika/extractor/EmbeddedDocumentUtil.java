package org.apache.tika.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.parser.PasswordProvider;
import org.apache.tika.utils.ExceptionUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EmbeddedDocumentUtil implements Serializable {
   private final ParseContext context;
   private final EmbeddedDocumentExtractor embeddedDocumentExtractor;
   private TikaConfig tikaConfig;
   private MimeTypes mimeTypes;
   private Detector detector;

   public EmbeddedDocumentUtil(ParseContext context) {
      this.context = context;
      this.embeddedDocumentExtractor = getEmbeddedDocumentExtractor(context);
   }

   public static EmbeddedDocumentExtractor getEmbeddedDocumentExtractor(ParseContext context) {
      EmbeddedDocumentExtractor extractor = (EmbeddedDocumentExtractor)context.get(EmbeddedDocumentExtractor.class);
      if (extractor == null) {
         Parser embeddedParser = (Parser)context.get(Parser.class);
         if (embeddedParser == null) {
            TikaConfig tikaConfig = (TikaConfig)context.get(TikaConfig.class);
            if (tikaConfig == null) {
               context.set(Parser.class, new AutoDetectParser());
            } else {
               context.set(Parser.class, new AutoDetectParser(tikaConfig));
            }
         }

         extractor = new ParsingEmbeddedDocumentExtractor(context);
      }

      return extractor;
   }

   public PasswordProvider getPasswordProvider() {
      return (PasswordProvider)this.context.get(PasswordProvider.class);
   }

   public Detector getDetector() {
      if (this.detector == null) {
         this.detector = (Detector)this.context.get(Detector.class);
         if (this.detector == null) {
            this.detector = this.getTikaConfig().getDetector();
         }
      }

      return this.detector;
   }

   public MimeTypes getMimeTypes() {
      if (this.mimeTypes == null) {
         this.mimeTypes = (MimeTypes)this.context.get(MimeTypes.class);
         if (this.mimeTypes == null) {
            this.mimeTypes = this.getTikaConfig().getMimeRepository();
         }
      }

      return this.mimeTypes;
   }

   public TikaConfig getTikaConfig() {
      if (this.tikaConfig == null) {
         this.tikaConfig = (TikaConfig)this.context.get(TikaConfig.class);
         if (this.tikaConfig == null) {
            this.tikaConfig = TikaConfig.getDefaultConfig();
         }
      }

      return this.tikaConfig;
   }

   public String getExtension(TikaInputStream is, Metadata metadata) {
      String mimeString = metadata.get("Content-Type");
      TikaConfig config = this.getConfig();
      MimeType mimeType = null;
      MimeTypes types = config.getMimeRepository();
      boolean detected = false;
      if (mimeString != null) {
         try {
            mimeType = types.forName(mimeString);
         } catch (MimeTypeException var12) {
         }
      }

      if (mimeType == null) {
         Detector detector = config.getDetector();

         try {
            MediaType mediaType = detector.detect(is, metadata);
            mimeType = types.forName(mediaType.toString());
            detected = true;
            is.reset();
         } catch (IOException var10) {
         } catch (MimeTypeException var11) {
         }
      }

      if (mimeType != null) {
         if (detected) {
            metadata.set("Content-Type", mimeType.toString());
         }

         return mimeType.getExtension();
      } else {
         return ".bin";
      }
   }

   public TikaConfig getConfig() {
      TikaConfig config = (TikaConfig)this.context.get(TikaConfig.class);
      if (config == null) {
         config = TikaConfig.getDefaultConfig();
      }

      return config;
   }

   public static void recordException(Throwable t, Metadata m) {
      String ex = ExceptionUtils.getFilteredStackTrace(t);
      m.add(TikaCoreProperties.TIKA_META_EXCEPTION_WARNING, ex);
   }

   public static void recordEmbeddedStreamException(Throwable t, Metadata m) {
      String ex = ExceptionUtils.getFilteredStackTrace(t);
      m.add(TikaCoreProperties.TIKA_META_EXCEPTION_EMBEDDED_STREAM, ex);
   }

   public boolean shouldParseEmbedded(Metadata m) {
      return this.getEmbeddedDocumentExtractor().shouldParseEmbedded(m);
   }

   private EmbeddedDocumentExtractor getEmbeddedDocumentExtractor() {
      return this.embeddedDocumentExtractor;
   }

   public void parseEmbedded(InputStream inputStream, ContentHandler handler, Metadata metadata, boolean outputHtml) throws IOException, SAXException {
      this.embeddedDocumentExtractor.parseEmbedded(inputStream, handler, metadata, outputHtml);
   }

   public static Parser tryToFindExistingLeafParser(Class clazz, ParseContext context) {
      Parser p = (Parser)context.get(Parser.class);
      if (equals(p, clazz)) {
         return p;
      } else {
         Parser returnParser = null;
         if (p != null) {
            if (p instanceof ParserDecorator) {
               p = ((ParserDecorator)p).getWrappedParser();
            }

            if (equals(p, clazz)) {
               return p;
            }

            if (p instanceof CompositeParser) {
               returnParser = findInComposite((CompositeParser)p, clazz, context);
            }
         }

         return returnParser != null && equals(returnParser, clazz) ? returnParser : null;
      }
   }

   private static Parser findInComposite(CompositeParser p, Class clazz, ParseContext context) {
      Map<MediaType, Parser> map = p.getParsers(context);

      for(Map.Entry<MediaType, Parser> e : map.entrySet()) {
         Parser candidate = (Parser)e.getValue();
         if (equals(candidate, clazz)) {
            return candidate;
         }

         if (candidate instanceof ParserDecorator) {
            candidate = ((ParserDecorator)candidate).getWrappedParser();
         }

         if (equals(candidate, clazz)) {
            return candidate;
         }

         if (candidate instanceof CompositeParser) {
            candidate = findInComposite((CompositeParser)candidate, clazz, context);
         }

         if (equals(candidate, clazz)) {
            return candidate;
         }
      }

      return null;
   }

   private static boolean equals(Parser parser, Class clazz) {
      return parser == null ? false : parser.getClass().equals(clazz);
   }
}
