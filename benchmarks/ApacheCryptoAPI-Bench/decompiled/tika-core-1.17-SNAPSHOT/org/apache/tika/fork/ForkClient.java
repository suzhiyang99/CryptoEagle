package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.xml.sax.ContentHandler;

class ForkClient {
   private final List<ForkResource> resources = new ArrayList();
   private final ClassLoader loader;
   private final File jar;
   private final Process process;
   private final DataOutputStream output;
   private final DataInputStream input;
   private final InputStream error;

   public ForkClient(ClassLoader loader, Object object, List<String> java, long serverPulseMillis) throws IOException, TikaException {
      boolean ok = false;

      try {
         this.loader = loader;
         this.jar = createBootstrapJar();
         ProcessBuilder builder = new ProcessBuilder(new String[0]);
         List<String> command = new ArrayList();
         command.addAll(java);
         command.add("-jar");
         command.add(this.jar.getPath());
         command.add(Long.toString(serverPulseMillis));
         builder.command(command);
         this.process = builder.start();
         this.output = new DataOutputStream(this.process.getOutputStream());
         this.input = new DataInputStream(this.process.getInputStream());
         this.error = this.process.getErrorStream();
         this.waitForStartBeacon();
         this.sendObject(loader, this.resources);
         this.sendObject(object, this.resources);
         ok = true;
      } finally {
         if (!ok) {
            this.close();
         }

      }

   }

   private void waitForStartBeacon() throws IOException {
      int type;
      do {
         this.consumeErrorStream();
         type = this.input.read();
      } while((byte)type != 4);

      this.consumeErrorStream();
   }

   public synchronized boolean ping() {
      try {
         this.output.writeByte(2);
         this.output.flush();
         this.consumeErrorStream();
         int type = this.input.read();
         if (type == 2) {
            this.consumeErrorStream();
            return true;
         } else {
            return false;
         }
      } catch (IOException var2) {
         return false;
      }
   }

   public synchronized Throwable call(String method, Object... args) throws IOException, TikaException {
      List<ForkResource> r = new ArrayList(this.resources);
      this.output.writeByte(1);
      this.output.writeUTF(method);

      for(int i = 0; i < args.length; ++i) {
         this.sendObject(args[i], r);
      }

      return this.waitForResponse(r);
   }

   private void sendObject(Object object, List<ForkResource> resources) throws IOException, TikaException {
      int n = resources.size();
      if (object instanceof InputStream) {
         resources.add(new InputStreamResource((InputStream)object));
         object = new InputStreamProxy(n);
      } else if (object instanceof ContentHandler) {
         resources.add(new ContentHandlerResource((ContentHandler)object));
         object = new ContentHandlerProxy(n);
      } else if (object instanceof ClassLoader) {
         resources.add(new ClassLoaderResource((ClassLoader)object));
         object = new ClassLoaderProxy(n);
      }

      try {
         ForkObjectInputStream.sendObject(object, this.output);
      } catch (NotSerializableException nse) {
         throw new TikaException("Unable to serialize " + object.getClass().getSimpleName() + " to pass to the Forked Parser", nse);
      }

      this.waitForResponse(resources);
   }

   public synchronized void close() {
      try {
         if (this.output != null) {
            this.output.close();
         }

         if (this.input != null) {
            this.input.close();
         }

         if (this.error != null) {
            this.error.close();
         }
      } catch (IOException var3) {
      }

      if (this.process != null) {
         this.process.destroy();

         try {
            this.process.waitFor();
         } catch (InterruptedException var2) {
         }
      }

      if (this.jar != null) {
         this.jar.delete();
      }

   }

   private Throwable waitForResponse(List<ForkResource> resources) throws IOException {
      this.output.flush();

      while(true) {
         this.consumeErrorStream();
         int type = this.input.read();
         if (type == -1) {
            this.consumeErrorStream();
            throw new IOException("Lost connection to a forked server process");
         }

         if (type != 3) {
            if ((byte)type == -1) {
               try {
                  return (Throwable)ForkObjectInputStream.readObject(this.input, this.loader);
               } catch (ClassNotFoundException e) {
                  throw new IOException("Unable to deserialize an exception", e);
               }
            }

            return null;
         }

         ForkResource resource = (ForkResource)resources.get(this.input.readUnsignedByte());
         resource.process(this.input, this.output);
      }
   }

   private void consumeErrorStream() throws IOException {
      int n;
      while((n = this.error.available()) > 0) {
         byte[] b = new byte[n];
         n = this.error.read(b);
         if (n > 0) {
            System.err.write(b, 0, n);
         }
      }

   }

   private static File createBootstrapJar() throws IOException {
      File file = File.createTempFile("apache-tika-fork-", ".jar");
      boolean ok = false;

      try {
         fillBootstrapJar(file);
         ok = true;
      } finally {
         if (!ok) {
            file.delete();
         }

      }

      return file;
   }

   private static void fillBootstrapJar(File file) throws IOException {
      JarOutputStream jar = new JarOutputStream(new FileOutputStream(file));
      Throwable var2 = null;

      try {
         String manifest = "Main-Class: " + ForkServer.class.getName() + "\n";
         jar.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
         jar.write(manifest.getBytes(StandardCharsets.UTF_8));
         Class<?>[] bootstrap = new Class[]{ForkServer.class, ForkObjectInputStream.class, ForkProxy.class, ClassLoaderProxy.class, MemoryURLConnection.class, MemoryURLStreamHandler.class, MemoryURLStreamHandlerFactory.class, MemoryURLStreamRecord.class};
         ClassLoader loader = ForkServer.class.getClassLoader();

         for(Class<?> klass : bootstrap) {
            String path = klass.getName().replace('.', '/') + ".class";
            InputStream input = loader.getResourceAsStream(path);
            Throwable var12 = null;

            try {
               jar.putNextEntry(new JarEntry(path));
               IOUtils.copy((InputStream)input, (OutputStream)jar);
            } catch (Throwable var35) {
               var12 = var35;
               throw var35;
            } finally {
               if (input != null) {
                  if (var12 != null) {
                     try {
                        input.close();
                     } catch (Throwable var34) {
                        var12.addSuppressed(var34);
                     }
                  } else {
                     input.close();
                  }
               }

            }
         }
      } catch (Throwable var37) {
         var2 = var37;
         throw var37;
      } finally {
         if (jar != null) {
            if (var2 != null) {
               try {
                  jar.close();
               } catch (Throwable var33) {
                  var2.addSuppressed(var33);
               }
            } else {
               jar.close();
            }
         }

      }

   }
}
