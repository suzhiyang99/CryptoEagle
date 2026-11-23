package org.apache.directory.server.kerberos.shared.crypto.checksum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.directory.server.kerberos.shared.crypto.encryption.Aes128CtsSha1Encryption;
import org.apache.directory.server.kerberos.shared.crypto.encryption.Aes256CtsSha1Encryption;
import org.apache.directory.server.kerberos.shared.crypto.encryption.Des3CbcSha1KdEncryption;
import org.apache.directory.server.kerberos.shared.crypto.encryption.KeyUsage;
import org.apache.directory.shared.kerberos.components.Checksum;
import org.apache.directory.shared.kerberos.crypto.checksum.ChecksumType;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;

public class ChecksumHandler {
   private static final Map<ChecksumType, Class<?>> DEFAULT_CHECKSUMS;

   public Checksum calculateChecksum(ChecksumType checksumType, byte[] bytes, byte[] key, KeyUsage usage) throws KerberosException {
      if (!DEFAULT_CHECKSUMS.containsKey(checksumType)) {
         throw new KerberosException(ErrorType.KDC_ERR_SUMTYPE_NOSUPP);
      } else {
         ChecksumEngine digester = this.getEngine(checksumType);
         return new Checksum(checksumType, digester.calculateChecksum(bytes, key, usage));
      }
   }

   public void verifyChecksum(Checksum checksum, byte[] bytes, byte[] key, KeyUsage usage) throws KerberosException {
      if (checksum == null) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_INAPP_CKSUM);
      } else if (!DEFAULT_CHECKSUMS.containsKey(checksum.getChecksumType())) {
         throw new KerberosException(ErrorType.KDC_ERR_SUMTYPE_NOSUPP);
      } else {
         ChecksumType checksumType = checksum.getChecksumType();
         ChecksumEngine digester = this.getEngine(checksumType);
         Checksum newChecksum = new Checksum(checksumType, digester.calculateChecksum(bytes, key, usage));
         if (!newChecksum.equals(checksum)) {
            throw new KerberosException(ErrorType.KRB_AP_ERR_MODIFIED);
         }
      }
   }

   private ChecksumEngine getEngine(ChecksumType checksumType) throws KerberosException {
      Class<?> clazz = (Class)DEFAULT_CHECKSUMS.get(checksumType);
      if (clazz == null) {
         throw new KerberosException(ErrorType.KDC_ERR_SUMTYPE_NOSUPP);
      } else {
         try {
            return (ChecksumEngine)clazz.newInstance();
         } catch (IllegalAccessException iae) {
            throw new KerberosException(ErrorType.KDC_ERR_SUMTYPE_NOSUPP, iae);
         } catch (InstantiationException ie) {
            throw new KerberosException(ErrorType.KDC_ERR_SUMTYPE_NOSUPP, ie);
         }
      }
   }

   static {
      Map<ChecksumType, Class<?>> map = new HashMap();
      map.put(ChecksumType.HMAC_MD5, HmacMd5Checksum.class);
      map.put(ChecksumType.HMAC_SHA1_96_AES128, Aes128CtsSha1Encryption.class);
      map.put(ChecksumType.HMAC_SHA1_96_AES256, Aes256CtsSha1Encryption.class);
      map.put(ChecksumType.HMAC_SHA1_DES3_KD, Des3CbcSha1KdEncryption.class);
      map.put(ChecksumType.RSA_MD5, RsaMd5Checksum.class);
      DEFAULT_CHECKSUMS = Collections.unmodifiableMap(map);
   }
}
