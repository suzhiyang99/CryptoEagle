package org.apache.openejb.junit;

import java.util.List;
import org.apache.openejb.testing.ApplicationComposers;
import org.junit.rules.MethodRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class ApplicationComposer extends BlockJUnit4ClassRunner {
   private final ApplicationComposers delegate;

   public ApplicationComposer(Class<?> klass) throws InitializationError {
      super(klass);
      this.delegate = new ApplicationComposers(klass, new Object[0]);
   }

   protected List<MethodRule> rules(Object test) {
      List<MethodRule> rules = super.rules(test);
      rules.add(new MethodRule() {
         public Statement apply(Statement base, FrameworkMethod method, Object target) {
            return new DeployApplication(target, base, ApplicationComposer.this.delegate);
         }
      });
      return rules;
   }
}
