package org.apache.openejb.assembler.classic.event;

import java.util.Collections;
import java.util.List;
import org.apache.openejb.BeanContext;
import org.apache.openejb.observer.Event;

@Event
public class BeforeStartEjbs {
   private final List<BeanContext> ejbs;

   public BeforeStartEjbs(List<BeanContext> allDeployments) {
      this.ejbs = allDeployments;
   }

   public List<BeanContext> getEjbs() {
      return Collections.unmodifiableList(this.ejbs);
   }

   public String toString() {
      return "BeforeStartEjbs{#ejbs=" + this.ejbs.size() + '}';
   }
}
