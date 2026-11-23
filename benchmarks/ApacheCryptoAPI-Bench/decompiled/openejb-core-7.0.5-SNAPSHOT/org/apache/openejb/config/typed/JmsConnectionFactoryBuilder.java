package org.apache.openejb.config.typed;

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
   name = "JmsConnectionFactory"
)
public class JmsConnectionFactoryBuilder extends Resource {
   @XmlAttribute
   private String resourceAdapter = "Default JMS Resource Adapter";
   @XmlAttribute
   private String transactionSupport = "xa";
   @XmlAttribute
   private int poolMaxSize = 10;
   @XmlAttribute
   private int poolMinSize;
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration connectionMaxWaitTime = Duration.parse("5 seconds");
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration connectionMaxIdleTime = Duration.parse("15 Minutes");

   public JmsConnectionFactoryBuilder() {
      this.setClassName("org.apache.activemq.ra.ActiveMQManagedConnectionFactory");
      this.setType("javax.jms.ConnectionFactory");
      this.setId("JmsConnectionFactory");
   }

   public JmsConnectionFactoryBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public JmsConnectionFactoryBuilder withResourceAdapter(String resourceAdapter) {
      this.resourceAdapter = resourceAdapter;
      return this;
   }

   public void setResourceAdapter(String resourceAdapter) {
      this.resourceAdapter = resourceAdapter;
   }

   public String getResourceAdapter() {
      return this.resourceAdapter;
   }

   public JmsConnectionFactoryBuilder withTransactionSupport(String transactionSupport) {
      this.transactionSupport = transactionSupport;
      return this;
   }

   public void setTransactionSupport(String transactionSupport) {
      this.transactionSupport = transactionSupport;
   }

   public String getTransactionSupport() {
      return this.transactionSupport;
   }

   public JmsConnectionFactoryBuilder withPoolMaxSize(int poolMaxSize) {
      this.poolMaxSize = poolMaxSize;
      return this;
   }

   public void setPoolMaxSize(int poolMaxSize) {
      this.poolMaxSize = poolMaxSize;
   }

   public int getPoolMaxSize() {
      return this.poolMaxSize;
   }

   public JmsConnectionFactoryBuilder withPoolMinSize(int poolMinSize) {
      this.poolMinSize = poolMinSize;
      return this;
   }

   public void setPoolMinSize(int poolMinSize) {
      this.poolMinSize = poolMinSize;
   }

   public int getPoolMinSize() {
      return this.poolMinSize;
   }

   public JmsConnectionFactoryBuilder withConnectionMaxWaitTime(Duration connectionMaxWaitTime) {
      this.connectionMaxWaitTime = connectionMaxWaitTime;
      return this;
   }

   public void setConnectionMaxWaitTime(Duration connectionMaxWaitTime) {
      this.connectionMaxWaitTime = connectionMaxWaitTime;
   }

   public Duration getConnectionMaxWaitTime() {
      return this.connectionMaxWaitTime;
   }

   public JmsConnectionFactoryBuilder withConnectionMaxWaitTime(long time, TimeUnit unit) {
      return this.withConnectionMaxWaitTime(new Duration(time, unit));
   }

   public void setConnectionMaxWaitTime(long time, TimeUnit unit) {
      this.setConnectionMaxWaitTime(new Duration(time, unit));
   }

   public JmsConnectionFactoryBuilder withConnectionMaxIdleTime(Duration connectionMaxIdleTime) {
      this.connectionMaxIdleTime = connectionMaxIdleTime;
      return this;
   }

   public void setConnectionMaxIdleTime(Duration connectionMaxIdleTime) {
      this.connectionMaxIdleTime = connectionMaxIdleTime;
   }

   public Duration getConnectionMaxIdleTime() {
      return this.connectionMaxIdleTime;
   }

   public JmsConnectionFactoryBuilder withConnectionMaxIdleTime(long time, TimeUnit unit) {
      return this.withConnectionMaxIdleTime(new Duration(time, unit));
   }

   public void setConnectionMaxIdleTime(long time, TimeUnit unit) {
      this.setConnectionMaxIdleTime(new Duration(time, unit));
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
