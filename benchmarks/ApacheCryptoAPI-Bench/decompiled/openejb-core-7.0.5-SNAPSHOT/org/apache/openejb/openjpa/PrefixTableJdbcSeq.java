package org.apache.openejb.openjpa;

import org.apache.openjpa.jdbc.kernel.TableJDBCSeq;

public class PrefixTableJdbcSeq extends TableJDBCSeq {
   private String prefix;

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public void endConfiguration() {
      this.setTable(this.prefix + this.getTable());
      super.endConfiguration();
   }
}
