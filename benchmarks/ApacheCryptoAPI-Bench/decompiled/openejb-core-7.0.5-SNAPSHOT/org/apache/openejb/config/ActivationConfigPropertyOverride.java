package org.apache.openejb.config;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.Container;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.mdb.MdbContainer;
import org.apache.openejb.jee.ActivationConfig;
import org.apache.openejb.jee.ActivationConfigProperty;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.MessageDrivenBean;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.PropertyPlaceHolderHelper;

public class ActivationConfigPropertyOverride implements DynamicDeployer {
   private static final Logger logger;

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      Properties system = new Properties();
      system.putAll(SystemInstance.get().getProperties());
      system.putAll(appModule.getProperties());
      system.putAll(JavaSecurityManagers.getSystemProperties());

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         EjbJar ejbJar = ejbModule.getEjbJar();
         OpenejbJar openejbJar = ejbModule.getOpenejbJar();
         Properties module = new Properties();
         module.putAll(openejbJar.getProperties());
         module.putAll(system);
         Map<String, EjbDeployment> deployments = openejbJar.getDeploymentsByEjbName();

         for(EnterpriseBean bean : ejbJar.getEnterpriseBeans()) {
            String ejbName = bean.getEjbName();
            EjbDeployment ejbDeployment = (EjbDeployment)deployments.get(ejbName);
            if (bean instanceof MessageDrivenBean) {
               Properties properties = new Properties();
               properties.putAll(module);
               properties.putAll(ejbDeployment.getProperties());
               MessageDrivenBean mdb = (MessageDrivenBean)bean;
               if (mdb.getActivationConfig() != null) {
                  for(ActivationConfigProperty property : mdb.getActivationConfig().getActivationConfigProperty()) {
                     String originalValue = property.getActivationConfigPropertyValue();
                     String value = PropertyPlaceHolderHelper.simpleValue(originalValue);
                     if (value != null && !originalValue.equals(value)) {
                        property.setActivationConfigPropertyValue(value);
                     }
                  }
               }

               Properties overrides = new Properties();
               MdbContainer mdbContainer = this.getMdbContainer(ejbDeployment.getContainerId());
               if (mdbContainer != null) {
                  overrides.putAll(ConfigurationFactory.getOverrides(properties, "mdb.container." + mdbContainer.getContainerID() + ".activation", "EnterpriseBean"));
                  overrides.putAll(ConfigurationFactory.getOverrides(mdbContainer.getProperties(), "activation", "EnterpriseBean"));
               }

               overrides.putAll(ConfigurationFactory.getOverrides(properties, "mdb.activation", "EnterpriseBean"));
               overrides.putAll(ConfigurationFactory.getOverrides(properties, mdb.getMessagingType() + ".activation", "EnterpriseBean"));
               overrides.putAll(ConfigurationFactory.getOverrides(properties, ejbName + ".activation", "EnterpriseBean"));
               overrides.putAll(ConfigurationFactory.getOverrides(properties, ejbDeployment.getDeploymentId() + ".activation", "EnterpriseBean"));
               if (overrides.size() != 0) {
                  if (mdb.getActivationConfig() == null) {
                     mdb.setActivationConfig(new ActivationConfig());
                  }

                  List<ActivationConfigProperty> activationConfigList = mdb.getActivationConfig().getActivationConfigProperty();

                  for(Map.Entry<Object, Object> entry : overrides.entrySet()) {
                     Object property = String.valueOf(entry.getKey());
                     Object value = String.valueOf(entry.getValue());
                     ActivationConfigProperty activationConfigProperty = this.findActivationProperty(activationConfigList, property.toString());
                     if (activationConfigProperty != null) {
                        logger.info(String.format("Found %s bean with activation-config property %s=%s to override", ejbName, activationConfigProperty.getActivationConfigPropertyName(), activationConfigProperty.getActivationConfigPropertyValue()));
                        logger.info(String.format("Overriding %s bean activation-config property.%s=%s", ejbName, property, value));
                        activationConfigProperty.setActivationConfigPropertyValue(entry.getValue().toString());
                     } else {
                        logger.info(String.format("Adding %s bean activation-config property %s=%s", ejbName, property, value));
                        activationConfigProperty = new ActivationConfigProperty();
                        activationConfigProperty.setActivationConfigPropertyName(property.toString());
                        activationConfigProperty.setActivationConfigPropertyValue(value.toString());
                        activationConfigList.add(activationConfigProperty);
                     }
                  }
               }
            }
         }
      }

      return appModule;
   }

   private MdbContainer getMdbContainer(String containerId) {
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      if (containerId != null && containerId.length() != 0) {
         Container container = containerSystem.getContainer(containerId);
         return MdbContainer.class.isInstance(container) ? (MdbContainer)MdbContainer.class.cast(container) : null;
      } else {
         Container[] containers = containerSystem.containers();

         for(Container container : containers) {
            if (MdbContainer.class.isInstance(container)) {
               return (MdbContainer)MdbContainer.class.cast(container);
            }
         }

         return null;
      }
   }

   private ActivationConfigProperty findActivationProperty(List<ActivationConfigProperty> activationConfigList, String nameOfProperty) {
      for(ActivationConfigProperty activationProp : activationConfigList) {
         if (activationProp.getActivationConfigPropertyName().equals(nameOfProperty)) {
            return activationProp;
         }
      }

      return null;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, ActivationConfigPropertyOverride.class);
   }
}
