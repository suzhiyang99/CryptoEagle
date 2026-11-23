package org.apache.directory.server.kerberos.shared.crypto.encryption;

import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;

class NullEncryption extends EncryptionEngine {
   public EncryptionType getEncryptionType() {
      return EncryptionType.NULL;
   }

   public int getChecksumLength() {
      return 0;
   }

   public int getConfounderLength() {
      return 0;
   }

   public byte[] getDecryptedData(EncryptionKey key, EncryptedData data, KeyUsage usage) throws KerberosException {
      return data.getCipher();
   }

   public EncryptedData getEncryptedData(EncryptionKey key, byte[] plainText, KeyUsage usage) {
      return new EncryptedData(this.getEncryptionType(), key.getKeyVersion(), plainText);
   }

   public byte[] encrypt(byte[] plainText, byte[] keyBytes) {
      return plainText;
   }

   public byte[] decrypt(byte[] cipherText, byte[] keyBytes) {
      return cipherText;
   }

   public byte[] calculateIntegrity(byte[] plainText, byte[] key, KeyUsage usage) {
      return null;
   }
}
