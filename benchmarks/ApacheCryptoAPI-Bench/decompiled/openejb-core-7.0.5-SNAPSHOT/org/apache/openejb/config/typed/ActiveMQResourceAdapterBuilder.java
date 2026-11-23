package org.apache.openejb.config.typed;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.typed.util.Builders;
import org.apache.openejb.config.typed.util.DurationAdapter;
import org.apache.openejb.util.Duration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "ActiveMQResourceAdapter"
)
public class ActiveMQResourceAdapterBuilder extends Resource {
   @XmlAttribute
   private String brokerXmlConfig = "broker:(tcp://localhost:61616)?useJmx=false";
   @XmlAttribute
   private URI serverUrl = URI.create("vm://localhost?waitForStart=20000&async=true");
   @XmlAttribute
   private String dataSource = "Default Unmanaged JDBC Database";
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration startupTimeout = Duration.parse("10 seconds");

   public ActiveMQResourceAdapterBuilder() {
      this.setClassName("org.apache.openejb.resource.activemq.ActiveMQResourceAdapter");
      this.setType("ActiveMQResourceAdapter");
      this.setId("ActiveMQResourceAdapter");
   }

   public ActiveMQResourceAdapterBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public ActiveMQResourceAdapterBuilder withBrokerXmlConfig(String brokerXmlConfig) {
      this.brokerXmlConfig = brokerXmlConfig;
      return this;
   }

   public void setBrokerXmlConfig(String brokerXmlConfig) {
      this.brokerXmlConfig = brokerXmlConfig;
   }

   public String getBrokerXmlConfig() {
      return this.brokerXmlConfig;
   }

   public ActiveMQResourceAdapterBuilder withServerUrl(URI serverUrl) {
      this.serverUrl = serverUrl;
      return this;
   }

   public void setServerUrl(URI serverUrl) {
      this.serverUrl = serverUrl;
   }

   public URI getServerUrl() {
      return this.serverUrl;
   }

   public ActiveMQResourceAdapterBuilder withDataSource(String dataSource) {
      this.dataSource = dataSource;
      return this;
   }

   public void setDataSource(String dataSource) {
      this.dataSource = dataSource;
   }

   public String getDataSource() {
      return this.dataSource;
   }

   public ActiveMQResourceAdapterBuilder withStartupTimeout(Duration startupTimeout) {
      this.startupTimeout = startupTimeout;
      return this;
   }

   public void setStartupTimeout(Duration startupTimeout) {
      this.startupTimeout = startupTimeout;
   }

   public Duration getStartupTimeout() {
      return this.startupTimeout;
   }

   public ActiveMQResourceAdapterBuilder withStartupTimeout(long time, TimeUnit unit) {
      return this.withStartupTimeout(new Duration(time, unit));
   }

   public void setStartupTimeout(long time, TimeUnit unit) {
      this.setStartupTimeout(new Duration(time, unit));
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
