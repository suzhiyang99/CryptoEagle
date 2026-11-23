package org.apache.openejb.core.timer;

import java.io.Serializable;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import org.apache.openejb.BeanContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class TimerHandleImpl implements TimerHandle, Serializable {
   private static final long serialVersionUID = 9198316188404706377L;
   private final long id;
   private final String deploymentId;

   public TimerHandleImpl(long id, String deploymentId) {
      this.id = id;
      this.deploymentId = deploymentId;
   }

   public Timer getTimer() {
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      if (containerSystem == null) {
         throw new NoSuchObjectLocalException("OpenEJb container system is not running");
      } else {
         BeanContext beanContext = containerSystem.getBeanContext(this.deploymentId);
         if (beanContext == null) {
            throw new NoSuchObjectLocalException("Deployment info not found " + this.deploymentId);
         } else {
            EjbTimerService timerService = beanContext.getEjbTimerService();
            if (timerService == null) {
               throw new NoSuchObjectLocalException("Deployment no longer supports ejbTimout " + this.deploymentId + ". Has this ejb been redeployed?");
            } else {
               Timer timer = timerService.getTimer(this.id);
               if (timer == null) {
                  throw new NoSuchObjectLocalException("Timer not found for ejb " + this.deploymentId);
               } else {
                  return timer;
               }
            }
         }
      }
   }
}
