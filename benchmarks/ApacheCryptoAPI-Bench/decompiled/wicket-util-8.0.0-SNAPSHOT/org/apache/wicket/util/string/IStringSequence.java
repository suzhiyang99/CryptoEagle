package org.apache.wicket.util.string;

public interface IStringSequence {
   String get(int var1) throws IndexOutOfBoundsException;

   IStringIterator iterator();

   int size();
}
