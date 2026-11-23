package org.spark_project.jetty.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.spark_project.jetty.io.ClientConnectionFactory;
import org.spark_project.jetty.util.HostPort;

public class ProxyConfiguration {
   private final List<Proxy> proxies = new ArrayList();

   public List<Proxy> getProxies() {
      return this.proxies;
   }

   public Proxy match(Origin origin) {
      for(Proxy proxy : this.getProxies()) {
         if (proxy.matches(origin)) {
            return proxy;
         }
      }

      return null;
   }

   public abstract static class Proxy {
      private final Set<String> included = new HashSet();
      private final Set<String> excluded = new HashSet();
      private final Origin.Address address;
      private final boolean secure;

      protected Proxy(Origin.Address address, boolean secure) {
         this.address = address;
         this.secure = secure;
      }

      public Origin.Address getAddress() {
         return this.address;
      }

      public boolean isSecure() {
         return this.secure;
      }

      public Set<String> getIncludedAddresses() {
         return this.included;
      }

      public Set<String> getExcludedAddresses() {
         return this.excluded;
      }

      public URI getURI() {
         return null;
      }

      public boolean matches(Origin origin) {
         if (this.getAddress().equals(origin.getAddress())) {
            return false;
         } else {
            boolean result = this.included.isEmpty();
            Origin.Address address = origin.getAddress();

            for(String included : this.included) {
               if (this.matches(address, included)) {
                  result = true;
                  break;
               }
            }

            for(String excluded : this.excluded) {
               if (this.matches(address, excluded)) {
                  result = false;
                  break;
               }
            }

            return result;
         }
      }

      private boolean matches(Origin.Address address, String pattern) {
         HostPort hostPort = new HostPort(pattern);
         String host = hostPort.getHost();
         int port = hostPort.getPort();
         return host.equals(address.getHost()) && (port <= 0 || port == address.getPort());
      }

      public abstract ClientConnectionFactory newClientConnectionFactory(ClientConnectionFactory var1);

      public String toString() {
         return this.address.toString();
      }
   }
}
