package org.apache.directory.server.kerberos.shared.crypto.checksum;

import java.util.zip.CRC32;
import org.apache.directory.server.kerberos.shared.crypto.encryption.KeyUsage;
import org.apache.directory.shared.kerberos.crypto.checksum.ChecksumType;

class Crc32Checksum implements ChecksumEngine {
   public ChecksumType checksumType() {
      return ChecksumType.CRC32;
   }

   public byte[] calculateChecksum(byte[] data, byte[] key, KeyUsage usage) {
      CRC32 crc32 = new CRC32();
      crc32.update(data);
      return this.int2octet((int)crc32.getValue());
   }

   private byte[] int2octet(int value) {
      byte[] bytes = new byte[4];
      int i = 0;

      for(int shift = 24; i < 4; shift -= 8) {
         bytes[i] = (byte)(255 & value >> shift);
         ++i;
      }

      return bytes;
   }
}
