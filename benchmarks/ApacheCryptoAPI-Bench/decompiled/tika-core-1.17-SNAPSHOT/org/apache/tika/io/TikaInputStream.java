package org.apache.tika.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.SQLException;
import org.apache.tika.metadata.Metadata;

public class TikaInputStream extends TaggedInputStream {
   private static final int BLOB_SIZE_THRESHOLD = 1048576;
   private Path path;
   private final TemporaryResources tmp;
   private long length;
   private long position = 0L;
   private long mark = -1L;
   private Object openContainer;

   public static boolean isTikaInputStream(InputStream stream) {
      return stream instanceof TikaInputStream;
   }

   public static TikaInputStream get(InputStream stream, TemporaryResources tmp) {
      if (stream == null) {
         throw new NullPointerException("The Stream must not be null");
      } else if (stream instanceof TikaInputStream) {
         return (TikaInputStream)stream;
      } else {
         if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
         }

         return new TikaInputStream(stream, tmp, -1L);
      }
   }

   public static TikaInputStream get(InputStream stream) {
      return get(stream, new TemporaryResources());
   }

   public static TikaInputStream cast(InputStream stream) {
      return stream instanceof TikaInputStream ? (TikaInputStream)stream : null;
   }

   public static TikaInputStream get(byte[] data) {
      return get(data, new Metadata());
   }

   public static TikaInputStream get(byte[] data, Metadata metadata) {
      metadata.set("Content-Length", Integer.toString(data.length));
      return new TikaInputStream(new ByteArrayInputStream(data), new TemporaryResources(), (long)data.length);
   }

   public static TikaInputStream get(Path path) throws IOException {
      return get(path, new Metadata());
   }

   public static TikaInputStream get(Path path, Metadata metadata) throws IOException {
      metadata.set("resourceName", path.getFileName().toString());
      metadata.set("Content-Length", Long.toString(Files.size(path)));
      return new TikaInputStream(path);
   }

   /** @deprecated */
   @Deprecated
   public static TikaInputStream get(File file) throws FileNotFoundException {
      return get(file, new Metadata());
   }

   /** @deprecated */
   @Deprecated
   public static TikaInputStream get(File file, Metadata metadata) throws FileNotFoundException {
      metadata.set("resourceName", file.getName());
      metadata.set("Content-Length", Long.toString(file.length()));
      return new TikaInputStream(file);
   }

   public static TikaInputStream get(Blob blob) throws SQLException {
      return get(blob, new Metadata());
   }

   public static TikaInputStream get(Blob blob, Metadata metadata) throws SQLException {
      long length = -1L;

      try {
         length = blob.length();
         metadata.set("Content-Length", Long.toString(length));
      } catch (SQLException var5) {
      }

      return 0L <= length && length <= 1048576L ? get(blob.getBytes(1L, (int)length), metadata) : new TikaInputStream(new BufferedInputStream(blob.getBinaryStream()), new TemporaryResources(), length);
   }

   public static TikaInputStream get(URI uri) throws IOException {
      return get(uri, new Metadata());
   }

   public static TikaInputStream get(URI uri, Metadata metadata) throws IOException {
      if ("file".equalsIgnoreCase(uri.getScheme())) {
         Path path = Paths.get(uri);
         if (Files.isRegularFile(path, new LinkOption[0])) {
            return get(path, metadata);
         }
      }

      return get(uri.toURL(), metadata);
   }

   public static TikaInputStream get(URL url) throws IOException {
      return get(url, new Metadata());
   }

   public static TikaInputStream get(URL url, Metadata metadata) throws IOException {
      if ("file".equalsIgnoreCase(url.getProtocol())) {
         try {
            Path path = Paths.get(url.toURI());
            if (Files.isRegularFile(path, new LinkOption[0])) {
               return get(path, metadata);
            }
         } catch (URISyntaxException var8) {
         }
      }

      URLConnection connection = url.openConnection();
      String path = url.getPath();
      int slash = path.lastIndexOf(47);
      if (slash + 1 < path.length()) {
         metadata.set("resourceName", path.substring(slash + 1));
      }

      String type = connection.getContentType();
      if (type != null) {
         metadata.set("Content-Type", type);
      }

      String encoding = connection.getContentEncoding();
      if (encoding != null) {
         metadata.set("Content-Encoding", encoding);
      }

      int length = connection.getContentLength();
      if (length >= 0) {
         metadata.set("Content-Length", Integer.toString(length));
      }

      return new TikaInputStream(new BufferedInputStream(connection.getInputStream()), new TemporaryResources(), (long)length);
   }

   private TikaInputStream(Path path) throws IOException {
      super(new BufferedInputStream(Files.newInputStream(path)));
      this.path = path;
      this.tmp = new TemporaryResources();
      this.length = Files.size(path);
   }

   /** @deprecated */
   @Deprecated
   private TikaInputStream(File file) throws FileNotFoundException {
      super(new BufferedInputStream(new FileInputStream(file)));
      this.path = file.toPath();
      this.tmp = new TemporaryResources();
      this.length = file.length();
   }

   private TikaInputStream(InputStream stream, TemporaryResources tmp, long length) {
      super(stream);
      this.path = null;
      this.tmp = tmp;
      this.length = length;
   }

   public int peek(byte[] buffer) throws IOException {
      int n = 0;
      this.mark(buffer.length);
      int m = this.read(buffer);

      while(m != -1) {
         n += m;
         if (n < buffer.length) {
            m = this.read(buffer, n, buffer.length - n);
         } else {
            m = -1;
         }
      }

      this.reset();
      return n;
   }

   public Object getOpenContainer() {
      return this.openContainer;
   }

   public void setOpenContainer(Object container) {
      this.openContainer = container;
      if (container instanceof Closeable) {
         this.tmp.addResource((Closeable)container);
      }

   }

   public boolean hasFile() {
      return this.path != null;
   }

   public Path getPath() throws IOException {
      if (this.path == null) {
         if (this.position > 0L) {
            throw new IOException("Stream is already being read");
         }

         this.path = this.tmp.createTempFile();
         Files.copy(this.in, this.path, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
         InputStream newStream = Files.newInputStream(this.path);
         this.tmp.addResource(newStream);
         final InputStream oldStream = this.in;
         this.in = new BufferedInputStream(newStream) {
            public void close() throws IOException {
               oldStream.close();
            }
         };
         this.length = Files.size(this.path);
      }

      return this.path;
   }

   public File getFile() throws IOException {
      return this.getPath().toFile();
   }

   public FileChannel getFileChannel() throws IOException {
      FileChannel channel = FileChannel.open(this.getPath());
      this.tmp.addResource(channel);
      return channel;
   }

   public boolean hasLength() {
      return this.length != -1L;
   }

   public long getLength() throws IOException {
      if (this.length == -1L) {
         this.getPath();
      }

      return this.length;
   }

   public long getPosition() {
      return this.position;
   }

   public long skip(long ln) throws IOException {
      long n = super.skip(ln);
      this.position += n;
      return n;
   }

   public void mark(int readlimit) {
      super.mark(readlimit);
      this.mark = this.position;
   }

   public boolean markSupported() {
      return true;
   }

   public void reset() throws IOException {
      super.reset();
      this.position = this.mark;
      this.mark = -1L;
   }

   public void close() throws IOException {
      this.path = null;
      this.mark = -1L;
      this.tmp.addResource(this.in);
      this.tmp.close();
   }

   protected void afterRead(int n) {
      if (n != -1) {
         this.position += (long)n;
      }

   }

   public String toString() {
      String str = "TikaInputStream of ";
      if (this.hasFile()) {
         str = str + this.path.toString();
      } else {
         str = str + this.in.toString();
      }

      if (this.openContainer != null) {
         str = str + " (in " + this.openContainer + ")";
      }

      return str;
   }
}
