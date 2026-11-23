package org.spark_project.jetty.client.api;

public interface ContentResponse extends Response {
   String getMediaType();

   String getEncoding();

   byte[] getContent();

   String getContentAsString();
}
