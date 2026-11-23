package org.apache.manifoldcf.core.interfaces;

public interface IThreadContext {
   String _rcsid = "@(#)$Id: IThreadContext.java 988245 2010-08-23 18:39:35Z kwright $";

   void save(Object var1, Object var2);

   Object get(Object var1);
}
