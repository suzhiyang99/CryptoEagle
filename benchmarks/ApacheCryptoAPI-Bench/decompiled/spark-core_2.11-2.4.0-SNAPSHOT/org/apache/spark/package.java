package org.apache.spark;

import java.io.InputStream;
import java.util.Properties;
import scala.MatchError;
import scala.Tuple6;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0011<Q!\u0001\u0002\t\u0002%\tq\u0001]1dW\u0006<WM\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h\u0007\u0001\u0001\"AC\u0006\u000e\u0003\t1Q\u0001\u0004\u0002\t\u00025\u0011q\u0001]1dW\u0006<Wm\u0005\u0002\f\u001dA\u0011qBE\u0007\u0002!)\t\u0011#A\u0003tG\u0006d\u0017-\u0003\u0002\u0014!\t1\u0011I\\=SK\u001aDQ!F\u0006\u0005\u0002Y\ta\u0001P5oSRtD#A\u0005\b\u000baY\u0001\u0012B\r\u0002\u001dM\u0003\u0018M]6Ck&dG-\u00138g_B\u0011!dG\u0007\u0002\u0017\u0019)Ad\u0003E\u0005;\tq1\u000b]1sW\n+\u0018\u000e\u001c3J]\u001a|7CA\u000e\u000f\u0011\u0015)2\u0004\"\u0001 )\u0005I\u0002BC\u0011\u001c!\u0003\u0005\u0019\u0011)A\u0005E\u0005\u0019\u0001\u0010J\u0019\u0011\u0011=\u0019S%J\u0013&K\u0015J!\u0001\n\t\u0003\rQ+\b\u000f\\37!\t13&D\u0001(\u0015\tA\u0013&\u0001\u0003mC:<'\"\u0001\u0016\u0002\t)\fg/Y\u0005\u0003Y\u001d\u0012aa\u0015;sS:<\u0007b\u0002\u0018\u001c\u0005\u0004%\taL\u0001\u000egB\f'o[0wKJ\u001c\u0018n\u001c8\u0016\u0003A\u0002\"!\r\u001b\u000f\u0005=\u0011\u0014BA\u001a\u0011\u0003\u0019\u0001&/\u001a3fM&\u0011A&\u000e\u0006\u0003gAAaaN\u000e!\u0002\u0013\u0001\u0014AD:qCJ\\wL^3sg&|g\u000e\t\u0005\bsm\u0011\r\u0011\"\u00010\u00031\u0019\b/\u0019:l?\n\u0014\u0018M\\2i\u0011\u0019Y4\u0004)A\u0005a\u0005i1\u000f]1sW~\u0013'/\u00198dQ\u0002Bq!P\u000eC\u0002\u0013\u0005q&\u0001\bta\u0006\u00148n\u0018:fm&\u001c\u0018n\u001c8\t\r}Z\u0002\u0015!\u00031\u0003=\u0019\b/\u0019:l?J,g/[:j_:\u0004\u0003bB!\u001c\u0005\u0004%\taL\u0001\u0011gB\f'o[0ck&dGmX;tKJDaaQ\u000e!\u0002\u0013\u0001\u0014!E:qCJ\\wLY;jY\u0012|Vo]3sA!9Qi\u0007b\u0001\n\u0003y\u0013AD:qCJ\\wL]3q_~+(\u000f\u001c\u0005\u0007\u000fn\u0001\u000b\u0011\u0002\u0019\u0002\u001fM\u0004\u0018M]6`e\u0016\u0004xnX;sY\u0002Bq!S\u000eC\u0002\u0013\u0005q&\u0001\tta\u0006\u00148n\u00182vS2$w\fZ1uK\"11j\u0007Q\u0001\nA\n\u0011c\u001d9be.|&-^5mI~#\u0017\r^3!\u0011\u001di5B1A\u0005\u0002=\nQb\u0015)B%.{f+\u0012*T\u0013>s\u0005BB(\fA\u0003%\u0001'\u0001\bT!\u0006\u00136j\u0018,F%NKuJ\u0014\u0011\t\u000fE[!\u0019!C\u0001_\u0005a1\u000bU!S\u0017~\u0013%+\u0011(D\u0011\"11k\u0003Q\u0001\nA\nQb\u0015)B%.{&IU!O\u0007\"\u0003\u0003bB+\f\u0005\u0004%\taL\u0001\u000f'B\u000b%kS0S\u000bZK5+S(O\u0011\u001996\u0002)A\u0005a\u0005y1\u000bU!S\u0017~\u0013VIV%T\u0013>s\u0005\u0005C\u0004Z\u0017\t\u0007I\u0011A\u0018\u0002!M\u0003\u0016IU&`\u0005VKE\nR0V'\u0016\u0013\u0006BB.\fA\u0003%\u0001'A\tT!\u0006\u00136j\u0018\"V\u00132#u,V*F%\u0002Bq!X\u0006C\u0002\u0013\u0005q&\u0001\bT!\u0006\u00136j\u0018*F!>{VK\u0015'\t\r}[\u0001\u0015!\u00031\u0003=\u0019\u0006+\u0011*L?J+\u0005kT0V%2\u0003\u0003bB1\f\u0005\u0004%\taL\u0001\u0011'B\u000b%kS0C+&cEi\u0018#B)\u0016CaaY\u0006!\u0002\u0013\u0001\u0014!E*Q\u0003J[uLQ+J\u0019\u0012{F)\u0011+FA\u0001"
)
public final class package {
   public static String SPARK_BUILD_DATE() {
      return package$.MODULE$.SPARK_BUILD_DATE();
   }

   public static String SPARK_REPO_URL() {
      return package$.MODULE$.SPARK_REPO_URL();
   }

   public static String SPARK_BUILD_USER() {
      return package$.MODULE$.SPARK_BUILD_USER();
   }

   public static String SPARK_REVISION() {
      return package$.MODULE$.SPARK_REVISION();
   }

   public static String SPARK_BRANCH() {
      return package$.MODULE$.SPARK_BRANCH();
   }

   public static String SPARK_VERSION() {
      return package$.MODULE$.SPARK_VERSION();
   }

   public static class SparkBuildInfo$ {
      public static final SparkBuildInfo$ MODULE$;
      // $FF: synthetic field
      private final Tuple6 x$1;
      private final String spark_version;
      private final String spark_branch;
      private final String spark_revision;
      private final String spark_build_user;
      private final String spark_repo_url;
      private final String spark_build_date;

      static {
         new SparkBuildInfo$();
      }

      public String spark_version() {
         return this.spark_version;
      }

      public String spark_branch() {
         return this.spark_branch;
      }

      public String spark_revision() {
         return this.spark_revision;
      }

      public String spark_build_user() {
         return this.spark_build_user;
      }

      public String spark_repo_url() {
         return this.spark_repo_url;
      }

      public String spark_build_date() {
         return this.spark_build_date;
      }

      private final Tuple6 liftedTree1$1(InputStream resourceStream$1) {
         boolean var12 = false;

         Tuple6 var10000;
         try {
            var12 = true;
            String unknownProp = "<unknown>";
            Properties props = new Properties();
            props.load(resourceStream$1);
            var10000 = new Tuple6(props.getProperty("version", unknownProp), props.getProperty("branch", unknownProp), props.getProperty("revision", unknownProp), props.getProperty("user", unknownProp), props.getProperty("url", unknownProp), props.getProperty("date", unknownProp));
            var12 = false;
         } catch (Exception var15) {
            throw new SparkException("Error loading properties from spark-version-info.properties", var15);
         } finally {
            if (var12) {
               if (resourceStream$1 != null) {
                  try {
                     resourceStream$1.close();
                  } catch (Exception var13) {
                     throw new SparkException("Error closing spark build info resource stream", var13);
                  }
               }

            }
         }

         Tuple6 var3 = var10000;
         if (resourceStream$1 != null) {
            try {
               resourceStream$1.close();
            } catch (Exception var14) {
               throw new SparkException("Error closing spark build info resource stream", var14);
            }
         }

         return var3;
      }

      public SparkBuildInfo$() {
         MODULE$ = this;
         InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("spark-version-info.properties");
         if (resourceStream == null) {
            throw new SparkException("Could not find spark-version-info.properties");
         } else {
            Tuple6 var1 = this.liftedTree1$1(resourceStream);
            if (var1 != null) {
               String spark_version = (String)var1._1();
               String spark_branch = (String)var1._2();
               String spark_revision = (String)var1._3();
               String spark_build_user = (String)var1._4();
               String spark_repo_url = (String)var1._5();
               String spark_build_date = (String)var1._6();
               if (spark_version != null && spark_branch != null && spark_revision != null && spark_build_user != null && spark_repo_url != null && spark_build_date != null) {
                  Tuple6 var15 = new Tuple6(spark_version, spark_branch, spark_revision, spark_build_user, spark_repo_url, spark_build_date);
                  this.x$1 = var15;
                  this.spark_version = (String)this.x$1._1();
                  this.spark_branch = (String)this.x$1._2();
                  this.spark_revision = (String)this.x$1._3();
                  this.spark_build_user = (String)this.x$1._4();
                  this.spark_repo_url = (String)this.x$1._5();
                  this.spark_build_date = (String)this.x$1._6();
                  return;
               }
            }

            throw new MatchError(var1);
         }
      }
   }
}
