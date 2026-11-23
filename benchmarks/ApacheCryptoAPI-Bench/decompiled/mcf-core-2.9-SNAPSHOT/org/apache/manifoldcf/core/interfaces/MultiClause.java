package org.apache.manifoldcf.core.interfaces;

import java.util.ArrayList;
import java.util.List;

public class MultiClause implements ClauseDescription {
   public static final String _rcsid = "@(#)$Id$";
   protected String columnName;
   protected List values;

   public MultiClause(String columnName, List values) {
      this.columnName = columnName;
      this.values = values;
   }

   public MultiClause(String columnName, Object[] values) {
      this.columnName = columnName;
      this.values = new ArrayList();

      for(int i = 0; i < values.length; ++i) {
         this.values.add(values[i]);
      }

   }

   public String getColumnName() {
      return this.columnName;
   }

   public String getOperation() {
      return "=";
   }

   public List getValues() {
      return this.values;
   }

   public String getJoinColumnName() {
      return null;
   }
}
