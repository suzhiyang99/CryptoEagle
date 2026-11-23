package org.apache.activemq.artemis.utils.collections;

public interface PriorityLinkedList<T> {
   void addHead(T var1, int var2);

   void addTail(T var1, int var2);

   T poll();

   void clear();

   int size();

   LinkedListIterator<T> iterator();

   boolean isEmpty();
}
