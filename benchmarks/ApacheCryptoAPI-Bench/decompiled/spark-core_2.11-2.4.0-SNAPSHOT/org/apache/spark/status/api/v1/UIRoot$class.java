package org.apache.spark.status.api.v1;

import java.util.zip.ZipOutputStream;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import scala.Option;

public abstract class UIRoot$class {
   public static void writeEventLogs(UIRoot $this, String appId, Option attemptId, ZipOutputStream zipStream) {
      Response.serverError().entity("Event logs are only available through the history server.").status(Status.SERVICE_UNAVAILABLE).build();
   }

   public static void $init$(UIRoot $this) {
   }
}
