package org.spark_project.jetty.security;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletRequest;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.Loader;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.resource.Resource;
import org.spark_project.jetty.util.security.Credential;

public class JDBCLoginService extends MappedLoginService {
   private static final Logger LOG = Log.getLogger(JDBCLoginService.class);
   protected String _config;
   protected String _jdbcDriver;
   protected String _url;
   protected String _userName;
   protected String _password;
   protected String _userTableKey;
   protected String _userTablePasswordField;
   protected String _roleTableRoleField;
   protected int _cacheTime;
   protected long _lastHashPurge;
   protected Connection _con;
   protected String _userSql;
   protected String _roleSql;

   public JDBCLoginService() throws IOException {
   }

   public JDBCLoginService(String name) throws IOException {
      this.setName(name);
   }

   public JDBCLoginService(String name, String config) throws IOException {
      this.setName(name);
      this.setConfig(config);
   }

   public JDBCLoginService(String name, IdentityService identityService, String config) throws IOException {
      this.setName(name);
      this.setIdentityService(identityService);
      this.setConfig(config);
   }

   protected void doStart() throws Exception {
      Properties properties = new Properties();
      Resource resource = Resource.newResource(this._config);
      InputStream in = resource.getInputStream();
      Throwable var4 = null;

      try {
         properties.load(in);
      } catch (Throwable var15) {
         var4 = var15;
         throw var15;
      } finally {
         if (in != null) {
            if (var4 != null) {
               try {
                  in.close();
               } catch (Throwable var14) {
                  var4.addSuppressed(var14);
               }
            } else {
               in.close();
            }
         }

      }

      this._jdbcDriver = properties.getProperty("jdbcdriver");
      this._url = properties.getProperty("url");
      this._userName = properties.getProperty("username");
      this._password = properties.getProperty("password");
      String _userTable = properties.getProperty("usertable");
      this._userTableKey = properties.getProperty("usertablekey");
      String _userTableUserField = properties.getProperty("usertableuserfield");
      this._userTablePasswordField = properties.getProperty("usertablepasswordfield");
      String _roleTable = properties.getProperty("roletable");
      String _roleTableKey = properties.getProperty("roletablekey");
      this._roleTableRoleField = properties.getProperty("roletablerolefield");
      String _userRoleTable = properties.getProperty("userroletable");
      String _userRoleTableUserKey = properties.getProperty("userroletableuserkey");
      String _userRoleTableRoleKey = properties.getProperty("userroletablerolekey");
      this._cacheTime = new Integer(properties.getProperty("cachetime"));
      if (this._jdbcDriver == null || this._jdbcDriver.equals("") || this._url == null || this._url.equals("") || this._userName == null || this._userName.equals("") || this._password == null || this._cacheTime < 0) {
         LOG.warn("UserRealm " + this.getName() + " has not been properly configured");
      }

      this._cacheTime *= 1000;
      this._lastHashPurge = 0L;
      this._userSql = "select " + this._userTableKey + "," + this._userTablePasswordField + " from " + _userTable + " where " + _userTableUserField + " = ?";
      this._roleSql = "select r." + this._roleTableRoleField + " from " + _roleTable + " r, " + _userRoleTable + " u where u." + _userRoleTableUserKey + " = ? and r." + _roleTableKey + " = u." + _userRoleTableRoleKey;
      Loader.loadClass(this.getClass(), this._jdbcDriver).newInstance();
      super.doStart();
   }

   public String getConfig() {
      return this._config;
   }

   public void setConfig(String config) {
      if (this.isRunning()) {
         throw new IllegalStateException("Running");
      } else {
         this._config = config;
      }
   }

   public void connectDatabase() {
      try {
         Class.forName(this._jdbcDriver);
         this._con = DriverManager.getConnection(this._url, this._userName, this._password);
      } catch (SQLException e) {
         LOG.warn("UserRealm " + this.getName() + " could not connect to database; will try later", (Throwable)e);
      } catch (ClassNotFoundException e) {
         LOG.warn("UserRealm " + this.getName() + " could not connect to database; will try later", (Throwable)e);
      }

   }

   public UserIdentity login(String username, Object credentials, ServletRequest request) {
      long now = System.currentTimeMillis();
      if (now - this._lastHashPurge > (long)this._cacheTime || this._cacheTime == 0) {
         this._users.clear();
         this._lastHashPurge = now;
         this.closeConnection();
      }

      return super.login(username, credentials, request);
   }

   protected void loadUsers() {
   }

   /** @deprecated */
   @Deprecated
   protected UserIdentity loadUser(String username) {
      try {
         if (null == this._con) {
            this.connectDatabase();
         }

         if (null == this._con) {
            throw new SQLException("Can't connect to database");
         } else {
            PreparedStatement stat1 = this._con.prepareStatement(this._userSql);
            Throwable var3 = null;

            PreparedStatement stat2;
            try {
               stat1.setObject(1, username);
               ResultSet rs1 = stat1.executeQuery();
               Throwable var5 = null;

               try {
                  if (!rs1.next()) {
                     return null;
                  }

                  int key = rs1.getInt(this._userTableKey);
                  String credentials = rs1.getString(this._userTablePasswordField);
                  List<String> roles = new ArrayList();
                  stat2 = this._con.prepareStatement(this._roleSql);
                  Throwable var10 = null;

                  try {
                     stat2.setInt(1, key);
                     ResultSet rs2 = stat2.executeQuery();
                     Throwable var12 = null;

                     try {
                        while(rs2.next()) {
                           roles.add(rs2.getString(this._roleTableRoleField));
                        }
                     } catch (Throwable var91) {
                        var12 = var91;
                        throw var91;
                     } finally {
                        if (rs2 != null) {
                           if (var12 != null) {
                              try {
                                 rs2.close();
                              } catch (Throwable var90) {
                                 var12.addSuppressed(var90);
                              }
                           } else {
                              rs2.close();
                           }
                        }

                     }
                  } catch (Throwable var93) {
                     var10 = var93;
                     throw var93;
                  } finally {
                     if (stat2 != null) {
                        if (var10 != null) {
                           try {
                              stat2.close();
                           } catch (Throwable var89) {
                              var10.addSuppressed(var89);
                           }
                        } else {
                           stat2.close();
                        }
                     }

                  }

                  stat2 = this.putUser(username, Credential.getCredential(credentials), (String[])roles.toArray(new String[roles.size()]));
               } catch (Throwable var95) {
                  var5 = var95;
                  throw var95;
               } finally {
                  if (rs1 != null) {
                     if (var5 != null) {
                        try {
                           rs1.close();
                        } catch (Throwable var88) {
                           var5.addSuppressed(var88);
                        }
                     } else {
                        rs1.close();
                     }
                  }

               }
            } catch (Throwable var97) {
               var3 = var97;
               throw var97;
            } finally {
               if (stat1 != null) {
                  if (var3 != null) {
                     try {
                        stat1.close();
                     } catch (Throwable var87) {
                        var3.addSuppressed(var87);
                     }
                  } else {
                     stat1.close();
                  }
               }

            }

            return stat2;
         }
      } catch (SQLException e) {
         LOG.warn("UserRealm " + this.getName() + " could not load user information from database", (Throwable)e);
         this.closeConnection();
         return null;
      }
   }

   public MappedLoginService.KnownUser loadUserInfo(String username) {
      try {
         if (null == this._con) {
            this.connectDatabase();
         }

         if (null == this._con) {
            throw new SQLException("Can't connect to database");
         } else {
            PreparedStatement stat1 = this._con.prepareStatement(this._userSql);
            Throwable var3 = null;

            JDBCKnownUser var8;
            try {
               stat1.setObject(1, username);
               ResultSet rs1 = stat1.executeQuery();
               Throwable var5 = null;

               try {
                  if (!rs1.next()) {
                     return null;
                  }

                  int key = rs1.getInt(this._userTableKey);
                  String credentials = rs1.getString(this._userTablePasswordField);
                  var8 = new JDBCKnownUser(username, Credential.getCredential(credentials), key);
               } catch (Throwable var36) {
                  var5 = var36;
                  throw var36;
               } finally {
                  if (rs1 != null) {
                     if (var5 != null) {
                        try {
                           rs1.close();
                        } catch (Throwable var35) {
                           var5.addSuppressed(var35);
                        }
                     } else {
                        rs1.close();
                     }
                  }

               }
            } catch (Throwable var38) {
               var3 = var38;
               throw var38;
            } finally {
               if (stat1 != null) {
                  if (var3 != null) {
                     try {
                        stat1.close();
                     } catch (Throwable var34) {
                        var3.addSuppressed(var34);
                     }
                  } else {
                     stat1.close();
                  }
               }

            }

            return var8;
         }
      } catch (SQLException e) {
         LOG.warn("UserRealm " + this.getName() + " could not load user information from database", (Throwable)e);
         this.closeConnection();
         return null;
      }
   }

   public String[] loadRoleInfo(MappedLoginService.KnownUser user) {
      JDBCKnownUser jdbcUser = (JDBCKnownUser)user;

      try {
         if (null == this._con) {
            this.connectDatabase();
         }

         if (null == this._con) {
            throw new SQLException("Can't connect to database");
         } else {
            List<String> roles = new ArrayList();
            PreparedStatement stat2 = this._con.prepareStatement(this._roleSql);
            Throwable var5 = null;

            Object var8;
            try {
               stat2.setInt(1, jdbcUser.getUserKey());
               ResultSet rs2 = stat2.executeQuery();
               Throwable var7 = null;

               try {
                  while(rs2.next()) {
                     roles.add(rs2.getString(this._roleTableRoleField));
                  }

                  var8 = (String[])roles.toArray(new String[roles.size()]);
               } catch (Throwable var33) {
                  var8 = var33;
                  var7 = var33;
                  throw var33;
               } finally {
                  if (rs2 != null) {
                     if (var7 != null) {
                        try {
                           rs2.close();
                        } catch (Throwable var32) {
                           var7.addSuppressed(var32);
                        }
                     } else {
                        rs2.close();
                     }
                  }

               }
            } catch (Throwable var35) {
               var5 = var35;
               throw var35;
            } finally {
               if (stat2 != null) {
                  if (var5 != null) {
                     try {
                        stat2.close();
                     } catch (Throwable var31) {
                        var5.addSuppressed(var31);
                     }
                  } else {
                     stat2.close();
                  }
               }

            }

            return (String[])var8;
         }
      } catch (SQLException e) {
         LOG.warn("UserRealm " + this.getName() + " could not load user information from database", (Throwable)e);
         this.closeConnection();
         return null;
      }
   }

   private void closeConnection() {
      if (this._con != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Closing db connection for JDBCUserRealm");
         }

         try {
            this._con.close();
         } catch (Exception e) {
            LOG.ignore(e);
         }
      }

      this._con = null;
   }

   public class JDBCKnownUser extends MappedLoginService.KnownUser {
      int _userKey;

      public JDBCKnownUser(String name, Credential credential, int key) {
         super(name, credential);
         this._userKey = key;
      }

      public int getUserKey() {
         return this._userKey;
      }
   }
}
