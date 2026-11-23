package org.apache.directory.server.kerberos.shared.crypto.checksum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.server.kerberos.shared.crypto.encryption.KeyUsage;
import org.apache.directory.shared.kerberos.crypto.checksum.ChecksumType;

class RsaMd5Checksum implements ChecksumEngine {
   public ChecksumType checksumType() {
      return ChecksumType.RSA_MD5;
   }

   public byte[] calculateChecksum(byte[] data, byte[] key, KeyUsage usage) {
      try {
         MessageDigest digester = MessageDigest.getInstance(LdapSecurityConstants.HASH_METHOD_MD5.getAlgorithm());
         return digester.digest(data);
      } catch (NoSuchAlgorithmException var5) {
         return null;
      }
   }
}
