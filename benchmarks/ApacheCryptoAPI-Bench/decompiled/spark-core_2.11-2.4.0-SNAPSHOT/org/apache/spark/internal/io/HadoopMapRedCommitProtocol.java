package org.apache.spark.internal.io;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCommitter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001e2A!\u0001\u0002\u0001\u001b\tQ\u0002*\u00193p_Bl\u0015\r\u001d*fI\u000e{W.\\5u!J|Go\\2pY*\u00111\u0001B\u0001\u0003S>T!!\u0002\u0004\u0002\u0011%tG/\u001a:oC2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001'\t\u0001a\u0002\u0005\u0002\u0010!5\t!!\u0003\u0002\u0012\u0005\ti\u0002*\u00193p_Bl\u0015\r\u001d*fIV\u001cWmQ8n[&$\bK]8u_\u000e|G\u000e\u0003\u0005\u0014\u0001\t\u0005\t\u0015!\u0003\u0015\u0003\u0015QwNY%e!\t)2D\u0004\u0002\u001735\tqCC\u0001\u0019\u0003\u0015\u00198-\u00197b\u0013\tQr#\u0001\u0004Qe\u0016$WMZ\u0005\u00039u\u0011aa\u0015;sS:<'B\u0001\u000e\u0018\u0011!y\u0002A!A!\u0002\u0013!\u0012\u0001\u00029bi\"DQ!\t\u0001\u0005\u0002\t\na\u0001P5oSRtDcA\u0012%KA\u0011q\u0002\u0001\u0005\u0006'\u0001\u0002\r\u0001\u0006\u0005\u0006?\u0001\u0002\r\u0001\u0006\u0005\u0006O\u0001!\t\u0005K\u0001\u000fg\u0016$X\u000f]\"p[6LG\u000f^3s)\tI\u0013\u0007\u0005\u0002+_5\t1F\u0003\u0002-[\u00051Q.\u00199sK\u0012T!A\f\u0005\u0002\r!\fGm\\8q\u0013\t\u00014FA\bPkR\u0004X\u000f^\"p[6LG\u000f^3s\u0011\u0015\u0011d\u00051\u00014\u0003\u001d\u0019wN\u001c;fqR\u0004\"\u0001N\u001c\u000e\u0003UR!AN\u0017\u0002\u00135\f\u0007O]3ek\u000e,\u0017B\u0001\u001d6\u0005I!\u0016m]6BiR,W\u000e\u001d;D_:$X\r\u001f;"
)
public class HadoopMapRedCommitProtocol extends HadoopMapReduceCommitProtocol {
   public OutputCommitter setupCommitter(TaskAttemptContext context) {
      JobConf config = (JobConf)context.getConfiguration();
      return config.getOutputCommitter();
   }

   public HadoopMapRedCommitProtocol(String jobId, String path) {
      super(jobId, path, HadoopMapReduceCommitProtocol$.MODULE$.$lessinit$greater$default$3());
   }
}
