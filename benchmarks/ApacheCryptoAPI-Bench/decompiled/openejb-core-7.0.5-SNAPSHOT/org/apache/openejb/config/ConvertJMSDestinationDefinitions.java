package org.apache.openejb.config;

import java.util.List;
import java.util.Properties;
import javax.jms.ConnectionFactory;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.jee.JMSDestination;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.KeyedCollection;
import org.apache.openejb.jee.Property;
import org.apache.openejb.util.PropertyPlaceHolderHelper;

public class ConvertJMSDestinationDefinitions extends BaseConvertDefinitions {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      List<JndiConsumer> jndiConsumers = this.collectConsumers(appModule);
      KeyedCollection<String, JMSDestination> destinations = new KeyedCollection();

      for(JndiConsumer consumer : jndiConsumers) {
         if (consumer != null) {
            destinations.addAll(consumer.getJMSDestination());
         }
      }

      for(JMSDestination destination : destinations) {
         appModule.getResources().add(this.toResource(destination));
      }

      return appModule;
   }

   private Resource toResource(JMSDestination factory) {
      String name = this.cleanUpName(factory.getName());
      Resource factoryResource = new Resource(name, ConnectionFactory.class.getName());
      factoryResource.setJndi(factory.getName().replaceFirst("java:", ""));
      factoryResource.setType(factory.getInterfaceName() != null && !factory.getInterfaceName().isEmpty() ? factory.getInterfaceName() : "javax.jms.Queue");
      if (factory.getClassName() != null && !factory.getClassName().isEmpty()) {
         factoryResource.setClassName(factory.getClassName());
      }

      Properties p = factoryResource.getProperties();
      put(p, "Origin", "Annotation");
      put(p, "JndiName", factoryResource.getJndi());
      put(p, "Destination", factory.getDestinationName());
      if (factory.getResourceAdapter() != null && !factory.getResourceAdapter().isEmpty()) {
         put(p, "ResourceAdapter", factory.getResourceAdapter());
      }

      this.setProperties(factory, p);
      return factoryResource;
   }

   private void setProperties(JMSDestination d, Properties p) {
      for(Property property : d.getProperty()) {
         String key = property.getName();
         String value = property.getValue();
         put(p, key, value);
      }

   }

   private static void put(Properties properties, String key, Object value) {
      if (key != null && value != null) {
         properties.put(key, PropertyPlaceHolderHelper.value(String.valueOf(value)));
      }

   }
}
