package org.apache.openejb.junit;

import org.apache.openejb.testing.ApplicationComposers;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ApplicationComposerRule implements TestRule {
   private final Object instance;
   private final Object[] modules;

   public ApplicationComposerRule(Object instance, Object... modules) {
      this.instance = instance;
      if (modules == null) {
         this.modules = new Object[0];
      } else {
         this.modules = modules;
      }

   }

   public <T> T getInstance(Class<T> as) {
      return (T)as.cast(this.instance);
   }

   public Object[] getModules() {
      return this.modules;
   }

   public Statement apply(Statement base, Description description) {
      return new DeployApplication(this.instance, base, new ApplicationComposers(this.instance.getClass(), this.modules));
   }
}
