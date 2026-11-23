package org.apache.manifoldcf.core.interfaces;

import java.util.List;

public interface ClauseDescription {
   String _rcsid = "@(#)$Id$";

   String getColumnName();

   String getOperation();

   List getValues();

   String getJoinColumnName();
}
