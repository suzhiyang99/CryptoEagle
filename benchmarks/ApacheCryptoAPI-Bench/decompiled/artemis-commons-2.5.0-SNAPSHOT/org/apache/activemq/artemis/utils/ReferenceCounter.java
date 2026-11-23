package org.apache.activemq.artemis.utils;

public interface ReferenceCounter {
   int increment();

   int decrement();
}
