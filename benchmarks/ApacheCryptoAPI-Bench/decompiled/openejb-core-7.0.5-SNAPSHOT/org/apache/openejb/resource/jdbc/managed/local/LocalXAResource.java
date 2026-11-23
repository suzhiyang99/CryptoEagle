package org.apache.openejb.resource.jdbc.managed.local;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class LocalXAResource implements XAResource {
   private final Connection connection;
   private Xid currentXid;
   private boolean originalAutoCommit;
   private final Lock lock = new ReentrantLock();

   public LocalXAResource(Connection localTransaction) {
      this.connection = localTransaction;
   }

   public Xid getXid() {
      this.checkLock();
      return this.currentXid;
   }

   public void start(Xid xid, int flag) throws XAException {
      try {
         this.lock.tryLock(10L, TimeUnit.MINUTES);
      } catch (InterruptedException var6) {
         throw (XAException)(new XAException("can't get lock")).initCause(this.cantGetLock());
      }

      if (flag == 0) {
         if (this.currentXid != null) {
            throw new XAException("Already enlisted in another transaction with xid " + xid);
         }

         try {
            this.originalAutoCommit = this.connection.getAutoCommit();
         } catch (SQLException var5) {
            this.originalAutoCommit = true;
         }

         try {
            this.connection.setAutoCommit(false);
         } catch (SQLException e) {
            throw (XAException)(new XAException("Count not turn off auto commit for a XA transaction")).initCause(e);
         }

         this.currentXid = xid;
      } else {
         if (flag != 134217728) {
            throw new XAException("Unknown start flag " + flag);
         }

         if (xid != this.currentXid) {
            throw new XAException("Attempting to resume in different transaction: expected " + this.currentXid + ", but was " + xid);
         }
      }

   }

   private RuntimeException cantGetLock() {
      return new IllegalStateException("can't get lock on resource with Xid " + this.currentXid + " from thread " + Thread.currentThread().getName());
   }

   public void end(Xid xid, int flag) throws XAException {
      try {
         if (xid == null) {
            throw new NullPointerException("xid is null");
         }

         if (!this.currentXid.equals(xid)) {
            throw new XAException("Invalid Xid: expected " + this.currentXid + ", but was " + xid);
         }
      } finally {
         this.lock.unlock();
      }

   }

   public int prepare(Xid xid) {
      this.checkLock();

      try {
         if (this.connection.isReadOnly()) {
            this.connection.setAutoCommit(this.originalAutoCommit);
            return 3;
         }
      } catch (SQLException var3) {
      }

      return 0;
   }

   public void commit(Xid xid, boolean flag) throws XAException {
      this.checkLock();
      if (xid == null) {
         throw new NullPointerException("xid is null");
      } else if (!this.currentXid.equals(xid)) {
         throw new XAException("Invalid Xid: expected " + this.currentXid + ", but was " + xid);
      } else {
         try {
            if (this.connection.isClosed()) {
               throw new XAException("Conection is closed");
            }

            if (!this.connection.isReadOnly()) {
               this.connection.commit();
            }
         } catch (SQLException e) {
            throw (XAException)(new XAException()).initCause(e);
         } finally {
            try {
               this.connection.setAutoCommit(this.originalAutoCommit);
            } catch (SQLException var10) {
            }

            this.currentXid = null;
         }

      }
   }

   public void rollback(Xid xid) throws XAException {
      this.checkLock();
      if (xid == null) {
         throw new NullPointerException("xid is null");
      } else if (!this.currentXid.equals(xid)) {
         throw new XAException("Invalid Xid: expected " + this.currentXid + ", but was " + xid);
      } else {
         try {
            this.connection.rollback();
         } catch (SQLException e) {
            throw (XAException)(new XAException()).initCause(e);
         } finally {
            try {
               this.connection.setAutoCommit(this.originalAutoCommit);
            } catch (SQLException var9) {
            }

            this.currentXid = null;
         }

      }
   }

   public boolean isSameRM(XAResource xaResource) {
      return this == xaResource;
   }

   public void forget(Xid xid) {
      this.checkLock();
      if (xid != null && this.currentXid.equals(xid)) {
         this.currentXid = null;
      }

   }

   public Xid[] recover(int flag) {
      return new Xid[0];
   }

   public int getTransactionTimeout() {
      return 0;
   }

   public boolean setTransactionTimeout(int transactionTimeout) {
      return false;
   }

   private void checkLock() {
      if (!this.lock.tryLock()) {
         throw this.cantGetLock();
      }
   }
}
