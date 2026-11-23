package org.apache.openejb.persistence;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.SynchronizationType;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.assembler.classic.ReloadableEntityManagerFactory;
import org.apache.openejb.core.ivm.IntraVmArtifact;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.reflection.Reflections;

public class JtaEntityManager implements EntityManager, Serializable {
   private static final Logger baseLogger;
   private static final Method CREATE_NAMED_QUERY_FROM_NAME;
   private static final Method CREATE_QUERY_FROM_NAME;
   private static final Method CREATE_NATIVE_FROM_NAME;
   private static final Method CREATE_NAMED_QUERY_FROM_NAME_CLASS;
   private static final Method CREATE_QUERY_FROM_NAME_CLASS;
   private static final Method CREATE_QUERY_FROM_CRITERIA;
   private static final Method CREATE_NATIVE_FROM_NAME_CLASS;
   private static final Method CREATE_NATIVE_FROM_NAME_MAPPING;
   private static final ConcurrentMap<Class<?>, Boolean> IS_JPA21;
   private final JtaEntityManagerRegistry registry;
   private final EntityManagerFactory entityManagerFactory;
   private final Map properties;
   private final boolean extended;
   private final SynchronizationType synchronizationType;
   private final String unitName;
   private final Logger logger;
   private final boolean wrapNoTxQueries;
   private final boolean timer;

   public JtaEntityManager(JtaEntityManagerRegistry registry, EntityManagerFactory entityManagerFactory, Map properties, String unitName, String synchronizationType) {
      this(unitName, registry, entityManagerFactory, properties, false, synchronizationType);
   }

   public JtaEntityManager(String unitName, JtaEntityManagerRegistry registry, EntityManagerFactory entityManagerFactory, Map properties, boolean extended, String synchronizationType) {
      if (registry == null) {
         throw new NullPointerException("registry is null");
      } else if (entityManagerFactory == null) {
         throw new NullPointerException("entityManagerFactory is null");
      } else {
         this.unitName = unitName;
         this.registry = registry;
         this.entityManagerFactory = entityManagerFactory;
         this.properties = properties;
         this.extended = extended;
         this.synchronizationType = isJPA21(entityManagerFactory) && synchronizationType != null ? SynchronizationType.valueOf(synchronizationType.toUpperCase(Locale.ENGLISH)) : null;
         String globalTimerConfig = SystemInstance.get().getProperty("openejb.jpa.timer");
         Object localTimerConfig = properties == null ? null : properties.get("openejb.jpa.timer");
         this.timer = localTimerConfig == null ? globalTimerConfig == null || Boolean.parseBoolean(globalTimerConfig) : Boolean.parseBoolean(localTimerConfig.toString());
         this.logger = unitName == null ? baseLogger : baseLogger.getChildLogger(unitName);
         String wrapConfig = ReloadableEntityManagerFactory.class.isInstance(entityManagerFactory) ? ((ReloadableEntityManagerFactory)ReloadableEntityManagerFactory.class.cast(entityManagerFactory)).getUnitProperties().getProperty("openejb.jpa.query.wrap-no-tx", "true") : "true";
         this.wrapNoTxQueries = wrapConfig == null || "true".equalsIgnoreCase(wrapConfig);
      }
   }

   public static boolean isJPA21(EntityManagerFactory entityManagerFactory) {
      return ReloadableEntityManagerFactory.class.isInstance(entityManagerFactory) ? hasMethod(((ReloadableEntityManagerFactory)ReloadableEntityManagerFactory.class.cast(entityManagerFactory)).getEntityManagerFactoryCallable().getProvider(), "generateSchema", String.class, Map.class) : hasMethod(entityManagerFactory.getClass(), "createEntityManager", SynchronizationType.class);
   }

   private static boolean hasMethod(Class<?> objectClass, String name, Class<?>... params) {
      try {
         Boolean result = (Boolean)IS_JPA21.get(objectClass);
         if (result == null) {
            result = !Modifier.isAbstract(objectClass.getMethod(name, params).getModifiers());
            if (objectClass.getClassLoader() == JtaEntityManager.class.getClassLoader()) {
               IS_JPA21.putIfAbsent(objectClass, result);
            }
         }

         return result;
      } catch (Throwable var4) {
         return false;
      }
   }

   EntityManager getEntityManager() {
      return this.registry.getEntityManager(this.entityManagerFactory, this.properties, this.extended, this.unitName, this.synchronizationType);
   }

   boolean isTransactionActive() {
      return this.registry.isTransactionActive();
   }

   private void assertTransactionActive() throws TransactionRequiredException {
      if (!this.extended && !this.isTransactionActive()) {
         throw new TransactionRequiredException();
      }
   }

   void closeIfNoTx(EntityManager entityManager) {
      if (!this.extended && !this.isTransactionActive()) {
         entityManager.close();
         this.logger.debug("Closed EntityManager(unit=" + this.unitName + ", hashCode=" + entityManager.hashCode() + ")");
      }

   }

   public EntityManager getDelegate() {
      Timer timer = JtaEntityManager.Op.getDelegate.start(this.timer, this);

      EntityManager var3;
      try {
         EntityManager em = this.getEntityManager();
         em.getDelegate();
         var3 = em;
      } finally {
         timer.stop();
      }

      return var3;
   }

   public void persist(Object entity) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.persist.start(this.timer, this);

      try {
         this.getEntityManager().persist(entity);
      } finally {
         timer.stop();
      }

   }

   public <T> T merge(T entity) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.merge.start(this.timer, this);

      Object var3;
      try {
         var3 = this.getEntityManager().merge(entity);
      } finally {
         timer.stop();
      }

      return (T)var3;
   }

   public void remove(Object entity) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.remove.start(this.timer, this);

      try {
         this.getEntityManager().remove(entity);
      } finally {
         timer.stop();
      }

   }

   public <T> T find(Class<T> entityClass, Object primaryKey) {
      EntityManager entityManager = this.getEntityManager();

      Object var5;
      try {
         Timer timer = JtaEntityManager.Op.find.start(this.timer, this);

         try {
            var5 = entityManager.find(entityClass, primaryKey);
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

      return (T)var5;
   }

   public <T> T getReference(Class<T> entityClass, Object primaryKey) {
      EntityManager entityManager = this.getEntityManager();

      Object var5;
      try {
         Timer timer = JtaEntityManager.Op.getReference.start(this.timer, this);

         try {
            var5 = entityManager.getReference(entityClass, primaryKey);
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

      return (T)var5;
   }

   public void flush() {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.flush.start(this.timer, this);

      try {
         this.getEntityManager().flush();
      } finally {
         timer.stop();
      }

   }

   public void setFlushMode(FlushModeType flushMode) {
      EntityManager entityManager = this.getEntityManager();

      try {
         Timer timer = JtaEntityManager.Op.setFlushMode.start(this.timer, this);

         try {
            entityManager.setFlushMode(flushMode);
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

   }

   public FlushModeType getFlushMode() {
      EntityManager entityManager = this.getEntityManager();

      FlushModeType var3;
      try {
         Timer timer = JtaEntityManager.Op.getFlushMode.start(this.timer, this);

         try {
            var3 = entityManager.getFlushMode();
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

      return var3;
   }

   public void lock(Object entity, LockModeType lockMode) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.lock.start(this.timer, this);

      try {
         this.getEntityManager().lock(entity, lockMode);
      } finally {
         timer.stop();
      }

   }

   public void refresh(Object entity) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.refresh.start(this.timer, this);

      try {
         this.getEntityManager().refresh(entity);
      } finally {
         timer.stop();
      }

   }

   public void clear() {
      if (this.extended || this.isTransactionActive()) {
         Timer timer = JtaEntityManager.Op.clear.start(this.timer, this);

         try {
            this.getEntityManager().clear();
         } finally {
            timer.stop();
         }

      }
   }

   public boolean contains(Object entity) {
      Timer timer = JtaEntityManager.Op.contains.start(this.timer, this);

      boolean var3;
      try {
         var3 = (this.extended || this.isTransactionActive()) && this.getEntityManager().contains(entity);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public Query createQuery(String qlString) {
      Timer timer = JtaEntityManager.Op.createQuery.start(this.timer, this);

      Query var3;
      try {
         var3 = this.proxyIfNoTx(CREATE_QUERY_FROM_NAME, qlString);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public Query createNamedQuery(String name) {
      Timer timer = JtaEntityManager.Op.createNamedQuery.start(this.timer, this);

      Query var3;
      try {
         var3 = this.proxyIfNoTx(CREATE_NAMED_QUERY_FROM_NAME, name);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public Query createNativeQuery(String sqlString) {
      Timer timer = JtaEntityManager.Op.createNativeQuery.start(this.timer, this);

      Query var3;
      try {
         var3 = this.proxyIfNoTx(CREATE_NATIVE_FROM_NAME, sqlString);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public Query createNativeQuery(String sqlString, Class resultClass) {
      Timer timer = JtaEntityManager.Op.createNativeQuery.start(this.timer, this);

      Query var4;
      try {
         var4 = this.proxyIfNoTx(CREATE_NATIVE_FROM_NAME_CLASS, sqlString, resultClass);
      } finally {
         timer.stop();
      }

      return var4;
   }

   public Query createNativeQuery(String sqlString, String resultSetMapping) {
      Timer timer = JtaEntityManager.Op.createNativeQuery.start(this.timer, this);

      Query var4;
      try {
         var4 = this.proxyIfNoTx(CREATE_NATIVE_FROM_NAME_MAPPING, sqlString, resultSetMapping);
      } finally {
         timer.stop();
      }

      return var4;
   }

   private Query proxyIfNoTx(Method method, Object... args) {
      return (Query)(this.wrapNoTxQueries && !this.extended && !this.isTransactionActive() ? new JtaQuery(this.getEntityManager(), this, method, args) : (Query)this.createQuery(Query.class, this.getEntityManager(), method, args));
   }

   private <T> TypedQuery<T> typedProxyIfNoTx(Method method, Object... args) {
      return (TypedQuery<T>)(this.wrapNoTxQueries && !this.extended && !this.isTransactionActive() ? new JtaTypedQuery(this.getEntityManager(), this, method, args) : (TypedQuery)this.createQuery(TypedQuery.class, this.getEntityManager(), method, args));
   }

   <T> T createQuery(Class<T> expected, EntityManager entityManager, Method method, Object... args) {
      try {
         return (T)expected.cast(method.invoke(entityManager, args));
      } catch (IllegalAccessException e) {
         throw new IllegalStateException(e);
      } catch (InvocationTargetException e) {
         Throwable t = e.getCause();
         if (RuntimeException.class.isInstance(t)) {
            throw (RuntimeException)RuntimeException.class.cast(t);
         } else {
            throw new OpenEJBRuntimeException(t.getMessage(), t);
         }
      }
   }

   public void joinTransaction() {
      Timer timer = JtaEntityManager.Op.joinTransaction.start(this.timer, this);

      try {
         this.getDelegate().joinTransaction();
      } finally {
         timer.stop();
      }

   }

   public void close() {
      throw new IllegalStateException("PersistenceUnit(name=" + this.unitName + ") - entityManager.close() call - See JPA 2.0 section 7.9.1", (new Exception()).fillInStackTrace());
   }

   public boolean isOpen() {
      return true;
   }

   public EntityTransaction getTransaction() {
      throw new IllegalStateException("A JTA EntityManager can not use the EntityTransaction API.  See JPA 1.0 section 5.5");
   }

   public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
      Timer timer = JtaEntityManager.Op.createNamedQuery.start(this.timer, this);

      TypedQuery var4;
      try {
         var4 = this.typedProxyIfNoTx(CREATE_NAMED_QUERY_FROM_NAME_CLASS, name, resultClass);
      } finally {
         timer.stop();
      }

      return var4;
   }

   public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
      Timer timer = JtaEntityManager.Op.createQuery.start(this.timer, this);

      TypedQuery var3;
      try {
         var3 = this.typedProxyIfNoTx(CREATE_QUERY_FROM_CRITERIA, criteriaQuery);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
      Timer timer = JtaEntityManager.Op.createQuery.start(this.timer, this);

      TypedQuery var4;
      try {
         var4 = this.typedProxyIfNoTx(CREATE_QUERY_FROM_NAME_CLASS, qlString, resultClass);
      } finally {
         timer.stop();
      }

      return var4;
   }

   public void detach(Object entity) {
      Timer timer = JtaEntityManager.Op.detach.start(this.timer, this);

      try {
         if (!this.extended && this.isTransactionActive()) {
            this.getEntityManager().detach(entity);
         }
      } finally {
         timer.stop();
      }

   }

   public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
      EntityManager entityManager = this.getEntityManager();

      Object var6;
      try {
         Timer timer = JtaEntityManager.Op.find.start(this.timer, this);

         try {
            var6 = entityManager.find(entityClass, primaryKey, properties);
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

      return (T)var6;
   }

   public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
      EntityManager entityManager = this.getEntityManager();

      Object var6;
      try {
         Timer timer = JtaEntityManager.Op.find.start(this.timer, this);

         try {
            var6 = entityManager.find(entityClass, primaryKey, lockMode);
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

      return (T)var6;
   }

   public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
      EntityManager entityManager = this.getEntityManager();

      Object var7;
      try {
         Timer timer = JtaEntityManager.Op.find.start(this.timer, this);

         try {
            var7 = entityManager.find(entityClass, primaryKey, lockMode, properties);
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

      return (T)var7;
   }

   public EntityManagerFactory getEntityManagerFactory() {
      return this.entityManagerFactory;
   }

   public LockModeType getLockMode(Object entity) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.getLockMode.start(this.timer, this);

      LockModeType var3;
      try {
         var3 = this.getEntityManager().getLockMode(entity);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public Metamodel getMetamodel() {
      EntityManager entityManager = this.getEntityManager();

      Metamodel var3;
      try {
         Timer timer = JtaEntityManager.Op.getMetamodel.start(this.timer, this);

         try {
            var3 = entityManager.getMetamodel();
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

      return var3;
   }

   public Map<String, Object> getProperties() {
      EntityManager entityManager = this.getEntityManager();

      Map var3;
      try {
         Timer timer = JtaEntityManager.Op.getProperties.start(this.timer, this);

         try {
            var3 = entityManager.getProperties();
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

      return var3;
   }

   public CriteriaBuilder getCriteriaBuilder() {
      EntityManager entityManager = this.getEntityManager();

      CriteriaBuilder var3;
      try {
         Timer timer = JtaEntityManager.Op.getCriteriaBuilder.start(this.timer, this);

         try {
            var3 = entityManager.getCriteriaBuilder();
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

      return var3;
   }

   public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.lock.start(this.timer, this);

      try {
         this.getEntityManager().lock(entity, lockMode, properties);
      } finally {
         timer.stop();
      }

   }

   public void refresh(Object entity, Map<String, Object> properties) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.refresh.start(this.timer, this);

      try {
         this.getEntityManager().refresh(entity, properties);
      } finally {
         timer.stop();
      }

   }

   public void refresh(Object entity, LockModeType lockMode) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.refresh.start(this.timer, this);

      try {
         this.getEntityManager().refresh(entity, lockMode);
      } finally {
         timer.stop();
      }

   }

   public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
      this.assertTransactionActive();
      Timer timer = JtaEntityManager.Op.refresh.start(this.timer, this);

      try {
         this.getEntityManager().refresh(entity, lockMode, properties);
      } finally {
         timer.stop();
      }

   }

   public void setProperty(String name, Object value) {
      EntityManager entityManager = this.getEntityManager();

      try {
         Timer timer = JtaEntityManager.Op.setProperty.start(this.timer, this);

         try {
            entityManager.setProperty(name, value);
         } finally {
            timer.stop();
         }
      } finally {
         this.closeIfNoTx(entityManager);
      }

   }

   public <T> T unwrap(Class<T> cls) {
      return (T)this.getEntityManager().unwrap(cls);
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new IntraVmArtifact(this, true);
   }

   public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
      Timer timer = JtaEntityManager.Op.createNamedStoredProcedureQuery.start(this.timer, this);

      StoredProcedureQuery var3;
      try {
         var3 = this.getEntityManager().createNamedStoredProcedureQuery(name);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
      Timer timer = JtaEntityManager.Op.createNamedStoredProcedureQuery.start(this.timer, this);

      StoredProcedureQuery var3;
      try {
         var3 = this.getEntityManager().createStoredProcedureQuery(procedureName);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
      Timer timer = JtaEntityManager.Op.createStoredProcedureQuery.start(this.timer, this);

      StoredProcedureQuery var4;
      try {
         var4 = this.getEntityManager().createStoredProcedureQuery(procedureName, resultClasses);
      } finally {
         timer.stop();
      }

      return var4;
   }

   public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
      Timer timer = JtaEntityManager.Op.createStoredProcedureQuery.start(this.timer, this);

      StoredProcedureQuery var4;
      try {
         var4 = this.getEntityManager().createStoredProcedureQuery(procedureName, resultSetMappings);
      } finally {
         timer.stop();
      }

      return var4;
   }

   public Query createQuery(CriteriaUpdate updateQuery) {
      Timer timer = JtaEntityManager.Op.createQuery.start(this.timer, this);

      Query var3;
      try {
         var3 = this.getEntityManager().createQuery(updateQuery);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public Query createQuery(CriteriaDelete deleteQuery) {
      Timer timer = JtaEntityManager.Op.createQuery.start(this.timer, this);

      Query var3;
      try {
         var3 = this.getEntityManager().createQuery(deleteQuery);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
      Timer timer = JtaEntityManager.Op.createEntityGraph.start(this.timer, this);

      EntityGraph var3;
      try {
         var3 = this.getEntityManager().createEntityGraph(rootType);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public EntityGraph<?> createEntityGraph(String graphName) {
      Timer timer = JtaEntityManager.Op.createEntityGraph.start(this.timer, this);

      EntityGraph var3;
      try {
         var3 = this.getEntityManager().createEntityGraph(graphName);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public EntityGraph<?> getEntityGraph(String graphName) {
      Timer timer = JtaEntityManager.Op.getEntityGraph.start(this.timer, this);

      EntityGraph var3;
      try {
         var3 = this.getEntityManager().getEntityGraph(graphName);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
      Timer timer = JtaEntityManager.Op.getEntityGraphs.start(this.timer, this);

      List var3;
      try {
         var3 = this.getEntityManager().getEntityGraphs(entityClass);
      } finally {
         timer.stop();
      }

      return var3;
   }

   public boolean isJoinedToTransaction() {
      Timer timer = JtaEntityManager.Op.isJoinedToTransaction.start(this.timer, this);

      boolean var2;
      try {
         var2 = this.synchronizationType == null || this.getEntityManager().isJoinedToTransaction();
      } finally {
         timer.stop();
      }

      return var2;
   }

   static {
      baseLogger = Logger.getInstance(LogCategory.OPENEJB.createChild("persistence"), JtaEntityManager.class);
      CREATE_NAMED_QUERY_FROM_NAME = Reflections.findMethod("createNamedQuery", EntityManager.class, String.class);
      CREATE_QUERY_FROM_NAME = Reflections.findMethod("createQuery", EntityManager.class, String.class);
      CREATE_NATIVE_FROM_NAME = Reflections.findMethod("createNativeQuery", EntityManager.class, String.class);
      CREATE_NAMED_QUERY_FROM_NAME_CLASS = Reflections.findMethod("createNamedQuery", EntityManager.class, String.class, Class.class);
      CREATE_QUERY_FROM_NAME_CLASS = Reflections.findMethod("createQuery", EntityManager.class, String.class, Class.class);
      CREATE_QUERY_FROM_CRITERIA = Reflections.findMethod("createQuery", EntityManager.class, CriteriaQuery.class);
      CREATE_NATIVE_FROM_NAME_CLASS = Reflections.findMethod("createNativeQuery", EntityManager.class, String.class, Class.class);
      CREATE_NATIVE_FROM_NAME_MAPPING = Reflections.findMethod("createNativeQuery", EntityManager.class, String.class, String.class);
      IS_JPA21 = new ConcurrentHashMap();
   }

   public static class Timer {
      private final long start = System.nanoTime();
      private final Op operation;
      private final JtaEntityManager em;

      public Timer(Op operation, JtaEntityManager em) {
         this.operation = operation;
         this.em = em;
      }

      public void stop() {
         if (this.em.logger.isDebugEnabled()) {
            long time = TimeUnit.MILLISECONDS.convert(System.nanoTime() - this.start, TimeUnit.NANOSECONDS);
            this.em.logger.debug("PersistenceUnit(name=" + this.em.unitName + ") - entityManager." + this.operation + " - " + time + "ms");
         }
      }
   }

   private static enum Op {
      clear,
      close,
      contains,
      createNamedQuery,
      createNativeQuery,
      createQuery,
      find,
      flush,
      getFlushMode,
      getReference,
      getTransaction,
      lock,
      merge,
      refresh,
      remove,
      setFlushMode,
      persist,
      detach,
      getLockMode,
      unwrap,
      setProperty,
      getCriteriaBuilder,
      getProperties,
      getMetamodel,
      joinTransaction,
      getDelegate,
      createNamedStoredProcedureQuery,
      createStoredProcedureQuery,
      createEntityGraph,
      getEntityGraph,
      getEntityGraphs,
      isJoinedToTransaction;

      private static final Timer NOOP = new Timer((Op)null, (JtaEntityManager)null) {
         public void stop() {
         }
      };

      public Timer start(boolean timer, JtaEntityManager em) {
         return timer ? new Timer(this, em) : NOOP;
      }
   }
}
