package org.apache.openejb.assembler.classic;

public class EjbReferenceInfo extends InjectableInfo {
   public String homeClassName;
   public String interfaceClassName;
   public String ejbDeploymentId;
   public boolean ambiguous;
   public boolean externalReference;
   public String link;
   public boolean localbean;
}
