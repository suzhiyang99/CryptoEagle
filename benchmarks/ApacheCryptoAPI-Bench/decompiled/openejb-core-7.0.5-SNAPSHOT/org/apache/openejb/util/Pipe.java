package org.apache.openejb.util;

import java.io.InputStream;
import java.io.OutputStream;

public final class Pipe implements Runnable {
   private final InputStream in;
   private final OutputStream out;

   public Pipe(InputStream in, OutputStream out) {
      this.in = in;
      this.out = out;
   }

   public static void pipeOut(Process process) {
      pipe(process.getInputStream(), System.out);
      pipe(process.getErrorStream(), System.err);
   }

   public static void pipe(Process process) {
      pipeOut(process);
      pipe(System.in, process.getOutputStream());
   }

   public static void read(Process process) {
      pipe(process.getInputStream(), System.out);
      pipe(process.getErrorStream(), System.err);
   }

   public static void pipe(InputStream in, OutputStream out) {
      Thread thread = new Thread(new Pipe(in, out));
      thread.setDaemon(true);
      thread.start();
   }

   public void run() {
      try {
         byte[] buf = new byte[1];

         int i;
         while((i = this.in.read(buf)) != -1) {
            this.out.write(buf, 0, i);
         }
      } catch (Exception e) {
         String m = e.getMessage();
         if (null != m && m.toLowerCase().contains("stream closed")) {
            return;
         }

         e.printStackTrace();
      }

   }
}
