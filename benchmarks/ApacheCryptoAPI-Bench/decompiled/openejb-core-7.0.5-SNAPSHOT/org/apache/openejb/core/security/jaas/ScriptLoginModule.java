package org.apache.openejb.core.security.jaas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.URLs;

public class ScriptLoginModule implements LoginModule {
   private static final Logger log;
   private Subject subject;
   private CallbackHandler callbackHandler;
   private Map<String, ?> options;
   public Set<Principal> principals = new LinkedHashSet();
   private UserData userData;

   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
      this.options = options;
      this.subject = subject;
      this.callbackHandler = callbackHandler;
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

   private File getScriptFile(String path) {
      if (path != null && !"".equals(path)) {
         try {
            URI uri = URLs.uri(path);
            File result = new File(uri);
            if (result.exists()) {
               return result;
            }
         } catch (Exception var4) {
         }

         File result = new File(path);
         if (result.exists()) {
            return result;
         } else {
            result = new File(JavaSecurityManagers.getSystemProperty("openejb.home"), "conf");
            File result = new File(result, path);
            return result.exists() ? result : null;
         }
      } else {
         File result = new File(JavaSecurityManagers.getSystemProperty("openejb.home"), "conf/loginscript.js");
         return result.exists() ? result : null;
      }
   }

   public boolean login() throws LoginException {
      File script = this.getScriptFile((String)this.options.get("scriptURI"));
      if (script == null) {
         script = this.getScriptFile(JavaSecurityManagers.getSystemProperty("openejb.ScriptLoginModule.scriptURI"));
         if (script == null) {
            script = this.getScriptFile((String)null);
         }
      }

      if (script == null) {
         throw new LoginException("No login script defined");
      } else {
         String scriptText;
         try {
            scriptText = (new Scanner(script)).useDelimiter("\\Z").next();
         } catch (FileNotFoundException var10) {
            throw new LoginException("Invalid login script URI.");
         }

         this.userData = this.getUserData();
         ScriptEngineManager manager = new ScriptEngineManager();
         ScriptEngine engine = manager.getEngineByName((String)this.options.get("engineName"));
         ScriptContext newContext = new SimpleScriptContext();
         Bindings bindings = newContext.getBindings(100);
         bindings.put("user", this.userData.user);
         bindings.put("password", this.userData.pass);

         List<String> myGroups;
         try {
            myGroups = (List)engine.eval(scriptText, newContext);
         } catch (ScriptException e) {
            throw new LoginException("Cannot execute login script. Msg: " + e.getMessage());
         }

         this.userData.groups.addAll(myGroups);
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
