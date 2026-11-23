package org.apache.openejb.cdi;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.SingletonService;

public interface ThreadSingletonService extends SingletonService<WebBeansContext> {
   void initialize(StartupObject var1);

   Object contextEntered(WebBeansContext var1);

   void contextExited(Object var1);
}
