package org.apache.spark.status.api.v1;

import javax.ws.rs.GET;
import scala.Option;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001%2Q!\u0001\u0002\u0001\u00059\u0011ac\u00148f\u0003B\u0004H.[2bi&|gNU3t_V\u00148-\u001a\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\"\u0001A\b\u0011\u0005A\tR\"\u0001\u0002\n\u0005I\u0011!aG!cgR\u0014\u0018m\u0019;BaBd\u0017nY1uS>t'+Z:pkJ\u001cW\rC\u0003\u0015\u0001\u0011\u0005a#\u0001\u0004=S:LGOP\u0002\u0001)\u00059\u0002C\u0001\t\u0001\u0011\u0015I\u0002\u0001\"\u0001\u001b\u0003\u00199W\r^!qaR\t1\u0004\u0005\u0002\u00119%\u0011QD\u0001\u0002\u0010\u0003B\u0004H.[2bi&|g.\u00138g_\"\u0012\u0001d\b\t\u0003A\u001dj\u0011!\t\u0006\u0003E\r\n!A]:\u000b\u0005\u0011*\u0013AA<t\u0015\u00051\u0013!\u00026bm\u0006D\u0018B\u0001\u0015\"\u0005\r9U\t\u0016"
)
public class OneApplicationResource extends AbstractApplicationResource {
   @GET
   public ApplicationInfo getApp() {
      Option app = this.uiRoot().getApplicationInfo(this.appId());
      return (ApplicationInfo)app.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final OneApplicationResource $outer;

         public final Nothing$ apply() {
            throw new NotFoundException((new StringBuilder()).append((Object)"unknown app: ").append((Object)this.$outer.appId()).toString());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }
}
