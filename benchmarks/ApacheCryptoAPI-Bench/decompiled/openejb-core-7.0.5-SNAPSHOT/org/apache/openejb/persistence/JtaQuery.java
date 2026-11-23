package org.apache.openejb.persistence;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;

public class JtaQuery implements Query {
   private EntityManager entityManager;
   private final Object[] args;
   private final Method method;
   private final JtaEntityManager jtaEntityManager;
   private final Collection<QueryOperation> appliedOperations = new ArrayList();
   private boolean underTx;
   private boolean unwrap;
   private Query query;

   public JtaQuery(EntityManager entityManager, JtaEntityManager jtaEntityManager, Method method, Object... args) {
      this.entityManager = entityManager;
      this.jtaEntityManager = jtaEntityManager;
      this.method = method;
      this.args = args;
      this.underTx = jtaEntityManager.isTransactionActive();
      this.createQuery();
   }

   private Query createQuery() {
      if (!this.unwrap) {
         this.query = (Query)this.jtaEntityManager.createQuery(this.queryType(), this.entityManager, this.method, this.args);
      }

      if (!this.underTx) {
         for(QueryOperation op : this.appliedOperations) {
            this.query = op.apply(this.query);
         }
      }

      return this.query;
   }

   protected Class<? extends Query> queryType() {
      return Query.class;
   }

   private EntityManager getEntityManager() {
      if (!this.underTx) {
         this.entityManager = this.jtaEntityManager.getEntityManager();
         this.underTx = this.jtaEntityManager.isTransactionActive();
         this.createQuery();
      }

      return this.entityManager;
   }

   public List getResultList() {
      EntityManager em = this.getEntityManager();

      List var2;
      try {
         var2 = this.query.getResultList();
      } finally {
         this.jtaEntityManager.closeIfNoTx(em);
      }

      return var2;
   }

   public Object getSingleResult() {
      EntityManager em = this.getEntityManager();

      Object var2;
      try {
         var2 = this.query.getSingleResult();
      } finally {
         this.jtaEntityManager.closeIfNoTx(em);
      }

      return var2;
   }

   public int executeUpdate() {
      EntityManager em = this.getEntityManager();

      int var2;
      try {
         var2 = this.query.executeUpdate();
      } finally {
         this.jtaEntityManager.closeIfNoTx(em);
      }

      return var2;
   }

   public Query setMaxResults(final int i) {
      this.query.setMaxResults(i);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setMaxResults(i);
            }
         });
      }

      return this;
   }

   public Query setFirstResult(final int i) {
      this.query.setFirstResult(i);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setFirstResult(i);
            }
         });
      }

      return this;
   }

   public Query setFlushMode(final FlushModeType flushModeType) {
      this.query.setFlushMode(flushModeType);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setFlushMode(flushModeType);
            }
         });
      }

      return this;
   }

   public Query setHint(final String s, final Object o) {
      this.query.setHint(s, o);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setHint(s, o);
            }
         });
      }

      return this;
   }

   public Query setParameter(final String s, final Object o) {
      this.query.setParameter(s, o);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setParameter(s, o);
            }
         });
      }

      return this;
   }

   public Query setParameter(final String s, final Date date, final TemporalType temporalType) {
      this.query.setParameter(s, date, temporalType);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setParameter(s, date, temporalType);
            }
         });
      }

      return this;
   }

   public Query setParameter(final String s, final Calendar calendar, final TemporalType temporalType) {
      this.query.setParameter(s, calendar, temporalType);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setParameter(s, calendar, temporalType);
            }
         });
      }

      return this;
   }

   public Query setParameter(final int i, final Object o) {
      this.query.setParameter(i, o);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setParameter(i, o);
            }
         });
      }

      return this;
   }

   public Query setParameter(final int i, final Date date, final TemporalType temporalType) {
      this.query.setParameter(i, date, temporalType);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setParameter(i, date, temporalType);
            }
         });
      }

      return this;
   }

   public Query setParameter(final int i, final Calendar calendar, final TemporalType temporalType) {
      this.query.setParameter(i, calendar, temporalType);
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setParameter(i, calendar, temporalType);
            }
         });
      }

      return this;
   }

   public int getFirstResult() {
      return this.query.getFirstResult();
   }

   public FlushModeType getFlushMode() {
      return this.query.getFlushMode();
   }

   public Map<String, Object> getHints() {
      return this.query.getHints();
   }

   public LockModeType getLockMode() {
      return this.query.getLockMode();
   }

   public int getMaxResults() {
      return this.query.getMaxResults();
   }

   public Parameter<?> getParameter(String name) {
      return this.query.getParameter(name);
   }

   public Parameter<?> getParameter(int position) {
      return this.query.getParameter(position);
   }

   public <T> Parameter<T> getParameter(String name, Class<T> type) {
      return this.query.getParameter(name, type);
   }

   public <T> Parameter<T> getParameter(int position, Class<T> type) {
      return this.query.getParameter(position, type);
   }

   public <T> T getParameterValue(Parameter<T> param) {
      return (T)this.query.getParameterValue(param);
   }

   public Object getParameterValue(String name) {
      return this.query.getParameterValue(name);
   }

   public Object getParameterValue(int position) {
      return this.query.getParameterValue(position);
   }

   public Set<Parameter<?>> getParameters() {
      return this.query.getParameters();
   }

   public boolean isBound(Parameter<?> param) {
      return this.query.isBound(param);
   }

   public Query setLockMode(final LockModeType lockMode) {
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setLockMode(lockMode);
            }
         });
      }

      return this.query.setLockMode(lockMode);
   }

   public <T> Query setParameter(final Parameter<T> param, final T value) {
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setParameter(param, value);
            }
         });
      }

      return this.query.setParameter(param, value);
   }

   public Query setParameter(final Parameter<Calendar> param, final Calendar value, final TemporalType temporalType) {
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setParameter(param, value, temporalType);
            }
         });
      }

      return this.query.setParameter(param, value, temporalType);
   }

   public Query setParameter(final Parameter<Date> param, final Date value, final TemporalType temporalType) {
      if (!this.underTx) {
         this.appliedOperations.add(new QueryOperation() {
            public Query apply(Query query) {
               return query.setParameter(param, value, temporalType);
            }
         });
      }

      return this.query.setParameter(param, value, temporalType);
   }

   public <T> T unwrap(Class<T> cls) {
      this.unwrap = true;
      return (T)(this.getClass() == cls ? cls.cast(this) : this.query.unwrap(cls));
   }
}
