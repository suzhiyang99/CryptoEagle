package org.apache.openejb.core.security;

import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.openejb.core.security.jaas.UsernamePasswordCallbackHandler;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.ConfUtils;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.xbean.finder.archive.FileArchive;

public class SecurityServiceImpl extends AbstractSecurityService {
   private static final Map<Object, LoginContext> contexts = new ConcurrentHashMap();

   public SecurityServiceImpl() {
      this(autoJaccProvider());
   }

   public SecurityServiceImpl(String jaccProviderClass) {
      super(jaccProviderClass);
      installJaas();

      try {
         this.login("", "");
      } catch (Throwable var3) {
      }

   }

   protected static void installJaas() {
      String path = SystemInstance.get().getOptions().get("java.security.auth.login.config", (String)null);
      if (path == null) {
         URL loginConfig = ConfUtils.getConfResource("login.config");
         JavaSecurityManagers.setSystemProperty("java.security.auth.login.config", FileArchive.decode(loginConfig.toExternalForm()));
      }
   }

   public UUID login(String realmName, String username, String password) throws LoginException {
      if (realmName == null) {
         realmName = this.getRealmName();
      }

      LoginContext context = new LoginContext(realmName, new UsernamePasswordCallbackHandler(username, password));
      context.login();
      Subject subject = context.getSubject();
      UUID token = this.registerSubject(subject);
      contexts.put(token, context);
      return token;
   }

   public void logout(UUID securityIdentity) throws LoginException {
      LoginContext context = (LoginContext)contexts.remove(securityIdentity);
      if (null == context) {
         throw new IllegalStateException("Unable to logout. Can not recover LoginContext.");
      } else {
         context.logout();
         super.logout(securityIdentity);
      }
   }
}
