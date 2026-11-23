package org.apache.manifoldcf.core.interfaces;

public interface ICacheClass {
   String _rcsid = "@(#)$Id: ICacheClass.java 988245 2010-08-23 18:39:35Z kwright $";

   String getClassName();

   int getMaxLRUCount();
}
