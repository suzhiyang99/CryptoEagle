package org.spark_project.jetty.client.util;

import org.spark_project.jetty.client.api.ContentProvider;

public abstract class AbstractTypedContentProvider implements ContentProvider.Typed {
   private final String contentType;

   protected AbstractTypedContentProvider(String contentType) {
      this.contentType = contentType;
   }

   public String getContentType() {
      return this.contentType;
   }
}
