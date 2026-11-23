package org.apache.deltaspike.core.impl.scope.viewaccess;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class ViewAccessBeanAccessHistory {
   private final List<String> accessedBeans = new ArrayList();

   public List<String> getAccessedBeans() {
      return this.accessedBeans;
   }
}
