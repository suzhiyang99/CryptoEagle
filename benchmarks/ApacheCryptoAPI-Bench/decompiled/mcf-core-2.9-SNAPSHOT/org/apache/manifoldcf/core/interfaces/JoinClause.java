package org.apache.manifoldcf.core.interfaces;

import java.util.List;

public class JoinClause implements ClauseDescription {
   public static final String _rcsid = "@(#)$Id$";
   protected final String columnName;
   protected final String joinColumnName;
   protected final String operation;

   public JoinClause(String columnName, String joinColumnName) {
      this(columnName, joinColumnName, "=");
   }

   public JoinClause(String columnName, String joinColumnName, String operation) {
      this.columnName = columnName;
      this.joinColumnName = joinColumnName;
      this.operation = operation;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public String getOperation() {
      return this.operation;
   }

   public List getValues() {
      return null;
   }

   public String getJoinColumnName() {
      return this.joinColumnName;
   }
}
