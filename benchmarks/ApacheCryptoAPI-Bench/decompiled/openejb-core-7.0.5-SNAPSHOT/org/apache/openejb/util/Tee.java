package org.apache.openejb.util;

import java.io.InputStream;
import java.io.OutputStream;

public final class Tee implements Runnable {
   private final InputStream in;
   private final OutputStream[] out;

   public Tee(InputStream in, OutputStream... out) {
      this.in = in;
      this.out = out;
   }

   public static InputStream read(Process process) {
      return process.getInputStream();
   }

   public static void pipe(InputStream in, OutputStream out) {
      run(new Tee(in, new OutputStream[]{out}));
   }

   private static void run(Tee target) {
      Thread thread = new Thread(target);
      thread.setDaemon(true);
      thread.start();
   }

   public void run() {
      try {
         int i = -1;
         byte[] buf = new byte['耀'];

         while((i = this.in.read(buf)) != -1) {
            System.out.print("#");

            for(int o = 0; o < this.out.length; ++o) {
               try {
                  if (this.out[o] != null) {
                     this.out[o].write(buf, 0, i);
                  }
               } catch (Exception e) {
                  (new Exception(this.out[o].toString(), e)).printStackTrace();
                  this.out[o] = null;
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}
