package org.apache.manifoldcf.core.interfaces;

import java.util.Iterator;

public interface IResultRow {
   String _rcsid = "@(#)$Id: IResultRow.java 988245 2010-08-23 18:39:35Z kwright $";

   int getColumnCount();

   Iterator<String> getColumns();

   Object getValue(String var1);
}
