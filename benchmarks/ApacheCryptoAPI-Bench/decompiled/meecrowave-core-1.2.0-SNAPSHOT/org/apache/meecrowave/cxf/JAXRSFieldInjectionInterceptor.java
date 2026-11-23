package org.apache.meecrowave.cxf;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Priority;
import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Application;
import org.apache.cxf.jaxrs.model.ApplicationInfo;
import org.apache.cxf.jaxrs.model.MethodInvocationInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfoStack;
import org.apache.cxf.jaxrs.provider.ProviderFactory;
import org.apache.cxf.jaxrs.utils.InjectionUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.webbeans.annotation.EmptyAnnotationLiteral;
import org.apache.webbeans.intercept.ConstructorInterceptorInvocationContext;

@Interceptor
@Priority(0)
@JAXRSFieldInjectionInterceptor.Binding
public class JAXRSFieldInjectionInterceptor implements Serializable {
   private final AtomicBoolean injected = new AtomicBoolean();

   @AroundConstruct
   public Object injectContexts(InvocationContext ic) throws Exception {
      this.doInject(ic);
      return ic.proceed();
   }

   @AroundInvoke
   public Object lazyInjectContexts(InvocationContext ic) throws Exception {
      if (!this.injected.get()) {
         this.doInject(ic);
      }

      return ic.proceed();
   }

   private void doInject(InvocationContext ic) throws Exception {
      Message current = JAXRSUtils.getCurrentMessage();
      if (current != null) {
         OperationResourceInfoStack stack = (OperationResourceInfoStack)OperationResourceInfoStack.class.cast(current.get(OperationResourceInfoStack.class.getName()));
         if (stack != null && !stack.isEmpty()) {
            Object instance;
            if (ConstructorInterceptorInvocationContext.class.isInstance(ic)) {
               ConstructorInterceptorInvocationContext constructorInterceptorInvocationContext = (ConstructorInterceptorInvocationContext)ConstructorInterceptorInvocationContext.class.cast(ic);
               constructorInterceptorInvocationContext.directProceed();
               instance = constructorInterceptorInvocationContext.getNewInstance();
            } else {
               instance = ic.getTarget();
            }

            Application application = null;
            Object appInfo = current.getExchange().getEndpoint().get(Application.class.getName());
            if (ApplicationInfo.class.isInstance(appInfo)) {
               application = (Application)((ApplicationInfo)ApplicationInfo.class.cast(appInfo)).getProvider();
            }

            synchronized(this) {
               if (this.injected.get()) {
                  return;
               }

               InjectionUtils.injectContextProxiesAndApplication(((MethodInvocationInfo)stack.lastElement()).getMethodInfo().getClassResourceInfo(), instance, application, ProviderFactory.getInstance(current));
               this.injected.compareAndSet(false, true);
            }
         }
      }

   }

   @Target({ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   @InterceptorBinding
   public @interface Binding {
      Annotation INSTANCE = new EmptyAnnotationLiteral<Binding>() {
         public Class<? extends Annotation> annotationType() {
            return Binding.class;
         }
      };
   }
}
