package org.apache.manifoldcf.core.interfaces;

public interface IResultSet {
   String _rcsid = "@(#)$Id: IResultSet.java 988245 2010-08-23 18:39:35Z kwright $";

   IResultRow getRow(int var1);

   int getRowCount();

   IResultRow[] getRows();
}
