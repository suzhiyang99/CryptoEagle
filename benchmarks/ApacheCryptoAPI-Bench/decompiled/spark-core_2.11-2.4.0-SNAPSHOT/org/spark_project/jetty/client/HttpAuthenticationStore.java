package org.spark_project.jetty.client;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.spark_project.jetty.client.api.Authentication;
import org.spark_project.jetty.client.api.AuthenticationStore;
import org.spark_project.jetty.client.util.AbstractAuthentication;

public class HttpAuthenticationStore implements AuthenticationStore {
   private final List<Authentication> authentications = new CopyOnWriteArrayList();
   private final Map<URI, Authentication.Result> results = new ConcurrentHashMap();

   public void addAuthentication(Authentication authentication) {
      this.authentications.add(authentication);
   }

   public void removeAuthentication(Authentication authentication) {
      this.authentications.remove(authentication);
   }

   public void clearAuthentications() {
      this.authentications.clear();
   }

   public Authentication findAuthentication(String type, URI uri, String realm) {
      for(Authentication authentication : this.authentications) {
         if (authentication.matches(type, uri, realm)) {
            return authentication;
         }
      }

      return null;
   }

   public void addAuthenticationResult(Authentication.Result result) {
      this.results.put(result.getURI(), result);
   }

   public void removeAuthenticationResult(Authentication.Result result) {
      this.results.remove(result.getURI());
   }

   public void clearAuthenticationResults() {
      this.results.clear();
   }

   public Authentication.Result findAuthenticationResult(URI uri) {
      for(Map.Entry<URI, Authentication.Result> entry : this.results.entrySet()) {
         if (AbstractAuthentication.matchesURI((URI)entry.getKey(), uri)) {
            return (Authentication.Result)entry.getValue();
         }
      }

      return null;
   }
}
