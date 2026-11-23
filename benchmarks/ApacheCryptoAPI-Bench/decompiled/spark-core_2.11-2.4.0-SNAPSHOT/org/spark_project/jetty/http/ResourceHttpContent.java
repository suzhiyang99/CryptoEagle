package org.spark_project.jetty.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.resource.Resource;

public class ResourceHttpContent implements HttpContent {
   final Resource _resource;
   final String _contentType;
   final int _maxBuffer;
   HttpContent _gzip;
   String _etag;

   public ResourceHttpContent(Resource resource, String contentType) {
      this(resource, contentType, -1, (HttpContent)null);
   }

   public ResourceHttpContent(Resource resource, String contentType, int maxBuffer) {
      this(resource, contentType, maxBuffer, (HttpContent)null);
   }

   public ResourceHttpContent(Resource resource, String contentType, int maxBuffer, HttpContent gzip) {
      this._resource = resource;
      this._contentType = contentType;
      this._maxBuffer = maxBuffer;
      this._gzip = gzip;
   }

   public String getContentTypeValue() {
      return this._contentType;
   }

   public HttpField getContentType() {
      return this._contentType == null ? null : new HttpField(HttpHeader.CONTENT_TYPE, this._contentType);
   }

   public HttpField getContentEncoding() {
      return null;
   }

   public String getContentEncodingValue() {
      return null;
   }

   public String getCharacterEncoding() {
      return this._contentType == null ? null : MimeTypes.getCharsetFromContentType(this._contentType);
   }

   public MimeTypes.Type getMimeType() {
      return this._contentType == null ? null : (MimeTypes.Type)MimeTypes.CACHE.get(MimeTypes.getContentTypeWithoutCharset(this._contentType));
   }

   public HttpField getLastModified() {
      long lm = this._resource.lastModified();
      return lm >= 0L ? new HttpField(HttpHeader.LAST_MODIFIED, DateGenerator.formatDate(lm)) : null;
   }

   public String getLastModifiedValue() {
      long lm = this._resource.lastModified();
      return lm >= 0L ? DateGenerator.formatDate(lm) : null;
   }

   public ByteBuffer getDirectBuffer() {
      if (this._resource.length() > 0L && (this._maxBuffer <= 0 || (long)this._maxBuffer >= this._resource.length())) {
         try {
            return BufferUtil.toBuffer(this._resource, true);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } else {
         return null;
      }
   }

   public HttpField getETag() {
      return new HttpField(HttpHeader.ETAG, this.getETagValue());
   }

   public String getETagValue() {
      return this._resource.getWeakETag();
   }

   public ByteBuffer getIndirectBuffer() {
      if (this._resource.length() > 0L && (this._maxBuffer <= 0 || (long)this._maxBuffer >= this._resource.length())) {
         try {
            return BufferUtil.toBuffer(this._resource, false);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } else {
         return null;
      }
   }

   public HttpField getContentLength() {
      long l = this._resource.length();
      return l == -1L ? null : new HttpField.LongValueHttpField(HttpHeader.CONTENT_LENGTH, this._resource.length());
   }

   public long getContentLengthValue() {
      return this._resource.length();
   }

   public InputStream getInputStream() throws IOException {
      return this._resource.getInputStream();
   }

   public ReadableByteChannel getReadableByteChannel() throws IOException {
      return this._resource.getReadableByteChannel();
   }

   public Resource getResource() {
      return this._resource;
   }

   public void release() {
      this._resource.close();
   }

   public String toString() {
      return String.format("%s@%x{r=%s,gz=%b}", this.getClass().getSimpleName(), this.hashCode(), this._resource, this._gzip != null);
   }

   public HttpContent getGzipContent() {
      return this._gzip == null ? null : new GzipHttpContent(this, this._gzip);
   }
}
