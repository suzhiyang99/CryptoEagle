package org.apache.spark.storage;

import org.apache.spark.SparkConf;
import org.apache.spark.annotation.DeveloperApi;
import scala.Option;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001Q2Q!\u0001\u0002\u0002\u0002-\u0011a\u0002V8q_2|w-_'baB,'O\u0003\u0002\u0004\t\u000591\u000f^8sC\u001e,'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0011\u0001\u0001\u0004\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011M\u0001!\u0011!Q\u0001\nQ\tAaY8oMB\u0011QCF\u0007\u0002\t%\u0011q\u0003\u0002\u0002\n'B\f'o[\"p]\u001aDQ!\u0007\u0001\u0005\u0002i\ta\u0001P5oSRtDCA\u000e\u001e!\ta\u0002!D\u0001\u0003\u0011\u0015\u0019\u0002\u00041\u0001\u0015\u0011\u0015y\u0002A\"\u0001!\u0003I9W\r\u001e+pa>dwnZ=G_JDun\u001d;\u0015\u0005\u0005Z\u0003cA\u0007#I%\u00111E\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005\u0015BcBA\u0007'\u0013\t9c\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003S)\u0012aa\u0015;sS:<'BA\u0014\u000f\u0011\u0015ac\u00041\u0001%\u0003!Awn\u001d;oC6,\u0007F\u0001\u0001/!\ty#'D\u00011\u0015\t\tD!\u0001\u0006b]:|G/\u0019;j_:L!a\r\u0019\u0003\u0019\u0011+g/\u001a7pa\u0016\u0014\u0018\t]5"
)
public abstract class TopologyMapper {
   public abstract Option<String> getTopologyForHost(String var1);

   public TopologyMapper(SparkConf conf) {
   }
}
