package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.UUID;

public class TaggedInputStream extends ProxyInputStream {
   private final Serializable tag = UUID.randomUUID();

   public TaggedInputStream(InputStream proxy) {
      super(proxy);
   }

   public static TaggedInputStream get(InputStream proxy) {
      return proxy instanceof TaggedInputStream ? (TaggedInputStream)proxy : new TaggedInputStream(proxy);
   }

   public boolean isCauseOf(IOException exception) {
      if (exception instanceof TaggedIOException) {
         TaggedIOException tagged = (TaggedIOException)exception;
         return this.tag.equals(tagged.getTag());
      } else {
         return false;
      }
   }

   public void throwIfCauseOf(Exception exception) throws IOException {
      if (exception instanceof TaggedIOException) {
         TaggedIOException tagged = (TaggedIOException)exception;
         if (this.tag.equals(tagged.getTag())) {
            throw tagged.getCause();
         }
      }

   }

   protected void handleIOException(IOException e) throws IOException {
      throw new TaggedIOException(e, this.tag);
   }

   public String toString() {
      return "Tika Tagged InputStream wrapping " + this.in;
   }
}
