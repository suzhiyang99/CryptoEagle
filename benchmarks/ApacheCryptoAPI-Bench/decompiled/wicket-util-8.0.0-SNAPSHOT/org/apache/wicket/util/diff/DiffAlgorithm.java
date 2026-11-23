package org.apache.wicket.util.diff;

public interface DiffAlgorithm {
   Revision diff(Object[] var1, Object[] var2) throws DifferentiationFailedException;
}
