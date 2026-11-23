package org.apache.wicket.util.diff;

import java.util.List;

public class DeleteDelta extends Delta {
   DeleteDelta() {
   }

   public DeleteDelta(Chunk orig) {
      this.init(orig, (Chunk)null);
   }

   public void verify(List<Object> target) throws PatchFailedException {
      if (!this.original.verify(target)) {
         throw new PatchFailedException();
      }
   }

   public void applyTo(List<Object> target) {
      this.original.applyDelete(target);
   }

   public void toString(StringBuilder s) {
      s.append(this.original.rangeString());
      s.append("d");
      s.append(this.revised.rcsto());
      s.append(Diff.NL);
      this.original.toString(s, "< ", Diff.NL);
   }

   public void toRCSString(StringBuilder s, String EOL) {
      s.append("d");
      s.append(this.original.rcsfrom());
      s.append(" ");
      s.append(this.original.size());
      s.append(EOL);
   }

   public void accept(RevisionVisitor visitor) {
      visitor.visit(this);
   }
}
