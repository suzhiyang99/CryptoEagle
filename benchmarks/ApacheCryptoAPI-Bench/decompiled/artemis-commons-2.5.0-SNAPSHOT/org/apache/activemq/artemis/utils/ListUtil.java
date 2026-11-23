package org.apache.activemq.artemis.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
   public static List<String> toList(String commaSeparatedString) {
      List<String> list = new ArrayList();
      if (commaSeparatedString != null && commaSeparatedString.trim().length() != 0) {
         String[] values = commaSeparatedString.split(",");

         for(String value : values) {
            list.add(value.trim());
         }

         return list;
      } else {
         return list;
      }
   }
}
