package org.apache.openejb.config.typed.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.openejb.util.Duration;

public class DurationAdapter extends XmlAdapter<String, Duration> {
   public Duration unmarshal(String v) throws Exception {
      return Duration.parse(v);
   }

   public String marshal(Duration v) throws Exception {
      return v.toString();
   }
}
