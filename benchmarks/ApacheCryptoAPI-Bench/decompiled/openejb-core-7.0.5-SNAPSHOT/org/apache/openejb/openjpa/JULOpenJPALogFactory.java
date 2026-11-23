package org.apache.openejb.openjpa;

import org.apache.openejb.log.LoggerCreator;
import org.apache.openjpa.lib.log.Log;
import org.apache.openjpa.lib.log.LogFactoryAdapter;

public class JULOpenJPALogFactory extends LogFactoryAdapter {
   protected Log newLogAdapter(String channel) {
      return new JULOpenJPALog(new LoggerCreator(channel));
   }
}
