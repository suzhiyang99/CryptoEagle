package org.apache.openejb.core.cmp.jpa;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.EntityBean;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.cmp.CmpCallback;
import org.apache.openejb.core.cmp.CmpEngine;
import org.apache.openejb.core.cmp.ComplexKeyGenerator;
import org.apache.openejb.core.cmp.KeyGenerator;
import org.apache.openejb.core.cmp.SimpleKeyGenerator;
import org.apache.openejb.core.cmp.cmp2.Cmp2KeyGenerator;
import org.apache.openejb.core.cmp.cmp2.Cmp2Util;
import org.apache.openejb.core.transaction.EjbTransactionUtil;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openjpa.event.AbstractLifecycleListener;
import org.apache.openjpa.event.LifecycleEvent;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;
import org.apache.openjpa.persistence.OpenJPAEntityManagerSPI;

public class JpaCmpEngine implements CmpEngine {
   private static final Object[] NO_ARGS = new Object[0];
   public static final String CMP_PERSISTENCE_CONTEXT_REF_NAME = "comp/env/openejb/cmp";
   private final CmpCallback cmpCallback;
   private final ThreadLocal<Set<EntityBean>> creating = new ThreadLocal<Set<EntityBean>>() {
      protected Set<EntityBean> initialValue() {
         return new HashSet();
      }
   };
   protected Object entityManagerListener;

   public JpaCmpEngine(CmpCallback cmpCallback) {
      this.cmpCallback = cmpCallback;
   }

   public synchronized void deploy(BeanContext beanContext) throws OpenEJBException {
      this.configureKeyGenerator(beanContext);
   }

   public synchronized void undeploy(BeanContext beanContext) throws OpenEJBException {
      beanContext.setKeyGenerator((KeyGenerator)null);
   }

   private EntityManager getEntityManager(BeanContext beanContext) {
      EntityManager entityManager = null;

      try {
         entityManager = (EntityManager)beanContext.getJndiEnc().lookup("comp/env/openejb/cmp");
      } catch (NamingException var6) {
         try {
            entityManager = (EntityManager)(new InitialContext()).lookup("java:comp/env/openejb/cmp");
         } catch (NamingException var5) {
         }
      }

      if (entityManager == null) {
         throw new EJBException("Entity manager not found at \"openejb/cmp\" in jndi ejb " + beanContext.getDeploymentID());
      } else {
         this.registerListener(entityManager);
         return entityManager;
      }
   }

   private synchronized void registerListener(EntityManager entityManager) {
      if (entityManager instanceof OpenJPAEntityManagerSPI) {
         OpenJPAEntityManagerSPI openjpaEM = (OpenJPAEntityManagerSPI)entityManager;
         OpenJPAEntityManagerFactorySPI openjpaEMF = (OpenJPAEntityManagerFactorySPI)openjpaEM.getEntityManagerFactory();
         if (this.entityManagerListener == null) {
            this.entityManagerListener = new OpenJPALifecycleListener();
         }

         openjpaEMF.addLifecycleListener(this.entityManagerListener, (Class[])null);
      } else {
         Object delegate = entityManager.getDelegate();
         if (delegate != entityManager && delegate instanceof EntityManager) {
            this.registerListener((EntityManager)delegate);
         }

      }
   }

   public Object createBean(EntityBean bean, ThreadContext callContext) throws CreateException {
      TransactionPolicy txPolicy = this.startTransaction("persist", callContext);
      ((Set)this.creating.get()).add(bean);

      Object var8;
      try {
         BeanContext beanContext = callContext.getBeanContext();
         EntityManager entityManager = this.getEntityManager(beanContext);
         entityManager.persist(bean);
         entityManager.flush();
         bean = (EntityBean)entityManager.merge(bean);
         KeyGenerator kg = beanContext.getKeyGenerator();
         Object primaryKey = kg.getPrimaryKey(bean);
         var8 = primaryKey;
      } finally {
         ((Set)this.creating.get()).remove(bean);
         this.commitTransaction("persist", callContext, txPolicy);
      }

      return var8;
   }

   public Object loadBean(ThreadContext callContext, Object primaryKey) {
      TransactionPolicy txPolicy = this.startTransaction("load", callContext);

      Object var7;
      try {
         BeanContext beanContext = callContext.getBeanContext();
         Class<?> beanClass = beanContext.getCmpImplClass();
         EntityManager entityManager = this.getEntityManager(beanContext);
         var7 = entityManager.find(beanClass, primaryKey);
      } finally {
         this.commitTransaction("load", callContext, txPolicy);
      }

      return var7;
   }

   public void storeBeanIfNoTx(ThreadContext callContext, Object bean) {
      TransactionPolicy callerTxPolicy = callContext.getTransactionPolicy();
      if (callerTxPolicy == null || !callerTxPolicy.isTransactionActive()) {
         TransactionPolicy txPolicy = this.startTransaction("store", callContext);

         try {
            if (txPolicy.isNewTransaction()) {
               EntityManager entityManager = this.getEntityManager(callContext.getBeanContext());
               entityManager.merge(bean);
            }
         } finally {
            this.commitTransaction("store", callContext, txPolicy);
         }

      }
   }

   public void removeBean(ThreadContext callContext) {
      TransactionPolicy txPolicy = this.startTransaction("remove", callContext);

      try {
         BeanContext deploymentInfo = callContext.getBeanContext();
         Class<?> beanClass = deploymentInfo.getCmpImplClass();
         EntityManager entityManager = this.getEntityManager(deploymentInfo);
         Object primaryKey = callContext.getPrimaryKey();
         Object bean = entityManager.find(beanClass, primaryKey);
         entityManager.remove(bean);
      } finally {
         this.commitTransaction("remove", callContext, txPolicy);
      }

   }

   public List<Object> queryBeans(ThreadContext callContext, Method queryMethod, Object[] args) throws FinderException {
      BeanContext deploymentInfo = callContext.getBeanContext();
      EntityManager entityManager = this.getEntityManager(deploymentInfo);
      StringBuilder queryName = new StringBuilder();
      queryName.append(deploymentInfo.getAbstractSchemaName()).append(".").append(queryMethod.getName());
      String shortName = queryName.toString();
      if (queryMethod.getParameterTypes().length > 0) {
         queryName.append('(');
         boolean first = true;

         for(Class<?> parameterType : queryMethod.getParameterTypes()) {
            if (!first) {
               queryName.append(',');
            }

            queryName.append(parameterType.getCanonicalName());
            first = false;
         }

         queryName.append(')');
      }

      String fullName = queryName.toString();
      Query query = this.createNamedQuery(entityManager, fullName);
      if (query == null) {
         query = this.createNamedQuery(entityManager, shortName);
         if (query == null) {
            throw new FinderException("No query defined for method " + fullName);
         }
      }

      return this.executeSelectQuery(query, args);
   }

   public List<Object> queryBeans(BeanContext beanContext, String signature, Object[] args) throws FinderException {
      EntityManager entityManager = this.getEntityManager(beanContext);
      Query query = this.createNamedQuery(entityManager, signature);
      if (query == null) {
         int parenIndex = signature.indexOf(40);
         if (parenIndex > 0) {
            String shortName = signature.substring(0, parenIndex);
            query = this.createNamedQuery(entityManager, shortName);
         }

         if (query == null) {
            throw new FinderException("No query defined for method " + signature);
         }
      }

      return this.executeSelectQuery(query, args);
   }

   private List<Object> executeSelectQuery(Query query, Object[] args) {
      if (args == null) {
         args = NO_ARGS;
      }

      for(int i = 0; i < args.length; ++i) {
         Object arg = args[i];
         if (arg instanceof EJBObject) {
            arg = Cmp2Util.getEntityBean((EJBObject)arg);
         }

         if (arg instanceof EJBLocalObject) {
            arg = Cmp2Util.getEntityBean((EJBLocalObject)arg);
         }

         try {
            query.getParameter(i + 1);
         } catch (IllegalArgumentException var7) {
            continue;
         }

         query.setParameter(i + 1, arg);
      }

      List results = query.getResultList();

      for(Object value : results) {
         if (value instanceof EntityBean) {
            EntityBean entity = (EntityBean)value;
            this.cmpCallback.setEntityContext(entity);
            this.cmpCallback.ejbActivate(entity);
         }
      }

      return results;
   }

   public int executeUpdateQuery(BeanContext beanContext, String signature, Object[] args) throws FinderException {
      EntityManager entityManager = this.getEntityManager(beanContext);
      Query query = this.createNamedQuery(entityManager, signature);
      if (query == null) {
         int parenIndex = signature.indexOf(40);
         if (parenIndex > 0) {
            String shortName = signature.substring(0, parenIndex);
            query = this.createNamedQuery(entityManager, shortName);
         }

         if (query == null) {
            throw new FinderException("No query defined for method " + signature);
         }
      }

      if (args == null) {
         args = NO_ARGS;
      }

      for(int i = 0; i < args.length; ++i) {
         Object arg = args[i];
         if (arg instanceof EJBObject) {
            arg = Cmp2Util.getEntityBean((EJBObject)arg);
         }

         if (arg instanceof EJBLocalObject) {
            arg = Cmp2Util.getEntityBean((EJBLocalObject)arg);
         }

         query.setParameter(i + 1, arg);
      }

      int result = query.executeUpdate();
      return result;
   }

   private Query createNamedQuery(EntityManager entityManager, String name) {
      try {
         return entityManager.createNamedQuery(name);
      } catch (IllegalArgumentException ignored) {
         ignored.printStackTrace();
         return null;
      }
   }

   private TransactionPolicy startTransaction(String operation, ThreadContext callContext) {
      try {
         TransactionPolicy txPolicy = EjbTransactionUtil.createTransactionPolicy(TransactionType.Required, callContext);
         return txPolicy;
      } catch (Exception e) {
         throw new EJBException("Unable to start transaction for " + operation + " operation", e);
      }
   }

   private void commitTransaction(String operation, ThreadContext callContext, TransactionPolicy txPolicy) {
      try {
         EjbTransactionUtil.afterInvoke(txPolicy, callContext);
      } catch (Exception e) {
         throw new EJBException("Unable to complete transaction for " + operation + " operation", e);
      }
   }

   private void configureKeyGenerator(BeanContext di) throws OpenEJBException {
      if (di.isCmp2()) {
         di.setKeyGenerator(new Cmp2KeyGenerator());
      } else {
         String primaryKeyField = di.getPrimaryKeyField();
         Class cmpBeanImpl = di.getCmpImplClass();
         if (primaryKeyField != null) {
            di.setKeyGenerator(new SimpleKeyGenerator(cmpBeanImpl, primaryKeyField));
         } else if (Object.class.equals(di.getPrimaryKeyClass())) {
            di.setKeyGenerator(new SimpleKeyGenerator(cmpBeanImpl, "OpenEJB_pk"));
         } else {
            di.setKeyGenerator(new ComplexKeyGenerator(cmpBeanImpl, di.getPrimaryKeyClass()));
         }
      }

   }

   private class OpenJPALifecycleListener extends AbstractLifecycleListener {
      private OpenJPALifecycleListener() {
      }

      public void afterLoad(LifecycleEvent lifecycleEvent) {
         this.eventOccurred(lifecycleEvent);
         Object bean = lifecycleEvent.getSource();
         JpaCmpEngine.this.cmpCallback.setEntityContext((EntityBean)bean);
         JpaCmpEngine.this.cmpCallback.ejbActivate((EntityBean)bean);
         JpaCmpEngine.this.cmpCallback.ejbLoad((EntityBean)bean);
      }

      public void beforeStore(LifecycleEvent lifecycleEvent) {
         this.eventOccurred(lifecycleEvent);
         EntityBean bean = (EntityBean)lifecycleEvent.getSource();
         if (!((Set)JpaCmpEngine.this.creating.get()).contains(bean)) {
            JpaCmpEngine.this.cmpCallback.ejbStore(bean);
         }

      }

      public void afterAttach(LifecycleEvent lifecycleEvent) {
         this.eventOccurred(lifecycleEvent);
         Object bean = lifecycleEvent.getSource();
         JpaCmpEngine.this.cmpCallback.setEntityContext((EntityBean)bean);
      }

      public void beforeDelete(LifecycleEvent lifecycleEvent) {
         this.eventOccurred(lifecycleEvent);

         try {
            Object bean = lifecycleEvent.getSource();
            JpaCmpEngine.this.cmpCallback.ejbRemove((EntityBean)bean);
         } catch (RemoveException e) {
            throw new PersistenceException(e);
         }
      }

      public void afterDetach(LifecycleEvent lifecycleEvent) {
         this.eventOccurred(lifecycleEvent);
         Object bean = lifecycleEvent.getSource();
         JpaCmpEngine.this.cmpCallback.ejbPassivate((EntityBean)bean);
         JpaCmpEngine.this.cmpCallback.unsetEntityContext((EntityBean)bean);
      }

      public void beforePersist(LifecycleEvent lifecycleEvent) {
         this.eventOccurred(lifecycleEvent);
      }

      public void afterRefresh(LifecycleEvent lifecycleEvent) {
         this.eventOccurred(lifecycleEvent);
      }

      public void beforeDetach(LifecycleEvent lifecycleEvent) {
         this.eventOccurred(lifecycleEvent);
      }

      public void beforeAttach(LifecycleEvent lifecycleEvent) {
         this.eventOccurred(lifecycleEvent);
      }
   }
}
