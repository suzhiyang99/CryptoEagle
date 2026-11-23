package org.apache.openejb.config.sys;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.openejb.loader.IO;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.SuperProperties;

public class PropertiesAdapter extends XmlAdapter<String, Properties> {
   public Properties unmarshal(String s) throws Exception {
      return IO.readProperties(IO.read(s), new SuperProperties());
   }

   public String marshal(Properties properties) throws Exception {
      if (properties == null) {
         return null;
      } else {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         properties.store(out, (String)null);
         String string = new String(out.toByteArray());
         if (!(properties instanceof SuperProperties)) {
            string = string.replaceFirst("#.*?" + JavaSecurityManagers.getSystemProperty("line.separator"), "");
         }

         return string;
      }
   }
}
