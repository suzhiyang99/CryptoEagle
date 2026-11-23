package org.apache.wicket.util.diff;

import java.util.List;

public class AddDelta extends Delta {
   AddDelta() {
   }

   public AddDelta(int origpos, Chunk rev) {
      this.init(new Chunk(origpos, 0), rev);
   }

   public void verify(List<Object> target) throws PatchFailedException {
      if (this.original.first() > target.size()) {
         throw new PatchFailedException("original.first() > target.size()");
      }
   }

   public void applyTo(List<Object> target) {
      this.revised.applyAdd(this.original.first(), target);
   }

   public void toString(StringBuilder s) {
      s.append(this.original.anchor());
      s.append("a");
      s.append(this.revised.rangeString());
      s.append(Diff.NL);
      this.revised.toString(s, "> ", Diff.NL);
   }

   public void toRCSString(StringBuilder s, String EOL) {
      s.append("a");
      s.append(this.original.anchor());
      s.append(" ");
      s.append(this.revised.size());
      s.append(EOL);
      this.revised.toString(s, "", EOL);
   }

   public void accept(RevisionVisitor visitor) {
      visitor.visit(this);
   }
}
