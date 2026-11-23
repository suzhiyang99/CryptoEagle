package org.apache.deltaspike.core.impl.scope.window;

import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.config.base.CoreBaseConfig.ScopeCustomization.WindowRestriction;
import org.apache.deltaspike.core.spi.scope.window.WindowContextQuotaHandler;

@SessionScoped
public class DefaultWindowContextQuotaHandler implements WindowContextQuotaHandler {
   protected int maxWindowContextCount;
   @Inject
   private WindowContextQuotaHandlerCache quotaHandlerCache;
   private Stack<String> windowIdStack = new Stack();

   @PostConstruct
   protected void init() {
      this.maxWindowContextCount = WindowRestriction.MAX_COUNT;
   }

   public synchronized void checkWindowContextQuota(String windowId) {
      if (windowId != null) {
         if (!this.quotaHandlerCache.cacheWindowId(windowId)) {
            if (this.windowIdStack.contains(windowId)) {
               if (this.windowIdStack.size() > 1) {
                  this.windowIdStack.remove(windowId);
                  this.windowIdStack.push(windowId);
               }
            } else {
               this.windowIdStack.push(windowId);
               if (this.windowIdStack.size() > this.maxWindowContextCount) {
                  String windowIdToRemove = (String)this.windowIdStack.remove(0);
                  this.quotaHandlerCache.setWindowIdToDestroy(windowIdToRemove);
               }
            }

         }
      }
   }
}
