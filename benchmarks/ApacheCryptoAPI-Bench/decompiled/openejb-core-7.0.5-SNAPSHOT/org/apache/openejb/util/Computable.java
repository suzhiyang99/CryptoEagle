package org.apache.openejb.util;

public interface Computable<K, V> {
   V compute(K var1) throws InterruptedException;
}
