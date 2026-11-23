package org.spark_project.jetty.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.spark_project.jetty.util.resource.Resource;

public class GzipHttpContent implements HttpContent {
   private final HttpContent _content;
   private final HttpContent _contentGz;
   public static final String ETAG_GZIP = "--gzip";
   public static final String ETAG_GZIP_QUOTE = "--gzip\"";
   public static final PreEncodedHttpField CONTENT_ENCODING_GZIP;

   public static String removeGzipFromETag(String etag) {
      if (etag == null) {
         return null;
      } else {
         int i = etag.indexOf("--gzip\"");
         return i < 0 ? etag : etag.substring(0, i) + '"';
      }
   }

   public GzipHttpContent(HttpContent content, HttpContent contentGz) {
      this._content = content;
      this._contentGz = contentGz;
   }

   public int hashCode() {
      return this._content.hashCode();
   }

   public boolean equals(Object obj) {
      return this._content.equals(obj);
   }

   public Resource getResource() {
      return this._content.getResource();
   }

   public HttpField getETag() {
      return new HttpField(HttpHeader.ETAG, this.getETagValue());
   }

   public String getETagValue() {
      return this._content.getResource().getWeakETag("--gzip");
   }

   public HttpField getLastModified() {
      return this._content.getLastModified();
   }

   public String getLastModifiedValue() {
      return this._content.getLastModifiedValue();
   }

   public HttpField getContentType() {
      return this._content.getContentType();
   }

   public String getContentTypeValue() {
      return this._content.getContentTypeValue();
   }

   public HttpField getContentEncoding() {
      return CONTENT_ENCODING_GZIP;
   }

   public String getContentEncodingValue() {
      return CONTENT_ENCODING_GZIP.getValue();
   }

   public String getCharacterEncoding() {
      return this._content.getCharacterEncoding();
   }

   public MimeTypes.Type getMimeType() {
      return this._content.getMimeType();
   }

   public void release() {
      this._content.release();
   }

   public ByteBuffer getIndirectBuffer() {
      return this._contentGz.getIndirectBuffer();
   }

   public ByteBuffer getDirectBuffer() {
      return this._contentGz.getDirectBuffer();
   }

   public HttpField getContentLength() {
      return this._contentGz.getContentLength();
   }

   public long getContentLengthValue() {
      return this._contentGz.getContentLengthValue();
   }

   public InputStream getInputStream() throws IOException {
      return this._contentGz.getInputStream();
   }

   public ReadableByteChannel getReadableByteChannel() throws IOException {
      return this._contentGz.getReadableByteChannel();
   }

   public String toString() {
      return String.format("GzipHttpContent@%x{r=%s|%s,lm=%s|%s,ct=%s}", this.hashCode(), this._content.getResource(), this._contentGz.getResource(), this._content.getResource().lastModified(), this._contentGz.getResource().lastModified(), this.getContentType());
   }

   public HttpContent getGzipContent() {
      return null;
   }

   static {
      CONTENT_ENCODING_GZIP = new PreEncodedHttpField(HttpHeader.CONTENT_ENCODING, "gzip");
   }
}
