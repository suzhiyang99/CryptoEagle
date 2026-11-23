package org.apache.openejb.config.sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.openejb.util.Join;

public class ListAdapter extends XmlAdapter<String, List<String>> {
   public List<String> unmarshal(String s) {
      String[] strings = s.split(", *");
      return new ArrayList(Arrays.asList(strings));
   }

   public String marshal(List<String> list) {
      return Join.join(", ", (Collection)list);
   }
}
