package org.apache.manifoldcf.core.interfaces;

import java.util.List;

public class NullCheckClause implements ClauseDescription {
   public static final String _rcsid = "@(#)$Id$";
   protected String columnName;
   protected boolean isNull;

   public NullCheckClause(String columnName, boolean isNull) {
      this.columnName = columnName;
      this.isNull = isNull;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public String getOperation() {
      return this.isNull ? " IS NULL" : " IS NOT NULL";
   }

   public List getValues() {
      return null;
   }

   public String getJoinColumnName() {
      return null;
   }
}
