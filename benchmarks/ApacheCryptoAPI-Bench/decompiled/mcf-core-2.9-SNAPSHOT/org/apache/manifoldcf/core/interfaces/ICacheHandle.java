package org.apache.manifoldcf.core.interfaces;

public interface ICacheHandle {
   String _rcsid = "@(#)$Id: ICacheHandle.java 988245 2010-08-23 18:39:35Z kwright $";

   String[] getReadLockStrings();

   String[] getWriteLockStrings();

   ICacheDescription[] getObjectDescriptions();

   StringSet getInvalidationKeys();

   String getTransactionID();
}
