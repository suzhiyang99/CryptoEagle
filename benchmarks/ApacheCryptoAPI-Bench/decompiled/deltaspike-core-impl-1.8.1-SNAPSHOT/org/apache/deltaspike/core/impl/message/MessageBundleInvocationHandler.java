package org.apache.deltaspike.core.impl.message;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.api.message.LocaleResolver;
import org.apache.deltaspike.core.api.message.Message;
import org.apache.deltaspike.core.api.message.MessageContext;
import org.apache.deltaspike.core.api.message.MessageContextConfig;
import org.apache.deltaspike.core.api.message.MessageInterpolator;
import org.apache.deltaspike.core.api.message.MessageResolver;
import org.apache.deltaspike.core.api.message.MessageTemplate;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.ClassUtils;

@Dependent
@Typed({MessageBundleInvocationHandler.class})
public class MessageBundleInvocationHandler implements InvocationHandler, Serializable {
   private static final long serialVersionUID = -8980912335543392357L;
   @Inject
   private MessageContext baseMessageContext = null;

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass().equals(Object.class)) {
         if ("hashCode".equals(method.getName())) {
            return proxy.getClass().hashCode();
         } else if ("toString".equals(method.getName())) {
            return proxy.getClass().toString();
         } else {
            return "equals".equals(method.getName()) ? proxy.getClass().equals(args[0].getClass()) : null;
         }
      } else {
         MessageTemplate messageTemplate = (MessageTemplate)method.getAnnotation(MessageTemplate.class);
         String messageTemplateValue;
         if (messageTemplate != null) {
            messageTemplateValue = messageTemplate.value();
         } else {
            messageTemplateValue = "{" + method.getName() + "}";
         }

         MessageContext messageContext = this.resolveMessageContextFromArguments(args);
         List<Serializable> arguments = this.resolveMessageArguments(args);
         if (messageContext == null) {
            messageContext = this.baseMessageContext.clone();
            MessageContextConfig messageContextConfig = (MessageContextConfig)method.getDeclaringClass().getAnnotation(MessageContextConfig.class);
            if (messageContextConfig != null) {
               this.applyMessageContextConfig(messageContext, messageContextConfig);
            }
         }

         String messageBundleName = method.getDeclaringClass().getName();
         Message message = messageContext.messageSource(new String[]{messageBundleName}).message().template(messageTemplateValue).argument((Serializable[])arguments.toArray(new Serializable[arguments.size()]));
         return String.class.isAssignableFrom(method.getReturnType()) ? message.toString() : message;
      }
   }

   private void applyMessageContextConfig(MessageContext messageContext, MessageContextConfig messageContextConfig) {
      if (!MessageResolver.class.equals(messageContextConfig.messageResolver())) {
         Class<? extends MessageResolver> messageResolverClass = ClassUtils.tryToLoadClassForName(messageContextConfig.messageResolver().getName());
         messageContext.messageResolver((MessageResolver)BeanProvider.getContextualReference(messageResolverClass, new Annotation[]{new AnyLiteral()}));
      }

      if (!MessageInterpolator.class.equals(messageContextConfig.messageInterpolator())) {
         Class<? extends MessageInterpolator> messageInterpolatorClass = ClassUtils.tryToLoadClassForName(messageContextConfig.messageInterpolator().getName());
         messageContext.messageInterpolator((MessageInterpolator)BeanProvider.getContextualReference(messageInterpolatorClass, new Annotation[]{new AnyLiteral()}));
      }

      if (!LocaleResolver.class.equals(messageContextConfig.localeResolver())) {
         Class<? extends LocaleResolver> localeResolverClass = ClassUtils.tryToLoadClassForName(messageContextConfig.localeResolver().getName());
         messageContext.localeResolver((LocaleResolver)BeanProvider.getContextualReference(localeResolverClass, new Annotation[]{new AnyLiteral()}));
      }

      String[] messageSources = messageContextConfig.messageSource();
      messageContext.messageSource(messageSources);
   }

   private List<Serializable> resolveMessageArguments(Object[] args) {
      List<Serializable> arguments = new ArrayList();
      if (args != null && args.length > 0) {
         for(int i = 0; i < args.length; ++i) {
            Object arg = args[i];
            if (i != 0 || arg == null || !MessageContext.class.isAssignableFrom(arg.getClass())) {
               if (arg == null) {
                  arguments.add("'null'");
               } else if (arg instanceof Serializable) {
                  arguments.add((Serializable)arg);
               } else {
                  arguments.add(arg.toString());
               }
            }
         }
      }

      return arguments;
   }

   private MessageContext resolveMessageContextFromArguments(Object[] args) {
      return args != null && args.length > 0 && args[0] != null && MessageContext.class.isAssignableFrom(args[0].getClass()) ? (MessageContext)args[0] : null;
   }
}
