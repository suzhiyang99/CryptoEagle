package org.apache.openejb.testng;

import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.testing.ApplicationComposers;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class ApplicationComposerListener implements IInvokedMethodListener {
   private ApplicationComposers delegate;

   public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
      this.delegate = new ApplicationComposers(method.getTestMethod().getRealClass(), new Object[0]);

      try {
         this.delegate.before(testResult.getInstance());
      } catch (Exception e) {
         throw new OpenEJBRuntimeException(e);
      }
   }

   public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
      try {
         this.delegate.after();
      } catch (Exception e) {
         throw new OpenEJBRuntimeException(e);
      }
   }
}
