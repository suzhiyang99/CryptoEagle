package org.apache.manifoldcf.core.jdbcpool;

import java.sql.Connection;

public class WrappedConnection {
   public static final String _rcsid = "@(#)$Id$";
   protected Connection connection;
   protected ConnectionPool owner;
   protected Exception instantiationException;

   public WrappedConnection(ConnectionPool owner, Connection connection) {
      this(owner, connection, (Exception)null);
   }

   public WrappedConnection(ConnectionPool owner, Connection connection, Exception instantiationException) {
      this.owner = owner;
      this.connection = connection;
      this.instantiationException = instantiationException;
   }

   public Connection getConnection() {
      return this.connection;
   }

   public void release() {
      this.owner.releaseConnection(this);
      this.connection = null;
   }

   public Exception getInstantiationException() {
      return this.instantiationException;
   }
}
