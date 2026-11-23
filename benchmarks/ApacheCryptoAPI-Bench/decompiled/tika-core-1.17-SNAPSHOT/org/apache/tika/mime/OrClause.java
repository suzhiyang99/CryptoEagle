package org.apache.tika.mime;

import java.util.List;

class OrClause implements Clause {
   private final List<Clause> clauses;

   OrClause(List<Clause> clauses) {
      this.clauses = clauses;
   }

   public boolean eval(byte[] data) {
      for(Clause clause : this.clauses) {
         if (clause.eval(data)) {
            return true;
         }
      }

      return false;
   }

   public int size() {
      int size = 0;

      for(Clause clause : this.clauses) {
         size = Math.max(size, clause.size());
      }

      return size;
   }

   public String toString() {
      return "or" + this.clauses;
   }
}
