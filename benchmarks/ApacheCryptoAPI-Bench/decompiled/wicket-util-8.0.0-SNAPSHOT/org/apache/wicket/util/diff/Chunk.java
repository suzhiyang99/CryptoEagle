package org.apache.wicket.util.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chunk extends ToString {
   protected int anchor;
   protected int count;
   protected List<Object> chunk;

   public Chunk(int pos, int count) {
      this.anchor = pos;
      this.count = count >= 0 ? count : 0;
   }

   public Chunk(Object[] iseq, int pos, int count) {
      this(pos, count);
      this.chunk = slice(iseq, pos, count);
   }

   public Chunk(Object[] iseq, int pos, int count, int offset) {
      this(offset, count);
      this.chunk = slice(iseq, pos, count);
   }

   public Chunk(List<Object> iseq, int pos, int count) {
      this(pos, count);
      this.chunk = slice(iseq, pos, count);
   }

   public Chunk(List<Object> iseq, int pos, int count, int offset) {
      this(offset, count);
      this.chunk = slice(iseq, pos, count);
   }

   public int anchor() {
      return this.anchor;
   }

   public int size() {
      return this.count;
   }

   public int first() {
      return this.anchor();
   }

   public int last() {
      return this.anchor() + this.size() - 1;
   }

   public int rcsfrom() {
      return this.anchor + 1;
   }

   public int rcsto() {
      return this.anchor + this.count;
   }

   public List<Object> chunk() {
      return this.chunk;
   }

   public boolean verify(List<Object> target) {
      if (this.chunk == null) {
         return true;
      } else if (this.last() > target.size()) {
         return false;
      } else {
         for(int i = 0; i < this.count; ++i) {
            if (!target.get(this.anchor + i).equals(this.chunk.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public void applyDelete(List<Object> target) {
      for(int i = this.last(); i >= this.first(); --i) {
         target.remove(i);
      }

   }

   public void applyAdd(int start, List<Object> target) {
      for(Object aChunk : this.chunk) {
         target.add(start++, aChunk);
      }

   }

   public void toString(StringBuilder s) {
      this.toString(s, "", "");
   }

   public StringBuilder toString(StringBuilder s, String prefix, String postfix) {
      if (this.chunk != null) {
         for(Object aChunk : this.chunk) {
            s.append(prefix);
            s.append(aChunk);
            s.append(postfix);
         }
      }

      return s;
   }

   public static <T> List<T> slice(List<T> seq, int pos, int count) {
      return count <= 0 ? new ArrayList() : new ArrayList(seq.subList(pos, pos + count));
   }

   public static List<Object> slice(Object[] seq, int pos, int count) {
      return slice(Arrays.asList(seq), pos, count);
   }

   public String rangeString() {
      StringBuilder result = new StringBuilder();
      this.rangeString(result);
      return result.toString();
   }

   public void rangeString(StringBuilder s) {
      this.rangeString(s, ",");
   }

   public void rangeString(StringBuilder s, String separ) {
      if (this.size() <= 1) {
         s.append(Integer.toString(this.rcsfrom()));
      } else {
         s.append(Integer.toString(this.rcsfrom()));
         s.append(separ);
         s.append(Integer.toString(this.rcsto()));
      }

   }
}
