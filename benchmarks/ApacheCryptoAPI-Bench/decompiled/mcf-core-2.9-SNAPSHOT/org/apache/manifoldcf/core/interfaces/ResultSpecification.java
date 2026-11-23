package org.apache.manifoldcf.core.interfaces;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class ResultSpecification {
   public static final int FORM_DEFAULT = 0;
   public static final int FORM_STRING = 1;
   public static final int FORM_STREAM = 2;
   protected HashMap columnSpecifications = new HashMap();

   public int hashCode() {
      String[] keys = new String[this.columnSpecifications.size()];
      Iterator iter = this.columnSpecifications.keySet().iterator();

      for(int i = 0; iter.hasNext(); keys[i++] = (String)iter.next()) {
      }

      Arrays.sort(keys);
      int rval = 0;

      ColumnSpecification cs;
      for(int var7 = 0; var7 < keys.length; rval += cs.hashCode()) {
         String key = keys[var7++];
         rval += key.hashCode();
         cs = (ColumnSpecification)this.columnSpecifications.get(key);
      }

      return rval;
   }

   public boolean equals(Object o) {
      if (!(o instanceof ResultSpecification)) {
         return false;
      } else {
         ResultSpecification other = (ResultSpecification)o;
         if (other.columnSpecifications.size() != this.columnSpecifications.size()) {
            return false;
         } else {
            for(String key : this.columnSpecifications.keySet()) {
               ColumnSpecification thisSpec = (ColumnSpecification)this.columnSpecifications.get(key);
               ColumnSpecification otherSpec = (ColumnSpecification)other.columnSpecifications.get(key);
               if (otherSpec == null) {
                  return false;
               }

               if (!thisSpec.equals(otherSpec)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      String[] keys = new String[this.columnSpecifications.size()];
      Iterator iter = this.columnSpecifications.keySet().iterator();

      for(int i = 0; iter.hasNext(); keys[i++] = (String)iter.next()) {
      }

      Arrays.sort(keys);
      sb.append(Integer.toString(keys.length));
      int var7 = 0;

      while(var7 < keys.length) {
         String key = keys[var7++];
         sb.append("-").append(key);
         ColumnSpecification cs = (ColumnSpecification)this.columnSpecifications.get(key);
         sb.append("-").append(cs.toString());
      }

      return sb.toString();
   }

   public void setForm(String columnName, int formValue) {
      ColumnSpecification cs = (ColumnSpecification)this.columnSpecifications.get(columnName);
      if (cs == null) {
         if (formValue == 0) {
            return;
         }

         cs = new ColumnSpecification();
         this.columnSpecifications.put(columnName, cs);
      } else if (formValue == 0) {
         this.columnSpecifications.remove(columnName);
         return;
      }

      cs.setForm(formValue);
   }

   public int getForm(String columnName) {
      ColumnSpecification cs = (ColumnSpecification)this.columnSpecifications.get(columnName);
      return cs == null ? 0 : cs.getForm();
   }

   protected static class ColumnSpecification {
      protected int formValue = 0;

      public ColumnSpecification() {
      }

      public void setForm(int formValue) {
         this.formValue = formValue;
      }

      public int getForm() {
         return this.formValue;
      }

      public String toString() {
         return Integer.toString(this.formValue);
      }

      public int hashCode() {
         return this.formValue;
      }

      public boolean equals(Object o) {
         if (!(o instanceof ColumnSpecification)) {
            return false;
         } else {
            ColumnSpecification other = (ColumnSpecification)o;
            return other.formValue == this.formValue;
         }
      }
   }
}
