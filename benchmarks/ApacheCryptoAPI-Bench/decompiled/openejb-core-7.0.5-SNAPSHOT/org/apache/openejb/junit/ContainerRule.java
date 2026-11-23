package org.apache.openejb.junit;

import java.util.Objects;
import org.apache.openejb.testing.ApplicationComposers;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ContainerRule implements TestRule {
   private final Object instance;

   public ContainerRule(Object instance) {
      this.instance = Objects.requireNonNull(instance);
   }

   public <T> T getInstance(Class<T> as) {
      return (T)as.cast(this.instance);
   }

   public Statement apply(final Statement statement, Description description) {
      return new Statement() {
         public void evaluate() throws Throwable {
            ApplicationComposers composers = new ContainerApplicationComposers(ContainerRule.this.instance);
            composers.startContainer(ContainerRule.this.instance);

            try {
               statement.evaluate();
            } finally {
               composers.after();
            }

         }
      };
   }

   public static class ContainerApplicationComposers extends ApplicationComposers {
      public ContainerApplicationComposers(Object modules) {
         super(modules);
      }

      protected boolean isApplication() {
         return false;
      }
   }
}
