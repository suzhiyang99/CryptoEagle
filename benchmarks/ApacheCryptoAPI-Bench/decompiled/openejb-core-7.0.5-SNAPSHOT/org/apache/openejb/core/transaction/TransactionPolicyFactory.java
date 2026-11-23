package org.apache.openejb.core.transaction;

import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;

public interface TransactionPolicyFactory {
   TransactionPolicy createTransactionPolicy(TransactionType var1) throws SystemException, ApplicationException;
}
