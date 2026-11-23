package org.spark_project.jetty.server;

import java.io.IOException;

public class HttpInputOverHTTP extends HttpInput {
   public HttpInputOverHTTP(HttpChannelState state) {
      super(state);
   }

   protected void produceContent() throws IOException {
      ((HttpConnection)this.getHttpChannelState().getHttpChannel().getEndPoint().getConnection()).fillAndParseForContent();
   }

   protected void blockForContent() throws IOException {
      ((HttpConnection)this.getHttpChannelState().getHttpChannel().getEndPoint().getConnection()).blockingReadFillInterested();

      try {
         super.blockForContent();
      } catch (Throwable e) {
         ((HttpConnection)this.getHttpChannelState().getHttpChannel().getEndPoint().getConnection()).blockingReadException(e);
      }

   }
}
