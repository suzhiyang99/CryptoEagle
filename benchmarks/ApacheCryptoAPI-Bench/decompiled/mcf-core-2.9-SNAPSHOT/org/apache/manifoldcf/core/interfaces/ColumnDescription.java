package org.apache.manifoldcf.core.interfaces;

public class ColumnDescription {
   public static final String _rcsid = "@(#)$Id: ColumnDescription.java 988245 2010-08-23 18:39:35Z kwright $";
   protected String typeString;
   protected boolean isPrimaryKey;
   protected boolean isNull;
   protected String referenceTable;
   protected String referenceColumn;
   protected boolean referenceCascade;

   public ColumnDescription(String typeString, boolean isPrimaryKey, boolean isNull, String referenceTable, String referenceColumn, boolean referenceCascade) {
      this.typeString = typeString;
      this.isPrimaryKey = isPrimaryKey;
      this.isNull = isNull;
      this.referenceTable = referenceTable;
      this.referenceColumn = referenceColumn;
      this.referenceCascade = referenceCascade;
   }

   public String getTypeString() {
      return this.typeString;
   }

   public boolean getIsPrimaryKey() {
      return this.isPrimaryKey;
   }

   public boolean getIsNull() {
      return this.isNull;
   }

   public String getReferenceTable() {
      return this.referenceTable;
   }

   public String getReferenceColumn() {
      return this.referenceColumn;
   }

   public boolean getReferenceCascade() {
      return this.referenceCascade;
   }
}
