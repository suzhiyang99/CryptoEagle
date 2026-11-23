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
   name = "StatelessContainer"
)
public class StatelessContainerBuilder extends Container {
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration accessTimeout = Duration.parse("30 seconds");
   @XmlAttribute
   private int maxSize = 10;
   @XmlAttribute
   private int minSize;
   @XmlAttribute
   private boolean strictPooling = true;
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration maxAge = Duration.parse("0 hours");
   @XmlAttribute
   private boolean replaceAged = true;
   @XmlAttribute
   private boolean replaceFlushed;
   @XmlAttribute
   private int maxAgeOffset = -1;
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration idleTimeout = Duration.parse("0 minutes");
   @XmlAttribute
   private boolean garbageCollection;
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration sweepInterval = Duration.parse("5 minutes");
   @XmlAttribute
   private int callbackThreads = 5;
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration closeTimeout = Duration.parse("5 minutes");

   public StatelessContainerBuilder() {
      this.setClassName("org.apache.openejb.core.stateless.StatelessContainerFactory");
      this.setType("STATELESS");
      this.setId("StatelessContainer");
      this.setFactoryName("create");
   }

   public StatelessContainerBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public StatelessContainerBuilder withAccessTimeout(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
      return this;
   }

   public void setAccessTimeout(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
   }

   public Duration getAccessTimeout() {
      return this.accessTimeout;
   }

   public StatelessContainerBuilder withAccessTimeout(long time, TimeUnit unit) {
      return this.withAccessTimeout(new Duration(time, unit));
   }

   public void setAccessTimeout(long time, TimeUnit unit) {
      this.setAccessTimeout(new Duration(time, unit));
   }

   public StatelessContainerBuilder withMaxSize(int maxSize) {
      this.maxSize = maxSize;
      return this;
   }

   public void setMaxSize(int maxSize) {
      this.maxSize = maxSize;
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public StatelessContainerBuilder withMinSize(int minSize) {
      this.minSize = minSize;
      return this;
   }

   public void setMinSize(int minSize) {
      this.minSize = minSize;
   }

   public int getMinSize() {
      return this.minSize;
   }

   public StatelessContainerBuilder withStrictPooling(boolean strictPooling) {
      this.strictPooling = strictPooling;
      return this;
   }

   public void setStrictPooling(boolean strictPooling) {
      this.strictPooling = strictPooling;
   }

   public boolean getStrictPooling() {
      return this.strictPooling;
   }

   public StatelessContainerBuilder withMaxAge(Duration maxAge) {
      this.maxAge = maxAge;
      return this;
   }

   public void setMaxAge(Duration maxAge) {
      this.maxAge = maxAge;
   }

   public Duration getMaxAge() {
      return this.maxAge;
   }

   public StatelessContainerBuilder withMaxAge(long time, TimeUnit unit) {
      return this.withMaxAge(new Duration(time, unit));
   }

   public void setMaxAge(long time, TimeUnit unit) {
      this.setMaxAge(new Duration(time, unit));
   }

   public StatelessContainerBuilder withReplaceAged(boolean replaceAged) {
      this.replaceAged = replaceAged;
      return this;
   }

   public void setReplaceAged(boolean replaceAged) {
      this.replaceAged = replaceAged;
   }

   public boolean getReplaceAged() {
      return this.replaceAged;
   }

   public StatelessContainerBuilder withReplaceFlushed(boolean replaceFlushed) {
      this.replaceFlushed = replaceFlushed;
      return this;
   }

   public void setReplaceFlushed(boolean replaceFlushed) {
      this.replaceFlushed = replaceFlushed;
   }

   public boolean getReplaceFlushed() {
      return this.replaceFlushed;
   }

   public StatelessContainerBuilder withMaxAgeOffset(int maxAgeOffset) {
      this.maxAgeOffset = maxAgeOffset;
      return this;
   }

   public void setMaxAgeOffset(int maxAgeOffset) {
      this.maxAgeOffset = maxAgeOffset;
   }

   public int getMaxAgeOffset() {
      return this.maxAgeOffset;
   }

   public StatelessContainerBuilder withIdleTimeout(Duration idleTimeout) {
      this.idleTimeout = idleTimeout;
      return this;
   }

   public void setIdleTimeout(Duration idleTimeout) {
      this.idleTimeout = idleTimeout;
   }

   public Duration getIdleTimeout() {
      return this.idleTimeout;
   }

   public StatelessContainerBuilder withIdleTimeout(long time, TimeUnit unit) {
      return this.withIdleTimeout(new Duration(time, unit));
   }

   public void setIdleTimeout(long time, TimeUnit unit) {
      this.setIdleTimeout(new Duration(time, unit));
   }

   public StatelessContainerBuilder withGarbageCollection(boolean garbageCollection) {
      this.garbageCollection = garbageCollection;
      return this;
   }

   public void setGarbageCollection(boolean garbageCollection) {
      this.garbageCollection = garbageCollection;
   }

   public boolean getGarbageCollection() {
      return this.garbageCollection;
   }

   public StatelessContainerBuilder withSweepInterval(Duration sweepInterval) {
      this.sweepInterval = sweepInterval;
      return this;
   }

   public void setSweepInterval(Duration sweepInterval) {
      this.sweepInterval = sweepInterval;
   }

   public Duration getSweepInterval() {
      return this.sweepInterval;
   }

   public StatelessContainerBuilder withSweepInterval(long time, TimeUnit unit) {
      return this.withSweepInterval(new Duration(time, unit));
   }

   public void setSweepInterval(long time, TimeUnit unit) {
      this.setSweepInterval(new Duration(time, unit));
   }

   public StatelessContainerBuilder withCallbackThreads(int callbackThreads) {
      this.callbackThreads = callbackThreads;
      return this;
   }

   public void setCallbackThreads(int callbackThreads) {
      this.callbackThreads = callbackThreads;
   }

   public int getCallbackThreads() {
      return this.callbackThreads;
   }

   public StatelessContainerBuilder withCloseTimeout(Duration closeTimeout) {
      this.closeTimeout = closeTimeout;
      return this;
   }

   public void setCloseTimeout(Duration closeTimeout) {
      this.closeTimeout = closeTimeout;
   }

   public Duration getCloseTimeout() {
      return this.closeTimeout;
   }

   public StatelessContainerBuilder withCloseTimeout(long time, TimeUnit unit) {
      return this.withCloseTimeout(new Duration(time, unit));
   }

   public void setCloseTimeout(long time, TimeUnit unit) {
      this.setCloseTimeout(new Duration(time, unit));
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
