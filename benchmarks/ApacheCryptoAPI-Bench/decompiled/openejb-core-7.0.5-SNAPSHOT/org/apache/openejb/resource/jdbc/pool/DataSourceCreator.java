package org.apache.openejb.resource.jdbc.pool;

import java.util.Properties;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import org.apache.openejb.resource.XAResourceWrapper;
import org.apache.xbean.recipe.ObjectRecipe;

public interface DataSourceCreator {
   DataSource managed(String var1, CommonDataSource var2);

   DataSource poolManaged(String var1, DataSource var2, Properties var3);

   DataSource pool(String var1, DataSource var2, Properties var3);

   DataSource poolManagedWithRecovery(String var1, XAResourceWrapper var2, String var3, Properties var4);

   DataSource poolManaged(String var1, String var2, Properties var3);

   CommonDataSource pool(String var1, String var2, Properties var3);

   void destroy(Object var1) throws Throwable;

   ObjectRecipe clearRecipe(Object var1);
}
