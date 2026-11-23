package org.apache.manifoldcf.core.interfaces;

public interface ICacheDescription {
   String _rcsid = "@(#)$Id: ICacheDescription.java 988245 2010-08-23 18:39:35Z kwright $";

   StringSet getObjectKeys();

   String getCriticalSectionName();

   ICacheClass getObjectClass();

   long getObjectExpirationTime(long var1);
}
