package org.apache.openejb.core.security.jaas;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.sql.DataSource;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Base64;
import org.apache.openejb.util.HexConverter;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Strings;

public class SQLLoginModule implements LoginModule {
   private static final Logger log;
   private final EnumMap<Option, String> optionsMap = new EnumMap(Option.class);
   private String connectionURL;
   private Properties properties;
   private Driver driver;
   private DataSource dataSource;
   private String userSelect;
   private String groupSelect;
   private String digest;
   private String encoding;
   private boolean loginSucceeded;
   private Subject subject;
   private CallbackHandler handler;
   private String cbUsername;
   private String cbPassword;
   private final Set<String> groups = new HashSet();
   private final Set<Principal> allPrincipals = new HashSet();

   public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
      this.subject = subject;
      this.handler = callbackHandler;

      for(Object key : options.keySet()) {
         Option option = SQLLoginModule.Option.findByName((String)key);
         if (option != null) {
            String value = (String)options.get(key);
            this.optionsMap.put(option, value.trim());
         } else {
            log.warning("Ignoring option: {0}. Not supported.", key);
         }
      }

      this.userSelect = (String)this.optionsMap.get(SQLLoginModule.Option.USER_SELECT);
      this.groupSelect = (String)this.optionsMap.get(SQLLoginModule.Option.GROUP_SELECT);
      this.digest = (String)this.optionsMap.get(SQLLoginModule.Option.DIGEST);
      this.encoding = (String)this.optionsMap.get(SQLLoginModule.Option.ENCODING);
      if (!Strings.checkNullBlankString(this.digest)) {
         try {
            MessageDigest.getInstance(this.digest);
         } catch (NoSuchAlgorithmException e) {
            this.initError(e, "Digest algorithm %s is not available.", this.digest);
         }

         if (this.encoding != null && !"hex".equalsIgnoreCase(this.encoding) && !"base64".equalsIgnoreCase(this.encoding)) {
            this.initError((Exception)null, "Digest Encoding %s is not supported.", this.encoding);
         }
      }

      if (this.optionsMap.containsKey(SQLLoginModule.Option.DATABASE_POOL_NAME)) {
         String dataSourceName = (String)this.optionsMap.get(SQLLoginModule.Option.DATABASE_POOL_NAME);
         ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);

         try {
            this.dataSource = (DataSource)containerSystem.getJNDIContext().lookup("openejb/Resource/" + dataSourceName);
         } catch (NamingException e) {
            this.initError(e, "Data source %s not found.", dataSourceName);
         }
      } else if (this.optionsMap.containsKey(SQLLoginModule.Option.CONNECTION_URL)) {
         this.connectionURL = (String)this.optionsMap.get(SQLLoginModule.Option.CONNECTION_URL);
         String user = (String)this.optionsMap.get(SQLLoginModule.Option.USER);
         String password = (String)this.optionsMap.get(SQLLoginModule.Option.PASSWORD);
         String driverName = (String)this.optionsMap.get(SQLLoginModule.Option.DRIVER);
         this.properties = new Properties();
         if (user != null) {
            this.properties.put("user", user);
         }

         if (password != null) {
            this.properties.put("password", password);
         }

         if (driverName != null) {
            ClassLoader cl = this.getClass().getClassLoader();

            try {
               this.driver = (Driver)cl.loadClass(driverName).newInstance();
            } catch (ClassNotFoundException e) {
               this.initError(e, "Driver class %s is not available. Perhaps you need to add it as a dependency in your deployment plan?", driverName);
            } catch (Exception e) {
               this.initError(e, "Unable to load, instantiate, register driver %s: %s", driverName, e.getMessage());
            }
         }
      } else {
         this.initError((Exception)null, "Neither %s nor %s was specified", SQLLoginModule.Option.DATABASE_POOL_NAME.name, SQLLoginModule.Option.CONNECTION_URL.name);
      }

   }

   private void initError(Exception e, String format, Object... args) {
      String message = String.format(format, args);
      log.error("Initialization failed. {0}", message);
      throw new IllegalArgumentException(message, e);
   }

   public boolean login() throws LoginException {
      this.loginSucceeded = false;
      Callback[] callbacks = new Callback[]{new NameCallback("User name"), new PasswordCallback("Password", false)};

      try {
         this.handler.handle(callbacks);
      } catch (UnsupportedCallbackException | IOException ioe) {
         throw (LoginException)(new LoginException()).initCause(ioe);
      }

      assert callbacks.length == 2;

      this.cbUsername = ((NameCallback)callbacks[0]).getName();
      if (Strings.checkNullBlankString(this.cbUsername)) {
         throw new FailedLoginException();
      } else {
         char[] provided = ((PasswordCallback)callbacks[1]).getPassword();
         this.cbPassword = provided == null ? null : new String(provided);

         try {
            Connection conn;
            if (this.dataSource != null) {
               conn = this.dataSource.getConnection();
            } else if (this.driver != null) {
               conn = this.driver.connect(this.connectionURL, this.properties);
            } else {
               conn = DriverManager.getConnection(this.connectionURL, this.properties);
            }

            try {
               PreparedStatement statement = conn.prepareStatement(this.userSelect);

               try {
                  int count = statement.getParameterMetaData().getParameterCount();

                  for(int i = 0; i < count; ++i) {
                     statement.setObject(i + 1, this.cbUsername);
                  }

                  ResultSet result = statement.executeQuery();

                  try {
                     boolean found = false;

                     while(result.next()) {
                        String userName = result.getString(1);
                        String userPassword = result.getString(2);
                        if (this.cbUsername.equals(userName)) {
                           found = true;
                           if (!this.checkPassword(userPassword, this.cbPassword)) {
                              throw new FailedLoginException();
                           }
                           break;
                        }
                     }

                     if (!found) {
                        throw new FailedLoginException();
                     }
                  } finally {
                     result.close();
                  }
               } finally {
                  statement.close();
               }

               statement = conn.prepareStatement(this.groupSelect);

               try {
                  int count = statement.getParameterMetaData().getParameterCount();

                  for(int i = 0; i < count; ++i) {
                     statement.setObject(i + 1, this.cbUsername);
                  }

                  ResultSet result = statement.executeQuery();

                  try {
                     while(result.next()) {
                        String userName = result.getString(1);
                        String groupName = result.getString(2);
                        if (this.cbUsername.equals(userName)) {
                           this.groups.add(groupName);
                        }
                     }
                  } finally {
                     result.close();
                  }
               } finally {
                  statement.close();
               }
            } finally {
               conn.close();
            }
         } catch (LoginException e) {
            this.cbUsername = null;
            this.cbPassword = null;
            this.groups.clear();
            throw e;
         } catch (SQLException sqle) {
            this.cbUsername = null;
            this.cbPassword = null;
            this.groups.clear();
            throw (LoginException)(new LoginException("SQL error")).initCause(sqle);
         } catch (Exception e) {
            this.cbUsername = null;
            this.cbPassword = null;
            this.groups.clear();
            throw (LoginException)(new LoginException("Could not access datasource")).initCause(e);
         }

         this.loginSucceeded = true;
         return true;
      }
   }

   public boolean commit() throws LoginException {
      if (this.loginSucceeded) {
         if (this.cbUsername != null) {
            this.allPrincipals.add(new UserPrincipal(this.cbUsername));
         }

         for(String group : this.groups) {
            this.allPrincipals.add(new GroupPrincipal(group));
         }

         this.subject.getPrincipals().addAll(this.allPrincipals);
      }

      this.cbUsername = null;
      this.cbPassword = null;
      this.groups.clear();
      return this.loginSucceeded;
   }

   public boolean abort() throws LoginException {
      if (this.loginSucceeded) {
         this.cbUsername = null;
         this.cbPassword = null;
         this.groups.clear();
         this.allPrincipals.clear();
      }

      return this.loginSucceeded;
   }

   public boolean logout() throws LoginException {
      this.loginSucceeded = false;
      this.cbUsername = null;
      this.cbPassword = null;
      this.groups.clear();
      if (!this.subject.isReadOnly()) {
         this.subject.getPrincipals().removeAll(this.allPrincipals);
      }

      this.allPrincipals.clear();
      return true;
   }

   private boolean checkPassword(String real, String provided) {
      if (real == null && provided == null) {
         return true;
      } else if (real != null && provided != null) {
         if (Strings.checkNullBlankString(this.digest)) {
            return real.equals(provided);
         } else {
            try {
               MessageDigest md = MessageDigest.getInstance(this.digest);
               byte[] data = md.digest(provided.getBytes());
               if (this.encoding == null || "hex".equalsIgnoreCase(this.encoding)) {
                  return real.equalsIgnoreCase(HexConverter.bytesToHex(data));
               }

               if ("base64".equalsIgnoreCase(this.encoding)) {
                  return real.equals(new String(Base64.encodeBase64(data)));
               }
            } catch (NoSuchAlgorithmException e) {
               log.error("Should not occur.  Availability of algorithm has been checked at initialization.", (Throwable)e);
            }

            return false;
         }
      } else {
         return false;
      }
   }

   static {
      log = Logger.getInstance(LogCategory.OPENEJB_SECURITY, "org.apache.openejb.util.resources");
   }

   private static enum Option {
      USER_SELECT("userSelect"),
      GROUP_SELECT("groupSelect"),
      CONNECTION_URL("jdbcURL"),
      USER("jdbcUser"),
      PASSWORD("jdbcPassword"),
      DRIVER("jdbcDriver"),
      DATABASE_POOL_NAME("dataSourceName"),
      DIGEST("digest"),
      ENCODING("encoding");

      public final String name;

      private Option(String name) {
         this.name = name;
      }

      public static Option findByName(String name) {
         for(Option opt : values()) {
            if (opt.name.equals(name)) {
               return opt;
            }
         }

         return null;
      }
   }
}
