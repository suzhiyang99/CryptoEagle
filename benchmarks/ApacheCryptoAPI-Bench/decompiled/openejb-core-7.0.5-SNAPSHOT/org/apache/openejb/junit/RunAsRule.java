package org.apache.openejb.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.security.RunAs;
import org.apache.openejb.BeanContext;
import org.apache.openejb.core.ThreadContext;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RunAsRule extends BeanContextBaseRule implements TestRule {
   private final ThreadLocal<String> role;

   public RunAsRule() {
      this((Object)null);
   }

   public RunAsRule(Object o) {
      super(o);
      this.role = new ThreadLocal();
   }

   public void role(String role) {
      this.role.set(role);
   }

   public Statement apply(final Statement base, final Description description) {
      return new Statement() {
         public void evaluate() throws Throwable {
            RunAs annotation = (RunAs)description.getAnnotation(RunAs.class);
            As as = (As)description.getAnnotation(As.class);
            String currentRole = (String)RunAsRule.this.role.get();
            RunAsRule.this.role.remove();
            if (annotation == null && as == null && currentRole == null) {
               base.evaluate();
            } else {
               BeanContext beanContext = RunAsRule.this.getBeanContext();
               if (currentRole == null) {
                  if (annotation == null) {
                     currentRole = as.value();
                  } else {
                     currentRole = annotation.value();
                  }
               }

               String runAs = beanContext.getRunAs();
               String runAsUser = beanContext.getRunAsUser();
               beanContext.setRunAs(currentRole);
               ThreadContext old = ThreadContext.enter(new ThreadContext(beanContext, (Object)null));

               try {
                  base.evaluate();
               } finally {
                  ThreadContext.exit(old);
                  beanContext.setRunAs(runAs);
                  beanContext.setRunAsUser(runAsUser);
               }

            }
         }
      };
   }

   @Target({ElementType.METHOD, ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface As {
      String value();
   }
}
