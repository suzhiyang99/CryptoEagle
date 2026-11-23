package org.apache.directory.server.kerberos.shared.crypto.encryption;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.ldap.model.constants.Loggers;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CipherTextHandler {
   private static final Logger LOG_KRB;
   private static final Map<EncryptionType, Class<? extends EncryptionEngine>> DEFAULT_CIPHERS;

   public EncryptedData seal(EncryptionKey key, Asn1Object message, KeyUsage usage) throws KerberosException {
      try {
         int bufferSize = message.computeLength();
         ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
         byte[] encoded = message.encode(buffer).array();
         return this.encrypt(key, encoded, usage);
      } catch (EncoderException ioe) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, ioe);
      } catch (ClassCastException cce) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, cce);
      }
   }

   public EncryptedData encrypt(EncryptionKey key, byte[] plainText, KeyUsage usage) throws KerberosException {
      EncryptionEngine engine = this.getEngine(key);
      return engine.getEncryptedData(key, plainText, usage);
   }

   public byte[] decrypt(EncryptionKey key, EncryptedData data, KeyUsage usage) throws KerberosException {
      LOG_KRB.debug("Decrypting data using key {} and usage {}", key.getKeyType(), usage);
      EncryptionEngine engine = this.getEngine(key);
      return engine.getDecryptedData(key, data, usage);
   }

   private EncryptionEngine getEngine(EncryptionKey key) throws KerberosException {
      EncryptionType encryptionType = key.getKeyType();
      Class<?> clazz = (Class)DEFAULT_CIPHERS.get(encryptionType);
      if (clazz == null) {
         throw new KerberosException(ErrorType.KDC_ERR_ETYPE_NOSUPP);
      } else {
         try {
            return (EncryptionEngine)clazz.newInstance();
         } catch (IllegalAccessException iae) {
            throw new KerberosException(ErrorType.KDC_ERR_ETYPE_NOSUPP, iae);
         } catch (InstantiationException ie) {
            throw new KerberosException(ErrorType.KDC_ERR_ETYPE_NOSUPP, ie);
         }
      }
   }

   static {
      LOG_KRB = LoggerFactory.getLogger(Loggers.KERBEROS_LOG.getName());
      Map<EncryptionType, Class<? extends EncryptionEngine>> map = new HashMap();
      map.put(EncryptionType.DES_CBC_MD5, DesCbcMd5Encryption.class);
      map.put(EncryptionType.DES3_CBC_SHA1_KD, Des3CbcSha1KdEncryption.class);
      map.put(EncryptionType.AES128_CTS_HMAC_SHA1_96, Aes128CtsSha1Encryption.class);
      map.put(EncryptionType.AES256_CTS_HMAC_SHA1_96, Aes256CtsSha1Encryption.class);
      map.put(EncryptionType.RC4_HMAC, ArcFourHmacMd5Encryption.class);
      DEFAULT_CIPHERS = Collections.unmodifiableMap(map);
   }
}
