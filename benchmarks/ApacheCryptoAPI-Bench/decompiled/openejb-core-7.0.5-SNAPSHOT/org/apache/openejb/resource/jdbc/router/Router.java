package org.apache.openejb.resource.jdbc.router;

import javax.sql.DataSource;

public interface Router {
   DataSource getDataSource();
}
