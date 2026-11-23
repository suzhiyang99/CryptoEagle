package org.apache.openejb.core.entity;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;

public class EntrancyTracker {
   private final ThreadLocal<Set<InstanceKey>> inCallThreadLocal = new ThreadLocal<Set<InstanceKey>>() {
      protected Set<InstanceKey> initialValue() {
         return new HashSet();
      }
   };
   private final TransactionSynchronizationRegistry synchronizationRegistry;

   public EntrancyTracker(TransactionSynchronizationRegistry synchronizationRegistry) {
      this.synchronizationRegistry = synchronizationRegistry;
   }

   public void enter(BeanContext beanContext, Object primaryKey) throws ApplicationException {
      if (primaryKey != null && !beanContext.isReentrant()) {
         Object deploymentId = beanContext.getDeploymentID();
         InstanceKey key = new InstanceKey(deploymentId, primaryKey);

         Set<InstanceKey> inCall;
         try {
            inCall = (Set)this.synchronizationRegistry.getResource(EntrancyTracker.class);
            if (inCall == null) {
               inCall = new HashSet();
               this.synchronizationRegistry.putResource(EntrancyTracker.class, inCall);
            }
         } catch (IllegalStateException var7) {
            inCall = (Set)this.inCallThreadLocal.get();
         }

         if (!inCall.add(key)) {
            ApplicationException exception = new ApplicationException(new RemoteException("Attempted reentrant access. Bean " + deploymentId + " is not reentrant and instance " + primaryKey + " has already been entered : " + inCall));
            exception.printStackTrace();
            throw exception;
         }
      }
   }

   public void exit(BeanContext beanContext, Object primaryKey) throws ApplicationException {
      if (primaryKey != null && !beanContext.isReentrant()) {
         Object deploymentId = beanContext.getDeploymentID();
         InstanceKey key = new InstanceKey(deploymentId, primaryKey);
         Set<InstanceKey> inCall = null;

         try {
            inCall = (Set)this.synchronizationRegistry.getResource(EntrancyTracker.class);
         } catch (IllegalStateException var7) {
            inCall = (Set)this.inCallThreadLocal.get();
         }

         if (inCall != null) {
            inCall.remove(key);
         }

      }
   }

   private static class InstanceKey {
      private final Object deploymentId;
      private final Object primaryKey;

      public InstanceKey(Object deploymentId, Object primaryKey) {
         this.deploymentId = deploymentId;
         this.primaryKey = primaryKey;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            InstanceKey that = (InstanceKey)o;
            return this.deploymentId.equals(that.deploymentId) && this.primaryKey.equals(that.primaryKey);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.deploymentId.hashCode();
         result = 31 * result + this.primaryKey.hashCode();
         return result;
      }

      public String toString() {
         return this.deploymentId + ":" + this.primaryKey;
      }
   }
}
