package org.apache.openejb.assembler.classic;

import java.net.URI;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.core.ivm.naming.CrossClassLoaderJndiReference;
import org.apache.openejb.core.ivm.naming.IntraVmJndiReference;
import org.apache.openejb.core.ivm.naming.Reference;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Messages;

public class LazyEjbReference extends Reference {
   private static final Messages messages = new Messages(LazyEjbReference.class);
   private final EjbResolver.Reference info;
   private Reference reference;
   private final URI moduleUri;
   private final boolean useCrossClassLoaderRef;

   public LazyEjbReference(EjbResolver.Reference info, URI moduleUri, boolean useCrossClassLoaderRef) {
      this.info = info;
      this.moduleUri = moduleUri;
      this.useCrossClassLoaderRef = useCrossClassLoaderRef;
   }

   public Object getObject() throws NamingException {
      if (this.reference != null) {
         return this.reference.getObject();
      } else {
         SystemInstance systemInstance = SystemInstance.get();
         EjbResolver resolver = (EjbResolver)systemInstance.getComponent(EjbResolver.class);
         String deploymentId = resolver.resolve(this.info, this.moduleUri);
         if (deploymentId == null) {
            String key = "lazyEjbRefNotResolved";
            if (this.info.getHome() != null) {
               key = key + ".home";
            }

            String message = messages.format(key, this.info.getName(), this.info.getEjbLink(), this.info.getHome(), this.info.getInterface());
            throw new NameNotFoundException(message);
         } else {
            ContainerSystem containerSystem = (ContainerSystem)systemInstance.getComponent(ContainerSystem.class);
            BeanContext beanContext = containerSystem.getBeanContext(deploymentId);
            if (beanContext == null) {
               String message = messages.format("deploymentNotFound", this.info.getName(), deploymentId);
               throw new NameNotFoundException(message);
            } else {
               InterfaceType type = null;
               switch (this.info.getRefType()) {
                  case LOCAL:
                     type = InterfaceType.BUSINESS_LOCAL;
                     break;
                  case REMOTE:
                     type = InterfaceType.BUSINESS_REMOTE;
               }

               String jndiName = "openejb/Deployment/" + JndiBuilder.format(deploymentId, this.info.getInterface(), type);
               if (this.useCrossClassLoaderRef && this.isRemote(beanContext)) {
                  this.reference = new CrossClassLoaderJndiReference(jndiName);
               } else {
                  this.reference = new IntraVmJndiReference(jndiName);
               }

               return this.reference.getObject();
            }
         }
      }
   }

   private boolean isRemote(BeanContext beanContext) {
      switch (this.info.getRefType()) {
         case LOCAL:
            return false;
         case REMOTE:
            return true;
         case UNKNOWN:
            for(Class clazz : beanContext.getInterfaces(InterfaceType.BUSINESS_REMOTE)) {
               if (clazz.getName().equals(this.info.getInterface())) {
                  return true;
               }
            }
         default:
            return false;
      }
   }
}
