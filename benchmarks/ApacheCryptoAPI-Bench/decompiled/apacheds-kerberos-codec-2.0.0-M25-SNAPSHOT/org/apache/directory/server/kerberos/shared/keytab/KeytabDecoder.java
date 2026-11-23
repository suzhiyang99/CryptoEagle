package org.apache.directory.server.kerberos.shared.keytab;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

class KeytabDecoder {
   byte[] getKeytabVersion(ByteBuffer buffer) {
      byte[] version = new byte[2];
      buffer.get(version);
      return version;
   }

   List<KeytabEntry> getKeytabEntries(ByteBuffer buffer) throws IOException {
      List<KeytabEntry> entries = new ArrayList();

      while(buffer.remaining() > 0) {
         int size = buffer.getInt();
         if (size < 0 || size > buffer.capacity()) {
            throw new IOException("Invalid size for the keytab entry");
         }

         byte[] entry = new byte[size];
         buffer.get(entry);
         entries.add(this.getKeytabEntry(ByteBuffer.wrap(entry)));
      }

      return entries;
   }

   private KeytabEntry getKeytabEntry(ByteBuffer buffer) throws IOException {
      String principalName = this.getPrincipalName(buffer);
      int principalType = buffer.getInt();
      long time = (long)buffer.getInt();
      KerberosTime timeStamp = new KerberosTime(time * 1000L);
      byte keyVersion = buffer.get();
      EncryptionKey key = this.getKeyBlock(buffer);
      return new KeytabEntry(principalName, principalType, timeStamp, keyVersion, key);
   }

   private String getPrincipalName(ByteBuffer buffer) throws IOException {
      int count = buffer.getShort();
      String realm = this.getCountedString(buffer);
      StringBuffer principalNameBuffer = new StringBuffer();

      for(int ii = 0; ii < count; ++ii) {
         String nameComponent = this.getCountedString(buffer);
         principalNameBuffer.append(nameComponent);
         if (ii < count - 1) {
            principalNameBuffer.append("/");
         }
      }

      principalNameBuffer.append("@").append(realm);
      return principalNameBuffer.toString();
   }

   private EncryptionKey getKeyBlock(ByteBuffer buffer) throws IOException {
      int type = buffer.getShort();
      byte[] keyblock = this.getCountedBytes(buffer);
      EncryptionType encryptionType = EncryptionType.getTypeByValue(type);
      EncryptionKey key = new EncryptionKey(encryptionType, keyblock);
      return key;
   }

   private String getCountedString(ByteBuffer buffer) throws IOException {
      int length = buffer.getShort();
      if (length >= 0 && length <= buffer.capacity()) {
         byte[] data = new byte[length];
         buffer.get(data);

         try {
            return new String(data, "ASCII");
         } catch (UnsupportedEncodingException var5) {
            return "";
         }
      } else {
         throw new IOException("Invalid size for the keytab entry");
      }
   }

   private byte[] getCountedBytes(ByteBuffer buffer) throws IOException {
      int length = buffer.getShort();
      if (length >= 0 && length <= buffer.capacity()) {
         byte[] data = new byte[length];
         buffer.get(data);
         return data;
      } else {
         throw new IOException("Invalid size for the keytab entry");
      }
   }
}
