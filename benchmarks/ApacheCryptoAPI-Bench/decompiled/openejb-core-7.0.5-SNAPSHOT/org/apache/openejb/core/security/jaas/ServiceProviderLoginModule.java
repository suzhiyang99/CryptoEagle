package org.apache.openejb.core.security.jaas;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class ServiceProviderLoginModule implements LoginModule {
   private static final Logger log;
   private Subject subject;
   private CallbackHandler callbackHandler;
   private ServiceLoader<LoginProvider> loader;
   public Set<Principal> principals = new LinkedHashSet();
   private UserData userData;

   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
      this.subject = subject;
      this.callbackHandler = callbackHandler;
      this.loader = ServiceLoader.load(LoginProvider.class);
   }

   private UserData getUserData() throws LoginException {
      Callback[] callbacks = new Callback[2];
      callbacks[0] = new NameCallback("Username: ");
      callbacks[1] = new PasswordCallback("Password: ", false);

      try {
         this.callbackHandler.handle(callbacks);
      } catch (IOException ioe) {
         throw new LoginException(ioe.getMessage());
      } catch (UnsupportedCallbackException uce) {
         throw new LoginException(uce.getMessage() + " not available to obtain information from user");
      }

      String user = ((NameCallback)callbacks[0]).getName();
      char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
      if (tmpPassword == null) {
         tmpPassword = new char[0];
      }

      String password = new String(tmpPassword);
      return new UserData(user, password);
   }

   public boolean login() throws LoginException {
      Iterator<LoginProvider> loginProviders = this.loader.iterator();
      if (!loginProviders.hasNext()) {
         throw new FailedLoginException("No LoginProvider defined.");
      } else {
         this.userData = this.getUserData();

         while(loginProviders.hasNext()) {
            LoginProvider loginProvider = (LoginProvider)loginProviders.next();
            List<String> myGroups = loginProvider.authenticate(this.userData.user, this.userData.pass);
            if (myGroups != null) {
               this.userData.groups.addAll(myGroups);
            }
         }

         return true;
      }
   }

   public boolean commit() throws LoginException {
      this.principals.add(new UserPrincipal(this.userData.user));

      for(String myGroup : this.userData.groups) {
         this.principals.add(new GroupPrincipal(myGroup));
      }

      this.subject.getPrincipals().addAll(this.principals);
      this.clear();
      log.debug("commit");
      return true;
   }

   public boolean abort() throws LoginException {
      this.clear();
      log.debug("abort");
      return true;
   }

   public boolean logout() throws LoginException {
      this.subject.getPrincipals().removeAll(this.principals);
      this.principals.clear();
      log.debug("logout");
      return true;
   }

   private void clear() {
      this.userData = null;
   }

   static {
      log = Logger.getInstance(LogCategory.OPENEJB_SECURITY, "org.apache.openejb.util.resources");
   }

   private final class UserData {
      public final String user;
      public final String pass;
      public final Set<String> groups;

      private UserData(String user, String pass) {
         this.groups = new HashSet();
         this.user = user;
         this.pass = pass;
      }
   }
}
