package org.apache.tika.io;

import java.io.IOException;

public class TaggedIOException extends IOExceptionWithCause {
   private final Object tag;

   public TaggedIOException(IOException original, Object tag) {
      super(original.getMessage(), original);
      this.tag = tag;
   }

   public Object getTag() {
      return this.tag;
   }

   public IOException getCause() {
      return (IOException)super.getCause();
   }
}
