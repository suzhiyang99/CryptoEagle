package org.apache.openejb.persistence;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

public class JtaTypedQuery<X> extends JtaQuery implements TypedQuery<X> {
   public JtaTypedQuery(EntityManager entityManager, JtaEntityManager jtaEm, Method method, Object... args) {
      super(entityManager, jtaEm, method, args);
   }

   protected Class<? extends Query> queryType() {
      return TypedQuery.class;
   }

   public List<X> getResultList() {
      return super.getResultList();
   }

   public X getSingleResult() {
      return (X)super.getSingleResult();
   }

   public TypedQuery<X> setFirstResult(int i) {
      super.setFirstResult(i);
      return this;
   }

   public TypedQuery<X> setFlushMode(FlushModeType flushModeType) {
      super.setFlushMode(flushModeType);
      return this;
   }

   public TypedQuery<X> setHint(String s, Object o) {
      super.setHint(s, o);
      return this;
   }

   public TypedQuery<X> setLockMode(LockModeType lockMode) {
      super.setLockMode(lockMode);
      return this;
   }

   public TypedQuery<X> setMaxResults(int i) {
      super.setMaxResults(i);
      return this;
   }

   public TypedQuery<X> setParameter(int i, Calendar calendar, TemporalType temporalType) {
      super.setParameter(i, calendar, temporalType);
      return this;
   }

   public TypedQuery<X> setParameter(int i, Date date, TemporalType temporalType) {
      super.setParameter(i, date, temporalType);
      return this;
   }

   public TypedQuery<X> setParameter(int i, Object o) {
      super.setParameter(i, o);
      return this;
   }

   public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
      super.setParameter(param, value, temporalType);
      return this;
   }

   public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
      super.setParameter(param, value, temporalType);
      return this;
   }

   public <T> TypedQuery<X> setParameter(Parameter<T> param, T value) {
      super.setParameter(param, value);
      return this;
   }

   public TypedQuery<X> setParameter(String s, Calendar calendar, TemporalType temporalType) {
      super.setParameter(s, calendar, temporalType);
      return this;
   }

   public TypedQuery<X> setParameter(String s, Date date, TemporalType temporalType) {
      super.setParameter(s, date, temporalType);
      return this;
   }

   public TypedQuery<X> setParameter(String s, Object o) {
      super.setParameter(s, o);
      return this;
   }
}
