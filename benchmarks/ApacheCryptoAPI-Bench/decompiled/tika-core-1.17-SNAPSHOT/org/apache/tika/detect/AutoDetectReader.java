package org.apache.tika.detect;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import org.apache.tika.config.LoadErrorHandler;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.utils.CharsetUtils;
import org.xml.sax.InputSource;

public class AutoDetectReader extends BufferedReader {
   private static final ServiceLoader DEFAULT_LOADER = new ServiceLoader(AutoDetectReader.class.getClassLoader());
   private final Charset charset;

   private static Charset detect(InputStream input, Metadata metadata, List<EncodingDetector> detectors, LoadErrorHandler handler) throws IOException, TikaException {
      for(EncodingDetector detector : detectors) {
         try {
            Charset charset = detector.detect(input, metadata);
            if (charset != null) {
               return charset;
            }
         } catch (NoClassDefFoundError e) {
            handler.handleLoadError(detector.getClass().getName(), e);
         }
      }

      MediaType type = MediaType.parse(metadata.get("Content-Type"));
      if (type != null) {
         String charset = (String)type.getParameters().get("charset");
         if (charset != null) {
            try {
               return CharsetUtils.forName(charset);
            } catch (Exception var8) {
            }
         }
      }

      throw new TikaException("Failed to detect the character encoding of a document");
   }

   private AutoDetectReader(InputStream stream, Charset charset) throws IOException {
      super(new InputStreamReader(stream, charset));
      this.charset = charset;
      this.mark(1);
      if (this.read() != 65279) {
         this.reset();
      }

   }

   private AutoDetectReader(InputStream stream, Metadata metadata, List<EncodingDetector> detectors, LoadErrorHandler handler) throws IOException, TikaException {
      this(stream, detect(stream, metadata, detectors, handler));
   }

   public AutoDetectReader(InputStream stream, Metadata metadata, EncodingDetector encodingDetector) throws IOException, TikaException {
      this(getBuffered(stream), metadata, Collections.singletonList(encodingDetector), DEFAULT_LOADER.getLoadErrorHandler());
   }

   public AutoDetectReader(InputStream stream, Metadata metadata, ServiceLoader loader) throws IOException, TikaException {
      this(getBuffered(stream), metadata, loader.loadServiceProviders(EncodingDetector.class), loader.getLoadErrorHandler());
   }

   public AutoDetectReader(InputStream stream, Metadata metadata) throws IOException, TikaException {
      this(stream, metadata, DEFAULT_LOADER);
   }

   public AutoDetectReader(InputStream stream) throws IOException, TikaException {
      this(stream, new Metadata());
   }

   private static InputStream getBuffered(InputStream stream) {
      return (InputStream)(stream.markSupported() ? stream : new BufferedInputStream(stream));
   }

   public Charset getCharset() {
      return this.charset;
   }

   public InputSource asInputSource() {
      InputSource source = new InputSource(this);
      source.setEncoding(this.charset.name());
      return source;
   }
}
