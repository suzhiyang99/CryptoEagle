package org.apache.directory.server.kerberos.shared.crypto.encryption;

import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.crypto.checksum.ChecksumType;

public class Aes128CtsSha1Encryption extends AesCtsSha1Encryption {
   public EncryptionType getEncryptionType() {
      return EncryptionType.AES128_CTS_HMAC_SHA1_96;
   }

   public ChecksumType checksumType() {
      return ChecksumType.HMAC_SHA1_96_AES128;
   }

   public int getKeyLength() {
      return 128;
   }
}
