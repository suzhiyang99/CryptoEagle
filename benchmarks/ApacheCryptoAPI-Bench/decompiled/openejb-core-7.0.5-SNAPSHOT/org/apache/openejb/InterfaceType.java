package org.apache.openejb;

public enum InterfaceType {
   EJB_HOME("Home"),
   EJB_OBJECT("Remote"),
   EJB_LOCAL_HOME("LocalHome"),
   EJB_LOCAL("Local"),
   BUSINESS_LOCAL("Local"),
   LOCALBEAN("LocalBean"),
   BUSINESS_LOCAL_HOME("LocalHome"),
   BUSINESS_REMOTE("Remote"),
   BUSINESS_REMOTE_HOME("Home"),
   SERVICE_ENDPOINT("ServiceEndpoint"),
   BUSINESS_LOCALBEAN_HOME("LocalBeanHome"),
   TIMEOUT("Timeout"),
   UNKNOWN("Unknown");

   private final String specName;

   private InterfaceType(String name) {
      this.specName = name;
   }

   public String getSpecName() {
      return this.specName;
   }

   public boolean isHome() {
      switch (this) {
         case EJB_HOME:
            return true;
         case EJB_LOCAL_HOME:
            return true;
         case BUSINESS_LOCAL_HOME:
            return true;
         case BUSINESS_LOCALBEAN_HOME:
            return true;
         case BUSINESS_REMOTE_HOME:
            return true;
         default:
            return false;
      }
   }

   public boolean isComponent() {
      switch (this) {
         case EJB_OBJECT:
            return true;
         case EJB_LOCAL:
            return true;
         default:
            return false;
      }
   }

   public boolean isBusiness() {
      switch (this) {
         case BUSINESS_LOCAL:
            return true;
         case BUSINESS_REMOTE:
            return true;
         case LOCALBEAN:
            return true;
         default:
            return false;
      }
   }

   public boolean isRemote() {
      switch (this) {
         case EJB_HOME:
            return true;
         case EJB_LOCAL_HOME:
         case BUSINESS_LOCAL_HOME:
         case BUSINESS_LOCALBEAN_HOME:
         case EJB_LOCAL:
         case BUSINESS_LOCAL:
         default:
            return false;
         case BUSINESS_REMOTE_HOME:
            return true;
         case EJB_OBJECT:
            return true;
         case BUSINESS_REMOTE:
            return true;
      }
   }

   public boolean isLocal() {
      switch (this) {
         case EJB_LOCAL_HOME:
            return true;
         case BUSINESS_LOCAL_HOME:
            return true;
         case BUSINESS_LOCALBEAN_HOME:
         case BUSINESS_REMOTE_HOME:
         case EJB_OBJECT:
         case BUSINESS_REMOTE:
         default:
            return false;
         case EJB_LOCAL:
            return true;
         case BUSINESS_LOCAL:
            return true;
         case LOCALBEAN:
            return true;
      }
   }

   public boolean isLocalBean() {
      switch (this) {
         case BUSINESS_LOCALBEAN_HOME:
            return true;
         case LOCALBEAN:
            return true;
         default:
            return false;
      }
   }

   public InterfaceType getCounterpart() {
      switch (this) {
         case EJB_HOME:
            return EJB_OBJECT;
         case EJB_LOCAL_HOME:
            return EJB_LOCAL;
         case BUSINESS_LOCAL_HOME:
            return BUSINESS_LOCAL;
         case BUSINESS_LOCALBEAN_HOME:
            return LOCALBEAN;
         case BUSINESS_REMOTE_HOME:
            return BUSINESS_REMOTE;
         case EJB_OBJECT:
            return EJB_HOME;
         case EJB_LOCAL:
            return EJB_LOCAL_HOME;
         case BUSINESS_LOCAL:
            return BUSINESS_LOCAL_HOME;
         case BUSINESS_REMOTE:
            return BUSINESS_REMOTE_HOME;
         case LOCALBEAN:
            return BUSINESS_LOCALBEAN_HOME;
         default:
            throw new IllegalArgumentException("InterfaceType has no counterpart: " + this);
      }
   }
}
