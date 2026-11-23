package org.apache.deltaspike.core.impl.crypto;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import org.apache.deltaspike.core.api.crypto.CipherService;

@ApplicationScoped
public class CdiCipherService implements CipherService {
   private DefaultCipherService cipherService = new DefaultCipherService();

   public void setMasterHash(String masterPassword, String masterSalt, boolean overwrite) throws IOException {
      this.cipherService.setMasterHash(masterPassword, masterSalt, overwrite);
   }

   public String encrypt(String cleartext, String masterSalt) {
      return this.cipherService.encrypt(cleartext, masterSalt);
   }

   public String decrypt(String encryptedValue, String masterSalt) {
      return this.cipherService.decrypt(encryptedValue, masterSalt);
   }
}
