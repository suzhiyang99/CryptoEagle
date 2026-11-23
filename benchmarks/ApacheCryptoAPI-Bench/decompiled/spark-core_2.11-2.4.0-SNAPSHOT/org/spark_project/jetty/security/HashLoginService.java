package org.spark_project.jetty.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.resource.Resource;
import org.spark_project.jetty.util.security.Credential;

public class HashLoginService extends MappedLoginService implements PropertyUserStore.UserListener {
   private static final Logger LOG = Log.getLogger(HashLoginService.class);
   private PropertyUserStore _propertyUserStore;
   private String _config;
   private boolean hotReload = false;

   public HashLoginService() {
   }

   public HashLoginService(String name) {
      this.setName(name);
   }

   public HashLoginService(String name, String config) {
      this.setName(name);
      this.setConfig(config);
   }

   public String getConfig() {
      return this._config;
   }

   /** @deprecated */
   @Deprecated
   public Resource getConfigResource() {
      return null;
   }

   public void setConfig(String config) {
      this._config = config;
   }

   public boolean isHotReload() {
      return this.hotReload;
   }

   public void setHotReload(boolean enable) {
      if (this.isRunning()) {
         throw new IllegalStateException("Cannot set hot reload while user store is running");
      } else {
         this.hotReload = enable;
      }
   }

   /** @deprecated */
   @Deprecated
   public void setRefreshInterval(int sec) {
   }

   /** @deprecated */
   @Deprecated
   public int getRefreshInterval() {
      return this.hotReload ? 1 : 0;
   }

   protected UserIdentity loadUser(String username) {
      return null;
   }

   public void loadUsers() throws IOException {
   }

   protected String[] loadRoleInfo(MappedLoginService.KnownUser user) {
      UserIdentity id = this._propertyUserStore.getUserIdentity(user.getName());
      if (id == null) {
         return null;
      } else {
         Set<MappedLoginService.RolePrincipal> roles = id.getSubject().getPrincipals(MappedLoginService.RolePrincipal.class);
         if (roles == null) {
            return null;
         } else {
            List<String> list = new ArrayList();

            for(MappedLoginService.RolePrincipal r : roles) {
               list.add(r.getName());
            }

            return (String[])list.toArray(new String[roles.size()]);
         }
      }
   }

   protected MappedLoginService.KnownUser loadUserInfo(String userName) {
      UserIdentity id = this._propertyUserStore.getUserIdentity(userName);
      return id != null ? (MappedLoginService.KnownUser)id.getUserPrincipal() : null;
   }

   protected void doStart() throws Exception {
      super.doStart();
      if (this._propertyUserStore == null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("doStart: Starting new PropertyUserStore. PropertiesFile: " + this._config + " hotReload: " + this.hotReload);
         }

         this._propertyUserStore = new PropertyUserStore();
         this._propertyUserStore.setHotReload(this.hotReload);
         this._propertyUserStore.setConfigPath(this._config);
         this._propertyUserStore.registerUserListener(this);
         this._propertyUserStore.start();
      }

   }

   protected void doStop() throws Exception {
      super.doStop();
      if (this._propertyUserStore != null) {
         this._propertyUserStore.stop();
      }

      this._propertyUserStore = null;
   }

   public void update(String userName, Credential credential, String[] roleArray) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("update: " + userName + " Roles: " + roleArray.length);
      }

   }

   public void remove(String userName) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("remove: " + userName);
      }

      this.removeUser(userName);
   }

   public class HashKnownUser extends MappedLoginService.KnownUser {
      String[] _roles;

      public HashKnownUser(String name, Credential credential) {
         super(name, credential);
      }

      public void setRoles(String[] roles) {
         this._roles = roles;
      }

      public String[] getRoles() {
         return this._roles;
      }
   }
}
