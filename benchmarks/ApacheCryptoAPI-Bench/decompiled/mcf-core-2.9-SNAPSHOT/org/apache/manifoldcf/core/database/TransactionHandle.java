package org.apache.manifoldcf.core.database;

import org.apache.manifoldcf.core.interfaces.IDFactory;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class TransactionHandle {
   public static final String _rcsid = "@(#)$Id: TransactionHandle.java 988245 2010-08-23 18:39:35Z kwright $";
   protected TransactionHandle previousTransaction;
   protected String transactionID;
   protected int transactionType;

   public TransactionHandle(IThreadContext tc, TransactionHandle previousTransaction, int transactionType) throws ManifoldCFException {
      this.transactionID = IDFactory.make(tc);
      this.previousTransaction = previousTransaction;
      this.transactionType = transactionType;
   }

   public String getTransactionID() {
      return this.transactionID;
   }

   public TransactionHandle getParent() {
      return this.previousTransaction;
   }

   public int getTransactionType() {
      return this.transactionType;
   }
}
