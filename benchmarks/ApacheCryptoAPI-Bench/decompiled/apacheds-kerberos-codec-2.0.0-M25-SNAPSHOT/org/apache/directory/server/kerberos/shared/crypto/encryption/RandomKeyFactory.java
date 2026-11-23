package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;

public class RandomKeyFactory {
   private static final Map<EncryptionType, String> DEFAULT_CIPHERS;

   public static Map<EncryptionType, EncryptionKey> getRandomKeys() throws KerberosException {
      return getRandomKeys(DEFAULT_CIPHERS.keySet());
   }

   public static Map<EncryptionType, EncryptionKey> getRandomKeys(Set<EncryptionType> ciphers) throws KerberosException {
      Map<EncryptionType, EncryptionKey> map = new HashMap();

      for(EncryptionType encryptionType : ciphers) {
         map.put(encryptionType, getRandomKey(encryptionType));
      }

      return map;
   }

   public static EncryptionKey getRandomKey(EncryptionType encryptionType) throws KerberosException {
      String algorithm = (String)DEFAULT_CIPHERS.get(encryptionType);
      if (algorithm == null) {
         throw new KerberosException(ErrorType.KDC_ERR_ETYPE_NOSUPP, I18n.err(I18n.ERR_616, new Object[]{encryptionType.getName()}));
      } else {
         try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            if (encryptionType.equals(EncryptionType.AES128_CTS_HMAC_SHA1_96)) {
               keyGenerator.init(128);
            }

            if (encryptionType.equals(EncryptionType.AES256_CTS_HMAC_SHA1_96)) {
               keyGenerator.init(256);
            }

            SecretKey key = keyGenerator.generateKey();
            byte[] keyBytes = key.getEncoded();
            return new EncryptionKey(encryptionType, keyBytes);
         } catch (NoSuchAlgorithmException nsae) {
            throw new KerberosException(ErrorType.KDC_ERR_ETYPE_NOSUPP, nsae);
         }
      }
   }

   static {
      Map<EncryptionType, String> map = new HashMap();
      map.put(EncryptionType.DES_CBC_MD5, "DES");
      map.put(EncryptionType.DES3_CBC_SHA1_KD, "DESede");
      map.put(EncryptionType.RC4_HMAC, "RC4");
      map.put(EncryptionType.AES128_CTS_HMAC_SHA1_96, "AES");
      map.put(EncryptionType.AES256_CTS_HMAC_SHA1_96, "AES");
      DEFAULT_CIPHERS = Collections.unmodifiableMap(map);
   }
}
