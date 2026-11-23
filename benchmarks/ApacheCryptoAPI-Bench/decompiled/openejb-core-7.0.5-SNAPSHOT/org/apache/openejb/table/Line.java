package org.apache.openejb.table;

import java.io.PrintStream;
import org.apache.openejb.util.JavaSecurityManagers;

public class Line {
   public static final String COL_SEP = "|";
   public static final String HEADER_CHAR = "=";
   public static final String LINE_CHAR = "-";
   public static final char EMPTY_CHAR = ' ';
   private final String[] columns;
   private String cr = JavaSecurityManagers.getSystemProperty("line.separator");

   public Line(String... columns) {
      this.columns = columns;
   }

   public String[] getColumns() {
      return this.columns;
   }

   public void print(int[] max, PrintStream out) {
      this.print(max, out, false);
   }

   public void print(int[] max, PrintStream out, boolean header) {
      StringBuilder sb = new StringBuilder("");

      for(int i = 0; i < max.length; ++i) {
         sb.append(' ');
         int spaces = max[i] - this.columns[i].length();

         for(int k = 0; k < spaces / 2; ++k) {
            sb.append(' ');
         }

         sb.append(this.columns[i]);

         for(int k = 0; k < spaces - spaces / 2; ++k) {
            sb.append(' ');
         }

         sb.append(' ').append("|");
      }

      String lineStr = sb.toString();
      StringBuilder sep = new StringBuilder("");
      String s;
      if (header) {
         s = "=";
      } else {
         s = "-";
      }

      for(int i = 0; i < lineStr.length(); ++i) {
         sep.append(s);
      }

      if (header) {
         this.printLine(out, sep.toString());
      }

      this.printLine(out, lineStr);
      this.printLine(out, sep.toString());
   }

   private void printLine(PrintStream out, String s) {
      out.print(s + this.cr);
   }

   public void setCr(String cr) {
      this.cr = cr;
   }
}
