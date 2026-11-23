package org.apache.openejb.junit;

import java.util.concurrent.Callable;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.testing.ApplicationComposers;
import org.junit.runners.model.Statement;

public class DeployApplication extends Statement {
   private final Object testInstance;
   private final Statement next;
   private final ApplicationComposers delegate;

   public DeployApplication(Object testInstance, Statement next, ApplicationComposers delegate) {
      this.testInstance = testInstance;
      this.next = next;
      this.delegate = delegate;
   }

   public void evaluate() throws Throwable {
      this.delegate.evaluate(this.testInstance, new Callable<Void>() {
         public Void call() throws Exception {
            try {
               DeployApplication.this.next.evaluate();
               return null;
            } catch (Error e) {
               throw e;
            } catch (Throwable throwable) {
               if (throwable instanceof Exception) {
                  throw (Exception)throwable;
               } else {
                  throw new OpenEJBRuntimeException("Failed test evaluation", throwable);
               }
            }
         }
      });
   }
}
