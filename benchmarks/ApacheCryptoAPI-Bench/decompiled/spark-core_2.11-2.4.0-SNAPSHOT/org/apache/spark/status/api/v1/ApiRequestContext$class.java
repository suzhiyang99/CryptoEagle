package org.apache.spark.status.api.v1;

public abstract class ApiRequestContext$class {
   public static UIRoot uiRoot(ApiRequestContext $this) {
      return UIRootFromServletContext$.MODULE$.getUiRoot($this.servletContext());
   }

   public static void $init$(ApiRequestContext $this) {
   }
}
