package org.apache.directory.server.kerberos.shared.keytab;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class KeytabEncoder {
   private short getKeytabVersion(byte[] version) {
      if (version != null && version.length == 2 && version[0] == 5) {
         switch (version[1]) {
            case 1:
               return 1281;
            case 2:
               return 1282;
            default:
               return -1;
         }
      } else {
         return -1;
      }
   }

   public ByteBuffer write(byte[] keytabVersion, List<KeytabEntry> entries) {
      List<ByteBuffer> keytabEntryBuffers = new ArrayList();
      short version = this.getKeytabVersion(keytabVersion);
      int buffersSize = this.encodeKeytabEntries(keytabEntryBuffers, version, entries);
      ByteBuffer buffer = ByteBuffer.allocate(keytabVersion.length + buffersSize);
      buffer.put(keytabVersion);

      for(ByteBuffer keytabEntryBuffer : keytabEntryBuffers) {
         buffer.put(keytabEntryBuffer);
      }

      buffer.flip();
      return buffer;
   }

   private int encodeKeytabEntries(List<ByteBuffer> buffers, short version, List<KeytabEntry> entries) {
      int size = 0;

      for(KeytabEntry keytabEntry : entries) {
         ByteBuffer entryBuffer = this.encodeKeytabEntry(version, keytabEntry);
         buffers.add(entryBuffer);
         size += entryBuffer.limit();
      }

      return size;
   }

   private ByteBuffer encodeKeytabEntry(short version, KeytabEntry entry) {
      ByteBuffer principalNameBuffer = this.encodePrincipalName(version, entry.getPrincipalName());
      ByteBuffer keyBlockBuffer = this.encodeKeyBlock(entry.getKey());
      int bufferSize = 4 + principalNameBuffer.limit() + 4 + 1 + keyBlockBuffer.limit();
      if (version == 1282) {
         bufferSize += 4;
      }

      ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
      buffer.putInt(bufferSize - 4);
      buffer.put(principalNameBuffer);
      if (version == 1282) {
         buffer.putInt(entry.getPrincipalType());
      }

      buffer.putInt((int)(entry.getTimeStamp().getTime() / 1000L));
      buffer.put(entry.getKeyVersion());
      buffer.put(keyBlockBuffer);
      buffer.flip();
      return buffer;
   }

   private ByteBuffer encodePrincipalName(short version, String principalName) {
      String[] split = principalName.split("@");
      String nameComponentPart = split[0];
      String realm = split[1];
      String[] nameComponents = nameComponentPart.split("/");
      List<byte[]> strings = new ArrayList();
      int size = 2;
      size += this.encodeCountedString(strings, realm);

      for(String nameComponent : nameComponents) {
         size += this.encodeCountedString(strings, nameComponent);
      }

      ByteBuffer buffer = ByteBuffer.allocate(size);
      if (version == 1281) {
         buffer.putShort((short)(nameComponents.length + 1));
      } else {
         buffer.putShort((short)nameComponents.length);
      }

      for(byte[] string : strings) {
         buffer.putShort((short)string.length);
         buffer.put(string);
      }

      buffer.flip();
      return buffer;
   }

   private ByteBuffer encodeKeyBlock(EncryptionKey key) {
      byte[] keyBytes = key.getKeyValue();
      int size = 4 + keyBytes.length;
      ByteBuffer buffer = ByteBuffer.allocate(size);
      buffer.putShort((short)key.getKeyType().getValue());
      buffer.putShort((short)keyBytes.length);
      buffer.put(keyBytes);
      buffer.flip();
      return buffer;
   }

   private short encodeCountedString(List<byte[]> nameComponentBytes, String string) {
      try {
         byte[] data = string.getBytes("US-ASCII");
         nameComponentBytes.add(data);
         return (short)(data.length + 2);
      } catch (UnsupportedEncodingException uee) {
         throw new RuntimeException(uee.getMessage(), uee);
      }
   }
}
