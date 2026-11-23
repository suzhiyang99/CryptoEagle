package org.apache.tika.fork;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;
import org.apache.tika.exception.TikaException;

class ForkServer implements Runnable, Checksum {
   public static final byte ERROR = -1;
   public static final byte DONE = 0;
   public static final byte CALL = 1;
   public static final byte PING = 2;
   public static final byte RESOURCE = 3;
   public static final byte READY = 4;
   private long serverPulseMillis = 5000L;
   private final DataInputStream input;
   private final DataOutputStream output;
   private volatile boolean active = true;

   public static void main(String[] args) throws Exception {
      long serverPulseMillis = -1L;
      if (args.length > 0) {
         serverPulseMillis = Long.parseLong(args[0]);
      }

      URL.setURLStreamHandlerFactory(new MemoryURLStreamHandlerFactory());
      ForkServer server = new ForkServer(System.in, System.out, serverPulseMillis);
      System.setIn(new ByteArrayInputStream(new byte[0]));
      System.setOut(System.err);
      Thread watchdog = new Thread(server, "Tika Watchdog");
      watchdog.setDaemon(true);
      watchdog.start();
      server.processRequests();
   }

   public ForkServer(InputStream input, OutputStream output, long serverPulseMillis) throws IOException {
      this.input = new DataInputStream(new CheckedInputStream(input, this));
      this.output = new DataOutputStream(new CheckedOutputStream(output, this));
      this.serverPulseMillis = serverPulseMillis;
   }

   public void run() {
      try {
         while(true) {
            if (this.active) {
               this.active = false;
               Thread.sleep(this.serverPulseMillis);
            } else {
               System.exit(0);
               break;
            }
         }
      } catch (InterruptedException var2) {
      }

   }

   public void processRequests() {
      try {
         this.output.writeByte(4);
         this.output.flush();
         ClassLoader loader = (ClassLoader)this.readObject(ForkServer.class.getClassLoader());
         Thread.currentThread().setContextClassLoader(loader);
         Object object = this.readObject(loader);

         while(true) {
            int request = this.input.read();
            if (request == -1) {
               break;
            }

            if (request == 2) {
               this.output.writeByte(2);
            } else {
               if (request != 1) {
                  throw new IllegalStateException("Unexpected request");
               }

               this.call(loader, object);
            }

            this.output.flush();
         }
      } catch (Throwable t) {
         t.printStackTrace();
      }

      System.err.flush();
   }

   private void call(ClassLoader loader, Object object) throws Exception {
      Method method = this.getMethod(object, this.input.readUTF());
      Object[] args = new Object[method.getParameterTypes().length];

      for(int i = 0; i < args.length; ++i) {
         args[i] = this.readObject(loader);
      }

      try {
         method.invoke(object, args);
         this.output.write(0);
      } catch (InvocationTargetException e) {
         this.output.write(-1);
         Throwable toSend = e.getCause();

         try {
            ForkObjectInputStream.sendObject(toSend, this.output);
         } catch (NotSerializableException var9) {
            TikaException te = new TikaException(toSend.getMessage());
            te.setStackTrace(toSend.getStackTrace());
            ForkObjectInputStream.sendObject(te, this.output);
         }
      }

   }

   private Method getMethod(Object object, String name) {
      for(Class<?> klass = object.getClass(); klass != null; klass = klass.getSuperclass()) {
         for(Class<?> iface : klass.getInterfaces()) {
            for(Method method : iface.getMethods()) {
               if (name.equals(method.getName())) {
                  return method;
               }
            }
         }
      }

      return null;
   }

   private Object readObject(ClassLoader loader) throws IOException, ClassNotFoundException {
      Object object = ForkObjectInputStream.readObject(this.input, loader);
      if (object instanceof ForkProxy) {
         ((ForkProxy)object).init(this.input, this.output);
      }

      this.output.writeByte(0);
      this.output.flush();
      return object;
   }

   public void update(int b) {
      this.active = true;
   }

   public void update(byte[] b, int off, int len) {
      this.active = true;
   }

   public long getValue() {
      return 0L;
   }

   public void reset() {
   }
}
