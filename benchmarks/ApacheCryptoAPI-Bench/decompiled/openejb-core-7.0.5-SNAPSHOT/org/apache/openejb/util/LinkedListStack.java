package org.apache.openejb.util;

import java.util.EmptyStackException;

public class LinkedListStack implements Stack {
   private LinkedEntry occupiedEntries;
   private LinkedEntry vacantEntries;
   private int size;

   public LinkedListStack(int initialSize) {
      for(int i = 0; i < initialSize; ++i) {
         this.vacantEntries = new LinkedEntry((Object)null, this.vacantEntries);
      }

   }

   public synchronized Object push(Object object) {
      if (this.vacantEntries == null) {
         this.occupiedEntries = new LinkedEntry(object, this.occupiedEntries);
      } else {
         LinkedEntry entry = this.vacantEntries;
         this.vacantEntries = this.vacantEntries.next;
         this.occupiedEntries = entry.set(object, this.occupiedEntries);
      }

      ++this.size;
      return object;
   }

   public synchronized Object pop() throws EmptyStackException {
      LinkedEntry entry = this.occupiedEntries;
      if (entry == null) {
         return null;
      } else {
         this.occupiedEntries = this.occupiedEntries.next;
         Object value = entry.value;
         this.vacantEntries = entry.set((Object)null, this.vacantEntries);
         --this.size;
         return value;
      }
   }

   public synchronized int size() {
      return this.size;
   }

   static class LinkedEntry {
      LinkedEntry next;
      Object value;

      LinkedEntry(Object value, LinkedEntry next) {
         this.set(value, next);
      }

      LinkedEntry set(Object value, LinkedEntry next) {
         this.next = next;
         this.value = value;
         return this;
      }
   }
}
