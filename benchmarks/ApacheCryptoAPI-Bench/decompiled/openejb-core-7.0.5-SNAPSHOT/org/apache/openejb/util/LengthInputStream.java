package org.apache.openejb.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LengthInputStream extends FilterInputStream {
   private long length;

   public LengthInputStream(InputStream in) throws IOException {
      super(in);
   }

   public int read() throws IOException {
      int i = super.read();
      if (i > 0) {
         ++this.length;
      }

      return i;
   }

   public int read(byte[] b) throws IOException {
      int i = super.read(b);
      if (i > 0) {
         this.length += (long)i;
      }

      return i;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int i = super.read(b, off, len);
      if (i > 0) {
         this.length += (long)i;
      }

      return i;
   }

   public long getLength() {
      return this.length;
   }
}
