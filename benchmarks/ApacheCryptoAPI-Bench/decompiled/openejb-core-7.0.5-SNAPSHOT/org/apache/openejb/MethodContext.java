package org.apache.openejb;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.LockType;
import org.apache.openejb.core.interceptor.InterceptorData;
import org.apache.openejb.core.timer.ScheduleData;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openejb.util.Duration;

public class MethodContext {
   private final BeanContext beanContext;
   private final Method beanMethod;
   private final List<ScheduleData> schedules = new ArrayList();
   private final List<InterceptorData> interceptors = new ArrayList();
   private final Set<InterceptorData> cdiInterceptors = new LinkedHashSet();
   private InterceptorData self = null;
   private LockType lockType;
   private TransactionType transactionType;
   private Duration accessTimeout;
   private boolean asynchronous;

   public MethodContext(BeanContext beanContext, Method beanMethod) {
      this.beanContext = beanContext;
      this.beanMethod = beanMethod;
   }

   public void setSelfInterception(InterceptorData data) {
      this.self = data;
   }

   public void setAccessTimeout(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
   }

   public Duration getAccessTimeout() {
      return this.accessTimeout;
   }

   public BeanContext getBeanContext() {
      return this.beanContext;
   }

   public Method getBeanMethod() {
      return this.beanMethod;
   }

   public void addCdiInterceptor(InterceptorData data) {
      this.cdiInterceptors.add(data);
   }

   public void setInterceptors(List<InterceptorData> interceptors) {
      this.interceptors.clear();
      this.interceptors.addAll(interceptors);
   }

   public List<InterceptorData> getInterceptors() {
      List<InterceptorData> datas = this.beanContext.getInterceptorData();
      datas.addAll(this.interceptors);
      datas.addAll(this.beanContext.getCdiInterceptors());
      datas.addAll(this.cdiInterceptors);
      if (this.self != null) {
         datas.add(this.self);
      }

      return datas;
   }

   public LockType getLockType() {
      return this.lockType != null ? this.lockType : this.beanContext.getLockType();
   }

   public void setLockType(LockType lockType) {
      this.lockType = lockType;
   }

   public TransactionType getTransactionType() {
      return this.transactionType != null ? this.transactionType : this.beanContext.getTransactionType();
   }

   public void setTransactionType(TransactionType transactionType) {
      this.transactionType = transactionType;
   }

   public List<ScheduleData> getSchedules() {
      return this.schedules;
   }

   public boolean isAsynchronous() {
      return this.asynchronous;
   }

   public void setAsynchronous(boolean asynchronous) {
      this.asynchronous = asynchronous;
   }

   public static class InterfaceMethodContext {
      private final MethodContext beanMethod;
      private final Method method;
      private TransactionType transactionType;

      public InterfaceMethodContext(MethodContext beanMethod, Method method) {
         this.beanMethod = beanMethod;
         this.method = method;
      }

      public Method getMethod() {
         return this.method;
      }

      public void setTransactionType(TransactionType transactionType) {
         this.transactionType = transactionType;
      }

      public TransactionType getTransactionType() {
         return this.transactionType != null ? this.transactionType : this.beanMethod.getTransactionType();
      }

      public MethodContext getBeanMethod() {
         return this.beanMethod;
      }
   }
}
