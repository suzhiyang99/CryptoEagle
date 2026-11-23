package org.apache.tika.io;

import java.io.InputStream;

public class ClosedInputStream extends InputStream {
   public int read() {
      return -1;
   }
}
