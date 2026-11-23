package org.apache.openejb.core.security.jaas;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class UsernamePasswordCallbackHandler implements CallbackHandler {
   private final String username;
   private final String password;

   public UsernamePasswordCallbackHandler(String username, String password) {
      this.username = username;
      this.password = password;
   }

   public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
      for(int i = 0; i < callbacks.length; ++i) {
         Callback callback = callbacks[i];
         if (callback instanceof PasswordCallback) {
            PasswordCallback passwordCallback = (PasswordCallback)callback;
            if (this.password == null) {
               passwordCallback.setPassword((char[])null);
            } else {
               passwordCallback.setPassword(this.password.toCharArray());
            }
         } else if (callback instanceof NameCallback) {
            NameCallback nameCallback = (NameCallback)callback;
            if (this.username == null) {
               nameCallback.setName((String)null);
            } else {
               nameCallback.setName(this.username);
            }
         }
      }

   }
}
