package org.apache.openejb.assembler.classic.event;

import org.apache.openejb.api.resource.DestroyableResource;
import org.apache.openejb.observer.Event;

@Event
public class ResourceBeforeDestroyed extends ResourceEvent {
   public ResourceBeforeDestroyed(Object resource, String name) {
      super(name, resource);
   }

   public void replaceBy(Object newResource) {
      if (DestroyableResource.class.isInstance(this.resource)) {
         ((DestroyableResource)DestroyableResource.class.cast(this.resource)).destroyResource();
      }

      super.replaceBy(newResource);
   }
}
