package org.apache.spark.status.api.v1;

import java.util.NoSuchElementException;
import org.apache.spark.ui.SparkUI;
import scala.Function1;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;

public abstract class BaseAppResource$class {
   public static Object withUI(BaseAppResource $this, Function1 fn) {
      try {
         return $this.uiRoot().withSparkUI($this.appId(), Option$.MODULE$.apply($this.attemptId()), new Serializable($this, fn) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BaseAppResource $outer;
            private final Function1 fn$1;

            public final T apply(SparkUI ui) {
               String user = this.$outer.httpRequest().getRemoteUser();
               if (ui.securityManager().checkUIViewPermissions(user)) {
                  return (T)this.fn$1.apply(ui);
               } else {
                  throw new ForbiddenException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"user \"", "\" is not authorized"})))).raw(Predef$.MODULE$.genericWrapArray(new Object[]{user})));
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.fn$1 = fn$1;
               }
            }
         });
      } catch (NoSuchElementException var3) {
         String appKey = (String)Option$.MODULE$.apply($this.attemptId()).map(new Serializable($this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BaseAppResource $outer;

            public final String apply(String x$1) {
               return (new StringBuilder()).append((Object)this.$outer.appId()).append((Object)"/").append((Object)x$1).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }).getOrElse(new Serializable($this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final BaseAppResource $outer;

            public final String apply() {
               return this.$outer.appId();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         throw new NotFoundException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"no such app: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{appKey})));
      }
   }

   public static void $init$(BaseAppResource $this) {
   }
}
