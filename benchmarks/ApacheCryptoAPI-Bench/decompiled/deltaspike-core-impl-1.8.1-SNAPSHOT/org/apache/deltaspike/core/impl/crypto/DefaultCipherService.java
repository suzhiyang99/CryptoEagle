package org.apache.deltaspike.core.impl.crypto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DefaultCipherService {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   private static final String HASH_ALGORITHM = "SHA-256";
   private static final String CIPHER_ALGORITHM = "AES";

   public String setMasterHash(String masterPassword, String masterSalt, boolean overwrite) throws IOException {
      File masterFile = this.getMasterFile();
      if (!masterFile.getParentFile().exists() && !masterFile.getParentFile().mkdirs()) {
         throw new IOException("Can not create directory " + masterFile.getParent());
      } else {
         String saltHash = this.byteToHex(this.secureHash(masterSalt));
         String saltKey = this.byteToHex(this.secureHash(saltHash));
         String encrypted = this.byteToHex(this.aesEncrypt(this.byteToHex(this.secureHash(masterPassword)), saltHash));
         Properties keys = new Properties();
         if (masterFile.exists()) {
            keys = this.loadProperties(masterFile.toURI().toURL());
         }

         if (keys.get(saltKey) != null && !overwrite) {
            throw new IllegalStateException("MasterKey for hash " + saltKey + " already exists. Forced overwrite option needed");
         } else {
            keys.put(saltKey, encrypted);
            keys.store(new FileOutputStream(masterFile), (String)null);
            return saltKey;
         }
      }
   }

   protected String getMasterKey(String masterSalt) {
      File masterFile = this.getMasterFile();
      if (!masterFile.exists()) {
         throw new IllegalStateException("Could not find master.hash file. Create a master password first!");
      } else {
         try {
            String saltHash = this.byteToHex(this.secureHash(masterSalt));
            String saltKey = this.byteToHex(this.secureHash(saltHash));
            Properties keys = this.loadProperties(masterFile.toURI().toURL());
            String encryptedMasterKey = (String)keys.get(saltKey);
            if (encryptedMasterKey == null) {
               throw new IllegalStateException("Could not find master key for hash " + saltKey + ". Create a master password first!");
            } else {
               return this.aesDecrypt(this.hexToByte(encryptedMasterKey), saltHash);
            }
         } catch (MalformedURLException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public String encrypt(String cleartext, String masterSalt) {
      return this.byteToHex(this.aesEncrypt(cleartext, this.getMasterKey(masterSalt)));
   }

   public String decrypt(String encryptedValue, String masterSalt) {
      return this.aesDecrypt(this.hexToByte(encryptedValue), this.getMasterKey(masterSalt));
   }

   protected File getMasterFile() {
      String userHome = System.getProperty("user.home");
      if (userHome != null && !userHome.isEmpty()) {
         return new File(userHome, ".deltaspike/master.hash");
      } else {
         throw new IllegalStateException("Can not determine user home directory");
      }
   }

   protected byte[] secureHash(String value) {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         return md.digest(value.getBytes(UTF_8));
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      }
   }

   public byte[] aesEncrypt(String valueToEncrypt, String key) {
      try {
         SecretKeySpec secretKeySpec = this.getSecretKeySpec(key);
         Cipher cipher = Cipher.getInstance("AES");
         cipher.init(1, secretKeySpec);
         return cipher.doFinal(valueToEncrypt.getBytes(UTF_8));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public String aesDecrypt(byte[] encryptedValue, String key) {
      try {
         SecretKeySpec secretKeySpec = this.getSecretKeySpec(key);
         Cipher cipher = Cipher.getInstance("AES");
         cipher.init(2, secretKeySpec);
         return new String(cipher.doFinal(encryptedValue), UTF_8);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private SecretKeySpec getSecretKeySpec(String password) {
      byte[] pwdHash = this.secureHash(password);
      byte[] key = Arrays.copyOf(pwdHash, 16);
      return new SecretKeySpec(key, "AES");
   }

   protected String byteToHex(byte[] hash) {
      StringBuilder sb = new StringBuilder(hash.length * 2);

      for(byte b : hash) {
         sb.append(Character.forDigit(b >> 4 & 15, 16));
         sb.append(Character.forDigit(b & 15, 16));
      }

      return sb.toString();
   }

   protected byte[] hexToByte(String hexString) {
      if (hexString != null && hexString.length() != 0) {
         hexString = hexString.trim();
         if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("not a valid hex string " + hexString);
         } else {
            byte[] bytes = new byte[hexString.length() / 2];

            for(int i = 0; i < hexString.length() / 2; ++i) {
               int val = (Character.digit(hexString.charAt(i * 2), 16) << 4) + Character.digit(hexString.charAt(i * 2 + 1), 16);
               bytes[i] = (byte)val;
            }

            return bytes;
         }
      } else {
         return new byte[0];
      }
   }

   private Properties loadProperties(URL url) {
      Properties props = new Properties();
      InputStream inputStream = null;

      try {
         inputStream = url.openStream();
         if (inputStream != null) {
            props.load(inputStream);
         }
      } catch (IOException e) {
         throw new IllegalStateException(e);
      } finally {
         try {
            if (inputStream != null) {
               inputStream.close();
            }
         } catch (IOException var11) {
         }

      }

      return props;
   }
}
