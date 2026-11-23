package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

class InputStreamProxy extends InputStream implements ForkProxy {
   private static final long serialVersionUID = 4350939227765568438L;
   private final int resource;
   private transient DataInputStream input;
   private transient DataOutputStream output;

   public InputStreamProxy(int resource) {
      this.resource = resource;
   }

   public void init(DataInputStream input, DataOutputStream output) {
      this.input = input;
      this.output = output;
   }

   public int read() throws IOException {
      this.output.writeByte(3);
      this.output.writeByte(this.resource);
      this.output.writeInt(1);
      this.output.flush();
      int n = this.input.readInt();
      return n == 1 ? this.input.readUnsignedByte() : n;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      this.output.writeByte(3);
      this.output.writeByte(this.resource);
      this.output.writeInt(len);
      this.output.flush();
      int n = this.input.readInt();
      if (n > 0) {
         this.input.readFully(b, off, n);
      }

      return n;
   }
}
