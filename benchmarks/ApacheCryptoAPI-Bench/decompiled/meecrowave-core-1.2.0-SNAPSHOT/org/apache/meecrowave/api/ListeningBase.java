package org.apache.meecrowave.api;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;

public abstract class ListeningBase {
   private final Connector connector;
   private final Context context;
   private final Host host;

   ListeningBase(Connector connector, Host host, Context context) {
      this.connector = connector;
      this.host = host;
      this.context = context;
   }

   public Context getContext() {
      return this.context;
   }

   public Connector getConnector() {
      return this.connector;
   }

   public Host getHost() {
      return this.host;
   }

   public String getFirstBase() {
      return (this.connector.getSecure() ? "https" : "http") + "://" + this.host.getName() + (this.connector.getPort() > 0 ? ":" + this.connector.getPort() : "") + this.context.getPath();
   }
}
