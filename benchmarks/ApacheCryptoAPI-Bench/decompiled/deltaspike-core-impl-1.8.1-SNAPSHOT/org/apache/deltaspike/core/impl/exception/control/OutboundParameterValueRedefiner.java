package org.apache.deltaspike.core.impl.exception.control;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apache.deltaspike.core.util.metadata.builder.ParameterValueRedefiner;

class OutboundParameterValueRedefiner implements ParameterValueRedefiner {
   private final ExceptionEvent<?> event;
   private final Bean<?> declaringBean;
   private final HandlerMethodImpl<?> handlerMethod;

   OutboundParameterValueRedefiner(ExceptionEvent<?> event, HandlerMethodImpl<?> handlerMethod) {
      this.event = event;
      this.declaringBean = handlerMethod.getDeclaringBean();
      this.handlerMethod = handlerMethod;
   }

   public Object redefineParameterValue(ParameterValueRedefiner.ParameterValue value) {
      CreationalContext<?> ctx = BeanManagerProvider.getInstance().getBeanManager().createCreationalContext(this.declaringBean);

      ExceptionEvent var3;
      try {
         if (value.getPosition() != this.handlerMethod.getHandlerParameter().getPosition()) {
            Object var7 = value.getDefaultValue(ctx);
            return var7;
         }

         var3 = this.event;
      } finally {
         if (ctx != null) {
            ctx.release();
         }

      }

      return var3;
   }
}
