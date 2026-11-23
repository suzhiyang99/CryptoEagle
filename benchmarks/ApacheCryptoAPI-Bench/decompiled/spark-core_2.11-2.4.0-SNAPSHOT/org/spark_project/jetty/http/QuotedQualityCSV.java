package org.spark_project.jetty.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuotedQualityCSV extends QuotedCSV implements Iterable<String> {
   private static final Double ZERO = new Double((double)0.0F);
   private static final Double ONE = new Double((double)1.0F);
   private final List<Double> _quality = new ArrayList();
   private boolean _sorted = false;

   public QuotedQualityCSV(String... values) {
      super();

      for(String v : values) {
         this.addValue(v);
      }

   }

   public void addValue(String value) {
      super.addValue(value);

      while(this._quality.size() < this._values.size()) {
         this._quality.add(ONE);
      }

   }

   protected void parsedValue(StringBuffer buffer) {
      super.parsedValue(buffer);
   }

   protected void parsedParam(StringBuffer buffer, int valueLength, int paramName, int paramValue) {
      if (paramName < 0 && buffer.charAt(buffer.length() - 1) == ';') {
         buffer.setLength(buffer.length() - 1);
      }

      if (paramValue >= 0 && buffer.charAt(paramName) == 'q' && paramValue > paramName && buffer.length() >= paramName && buffer.charAt(paramName + 1) == '=') {
         Double q;
         try {
            q = this._keepQuotes && buffer.charAt(paramValue) == '"' ? new Double(buffer.substring(paramValue + 1, buffer.length() - 1)) : new Double(buffer.substring(paramValue));
         } catch (Exception var7) {
            q = ZERO;
         }

         buffer.setLength(paramName - 1);

         while(this._quality.size() < this._values.size()) {
            this._quality.add(ONE);
         }

         this._quality.add(q);
      }

   }

   public List<String> getValues() {
      if (!this._sorted) {
         this.sort();
      }

      return this._values;
   }

   public Iterator<String> iterator() {
      if (!this._sorted) {
         this.sort();
      }

      return this._values.iterator();
   }

   protected void sort() {
      this._sorted = true;
      Double last = ZERO;
      int i = this._values.size();

      while(i-- > 0) {
         String v = (String)this._values.get(i);
         Double q = (Double)this._quality.get(i);
         int compare = last.compareTo(q);
         if (compare > 0) {
            this._values.set(i, this._values.get(i + 1));
            this._values.set(i + 1, v);
            this._quality.set(i, this._quality.get(i + 1));
            this._quality.set(i + 1, q);
            last = ZERO;
            i = this._values.size();
         } else {
            last = q;
         }
      }

      i = this._quality.size();

      while(i > 0) {
         --i;
         if (!((Double)this._quality.get(i)).equals(ZERO)) {
            break;
         }

         this._quality.remove(i);
         this._values.remove(i);
      }

   }
}
