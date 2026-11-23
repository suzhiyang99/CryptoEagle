package org.apache.wicket.util.crypt;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.wicket.util.lang.Args;

public class SunJceCrypt extends AbstractCrypt {
   private static final int COUNT = 17;
   public static final String DEFAULT_CRYPT_METHOD = "PBEWithMD5AndDES";
   public static final byte[] SALT = new byte[]{21, -116, -93, 74, 102, 81, 42, -68};
   private static final PBEParameterSpec PARAMETER_SPEC;
   private final String cryptMethod;

   public SunJceCrypt() {
      this("PBEWithMD5AndDES");
   }

   public SunJceCrypt(String cryptMethod) {
      this.cryptMethod = (String)Args.notNull(cryptMethod, "Crypt method");
   }

   protected byte[] crypt(byte[] input, int mode) throws GeneralSecurityException {
      SecretKey key = this.generateSecretKey();
      AlgorithmParameterSpec spec = this.createParameterSpec();
      Cipher ciph = this.createCipher(key, spec, mode);
      return ciph.doFinal(input);
   }

   protected Cipher createCipher(SecretKey key, AlgorithmParameterSpec spec, int mode) throws GeneralSecurityException {
      Cipher cipher = Cipher.getInstance(this.cryptMethod);
      cipher.init(mode, key, spec);
      return cipher;
   }

   protected SecretKey generateSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(this.cryptMethod);
      KeySpec spec = this.createKeySpec();
      return keyFactory.generateSecret(spec);
   }

   protected AlgorithmParameterSpec createParameterSpec() {
      return PARAMETER_SPEC;
   }

   protected KeySpec createKeySpec() {
      return new PBEKeySpec(this.getKey().toCharArray());
   }

   static {
      PARAMETER_SPEC = new PBEParameterSpec(SALT, 17);
   }
}
