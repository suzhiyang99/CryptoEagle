package org.spark_project.jetty.security;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.security.auth.Subject;
import javax.servlet.ServletRequest;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.security.Credential;

public abstract class MappedLoginService extends AbstractLifeCycle implements LoginService {
   private static final Logger LOG = Log.getLogger(MappedLoginService.class);
   protected IdentityService _identityService = new DefaultIdentityService();
   protected String _name;
   protected final ConcurrentMap<String, UserIdentity> _users = new ConcurrentHashMap();

   protected MappedLoginService() {
   }

   public String getName() {
      return this._name;
   }

   public IdentityService getIdentityService() {
      return this._identityService;
   }

   public ConcurrentMap<String, UserIdentity> getUsers() {
      return this._users;
   }

   public void setIdentityService(IdentityService identityService) {
      if (this.isRunning()) {
         throw new IllegalStateException("Running");
      } else {
         this._identityService = identityService;
      }
   }

   public void setName(String name) {
      if (this.isRunning()) {
         throw new IllegalStateException("Running");
      } else {
         this._name = name;
      }
   }

   public void setUsers(Map<String, UserIdentity> users) {
      if (this.isRunning()) {
         throw new IllegalStateException("Running");
      } else {
         this._users.clear();
         this._users.putAll(users);
      }
   }

   protected void doStart() throws Exception {
      this.loadUsers();
      super.doStart();
   }

   protected void doStop() throws Exception {
      super.doStop();
   }

   public void logout(UserIdentity identity) {
      LOG.debug("logout {}", identity);
   }

   public String toString() {
      return this.getClass().getSimpleName() + "[" + this._name + "]";
   }

   protected synchronized UserIdentity putUser(String userName, Object info) {
      UserIdentity identity;
      if (info instanceof UserIdentity) {
         identity = (UserIdentity)info;
      } else {
         Credential credential = info instanceof Credential ? (Credential)info : Credential.getCredential(info.toString());
         Principal userPrincipal = new KnownUser(userName, credential);
         Subject subject = new Subject();
         subject.getPrincipals().add(userPrincipal);
         subject.getPrivateCredentials().add(credential);
         subject.setReadOnly();
         identity = this._identityService.newUserIdentity(subject, userPrincipal, IdentityService.NO_ROLES);
      }

      this._users.put(userName, identity);
      return identity;
   }

   public synchronized UserIdentity putUser(String userName, Credential credential, String[] roles) {
      Principal userPrincipal = new KnownUser(userName, credential);
      Subject subject = new Subject();
      subject.getPrincipals().add(userPrincipal);
      subject.getPrivateCredentials().add(credential);
      if (roles != null) {
         for(String role : roles) {
            subject.getPrincipals().add(new RolePrincipal(role));
         }
      }

      subject.setReadOnly();
      UserIdentity identity = this._identityService.newUserIdentity(subject, userPrincipal, roles);
      this._users.put(userName, identity);
      return identity;
   }

   public synchronized UserIdentity putUser(KnownUser userPrincipal, String[] roles) {
      Subject subject = new Subject();
      subject.getPrincipals().add(userPrincipal);
      subject.getPrivateCredentials().add(userPrincipal._credential);
      if (roles != null) {
         for(String role : roles) {
            subject.getPrincipals().add(new RolePrincipal(role));
         }
      }

      subject.setReadOnly();
      UserIdentity identity = this._identityService.newUserIdentity(subject, userPrincipal, roles);
      this._users.put(userPrincipal._name, identity);
      return identity;
   }

   public void removeUser(String username) {
      this._users.remove(username);
   }

   public UserIdentity login(String username, Object credentials, ServletRequest request) {
      if (username == null) {
         return null;
      } else {
         UserIdentity user = (UserIdentity)this._users.get(username);
         if (user == null) {
            KnownUser userPrincipal = this.loadUserInfo(username);
            if (userPrincipal != null && userPrincipal.authenticate(credentials)) {
               String[] roles = this.loadRoleInfo(userPrincipal);
               user = this.putUser(userPrincipal, roles);
               return user;
            }
         } else {
            UserPrincipal principal = (UserPrincipal)user.getUserPrincipal();
            if (principal.authenticate(credentials)) {
               return user;
            }
         }

         return null;
      }
   }

   public boolean validate(UserIdentity user) {
      if (this._users.containsKey(user.getUserPrincipal().getName())) {
         return true;
      } else {
         return this.loadUser(user.getUserPrincipal().getName()) != null;
      }
   }

   protected abstract String[] loadRoleInfo(KnownUser var1);

   protected abstract KnownUser loadUserInfo(String var1);

   protected abstract UserIdentity loadUser(String var1);

   protected abstract void loadUsers() throws IOException;

   public static class RolePrincipal implements Principal, Serializable {
      private static final long serialVersionUID = 2998397924051854402L;
      private final String _roleName;

      public RolePrincipal(String name) {
         this._roleName = name;
      }

      public String getName() {
         return this._roleName;
      }
   }

   public static class Anonymous implements UserPrincipal, Serializable {
      private static final long serialVersionUID = 1097640442553284845L;

      public boolean isAuthenticated() {
         return false;
      }

      public String getName() {
         return "Anonymous";
      }

      public boolean authenticate(Object credentials) {
         return false;
      }
   }

   public static class KnownUser implements UserPrincipal, Serializable {
      private static final long serialVersionUID = -6226920753748399662L;
      private final String _name;
      private final Credential _credential;

      public KnownUser(String name, Credential credential) {
         this._name = name;
         this._credential = credential;
      }

      public boolean authenticate(Object credentials) {
         return this._credential != null && this._credential.check(credentials);
      }

      public String getName() {
         return this._name;
      }

      public boolean isAuthenticated() {
         return true;
      }

      public String toString() {
         return this._name;
      }
   }

   public interface UserPrincipal extends Principal, Serializable {
      boolean authenticate(Object var1);

      boolean isAuthenticated();
   }
}
