package org.apache.directory.server.kerberos.shared.crypto.checksum;

import java.security.GeneralSecurityException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.directory.server.kerberos.shared.crypto.encryption.KeyUsage;
import org.apache.directory.shared.kerberos.crypto.checksum.ChecksumType;

class HmacMd5Checksum implements ChecksumEngine {
   public ChecksumType checksumType() {
      return ChecksumType.HMAC_MD5;
   }

   public byte[] calculateChecksum(byte[] data, byte[] key, KeyUsage usage) {
      try {
         SecretKey sk = new SecretKeySpec(key, "ARCFOUR");
         Mac mac = Mac.getInstance("HmacMD5");
         mac.init(sk);
         return mac.doFinal(data);
      } catch (GeneralSecurityException nsae) {
         nsae.printStackTrace();
         return null;
      }
   }
}
