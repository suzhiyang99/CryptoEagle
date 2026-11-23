package org.apache.openejb.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import javax.enterprise.inject.spi.Bean;
import org.apache.webbeans.container.InjectionResolver;

public class WebAppInjectionResolver extends InjectionResolver {
   private final WebappWebBeansContext context;

   public WebAppInjectionResolver(WebappWebBeansContext ctx) {
      super(ctx);
      this.context = ctx;
   }

   public Set<Bean<?>> implResolveByType(boolean delegate, Type injectionPointType, Class<?> injectinPointClass, Annotation... qualifiers) {
      Set<Bean<?>> set = super.implResolveByType(delegate, injectionPointType, injectinPointClass, qualifiers);
      return set.isEmpty() && this.context.getParent() != null ? this.context.getParent().getBeanManagerImpl().getInjectionResolver().implResolveByType(delegate, injectionPointType, injectinPointClass, qualifiers) : set;
   }
}
