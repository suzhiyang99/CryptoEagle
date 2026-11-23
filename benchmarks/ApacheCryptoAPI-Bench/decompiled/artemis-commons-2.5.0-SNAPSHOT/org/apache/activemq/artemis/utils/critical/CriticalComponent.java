package org.apache.activemq.artemis.utils.critical;

public interface CriticalComponent {
   default void enterCritical(int path) {
   }

   default void leaveCritical(int path) {
   }

   boolean isExpired(long var1);
}
