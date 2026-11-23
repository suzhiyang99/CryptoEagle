package org.apache.openejb.assembler.classic;

import java.util.ArrayList;
import java.util.List;

public class JndiEncInfo extends InfoObject {
   public final List<EnvEntryInfo> envEntries = new ArrayList();
   public final List<EjbReferenceInfo> ejbReferences = new ArrayList();
   public final List<EjbLocalReferenceInfo> ejbLocalReferences = new ArrayList();
   public final List<ResourceReferenceInfo> resourceRefs = new ArrayList();
   public final List<PersistenceUnitReferenceInfo> persistenceUnitRefs = new ArrayList();
   public final List<PersistenceContextReferenceInfo> persistenceContextRefs = new ArrayList();
   public final List<ResourceEnvReferenceInfo> resourceEnvRefs = new ArrayList();
   public final List<ServiceReferenceInfo> serviceRefs = new ArrayList();
}
