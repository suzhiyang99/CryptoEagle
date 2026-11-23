package org.apache.openejb.persistence;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class CriteriaLogQuery<T> implements TypedQuery<T> {
   private static final Logger LOGGER;
   private static final Map<Class<?>, Method> methodsCache;
   private static final Map<Class<?>, Class<?>> unwrapCache;
   private static final String GET_QUERY_STRING_MTD = "getQueryString";
   private final TypedQuery<T> delegate;
   private final String logLevel;

   public CriteriaLogQuery(TypedQuery<T> query, String level) {
      this.delegate = query;
      this.logLevel = level.toLowerCase();
   }

   private void logJPQLQuery() {
      Class<?> clazz = this.delegate.getClass();
      Method mtd = (Method)methodsCache.get(clazz);
      Class<?> unwrapQuery = (Class)unwrapCache.get(clazz);
      if (mtd == null) {
         try {
            mtd = clazz.getMethod("getQueryString");
         } catch (NoSuchMethodException var9) {
            try {
               unwrapQuery = clazz.getClassLoader().loadClass("org.hibernate.Query");
               unwrapCache.put(clazz, unwrapQuery);
               mtd = unwrapQuery.getMethod("getQueryString");
            } catch (Exception var8) {
               try {
                  mtd = this.getClass().getMethod("getQueryString");
               } catch (NoSuchMethodException var7) {
               }
            }
         }

         methodsCache.put(clazz, mtd);
      }

      this.logJPQLQuery(unwrapQuery, mtd);
   }

   public String getQueryString() {
      return this.delegate.getClass().getName() + " doesn't support getQueryString() method: '" + this.delegate.toString() + "'";
   }

   private void logJPQLQuery(Class<?> unwrap, Method mtd) {
      String query = null;
      Object realQuery = this.delegate;
      if (unwrap != null) {
         realQuery = this.delegate.unwrap(unwrap);
      }

      try {
         query = (String)mtd.invoke(realQuery);
      } catch (Exception var8) {
         try {
            query = this.getQueryString();
         } catch (Exception var7) {
         }
      }

      String msg = "executing query '" + query + "'";
      if (this.logLevel.equals("info")) {
         LOGGER.info(msg);
      } else if (!this.logLevel.equals("debug") && !this.logLevel.equals("fine") && !this.logLevel.equals("finest")) {
         if (this.logLevel.equals("error")) {
            LOGGER.error(msg);
         } else if (this.logLevel.equals("fatal")) {
            LOGGER.fatal(msg);
         } else if (!this.logLevel.equals("warning") && !this.logLevel.equals("warn")) {
            LOGGER.debug(msg);
         } else {
            LOGGER.warning(msg);
         }
      } else {
         LOGGER.debug(msg);
      }

   }

   public List<T> getResultList() {
      this.logJPQLQuery();
      return this.delegate.getResultList();
   }

   public T getSingleResult() {
      this.logJPQLQuery();
      return (T)this.delegate.getSingleResult();
   }

   public int executeUpdate() {
      this.logJPQLQuery();
      return this.delegate.executeUpdate();
   }

   public TypedQuery<T> setMaxResults(int maxResult) {
      this.delegate.setMaxResults(maxResult);
      return this;
   }

   public int getMaxResults() {
      return this.delegate.getMaxResults();
   }

   public TypedQuery<T> setFirstResult(int startPosition) {
      this.delegate.setFirstResult(startPosition);
      return this;
   }

   public int getFirstResult() {
      return this.delegate.getFirstResult();
   }

   public TypedQuery<T> setHint(String hintName, Object value) {
      this.delegate.setHint(hintName, value);
      return this;
   }

   public <E> TypedQuery<T> setParameter(Parameter<E> param, E value) {
      this.delegate.setParameter(param, value);
      return this;
   }

   public Map<String, Object> getHints() {
      return this.delegate.getHints();
   }

   public TypedQuery<T> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
      this.delegate.setParameter(param, value, temporalType);
      return this;
   }

   public TypedQuery<T> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
      this.delegate.setParameter(param, value, temporalType);
      return this;
   }

   public TypedQuery<T> setParameter(String name, Object value) {
      this.delegate.setParameter(name, value);
      return this;
   }

   public TypedQuery<T> setParameter(String name, Calendar value, TemporalType temporalType) {
      this.delegate.setParameter(name, value, temporalType);
      return this;
   }

   public TypedQuery<T> setParameter(String name, Date value, TemporalType temporalType) {
      this.delegate.setParameter(name, value, temporalType);
      return this;
   }

   public TypedQuery<T> setParameter(int position, Object value) {
      this.delegate.setParameter(position, value);
      return this;
   }

   public TypedQuery<T> setParameter(int position, Calendar value, TemporalType temporalType) {
      this.delegate.setParameter(position, value, temporalType);
      return this;
   }

   public TypedQuery<T> setParameter(int position, Date value, TemporalType temporalType) {
      this.delegate.setParameter(position, value, temporalType);
      return this;
   }

   public Set<Parameter<?>> getParameters() {
      return this.delegate.getParameters();
   }

   public Parameter<?> getParameter(String name) {
      return this.delegate.getParameter(name);
   }

   public <T> Parameter<T> getParameter(String name, Class<T> type) {
      return this.delegate.getParameter(name, type);
   }

   public Parameter<?> getParameter(int position) {
      return this.delegate.getParameter(position);
   }

   public <T> Parameter<T> getParameter(int position, Class<T> type) {
      return this.delegate.getParameter(position, type);
   }

   public boolean isBound(Parameter<?> param) {
      return this.delegate.isBound(param);
   }

   public <T> T getParameterValue(Parameter<T> param) {
      return (T)this.delegate.getParameterValue(param);
   }

   public Object getParameterValue(String name) {
      return this.delegate.getParameterValue(name);
   }

   public Object getParameterValue(int position) {
      return this.delegate.getParameterValue(position);
   }

   public TypedQuery<T> setFlushMode(FlushModeType flushMode) {
      return this.delegate.setFlushMode(flushMode);
   }

   public FlushModeType getFlushMode() {
      return this.delegate.getFlushMode();
   }

   public TypedQuery<T> setLockMode(LockModeType lockMode) {
      return this.delegate.setLockMode(lockMode);
   }

   public LockModeType getLockMode() {
      return this.delegate.getLockMode();
   }

   public <T> T unwrap(Class<T> cls) {
      return (T)this.delegate.unwrap(cls);
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB_JPA, CriteriaLogQuery.class);
      methodsCache = new ConcurrentHashMap();
      unwrapCache = new ConcurrentHashMap();
   }
}
