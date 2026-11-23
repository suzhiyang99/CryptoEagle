package org.apache.openejb.resource.openshift;

import java.util.Properties;
import org.apache.openejb.api.resource.PropertiesResourceProvider;
import org.apache.openejb.testng.PropertiesBuilder;

public class OpenshiftMySQLPropertiesProvider implements PropertiesResourceProvider {
   public Properties provides() {
      return (new PropertiesBuilder()).p("JdbcDriver", "com.mysql.jdbc.Driver").p("JdbcUrl", String.format("jdbc:mysql://%s:%s/%s?tcpKeepAlive=true", System.getenv("OPENSHIFT_MYSQL_DB_HOST"), System.getenv("OPENSHIFT_MYSQL_DB_PORT"), System.getenv("OPENSHIFT_APP_NAME"))).p("UserName", System.getenv("OPENSHIFT_MYSQL_DB_USERNAME")).p("Password", System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD")).p("ValidationQuery", "SELECT 1").build();
   }
}
