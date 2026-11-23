package org.apache.manifoldcf.core.interfaces;

public class IndexDescription {
   public static final String _rcsid = "@(#)$Id: IndexDescription.java 988245 2010-08-23 18:39:35Z kwright $";
   protected boolean isUnique;
   protected String[] columnNames;

   public IndexDescription(boolean isUnique, String[] columnNames) {
      this.isUnique = isUnique;
      this.columnNames = columnNames;
   }

   public boolean getIsUnique() {
      return this.isUnique;
   }

   public String[] getColumnNames() {
      return this.columnNames;
   }

   public boolean equals(Object o) {
      if (!(o instanceof IndexDescription)) {
         return false;
      } else {
         IndexDescription id = (IndexDescription)o;
         if (id.isUnique == this.isUnique && id.columnNames.length == this.columnNames.length) {
            for(int i = 0; i < this.columnNames.length; ++i) {
               if (!this.columnNames[i].equals(id.columnNames[i])) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
