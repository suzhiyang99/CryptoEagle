package org.apache.openejb.core.webservices;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import javax.wsdl.xml.WSDLLocator;
import org.xml.sax.InputSource;

public class WsdlResolver implements WSDLLocator {
   private String baseUri;
   private String importedUri;
   private InputSource inputSource;

   public WsdlResolver(String baseURI, InputSource is) {
      this.baseUri = baseURI;
      this.inputSource = is;
   }

   public InputSource getBaseInputSource() {
      return this.inputSource;
   }

   public String getBaseURI() {
      return this.baseUri;
   }

   public String getLatestImportURI() {
      return this.importedUri;
   }

   public InputSource getImportInputSource(String parent, String importLocation) {
      this.baseUri = parent;

      try {
         URL parentUrl = new URL(parent);
         URL importUrl = new URL(parentUrl, importLocation);
         if (importUrl != null && !importUrl.getProtocol().startsWith("file")) {
            URLConnection con = importUrl.openConnection();
            con.setUseCaches(false);
            this.inputSource = new InputSource(con.getInputStream());
         } else {
            File file = new File(importUrl.toURI());
            if (file.exists()) {
               UriResolver resolver = new UriResolver(parent.toString(), importLocation);
               this.inputSource = new InputSource(resolver.getInputStream());
            } else {
               UriResolver resolver = new UriResolver(importLocation);
               if (resolver.isResolved()) {
                  this.inputSource = new InputSource(resolver.getInputStream());
               }
            }
         }

         this.importedUri = importUrl.toURI().toString();
      } catch (IOException | URISyntaxException var7) {
      }

      return this.inputSource;
   }

   public void close() {
      if (this.inputSource.getByteStream() != null) {
         try {
            this.inputSource.getByteStream().close();
         } catch (IOException var2) {
         }
      }

   }
}
