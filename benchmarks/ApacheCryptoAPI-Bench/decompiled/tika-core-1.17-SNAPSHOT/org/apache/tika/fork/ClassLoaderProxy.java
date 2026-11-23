package org.apache.tika.fork;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ClassLoaderProxy extends ClassLoader implements ForkProxy {
   private static final long serialVersionUID = -7303109260448540420L;
   private final Set<String> notFound = new HashSet();
   private final int resource;
   private transient DataInputStream input;
   private transient DataOutputStream output;

   public ClassLoaderProxy(int resource) {
      this.resource = resource;
   }

   public void init(DataInputStream input, DataOutputStream output) {
      this.input = input;
      this.output = output;
   }

   protected synchronized URL findResource(String name) {
      if (this.notFound.contains(name)) {
         return null;
      } else {
         try {
            this.output.write(3);
            this.output.write(this.resource);
            this.output.write(1);
            this.output.writeUTF(name);
            this.output.flush();
            if (this.input.readBoolean()) {
               return MemoryURLStreamHandler.createURL(this.readStream());
            } else {
               this.notFound.add(name);
               return null;
            }
         } catch (IOException var3) {
            return null;
         }
      }
   }

   protected synchronized Enumeration<URL> findResources(String name) throws IOException {
      this.output.write(3);
      this.output.write(this.resource);
      this.output.write(2);
      this.output.writeUTF(name);
      this.output.flush();
      List<URL> resources = new ArrayList();

      while(this.input.readBoolean()) {
         resources.add(MemoryURLStreamHandler.createURL(this.readStream()));
      }

      return Collections.enumeration(resources);
   }

   protected synchronized Class<?> findClass(String name) throws ClassNotFoundException {
      try {
         this.output.write(3);
         this.output.write(this.resource);
         this.output.write(1);
         this.output.writeUTF(name.replace('.', '/') + ".class");
         this.output.flush();
         if (this.input.readBoolean()) {
            byte[] data = this.readStream();
            return this.defineClass(name, data, 0, data.length);
         } else {
            throw new ClassNotFoundException("Unable to find class " + name);
         }
      } catch (IOException e) {
         throw new ClassNotFoundException("Unable to load class " + name, e);
      }
   }

   private byte[] readStream() throws IOException {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      byte[] buffer = new byte['\uffff'];

      int n;
      while((n = this.input.readUnsignedShort()) > 0) {
         this.input.readFully(buffer, 0, n);
         stream.write(buffer, 0, n);
      }

      return stream.toByteArray();
   }
}
