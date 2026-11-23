package org.apache.manifoldcf.core.interfaces;

public interface ICacheCreateHandle {
   String _rcsid = "@(#)$Id: ICacheCreateHandle.java 988245 2010-08-23 18:39:35Z kwright $";

   String[] getCriticalSectionNames();

   long getLookupTime();

   String getTransactionID();
}
