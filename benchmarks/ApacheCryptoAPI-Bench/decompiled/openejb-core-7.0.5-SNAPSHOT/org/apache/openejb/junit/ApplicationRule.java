package org.apache.openejb.junit;

import java.util.Objects;
import org.apache.openejb.testing.ApplicationComposers;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ApplicationRule implements TestRule {
   private final Object instance;

   public ApplicationRule(Object instance) {
      this.instance = Objects.requireNonNull(instance);
   }

   public <T> T getInstance(Class<T> as) {
      return (T)as.cast(this.instance);
   }

   public Statement apply(final Statement statement, Description description) {
      return new Statement() {
         public void evaluate() throws Throwable {
            Thread thread = Thread.currentThread();
            ClassLoader old = thread.getContextClassLoader();
            ApplicationComposers composers = new ApplicationOnlyApplicationComposers(ApplicationRule.this.instance);
            composers.deployApp(ApplicationRule.this.instance);

            try {
               statement.evaluate();
            } finally {
               composers.stopApplication();
               thread.setContextClassLoader(old);
            }

         }
      };
   }

   public static class ApplicationOnlyApplicationComposers extends ApplicationComposers {
      public ApplicationOnlyApplicationComposers(Object modules) {
         super(modules);
      }

      protected boolean isContainer() {
         return false;
      }

      protected boolean mockCdiContexts() {
         return false;
      }
   }
}
