package org.apache.spark;

import org.apache.hadoop.io.Text;
import scala.None$;
import scala.Option;

public final class SecurityManager$ {
   public static final SecurityManager$ MODULE$;
   private final String SPARK_AUTH_CONF;
   private final String SPARK_AUTH_SECRET_CONF;
   private final String ENV_AUTH_SECRET;
   private final Text SECRET_LOOKUP_KEY;

   static {
      new SecurityManager$();
   }

   public String SPARK_AUTH_CONF() {
      return this.SPARK_AUTH_CONF;
   }

   public String SPARK_AUTH_SECRET_CONF() {
      return this.SPARK_AUTH_SECRET_CONF;
   }

   public String ENV_AUTH_SECRET() {
      return this.ENV_AUTH_SECRET;
   }

   public Text SECRET_LOOKUP_KEY() {
      return this.SECRET_LOOKUP_KEY;
   }

   public Option<byte[]> $lessinit$greater$default$2() {
      return None$.MODULE$;
   }

   private SecurityManager$() {
      MODULE$ = this;
      this.SPARK_AUTH_CONF = org.apache.spark.internal.config.package$.MODULE$.NETWORK_AUTH_ENABLED().key();
      this.SPARK_AUTH_SECRET_CONF = "spark.authenticate.secret";
      this.ENV_AUTH_SECRET = "_SPARK_AUTH_SECRET";
      this.SECRET_LOOKUP_KEY = new Text("sparkCookie");
   }
}
