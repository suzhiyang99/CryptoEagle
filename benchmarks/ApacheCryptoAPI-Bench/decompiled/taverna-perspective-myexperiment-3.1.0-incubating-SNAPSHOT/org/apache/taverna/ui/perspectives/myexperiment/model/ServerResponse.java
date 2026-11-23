package org.apache.taverna.ui.perspectives.myexperiment.model;

import org.jdom.Document;

public class ServerResponse {
   public static int LOCAL_FAILURE = -1;
   private int iResponseCode;
   private Document docResponseBody;

   public ServerResponse() {
   }

   public ServerResponse(int responseCode, Document responseBody) {
      this.iResponseCode = responseCode;
      this.docResponseBody = responseBody;
   }

   public int getResponseCode() {
      return this.iResponseCode;
   }

   public void setResponseCode(int responseCode) {
      this.iResponseCode = responseCode;
   }

   public Document getResponseBody() {
      return this.docResponseBody;
   }

   public void setResponseBody(Document responseBody) {
      this.docResponseBody = responseBody;
   }
}
