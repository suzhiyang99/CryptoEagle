package org.apache.openejb.assembler.classic.event;

import org.apache.openejb.observer.Event;

@Event
public class ResourceCreated extends ResourceEvent {
   public ResourceCreated(Object resource, String name) {
      super(name, resource);
   }
}
