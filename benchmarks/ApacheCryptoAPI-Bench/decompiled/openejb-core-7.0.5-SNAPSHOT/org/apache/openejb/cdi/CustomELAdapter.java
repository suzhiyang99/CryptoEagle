package org.apache.openejb.cdi;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import org.apache.openejb.AppContext;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.el22.WebBeansELResolver;
import org.apache.webbeans.el22.WrappedExpressionFactory;
import org.apache.webbeans.spi.adaptor.ELAdaptor;

public class CustomELAdapter implements ELAdaptor {
   private final AppContext appContext;

   public CustomELAdapter(AppContext appContext) {
      this.appContext = appContext;
   }

   public ELResolver getOwbELResolver() {
      WebBeansContext old = null;
      boolean exit = false;

      try {
         WebBeansContext.currentInstance();
      } catch (IllegalStateException var7) {
         old = ThreadSingletonServiceImpl.enter(this.appContext.getWebBeansContext());
         exit = true;
      }

      WebBeansELResolver ise;
      try {
         ise = new WebBeansELResolver();
      } finally {
         if (exit) {
            ThreadSingletonServiceImpl.exit(old);
         }

      }

      return ise;
   }

   public ExpressionFactory getOwbWrappedExpressionFactory(ExpressionFactory expressionFactory) {
      return (ExpressionFactory)(!this.appContext.isCdiEnabled() ? expressionFactory : new WrappedExpressionFactory(expressionFactory));
   }
}
