package org.apache.meecrowave.cxf;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.jws.WebService;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.AbstractEndpointFactory;
import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.logging.tomcat.LogFacade;
import org.apache.webbeans.component.OwbBean;

public class JAXWSCdiExtension implements Extension {
   private boolean active;
   private final List<Bean<?>> serviceBeans = new ArrayList();
   private final Collection<Runnable> preShutdownTasks = new ArrayList();

   public JAXWSCdiExtension() {
      try {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         loader.loadClass("org.apache.cxf.jaxws.JaxWsServerFactoryBean");
         loader.loadClass("org.apache.cxf.service.model.SchemaInfo");
         this.active = true;
      } catch (ClassNotFoundException | NoClassDefFoundError var2) {
         this.active = false;
      }

   }

   public <T> void collect(@Observes ProcessBean<T> processBean) {
      if (this.active && processBean.getAnnotated().isAnnotationPresent(WebService.class)) {
         this.serviceBeans.add(processBean.getBean());
      }

   }

   public void load(@Observes AfterDeploymentValidation afterDeploymentValidation, BeanManager beanManager) {
      if (this.active && !this.serviceBeans.isEmpty() && ((Meecrowave.Builder)Meecrowave.Builder.class.cast(beanManager.getReference(beanManager.resolve(beanManager.getBeans(Meecrowave.Builder.class, new Annotation[0])), Meecrowave.Builder.class, (CreationalContext)null))).isJaxwsSupportIfAvailable()) {
         Bean<?> busBean = beanManager.resolve(beanManager.getBeans("cxf"));
         Bus bus = (Bus)Bus.class.cast(beanManager.getReference(busBean, Bus.class, beanManager.createCreationalContext(busBean)));
         Bean<?> mapperBean = beanManager.resolve(beanManager.getBeans(JAXWSAddressMapper.class, new Annotation[0]));
         JAXWSAddressMapper mapper;
         if (mapperBean == null) {
            mapper = (type) -> {
               WsMapping wsMapping = (WsMapping)type.getAnnotation(WsMapping.class);
               return wsMapping != null ? wsMapping.value() : "/webservices/" + type.getSimpleName();
            };
         } else {
            mapper = (JAXWSAddressMapper)JAXWSAddressMapper.class.cast(beanManager.getReference(mapperBean, JAXWSAddressMapper.class, beanManager.createCreationalContext(mapperBean)));
         }

         this.serviceBeans.forEach((bean) -> {
            Class<?> beanClass = OwbBean.class.isInstance(bean) ? ((OwbBean)OwbBean.class.cast(bean)).getReturnType() : bean.getBeanClass();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            try {
               AbstractEndpointFactory aef = (AbstractEndpointFactory)AbstractEndpointFactory.class.cast(loader.loadClass("org.apache.cxf.jaxws.JaxWsServerFactoryBean").getConstructor().newInstance());
               aef.setBus(bus);
               aef.setAddress(mapper.map(beanClass));
               Class<? extends AbstractEndpointFactory> factoryClass = aef.getClass();
               factoryClass.getMethod("setStart", Boolean.TYPE).invoke(aef, true);
               factoryClass.getMethod("setServiceClass", Class.class).invoke(aef, beanClass);
               CreationalContext<Object> creationalContext = beanManager.createCreationalContext((Contextual)null);
               if (!beanManager.isNormalScope(bean.getScope())) {
                  this.preShutdownTasks.add(creationalContext::release);
               }

               factoryClass.getMethod("setServiceBean", Object.class).invoke(aef, beanManager.getReference(bean, Object.class, creationalContext));
               Object server = factoryClass.getMethod("create").invoke(aef);
               Class<?> serverClass = server.getClass();
               serverClass.getMethod("start").invoke(server);
               this.preShutdownTasks.add((Runnable)() -> {
                  try {
                     serverClass.getMethod("destroy").invoke(server);
                  } catch (NoSuchMethodException | IllegalAccessException | NoClassDefFoundError e) {
                     throw new IllegalStateException(e);
                  } catch (InvocationTargetException e) {
                     throw new IllegalStateException(e.getCause());
                  }
               });
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | NoClassDefFoundError e) {
               throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
               throw new IllegalStateException(e.getCause());
            }
         });
         this.serviceBeans.clear();
      }
   }

   public void release(@Observes BeforeShutdown beforeShutdown) {
      this.preShutdownTasks.stream().map((r) -> () -> {
            try {
               r.run();
            } catch (RuntimeException re) {
               (new LogFacade(JAXWSCdiExtension.class.getName())).warn(re.getMessage(), re);
            }

         }).forEach(Runnable::run);
      this.preShutdownTasks.clear();
   }
}
