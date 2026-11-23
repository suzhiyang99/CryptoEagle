package org.apache.openejb.junit;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.web.lifecycle.test.MockHttpSession;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ScopesRule implements TestRule {
   public Statement apply(final Statement base, final Description description) {
      return new Statement() {
         public void evaluate() throws Throwable {
            CdiScopes annotation = (CdiScopes)description.getAnnotation(CdiScopes.class);
            if (annotation == null) {
               base.evaluate();
            } else {
               WebBeansContext wbc = WebBeansContext.currentInstance();
               Map<Class<?>, Object> init = new HashMap();
               Class<? extends Annotation>[] scopes = annotation.value();

               for(Class<? extends Annotation> c : scopes) {
                  Object o = ScopesRule.this.param(c);
                  if (o != null) {
                     init.put(c, o);
                  }

                  wbc.getContextsService().startContext(c, o);
               }

               boolean var16 = false;

               try {
                  var16 = true;
                  base.evaluate();
                  var16 = false;
               } finally {
                  if (var16) {
                     for(Class<? extends Annotation> c : scopes) {
                        wbc.getContextsService().endContext(c, init.get(c));
                     }

                  }
               }

               for(Class<? extends Annotation> c : scopes) {
                  wbc.getContextsService().endContext(c, init.get(c));
               }

            }
         }
      };
   }

   private Object param(Class<?> c) {
      return SessionScoped.class == c ? new MockHttpSession() : null;
   }
}
