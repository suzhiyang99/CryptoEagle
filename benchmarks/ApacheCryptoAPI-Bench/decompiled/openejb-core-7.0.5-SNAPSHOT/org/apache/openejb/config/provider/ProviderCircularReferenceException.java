package org.apache.openejb.config.provider;

import java.util.Collection;
import java.util.Set;
import org.apache.openejb.util.Join;

public class ProviderCircularReferenceException extends IllegalStateException {
   private final Set<ID> ids;

   public ProviderCircularReferenceException(Set<ID> ids) {
      super(String.format("Circular reference: %s -> %s", Join.join(" -> ", (Collection)ids), ids.iterator().next()));
      this.ids = ids;
   }

   public Set<ID> getIds() {
      return this.ids;
   }
}
