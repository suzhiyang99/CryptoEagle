package org.apache.manifoldcf.core.i18n;

import java.io.InputStream;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class MCFVelocityResourceLoader extends ResourceLoader {
   protected Class classInstance;

   public MCFVelocityResourceLoader(Class classInstance) {
      this.classInstance = classInstance;
   }

   public long getLastModified(Resource resource) {
      return 0L;
   }

   public InputStream getResourceStream(String source) throws ResourceNotFoundException {
      InputStream rval = this.classInstance.getResourceAsStream(source);
      if (rval == null) {
         throw new ResourceNotFoundException("Resource '" + source + "' not found.");
      } else {
         return rval;
      }
   }

   public void init(ExtendedProperties configuration) {
   }

   public boolean isSourceModified(Resource resource) {
      return true;
   }
}
