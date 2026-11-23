package org.apache.openejb.resource.jdbc.pool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.TransactionManagerWrapper;
import org.apache.openejb.resource.XAResourceWrapper;
import org.apache.openejb.resource.jdbc.managed.local.ManagedDataSource;
import org.apache.openejb.resource.jdbc.managed.xa.ManagedXADataSource;
import org.apache.openejb.util.PassthroughFactory;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

public abstract class PoolDataSourceCreator implements DataSourceCreator {
   protected final Map<Object, ObjectRecipe> recipes = new HashMap();

   protected void cleanProperty(Object ds, String name) {
      Map<String, Object> unsetProperties = ((ObjectRecipe)this.recipes.get(ds)).getUnsetProperties();
      Iterator<Map.Entry<String, Object>> iterator = unsetProperties.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry<String, Object> entry = (Map.Entry)iterator.next();
         if (((String)entry.getKey()).equalsIgnoreCase(name)) {
            iterator.remove();
         }
      }

   }

   public DataSource managed(String name, CommonDataSource ds) {
      TransactionManager transactionManager = OpenEJB.getTransactionManager();
      return (DataSource)(ds instanceof XADataSource ? new ManagedXADataSource(ds, transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class)) : new ManagedDataSource((DataSource)DataSource.class.cast(ds), transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class)));
   }

   public DataSource poolManagedWithRecovery(String name, XAResourceWrapper xaResourceWrapper, String driver, Properties properties) {
      TransactionManager transactionManager = new TransactionManagerWrapper(OpenEJB.getTransactionManager(), name, xaResourceWrapper);
      CommonDataSource ds = this.pool(name, driver, properties);
      return (DataSource)(ds instanceof XADataSource ? new ManagedXADataSource(ds, transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class)) : new ManagedDataSource((DataSource)DataSource.class.cast(ds), transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class)));
   }

   public DataSource poolManaged(String name, DataSource ds, Properties properties) {
      return this.managed(name, this.pool(name, ds, properties));
   }

   public DataSource poolManaged(String name, String driver, Properties properties) {
      return this.managed(name, this.pool(name, driver, properties));
   }

   public void destroy(Object object) throws Throwable {
      if (object instanceof ManagedDataSource) {
         this.doDestroy(((ManagedDataSource)object).getDelegate());
      } else {
         this.doDestroy((DataSource)object);
      }

   }

   protected abstract void doDestroy(CommonDataSource var1) throws Throwable;

   protected <T> T build(Class<T> clazz, Properties properties) {
      ObjectRecipe serviceRecipe = new ObjectRecipe(clazz);
      this.recipeOptions(serviceRecipe);
      serviceRecipe.setAllProperties(properties);
      T value = (T)serviceRecipe.create();
      if (this.trackRecipeFor(value)) {
         this.recipes.put(value, serviceRecipe);
      }

      return value;
   }

   protected boolean trackRecipeFor(Object value) {
      return value instanceof DataSource;
   }

   protected <T> T build(Class<T> clazz, Object instance, Properties properties) {
      ObjectRecipe recipe = PassthroughFactory.recipe(instance);
      this.recipeOptions(recipe);
      recipe.setAllProperties(properties);
      T value = (T)recipe.create();
      this.recipes.put(value, recipe);
      return value;
   }

   private void recipeOptions(ObjectRecipe recipe) {
      recipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
      recipe.allow(Option.IGNORE_MISSING_PROPERTIES);
   }

   public ObjectRecipe clearRecipe(Object object) {
      return object instanceof ManagedDataSource ? (ObjectRecipe)this.recipes.remove(((ManagedDataSource)object).getDelegate()) : (ObjectRecipe)this.recipes.remove(object);
   }
}
