package org.apache.openejb.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.codehaus.swizzle.stream.ScanBuffer;

public class OutputScanner extends FilterOutputStream {
   private final CountDownLatch found = new CountDownLatch(1);

   public OutputScanner(OutputStream out, String scanString) {
      super((OutputStream)null);
      this.out = new Scan(out, scanString);
   }

   public void await() throws InterruptedException {
      this.found.await();
   }

   public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
      return this.found.await(timeout, unit);
   }

   public class Scan extends FilterOutputStream {
      private final ScanBuffer scan;

      public Scan(OutputStream out, String scanString) {
         super(out);
         this.scan = new ScanBuffer(scanString);
      }

      public void write(int b) throws IOException {
         this.check(b);
         super.write(b);
      }

      public void write(byte[] bytes) throws IOException {
         for(byte b : bytes) {
            this.check(b);
         }

         super.write(bytes);
      }

      public void write(byte[] bytes, int off, int len) throws IOException {
         for(int i = off; i < len; ++i) {
            this.check(bytes[i]);
         }

         super.write(bytes, off, len);
      }

      private void check(int b) {
         this.scan.append(b);
         if (this.scan.match()) {
            OutputScanner.this.out = this.out;
            OutputScanner.this.found.countDown();
         }

      }
   }
}
