package org.apache.activemq.artemis.utils.collections;

import java.util.Iterator;

public interface LinkedListIterator<E> extends Iterator<E>, AutoCloseable {
   void repeat();

   void close();
}
