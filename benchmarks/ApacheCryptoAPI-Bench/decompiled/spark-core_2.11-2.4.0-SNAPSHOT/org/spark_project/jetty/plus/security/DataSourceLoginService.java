package org.spark_project.jetty.plus.security;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.ServletRequest;
import javax.sql.DataSource;
import org.spark_project.jetty.plus.jndi.NamingEntryUtil;
import org.spark_project.jetty.security.IdentityService;
import org.spark_project.jetty.security.MappedLoginService;
import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.security.Credential;

public class DataSourceLoginService extends MappedLoginService {
   private static final Logger LOG = Log.getLogger(DataSourceLoginService.class);
   private String _jndiName = "javax.sql.DataSource/default";
   private DataSource _datasource;
   private Server _server;
   private String _userTableName = "users";
   private String _userTableKey = "id";
   private String _userTableUserField = "username";
   private String _userTablePasswordField = "pwd";
   private String _roleTableName = "roles";
   private String _roleTableKey = "id";
   private String _roleTableRoleField = "role";
   private String _userRoleTableName = "user_roles";
   private String _userRoleTableUserKey = "user_id";
   private String _userRoleTableRoleKey = "role_id";
   private int _cacheMs = 30000;
   private long _lastPurge = 0L;
   private String _userSql;
   private String _roleSql;
   private boolean _createTables = false;

   public DataSourceLoginService() {
   }

   public DataSourceLoginService(String name) {
      this.setName(name);
   }

   public DataSourceLoginService(String name, IdentityService identityService) {
      this.setName(name);
      this.setIdentityService(identityService);
   }

   public void setJndiName(String jndi) {
      this._jndiName = jndi;
   }

   public String getJndiName() {
      return this._jndiName;
   }

   public void setServer(Server server) {
      this._server = server;
   }

   public Server getServer() {
      return this._server;
   }

   public void setCreateTables(boolean createTables) {
      this._createTables = createTables;
   }

   public boolean getCreateTables() {
      return this._createTables;
   }

   public void setUserTableName(String name) {
      this._userTableName = name;
   }

   public String getUserTableName() {
      return this._userTableName;
   }

   public String getUserTableKey() {
      return this._userTableKey;
   }

   public void setUserTableKey(String tableKey) {
      this._userTableKey = tableKey;
   }

   public String getUserTableUserField() {
      return this._userTableUserField;
   }

   public void setUserTableUserField(String tableUserField) {
      this._userTableUserField = tableUserField;
   }

   public String getUserTablePasswordField() {
      return this._userTablePasswordField;
   }

   public void setUserTablePasswordField(String tablePasswordField) {
      this._userTablePasswordField = tablePasswordField;
   }

   public String getRoleTableName() {
      return this._roleTableName;
   }

   public void setRoleTableName(String tableName) {
      this._roleTableName = tableName;
   }

   public String getRoleTableKey() {
      return this._roleTableKey;
   }

   public void setRoleTableKey(String tableKey) {
      this._roleTableKey = tableKey;
   }

   public String getRoleTableRoleField() {
      return this._roleTableRoleField;
   }

   public void setRoleTableRoleField(String tableRoleField) {
      this._roleTableRoleField = tableRoleField;
   }

   public String getUserRoleTableName() {
      return this._userRoleTableName;
   }

   public void setUserRoleTableName(String roleTableName) {
      this._userRoleTableName = roleTableName;
   }

   public String getUserRoleTableUserKey() {
      return this._userRoleTableUserKey;
   }

   public void setUserRoleTableUserKey(String roleTableUserKey) {
      this._userRoleTableUserKey = roleTableUserKey;
   }

   public String getUserRoleTableRoleKey() {
      return this._userRoleTableRoleKey;
   }

   public void setUserRoleTableRoleKey(String roleTableRoleKey) {
      this._userRoleTableRoleKey = roleTableRoleKey;
   }

   public void setCacheMs(int ms) {
      this._cacheMs = ms;
   }

   public int getCacheMs() {
      return this._cacheMs;
   }

   protected void loadUsers() {
   }

   /** @deprecated */
   @Deprecated
   protected UserIdentity loadUser(String userName) {
      try {
         Connection connection = this.getConnection();
         Throwable var3 = null;

         PreparedStatement statement2;
         try {
            PreparedStatement statement1 = connection.prepareStatement(this._userSql);
            Throwable var5 = null;

            try {
               statement1.setObject(1, userName);
               ResultSet rs1 = statement1.executeQuery();
               Throwable var7 = null;

               try {
                  if (!rs1.next()) {
                     return null;
                  }

                  int key = rs1.getInt(this._userTableKey);
                  String credentials = rs1.getString(this._userTablePasswordField);
                  List<String> roles = new ArrayList();
                  statement2 = connection.prepareStatement(this._roleSql);
                  Throwable var12 = null;

                  try {
                     statement2.setInt(1, key);
                     ResultSet rs2 = statement2.executeQuery();
                     Throwable var14 = null;

                     try {
                        while(rs2.next()) {
                           roles.add(rs2.getString(this._roleTableRoleField));
                        }
                     } catch (Throwable var137) {
                        var14 = var137;
                        throw var137;
                     } finally {
                        if (rs2 != null) {
                           if (var14 != null) {
                              try {
                                 rs2.close();
                              } catch (Throwable var136) {
                                 var14.addSuppressed(var136);
                              }
                           } else {
                              rs2.close();
                           }
                        }

                     }
                  } catch (Throwable var139) {
                     var12 = var139;
                     throw var139;
                  } finally {
                     if (statement2 != null) {
                        if (var12 != null) {
                           try {
                              statement2.close();
                           } catch (Throwable var135) {
                              var12.addSuppressed(var135);
                           }
                        } else {
                           statement2.close();
                        }
                     }

                  }

                  statement2 = this.putUser(userName, Credential.getCredential(credentials), (String[])roles.toArray(new String[roles.size()]));
               } catch (Throwable var141) {
                  var7 = var141;
                  throw var141;
               } finally {
                  if (rs1 != null) {
                     if (var7 != null) {
                        try {
                           rs1.close();
                        } catch (Throwable var134) {
                           var7.addSuppressed(var134);
                        }
                     } else {
                        rs1.close();
                     }
                  }

               }
            } catch (Throwable var143) {
               var5 = var143;
               throw var143;
            } finally {
               if (statement1 != null) {
                  if (var5 != null) {
                     try {
                        statement1.close();
                     } catch (Throwable var133) {
                        var5.addSuppressed(var133);
                     }
                  } else {
                     statement1.close();
                  }
               }

            }
         } catch (Throwable var145) {
            var3 = var145;
            throw var145;
         } finally {
            if (connection != null) {
               if (var3 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var132) {
                     var3.addSuppressed(var132);
                  }
               } else {
                  connection.close();
               }
            }

         }

         return statement2;
      } catch (NamingException e) {
         LOG.warn("No datasource for " + this._jndiName, (Throwable)e);
      } catch (SQLException e) {
         LOG.warn("Problem loading user info for " + userName, (Throwable)e);
      }

      return null;
   }

   public MappedLoginService.KnownUser loadUserInfo(String username) {
      try {
         Connection connection = this.getConnection();
         Throwable var3 = null;

         DBUser var10;
         try {
            PreparedStatement statement1 = connection.prepareStatement(this._userSql);
            Throwable var5 = null;

            try {
               statement1.setObject(1, username);
               ResultSet rs1 = statement1.executeQuery();
               Throwable var7 = null;

               try {
                  if (!rs1.next()) {
                     return null;
                  }

                  int key = rs1.getInt(this._userTableKey);
                  String credentials = rs1.getString(this._userTablePasswordField);
                  var10 = new DBUser(username, Credential.getCredential(credentials), key);
               } catch (Throwable var66) {
                  var7 = var66;
                  throw var66;
               } finally {
                  if (rs1 != null) {
                     if (var7 != null) {
                        try {
                           rs1.close();
                        } catch (Throwable var65) {
                           var7.addSuppressed(var65);
                        }
                     } else {
                        rs1.close();
                     }
                  }

               }
            } catch (Throwable var68) {
               var5 = var68;
               throw var68;
            } finally {
               if (statement1 != null) {
                  if (var5 != null) {
                     try {
                        statement1.close();
                     } catch (Throwable var64) {
                        var5.addSuppressed(var64);
                     }
                  } else {
                     statement1.close();
                  }
               }

            }
         } catch (Throwable var70) {
            var3 = var70;
            throw var70;
         } finally {
            if (connection != null) {
               if (var3 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var63) {
                     var3.addSuppressed(var63);
                  }
               } else {
                  connection.close();
               }
            }

         }

         return var10;
      } catch (NamingException e) {
         LOG.warn("No datasource for " + this._jndiName, (Throwable)e);
      } catch (SQLException e) {
         LOG.warn("Problem loading user info for " + username, (Throwable)e);
      }

      return null;
   }

   public String[] loadRoleInfo(MappedLoginService.KnownUser user) {
      DBUser dbuser = (DBUser)user;

      try {
         Connection connection = this.getConnection();
         Throwable var4 = null;

         Object var10;
         try {
            PreparedStatement statement2 = connection.prepareStatement(this._roleSql);
            Throwable var6 = null;

            try {
               List<String> roles = new ArrayList();
               statement2.setInt(1, dbuser.getKey());
               ResultSet rs2 = statement2.executeQuery();
               Throwable var9 = null;

               try {
                  while(rs2.next()) {
                     roles.add(rs2.getString(this._roleTableRoleField));
                  }

                  var10 = (String[])roles.toArray(new String[roles.size()]);
               } catch (Throwable var60) {
                  var10 = var60;
                  var9 = var60;
                  throw var60;
               } finally {
                  if (rs2 != null) {
                     if (var9 != null) {
                        try {
                           rs2.close();
                        } catch (Throwable var59) {
                           var9.addSuppressed(var59);
                        }
                     } else {
                        rs2.close();
                     }
                  }

               }
            } catch (Throwable var62) {
               var6 = var62;
               throw var62;
            } finally {
               if (statement2 != null) {
                  if (var6 != null) {
                     try {
                        statement2.close();
                     } catch (Throwable var58) {
                        var6.addSuppressed(var58);
                     }
                  } else {
                     statement2.close();
                  }
               }

            }
         } catch (Throwable var64) {
            var4 = var64;
            throw var64;
         } finally {
            if (connection != null) {
               if (var4 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var57) {
                     var4.addSuppressed(var57);
                  }
               } else {
                  connection.close();
               }
            }

         }

         return (String[])var10;
      } catch (NamingException e) {
         LOG.warn("No datasource for " + this._jndiName, (Throwable)e);
      } catch (SQLException e) {
         LOG.warn("Problem loading user info for " + user.getName(), (Throwable)e);
      }

      return null;
   }

   public UserIdentity login(String username, Object credentials, ServletRequest request) {
      long now = System.currentTimeMillis();
      if (now - this._lastPurge > (long)this._cacheMs || this._cacheMs == 0) {
         this._users.clear();
         this._lastPurge = now;
      }

      return super.login(username, credentials, request);
   }

   public void initDb() throws NamingException, SQLException {
      if (this._datasource == null) {
         InitialContext ic = new InitialContext();

         assert ic != null;

         if (this._server != null) {
            try {
               this._datasource = (DataSource)NamingEntryUtil.lookup(this._server, this._jndiName);
            } catch (NameNotFoundException var3) {
            }
         }

         if (this._datasource == null) {
            this._datasource = (DataSource)NamingEntryUtil.lookup((Object)null, this._jndiName);
         }

         this._userSql = "select " + this._userTableKey + "," + this._userTablePasswordField + " from " + this._userTableName + " where " + this._userTableUserField + " = ?";
         this._roleSql = "select r." + this._roleTableRoleField + " from " + this._roleTableName + " r, " + this._userRoleTableName + " u where u." + this._userRoleTableUserKey + " = ? and r." + this._roleTableKey + " = u." + this._userRoleTableRoleKey;
         this.prepareTables();
      }
   }

   private void prepareTables() throws NamingException, SQLException {
      if (this._createTables) {
         boolean autocommit = true;
         Connection connection = this.getConnection();

         try {
            Statement stmt = connection.createStatement();
            Throwable var4 = null;

            try {
               autocommit = connection.getAutoCommit();
               connection.setAutoCommit(false);
               DatabaseMetaData metaData = connection.getMetaData();
               String tableName = metaData.storesLowerCaseIdentifiers() ? this._userTableName.toLowerCase(Locale.ENGLISH) : (metaData.storesUpperCaseIdentifiers() ? this._userTableName.toUpperCase(Locale.ENGLISH) : this._userTableName);
               ResultSet result = metaData.getTables((String)null, (String)null, tableName, (String[])null);
               Throwable var8 = null;

               try {
                  if (!result.next()) {
                     stmt.executeUpdate("create table " + this._userTableName + "(" + this._userTableKey + " integer," + this._userTableUserField + " varchar(100) not null unique," + this._userTablePasswordField + " varchar(20) not null, primary key(" + this._userTableKey + "))");
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Created table " + this._userTableName);
                     }
                  }
               } catch (Throwable var184) {
                  var8 = var184;
                  throw var184;
               } finally {
                  if (result != null) {
                     if (var8 != null) {
                        try {
                           result.close();
                        } catch (Throwable var176) {
                           var8.addSuppressed(var176);
                        }
                     } else {
                        result.close();
                     }
                  }

               }

               tableName = metaData.storesLowerCaseIdentifiers() ? this._roleTableName.toLowerCase(Locale.ENGLISH) : (metaData.storesUpperCaseIdentifiers() ? this._roleTableName.toUpperCase(Locale.ENGLISH) : this._roleTableName);
               result = metaData.getTables((String)null, (String)null, tableName, (String[])null);
               var8 = null;

               try {
                  if (!result.next()) {
                     String str = "create table " + this._roleTableName + " (" + this._roleTableKey + " integer, " + this._roleTableRoleField + " varchar(100) not null unique, primary key(" + this._roleTableKey + "))";
                     stmt.executeUpdate(str);
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Created table " + this._roleTableName);
                     }
                  }
               } catch (Throwable var182) {
                  var8 = var182;
                  throw var182;
               } finally {
                  if (result != null) {
                     if (var8 != null) {
                        try {
                           result.close();
                        } catch (Throwable var175) {
                           var8.addSuppressed(var175);
                        }
                     } else {
                        result.close();
                     }
                  }

               }

               tableName = metaData.storesLowerCaseIdentifiers() ? this._userRoleTableName.toLowerCase(Locale.ENGLISH) : (metaData.storesUpperCaseIdentifiers() ? this._userRoleTableName.toUpperCase(Locale.ENGLISH) : this._userRoleTableName);
               result = metaData.getTables((String)null, (String)null, tableName, (String[])null);
               var8 = null;

               try {
                  if (!result.next()) {
                     stmt.executeUpdate("create table " + this._userRoleTableName + " (" + this._userRoleTableUserKey + " integer, " + this._userRoleTableRoleKey + " integer, primary key (" + this._userRoleTableUserKey + ", " + this._userRoleTableRoleKey + "))");
                     stmt.executeUpdate("create index indx_user_role on " + this._userRoleTableName + "(" + this._userRoleTableUserKey + ")");
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Created table " + this._userRoleTableName + " and index");
                     }
                  }
               } catch (Throwable var180) {
                  var8 = var180;
                  throw var180;
               } finally {
                  if (result != null) {
                     if (var8 != null) {
                        try {
                           result.close();
                        } catch (Throwable var174) {
                           var8.addSuppressed(var174);
                        }
                     } else {
                        result.close();
                     }
                  }

               }

               connection.commit();
            } catch (Throwable var186) {
               var4 = var186;
               throw var186;
            } finally {
               if (stmt != null) {
                  if (var4 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var173) {
                        var4.addSuppressed(var173);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } finally {
            try {
               connection.setAutoCommit(autocommit);
            } catch (SQLException e) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Prepare tables", (Throwable)e);
               }
            } finally {
               try {
                  connection.close();
               } catch (SQLException e) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Prepare tables", (Throwable)e);
                  }
               }

            }

         }
      } else if (LOG.isDebugEnabled()) {
         LOG.debug("createTables false");
      }

   }

   private Connection getConnection() throws NamingException, SQLException {
      this.initDb();
      return this._datasource.getConnection();
   }

   public class DBUser extends MappedLoginService.KnownUser {
      private int _key;

      public DBUser(String name, Credential credential, int key) {
         super(name, credential);
         this._key = key;
      }

      public int getKey() {
         return this._key;
      }
   }
}
