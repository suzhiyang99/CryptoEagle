package org.apache.wicket.util.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Revision extends ToString {
   List<Delta> deltas_ = new LinkedList();

   public synchronized void addDelta(Delta delta) {
      if (delta == null) {
         throw new IllegalArgumentException("new delta is null");
      } else {
         this.deltas_.add(delta);
      }
   }

   public synchronized void insertDelta(Delta delta) {
      if (delta == null) {
         throw new IllegalArgumentException("new delta is null");
      } else {
         this.deltas_.add(0, delta);
      }
   }

   public Delta getDelta(int i) {
      return (Delta)this.deltas_.get(i);
   }

   public int size() {
      return this.deltas_.size();
   }

   public Object[] patch(Object[] src) throws PatchFailedException {
      List<Object> target = new ArrayList(Arrays.asList(src));
      this.applyTo(target);
      return target.toArray();
   }

   public synchronized void applyTo(List<Object> target) throws PatchFailedException {
      ListIterator<Delta> i = this.deltas_.listIterator(this.deltas_.size());

      while(i.hasPrevious()) {
         Delta delta = (Delta)i.previous();
         delta.patch(target);
      }

   }

   public synchronized void toString(StringBuilder s) {
      for(Delta delta : this.deltas_) {
         delta.toString(s);
      }

   }

   public synchronized void toRCSString(StringBuilder s, String EOL) {
      for(Delta deltas : this.deltas_) {
         deltas.toRCSString(s, EOL);
      }

   }

   public void toRCSString(StringBuilder s) {
      this.toRCSString(s, Diff.NL);
   }

   public String toRCSString(String EOL) {
      StringBuilder s = new StringBuilder();
      this.toRCSString(s, EOL);
      return s.toString();
   }

   public String toRCSString() {
      return this.toRCSString(Diff.NL);
   }

   public void accept(RevisionVisitor visitor) {
      visitor.visit(this);

      for(Delta delta : this.deltas_) {
         delta.accept(visitor);
      }

   }
}
