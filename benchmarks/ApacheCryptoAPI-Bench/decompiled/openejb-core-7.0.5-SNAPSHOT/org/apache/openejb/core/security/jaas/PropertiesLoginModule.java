package org.apache.openejb.core.security.jaas;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
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
import org.apache.openejb.loader.IO;
import org.apache.openejb.util.ConfUtils;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class PropertiesLoginModule implements LoginModule {
   private static final String USER_FILE = "UsersFile";
   private static final String GROUP_FILE = "GroupsFile";
   private static final Logger log;
   private Subject subject;
   private CallbackHandler callbackHandler;
   private boolean debug;
   private Properties users = new Properties();
   private Properties groups = new Properties();
   private String user;
   private final Set principals = new LinkedHashSet();
   private URL usersUrl;
   private URL groupsUrl;

   public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
      this.subject = subject;
      this.callbackHandler = callbackHandler;
      this.debug = log.isDebugEnabled() || "true".equalsIgnoreCase((String)options.get("Debug"));
      String usersFile = String.valueOf(options.get("UsersFile"));
      String groupsFile = String.valueOf(options.get("GroupsFile"));
      this.usersUrl = ConfUtils.getConfResource(usersFile);
      this.groupsUrl = ConfUtils.getConfResource(groupsFile);
      if (this.debug) {
         log.debug("Users file: " + this.usersUrl.toExternalForm());
         log.debug("Groups file: " + this.groupsUrl.toExternalForm());
      }

   }

   public boolean login() throws LoginException {
      try {
         this.users = IO.readProperties(this.usersUrl);
      } catch (IOException var7) {
         throw new LoginException("Unable to load user properties file " + this.usersUrl.getFile());
      }

      try {
         this.groups = IO.readProperties(this.groupsUrl);
      } catch (IOException var6) {
         throw new LoginException("Unable to load group properties file " + this.groupsUrl.getFile());
      }

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

      this.user = ((NameCallback)callbacks[0]).getName();
      char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
      if (tmpPassword == null) {
         tmpPassword = new char[0];
      }

      String password = this.users.getProperty(this.user);
      if (password == null) {
         throw new FailedLoginException("User does not exist");
      } else if (!password.equals(new String(tmpPassword))) {
         throw new FailedLoginException("Password does not match");
      } else {
         this.users.clear();
         if (this.debug) {
            log.debug("Logged in as '" + this.user + "'");
         }

         return true;
      }
   }

   public boolean commit() throws LoginException {
      this.principals.add(new UserPrincipal(this.user));
      Enumeration enumeration = this.groups.keys();

      while(enumeration.hasMoreElements()) {
         String name = (String)enumeration.nextElement();
         String[] userList = String.valueOf(this.groups.getProperty(name)).split(",");

         for(int i = 0; i < userList.length; ++i) {
            if (this.user.equals(userList[i])) {
               this.principals.add(new GroupPrincipal(name));
               break;
            }
         }
      }

      this.subject.getPrincipals().addAll(this.principals);
      this.clear();
      if (this.debug) {
         log.debug("commit");
      }

      return true;
   }

   public boolean abort() throws LoginException {
      this.clear();
      if (this.debug) {
         log.debug("abort");
      }

      return true;
   }

   public boolean logout() throws LoginException {
      this.subject.getPrincipals().removeAll(this.principals);
      this.principals.clear();
      if (this.debug) {
         log.debug("logout");
      }

      return true;
   }

   private void clear() {
      this.groups.clear();
      this.user = null;
   }

   static {
      log = Logger.getInstance(LogCategory.OPENEJB_SECURITY, "org.apache.openejb.util.resources");
   }
}
