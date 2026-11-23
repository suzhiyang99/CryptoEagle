package org.apache.openejb;

public interface Container {
   ContainerType getContainerType();

   Object getContainerID();

   BeanContext getBeanContext(Object var1);

   BeanContext[] getBeanContexts();

   void deploy(BeanContext var1) throws OpenEJBException;

   void start(BeanContext var1) throws OpenEJBException;

   void stop(BeanContext var1) throws OpenEJBException;

   void undeploy(BeanContext var1) throws OpenEJBException;
}
