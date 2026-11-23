package org.apache.activemq.artemis.utils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.jboss.logging.Logger;

public class DefaultSensitiveStringCodec implements SensitiveDataCodec<String> {
   private static final Logger logger = Logger.getLogger(DefaultSensitiveStringCodec.class);
   public static final String ALGORITHM = "algorithm";
   public static final String BLOWFISH_KEY = "key";
   public static final String ONE_WAY = "one-way";
   public static final String TWO_WAY = "two-way";
   private CodecAlgorithm algorithm;

   public DefaultSensitiveStringCodec() {
      this.algorithm = new BlowfishAlgorithm(Collections.EMPTY_MAP);
   }

   public String decode(Object secret) throws Exception {
      return this.algorithm.decode((String)secret);
   }

   public String encode(Object secret) throws Exception {
      return this.algorithm.encode((String)secret);
   }

   public void init(Map<String, String> params) throws Exception {
      String algorithm = (String)params.get("algorithm");
      if (algorithm != null && !algorithm.equals("two-way")) {
         if (!algorithm.equals("one-way")) {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
         }

         this.algorithm = new PBKDF2Algorithm(params);
      } else {
         this.algorithm = new BlowfishAlgorithm(params);
      }

   }

   public static void main(String[] args) throws Exception {
      if (args.length != 1) {
         System.err.println("Use: java -cp <classPath> org.apache.activemq.artemis.utils.DefaultSensitiveStringCodec password-to-encode");
         System.err.println("Error: no password on the args");
         System.exit(-1);
      }

      DefaultSensitiveStringCodec codec = new DefaultSensitiveStringCodec();
      Map<String, String> params = new HashMap();
      Properties properties = System.getProperties();
      synchronized(properties) {
         for(String name : properties.stringPropertyNames()) {
            params.put(name, properties.getProperty(name));
         }
      }

      codec.init(params);
      Object encode = codec.encode(args[0]);
      System.out.println("Encoded password (without quotes): \"" + encode + "\"");
   }

   public boolean verify(char[] inputValue, String storedValue) {
      return this.algorithm.verify(inputValue, storedValue);
   }

   private abstract class CodecAlgorithm {
      protected Map<String, String> params;

      CodecAlgorithm(Map<String, String> params) {
         this.params = params;
      }

      public abstract String decode(String var1) throws Exception;

      public abstract String encode(String var1) throws Exception;

      public boolean verify(char[] inputValue, String storedValue) {
         return false;
      }
   }

   private class BlowfishAlgorithm extends CodecAlgorithm {
      private byte[] internalKey = "clusterpassword".getBytes();

      BlowfishAlgorithm(Map<String, String> params) {
         super(params);
         String key = (String)params.get("key");
         if (key != null) {
            this.updateKey(key);
         }

      }

      private void updateKey(String key) {
         this.internalKey = key.getBytes();
      }

      public String decode(String secret) throws Exception {
         SecretKeySpec key = new SecretKeySpec(this.internalKey, "Blowfish");

         byte[] encoding;
         try {
            encoding = (new BigInteger(secret, 16)).toByteArray();
         } catch (Exception ex) {
            if (DefaultSensitiveStringCodec.logger.isDebugEnabled()) {
               DefaultSensitiveStringCodec.logger.debug(ex.getMessage(), ex);
            }

            throw new IllegalArgumentException("Password must be encrypted.");
         }

         if (encoding.length % 8 != 0) {
            int length = encoding.length;
            int newLength = (length / 8 + 1) * 8;
            int pad = newLength - length;
            byte[] old = encoding;
            encoding = new byte[newLength];
            System.arraycopy(old, 0, encoding, pad, old.length);
         }

         Cipher cipher = Cipher.getInstance("Blowfish");
         cipher.init(2, key);
         byte[] decode = cipher.doFinal(encoding);
         return new String(decode);
      }

      public String encode(String secret) throws Exception {
         SecretKeySpec key = new SecretKeySpec(this.internalKey, "Blowfish");
         Cipher cipher = Cipher.getInstance("Blowfish");
         cipher.init(1, key);
         byte[] encoding = cipher.doFinal(secret.getBytes());
         BigInteger n = new BigInteger(encoding);
         return n.toString(16);
      }
   }

   private class PBKDF2Algorithm extends CodecAlgorithm {
      private static final String SEPARATOR = ":";
      private String sceretKeyAlgorithm = "PBKDF2WithHmacSHA1";
      private String randomScheme = "SHA1PRNG";
      private int keyLength = 512;
      private int saltLength = 32;
      private int iterations = 1024;
      private SecretKeyFactory skf;

      PBKDF2Algorithm(Map<String, String> params) throws NoSuchAlgorithmException {
         super(params);
         this.skf = SecretKeyFactory.getInstance(this.sceretKeyAlgorithm);
      }

      public String decode(String secret) throws Exception {
         throw new IllegalArgumentException("Algorithm doesn't support decoding");
      }

      public byte[] getSalt() throws NoSuchAlgorithmException {
         byte[] salt = RandomUtil.randomBytes(this.saltLength);
         return salt;
      }

      public String encode(String secret) throws Exception {
         char[] chars = secret.toCharArray();
         byte[] salt = this.getSalt();
         StringBuilder builder = new StringBuilder();
         builder.append(this.iterations).append(":").append(ByteUtil.bytesToHex(salt)).append(":");
         PBEKeySpec spec = new PBEKeySpec(chars, salt, this.iterations, this.keyLength);
         byte[] hash = this.skf.generateSecret(spec).getEncoded();
         String hexValue = ByteUtil.bytesToHex(hash);
         builder.append(hexValue);
         return builder.toString();
      }

      public boolean verify(char[] plainChars, String storedValue) {
         String[] parts = storedValue.split(":");
         int originalIterations = Integer.parseInt(parts[0]);
         byte[] salt = ByteUtil.hexToBytes(parts[1]);
         byte[] originalHash = ByteUtil.hexToBytes(parts[2]);
         PBEKeySpec spec = new PBEKeySpec(plainChars, salt, originalIterations, originalHash.length * 8);

         byte[] newHash;
         try {
            newHash = this.skf.generateSecret(spec).getEncoded();
         } catch (InvalidKeySpecException var10) {
            return false;
         }

         return Arrays.equals(newHash, originalHash);
      }
   }
}
