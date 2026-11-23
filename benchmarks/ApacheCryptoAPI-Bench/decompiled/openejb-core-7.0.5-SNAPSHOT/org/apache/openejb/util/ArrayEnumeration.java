package org.apache.openejb.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

public final class ArrayEnumeration implements Enumeration, Externalizable {
   static final long serialVersionUID = -1194966576855523042L;
   private Object[] elements;
   private int elementsIndex;

   public ArrayEnumeration(Vector elements) {
      this.elements = new Object[elements.size()];
      elements.copyInto(this.elements);
   }

   public ArrayEnumeration(List list) {
      this.elements = new Object[list.size()];
      list.toArray(this.elements);
   }

   public ArrayEnumeration(Set<?> set) {
      this.elements = new Object[set.size()];
      set.toArray(this.elements);
   }

   public ArrayEnumeration() {
   }

   public Object get(int index) {
      return this.elements[index];
   }

   public void set(int index, Object o) {
      this.elements[index] = o;
   }

   public int size() {
      return this.elements.length;
   }

   public boolean hasMoreElements() {
      return this.elementsIndex < this.elements.length;
   }

   public Object nextElement() {
      if (!this.hasMoreElements()) {
         throw new NoSuchElementException("No more elements exist");
      } else {
         return this.elements[this.elementsIndex++];
      }
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeInt(this.elements.length);
      out.writeInt(this.elementsIndex);

      for(Object element : this.elements) {
         out.writeObject(element);
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.elements = new Object[in.readInt()];
      this.elementsIndex = in.readInt();

      for(int i = 0; i < this.elements.length; ++i) {
         this.elements[i] = in.readObject();
      }

   }
}
