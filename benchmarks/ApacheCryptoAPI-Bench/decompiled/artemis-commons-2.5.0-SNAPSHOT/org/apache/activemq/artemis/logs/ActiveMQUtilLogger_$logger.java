package org.apache.activemq.artemis.logs;

import java.io.Serializable;
import java.util.Locale;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

public class ActiveMQUtilLogger_$logger extends DelegatingBasicLogger implements ActiveMQUtilLogger, BasicLogger, Serializable {
   private static final long serialVersionUID = 1L;
   private static final String FQCN = ActiveMQUtilLogger_$logger.class.getName();
   private static final Locale LOCALE;
   private static final String startingService = "AMQ201000: Network is healthy, starting service {0}";
   private static final String stoppingService = "AMQ201001: Network is unhealthy, stopping service {0}";
   private static final String missingPrivsForClassloader = "AMQ202000: Missing privileges to set Thread Context Class Loader on Thread Factory. Using current Thread Context Class Loader";
   private static final String addressloopback = "AMQ202001: {0} is a loopback address and will be discarded.";
   private static final String addressWasntReacheable = "AMQ202002: Ping Address {0} wasn't reacheable.";
   private static final String urlWasntReacheable = "AMQ202003: Ping Url {0} wasn't reacheable.";
   private static final String errorStartingComponent = "AMQ202004: Error starting component {0} ";
   private static final String errorStoppingComponent = "AMQ202005: Error stopping component {0} ";
   private static final String failedToCheckURL = "AMQ202006: Failed to check Url {0}.";
   private static final String failedToCheckAddress = "AMQ202007: Failed to check Address {0}.";
   private static final String failedToParseAddressList = "AMQ202008: Failed to check Address list {0}.";
   private static final String failedToParseUrlList = "AMQ202009: Failed to check Url list {0}.";
   private static final String failedToSetNIC = "AMQ202010: Failed to set NIC {0}.";
   private static final String failedToReadFromStream = "AMQ202011: Failed to read from stream {0}.";
   private static final String failedToSerializeObject = "AMQ202012: Object cannot be serialized.";
   private static final String failedToDeserializeObject = "AMQ202013: Unable to deserialize object.";
   private static final String failedToEncodeByteArrayToBase64Notation = "AMQ202014: Unable to encode byte array into Base64 notation.";
   private static final String failedToCleanupFile = "AMQ202015: Failed to clean up file {0}";
   private static final String failedListFilesToCleanup = "AMQ202016: Could not list files to clean up in {0}";

   public ActiveMQUtilLogger_$logger(Logger log) {
      super(log);
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   public final void startingService(String component) {
      super.log.logv(FQCN, Level.INFO, (Throwable)null, this.startingService$str(), component);
   }

   protected String startingService$str() {
      return "AMQ201000: Network is healthy, starting service {0}";
   }

   public final void stoppingService(String component) {
      super.log.logv(FQCN, Level.INFO, (Throwable)null, this.stoppingService$str(), component);
   }

   protected String stoppingService$str() {
      return "AMQ201001: Network is unhealthy, stopping service {0}";
   }

   public final void missingPrivsForClassloader() {
      super.log.logv(FQCN, Level.WARN, (Throwable)null, this.missingPrivsForClassloader$str(), new Object[0]);
   }

   protected String missingPrivsForClassloader$str() {
      return "AMQ202000: Missing privileges to set Thread Context Class Loader on Thread Factory. Using current Thread Context Class Loader";
   }

   public final void addressloopback(String address) {
      super.log.logv(FQCN, Level.WARN, (Throwable)null, this.addressloopback$str(), address);
   }

   protected String addressloopback$str() {
      return "AMQ202001: {0} is a loopback address and will be discarded.";
   }

   public final void addressWasntReacheable(String address) {
      super.log.logv(FQCN, Level.WARN, (Throwable)null, this.addressWasntReacheable$str(), address);
   }

   protected String addressWasntReacheable$str() {
      return "AMQ202002: Ping Address {0} wasn't reacheable.";
   }

   public final void urlWasntReacheable(String url) {
      super.log.logv(FQCN, Level.WARN, (Throwable)null, this.urlWasntReacheable$str(), url);
   }

   protected String urlWasntReacheable$str() {
      return "AMQ202003: Ping Url {0} wasn't reacheable.";
   }

   public final void errorStartingComponent(Exception e, String component) {
      super.log.logv(FQCN, Level.WARN, e, this.errorStartingComponent$str(), component);
   }

   protected String errorStartingComponent$str() {
      return "AMQ202004: Error starting component {0} ";
   }

   public final void errorStoppingComponent(Exception e, String component) {
      super.log.logv(FQCN, Level.WARN, e, this.errorStoppingComponent$str(), component);
   }

   protected String errorStoppingComponent$str() {
      return "AMQ202005: Error stopping component {0} ";
   }

   public final void failedToCheckURL(Exception e, String url) {
      super.log.logv(FQCN, Level.WARN, e, this.failedToCheckURL$str(), url);
   }

   protected String failedToCheckURL$str() {
      return "AMQ202006: Failed to check Url {0}.";
   }

   public final void failedToCheckAddress(Exception e, String address) {
      super.log.logv(FQCN, Level.WARN, e, this.failedToCheckAddress$str(), address);
   }

   protected String failedToCheckAddress$str() {
      return "AMQ202007: Failed to check Address {0}.";
   }

   public final void failedToParseAddressList(Exception e, String addressList) {
      super.log.logv(FQCN, Level.WARN, e, this.failedToParseAddressList$str(), addressList);
   }

   protected String failedToParseAddressList$str() {
      return "AMQ202008: Failed to check Address list {0}.";
   }

   public final void failedToParseUrlList(Exception e, String urlList) {
      super.log.logv(FQCN, Level.WARN, e, this.failedToParseUrlList$str(), urlList);
   }

   protected String failedToParseUrlList$str() {
      return "AMQ202009: Failed to check Url list {0}.";
   }

   public final void failedToSetNIC(Exception e, String nic) {
      super.log.logv(FQCN, Level.WARN, e, this.failedToSetNIC$str(), nic);
   }

   protected String failedToSetNIC$str() {
      return "AMQ202010: Failed to set NIC {0}.";
   }

   public final void failedToReadFromStream(String stream) {
      super.log.logv(FQCN, Level.WARN, (Throwable)null, this.failedToReadFromStream$str(), stream);
   }

   protected String failedToReadFromStream$str() {
      return "AMQ202011: Failed to read from stream {0}.";
   }

   public final void failedToSerializeObject(Exception e) {
      super.log.logv(FQCN, Level.WARN, e, this.failedToSerializeObject$str(), new Object[0]);
   }

   protected String failedToSerializeObject$str() {
      return "AMQ202012: Object cannot be serialized.";
   }

   public final void failedToDeserializeObject(Exception e) {
      super.log.logv(FQCN, Level.WARN, e, this.failedToDeserializeObject$str(), new Object[0]);
   }

   protected String failedToDeserializeObject$str() {
      return "AMQ202013: Unable to deserialize object.";
   }

   public final void failedToEncodeByteArrayToBase64Notation(Exception e) {
      super.log.logv(FQCN, Level.WARN, e, this.failedToEncodeByteArrayToBase64Notation$str(), new Object[0]);
   }

   protected String failedToEncodeByteArrayToBase64Notation$str() {
      return "AMQ202014: Unable to encode byte array into Base64 notation.";
   }

   public final void failedToCleanupFile(String file) {
      super.log.logv(FQCN, Level.WARN, (Throwable)null, this.failedToCleanupFile$str(), file);
   }

   protected String failedToCleanupFile$str() {
      return "AMQ202015: Failed to clean up file {0}";
   }

   public final void failedListFilesToCleanup(String path) {
      super.log.logv(FQCN, Level.WARN, (Throwable)null, this.failedListFilesToCleanup$str(), path);
   }

   protected String failedListFilesToCleanup$str() {
      return "AMQ202016: Could not list files to clean up in {0}";
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
