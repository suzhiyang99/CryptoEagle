package org.apache.spark.status.api.v1;

import scala.Tuple2;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001)3A!\u0001\u0002\u0001\u001f\tQ\u0012\t\u001d9mS\u000e\fG/[8o\u000b:4\u0018N]8o[\u0016tG/\u00138g_*\u00111\u0001B\u0001\u0003mFR!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u000511\u000f^1ukNT!!\u0003\u0006\u0002\u000bM\u0004\u0018M]6\u000b\u0005-a\u0011AB1qC\u000eDWMC\u0001\u000e\u0003\ry'oZ\u0002\u0001'\t\u0001\u0001\u0003\u0005\u0002\u0012)5\t!CC\u0001\u0014\u0003\u0015\u00198-\u00197b\u0013\t)\"C\u0001\u0004B]f\u0014VM\u001a\u0005\t/\u0001\u0011)\u0019!C\u00011\u00059!/\u001e8uS6,W#A\r\u0011\u0005iYR\"\u0001\u0002\n\u0005q\u0011!a\u0003*v]RLW.Z%oM>D\u0001B\b\u0001\u0003\u0002\u0003\u0006I!G\u0001\teVtG/[7fA!A\u0001\u0005\u0001BC\u0002\u0013\u0005\u0011%A\bta\u0006\u00148\u000e\u0015:pa\u0016\u0014H/[3t+\u0005\u0011\u0003cA\u0012,]9\u0011A%\u000b\b\u0003K!j\u0011A\n\u0006\u0003O9\ta\u0001\u0010:p_Rt\u0014\"A\n\n\u0005)\u0012\u0012a\u00029bG.\fw-Z\u0005\u0003Y5\u00121aU3r\u0015\tQ#\u0003\u0005\u0003\u0012_E\n\u0014B\u0001\u0019\u0013\u0005\u0019!V\u000f\u001d7feA\u0011!'\u000e\b\u0003#MJ!\u0001\u000e\n\u0002\rA\u0013X\rZ3g\u0013\t1tG\u0001\u0004TiJLgn\u001a\u0006\u0003iIA\u0001\"\u000f\u0001\u0003\u0002\u0003\u0006IAI\u0001\u0011gB\f'o\u001b)s_B,'\u000f^5fg\u0002B\u0001b\u000f\u0001\u0003\u0006\u0004%\t!I\u0001\u0011gf\u001cH/Z7Qe>\u0004XM\u001d;jKND\u0001\"\u0010\u0001\u0003\u0002\u0003\u0006IAI\u0001\u0012gf\u001cH/Z7Qe>\u0004XM\u001d;jKN\u0004\u0003\u0002C \u0001\u0005\u000b\u0007I\u0011A\u0011\u0002!\rd\u0017m]:qCRDWI\u001c;sS\u0016\u001c\b\u0002C!\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002#\rd\u0017m]:qCRDWI\u001c;sS\u0016\u001c\b\u0005\u0003\u0004D\u0001\u0011\u0005\u0001\u0002R\u0001\u0007y%t\u0017\u000e\u001e \u0015\u000b\u00153u\tS%\u0011\u0005i\u0001\u0001\"B\fC\u0001\u0004I\u0002\"\u0002\u0011C\u0001\u0004\u0011\u0003\"B\u001eC\u0001\u0004\u0011\u0003\"B C\u0001\u0004\u0011\u0003"
)
public class ApplicationEnvironmentInfo {
   private final RuntimeInfo runtime;
   private final Seq<Tuple2<String, String>> sparkProperties;
   private final Seq<Tuple2<String, String>> systemProperties;
   private final Seq<Tuple2<String, String>> classpathEntries;

   public RuntimeInfo runtime() {
      return this.runtime;
   }

   public Seq<Tuple2<String, String>> sparkProperties() {
      return this.sparkProperties;
   }

   public Seq<Tuple2<String, String>> systemProperties() {
      return this.systemProperties;
   }

   public Seq<Tuple2<String, String>> classpathEntries() {
      return this.classpathEntries;
   }

   public ApplicationEnvironmentInfo(RuntimeInfo runtime, Seq<Tuple2<String, String>> sparkProperties, Seq<Tuple2<String, String>> systemProperties, Seq<Tuple2<String, String>> classpathEntries) {
      this.runtime = runtime;
      this.sparkProperties = sparkProperties;
      this.systemProperties = systemProperties;
      this.classpathEntries = classpathEntries;
   }
}
