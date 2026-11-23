package org.spark_project.jetty.server;

import java.util.List;
import org.spark_project.jetty.http.BadMessageException;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.MetaData;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;

public interface ConnectionFactory {
   String getProtocol();

   List<String> getProtocols();

   Connection newConnection(Connector var1, EndPoint var2);

   public interface Upgrading extends ConnectionFactory {
      Connection upgradeConnection(Connector var1, EndPoint var2, MetaData.Request var3, HttpFields var4) throws BadMessageException;
   }
}
