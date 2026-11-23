package org.apache.openejb.resource.jdbc;

import javax.annotation.PostConstruct;
import org.apache.openejb.resource.jdbc.router.FailOverRouter;
import org.apache.openejb.resource.jdbc.router.Router;

public class FailoverDataSource extends RoutedDataSource {
   public FailoverDataSource() {
      super((Router)(new FailOverRouter()));
   }

   private FailOverRouter failoverRouter() {
      return (FailOverRouter)FailOverRouter.class.cast(this.delegate);
   }

   @PostConstruct
   private void init() {
      this.failoverRouter().init();
   }

   public void setDatasourceNames(String datasourceNames) {
      this.failoverRouter().setDatasourceNames(datasourceNames);
   }

   public void setDelimiter(String delimiter) {
      this.failoverRouter().setDelimiter(delimiter);
   }

   public void setStrategy(String strategy) {
      this.failoverRouter().setStrategy(strategy);
   }

   public void setStrategyInstance(FailOverRouter.Strategy strategy) {
      this.failoverRouter().setStrategyInstance(strategy);
   }

   public void setExceptionSelectorInstance(FailOverRouter.ExceptionSelector selector) {
      this.failoverRouter().setExceptionSelectorInstance(selector);
   }

   public void setExceptionSelector(String selector) {
      this.failoverRouter().setExceptionSelector(selector);
   }

   public void setErrorHandlerInstance(FailOverRouter.ErrorHandler errorHandler) {
      this.failoverRouter().setErrorHandlerInstance(errorHandler);
   }

   public void setErrorHandler(String errorHandler) {
      this.failoverRouter().setErrorHandler(errorHandler);
   }
}
