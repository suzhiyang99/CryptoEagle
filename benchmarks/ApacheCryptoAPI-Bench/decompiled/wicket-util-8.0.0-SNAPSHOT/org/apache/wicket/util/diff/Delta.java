package org.apache.wicket.util.diff;

import java.util.List;

public abstract class Delta extends ToString {
   protected Chunk original;
   protected Chunk revised;
   static Class<?>[][] DeltaClass = new Class[2][2];

   public static Delta newDelta(Chunk orig, Chunk rev) {
      Class<?> c = DeltaClass[orig.size() > 0 ? 1 : 0][rev.size() > 0 ? 1 : 0];

      Delta result;
      try {
         result = (Delta)c.newInstance();
      } catch (Exception var5) {
         return null;
      }

      result.init(orig, rev);
      return result;
   }

   protected Delta() {
   }

   protected Delta(Chunk orig, Chunk rev) {
      this.init(orig, rev);
   }

   protected void init(Chunk orig, Chunk rev) {
      this.original = orig;
      this.revised = rev;
   }

   public abstract void verify(List<Object> var1) throws PatchFailedException;

   public final void patch(List<Object> target) throws PatchFailedException {
      this.verify(target);

      try {
         this.applyTo(target);
      } catch (Exception e) {
         throw new PatchFailedException(e.getMessage());
      }
   }

   public abstract void applyTo(List<Object> var1);

   public void toString(StringBuilder s) {
      this.original.rangeString(s);
      s.append("x");
      this.revised.rangeString(s);
      s.append(Diff.NL);
      this.original.toString(s, "> ", "\n");
      s.append("---");
      s.append(Diff.NL);
      this.revised.toString(s, "< ", "\n");
   }

   public abstract void toRCSString(StringBuilder var1, String var2);

   public String toRCSString(String EOL) {
      StringBuilder s = new StringBuilder();
      this.toRCSString(s, EOL);
      return s.toString();
   }

   public Chunk getOriginal() {
      return this.original;
   }

   public Chunk getRevised() {
      return this.revised;
   }

   public abstract void accept(RevisionVisitor var1);

   static {
      try {
         DeltaClass[0][0] = ChangeDelta.class;
         DeltaClass[0][1] = AddDelta.class;
         DeltaClass[1][0] = DeleteDelta.class;
         DeltaClass[1][1] = ChangeDelta.class;
      } catch (Exception var1) {
      }

   }
}
