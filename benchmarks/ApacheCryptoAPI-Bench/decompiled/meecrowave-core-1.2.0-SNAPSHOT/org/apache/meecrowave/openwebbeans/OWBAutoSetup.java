package org.apache.meecrowave.openwebbeans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.configurator.BeanConfigurator;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.cxf.JAXRSFieldInjectionInterceptor;
import org.apache.webbeans.annotation.DefaultLiteral;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.configurator.BeanConfiguratorImpl;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.intercept.InterceptorsManager;
import org.apache.webbeans.servlet.WebBeansConfigurationListener;
import org.apache.webbeans.web.context.WebConversationFilter;

public class OWBAutoSetup implements ServletContainerInitializer {
   public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
      Meecrowave.Builder builder = (Meecrowave.Builder)Meecrowave.Builder.class.cast(ctx.getAttribute("meecrowave.configuration"));
      Meecrowave instance = (Meecrowave)Meecrowave.class.cast(ctx.getAttribute("meecrowave.instance"));
      if (builder.isCdiConversation()) {
         FilterRegistration.Dynamic filter = ctx.addFilter("owb-conversation", WebConversationFilter.class);
         filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, new String[]{"/*"});
      }

      EagerBootListener bootListener = new EagerBootListener(instance);
      bootListener.doContextInitialized(new ServletContextEvent(ctx));
      ctx.addListener(bootListener);
   }

   public static class EagerBootListener extends WebBeansConfigurationListener implements Extension {
      private final Meecrowave meecrowave;

      private EagerBootListener(Meecrowave meecrowave) {
         this.meecrowave = meecrowave;
      }

      public void contextInitialized(ServletContextEvent event) {
      }

      private void doContextInitialized(ServletContextEvent event) {
         try {
            WebBeansContext instance = WebBeansContext.getInstance();
            this.customizeContext(instance);
         } catch (IllegalStateException var3) {
         }

         super.contextInitialized(event);
      }

      private void customizeContext(WebBeansContext instance) {
         BeanManagerImpl beanManager = instance.getBeanManagerImpl();
         InterceptorsManager interceptorsManager = instance.getInterceptorsManager();
         beanManager.addInternalBean(this.newBean(instance, (configurator) -> configurator.beanClass(Meecrowave.Builder.class).scope(ApplicationScoped.class).qualifiers(new Annotation[]{DefaultLiteral.INSTANCE}).types(new Type[]{Meecrowave.Builder.class, Object.class}).createWith((cc) -> this.meecrowave.getConfiguration())));
         beanManager.addInternalBean(this.newBean(instance, (configurator) -> configurator.beanClass(Meecrowave.class).scope(ApplicationScoped.class).qualifiers(new Annotation[]{DefaultLiteral.INSTANCE}).types(new Type[]{Meecrowave.class, AutoCloseable.class, Object.class}).createWith((cc) -> this.meecrowave)));
         interceptorsManager.addInterceptorBindingType(JAXRSFieldInjectionInterceptor.Binding.class, new Annotation[0]);
         beanManager.addAdditionalAnnotatedType(this, beanManager.createAnnotatedType(JAXRSFieldInjectionInterceptor.class));
      }

      private <T> Bean<?> newBean(WebBeansContext instance, Consumer<BeanConfigurator<T>> configurer) {
         BeanConfiguratorImpl<T> meecrowaveBeanBuilder = new BeanConfiguratorImpl(instance);
         configurer.accept(meecrowaveBeanBuilder);
         return meecrowaveBeanBuilder.getBean();
      }
   }
}
