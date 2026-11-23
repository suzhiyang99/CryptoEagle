package org.apache.spark.status.api.v1;

import javax.ws.rs.GET;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.TraversableLike;
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001%2Q!\u0001\u0002\u0001\u00059\u0011Qd\u00148f\u0003B\u0004H.[2bi&|g.\u0011;uK6\u0004HOU3t_V\u00148-\u001a\u0006\u0003\u0007\u0011\t!A^\u0019\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0007gR\fG/^:\u000b\u0005%Q\u0011!B:qCJ\\'BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\"\u0001A\b\u0011\u0005A\tR\"\u0001\u0002\n\u0005I\u0011!aG!cgR\u0014\u0018m\u0019;BaBd\u0017nY1uS>t'+Z:pkJ\u001cW\rC\u0003\u0015\u0001\u0011\u0005a#\u0001\u0004=S:LGOP\u0002\u0001)\u00059\u0002C\u0001\t\u0001\u0011\u0015I\u0002\u0001\"\u0001\u001b\u0003)9W\r^!ui\u0016l\u0007\u000f\u001e\u000b\u00027A\u0011\u0001\u0003H\u0005\u0003;\t\u0011a#\u00119qY&\u001c\u0017\r^5p]\u0006#H/Z7qi&sgm\u001c\u0015\u00031}\u0001\"\u0001I\u0014\u000e\u0003\u0005R!AI\u0012\u0002\u0005I\u001c(B\u0001\u0013&\u0003\t98OC\u0001'\u0003\u0015Q\u0017M^1y\u0013\tA\u0013EA\u0002H\u000bR\u0003"
)
public class OneApplicationAttemptResource extends AbstractApplicationResource {
   @GET
   public ApplicationAttemptInfo getAttempt() {
      return (ApplicationAttemptInfo)this.uiRoot().getApplicationInfo(this.appId()).flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final OneApplicationAttemptResource $outer;

         public final Option<ApplicationAttemptInfo> apply(ApplicationInfo app) {
            return ((TraversableLike)app.attempts().filter(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final boolean apply(ApplicationAttemptInfo x$8) {
                  boolean var3;
                  label23: {
                     Option var10000 = x$8.attemptId();
                     String var2 = this.$outer.org$apache$spark$status$api$v1$OneApplicationAttemptResource$$anonfun$$$outer().attemptId();
                     if (var10000 == null) {
                        if (var2 == null) {
                           break label23;
                        }
                     } else if (var10000.equals(var2)) {
                        break label23;
                     }

                     var3 = false;
                     return var3;
                  }

                  var3 = true;
                  return var3;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            })).headOption();
         }

         // $FF: synthetic method
         public OneApplicationAttemptResource org$apache$spark$status$api$v1$OneApplicationAttemptResource$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final OneApplicationAttemptResource $outer;

         public final Nothing$ apply() {
            throw new NotFoundException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"unknown app ", ", attempt ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.appId(), this.$outer.attemptId()})));
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
