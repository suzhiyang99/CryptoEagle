package org.apache.activemq.artemis.utils;

import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.ActiveMQExceptionType;
import org.apache.activemq.artemis.logs.ActiveMQUtilBundle;

public final class PasswordMaskingUtil {
   private PasswordMaskingUtil() {
   }

   public static HashProcessor getHashProcessor(String storedPassword) throws Exception {
      if (!isEncoded(storedPassword)) {
         return PasswordMaskingUtil.LazyPlainTextProcessorHolder.INSTANCE;
      } else {
         Exception secureProcessorException = PasswordMaskingUtil.LazySecureProcessorHolder.EXCEPTION;
         if (secureProcessorException != null) {
            throw new RuntimeException(secureProcessorException);
         } else {
            return PasswordMaskingUtil.LazySecureProcessorHolder.INSTANCE;
         }
      }
   }

   private static boolean isEncoded(String storedPassword) {
      return storedPassword == null || storedPassword.startsWith("ENC(") && storedPassword.endsWith(")");
   }

   public static HashProcessor getHashProcessor() {
      HashProcessor processor = PasswordMaskingUtil.LazySecureProcessorHolder.INSTANCE;
      if (processor == null) {
         processor = PasswordMaskingUtil.LazyPlainTextProcessorHolder.INSTANCE;
      }

      return processor;
   }

   public static SensitiveDataCodec<String> getCodec(String codecDesc) throws ActiveMQException {
      String[] parts = codecDesc.split(";");
      if (parts.length < 1) {
         throw new ActiveMQException(ActiveMQExceptionType.ILLEGAL_STATE, "Invalid PasswordCodec value: " + codecDesc);
      } else {
         String codecClassName = parts[0];
         SensitiveDataCodec<String> codecInstance = (SensitiveDataCodec)AccessController.doPrivileged(() -> {
            ServiceLoader<SensitiveDataCodec> serviceLoader = ServiceLoader.load(SensitiveDataCodec.class, PasswordMaskingUtil.class.getClassLoader());

            try {
               for(SensitiveDataCodec<String> codec : serviceLoader) {
                  if (codec.getClass().getCanonicalName().equals(codecClassName)) {
                     return (SensitiveDataCodec)codec.getClass().newInstance();
                  }
               }
            } catch (Exception var6) {
            }

            try {
               return (SensitiveDataCodec)PasswordMaskingUtil.class.getClassLoader().loadClass(codecClassName).newInstance();
            } catch (Exception var5) {
               try {
                  return (SensitiveDataCodec)Thread.currentThread().getContextClassLoader().loadClass(codecClassName).newInstance();
               } catch (Exception e2) {
                  throw ActiveMQUtilBundle.BUNDLE.errorCreatingCodec(e2, codecClassName);
               }
            }
         });
         if (parts.length > 1) {
            Map<String, String> props = new HashMap();

            for(int i = 1; i < parts.length; ++i) {
               String[] keyVal = parts[i].split("=");
               if (keyVal.length != 2) {
                  throw ActiveMQUtilBundle.BUNDLE.invalidProperty(parts[i]);
               }

               props.put(keyVal[0], keyVal[1]);
            }

            try {
               codecInstance.init(props);
            } catch (Exception e) {
               throw new ActiveMQException("Fail to init codec", e, ActiveMQExceptionType.SECURITY_EXCEPTION);
            }
         }

         return codecInstance;
      }
   }

   public static DefaultSensitiveStringCodec getDefaultCodec() {
      return new DefaultSensitiveStringCodec();
   }

   private static final class LazyPlainTextProcessorHolder {
      private static final HashProcessor INSTANCE = new NoHashProcessor();
   }

   private static final class LazySecureProcessorHolder {
      private static final HashProcessor INSTANCE;
      private static final Exception EXCEPTION;

      static {
         HashProcessor processor = null;
         Exception exception = null;
         String codecDesc = DefaultSensitiveStringCodec.class.getName() + ";" + "algorithm" + "=" + "one-way";

         try {
            DefaultSensitiveStringCodec codec = (DefaultSensitiveStringCodec)PasswordMaskingUtil.getCodec(codecDesc);
            processor = new SecureHashProcessor(codec);
         } catch (Exception e) {
            exception = e;
         } finally {
            EXCEPTION = exception;
            INSTANCE = processor;
         }

      }
   }
}
