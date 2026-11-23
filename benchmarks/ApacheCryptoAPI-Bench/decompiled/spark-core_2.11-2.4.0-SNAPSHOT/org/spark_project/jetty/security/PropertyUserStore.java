package org.spark_project.jetty.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.security.auth.Subject;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.PathWatcher;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.resource.PathResource;
import org.spark_project.jetty.util.resource.Resource;
import org.spark_project.jetty.util.security.Credential;

public class PropertyUserStore extends AbstractLifeCycle implements PathWatcher.Listener {
   private static final Logger LOG = Log.getLogger(PropertyUserStore.class);
   private Path _configPath;
   private Resource _configResource;
   private PathWatcher pathWatcher;
   private boolean hotReload = false;
   private IdentityService _identityService = new DefaultIdentityService();
   private boolean _firstLoad = true;
   private final List<String> _knownUsers = new ArrayList();
   private final Map<String, UserIdentity> _knownUserIdentities = new HashMap();
   private List<UserListener> _listeners;

   /** @deprecated */
   @Deprecated
   public String getConfig() {
      return this._configPath != null ? this._configPath.toString() : null;
   }

   public void setConfig(String config) {
      try {
         Resource configResource = Resource.newResource(config);
         if (configResource.getFile() != null) {
            this.setConfigPath(configResource.getFile());
         } else {
            throw new IllegalArgumentException(config + " is not a file");
         }
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   public Path getConfigPath() {
      return this._configPath;
   }

   public void setConfigPath(String configFile) {
      if (configFile == null) {
         this._configPath = null;
      } else {
         this._configPath = (new File(configFile)).toPath();
      }

   }

   public void setConfigPath(File configFile) {
      if (configFile == null) {
         this._configPath = null;
      } else {
         this._configPath = configFile.toPath();
      }
   }

   public void setConfigPath(Path configPath) {
      this._configPath = configPath;
   }

   public UserIdentity getUserIdentity(String userName) {
      return (UserIdentity)this._knownUserIdentities.get(userName);
   }

   public Resource getConfigResource() throws IOException {
      if (this._configResource == null) {
         this._configResource = new PathResource(this._configPath);
      }

      return this._configResource;
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

   public String toString() {
      StringBuilder s = new StringBuilder();
      s.append(this.getClass().getName());
      s.append("[");
      s.append("users.count=").append(this._knownUsers.size());
      s.append("identityService=").append(this._identityService);
      s.append("]");
      return s.toString();
   }

   private void loadUsers() throws IOException {
      if (this._configPath != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Loading " + this + " from " + this._configPath);
         }

         Properties properties = new Properties();
         if (this.getConfigResource().exists()) {
            properties.load(this.getConfigResource().getInputStream());
         }

         Set<String> known = new HashSet();

         for(Map.Entry<Object, Object> entry : properties.entrySet()) {
            String username = ((String)entry.getKey()).trim();
            String credentials = ((String)entry.getValue()).trim();
            String roles = null;
            int c = credentials.indexOf(44);
            if (c > 0) {
               roles = credentials.substring(c + 1).trim();
               credentials = credentials.substring(0, c).trim();
            }

            if (username != null && username.length() > 0 && credentials != null && credentials.length() > 0) {
               String[] roleArray = IdentityService.NO_ROLES;
               if (roles != null && roles.length() > 0) {
                  roleArray = StringUtil.csvSplit(roles);
               }

               known.add(username);
               Credential credential = Credential.getCredential(credentials);
               Principal userPrincipal = new MappedLoginService.KnownUser(username, credential);
               Subject subject = new Subject();
               subject.getPrincipals().add(userPrincipal);
               subject.getPrivateCredentials().add(credential);
               if (roles != null) {
                  for(String role : roleArray) {
                     subject.getPrincipals().add(new MappedLoginService.RolePrincipal(role));
                  }
               }

               subject.setReadOnly();
               this._knownUserIdentities.put(username, this._identityService.newUserIdentity(subject, userPrincipal, roleArray));
               this.notifyUpdate(username, credential, roleArray);
            }
         }

         synchronized(this._knownUsers) {
            if (!this._firstLoad) {
               for(String user : this._knownUsers) {
                  if (!known.contains(user)) {
                     this._knownUserIdentities.remove(user);
                     this.notifyRemove(user);
                  }
               }
            }

            this._knownUsers.clear();
            this._knownUsers.addAll(known);
         }

         this._firstLoad = false;
         if (LOG.isDebugEnabled()) {
            LOG.debug("Loaded " + this + " from " + this._configPath);
         }

      }
   }

   protected void doStart() throws Exception {
      super.doStart();
      this.loadUsers();
      if (this.isHotReload() && this._configPath != null) {
         this.pathWatcher = new PathWatcher();
         this.pathWatcher.watch(this._configPath);
         this.pathWatcher.addListener(this);
         this.pathWatcher.setNotifyExistingOnStart(false);
         this.pathWatcher.start();
      }

   }

   public void onPathWatchEvent(PathWatcher.PathWatchEvent event) {
      try {
         this.loadUsers();
      } catch (IOException e) {
         LOG.warn(e);
      }

   }

   protected void doStop() throws Exception {
      super.doStop();
      if (this.pathWatcher != null) {
         this.pathWatcher.stop();
      }

      this.pathWatcher = null;
   }

   private void notifyUpdate(String username, Credential credential, String[] roleArray) {
      if (this._listeners != null) {
         Iterator<UserListener> i = this._listeners.iterator();

         while(i.hasNext()) {
            ((UserListener)i.next()).update(username, credential, roleArray);
         }
      }

   }

   private void notifyRemove(String username) {
      if (this._listeners != null) {
         Iterator<UserListener> i = this._listeners.iterator();

         while(i.hasNext()) {
            ((UserListener)i.next()).remove(username);
         }
      }

   }

   public void registerUserListener(UserListener listener) {
      if (this._listeners == null) {
         this._listeners = new ArrayList();
      }

      this._listeners.add(listener);
   }

   public interface UserListener {
      void update(String var1, Credential var2, String[] var3);

      void remove(String var1);
   }
}
