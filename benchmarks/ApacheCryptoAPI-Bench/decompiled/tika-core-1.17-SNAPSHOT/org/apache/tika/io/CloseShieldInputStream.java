package org.apache.tika.io;

import java.io.InputStream;

public class CloseShieldInputStream extends ProxyInputStream {
   public CloseShieldInputStream(InputStream in) {
      super(in);
   }

   public void close() {
      this.in = new ClosedInputStream();
   }
}
