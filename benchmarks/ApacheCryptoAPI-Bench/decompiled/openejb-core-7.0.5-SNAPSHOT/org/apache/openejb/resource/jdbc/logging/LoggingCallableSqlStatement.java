package org.apache.openejb.resource.jdbc.logging;

import java.sql.PreparedStatement;

public class LoggingCallableSqlStatement extends LoggingPreparedSqlStatement {
   public LoggingCallableSqlStatement(PreparedStatement result, String query, String[] debugPackages) {
      super(result, query, debugPackages);
   }
}
