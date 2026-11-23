package org.apache.openejb.core.webservices;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.Bean;
import javax.naming.Context;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.PortInfo;
import org.apache.openejb.Injection;
import org.apache.openejb.InjectionProcessor;
import org.apache.openejb.util.AppFinder;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.context.creational.CreationalContextImpl;

public class HandlerResolverImpl implements HandlerResolver {
   private static final Logger LOGGER;
   private final List<HandlerChainData> handlerChains;
   private final Collection<Injection> injections;
   private final Context context;
   private final List<InjectionProcessor<Handler>> handlerInstances = new ArrayList();

   public HandlerResolverImpl(List<HandlerChainData> handlerChains, Collection<Injection> injections, Context context) {
      this.handlerChains = handlerChains;
      this.injections = injections;
      this.context = context;
   }

   public void destroyHandlers() {
      List<InjectionProcessor<Handler>> handlerInstances = new ArrayList(this.handlerInstances);
      this.handlerInstances.clear();

      for(InjectionProcessor<Handler> handlerInstance : handlerInstances) {
         handlerInstance.preDestroy();
      }

   }

   public List<Handler> getHandlerChain(PortInfo portInfo) {
      List<Handler> chain = new ArrayList();

      for(HandlerChainData handlerChain : this.handlerChains) {
         List<Handler> handlers = this.buildHandlers(portInfo, handlerChain);
         handlers = this.sortHandlers(handlers);
         chain.addAll(handlers);
      }

      chain = this.sortHandlers(chain);
      return chain;
   }

   private List<Handler> buildHandlers(PortInfo portInfo, HandlerChainData handlerChain) {
      if (this.matchServiceName(portInfo, handlerChain.getServiceNamePattern()) && this.matchPortName(portInfo, handlerChain.getPortNamePattern()) && this.matchBinding(portInfo, handlerChain.getProtocolBindings())) {
         List<Handler> handlers = new ArrayList(handlerChain.getHandlers().size());
         Iterator var4 = handlerChain.getHandlers().iterator();

         while(true) {
            HandlerData handler;
            while(true) {
               if (!var4.hasNext()) {
                  return handlers;
               }

               handler = (HandlerData)var4.next();
               WebBeansContext webBeansContext = (WebBeansContext)AppFinder.findAppContextOrWeb(Thread.currentThread().getContextClassLoader(), AppFinder.WebBeansContextTransformer.INSTANCE);
               if (webBeansContext == null) {
                  break;
               }

               BeanManagerImpl bm = webBeansContext.getBeanManagerImpl();
               if (!bm.isInUse()) {
                  break;
               }

               try {
                  Set<Bean<?>> beans = bm.getBeans(handler.getHandlerClass(), new Annotation[0]);
                  Bean<?> bean = bm.resolve(beans);
                  if (bean == null) {
                     break;
                  }

                  final boolean normalScoped = bm.isNormalScope(bean.getScope());
                  final CreationalContextImpl<?> creationalContext = bm.createCreationalContext(bean);
                  Handler instance = (Handler)Handler.class.cast(bm.getReference(bean, bean.getBeanClass(), creationalContext));
                  handlers.add(instance);
                  this.handlerInstances.add(new InjectionProcessor<Handler>(instance, Collections.emptySet(), (Context)null) {
                     public void preDestroy() {
                        if (!normalScoped) {
                           creationalContext.release();
                        }

                     }
                  });
               } catch (InjectionException ie) {
                  LOGGER.info(ie.getMessage(), (Throwable)ie);
                  break;
               }
            }

            try {
               Class<? extends Handler> handlerClass = handler.getHandlerClass().asSubclass(Handler.class);
               InjectionProcessor<Handler> processor = new InjectionProcessor<Handler>(handlerClass, this.injections, handler.getPostConstruct(), handler.getPreDestroy(), InjectionProcessor.unwrap(this.context));
               processor.createInstance();
               processor.postConstruct();
               Handler handlerInstance = processor.getInstance();
               handlers.add(handlerInstance);
               this.handlerInstances.add(processor);
            } catch (Exception e) {
               throw new WebServiceException("Failed to instantiate handler", e);
            }
         }
      } else {
         return Collections.emptyList();
      }
   }

   private boolean matchServiceName(PortInfo info, QName namePattern) {
      return this.match(info == null ? null : info.getServiceName(), namePattern);
   }

   private boolean matchPortName(PortInfo info, QName namePattern) {
      return this.match(info == null ? null : info.getPortName(), namePattern);
   }

   private boolean matchBinding(PortInfo info, List bindings) {
      return this.match(info == null ? null : info.getBindingID(), bindings);
   }

   private boolean match(String binding, List bindings) {
      if (binding != null) {
         if (bindings != null && !bindings.isEmpty()) {
            String actualBindingURI = JaxWsUtils.getBindingURI(binding);

            for(String bindingToken : bindings) {
               String bindingURI = JaxWsUtils.getBindingURI(bindingToken);
               if (actualBindingURI.equals(bindingURI)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      } else {
         return bindings == null || bindings.isEmpty();
      }
   }

   private boolean match(QName name, QName namePattern) {
      if (name != null) {
         if (namePattern == null) {
            return true;
         } else if (namePattern.getNamespaceURI() != null && !name.getNamespaceURI().equals(namePattern.getNamespaceURI())) {
            return false;
         } else {
            String localNamePattern = namePattern.getLocalPart();
            if (localNamePattern.equals("*")) {
               return true;
            } else if (localNamePattern.endsWith("*")) {
               localNamePattern = localNamePattern.substring(0, localNamePattern.length() - 1);
               return name.getLocalPart().startsWith(localNamePattern);
            } else {
               return name.getLocalPart().equals(localNamePattern);
            }
         }
      } else {
         return namePattern == null || namePattern.getLocalPart().equals("*");
      }
   }

   private List<Handler> sortHandlers(List<Handler> handlers) {
      List<LogicalHandler> logicalHandlers = new ArrayList();
      List<Handler> protocolHandlers = new ArrayList();

      for(Handler handler : handlers) {
         if (handler instanceof LogicalHandler) {
            logicalHandlers.add((LogicalHandler)handler);
         } else {
            protocolHandlers.add(handler);
         }
      }

      List<Handler> sortedHandlers = new ArrayList();
      sortedHandlers.addAll(logicalHandlers);
      sortedHandlers.addAll(protocolHandlers);
      return sortedHandlers;
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB_WS, HandlerResolverImpl.class);
   }
}
