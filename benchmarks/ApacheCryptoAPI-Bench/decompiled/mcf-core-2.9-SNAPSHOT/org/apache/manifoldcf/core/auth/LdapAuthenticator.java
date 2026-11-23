package org.apache.manifoldcf.core.auth;

import java.util.Hashtable;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.manifoldcf.core.interfaces.IAuth;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.LockManagerFactory;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.Logging;

public class LdapAuthenticator implements IAuth {
   private static final String CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
   private static final String PROVIDER_URL_PROPERTY = "org.apache.manifoldcf.login.ldap.providerurl";
   private static final String SECURITY_AUTHENTICATION_TYPE = "org.apache.manifoldcf.login.ldap.securityauthenticationtype";
   private static final String SECURITY_PRINCIPLE = "org.apache.manifoldcf.login.ldap.securityprincipal";
   private static final String CONTEXT_SEARCH_QUERY = "org.apache.manifoldcf.login.ldap.contextsearchquery";
   private static final String SEARCH_ATTRIBUTE = "org.apache.manifoldcf.login.ldap.searchattribute";
   protected final String securityPrincipal;
   protected final String securityAuthenticationType;
   protected final String providerURLProperty;
   protected final String contextSearchQuery;
   protected final String searchAttribute;

   public LdapAuthenticator(IThreadContext threadContext) throws ManifoldCFException {
      this.securityPrincipal = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.login.ldap.securityprincipal", "");
      this.securityAuthenticationType = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.login.ldap.securityauthenticationtype", "simple");
      this.providerURLProperty = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.login.ldap.providerurl", "");
      this.contextSearchQuery = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.login.ldap.contextsearchquery", "");
      this.searchAttribute = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.login.ldap.searchattribute", "uid");
   }

   private Hashtable<String, String> buildEnvironment(String userID, String password) {
      Hashtable<String, String> environment = new Hashtable();
      environment.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
      environment.put("java.naming.provider.url", this.providerURLProperty);
      environment.put("java.naming.security.authentication", this.securityAuthenticationType);
      environment.put("java.naming.security.principal", substituteUser(this.securityPrincipal, userID));
      environment.put("java.naming.security.credentials", password);
      return environment;
   }

   private static String substituteUser(String source, String substitution) {
      return source.replace("$(userID)", substitution);
   }

   public boolean verifyUILogin(String userId, String password) throws ManifoldCFException {
      return this.verifyLogin(userId, password);
   }

   public boolean verifyAPILogin(String userId, String password) throws ManifoldCFException {
      return this.verifyLogin(userId, password);
   }

   protected boolean verifyLogin(String userId, String password) throws ManifoldCFException {
      boolean authenticated = false;
      if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(password)) {
         try {
            Logging.misc.info("Authentication attempt for user = " + userId);
            DirContext ctx = new InitialDirContext(this.buildEnvironment(userId, password));
            NamingEnumeration results = null;

            try {
               SearchControls controls = new SearchControls();
               controls.setSearchScope(2);
               results = ctx.search("", substituteUser(this.contextSearchQuery, userId), controls);

               while(results.hasMore()) {
                  SearchResult searchResult = (SearchResult)results.next();
                  if (userId.equals(searchResult.getAttributes().get(this.searchAttribute).get())) {
                     Logging.misc.info("Successfully authenticated : " + userId);
                     authenticated = true;
                     break;
                  }
               }
            } catch (Exception e) {
               Logging.misc.error("User not authenticated = " + userId + " exception = " + e.getMessage(), e);
               throw new ManifoldCFException("User not authenticated: " + e.getMessage(), e);
            } finally {
               if (results != null) {
                  try {
                     results.close();
                  } catch (Exception var18) {
                  }
               }

               if (ctx != null) {
                  try {
                     ctx.close();
                  } catch (Exception var17) {
                  }
               }

            }
         } catch (NamingException e) {
            Logging.misc.error("Exception authenticating user = " + userId + " exception = " + e.getMessage(), e);
            throw new ManifoldCFException("Exception authenticating user: " + e.getMessage(), e);
         }
      }

      return authenticated;
   }

   public boolean checkCapability(String userId, int capability) throws ManifoldCFException {
      return true;
   }
}
