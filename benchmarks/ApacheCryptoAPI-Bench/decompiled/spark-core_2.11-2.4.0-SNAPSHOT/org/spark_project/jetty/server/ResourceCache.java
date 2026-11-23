package org.spark_project.jetty.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.spark_project.jetty.http.DateGenerator;
import org.spark_project.jetty.http.GzipHttpContent;
import org.spark_project.jetty.http.HttpContent;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.MimeTypes;
import org.spark_project.jetty.http.PreEncodedHttpField;
import org.spark_project.jetty.http.ResourceHttpContent;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.resource.Resource;
import org.spark_project.jetty.util.resource.ResourceFactory;

public class ResourceCache implements HttpContent.Factory {
   private static final Logger LOG = Log.getLogger(ResourceCache.class);
   private final ConcurrentMap<String, CachedHttpContent> _cache;
   private final AtomicInteger _cachedSize;
   private final AtomicInteger _cachedFiles;
   private final ResourceFactory _factory;
   private final ResourceCache _parent;
   private final MimeTypes _mimeTypes;
   private final boolean _etags;
   private final boolean _gzip;
   private final boolean _useFileMappedBuffer;
   private int _maxCachedFileSize = 134217728;
   private int _maxCachedFiles = 2048;
   private int _maxCacheSize = 268435456;

   public ResourceCache(ResourceCache parent, ResourceFactory factory, MimeTypes mimeTypes, boolean useFileMappedBuffer, boolean etags, boolean gzip) {
      this._factory = factory;
      this._cache = new ConcurrentHashMap();
      this._cachedSize = new AtomicInteger();
      this._cachedFiles = new AtomicInteger();
      this._mimeTypes = mimeTypes;
      this._parent = parent;
      this._useFileMappedBuffer = useFileMappedBuffer;
      this._etags = etags;
      this._gzip = gzip;
   }

   public int getCachedSize() {
      return this._cachedSize.get();
   }

   public int getCachedFiles() {
      return this._cachedFiles.get();
   }

   public int getMaxCachedFileSize() {
      return this._maxCachedFileSize;
   }

   public void setMaxCachedFileSize(int maxCachedFileSize) {
      this._maxCachedFileSize = maxCachedFileSize;
      this.shrinkCache();
   }

   public int getMaxCacheSize() {
      return this._maxCacheSize;
   }

   public void setMaxCacheSize(int maxCacheSize) {
      this._maxCacheSize = maxCacheSize;
      this.shrinkCache();
   }

   public int getMaxCachedFiles() {
      return this._maxCachedFiles;
   }

   public void setMaxCachedFiles(int maxCachedFiles) {
      this._maxCachedFiles = maxCachedFiles;
      this.shrinkCache();
   }

   public boolean isUseFileMappedBuffer() {
      return this._useFileMappedBuffer;
   }

   public void flushCache() {
      if (this._cache != null) {
         while(this._cache.size() > 0) {
            for(String path : this._cache.keySet()) {
               CachedHttpContent content = (CachedHttpContent)this._cache.remove(path);
               if (content != null) {
                  content.invalidate();
               }
            }
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public HttpContent lookup(String pathInContext) throws IOException {
      return this.getContent(pathInContext, this._maxCachedFileSize);
   }

   public HttpContent getContent(String pathInContext, int maxBufferSize) throws IOException {
      CachedHttpContent content = (CachedHttpContent)this._cache.get(pathInContext);
      if (content != null && content.isValid()) {
         return content;
      } else {
         Resource resource = this._factory.getResource(pathInContext);
         HttpContent loaded = this.load(pathInContext, resource, maxBufferSize);
         if (loaded != null) {
            return loaded;
         } else {
            if (this._parent != null) {
               HttpContent httpContent = this._parent.getContent(pathInContext, maxBufferSize);
               if (httpContent != null) {
                  return httpContent;
               }
            }

            return null;
         }
      }
   }

   protected boolean isCacheable(Resource resource) {
      if (this._maxCachedFiles <= 0) {
         return false;
      } else {
         long len = resource.length();
         return len > 0L && (this._useFileMappedBuffer || len < (long)this._maxCachedFileSize && len < (long)this._maxCacheSize);
      }
   }

   private HttpContent load(String pathInContext, Resource resource, int maxBufferSize) throws IOException {
      if (resource != null && resource.exists()) {
         if (resource.isDirectory()) {
            return new ResourceHttpContent(resource, this._mimeTypes.getMimeByExtension(resource.toString()), this.getMaxCachedFileSize());
         } else if (!this.isCacheable(resource)) {
            String mt = this._mimeTypes.getMimeByExtension(pathInContext);
            if (this._gzip) {
               String pathInContextGz = pathInContext + ".gz";
               CachedHttpContent contentGz = (CachedHttpContent)this._cache.get(pathInContextGz);
               if (contentGz != null && contentGz.isValid() && contentGz.getResource().lastModified() >= resource.lastModified()) {
                  return new ResourceHttpContent(resource, mt, maxBufferSize, contentGz);
               }

               Resource resourceGz = this._factory.getResource(pathInContextGz);
               if (resourceGz.exists() && resourceGz.lastModified() >= resource.lastModified() && resourceGz.length() < resource.length()) {
                  return new ResourceHttpContent(resource, mt, maxBufferSize, new ResourceHttpContent(resourceGz, this._mimeTypes.getMimeByExtension(pathInContextGz), maxBufferSize));
               }
            }

            return new ResourceHttpContent(resource, mt, maxBufferSize);
         } else {
            CachedHttpContent content = null;
            if (this._gzip) {
               String pathInContextGz = pathInContext + ".gz";
               CachedHttpContent contentGz = (CachedHttpContent)this._cache.get(pathInContextGz);
               if (contentGz == null || !contentGz.isValid()) {
                  contentGz = null;
                  Resource resourceGz = this._factory.getResource(pathInContextGz);
                  if (resourceGz.exists() && resourceGz.lastModified() >= resource.lastModified() && resourceGz.length() < resource.length()) {
                     contentGz = new CachedHttpContent(pathInContextGz, resourceGz, (CachedHttpContent)null);
                     CachedHttpContent added = (CachedHttpContent)this._cache.putIfAbsent(pathInContextGz, contentGz);
                     if (added != null) {
                        contentGz.invalidate();
                        contentGz = added;
                     }
                  }
               }

               content = new CachedHttpContent(pathInContext, resource, contentGz);
            } else {
               content = new CachedHttpContent(pathInContext, resource, (CachedHttpContent)null);
            }

            CachedHttpContent added = (CachedHttpContent)this._cache.putIfAbsent(pathInContext, content);
            if (added != null) {
               content.invalidate();
               content = added;
            }

            return content;
         }
      } else {
         return null;
      }
   }

   private void shrinkCache() {
      while(this._cache.size() > 0 && (this._cachedFiles.get() > this._maxCachedFiles || this._cachedSize.get() > this._maxCacheSize)) {
         SortedSet<CachedHttpContent> sorted = new TreeSet(new Comparator<CachedHttpContent>() {
            public int compare(CachedHttpContent c1, CachedHttpContent c2) {
               if (c1._lastAccessed < c2._lastAccessed) {
                  return -1;
               } else if (c1._lastAccessed > c2._lastAccessed) {
                  return 1;
               } else {
                  return c1._contentLengthValue < c2._contentLengthValue ? -1 : c1._key.compareTo(c2._key);
               }
            }
         });

         for(CachedHttpContent content : this._cache.values()) {
            sorted.add(content);
         }

         for(CachedHttpContent content : sorted) {
            if (this._cachedFiles.get() <= this._maxCachedFiles && this._cachedSize.get() <= this._maxCacheSize) {
               break;
            }

            if (content == this._cache.remove(content.getKey())) {
               content.invalidate();
            }
         }
      }

   }

   protected ByteBuffer getIndirectBuffer(Resource resource) {
      try {
         return BufferUtil.toBuffer(resource, true);
      } catch (IllegalArgumentException | IOException e) {
         LOG.warn(e);
         return null;
      }
   }

   protected ByteBuffer getMappedBuffer(Resource resource) {
      try {
         if (this._useFileMappedBuffer && resource.getFile() != null && resource.length() < 2147483647L) {
            return BufferUtil.toMappedBuffer(resource.getFile());
         }
      } catch (IllegalArgumentException | IOException e) {
         LOG.warn(e);
      }

      return null;
   }

   protected ByteBuffer getDirectBuffer(Resource resource) {
      try {
         return BufferUtil.toBuffer(resource, true);
      } catch (IllegalArgumentException | IOException e) {
         LOG.warn(e);
         return null;
      }
   }

   public String toString() {
      return "ResourceCache[" + this._parent + "," + this._factory + "]@" + this.hashCode();
   }

   public class CachedHttpContent implements HttpContent {
      final String _key;
      final Resource _resource;
      final int _contentLengthValue;
      final HttpField _contentType;
      final String _characterEncoding;
      final MimeTypes.Type _mimeType;
      final HttpField _contentLength;
      final HttpField _lastModified;
      final long _lastModifiedValue;
      final HttpField _etag;
      final CachedGzipHttpContent _gzipped;
      volatile long _lastAccessed;
      AtomicReference<ByteBuffer> _indirectBuffer = new AtomicReference();
      AtomicReference<ByteBuffer> _directBuffer = new AtomicReference();

      CachedHttpContent(String pathInContext, Resource resource, CachedHttpContent gzipped) {
         this._key = pathInContext;
         this._resource = resource;
         String contentType = ResourceCache.this._mimeTypes.getMimeByExtension(this._resource.toString());
         this._contentType = contentType == null ? null : new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, contentType);
         this._characterEncoding = this._contentType == null ? null : MimeTypes.getCharsetFromContentType(contentType);
         this._mimeType = this._contentType == null ? null : (MimeTypes.Type)MimeTypes.CACHE.get(MimeTypes.getContentTypeWithoutCharset(contentType));
         boolean exists = resource.exists();
         this._lastModifiedValue = exists ? resource.lastModified() : -1L;
         this._lastModified = this._lastModifiedValue == -1L ? null : new PreEncodedHttpField(HttpHeader.LAST_MODIFIED, DateGenerator.formatDate(this._lastModifiedValue));
         this._contentLengthValue = exists ? (int)resource.length() : 0;
         this._contentLength = new PreEncodedHttpField(HttpHeader.CONTENT_LENGTH, Long.toString((long)this._contentLengthValue));
         if (ResourceCache.this._cachedFiles.incrementAndGet() > ResourceCache.this._maxCachedFiles) {
            ResourceCache.this.shrinkCache();
         }

         this._lastAccessed = System.currentTimeMillis();
         this._etag = ResourceCache.this._etags ? new PreEncodedHttpField(HttpHeader.ETAG, resource.getWeakETag()) : null;
         this._gzipped = gzipped == null ? null : ResourceCache.this.new CachedGzipHttpContent(this, gzipped);
      }

      public String getKey() {
         return this._key;
      }

      public boolean isCached() {
         return this._key != null;
      }

      public boolean isMiss() {
         return false;
      }

      public Resource getResource() {
         return this._resource;
      }

      public HttpField getETag() {
         return this._etag;
      }

      public String getETagValue() {
         return this._etag.getValue();
      }

      boolean isValid() {
         if (this._lastModifiedValue == this._resource.lastModified() && (long)this._contentLengthValue == this._resource.length()) {
            this._lastAccessed = System.currentTimeMillis();
            return true;
         } else {
            if (this == ResourceCache.this._cache.remove(this._key)) {
               this.invalidate();
            }

            return false;
         }
      }

      protected void invalidate() {
         ByteBuffer indirect = (ByteBuffer)this._indirectBuffer.get();
         if (indirect != null && this._indirectBuffer.compareAndSet(indirect, (Object)null)) {
            ResourceCache.this._cachedSize.addAndGet(-BufferUtil.length(indirect));
         }

         ByteBuffer direct = (ByteBuffer)this._directBuffer.get();
         if (direct != null && !BufferUtil.isMappedBuffer(direct) && this._directBuffer.compareAndSet(direct, (Object)null)) {
            ResourceCache.this._cachedSize.addAndGet(-BufferUtil.length(direct));
         }

         ResourceCache.this._cachedFiles.decrementAndGet();
         this._resource.close();
      }

      public HttpField getLastModified() {
         return this._lastModified;
      }

      public String getLastModifiedValue() {
         return this._lastModified == null ? null : this._lastModified.getValue();
      }

      public HttpField getContentType() {
         return this._contentType;
      }

      public String getContentTypeValue() {
         return this._contentType == null ? null : this._contentType.getValue();
      }

      public HttpField getContentEncoding() {
         return null;
      }

      public String getContentEncodingValue() {
         return null;
      }

      public String getCharacterEncoding() {
         return this._characterEncoding;
      }

      public MimeTypes.Type getMimeType() {
         return this._mimeType;
      }

      public void release() {
      }

      public ByteBuffer getIndirectBuffer() {
         ByteBuffer buffer = (ByteBuffer)this._indirectBuffer.get();
         if (buffer == null) {
            ByteBuffer buffer2 = ResourceCache.this.getIndirectBuffer(this._resource);
            if (buffer2 == null) {
               ResourceCache.LOG.warn("Could not load " + this);
            } else if (this._indirectBuffer.compareAndSet((Object)null, buffer2)) {
               buffer = buffer2;
               if (ResourceCache.this._cachedSize.addAndGet(BufferUtil.length(buffer2)) > ResourceCache.this._maxCacheSize) {
                  ResourceCache.this.shrinkCache();
               }
            } else {
               buffer = (ByteBuffer)this._indirectBuffer.get();
            }
         }

         return buffer == null ? null : buffer.slice();
      }

      public ByteBuffer getDirectBuffer() {
         ByteBuffer buffer = (ByteBuffer)this._directBuffer.get();
         if (buffer == null) {
            ByteBuffer mapped = ResourceCache.this.getMappedBuffer(this._resource);
            ByteBuffer direct = mapped == null ? ResourceCache.this.getDirectBuffer(this._resource) : mapped;
            if (direct == null) {
               ResourceCache.LOG.warn("Could not load " + this);
            } else if (this._directBuffer.compareAndSet((Object)null, direct)) {
               buffer = direct;
               if (mapped == null && ResourceCache.this._cachedSize.addAndGet(BufferUtil.length(direct)) > ResourceCache.this._maxCacheSize) {
                  ResourceCache.this.shrinkCache();
               }
            } else {
               buffer = (ByteBuffer)this._directBuffer.get();
            }
         }

         return buffer == null ? null : buffer.asReadOnlyBuffer();
      }

      public HttpField getContentLength() {
         return this._contentLength;
      }

      public long getContentLengthValue() {
         return (long)this._contentLengthValue;
      }

      public InputStream getInputStream() throws IOException {
         ByteBuffer indirect = this.getIndirectBuffer();
         return (InputStream)(indirect != null && indirect.hasArray() ? new ByteArrayInputStream(indirect.array(), indirect.arrayOffset() + indirect.position(), indirect.remaining()) : this._resource.getInputStream());
      }

      public ReadableByteChannel getReadableByteChannel() throws IOException {
         return this._resource.getReadableByteChannel();
      }

      public String toString() {
         return String.format("CachedContent@%x{r=%s,e=%b,lm=%s,ct=%s,gz=%b}", this.hashCode(), this._resource, this._resource.exists(), this._lastModified, this._contentType, this._gzipped != null);
      }

      public HttpContent getGzipContent() {
         return this._gzipped != null && this._gzipped.isValid() ? this._gzipped : null;
      }
   }

   public class CachedGzipHttpContent extends GzipHttpContent {
      private final CachedHttpContent _content;
      private final CachedHttpContent _contentGz;
      private final HttpField _etag;

      CachedGzipHttpContent(CachedHttpContent content, CachedHttpContent contentGz) {
         super(content, contentGz);
         this._content = content;
         this._contentGz = contentGz;
         this._etag = ResourceCache.this._etags ? new PreEncodedHttpField(HttpHeader.ETAG, this._content.getResource().getWeakETag("--gzip")) : null;
      }

      public boolean isValid() {
         return this._contentGz.isValid() && this._content.isValid() && this._content.getResource().lastModified() <= this._contentGz.getResource().lastModified();
      }

      public HttpField getETag() {
         return this._etag != null ? this._etag : super.getETag();
      }

      public String getETagValue() {
         return this._etag != null ? this._etag.getValue() : super.getETagValue();
      }

      public String toString() {
         return "Cached" + super.toString();
      }
   }
}
