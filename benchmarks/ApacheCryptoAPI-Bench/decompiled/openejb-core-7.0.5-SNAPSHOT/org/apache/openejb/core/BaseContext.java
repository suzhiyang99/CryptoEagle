package org.apache.openejb.core;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.Identity;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.interceptor.InvocationContext;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.ivm.IntraVmArtifact;
import org.apache.openejb.core.timer.EjbTimerService;
import org.apache.openejb.core.timer.TimerServiceImpl;
import org.apache.openejb.core.timer.Timers;
import org.apache.openejb.core.transaction.EjbUserTransaction;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.transaction.TransactionType;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.threads.task.CUTask;

public abstract class BaseContext implements EJBContext, Serializable {
   protected final SecurityService securityService;
   protected final UserTransaction userTransaction;

   protected BaseContext(SecurityService securityService) {
      this(securityService, new EjbUserTransaction());
   }

   protected BaseContext(SecurityService securityService, UserTransaction userTransaction) {
      this.securityService = securityService;
      this.userTransaction = new UserTransactionWrapper(userTransaction);
   }

   private boolean isAsyncOperation(ThreadContext threadContext) {
      return threadContext.getCurrentOperation() == null && threadContext.get(CUTask.Context.class) != null;
   }

   protected abstract void check(ThreadContext var1, Call var2);

   protected IllegalStateException illegal(Call call, Operation operation) {
      return new IllegalStateException(call + " cannot be called in " + operation);
   }

   public Map<String, Object> getContextData() {
      this.doCheck(BaseContext.Call.getContextData);
      return ((InvocationContext)ThreadContext.getThreadContext().get(InvocationContext.class)).getContextData();
   }

   public void doCheck(Call call) {
      ThreadContext context = ThreadContext.getThreadContext();
      if (!this.isAsyncOperation(context)) {
         this.check(context, call);
      }

   }

   public EJBHome getEJBHome() {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      return di.getEJBHome();
   }

   public EJBLocalHome getEJBLocalHome() {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      return di.getEJBLocalHome();
   }

   public Principal getCallerPrincipal() {
      this.doCheck(BaseContext.Call.getCallerPrincipal);
      Principal callerPrincipal = this.getCallerPrincipal(this.securityService);
      if (callerPrincipal == null) {
         callerPrincipal = UnauthenticatedPrincipal.INSTANCE;
      }

      return callerPrincipal;
   }

   protected Principal getCallerPrincipal(SecurityService securityService) {
      return securityService.getCallerPrincipal();
   }

   public boolean isCallerInRole(String s) {
      this.doCheck(BaseContext.Call.isCallerInRole);
      return this.isCallerInRole(this.securityService, s);
   }

   protected boolean isCallerInRole(SecurityService securityService, String roleName) {
      this.doCheck(BaseContext.Call.isCallerInRole);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      String roleLink = di.getSecurityRoleReference(roleName);
      return securityService.isCallerInRole(roleLink);
   }

   public UserTransaction getUserTransaction() throws IllegalStateException {
      this.doCheck(BaseContext.Call.getUserTransaction);
      return this.getUserTransaction(this.userTransaction);
   }

   public UserTransaction getUserTransaction(UserTransaction userTransaction) throws IllegalStateException {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      if (di.isBeanManagedTransaction()) {
         return userTransaction;
      } else {
         throw new IllegalStateException("container-managed transaction beans can not access the UserTransaction");
      }
   }

   public void setRollbackOnly() throws IllegalStateException {
      this.doCheck(BaseContext.Call.setRollbackOnly);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      if (di.isBeanManagedTransaction()) {
         throw new IllegalStateException("bean-managed transaction beans can not access the setRollbackOnly() method");
      } else {
         TransactionPolicy txPolicy = threadContext.getTransactionPolicy();
         if (txPolicy == null) {
            throw new IllegalStateException("ThreadContext does not contain a TransactionEnvironment");
         } else if (txPolicy.getTransactionType() != TransactionType.Never && txPolicy.getTransactionType() != TransactionType.NotSupported && txPolicy.getTransactionType() != TransactionType.Supports) {
            txPolicy.setRollbackOnly();
         } else {
            throw new IllegalStateException("setRollbackOnly accessible only from MANDATORY, REQUIRED, or REQUIRES_NEW");
         }
      }
   }

   public boolean getRollbackOnly() throws IllegalStateException {
      this.doCheck(BaseContext.Call.getRollbackOnly);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      if (di.isBeanManagedTransaction()) {
         throw new IllegalStateException("bean-managed transaction beans can not access the getRollbackOnly() method: deploymentId=" + di.getDeploymentID());
      } else {
         TransactionPolicy txPolicy = threadContext.getTransactionPolicy();
         if (txPolicy == null) {
            throw new IllegalStateException("ThreadContext does not contain a TransactionEnvironment");
         } else if (txPolicy.getTransactionType() != TransactionType.Never && txPolicy.getTransactionType() != TransactionType.NotSupported && txPolicy.getTransactionType() != TransactionType.Supports) {
            return txPolicy.isRollbackOnly();
         } else {
            throw new IllegalStateException("getRollbackOnly accessible only from MANDATORY, REQUIRED, or REQUIRES_NEW");
         }
      }
   }

   public TimerService getTimerService() throws IllegalStateException {
      this.doCheck(BaseContext.Call.getTimerService);
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext beanContext = threadContext.getBeanContext();
      EjbTimerService timerService = beanContext.getEjbTimerService();
      if (timerService == null) {
         throw new IllegalStateException("This ejb does not support timers " + beanContext.getDeploymentID());
      } else {
         if (!timerService.isStarted()) {
            try {
               timerService.start();
            } catch (OpenEJBException e) {
               throw new IllegalStateException(e);
            }
         }

         return new TimerServiceImpl(timerService, threadContext.getPrimaryKey(), beanContext.getEjbTimeout()) {
            public Collection<Timer> getAllTimers() throws IllegalStateException, EJBException {
               return Timers.all();
            }
         };
      }
   }

   public boolean isTimerMethodAllowed() {
      return true;
   }

   public boolean isUserTransactionAccessAllowed() {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext di = threadContext.getBeanContext();
      this.doCheck(BaseContext.Call.UserTransactionMethod);
      return di.isBeanManagedTransaction();
   }

   public final Properties getEnvironment() {
      throw new UnsupportedOperationException();
   }

   public final Identity getCallerIdentity() {
      throw new UnsupportedOperationException();
   }

   public final boolean isCallerInRole(Identity identity) {
      throw new UnsupportedOperationException();
   }

   public Object lookup(String name) {
      ThreadContext threadContext = ThreadContext.getThreadContext();
      BeanContext beanContext = threadContext.getBeanContext();
      Context jndiEnc = beanContext.getJndiEnc();

      try {
         jndiEnc = (Context)jndiEnc.lookup("comp/env");
         return jndiEnc.lookup(name);
      } catch (RuntimeException | NamingException e) {
         throw new IllegalArgumentException(e);
      }
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new IntraVmArtifact(this, true);
   }

   public static enum Call {
      getEJBObject,
      getEJBLocalObject,
      isCallerInRole,
      setRollbackOnly,
      getCallerPrincipal,
      getRollbackOnly,
      getTimerService,
      getUserTransaction,
      getBusinessObject,
      timerMethod,
      getInvokedBusinessInterface,
      UserTransactionMethod,
      getMessageContext,
      getPrimaryKey,
      getContextData;
   }

   public class UserTransactionWrapper implements UserTransaction {
      private final UserTransaction userTransaction;

      public UserTransactionWrapper(UserTransaction userTransaction) {
         this.userTransaction = userTransaction;
      }

      public void begin() throws NotSupportedException, SystemException {
         if (!BaseContext.this.isUserTransactionAccessAllowed()) {
            throw new IllegalStateException();
         } else {
            this.userTransaction.begin();
         }
      }

      public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException {
         if (!BaseContext.this.isUserTransactionAccessAllowed()) {
            throw new IllegalStateException();
         } else {
            this.userTransaction.commit();
         }
      }

      public int getStatus() throws SystemException {
         if (!BaseContext.this.isUserTransactionAccessAllowed()) {
            throw new IllegalStateException();
         } else {
            return this.userTransaction.getStatus();
         }
      }

      public void rollback() throws IllegalStateException, SecurityException, SystemException {
         if (!BaseContext.this.isUserTransactionAccessAllowed()) {
            throw new IllegalStateException();
         } else {
            this.userTransaction.rollback();
         }
      }

      public void setRollbackOnly() throws IllegalStateException, SystemException {
         if (!BaseContext.this.isUserTransactionAccessAllowed()) {
            throw new IllegalStateException();
         } else {
            this.userTransaction.setRollbackOnly();
         }
      }

      public void setTransactionTimeout(int i) throws SystemException {
         if (!BaseContext.this.isUserTransactionAccessAllowed()) {
            throw new IllegalStateException();
         } else {
            this.userTransaction.setTransactionTimeout(i);
         }
      }
   }

   public static class State {
   }
}
