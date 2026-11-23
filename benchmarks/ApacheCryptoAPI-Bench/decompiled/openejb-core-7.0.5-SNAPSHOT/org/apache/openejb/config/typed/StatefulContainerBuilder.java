package org.apache.openejb.config.typed;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.openejb.config.sys.Container;
import org.apache.openejb.config.typed.util.Builders;
import org.apache.openejb.config.typed.util.DurationAdapter;
import org.apache.openejb.util.Duration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "StatefulContainer"
)
public class StatefulContainerBuilder extends Container {
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration accessTimeout = Duration.parse("30 seconds");
   @XmlAttribute
   private String cache = "org.apache.openejb.core.stateful.SimpleCache";
   @XmlAttribute
   private String passivator = "org.apache.openejb.core.stateful.SimplePassivater";
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration timeOut = Duration.parse("20");
   @XmlAttribute
   private int frequency = 60;
   @XmlAttribute
   private int capacity = 1000;
   @XmlAttribute
   private int bulkPassivate = 100;

   public StatefulContainerBuilder() {
      this.setClassName("org.apache.openejb.core.stateful.StatefulContainerFactory");
      this.setType("STATEFUL");
      this.setId("StatefulContainer");
      this.setFactoryName("create");
   }

   public StatefulContainerBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public StatefulContainerBuilder withAccessTimeout(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
      return this;
   }

   public void setAccessTimeout(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
   }

   public Duration getAccessTimeout() {
      return this.accessTimeout;
   }

   public StatefulContainerBuilder withAccessTimeout(long time, TimeUnit unit) {
      return this.withAccessTimeout(new Duration(time, unit));
   }

   public void setAccessTimeout(long time, TimeUnit unit) {
      this.setAccessTimeout(new Duration(time, unit));
   }

   public StatefulContainerBuilder withCache(String cache) {
      this.cache = cache;
      return this;
   }

   public void setCache(String cache) {
      this.cache = cache;
   }

   public String getCache() {
      return this.cache;
   }

   public StatefulContainerBuilder withPassivator(String passivator) {
      this.passivator = passivator;
      return this;
   }

   public void setPassivator(String passivator) {
      this.passivator = passivator;
   }

   public String getPassivator() {
      return this.passivator;
   }

   public StatefulContainerBuilder withTimeOut(Duration timeOut) {
      this.timeOut = timeOut;
      return this;
   }

   public void setTimeOut(Duration timeOut) {
      this.timeOut = timeOut;
   }

   public Duration getTimeOut() {
      return this.timeOut;
   }

   public StatefulContainerBuilder withTimeOut(long time, TimeUnit unit) {
      return this.withTimeOut(new Duration(time, unit));
   }

   public void setTimeOut(long time, TimeUnit unit) {
      this.setTimeOut(new Duration(time, unit));
   }

   public StatefulContainerBuilder withFrequency(int frequency) {
      this.frequency = frequency;
      return this;
   }

   public void setFrequency(int frequency) {
      this.frequency = frequency;
   }

   public int getFrequency() {
      return this.frequency;
   }

   public StatefulContainerBuilder withCapacity(int capacity) {
      this.capacity = capacity;
      return this;
   }

   public void setCapacity(int capacity) {
      this.capacity = capacity;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public StatefulContainerBuilder withBulkPassivate(int bulkPassivate) {
      this.bulkPassivate = bulkPassivate;
      return this;
   }

   public void setBulkPassivate(int bulkPassivate) {
      this.bulkPassivate = bulkPassivate;
   }

   public int getBulkPassivate() {
      return this.bulkPassivate;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
