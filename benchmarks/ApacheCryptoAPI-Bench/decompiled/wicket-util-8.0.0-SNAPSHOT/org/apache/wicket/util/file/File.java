package org.apache.wicket.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.time.Time;
import org.apache.wicket.util.watch.IModifiable;

public class File extends java.io.File implements IModifiable {
   private static final long serialVersionUID = 1L;

   public File(File parent, String child) {
      super(parent, child);
   }

   public File(java.io.File parent, String child) {
      super(parent, child);
   }

   public File(java.io.File file) {
      super(file.getAbsolutePath());
   }

   public File(String pathname) {
      super(pathname);
   }

   public File(String parent, String child) {
      super(parent, child);
   }

   public File(URI uri) {
      super(uri);
   }

   public File file(String name) {
      return new File(this, name);
   }

   public String getExtension() {
      int lastDot = this.getName().lastIndexOf(46);
      return lastDot >= 0 ? this.getName().substring(lastDot + 1) : null;
   }

   public Folder getParentFolder() {
      return new Folder(this.getParent());
   }

   public InputStream inputStream() throws FileNotFoundException {
      return new BufferedInputStream(new FileInputStream(this));
   }

   public Time lastModifiedTime() {
      long time = this.lastModified();
      return time == 0L ? null : Time.millis(time);
   }

   public OutputStream outputStream() throws FileNotFoundException {
      Folder parent = this.getParentFolder();
      if (!parent.exists() && !parent.mkdirs()) {
         throw new FileNotFoundException("Couldn't create path " + parent);
      } else {
         return new BufferedOutputStream(new FileOutputStream(this));
      }
   }

   public String readString() throws IOException {
      InputStream in = new FileInputStream(this);

      String var2;
      try {
         var2 = Streams.readString(in);
      } finally {
         in.close();
      }

      return var2;
   }

   public Object readObject() throws IOException, ClassNotFoundException {
      return (new ObjectInputStream(this.inputStream())).readObject();
   }

   public void writeObject(Serializable object) throws IOException {
      (new ObjectOutputStream(this.outputStream())).writeObject(object);
   }

   public boolean remove() {
      return Files.remove(this);
   }

   public void sync() throws IOException {
      FileInputStream in = new FileInputStream(this);

      try {
         in.getFD().sync();
      } finally {
         in.close();
      }

   }

   public String toQuotedString() {
      return "\"" + this.toString() + "\"";
   }

   public int write(File file) throws IOException {
      InputStream in = new BufferedInputStream(new FileInputStream(file));

      int var3;
      try {
         var3 = this.write(in);
      } finally {
         in.close();
      }

      return var3;
   }

   public int write(InputStream input) throws IOException {
      return Files.writeTo(this, input);
   }

   public void write(String string) throws IOException {
      FileWriter out = new FileWriter(this);

      try {
         out.write(string);
      } finally {
         out.close();
      }

   }
}
