package org.spark_project.jetty.util.security;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.spark_project.jetty.util.TypeUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class Credential implements Serializable {
   private static final long serialVersionUID = -7760551052768181572L;
   private static final Logger LOG = Log.getLogger(Credential.class);

   public abstract boolean check(Object var1);

   public static Credential getCredential(String credential) {
      if (credential.startsWith("CRYPT:")) {
         return new Crypt(credential);
      } else {
         return (Credential)(credential.startsWith("MD5:") ? new MD5(credential) : new Password(credential));
      }
   }

   protected static boolean stringEquals(String s1, String s2) {
      if (s1 == s2) {
         return true;
      } else if (s1 != null && s2 != null) {
         boolean result = true;
         int l1 = s1.length();
         int l2 = s2.length();
         if (l1 != l2) {
            result = false;
         }

         int l = Math.min(l1, l2);

         for(int i = 0; i < l; ++i) {
            result &= s1.charAt(i) == s2.charAt(i);
         }

         return result;
      } else {
         return false;
      }
   }

   protected static boolean byteEquals(byte[] b1, byte[] b2) {
      if (b1 == b2) {
         return true;
      } else if (b1 != null && b2 != null) {
         boolean result = true;
         int l1 = b1.length;
         int l2 = b2.length;
         if (l1 != l2) {
            result = false;
         }

         int l = Math.min(l1, l2);

         for(int i = 0; i < l; ++i) {
            result &= b1[i] == b2[i];
         }

         return result;
      } else {
         return false;
      }
   }

   public static class Crypt extends Credential {
      private static final long serialVersionUID = -2027792997664744210L;
      private static final String __TYPE = "CRYPT:";
      private final String _cooked;

      Crypt(String cooked) {
         this._cooked = cooked.startsWith("CRYPT:") ? cooked.substring("CRYPT:".length()) : cooked;
      }

      public boolean check(Object credentials) {
         if (credentials instanceof char[]) {
            credentials = new String((char[])credentials);
         }

         if (!(credentials instanceof String) && !(credentials instanceof Password)) {
            Credential.LOG.warn("Can't check " + credentials.getClass() + " against CRYPT");
         }

         return stringEquals(this._cooked, UnixCrypt.crypt(credentials.toString(), this._cooked));
      }

      public static String crypt(String user, String pw) {
         return "CRYPT:" + UnixCrypt.crypt(pw, user);
      }
   }

   public static class MD5 extends Credential {
      private static final long serialVersionUID = 5533846540822684240L;
      private static final String __TYPE = "MD5:";
      private static final Object __md5Lock = new Object();
      private static MessageDigest __md;
      private final byte[] _digest;

      MD5(String digest) {
         digest = digest.startsWith("MD5:") ? digest.substring("MD5:".length()) : digest;
         this._digest = TypeUtil.parseBytes(digest, 16);
      }

      public byte[] getDigest() {
         return this._digest;
      }

      public boolean check(Object credentials) {
         try {
            if (credentials instanceof char[]) {
               credentials = new String((char[])credentials);
            }

            if (!(credentials instanceof Password) && !(credentials instanceof String)) {
               if (credentials instanceof MD5) {
                  MD5 md5 = (MD5)credentials;
                  return byteEquals(this._digest, md5._digest);
               } else if (credentials instanceof Credential) {
                  return ((Credential)credentials).check(this);
               } else {
                  Credential.LOG.warn("Can't check " + credentials.getClass() + " against MD5");
                  return false;
               }
            } else {
               byte[] digest;
               synchronized(__md5Lock) {
                  if (__md == null) {
                     __md = MessageDigest.getInstance("MD5");
                  }

                  __md.reset();
                  __md.update(credentials.toString().getBytes(StandardCharsets.ISO_8859_1));
                  digest = __md.digest();
               }

               return byteEquals(this._digest, digest);
            }
         } catch (Exception e) {
            Credential.LOG.warn(e);
            return false;
         }
      }

      public static String digest(String password) {
         try {
            byte[] digest;
            synchronized(__md5Lock) {
               if (__md == null) {
                  try {
                     __md = MessageDigest.getInstance("MD5");
                  } catch (Exception e) {
                     Credential.LOG.warn(e);
                     return null;
                  }
               }

               __md.reset();
               __md.update(password.getBytes(StandardCharsets.ISO_8859_1));
               digest = __md.digest();
            }

            return "MD5:" + TypeUtil.toString(digest, 16);
         } catch (Exception e) {
            Credential.LOG.warn(e);
            return null;
         }
      }
   }
}
