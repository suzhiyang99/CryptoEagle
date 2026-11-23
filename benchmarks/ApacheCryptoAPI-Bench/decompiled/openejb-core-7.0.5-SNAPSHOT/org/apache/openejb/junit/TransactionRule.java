package org.apache.openejb.junit;

import java.lang.reflect.Method;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Transactional;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.transaction.EjbTransactionUtil;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.transaction.TransactionType;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TransactionRule extends BeanContextBaseRule implements TestRule {
   private final boolean rollback;

   public TransactionRule() {
      this((Object)null, true);
   }

   public TransactionRule(boolean rollback) {
      this((Object)null, rollback);
   }

   public TransactionRule(Object o, boolean rollback) {
      super(o);
      this.rollback = rollback;
   }

   public Statement apply(final Statement base, final Description description) {
      return new Statement() {
         public void evaluate() throws Throwable {
            TransactionAttribute annotation = (TransactionAttribute)description.getAnnotation(TransactionAttribute.class);
            Transactional annotation2 = (Transactional)description.getAnnotation(Transactional.class);
            if (annotation == null && annotation2 == null) {
               base.evaluate();
            } else {
               BeanContext beanContext = TransactionRule.this.getBeanContext();
               Method method = beanContext.getManagedClass().getMethod(description.getMethodName());
               TransactionType transactionType = TransactionType.get(annotation == null ? TransactionAttributeType.valueOf(annotation2.value().name()) : annotation.value());
               beanContext.getMethodContext(method).setTransactionType(transactionType);
               ThreadContext tc = ThreadContext.getThreadContext();
               boolean tcCreated;
               if (tc == null) {
                  tcCreated = true;
                  tc = ThreadContext.enter(new ThreadContext(beanContext, (Object)null));
               } else {
                  tcCreated = false;
               }

               TransactionPolicy policy = EjbTransactionUtil.createTransactionPolicy(transactionType, tc);

               try {
                  base.evaluate();
               } finally {
                  if (TransactionRule.this.rollback) {
                     policy.setRollbackOnly();
                  }

                  EjbTransactionUtil.afterInvoke(policy, tc);
                  if (tcCreated) {
                     ThreadContext.exit(tc);
                  }

               }

            }
         }
      };
   }
}
