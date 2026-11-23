package org.spark_project.jetty.security.authentication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import org.spark_project.jetty.security.AbstractUserAuthentication;
import org.spark_project.jetty.security.LoginService;
import org.spark_project.jetty.security.SecurityHandler;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class SessionAuthentication extends AbstractUserAuthentication implements Serializable, HttpSessionActivationListener, HttpSessionBindingListener {
   private static final Logger LOG = Log.getLogger(SessionAuthentication.class);
   private static final long serialVersionUID = -4643200685888258706L;
   public static final String __J_AUTHENTICATED = "org.spark_project.jetty.security.UserIdentity";
   private final String _name;
   private final Object _credentials;
   private transient HttpSession _session;

   public SessionAuthentication(String method, UserIdentity userIdentity, Object credentials) {
      super(method, userIdentity);
      this._name = userIdentity.getUserPrincipal().getName();
      this._credentials = credentials;
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      SecurityHandler security = SecurityHandler.getCurrentSecurityHandler();
      if (security == null) {
         throw new IllegalStateException("!SecurityHandler");
      } else {
         LoginService login_service = security.getLoginService();
         if (login_service == null) {
            throw new IllegalStateException("!LoginService");
         } else {
            this._userIdentity = login_service.login(this._name, this._credentials, (ServletRequest)null);
            LOG.debug("Deserialized and relogged in {}", this);
         }
      }
   }

   public void logout() {
      if (this._session != null && this._session.getAttribute("org.spark_project.jetty.security.UserIdentity") != null) {
         this._session.removeAttribute("org.spark_project.jetty.security.UserIdentity");
      }

      this.doLogout();
   }

   private void doLogout() {
      SecurityHandler security = SecurityHandler.getCurrentSecurityHandler();
      if (security != null) {
         security.logout(this);
      }

      if (this._session != null) {
         this._session.removeAttribute("org.spark_project.jetty.security.sessionCreatedSecure");
      }

   }

   public String toString() {
      return String.format("%s@%x{%s,%s}", this.getClass().getSimpleName(), this.hashCode(), this._session == null ? "-" : this._session.getId(), this._userIdentity);
   }

   public void sessionWillPassivate(HttpSessionEvent se) {
   }

   public void sessionDidActivate(HttpSessionEvent se) {
      if (this._session == null) {
         this._session = se.getSession();
      }

   }

   public void valueBound(HttpSessionBindingEvent event) {
      if (this._session == null) {
         this._session = event.getSession();
      }

   }

   public void valueUnbound(HttpSessionBindingEvent event) {
      this.doLogout();
   }
}
