package org.apache.tika.mime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Document;

public class MimeTypesFactory {
   public static final String CUSTOM_MIMES_SYS_PROP = "tika.custom-mimetypes";

   public static MimeTypes create() {
      return new MimeTypes();
   }

   public static MimeTypes create(Document document) throws MimeTypeException {
      MimeTypes mimeTypes = new MimeTypes();
      (new MimeTypesReader(mimeTypes)).read(document);
      mimeTypes.init();
      return mimeTypes;
   }

   public static MimeTypes create(InputStream... inputStreams) throws IOException, MimeTypeException {
      MimeTypes mimeTypes = new MimeTypes();
      MimeTypesReader reader = new MimeTypesReader(mimeTypes);

      for(InputStream inputStream : inputStreams) {
         reader.read(inputStream);
      }

      mimeTypes.init();
      return mimeTypes;
   }

   public static MimeTypes create(InputStream stream) throws IOException, MimeTypeException {
      return create(stream);
   }

   public static MimeTypes create(URL... urls) throws IOException, MimeTypeException {
      InputStream[] streams = new InputStream[urls.length];

      for(int i = 0; i < streams.length; ++i) {
         streams[i] = urls[i].openStream();
      }

      boolean var13 = false;

      MimeTypes var15;
      try {
         var13 = true;
         var15 = create(streams);
         var13 = false;
      } finally {
         if (var13) {
            for(InputStream stream : streams) {
               stream.close();
            }

         }
      }

      for(InputStream stream : streams) {
         stream.close();
      }

      return var15;
   }

   public static MimeTypes create(URL url) throws IOException, MimeTypeException {
      return create(url);
   }

   public static MimeTypes create(String filePath) throws IOException, MimeTypeException {
      return create(MimeTypesReader.class.getResource(filePath));
   }

   public static MimeTypes create(String coreFilePath, String extensionFilePath) throws IOException, MimeTypeException {
      return create(coreFilePath, extensionFilePath, (ClassLoader)null);
   }

   public static MimeTypes create(String coreFilePath, String extensionFilePath, ClassLoader classLoader) throws IOException, MimeTypeException {
      if (classLoader == null) {
         classLoader = MimeTypesReader.class.getClassLoader();
      }

      String classPrefix = MimeTypesReader.class.getPackage().getName().replace('.', '/') + "/";
      URL coreURL = classLoader.getResource(classPrefix + coreFilePath);
      List<URL> extensionURLs = Collections.list(classLoader.getResources(classPrefix + extensionFilePath));
      List<URL> urls = new ArrayList();
      urls.add(coreURL);
      urls.addAll(extensionURLs);
      String customMimesPath = System.getProperty("tika.custom-mimetypes");
      if (customMimesPath != null) {
         File externalFile = new File(customMimesPath);
         if (!externalFile.exists()) {
            throw new IOException("Specified custom mimetypes file not found: " + customMimesPath);
         }

         URL externalURL = externalFile.toURI().toURL();
         urls.add(externalURL);
      }

      return create((URL[])urls.toArray(new URL[urls.size()]));
   }
}
