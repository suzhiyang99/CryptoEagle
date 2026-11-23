package org.apache.openejb.table;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.openejb.util.JavaSecurityManagers;

public class Lines {
   private final List<Line> lines;
   private final String cr;

   public Lines() {
      this(JavaSecurityManagers.getSystemProperty("line.separator"));
   }

   public Lines(String cr) {
      this.lines = new ArrayList();
      this.cr = cr;
   }

   public void add(Line line) {
      if (!this.lines.isEmpty() && ((Line)this.lines.iterator().next()).getColumns().length != line.getColumns().length) {
         throw new IllegalArgumentException("columns should have all the same size");
      } else {
         line.setCr(this.cr);
         this.lines.add(line);
      }
   }

   public void print(PrintStream out) {
      this.print(out, true);
   }

   public void print(PrintStream out, boolean headers) {
      Iterator<Line> it = this.lines.iterator();
      if (it.hasNext()) {
         int[] max = max(this.lines);
         ((Line)it.next()).print(max, out, headers);

         while(it.hasNext()) {
            ((Line)it.next()).print(max, out);
         }

      }
   }

   private static int[] max(List<Line> lines) {
      int[] max = new int[((Line)lines.iterator().next()).getColumns().length];

      for(Line line : lines) {
         for(int i = 0; i < max.length; ++i) {
            int ll = line.getColumns()[i].length();
            if (max[i] == 0) {
               max[i] = ll;
            } else if (max[i] < ll) {
               max[i] = ll;
            }
         }
      }

      return max;
   }

   public List<Line> getLines() {
      return this.lines;
   }
}
