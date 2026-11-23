package org.apache.spark.status.api.v1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A2Q!\u0001\u0002\u0001\u00059\u0011qbU5na2,G)\u0019;f!\u0006\u0014\u0018-\u001c\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\"\u0001A\b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g\u0011!1\u0002A!b\u0001\n\u0003A\u0012!D8sS\u001eLg.\u00197WC2,Xm\u0001\u0001\u0016\u0003e\u0001\"AG\u000f\u000f\u0005AY\u0012B\u0001\u000f\u0012\u0003\u0019\u0001&/\u001a3fM&\u0011ad\b\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005q\t\u0002\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\r\u0002\u001d=\u0014\u0018nZ5oC24\u0016\r\\;fA!)1\u0005\u0001C\u0001I\u00051A(\u001b8jiz\"\"!J\u0014\u0011\u0005\u0019\u0002Q\"\u0001\u0002\t\u000bY\u0011\u0003\u0019A\r\t\u000f%\u0002!\u0019!C\u0001U\u0005IA/[7fgR\fW\u000e]\u000b\u0002WA\u0011\u0001\u0003L\u0005\u0003[E\u0011A\u0001T8oO\"1q\u0006\u0001Q\u0001\n-\n!\u0002^5nKN$\u0018-\u001c9!\u0001"
)
public class SimpleDateParam {
   private final String originalValue;
   private final long timestamp;

   public String originalValue() {
      return this.originalValue;
   }

   public long timestamp() {
      return this.timestamp;
   }

   private final long liftedTree1$1(SimpleDateFormat format$1) {
      long var10000;
      try {
         var10000 = format$1.parse(this.originalValue()).getTime();
      } catch (ParseException var4) {
         SimpleDateFormat gmtDay = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
         gmtDay.setTimeZone(TimeZone.getTimeZone("GMT"));

         try {
            var10000 = gmtDay.parse(this.originalValue()).getTime();
         } catch (ParseException var3) {
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity((new StringBuilder()).append((Object)"Couldn't parse date: ").append((Object)this.originalValue()).toString()).build());
         }
      }

      return var10000;
   }

   public SimpleDateParam(String originalValue) {
      this.originalValue = originalValue;
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz", Locale.US);
      this.timestamp = this.liftedTree1$1(format);
   }
}
