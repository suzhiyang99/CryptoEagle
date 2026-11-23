package org.apache.tika.mime;

class Magic implements Clause, Comparable<Magic> {
   private final MimeType type;
   private final int priority;
   private final Clause clause;
   private final String string;

   Magic(MimeType type, int priority, Clause clause) {
      this.type = type;
      this.priority = priority;
      this.clause = clause;
      this.string = "[" + priority + "/" + clause + "]";
   }

   MimeType getType() {
      return this.type;
   }

   int getPriority() {
      return this.priority;
   }

   public boolean eval(byte[] data) {
      return this.clause.eval(data);
   }

   public int size() {
      return this.clause.size();
   }

   public String toString() {
      return this.string;
   }

   public int compareTo(Magic o) {
      int diff = o.priority - this.priority;
      if (diff == 0) {
         diff = o.size() - this.size();
      }

      if (diff == 0) {
         diff = o.type.compareTo(this.type);
      }

      if (diff == 0) {
         diff = o.string.compareTo(this.string);
      }

      return diff;
   }

   public boolean equals(Object o) {
      if (!(o instanceof Magic)) {
         return false;
      } else {
         Magic that = (Magic)o;
         return this.type.equals(that.type) && this.string.equals(that.string);
      }
   }

   public int hashCode() {
      return this.type.hashCode() ^ this.string.hashCode();
   }
}
