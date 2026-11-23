package org.spark_project.jetty.server;

import java.io.IOException;
import org.spark_project.jetty.http.HttpContent;
import org.spark_project.jetty.http.MimeTypes;
import org.spark_project.jetty.http.ResourceHttpContent;
import org.spark_project.jetty.util.resource.Resource;
import org.spark_project.jetty.util.resource.ResourceFactory;

public class ResourceContentFactory implements HttpContent.Factory {
   private final ResourceFactory _factory;
   private final MimeTypes _mimeTypes;
   private final boolean _gzip;

   public ResourceContentFactory(ResourceFactory factory, MimeTypes mimeTypes, boolean gzip) {
      this._factory = factory;
      this._mimeTypes = mimeTypes;
      this._gzip = gzip;
   }

   public HttpContent getContent(String pathInContext, int maxBufferSize) throws IOException {
      Resource resource = this._factory.getResource(pathInContext);
      HttpContent loaded = this.load(pathInContext, resource, maxBufferSize);
      return loaded;
   }

   private HttpContent load(String pathInContext, Resource resource, int maxBufferSize) throws IOException {
      if (resource != null && resource.exists()) {
         if (resource.isDirectory()) {
            return new ResourceHttpContent(resource, this._mimeTypes.getMimeByExtension(resource.toString()), maxBufferSize);
         } else {
            String mt = this._mimeTypes.getMimeByExtension(pathInContext);
            if (this._gzip) {
               String pathInContextGz = pathInContext + ".gz";
               Resource resourceGz = this._factory.getResource(pathInContextGz);
               if (resourceGz.exists() && resourceGz.lastModified() >= resource.lastModified() && resourceGz.length() < resource.length()) {
                  return new ResourceHttpContent(resource, mt, maxBufferSize, new ResourceHttpContent(resourceGz, this._mimeTypes.getMimeByExtension(pathInContextGz), maxBufferSize));
               }
            }

            return new ResourceHttpContent(resource, mt, maxBufferSize);
         }
      } else {
         return null;
      }
   }

   public String toString() {
      return "ResourceContentFactory[" + this._factory + "]@" + this.hashCode();
   }
}
