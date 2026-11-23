package org.apache.manifoldcf.core.auth;

import org.apache.manifoldcf.core.interfaces.IAuth;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.LockManagerFactory;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class DefaultAuthenticator implements IAuth {
   public static final String loginUserNameProperty = "org.apache.manifoldcf.login.name";
   public static final String loginPasswordProperty = "org.apache.manifoldcf.login.password";
   public static final String apiLoginUserNameProperty = "org.apache.manifoldcf.apilogin.name";
   public static final String apiLoginPasswordProperty = "org.apache.manifoldcf.apilogin.password";
   protected final String loginUserName;
   protected final String loginPassword;
   protected final String apiLoginUserName;
   protected final String apiLoginPassword;

   public DefaultAuthenticator(IThreadContext threadContext) throws ManifoldCFException {
      this.loginUserName = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.login.name", "admin");
      this.loginPassword = LockManagerFactory.getPossiblyObfuscatedStringProperty(threadContext, "org.apache.manifoldcf.login.password", "admin");
      this.apiLoginUserName = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.apilogin.name", "");
      this.apiLoginPassword = LockManagerFactory.getPossiblyObfuscatedStringProperty(threadContext, "org.apache.manifoldcf.apilogin.password", "");
   }

   public boolean verifyUILogin(String userId, String password) throws ManifoldCFException {
      if (userId != null && password != null) {
         return userId.equals(this.loginUserName) && password.equals(this.loginPassword);
      } else {
         return false;
      }
   }

   public boolean verifyAPILogin(String userId, String password) throws ManifoldCFException {
      if (userId != null && password != null) {
         return userId.equals(this.apiLoginUserName) && password.equals(this.apiLoginPassword);
      } else {
         return false;
      }
   }

   public boolean checkCapability(String userId, int capability) throws ManifoldCFException {
      return true;
   }
}
