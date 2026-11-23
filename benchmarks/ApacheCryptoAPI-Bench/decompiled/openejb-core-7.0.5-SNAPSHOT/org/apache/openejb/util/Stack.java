package org.apache.openejb.util;

import java.util.EmptyStackException;

public interface Stack {
   Object pop() throws EmptyStackException;

   Object push(Object var1);

   int size();
}
