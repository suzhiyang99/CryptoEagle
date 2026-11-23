package org.apache.openejb.core.timer.quartz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.openejb.quartz.impl.jdbcjobstore.StdJDBCDelegate;

public class PatchedStdJDBCDelegate extends StdJDBCDelegate {
   protected Object getObjectFromBlob(ResultSet rs, String colName) throws ClassNotFoundException, IOException, SQLException {
      Object obj = null;
      Blob blobLocator = rs.getBlob(colName);
      if (blobLocator != null && blobLocator.length() != 0L) {
         InputStream binaryInput = blobLocator.getBinaryStream();
         if (null != binaryInput && (!(binaryInput instanceof ByteArrayInputStream) || ((ByteArrayInputStream)binaryInput).available() != 0)) {
            ObjectInputStream in = new QuartzObjectInputStream(binaryInput, this.classLoadHelper);

            try {
               obj = in.readObject();
            } finally {
               in.close();
            }
         }
      }

      return obj;
   }
}
