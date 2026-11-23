package org.apache.manifoldcf.core.interfaces;

import java.util.ArrayList;
import java.util.List;

public class UnitaryClause implements ClauseDescription {
   public static final String _rcsid = "@(#)$Id$";
   protected String columnName;
   protected String operationName;
   protected List values;

   public UnitaryClause(String columnName, String operationName, Object value) {
      this.columnName = columnName;
      this.operationName = operationName;
      this.values = new ArrayList();
      this.values.add(value);
   }

   public UnitaryClause(String columnName, Object value) {
      this(columnName, "=", value);
   }

   public String getColumnName() {
      return this.columnName;
   }

   public String getOperation() {
      return this.operationName;
   }

   public List getValues() {
      return this.values;
   }

   public String getJoinColumnName() {
      return null;
   }
}
