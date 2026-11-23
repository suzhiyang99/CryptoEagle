package org.apache.openejb.core.security;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.jacc.EJBMethodPermission;
import javax.security.jacc.PolicyConfigurationFactory;
import javax.servlet.http.HttpServletRequest;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.api.resource.DestroyableResource;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.ThreadContextListener;
import org.apache.openejb.core.security.jaas.GroupPrincipal;
import org.apache.openejb.core.security.jacc.BasicJaccProvider;
import org.apache.openejb.core.security.jacc.BasicPolicyConfiguration;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.CallerPrincipal;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.JavaSecurityManagers;

public abstract class AbstractSecurityService implements DestroyableResource, SecurityService<UUID>, ThreadContextListener, BasicPolicyConfiguration.RoleResolver {
   private static final Map<Object, Identity> identities = new ConcurrentHashMap();
   protected static final ThreadLocal<Identity> clientIdentity = new ThreadLocal();
   protected String defaultUser;
   private String realmName;
   protected Subject defaultSubject;
   protected SecurityContext defaultContext;

   public AbstractSecurityService() {
      this(autoJaccProvider());
   }

   public AbstractSecurityService(String jaccProvider) {
      this.defaultUser = "guest";
      this.realmName = "PropertiesLogin";
      JavaSecurityManagers.setSystemProperty(JaccProvider.class.getName(), jaccProvider);
      installJacc();
      ThreadContext.addThreadContextListener(this);
      this.updateSecurityContext();
      SystemInstance.get().setComponent(BasicPolicyConfiguration.RoleResolver.class, this);
   }

   public void destroyResource() {
   }

   public void onLogout(HttpServletRequest request) {
      clientIdentity.remove();
   }

   public String getRealmName() {
      return this.realmName;
   }

   public void setRealmName(String realmName) {
      this.realmName = realmName;
   }

   public String getDefaultUser() {
      return this.defaultUser;
   }

   public void setDefaultUser(String defaultUser) {
      this.defaultUser = defaultUser;
      this.updateSecurityContext();
   }

   private void updateSecurityContext() {
      this.defaultSubject = this.createSubject(this.defaultUser, this.defaultUser);
      this.defaultContext = new SecurityContext(this.defaultSubject);
   }

   public void init(Properties props) throws Exception {
   }

   public UUID login(String username, String password) throws LoginException {
      return (UUID)this.login(this.realmName, username, password);
   }

   public Set<String> getLogicalRoles(Principal[] principals, Set<String> logicalRoles) {
      LinkedHashSet<String> roles = new LinkedHashSet(principals.length);

      for(Principal principal : principals) {
         String name = principal.getName();
         if (logicalRoles.contains(name)) {
            roles.add(name);
         }
      }

      return roles;
   }

   public void contextEntered(ThreadContext oldContext, ThreadContext newContext) {
      String moduleID = newContext.getBeanContext().getModuleID();
      JavaSecurityManagers.setContextID(moduleID);
      ProvidedSecurityContext providedSecurityContext = (ProvidedSecurityContext)newContext.get(ProvidedSecurityContext.class);
      SecurityContext securityContext = oldContext != null ? (SecurityContext)oldContext.get(SecurityContext.class) : (providedSecurityContext != null ? providedSecurityContext.context : null);
      if (providedSecurityContext == null && (securityContext == null || securityContext == this.defaultContext)) {
         Identity identity = (Identity)clientIdentity.get();
         if (identity != null) {
            securityContext = new SecurityContext(identity.subject);
         } else {
            securityContext = this.defaultContext;
         }
      }

      newContext.set(SecurityContext.class, securityContext);
   }

   public UUID overrideWithRunAsContext(ThreadContext ctx, BeanContext newContext, BeanContext oldContext) {
      Subject runAsSubject = this.getRunAsSubject(newContext);
      if (oldContext != null && runAsSubject == null) {
         runAsSubject = this.getRunAsSubject(oldContext);
      }

      ctx.set(SecurityContext.class, new SecurityContext(runAsSubject));
      return this.disassociate();
   }

   public Subject getRunAsSubject(BeanContext callingBeanContext) {
      return callingBeanContext == null ? null : this.createRunAsSubject(callingBeanContext.getRunAsUser(), callingBeanContext.getRunAs());
   }

   protected Subject createRunAsSubject(String runAsUser, String runAsRole) {
      return this.createSubject(runAsUser, runAsRole);
   }

   public void contextExited(ThreadContext exitedContext, ThreadContext reenteredContext) {
      if (reenteredContext == null) {
         JavaSecurityManagers.setContextID((String)null);
      } else {
         JavaSecurityManagers.setContextID(reenteredContext.getBeanContext().getModuleID());
      }

   }

   protected UUID registerSubject(Subject subject) {
      Identity identity = new Identity(subject);
      UUID token = identity.getToken();
      identities.put(token, identity);
      return token;
   }

   public void logout(UUID securityIdentity) throws LoginException {
      Identity identity = (Identity)identities.get(securityIdentity);
      if (identity == null) {
         throw new LoginException("Identity is not currently logged in: " + securityIdentity);
      } else {
         identities.remove(securityIdentity);
      }
   }

   protected void unregisterSubject(Object securityIdentity) {
      identities.remove(securityIdentity);
   }

   public void associate(UUID securityIdentity) throws LoginException {
      Identity existing = (Identity)clientIdentity.get();
      if (existing != null && existing.getToken() != null) {
         throw new LoginException("Thread already associated with a client identity.  Refusing to overwrite. (current=" + existing.getToken() + "/" + existing.getSubject() + ", refused=" + securityIdentity + ")");
      } else if (securityIdentity == null) {
         throw new NullPointerException("The security token passed in is null");
      } else {
         Identity identity = (Identity)identities.get(securityIdentity);
         if (identity == null) {
            throw new LoginException("Identity is not currently logged in: " + securityIdentity);
         } else {
            clientIdentity.set(identity);
         }
      }
   }

   public UUID disassociate() {
      UUID var2;
      try {
         Identity identity = (Identity)clientIdentity.get();
         var2 = identity == null ? null : identity.getToken();
      } finally {
         clientIdentity.remove();
      }

      return var2;
   }

   public boolean isCallerInRole(String role) {
      if (role == null) {
         throw new IllegalArgumentException("Role must not be null");
      } else {
         ThreadContext threadContext = ThreadContext.getThreadContext();
         if (threadContext == null) {
            return false;
         } else {
            SecurityContext securityContext = (SecurityContext)threadContext.get(SecurityContext.class);
            if ("**".equals(role)) {
               return securityContext != this.defaultContext;
            } else {
               for(Group grp : securityContext.subject.getPrincipals(Group.class)) {
                  if (grp.getName().equals(role)) {
                     return true;
                  }
               }

               for(GroupPrincipal grp : securityContext.subject.getPrincipals(GroupPrincipal.class)) {
                  if (grp.getName().equals(role)) {
                     return true;
                  }
               }

               return false;
            }
         }
      }
   }

   public Principal getCallerPrincipal() {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      if (threadContext == null) {
         Identity id = (Identity)clientIdentity.get();
         return id != null ? this.getCallerPrincipal(id.getSubject().getPrincipals()) : null;
      } else {
         SecurityContext securityContext = (SecurityContext)threadContext.get(SecurityContext.class);
         Set<Principal> principals = securityContext.subject.getPrincipals();
         return this.getCallerPrincipal(principals);
      }
   }

   private Principal getCallerPrincipal(Set<Principal> principals) {
      if (!principals.isEmpty()) {
         for(Principal principal : principals) {
            if (principal.getClass().isAnnotationPresent(CallerPrincipal.class)) {
               return principal;
            }
         }

         return (Principal)principals.iterator().next();
      } else {
         return null;
      }
   }

   public boolean isCallerAuthorized(Method method, InterfaceType type) {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext beanContext = threadContext.getBeanContext();

      try {
         String ejbName = beanContext.getEjbName();
         String name = type == null ? null : type.getSpecName();
         if ("LocalBean".equals(name) || "LocalBeanHome".equals(name)) {
            name = null;
         }

         Identity currentIdentity = (Identity)clientIdentity.get();
         SecurityContext securityContext;
         if (currentIdentity == null) {
            securityContext = (SecurityContext)threadContext.get(SecurityContext.class);
         } else {
            securityContext = new SecurityContext(currentIdentity.getSubject());
         }

         securityContext.acc.checkPermission(new EJBMethodPermission(ejbName, name, method));
         return true;
      } catch (AccessControlException var9) {
         return false;
      }
   }

   protected static String autoJaccProvider() {
      return SystemInstance.isInitialized() ? SystemInstance.get().getProperty(JaccProvider.class.getName(), BasicJaccProvider.class.getName()) : BasicJaccProvider.class.getName();
   }

   protected static void installJacc() {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      String providerKey = "javax.security.jacc.PolicyConfigurationFactory.provider";

      try {
         if (JavaSecurityManagers.getSystemProperty("javax.security.jacc.PolicyConfigurationFactory.provider") == null) {
            JavaSecurityManagers.setSystemProperty("javax.security.jacc.PolicyConfigurationFactory.provider", JaccProvider.Factory.class.getName());
            ClassLoader cl = JaccProvider.Factory.class.getClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
         }

         PolicyConfigurationFactory.getPolicyConfigurationFactory();
      } catch (Exception e) {
         throw new IllegalStateException("Could not install JACC Policy Configuration Factory: " + JavaSecurityManagers.getSystemProperty("javax.security.jacc.PolicyConfigurationFactory.provider"), e);
      } finally {
         Thread.currentThread().setContextClassLoader(contextClassLoader);
      }

      String e = SystemInstance.get().getOptions().get("javax.security.jacc.policy.provider", JaccProvider.Policy.class.getName());

      try {
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         Class policyClass = Class.forName(e, true, classLoader);
         Policy policy = (Policy)policyClass.newInstance();
         policy.refresh();
         Policy.setPolicy(policy);
      } catch (Exception e) {
         throw new IllegalStateException("Could not install JACC Policy Provider: " + e, e);
      }
   }

   protected Subject createSubject(String name, String groupName) {
      if (name == null) {
         return null;
      } else {
         User user = new User(name);
         Group group = new Group(groupName);
         group.addMember(user);
         HashSet<Principal> principals = new HashSet();
         principals.add(user);
         principals.add(group);
         return new Subject(true, principals, new HashSet(), new HashSet());
      }
   }

   public Object currentState() {
      return clientIdentity.get();
   }

   public void setState(Object o) {
      if (Identity.class.isInstance(o)) {
         clientIdentity.set(Identity.class.cast(o));
      } else if (o == null) {
         clientIdentity.remove();
      }

   }

   public static final class ProvidedSecurityContext {
      public final SecurityContext context;

      public ProvidedSecurityContext(SecurityContext context) {
         this.context = context;
      }
   }

   public static final class SecurityContext {
      public final Subject subject;
      public final AccessControlContext acc;

      public SecurityContext(Subject subject) {
         this.subject = subject;
         this.acc = (AccessControlContext)Subject.doAsPrivileged(subject, new PrivilegedAction() {
            public Object run() {
               return AccessController.getContext();
            }
         }, (AccessControlContext)null);
      }
   }

   protected static class Identity implements Serializable {
      private final Subject subject;
      private final UUID token;

      public Identity(Subject subject) {
         this.subject = subject;
         this.token = UUID.randomUUID();
      }

      public Identity(Subject subject, UUID token) {
         this.subject = subject;
         this.token = token;
      }

      public Subject getSubject() {
         return this.subject;
      }

      public UUID getToken() {
         return this.token;
      }
   }

   public static class Group implements java.security.acl.Group {
      private final List<Principal> members = new ArrayList();
      private final String name;

      public Group(String name) {
         this.name = name;
      }

      public boolean addMember(Principal user) {
         return this.members.add(user);
      }

      public boolean removeMember(Principal user) {
         return this.members.remove(user);
      }

      public boolean isMember(Principal member) {
         return this.members.contains(member);
      }

      public Enumeration<? extends Principal> members() {
         return Collections.enumeration(this.members);
      }

      public String getName() {
         return this.name;
      }
   }

   @CallerPrincipal
   public static class User implements Principal {
      private final String name;

      public User(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            boolean var10000;
            label35: {
               User user = (User)User.class.cast(o);
               if (this.name != null) {
                  if (this.name.equals(user.name)) {
                     break label35;
                  }
               } else if (user.name == null) {
                  break label35;
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.name != null ? this.name.hashCode() : 0;
      }
   }
}
