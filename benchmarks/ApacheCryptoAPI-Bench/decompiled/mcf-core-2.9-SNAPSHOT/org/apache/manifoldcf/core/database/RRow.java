package org.apache.manifoldcf.core.database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.manifoldcf.core.interfaces.IResultRow;

public class RRow implements IResultRow {
   public static final String _rcsid = "@(#)$Id: RRow.java 988245 2010-08-23 18:39:35Z kwright $";
   protected Map<String, Object> rowData = new HashMap();

   public void put(String name, Object value) {
      this.rowData.put(name, value);
   }

   public int getColumnCount() {
      return this.rowData.size();
   }

   public Iterator<String> getColumns() {
      return this.rowData.keySet().iterator();
   }

   public Object getValue(String columnName) {
      return this.rowData.get(columnName);
   }
}
