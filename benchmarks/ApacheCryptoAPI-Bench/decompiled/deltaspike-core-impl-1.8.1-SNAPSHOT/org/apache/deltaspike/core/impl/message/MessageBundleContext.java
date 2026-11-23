package org.apache.deltaspike.core.impl.message;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.Bean;

@Typed
abstract class MessageBundleContext {
   private static final ThreadLocal<Bean> MESSAGE_BUNDLE_BEAN = new ThreadLocal();

   private MessageBundleContext() {
   }

   static void setBean(Bean bean) {
      MESSAGE_BUNDLE_BEAN.set(bean);
   }

   static void reset() {
      MESSAGE_BUNDLE_BEAN.set((Object)null);
      MESSAGE_BUNDLE_BEAN.remove();
   }

   static Bean getCurrentMessageBundleBean() {
      return (Bean)MESSAGE_BUNDLE_BEAN.get();
   }
}
