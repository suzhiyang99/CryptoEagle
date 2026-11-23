package org.spark_project.jetty.security;

import java.util.Properties;
import javax.security.auth.Subject;
import javax.servlet.ServletRequest;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.B64Code;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.resource.Resource;

public class SpnegoLoginService extends AbstractLifeCycle implements LoginService {
   private static final Logger LOG = Log.getLogger(SpnegoLoginService.class);
   protected IdentityService _identityService;
   protected String _name;
   private String _config;
   private String _targetName;

   public SpnegoLoginService() {
   }

   public SpnegoLoginService(String name) {
      this.setName(name);
   }

   public SpnegoLoginService(String name, String config) {
      this.setName(name);
      this.setConfig(config);
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      if (this.isRunning()) {
         throw new IllegalStateException("Running");
      } else {
         this._name = name;
      }
   }

   public String getConfig() {
      return this._config;
   }

   public void setConfig(String config) {
      if (this.isRunning()) {
         throw new IllegalStateException("Running");
      } else {
         this._config = config;
      }
   }

   protected void doStart() throws Exception {
      Properties properties = new Properties();
      Resource resource = Resource.newResource(this._config);
      properties.load(resource.getInputStream());
      this._targetName = properties.getProperty("targetName");
      LOG.debug("Target Name {}", this._targetName);
      super.doStart();
   }

   public UserIdentity login(String username, Object credentials, ServletRequest request) {
      String encodedAuthToken = (String)credentials;
      byte[] authToken = B64Code.decode(encodedAuthToken);
      GSSManager manager = GSSManager.getInstance();

      try {
         Oid krb5Oid = new Oid("1.3.6.1.5.5.2");
         GSSName gssName = manager.createName(this._targetName, (Oid)null);
         GSSCredential serverCreds = manager.createCredential(gssName, Integer.MAX_VALUE, krb5Oid, 2);
         GSSContext gContext = manager.createContext(serverCreds);
         if (gContext == null) {
            LOG.debug("SpnegoUserRealm: failed to establish GSSContext");
         } else {
            while(!gContext.isEstablished()) {
               authToken = gContext.acceptSecContext(authToken, 0, authToken.length);
            }

            if (gContext.isEstablished()) {
               String clientName = gContext.getSrcName().toString();
               String role = clientName.substring(clientName.indexOf(64) + 1);
               LOG.debug("SpnegoUserRealm: established a security context");
               LOG.debug("Client Principal is: " + gContext.getSrcName());
               LOG.debug("Server Principal is: " + gContext.getTargName());
               LOG.debug("Client Default Role: " + role);
               SpnegoUserPrincipal user = new SpnegoUserPrincipal(clientName, authToken);
               Subject subject = new Subject();
               subject.getPrincipals().add(user);
               return this._identityService.newUserIdentity(subject, user, new String[]{role});
            }
         }
      } catch (GSSException gsse) {
         LOG.warn(gsse);
      }

      return null;
   }

   public boolean validate(UserIdentity user) {
      return false;
   }

   public IdentityService getIdentityService() {
      return this._identityService;
   }

   public void setIdentityService(IdentityService service) {
      this._identityService = service;
   }

   public void logout(UserIdentity user) {
   }
}
