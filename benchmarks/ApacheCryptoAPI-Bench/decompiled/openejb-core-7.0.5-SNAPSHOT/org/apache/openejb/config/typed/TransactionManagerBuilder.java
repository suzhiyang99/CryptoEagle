package org.apache.openejb.config.typed;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.openejb.config.sys.TransactionManager;
import org.apache.openejb.config.typed.util.Builders;
import org.apache.openejb.config.typed.util.DurationAdapter;
import org.apache.openejb.util.Duration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "TransactionManager"
)
public class TransactionManagerBuilder extends TransactionManager {
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration defaultTransactionTimeout = Duration.parse("10 minutes");
   @XmlAttribute
   private boolean txRecovery;
   @XmlAttribute
   private int bufferSizeKb = 32;
   @XmlAttribute
   private boolean checksumEnabled = true;
   @XmlAttribute
   private boolean adler32Checksum = true;
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration flushSleepTime = Duration.parse("50 Milliseconds");
   @XmlAttribute
   private String logFileDir = "txlog";
   @XmlAttribute
   private String logFileExt = "log";
   @XmlAttribute
   private String logFileName = "howl";
   @XmlAttribute
   private int maxBlocksPerFile = -1;
   @XmlAttribute
   private int maxBuffers;
   @XmlAttribute
   private int maxLogFiles = 2;
   @XmlAttribute
   private int minBuffers = 4;
   @XmlAttribute
   private int threadsWaitingForceThreshold = -1;

   public TransactionManagerBuilder() {
      this.setClassName("org.apache.openejb.resource.GeronimoTransactionManagerFactory");
      this.setType("TransactionManager");
      this.setId("TransactionManager");
      this.setConstructor("defaultTransactionTimeoutSeconds, defaultTransactionTimeout, txRecovery, tmId, bufferClassName, bufferSizeKb, checksumEnabled, adler32Checksum, flushSleepTimeMilliseconds, flushSleepTime, logFileDir, logFileExt, logFileName, maxBlocksPerFile, maxBuffers, maxLogFiles, minBuffers, threadsWaitingForceThreshold");
      this.setFactoryName("create");
   }

   public TransactionManagerBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public TransactionManagerBuilder withDefaultTransactionTimeout(Duration defaultTransactionTimeout) {
      this.defaultTransactionTimeout = defaultTransactionTimeout;
      return this;
   }

   public void setDefaultTransactionTimeout(Duration defaultTransactionTimeout) {
      this.defaultTransactionTimeout = defaultTransactionTimeout;
   }

   public Duration getDefaultTransactionTimeout() {
      return this.defaultTransactionTimeout;
   }

   public TransactionManagerBuilder withDefaultTransactionTimeout(long time, TimeUnit unit) {
      return this.withDefaultTransactionTimeout(new Duration(time, unit));
   }

   public void setDefaultTransactionTimeout(long time, TimeUnit unit) {
      this.setDefaultTransactionTimeout(new Duration(time, unit));
   }

   public TransactionManagerBuilder withTxRecovery(boolean txRecovery) {
      this.txRecovery = txRecovery;
      return this;
   }

   public void setTxRecovery(boolean txRecovery) {
      this.txRecovery = txRecovery;
   }

   public boolean getTxRecovery() {
      return this.txRecovery;
   }

   public TransactionManagerBuilder withBufferSizeKb(int bufferSizeKb) {
      this.bufferSizeKb = bufferSizeKb;
      return this;
   }

   public void setBufferSizeKb(int bufferSizeKb) {
      this.bufferSizeKb = bufferSizeKb;
   }

   public int getBufferSizeKb() {
      return this.bufferSizeKb;
   }

   public TransactionManagerBuilder withChecksumEnabled(boolean checksumEnabled) {
      this.checksumEnabled = checksumEnabled;
      return this;
   }

   public void setChecksumEnabled(boolean checksumEnabled) {
      this.checksumEnabled = checksumEnabled;
   }

   public boolean getChecksumEnabled() {
      return this.checksumEnabled;
   }

   public TransactionManagerBuilder withAdler32Checksum(boolean adler32Checksum) {
      this.adler32Checksum = adler32Checksum;
      return this;
   }

   public void setAdler32Checksum(boolean adler32Checksum) {
      this.adler32Checksum = adler32Checksum;
   }

   public boolean getAdler32Checksum() {
      return this.adler32Checksum;
   }

   public TransactionManagerBuilder withFlushSleepTime(Duration flushSleepTime) {
      this.flushSleepTime = flushSleepTime;
      return this;
   }

   public void setFlushSleepTime(Duration flushSleepTime) {
      this.flushSleepTime = flushSleepTime;
   }

   public Duration getFlushSleepTime() {
      return this.flushSleepTime;
   }

   public TransactionManagerBuilder withFlushSleepTime(long time, TimeUnit unit) {
      return this.withFlushSleepTime(new Duration(time, unit));
   }

   public void setFlushSleepTime(long time, TimeUnit unit) {
      this.setFlushSleepTime(new Duration(time, unit));
   }

   public TransactionManagerBuilder withLogFileDir(String logFileDir) {
      this.logFileDir = logFileDir;
      return this;
   }

   public void setLogFileDir(String logFileDir) {
      this.logFileDir = logFileDir;
   }

   public String getLogFileDir() {
      return this.logFileDir;
   }

   public TransactionManagerBuilder withLogFileExt(String logFileExt) {
      this.logFileExt = logFileExt;
      return this;
   }

   public void setLogFileExt(String logFileExt) {
      this.logFileExt = logFileExt;
   }

   public String getLogFileExt() {
      return this.logFileExt;
   }

   public TransactionManagerBuilder withLogFileName(String logFileName) {
      this.logFileName = logFileName;
      return this;
   }

   public void setLogFileName(String logFileName) {
      this.logFileName = logFileName;
   }

   public String getLogFileName() {
      return this.logFileName;
   }

   public TransactionManagerBuilder withMaxBlocksPerFile(int maxBlocksPerFile) {
      this.maxBlocksPerFile = maxBlocksPerFile;
      return this;
   }

   public void setMaxBlocksPerFile(int maxBlocksPerFile) {
      this.maxBlocksPerFile = maxBlocksPerFile;
   }

   public int getMaxBlocksPerFile() {
      return this.maxBlocksPerFile;
   }

   public TransactionManagerBuilder withMaxBuffers(int maxBuffers) {
      this.maxBuffers = maxBuffers;
      return this;
   }

   public void setMaxBuffers(int maxBuffers) {
      this.maxBuffers = maxBuffers;
   }

   public int getMaxBuffers() {
      return this.maxBuffers;
   }

   public TransactionManagerBuilder withMaxLogFiles(int maxLogFiles) {
      this.maxLogFiles = maxLogFiles;
      return this;
   }

   public void setMaxLogFiles(int maxLogFiles) {
      this.maxLogFiles = maxLogFiles;
   }

   public int getMaxLogFiles() {
      return this.maxLogFiles;
   }

   public TransactionManagerBuilder withMinBuffers(int minBuffers) {
      this.minBuffers = minBuffers;
      return this;
   }

   public void setMinBuffers(int minBuffers) {
      this.minBuffers = minBuffers;
   }

   public int getMinBuffers() {
      return this.minBuffers;
   }

   public TransactionManagerBuilder withThreadsWaitingForceThreshold(int threadsWaitingForceThreshold) {
      this.threadsWaitingForceThreshold = threadsWaitingForceThreshold;
      return this;
   }

   public void setThreadsWaitingForceThreshold(int threadsWaitingForceThreshold) {
      this.threadsWaitingForceThreshold = threadsWaitingForceThreshold;
   }

   public int getThreadsWaitingForceThreshold() {
      return this.threadsWaitingForceThreshold;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
