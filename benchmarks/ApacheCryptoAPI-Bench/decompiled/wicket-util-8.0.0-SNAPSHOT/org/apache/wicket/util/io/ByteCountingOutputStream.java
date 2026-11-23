package org.apache.wicket.util.io;

import java.io.IOException;
import java.io.OutputStream;

public final class ByteCountingOutputStream extends OutputStream {
   private long size;

   public void write(int b) throws IOException {
      ++this.size;
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.size += (long)len;
   }

   public long size() {
      return this.size;
   }
}
