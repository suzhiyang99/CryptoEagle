package org.apache.meecrowave.api;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;

public class StartListening extends ListeningBase {
   public StartListening(Connector connector, Host host, Context context) {
      super(connector, host, context);
   }
}
