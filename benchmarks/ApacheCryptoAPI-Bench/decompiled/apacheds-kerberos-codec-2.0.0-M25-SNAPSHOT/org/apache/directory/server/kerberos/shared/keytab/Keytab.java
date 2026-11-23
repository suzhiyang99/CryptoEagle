package org.apache.directory.server.kerberos.shared.keytab;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.directory.server.i18n.I18n;

public class Keytab {
   public static final byte[] VERSION_0X501_BYTES = new byte[]{5, 1};
   public static final short VERSION_0X501 = 1281;
   public static final byte[] VERSION_0X502_BYTES = new byte[]{5, 2};
   public static final short VERSION_0X502 = 1282;
   private byte[] keytabVersion;
   private List<KeytabEntry> entries;

   public Keytab() {
      this.keytabVersion = VERSION_0X502_BYTES;
      this.entries = new ArrayList();
   }

   public static Keytab read(File file) throws IOException {
      ByteBuffer buffer = ByteBuffer.wrap(getBytesFromFile(file));
      return readKeytab(buffer);
   }

   public static Keytab getInstance() {
      return new Keytab();
   }

   public void write(File file) throws IOException {
      KeytabEncoder writer = new KeytabEncoder();
      ByteBuffer buffer = writer.write(this.keytabVersion, this.entries);
      this.writeFile(buffer, file);
   }

   public void setEntries(List<KeytabEntry> entries) {
      this.entries = entries;
   }

   public void setKeytabVersion(byte[] keytabVersion) {
      this.keytabVersion = keytabVersion;
   }

   public List<KeytabEntry> getEntries() {
      return Collections.unmodifiableList(this.entries);
   }

   public byte[] getKeytabVersion() {
      return this.keytabVersion;
   }

   static Keytab read(byte[] bytes) throws IOException {
      ByteBuffer buffer = ByteBuffer.wrap(bytes);
      return readKeytab(buffer);
   }

   ByteBuffer write() {
      KeytabEncoder writer = new KeytabEncoder();
      return writer.write(this.keytabVersion, this.entries);
   }

   private static Keytab readKeytab(ByteBuffer buffer) throws IOException {
      KeytabDecoder reader = new KeytabDecoder();
      byte[] keytabVersion = reader.getKeytabVersion(buffer);
      List<KeytabEntry> entries = reader.getKeytabEntries(buffer);
      Keytab keytab = new Keytab();
      keytab.setKeytabVersion(keytabVersion);
      keytab.setEntries(entries);
      return keytab;
   }

   protected static byte[] getBytesFromFile(File file) throws IOException {
      InputStream is = Files.newInputStream(file.toPath());
      Throwable var2 = null;

      byte[] var8;
      try {
         long length = file.length();
         if (length > 2147483647L) {
            throw new IOException(I18n.err(I18n.ERR_618, new Object[]{file.getName()}));
         }

         byte[] bytes = new byte[(int)length];
         int offset = 0;

         int numRead;
         for(numRead = 0; offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {
         }

         if (offset < bytes.length) {
            throw new IOException(I18n.err(I18n.ERR_619, new Object[]{file.getName()}));
         }

         var8 = bytes;
      } catch (Throwable var17) {
         var2 = var17;
         throw var17;
      } finally {
         if (is != null) {
            if (var2 != null) {
               try {
                  is.close();
               } catch (Throwable var16) {
                  var2.addSuppressed(var16);
               }
            } else {
               is.close();
            }
         }

      }

      return var8;
   }

   protected void writeFile(ByteBuffer buffer, File file) throws IOException {
      OutputStream out = Files.newOutputStream(file.toPath());

      try {
         WritableByteChannel channel = Channels.newChannel(out);
         Throwable var5 = null;

         try {
            channel.write(buffer);
         } catch (Throwable var21) {
            var5 = var21;
            throw var21;
         } finally {
            if (channel != null) {
               if (var5 != null) {
                  try {
                     channel.close();
                  } catch (Throwable var20) {
                     var5.addSuppressed(var20);
                  }
               } else {
                  channel.close();
               }
            }

         }
      } finally {
         out.close();
      }

   }
}
