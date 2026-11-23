package org.apache.manifoldcf.core.database;

import java.util.ArrayList;
import org.apache.manifoldcf.core.interfaces.IResultRow;
import org.apache.manifoldcf.core.interfaces.IResultSet;

public class MergedResultSet implements IResultSet {
   public static final String _rcsid = "@(#)$Id: MergedResultSet.java 988245 2010-08-23 18:39:35Z kwright $";
   protected ArrayList resultSets = new ArrayList();
   protected int totalRowCount = 0;

   public void addResultSet(IResultSet set) {
      this.resultSets.add(set);
      this.totalRowCount += set.getRowCount();
   }

   public IResultRow getRow(int rowNumber) {
      IResultSet set;
      for(int j = 0; j < this.resultSets.size(); rowNumber -= set.getRowCount()) {
         set = (IResultSet)this.resultSets.get(j++);
         if (set.getRowCount() > rowNumber) {
            return set.getRow(rowNumber);
         }
      }

      return null;
   }

   public int getRowCount() {
      return this.totalRowCount;
   }

   public IResultRow[] getRows() {
      IResultRow[] rval = new IResultRow[this.totalRowCount];
      int i = 0;
      int j = 0;

      while(j < this.resultSets.size()) {
         IResultSet set = (IResultSet)this.resultSets.get(j++);

         for(int k = 0; k < set.getRowCount(); rval[i++] = set.getRow(k++)) {
         }
      }

      return rval;
   }
}
