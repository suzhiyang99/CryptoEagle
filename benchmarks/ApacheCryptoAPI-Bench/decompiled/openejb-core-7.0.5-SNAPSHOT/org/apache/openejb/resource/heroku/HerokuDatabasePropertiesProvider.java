package org.apache.openejb.resource.heroku;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.api.resource.PropertiesResourceProvider;
import org.apache.openejb.testng.PropertiesBuilder;
import org.apache.openejb.util.PropertyPlaceHolderHelper;

public class HerokuDatabasePropertiesProvider implements PropertiesResourceProvider {
   private final Map<String, String> jdbcMapping = new HashMap<String, String>() {
      {
         this.put("postgres", "postgresql");
         this.put("hsql", "hsqldb:hsql");
      }
   };
   private Properties properties;

   public Properties provides() {
      try {
         URI url = new URI(PropertyPlaceHolderHelper.simpleValue("${DATABASE_URL}"));
         String userInfo = url.getUserInfo();
         String jdbcUrl = "jdbc:" + (this.jdbcMapping.containsKey(url.getScheme()) ? (String)this.jdbcMapping.get(url.getScheme()) : url.getScheme()) + "://" + url.getHost() + (url.getPort() > 0 ? ":" + url.getPort() : "") + url.getPath();
         PropertiesBuilder builder = (new PropertiesBuilder()).p("JdbcUrl", jdbcUrl);
         if (userInfo != null) {
            int sep = userInfo.indexOf(58);
            if (sep > 0) {
               builder.p("UserName", userInfo.substring(0, sep)).p("Password", userInfo.substring(sep + 1, userInfo.length()));
            } else {
               builder.p("UserName", userInfo);
            }
         }

         if ((this.properties == null || "org.hsqldb.jdbcDriver".equals(this.properties.getProperty("JdbcDriver"))) && "postgres".equalsIgnoreCase(url.getScheme())) {
            builder.p("JdbcDriver", "org.postgresql.Driver");
         }

         return builder.build();
      } catch (URISyntaxException e) {
         throw new IllegalArgumentException(e);
      }
   }
}
