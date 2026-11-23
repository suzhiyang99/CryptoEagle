package org.apache.openejb.persistence;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

public class QueryLogEntityManager implements EntityManager {
   private final EntityManager delegate;
   private final String level;

   public QueryLogEntityManager(EntityManager entityManager, String level) {
      this.delegate = entityManager;
      this.level = level;
   }

   public void persist(Object entity) {
      this.delegate.persist(entity);
   }

   public <T> T merge(T entity) {
      return (T)this.delegate.merge(entity);
   }

   public void remove(Object entity) {
      this.delegate.remove(entity);
   }

   public <T> T find(Class<T> entityClass, Object primaryKey) {
      return (T)this.delegate.find(entityClass, primaryKey);
   }

   public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
      return (T)this.delegate.find(entityClass, primaryKey, properties);
   }

   public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
      return (T)this.delegate.find(entityClass, primaryKey, lockMode);
   }

   public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
      return (T)this.delegate.find(entityClass, primaryKey, lockMode, properties);
   }

   public <T> T getReference(Class<T> entityClass, Object primaryKey) {
      return (T)this.delegate.getReference(entityClass, primaryKey);
   }

   public void flush() {
      this.delegate.flush();
   }

   public void setFlushMode(FlushModeType flushMode) {
      this.delegate.setFlushMode(flushMode);
   }

   public FlushModeType getFlushMode() {
      return this.delegate.getFlushMode();
   }

   public void lock(Object entity, LockModeType lockMode) {
      this.delegate.lock(entity, lockMode);
   }

   public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
      this.delegate.lock(entity, lockMode, properties);
   }

   public void refresh(Object entity) {
      this.delegate.refresh(entity);
   }

   public void refresh(Object entity, Map<String, Object> properties) {
      this.delegate.refresh(entity, properties);
   }

   public void refresh(Object entity, LockModeType lockMode) {
      this.delegate.refresh(entity, lockMode);
   }

   public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
      this.delegate.refresh(entity, lockMode, properties);
   }

   public void clear() {
      this.delegate.clear();
   }

   public void detach(Object entity) {
      this.delegate.detach(entity);
   }

   public boolean contains(Object entity) {
      return this.delegate.contains(entity);
   }

   public LockModeType getLockMode(Object entity) {
      return this.delegate.getLockMode(entity);
   }

   public void setProperty(String propertyName, Object value) {
      this.delegate.setProperty(propertyName, value);
   }

   public Map<String, Object> getProperties() {
      return this.delegate.getProperties();
   }

   public Query createQuery(String qlString) {
      return this.delegate.createQuery(qlString);
   }

   public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
      return new CriteriaLogQuery<T>(this.delegate.createQuery(criteriaQuery), this.level);
   }

   public Query createQuery(CriteriaUpdate updateQuery) {
      return this.delegate.createQuery(updateQuery);
   }

   public Query createQuery(CriteriaDelete deleteQuery) {
      return this.delegate.createQuery(deleteQuery);
   }

   public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
      return this.delegate.createQuery(qlString, resultClass);
   }

   public Query createNamedQuery(String name) {
      return this.delegate.createNamedQuery(name);
   }

   public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
      return this.delegate.createNamedQuery(name, resultClass);
   }

   public Query createNativeQuery(String sqlString) {
      return this.delegate.createNativeQuery(sqlString);
   }

   public Query createNativeQuery(String sqlString, Class resultClass) {
      return this.delegate.createNativeQuery(sqlString, resultClass);
   }

   public Query createNativeQuery(String sqlString, String resultSetMapping) {
      return this.delegate.createNativeQuery(sqlString, resultSetMapping);
   }

   public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
      return this.delegate.createNamedStoredProcedureQuery(name);
   }

   public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
      return this.delegate.createStoredProcedureQuery(procedureName);
   }

   public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
      return this.delegate.createStoredProcedureQuery(procedureName, resultClasses);
   }

   public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
      return this.delegate.createStoredProcedureQuery(procedureName, resultSetMappings);
   }

   public void joinTransaction() {
      this.delegate.joinTransaction();
   }

   public boolean isJoinedToTransaction() {
      return this.delegate.isJoinedToTransaction();
   }

   public <T> T unwrap(Class<T> cls) {
      return (T)this.delegate.unwrap(cls);
   }

   public Object getDelegate() {
      return this.delegate.getDelegate();
   }

   public void close() {
      this.delegate.close();
   }

   public boolean isOpen() {
      return this.delegate.isOpen();
   }

   public EntityTransaction getTransaction() {
      return this.delegate.getTransaction();
   }

   public EntityManagerFactory getEntityManagerFactory() {
      return this.delegate.getEntityManagerFactory();
   }

   public CriteriaBuilder getCriteriaBuilder() {
      return this.delegate.getCriteriaBuilder();
   }

   public Metamodel getMetamodel() {
      return this.delegate.getMetamodel();
   }

   public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
      return this.delegate.createEntityGraph(rootType);
   }

   public EntityGraph<?> createEntityGraph(String graphName) {
      return this.delegate.createEntityGraph(graphName);
   }

   public EntityGraph<?> getEntityGraph(String graphName) {
      return this.delegate.getEntityGraph(graphName);
   }

   public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
      return this.delegate.getEntityGraphs(entityClass);
   }
}
