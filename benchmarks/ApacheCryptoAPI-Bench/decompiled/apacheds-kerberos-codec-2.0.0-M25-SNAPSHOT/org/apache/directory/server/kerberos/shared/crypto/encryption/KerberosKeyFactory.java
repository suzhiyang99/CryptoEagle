package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.directory.shared.kerberos.KerberosUtils;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class KerberosKeyFactory {
   public static final Map<EncryptionType, String> DEFAULT_CIPHERS;

   public static Map<EncryptionType, EncryptionKey> getKerberosKeys(String principalName, String passPhrase) {
      return getKerberosKeys(principalName, passPhrase, DEFAULT_CIPHERS.keySet());
   }

   public static Map<EncryptionType, EncryptionKey> getKerberosKeys(String principalName, String passPhrase, Set<EncryptionType> ciphers) {
      Map<EncryptionType, EncryptionKey> kerberosKeys = new HashMap();

      for(EncryptionType encryptionType : ciphers) {
         try {
            kerberosKeys.put(encryptionType, string2Key(principalName, passPhrase, encryptionType));
         } catch (IllegalArgumentException var7) {
         }
      }

      return kerberosKeys;
   }

   public static EncryptionKey string2Key(String principalName, String passPhrase, EncryptionType encryptionType) {
      KerberosPrincipal principal = new KerberosPrincipal(principalName);
      KerberosKey kerberosKey = new KerberosKey(principal, passPhrase.toCharArray(), KerberosUtils.getAlgoNameFromEncType(encryptionType));
      EncryptionKey encryptionKey = new EncryptionKey(encryptionType, kerberosKey.getEncoded(), kerberosKey.getVersionNumber());
      return encryptionKey;
   }

   static {
      Map<EncryptionType, String> map = new HashMap();
      map.put(EncryptionType.DES_CBC_MD5, "DES");
      map.put(EncryptionType.DES3_CBC_SHA1_KD, "DESede");
      map.put(EncryptionType.RC4_HMAC, "ArcFourHmac");
      map.put(EncryptionType.AES128_CTS_HMAC_SHA1_96, "AES128");
      map.put(EncryptionType.AES256_CTS_HMAC_SHA1_96, "AES256");
      DEFAULT_CIPHERS = Collections.unmodifiableMap(map);
   }
}
