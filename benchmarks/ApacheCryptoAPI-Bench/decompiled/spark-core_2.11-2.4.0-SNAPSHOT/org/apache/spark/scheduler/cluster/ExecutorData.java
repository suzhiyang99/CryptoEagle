package org.apache.spark.scheduler.cluster;

import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcEndpointRef;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001u3Q!\u0001\u0002\u0001\u00051\u0011A\"\u0012=fGV$xN\u001d#bi\u0006T!a\u0001\u0003\u0002\u000f\rdWo\u001d;fe*\u0011QAB\u0001\ng\u000eDW\rZ;mKJT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\b\u000e\u0003\tI!\u0001\u0005\u0002\u0003\u0019\u0015CXmY;u_JLeNZ8\t\u0011I\u0001!Q1A\u0005\u0002Q\t\u0001#\u001a=fGV$xN]#oIB|\u0017N\u001c;\u0004\u0001U\tQ\u0003\u0005\u0002\u001735\tqC\u0003\u0002\u0019\r\u0005\u0019!\u000f]2\n\u0005i9\"A\u0004*qG\u0016sG\r]8j]R\u0014VM\u001a\u0005\t9\u0001\u0011\t\u0011)A\u0005+\u0005\tR\r_3dkR|'/\u00128ea>Lg\u000e\u001e\u0011\t\u0011y\u0001!Q1A\u0005\u0002}\tq\"\u001a=fGV$xN]!eIJ,7o]\u000b\u0002AA\u0011a#I\u0005\u0003E]\u0011!B\u00159d\u0003\u0012$'/Z:t\u0011!!\u0003A!A!\u0002\u0013\u0001\u0013\u0001E3yK\u000e,Ho\u001c:BI\u0012\u0014Xm]:!\u0011!1\u0003A!b\u0001\n\u0003:\u0013\u0001D3yK\u000e,Ho\u001c:I_N$X#\u0001\u0015\u0011\u0005%zcB\u0001\u0016.\u001b\u0005Y#\"\u0001\u0017\u0002\u000bM\u001c\u0017\r\\1\n\u00059Z\u0013A\u0002)sK\u0012,g-\u0003\u00021c\t11\u000b\u001e:j]\u001eT!AL\u0016\t\u0013M\u0002!\u0011!Q\u0001\n!\"\u0014!D3yK\u000e,Ho\u001c:I_N$\b%\u0003\u0002'\u001f!Aa\u0007\u0001BA\u0002\u0013\u0005q'A\u0005ge\u0016,7i\u001c:fgV\t\u0001\b\u0005\u0002+s%\u0011!h\u000b\u0002\u0004\u0013:$\b\u0002\u0003\u001f\u0001\u0005\u0003\u0007I\u0011A\u001f\u0002\u001b\u0019\u0014X-Z\"pe\u0016\u001cx\fJ3r)\tq\u0014\t\u0005\u0002+\u007f%\u0011\u0001i\u000b\u0002\u0005+:LG\u000fC\u0004Cw\u0005\u0005\t\u0019\u0001\u001d\u0002\u0007a$\u0013\u0007\u0003\u0005E\u0001\t\u0005\t\u0015)\u00039\u0003)1'/Z3D_J,7\u000f\t\u0005\t\r\u0002\u0011)\u0019!C!o\u0005QAo\u001c;bY\u000e{'/Z:\t\u0013!\u0003!\u0011!Q\u0001\naJ\u0015a\u0003;pi\u0006d7i\u001c:fg\u0002J!AR\b\t\u0011-\u0003!Q1A\u0005B1\u000b\u0011\u0002\\8h+JdW*\u00199\u0016\u00035\u0003B!\u000b()Q%\u0011q*\r\u0002\u0004\u001b\u0006\u0004\b\"C)\u0001\u0005\u0003\u0005\u000b\u0011B'S\u0003)awnZ+sY6\u000b\u0007\u000fI\u0005\u0003\u0017>AQ\u0001\u0016\u0001\u0005\u0002U\u000ba\u0001P5oSRtDc\u0002,X1fS6\f\u0018\t\u0003\u001d\u0001AQAE*A\u0002UAQAH*A\u0002\u0001BQAJ*A\u0002!BQAN*A\u0002aBQAR*A\u0002aBQaS*A\u00025\u0003"
)
public class ExecutorData extends ExecutorInfo {
   private final RpcEndpointRef executorEndpoint;
   private final RpcAddress executorAddress;
   private int freeCores;

   public RpcEndpointRef executorEndpoint() {
      return this.executorEndpoint;
   }

   public RpcAddress executorAddress() {
      return this.executorAddress;
   }

   public String executorHost() {
      return super.executorHost();
   }

   public int freeCores() {
      return this.freeCores;
   }

   public void freeCores_$eq(int x$1) {
      this.freeCores = x$1;
   }

   public int totalCores() {
      return super.totalCores();
   }

   public Map<String, String> logUrlMap() {
      return super.logUrlMap();
   }

   public ExecutorData(RpcEndpointRef executorEndpoint, RpcAddress executorAddress, String executorHost, int freeCores, int totalCores, Map<String, String> logUrlMap) {
      this.executorEndpoint = executorEndpoint;
      this.executorAddress = executorAddress;
      this.freeCores = freeCores;
      super(executorHost, totalCores, logUrlMap);
   }
}
