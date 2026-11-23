package org.apache.openejb.core.security;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.CertStoreCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.callback.PasswordValidationCallback;
import javax.security.auth.message.callback.PrivateKeyCallback;
import javax.security.auth.message.callback.SecretKeyCallback;
import javax.security.auth.message.callback.TrustStoreCallback;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.SecurityService;

public class ConnectorCallbackHandler implements CallbackHandler {
   private Principal callerPrincipal;
   private String[] groupsArray;
   private final String securityRealmName;

   public ConnectorCallbackHandler(String securityRealmName) {
      this.securityRealmName = securityRealmName;
   }

   public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
      for(Callback callback : callbacks) {
         if (callback instanceof CallerPrincipalCallback) {
            this.callerPrincipal = ((CallerPrincipalCallback)callback).getPrincipal();
         } else if (callback instanceof GroupPrincipalCallback) {
            this.groupsArray = ((GroupPrincipalCallback)callback).getGroups();
         } else if (callback instanceof PasswordValidationCallback) {
            PasswordValidationCallback passwordValidationCallback = (PasswordValidationCallback)callback;
            String userName = passwordValidationCallback.getUsername();
            char[] password = passwordValidationCallback.getPassword();
            SecurityService securityService = (SecurityService)SystemInstance.get().getComponent(SecurityService.class);

            try {
               Object loginObj = securityService.login(this.securityRealmName, userName, password == null ? "" : new String(password));
               securityService.associate(loginObj);
               this.callerPrincipal = securityService.getCallerPrincipal();
               passwordValidationCallback.setResult(true);
            } catch (LoginException var11) {
               passwordValidationCallback.setResult(false);
            }
         } else if (!(callback instanceof CertStoreCallback) && !(callback instanceof PrivateKeyCallback) && !(callback instanceof SecretKeyCallback) && !(callback instanceof TrustStoreCallback)) {
            throw new UnsupportedCallbackException(callback);
         }
      }

   }

   public Principal getCallerPrincipal() {
      return this.callerPrincipal;
   }

   public List<String> getGroups() {
      return this.groupsArray == null ? null : Arrays.asList(this.groupsArray);
   }
}
