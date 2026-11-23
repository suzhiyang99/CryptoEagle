package org.apache.activemq.artemis.utils.collections;

import java.util.Set;

public interface ConcurrentSet<E> extends Set<E> {
   boolean addIfAbsent(E var1);
}
