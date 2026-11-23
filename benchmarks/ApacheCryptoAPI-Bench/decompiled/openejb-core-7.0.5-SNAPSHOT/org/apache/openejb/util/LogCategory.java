package org.apache.openejb.util;

public final class LogCategory {
   private final String name;
   public static final LogCategory OPENEJB = new LogCategory("OpenEJB");
   public static final LogCategory OPENEJB_ADMIN;
   public static final LogCategory OPENEJB_STARTUP;
   public static final LogCategory OPENEJB_STARTUP_CONFIG;
   public static final LogCategory OPENEJB_STARTUP_VALIDATION;
   public static final LogCategory OPENEJB_SERVER;
   public static final LogCategory OPENEJB_SERVER_REMOTE;
   public static final LogCategory OPENEJB_SECURITY;
   public static final LogCategory OPENEJB_RESOURCE_JDBC;
   public static final LogCategory OPENEJB_CONNECTOR;
   public static final LogCategory OPENEJB_DEPLOY;
   public static final LogCategory OPENEJB_HSQL;
   public static final LogCategory OPENEJB_WS;
   public static final LogCategory OPENEJB_RS;
   public static final LogCategory OPENEJB_JPA;
   public static final LogCategory OPENEJB_CDI;
   public static final LogCategory TRANSACTION;
   public static final LogCategory ACTIVEMQ;
   public static final LogCategory GERONIMO;
   public static final LogCategory OPENJPA;
   public static final LogCategory CORBA_ADAPTER;
   public static final LogCategory AXIS;
   public static final LogCategory AXIS2;
   public static final LogCategory CXF;
   public static final LogCategory TIMER;
   public static final LogCategory HTTPSERVER;
   public static final LogCategory SERVICEPOOL;
   public static final LogCategory OPENEJB_SQL;
   public static final LogCategory MONITORING;

   private LogCategory(String name) {
      this.name = name == null ? "" : name;
   }

   public String getName() {
      return this.name;
   }

   public LogCategory createChild(String child) {
      return new LogCategory(this.name + "." + child);
   }

   public boolean equals(Object o) {
      return this == o || o != null && this.getClass() == o.getClass() && this.name.equals(((LogCategory)LogCategory.class.cast(o)).name);
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   static {
      OPENEJB_ADMIN = OPENEJB.createChild("admin");
      OPENEJB_STARTUP = OPENEJB.createChild("startup");
      OPENEJB_STARTUP_CONFIG = OPENEJB_STARTUP.createChild("config");
      OPENEJB_STARTUP_VALIDATION = OPENEJB_STARTUP.createChild("validation");
      OPENEJB_SERVER = OPENEJB.createChild("server");
      OPENEJB_SERVER_REMOTE = OPENEJB_SERVER.createChild("remote");
      OPENEJB_SECURITY = OPENEJB.createChild("security");
      OPENEJB_RESOURCE_JDBC = OPENEJB.createChild("resource.jdbc");
      OPENEJB_CONNECTOR = OPENEJB.createChild("connector");
      OPENEJB_DEPLOY = OPENEJB.createChild("deploy");
      OPENEJB_HSQL = OPENEJB.createChild("hsql");
      OPENEJB_WS = OPENEJB.createChild("ws");
      OPENEJB_RS = OPENEJB.createChild("rs");
      OPENEJB_JPA = OPENEJB.createChild("jpa");
      OPENEJB_CDI = OPENEJB.createChild("cdi");
      TRANSACTION = new LogCategory("Transaction");
      ACTIVEMQ = new LogCategory("org.apache.activemq");
      GERONIMO = new LogCategory("org.apache.geronimo");
      OPENJPA = new LogCategory("openjpa");
      CORBA_ADAPTER = new LogCategory("CORBA-Adapter");
      AXIS = new LogCategory("axis");
      AXIS2 = new LogCategory("axis");
      CXF = new LogCategory("cxf");
      TIMER = new LogCategory("Timer");
      HTTPSERVER = OPENEJB_SERVER.createChild("http");
      SERVICEPOOL = OPENEJB_SERVER.createChild("pool");
      OPENEJB_SQL = OPENEJB.createChild("sql");
      MONITORING = OPENEJB.createChild("monitoring");
   }
}
