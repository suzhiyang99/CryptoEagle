package org.apache.wicket.util.diff;

public interface RevisionVisitor {
   void visit(Revision var1);

   void visit(DeleteDelta var1);

   void visit(ChangeDelta var1);

   void visit(AddDelta var1);
}
