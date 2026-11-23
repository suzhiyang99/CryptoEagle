package org.apache.wicket.util.diff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class ToString {
   public String toString() {
      StringBuilder s = new StringBuilder();
      this.toString(s);
      return s.toString();
   }

   public void toString(StringBuilder s) {
      s.append(super.toString());
   }

   public static String[] stringToArray(String value) {
      BufferedReader reader = new BufferedReader(new StringReader(value));
      List<String> l = new LinkedList();

      String s;
      try {
         while((s = reader.readLine()) != null) {
            l.add(s);
         }
      } catch (IOException var5) {
      }

      return (String[])l.toArray(new String[l.size()]);
   }

   public static String arrayToString(Object[] o) {
      return arrayToString(o, System.getProperty("line.separator"));
   }

   public static String arrayToString(Object[] o, String EOL) {
      StringBuilder buf = new StringBuilder();

      for(int i = 0; i < o.length - 1; ++i) {
         buf.append(o[i]);
         buf.append(EOL);
      }

      buf.append(o[o.length - 1]);
      return buf.toString();
   }
}
