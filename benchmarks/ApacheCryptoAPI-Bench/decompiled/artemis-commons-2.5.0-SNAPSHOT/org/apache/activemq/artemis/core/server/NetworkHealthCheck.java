package org.apache.activemq.artemis.core.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.artemis.logs.ActiveMQUtilLogger;
import org.apache.activemq.artemis.utils.ActiveMQThreadFactory;
import org.apache.activemq.artemis.utils.collections.ConcurrentHashSet;
import org.jboss.logging.Logger;

public class NetworkHealthCheck extends ActiveMQScheduledComponent {
   private static final Logger logger = Logger.getLogger(NetworkHealthCheck.class);
   private final Set<ActiveMQComponent> componentList;
   private final Set<InetAddress> addresses;
   private final Set<URL> urls;
   private NetworkInterface networkInterface;
   public static final String IPV6_DEFAULT_COMMAND = "ping6 -c 1 %2$s";
   public static final String IPV4_DEFAULT_COMMAND = "ping -c 1 -t %d %s";
   private String ipv4Command;
   private String ipv6Command;
   private boolean ignoreLoopback;
   private int networkTimeout;

   public NetworkHealthCheck() {
      this((String)null, 1000L, 1000);
   }

   public NetworkHealthCheck(String nicName, long checkPeriod, int networkTimeout) {
      super((ScheduledExecutorService)null, (Executor)null, checkPeriod, TimeUnit.MILLISECONDS, false);
      this.componentList = new ConcurrentHashSet<ActiveMQComponent>();
      this.addresses = new ConcurrentHashSet<InetAddress>();
      this.urls = new ConcurrentHashSet<URL>();
      this.ipv4Command = "ping -c 1 -t %d %s";
      this.ipv6Command = "ping6 -c 1 %2$s";
      this.ignoreLoopback = false;
      this.networkTimeout = networkTimeout;
      this.setNICName(nicName);
   }

   public NetworkHealthCheck setNICName(String nicName) {
      NetworkInterface netToUse;
      try {
         if (nicName != null) {
            netToUse = NetworkInterface.getByName(nicName);
         } else {
            netToUse = null;
         }
      } catch (Exception e) {
         ActiveMQUtilLogger.LOGGER.failedToSetNIC(e, nicName == null ? " " : nicName);
         netToUse = null;
      }

      this.networkInterface = netToUse;
      return this;
   }

   public boolean isIgnoreLoopback() {
      return this.ignoreLoopback;
   }

   public NetworkHealthCheck setIgnoreLoopback(boolean ignoreLoopback) {
      this.ignoreLoopback = ignoreLoopback;
      return this;
   }

   public Set<InetAddress> getAddresses() {
      return this.addresses;
   }

   public Set<URL> getUrls() {
      return this.urls;
   }

   public String getNICName() {
      return this.networkInterface != null ? this.networkInterface.getName() : null;
   }

   public NetworkHealthCheck parseAddressList(String addressList) {
      if (addressList != null) {
         String[] addresses = addressList.split(",");

         for(String address : addresses) {
            if (!address.trim().isEmpty()) {
               try {
                  this.addAddress(InetAddress.getByName(address.trim()));
               } catch (Exception e) {
                  ActiveMQUtilLogger.LOGGER.failedToParseAddressList(e, addressList);
               }
            }
         }
      }

      return this;
   }

   public NetworkHealthCheck parseURIList(String addressList) {
      if (addressList != null) {
         String[] addresses = addressList.split(",");

         for(String address : addresses) {
            if (!address.trim().isEmpty()) {
               try {
                  this.addURL(new URL(address.trim()));
               } catch (Exception e) {
                  ActiveMQUtilLogger.LOGGER.failedToParseUrlList(e, addressList);
               }
            }
         }
      }

      return this;
   }

   protected ActiveMQThreadFactory getThreadFactory() {
      return new ActiveMQThreadFactory("NetworkChecker", "Network-Checker-", false, this.getThisClassLoader());
   }

   private ClassLoader getThisClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return NetworkHealthCheck.this.getClass().getClassLoader();
         }
      });
   }

   public int getNetworkTimeout() {
      return this.networkTimeout;
   }

   public NetworkHealthCheck setPeriod(long period) {
      super.setPeriod(period);
      return this;
   }

   public NetworkHealthCheck setTimeUnit(TimeUnit timeUnit) {
      super.setTimeUnit(timeUnit);
      return this;
   }

   public NetworkHealthCheck setNetworkTimeout(int networkTimeout) {
      this.networkTimeout = networkTimeout;
      return this;
   }

   public NetworkHealthCheck addComponent(ActiveMQComponent component) {
      this.componentList.add(component);
      this.checkStart();
      return this;
   }

   public NetworkHealthCheck clearComponents() {
      this.componentList.clear();
      return this;
   }

   public NetworkHealthCheck addAddress(InetAddress address) {
      if (!this.check(address)) {
         ActiveMQUtilLogger.LOGGER.addressWasntReacheable(address.toString());
      }

      if (!this.ignoreLoopback && address.isLoopbackAddress()) {
         ActiveMQUtilLogger.LOGGER.addressloopback(address.toString());
      } else {
         this.addresses.add(address);
         this.checkStart();
      }

      return this;
   }

   public NetworkHealthCheck removeAddress(InetAddress address) {
      this.addresses.remove(address);
      return this;
   }

   public NetworkHealthCheck clearAddresses() {
      this.addresses.clear();
      return this;
   }

   public NetworkHealthCheck addURL(URL url) {
      if (!this.check(url)) {
         ActiveMQUtilLogger.LOGGER.urlWasntReacheable(url.toString());
      }

      this.urls.add(url);
      this.checkStart();
      return this;
   }

   public NetworkHealthCheck removeURL(URL url) {
      this.urls.remove(url);
      return this;
   }

   public NetworkHealthCheck clearURL() {
      this.urls.clear();
      return this;
   }

   public String getIpv4Command() {
      return this.ipv4Command;
   }

   public NetworkHealthCheck setIpv4Command(String ipv4Command) {
      this.ipv4Command = ipv4Command;
      return this;
   }

   public String getIpv6Command() {
      return this.ipv6Command;
   }

   public NetworkHealthCheck setIpv6Command(String ipv6Command) {
      this.ipv6Command = ipv6Command;
      return this;
   }

   private void checkStart() {
      if (!this.isStarted() && (!this.addresses.isEmpty() || !this.urls.isEmpty()) && !this.componentList.isEmpty()) {
         this.start();
      }

   }

   public void run() {
      boolean healthy = this.check();
      if (healthy) {
         for(ActiveMQComponent component : this.componentList) {
            if (!component.isStarted()) {
               try {
                  ActiveMQUtilLogger.LOGGER.startingService(component.toString());
                  component.start();
               } catch (Exception e) {
                  ActiveMQUtilLogger.LOGGER.errorStartingComponent(e, component.toString());
               }
            }
         }
      } else {
         for(ActiveMQComponent component : this.componentList) {
            if (component.isStarted()) {
               try {
                  ActiveMQUtilLogger.LOGGER.stoppingService(component.toString());
                  component.stop();
               } catch (Exception e) {
                  ActiveMQUtilLogger.LOGGER.errorStoppingComponent(e, component.toString());
               }
            }
         }
      }

   }

   public boolean check() {
      if (this.isEmpty()) {
         return true;
      } else {
         for(InetAddress address : this.addresses) {
            if (this.check(address)) {
               return true;
            }
         }

         for(URL url : this.urls) {
            if (this.check(url)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean check(InetAddress address) {
      try {
         if (address.isReachable(this.networkInterface, 0, this.networkTimeout)) {
            if (logger.isTraceEnabled()) {
               logger.tracef(address + " OK", new Object[0]);
            }

            return true;
         } else {
            return this.purePing(address);
         }
      } catch (Exception e) {
         ActiveMQUtilLogger.LOGGER.failedToCheckAddress(e, address == null ? " " : address.toString());
         return false;
      }
   }

   public boolean purePing(InetAddress address) throws IOException, InterruptedException {
      long timeout = Math.max(1L, TimeUnit.MILLISECONDS.toSeconds((long)this.networkTimeout));
      if (logger.isTraceEnabled()) {
         logger.trace("purePing on canonical address " + address.getCanonicalHostName());
      }

      ProcessBuilder processBuilder;
      if (address instanceof Inet6Address) {
         processBuilder = this.buildProcess(this.ipv6Command, timeout, address.getCanonicalHostName());
      } else {
         processBuilder = this.buildProcess(this.ipv4Command, timeout, address.getCanonicalHostName());
      }

      Process pingProcess = processBuilder.start();
      this.readStream(pingProcess.getInputStream(), false);
      this.readStream(pingProcess.getErrorStream(), true);
      return pingProcess.waitFor() == 0;
   }

   private ProcessBuilder buildProcess(String expressionCommand, long timeout, String host) {
      String command = String.format(expressionCommand, timeout, host);
      if (logger.isDebugEnabled()) {
         logger.debug("executing ping:: " + command);
      }

      ProcessBuilder builder = new ProcessBuilder(command.split(" "));
      return builder;
   }

   private void readStream(InputStream stream, boolean error) throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

      String inputLine;
      while((inputLine = reader.readLine()) != null) {
         if (error) {
            ActiveMQUtilLogger.LOGGER.failedToReadFromStream(inputLine);
         } else {
            logger.trace(inputLine);
         }
      }

      reader.close();
   }

   public boolean check(URL url) {
      try {
         URLConnection connection = url.openConnection();
         connection.setReadTimeout(this.networkTimeout);
         InputStream is = connection.getInputStream();
         is.close();
         return true;
      } catch (Exception e) {
         ActiveMQUtilLogger.LOGGER.failedToCheckURL(e, url == null ? " " : url.toString());
         return false;
      }
   }

   public boolean isEmpty() {
      return this.addresses.isEmpty() && this.urls.isEmpty();
   }
}
