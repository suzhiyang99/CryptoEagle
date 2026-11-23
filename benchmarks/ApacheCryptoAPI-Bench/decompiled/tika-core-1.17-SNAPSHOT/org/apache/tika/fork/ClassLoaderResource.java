package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

class ClassLoaderResource implements ForkResource {
   private final ClassLoader loader;

   public ClassLoaderResource(ClassLoader loader) {
      this.loader = loader;
   }

   public Throwable process(DataInputStream input, DataOutputStream output) throws IOException {
      byte type = input.readByte();
      String name = input.readUTF();
      if (type == 1) {
         InputStream stream = this.loader.getResourceAsStream(name);
         if (stream != null) {
            output.writeBoolean(true);
            this.writeAndCloseStream(output, stream);
         } else {
            output.writeBoolean(false);
         }
      } else if (type == 2) {
         Enumeration<URL> resources = this.loader.getResources(name);

         while(resources.hasMoreElements()) {
            output.writeBoolean(true);
            InputStream stream = ((URL)resources.nextElement()).openStream();
            this.writeAndCloseStream(output, stream);
         }

         output.writeBoolean(false);
      }

      output.flush();
      return null;
   }

   private void writeAndCloseStream(DataOutputStream output, InputStream stream) throws IOException {
      try {
         byte[] buffer = new byte['\uffff'];

         int n;
         while((n = stream.read(buffer)) != -1) {
            output.writeShort(n);
            output.write(buffer, 0, n);
         }

         output.writeShort(0);
      } finally {
         stream.close();
      }

   }
}
