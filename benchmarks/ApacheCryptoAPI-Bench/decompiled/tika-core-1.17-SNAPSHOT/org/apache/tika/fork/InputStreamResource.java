package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

class InputStreamResource implements ForkResource {
   private final InputStream stream;

   public InputStreamResource(InputStream stream) {
      this.stream = stream;
   }

   public Throwable process(DataInputStream input, DataOutputStream output) throws IOException {
      int n = input.readInt();
      byte[] buffer = new byte[n];

      int m;
      try {
         m = this.stream.read(buffer);
      } catch (IOException e) {
         return e;
      }

      output.writeInt(m);
      if (m > 0) {
         output.write(buffer, 0, m);
      }

      output.flush();
      return null;
   }
}
