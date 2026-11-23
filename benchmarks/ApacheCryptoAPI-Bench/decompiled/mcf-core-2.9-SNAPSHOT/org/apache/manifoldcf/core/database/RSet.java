package org.apache.manifoldcf.core.database;

import java.util.ArrayList;
import java.util.List;
import org.apache.manifoldcf.core.interfaces.IResultRow;
import org.apache.manifoldcf.core.interfaces.IResultSet;

public class RSet implements IResultSet {
   public static final String _rcsid = "@(#)$Id: RSet.java 988245 2010-08-23 18:39:35Z kwright $";
   protected List<IResultRow> rows = new ArrayList();

   public void addRow(IResultRow m) {
      this.rows.add(m);
   }

   public IResultRow getRow(int rowNumber) {
      return (IResultRow)this.rows.get(rowNumber);
   }

   public int getRowCount() {
      return this.rows.size();
   }

   public IResultRow[] getRows() {
      IResultRow[] rval = new IResultRow[this.rows.size()];

      for(int i = 0; i < this.rows.size(); ++i) {
         rval[i] = (IResultRow)this.rows.get(i);
      }

      return rval;
   }
}
