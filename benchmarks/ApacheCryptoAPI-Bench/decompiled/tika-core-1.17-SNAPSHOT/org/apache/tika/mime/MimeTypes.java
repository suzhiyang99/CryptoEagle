package org.apache.tika.mime;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.TextDetector;
import org.apache.tika.detect.XmlRootExtractor;
import org.apache.tika.metadata.Metadata;

public final class MimeTypes implements Detector, Serializable {
   private static final long serialVersionUID = -1350863170146349036L;
   public static final String OCTET_STREAM = "application/octet-stream";
   public static final String PLAIN_TEXT = "text/plain";
   public static final String XML = "application/xml";
   private final MimeType rootMimeType;
   private final List<MimeType> rootMimeTypeL;
   private final MimeType textMimeType;
   private final MimeType htmlMimeType;
   private final MimeType xmlMimeType;
   private final MediaTypeRegistry registry = new MediaTypeRegistry();
   private final Map<MediaType, MimeType> types = new HashMap();
   private Patterns patterns;
   private final List<Magic> magics;
   private final List<MimeType> xmls;
   private static MimeTypes DEFAULT_TYPES = null;
   private static Map<ClassLoader, MimeTypes> CLASSLOADER_SPECIFIC_DEFAULT_TYPES = new HashMap();

   public MimeTypes() {
      this.patterns = new Patterns(this.registry);
      this.magics = new ArrayList();
      this.xmls = new ArrayList();
      this.rootMimeType = new MimeType(MediaType.OCTET_STREAM);
      this.textMimeType = new MimeType(MediaType.TEXT_PLAIN);
      this.htmlMimeType = new MimeType(MediaType.TEXT_HTML);
      this.xmlMimeType = new MimeType(MediaType.APPLICATION_XML);
      this.rootMimeTypeL = Collections.singletonList(this.rootMimeType);
      this.add(this.rootMimeType);
      this.add(this.textMimeType);
      this.add(this.xmlMimeType);
   }

   /** @deprecated */
   public MimeType getMimeType(String name) {
      MimeType type = this.patterns.matches(name);
      if (type != null) {
         return type;
      } else {
         type = this.patterns.matches(name.toLowerCase(Locale.ENGLISH));
         return type != null ? type : this.rootMimeType;
      }
   }

   /** @deprecated */
   public MimeType getMimeType(File file) throws MimeTypeException, IOException {
      return this.forName((new Tika(this)).detect(file));
   }

   List<MimeType> getMimeType(byte[] data) {
      if (data == null) {
         throw new IllegalArgumentException("Data is missing");
      } else if (data.length == 0) {
         return this.rootMimeTypeL;
      } else {
         List<MimeType> result = new ArrayList(1);
         int currentPriority = -1;

         for(Magic magic : this.magics) {
            if (currentPriority > 0 && currentPriority > magic.getPriority()) {
               break;
            }

            if (magic.eval(data)) {
               result.add(magic.getType());
               currentPriority = magic.getPriority();
            }
         }

         if (result.isEmpty()) {
            try {
               TextDetector detector = new TextDetector(this.getMinLength());
               ByteArrayInputStream stream = new ByteArrayInputStream(data);
               MimeType type = this.forName(detector.detect(stream, new Metadata()).toString());
               return Collections.singletonList(type);
            } catch (Exception var11) {
               return this.rootMimeTypeL;
            }
         } else {
            for(int i = 0; i < result.size(); ++i) {
               MimeType matched = (MimeType)result.get(i);
               if ("application/xml".equals(matched.getName()) || "text/html".equals(matched.getName())) {
                  XmlRootExtractor extractor = new XmlRootExtractor();
                  QName rootElement = extractor.extractRootElement(data);
                  if (rootElement != null) {
                     for(MimeType type : this.xmls) {
                        if (type.matchesXML(rootElement.getNamespaceURI(), rootElement.getLocalPart())) {
                           result.set(i, type);
                           break;
                        }
                     }
                  } else if ("application/xml".equals(matched.getName())) {
                     boolean isHTML = false;

                     for(Magic magic : this.magics) {
                        if (magic.getType().equals(this.htmlMimeType) && magic.eval(data)) {
                           isHTML = true;
                           break;
                        }
                     }

                     if (isHTML) {
                        result.set(i, this.htmlMimeType);
                     } else {
                        result.set(i, this.textMimeType);
                     }
                  }
               }
            }

            return result;
         }
      }
   }

   byte[] readMagicHeader(InputStream stream) throws IOException {
      if (stream == null) {
         throw new IllegalArgumentException("InputStream is missing");
      } else {
         byte[] bytes = new byte[this.getMinLength()];
         int totalRead = 0;

         for(int lastRead = stream.read(bytes); lastRead != -1; lastRead = stream.read(bytes, totalRead, bytes.length - totalRead)) {
            totalRead += lastRead;
            if (totalRead == bytes.length) {
               return bytes;
            }
         }

         byte[] shorter = new byte[totalRead];
         System.arraycopy(bytes, 0, shorter, 0, totalRead);
         return shorter;
      }
   }

   public MimeType forName(String name) throws MimeTypeException {
      MediaType type = MediaType.parse(name);
      if (type != null) {
         MediaType normalisedType = this.registry.normalize(type);
         MimeType mime = (MimeType)this.types.get(normalisedType);
         if (mime == null) {
            synchronized(this) {
               mime = (MimeType)this.types.get(normalisedType);
               if (mime == null) {
                  mime = new MimeType(type);
                  this.add(mime);
                  this.types.put(type, mime);
               }
            }
         }

         return mime;
      } else {
         throw new MimeTypeException("Invalid media type name: " + name);
      }
   }

   public MimeType getRegisteredMimeType(String name) throws MimeTypeException {
      MediaType type = MediaType.parse(name);
      if (type != null) {
         MediaType normalisedType = this.registry.normalize(type);
         MimeType candidate = (MimeType)this.types.get(normalisedType);
         if (candidate != null) {
            return candidate;
         } else {
            return normalisedType.hasParameters() ? (MimeType)this.types.get(normalisedType.getBaseType()) : null;
         }
      } else {
         throw new MimeTypeException("Invalid media type name: " + name);
      }
   }

   public synchronized void setSuperType(MimeType type, MediaType parent) {
      this.registry.addSuperType(type.getType(), parent);
   }

   synchronized void addAlias(MimeType type, MediaType alias) {
      this.registry.addAlias(type.getType(), alias);
   }

   public void addPattern(MimeType type, String pattern) throws MimeTypeException {
      this.addPattern(type, pattern, false);
   }

   public void addPattern(MimeType type, String pattern, boolean isRegex) throws MimeTypeException {
      this.patterns.add(pattern, isRegex, type);
   }

   public MediaTypeRegistry getMediaTypeRegistry() {
      return this.registry;
   }

   public int getMinLength() {
      return 65536;
   }

   void add(MimeType type) {
      this.registry.addType(type.getType());
      this.types.put(type.getType(), type);
      if (type.hasMagic()) {
         this.magics.addAll(type.getMagics());
      }

      if (type.hasRootXML()) {
         this.xmls.add(type);
      }

   }

   void init() {
      for(MimeType type : this.types.values()) {
         this.magics.addAll(type.getMagics());
         if (type.hasRootXML()) {
            this.xmls.add(type);
         }
      }

      Collections.sort(this.magics);
      Collections.sort(this.xmls);
   }

   public MediaType detect(InputStream input, Metadata metadata) throws IOException {
      List<MimeType> possibleTypes = null;
      if (input != null) {
         input.mark(this.getMinLength());

         try {
            byte[] prefix = this.readMagicHeader(input);
            possibleTypes = this.getMimeType(prefix);
         } finally {
            input.reset();
         }
      }

      String resourceName = metadata.get("resourceName");
      if (resourceName != null) {
         String name = null;

         try {
            URI uri = new URI(resourceName);
            String path = uri.getPath();
            if (path != null) {
               int slash = path.lastIndexOf(47);
               if (slash + 1 < path.length()) {
                  name = path.substring(slash + 1);
               }
            }
         } catch (URISyntaxException var13) {
            name = resourceName;
         }

         if (name != null) {
            MimeType hint = this.getMimeType(name);
            possibleTypes = this.applyHint(possibleTypes, hint);
         }
      }

      String typeName = metadata.get("Content-Type");
      if (typeName != null) {
         try {
            MimeType hint = this.forName(typeName);
            possibleTypes = this.applyHint(possibleTypes, hint);
         } catch (MimeTypeException var12) {
         }
      }

      return possibleTypes != null && !possibleTypes.isEmpty() ? ((MimeType)possibleTypes.get(0)).getType() : MediaType.OCTET_STREAM;
   }

   private List<MimeType> applyHint(List<MimeType> possibleTypes, MimeType hint) {
      if (possibleTypes != null && !possibleTypes.isEmpty()) {
         for(int i = 0; i < possibleTypes.size(); ++i) {
            MimeType type = (MimeType)possibleTypes.get(i);
            if (hint.equals(type) || this.registry.isSpecializationOf(hint.getType(), type.getType())) {
               return Collections.singletonList(hint);
            }
         }

         return possibleTypes;
      } else {
         return Collections.singletonList(hint);
      }
   }

   public static synchronized MimeTypes getDefaultMimeTypes() {
      return getDefaultMimeTypes((ClassLoader)null);
   }

   public static synchronized MimeTypes getDefaultMimeTypes(ClassLoader classLoader) {
      MimeTypes types = DEFAULT_TYPES;
      if (classLoader != null) {
         types = (MimeTypes)CLASSLOADER_SPECIFIC_DEFAULT_TYPES.get(classLoader);
      }

      if (types == null) {
         try {
            types = MimeTypesFactory.create("tika-mimetypes.xml", "custom-mimetypes.xml", classLoader);
         } catch (MimeTypeException e) {
            throw new RuntimeException("Unable to parse the default media type registry", e);
         } catch (IOException e) {
            throw new RuntimeException("Unable to read the default media type registry", e);
         }

         if (classLoader == null) {
            DEFAULT_TYPES = types;
         } else {
            CLASSLOADER_SPECIFIC_DEFAULT_TYPES.put(classLoader, types);
         }
      }

      return types;
   }
}
