package org.spark_project.jetty.client;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import org.spark_project.jetty.client.api.ContentProvider;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class HttpContent implements Callback, Closeable {
   private static final Logger LOG = Log.getLogger(HttpContent.class);
   private static final ByteBuffer AFTER = ByteBuffer.allocate(0);
   private static final ByteBuffer CLOSE = ByteBuffer.allocate(0);
   private final ContentProvider provider;
   private final Iterator<ByteBuffer> iterator;
   private ByteBuffer buffer;
   private ByteBuffer content;
   private boolean last;

   public HttpContent(ContentProvider provider) {
      this.provider = provider;
      this.iterator = provider == null ? Collections.emptyIterator() : provider.iterator();
   }

   public boolean hasContent() {
      return this.provider != null;
   }

   public boolean isLast() {
      return this.last;
   }

   public ByteBuffer getByteBuffer() {
      return this.buffer;
   }

   public ByteBuffer getContent() {
      return this.content;
   }

   public boolean advance() {
      if (this.iterator instanceof Synchronizable) {
         synchronized(((Synchronizable)this.iterator).getLock()) {
            return this.advance(this.iterator);
         }
      } else {
         return this.advance(this.iterator);
      }
   }

   private boolean advance(Iterator<ByteBuffer> iterator) {
      boolean hasNext = iterator.hasNext();
      ByteBuffer bytes = hasNext ? (ByteBuffer)iterator.next() : null;
      boolean hasMore = hasNext && iterator.hasNext();
      boolean wasLast = this.last;
      this.last = !hasMore;
      if (hasNext) {
         this.buffer = bytes;
         this.content = bytes == null ? null : bytes.slice();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Advanced content to {} chunk {}", hasMore ? "next" : "last", String.valueOf(bytes));
         }

         return bytes != null;
      } else {
         if (wasLast) {
            this.buffer = this.content = AFTER;
            if (LOG.isDebugEnabled()) {
               LOG.debug("Advanced content past last chunk");
            }
         } else {
            this.buffer = this.content = CLOSE;
            if (LOG.isDebugEnabled()) {
               LOG.debug("Advanced content to last chunk");
            }
         }

         return false;
      }
   }

   public boolean isConsumed() {
      return this.buffer == AFTER;
   }

   public void succeeded() {
      if (!this.isConsumed()) {
         if (this.buffer != CLOSE) {
            if (this.iterator instanceof Callback) {
               ((Callback)this.iterator).succeeded();
            }

         }
      }
   }

   public void failed(Throwable x) {
      if (!this.isConsumed()) {
         if (this.buffer != CLOSE) {
            if (this.iterator instanceof Callback) {
               ((Callback)this.iterator).failed(x);
            }

         }
      }
   }

   public void close() {
      try {
         if (this.iterator instanceof Closeable) {
            ((Closeable)this.iterator).close();
         }
      } catch (Throwable x) {
         LOG.ignore(x);
      }

   }

   public String toString() {
      return String.format("%s@%x - has=%b,last=%b,consumed=%b,buffer=%s", this.getClass().getSimpleName(), this.hashCode(), this.hasContent(), this.isLast(), this.isConsumed(), BufferUtil.toDetailString(this.getContent()));
   }
}
