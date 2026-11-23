package org.apache.spark.rpc;

import java.nio.channels.ReadableByteChannel;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.apache.spark.util.RpcUtils$;
import scala.Function0;
import scala.concurrent.Future;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%uAB\u0001\u0003\u0011\u0003!!\"\u0001\u0004Sa\u000e,eN\u001e\u0006\u0003\u0007\u0011\t1A\u001d9d\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<\u0007CA\u0006\r\u001b\u0005\u0011aAB\u0007\u0003\u0011\u0003!aB\u0001\u0004Sa\u000e,eN^\n\u0003\u0019=\u0001\"\u0001E\n\u000e\u0003EQ\u0011AE\u0001\u0006g\u000e\fG.Y\u0005\u0003)E\u0011a!\u00118z%\u00164\u0007\"\u0002\f\r\t\u0003A\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003)AQA\u0007\u0007\u0005\u0002m\taa\u0019:fCR,G#\u0004\u000f\u00020\u0005E\u0012QGA \u0003\u0003\nY\u0005\u0005\u0002\f;\u00191QBAA\u0001\ty\u0019\"!H\b\t\u0011\u0001j\"\u0011!Q\u0001\n\u0005\nAaY8oMB\u0011!eI\u0007\u0002\t%\u0011A\u0005\u0002\u0002\n'B\f'o[\"p]\u001aDQAF\u000f\u0005\u0002\u0019\"\"\u0001H\u0014\t\u000b\u0001*\u0003\u0019A\u0011\t\u0011%j\"\u0019!C\u0001\t)\nA\u0003Z3gCVdG\u000fT8pWV\u0004H+[7f_V$X#A\u0016\u0011\u0005-a\u0013BA\u0017\u0003\u0005)\u0011\u0006o\u0019+j[\u0016|W\u000f\u001e\u0005\u0007_u\u0001\u000b\u0011B\u0016\u0002+\u0011,g-Y;mi2{wn[;q)&lWm\\;uA!1\u0011'\bD\u0001\u0005I\n1\"\u001a8ea>Lg\u000e\u001e*fMR\u00111G\u000e\t\u0003\u0017QJ!!\u000e\u0002\u0003\u001dI\u00038-\u00128ea>Lg\u000e\u001e*fM\")q\u0007\ra\u0001q\u0005AQM\u001c3q_&tG\u000f\u0005\u0002\fs%\u0011!H\u0001\u0002\f%B\u001cWI\u001c3q_&tG\u000fC\u0003=;\u0019\u0005Q(A\u0004bI\u0012\u0014Xm]:\u0016\u0003y\u0002\"aC \n\u0005\u0001\u0013!A\u0003*qG\u0006#GM]3tg\")!)\bD\u0001\u0007\u0006i1/\u001a;va\u0016sG\r]8j]R$2a\r#N\u0011\u0015)\u0015\t1\u0001G\u0003\u0011q\u0017-\\3\u0011\u0005\u001dSeB\u0001\tI\u0013\tI\u0015#\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u00172\u0013aa\u0015;sS:<'BA%\u0012\u0011\u00159\u0014\t1\u00019\u0011\u0015yUD\"\u0001Q\u0003i\t7/\u001f8d'\u0016$X\u000f]#oIB|\u0017N\u001c;SK\u001a\u0014\u00150\u0016*J)\t\tv\u000bE\u0002S+Nj\u0011a\u0015\u0006\u0003)F\t!bY8oGV\u0014(/\u001a8u\u0013\t16K\u0001\u0004GkR,(/\u001a\u0005\u00061:\u0003\rAR\u0001\u0004kJL\u0007\"\u0002.\u001e\t\u0003Y\u0016!F:fiV\u0004XI\u001c3q_&tGOU3g\u0005f,&+\u0013\u000b\u0003gqCQ\u0001W-A\u0002\u0019CQAX\u000f\u0005\u0002}\u000b\u0001c]3ukB,e\u000e\u001a9pS:$(+\u001a4\u0015\u0007M\u0002\u0017\rC\u0003=;\u0002\u0007a\bC\u0003c;\u0002\u0007a)\u0001\u0007f]\u0012\u0004x.\u001b8u\u001d\u0006lW\rC\u0003e;\u0019\u0005Q-\u0001\u0003ti>\u0004HC\u00014j!\t\u0001r-\u0003\u0002i#\t!QK\\5u\u0011\u001594\r1\u00014\u0011\u0015YWD\"\u0001m\u0003!\u0019\b.\u001e;e_^tG#\u00014\t\u000b9lb\u0011\u00017\u0002!\u0005<\u0018-\u001b;UKJl\u0017N\\1uS>t\u0007\"\u00029\u001e\r\u0003\t\u0018a\u00033fg\u0016\u0014\u0018.\u00197ju\u0016,\"A];\u0015\u0005Mt\bC\u0001;v\u0019\u0001!QA^8C\u0002]\u0014\u0011\u0001V\t\u0003qn\u0004\"\u0001E=\n\u0005i\f\"a\u0002(pi\"Lgn\u001a\t\u0003!qL!!`\t\u0003\u0007\u0005s\u0017\u0010\u0003\u0004\u0000_\u0002\u0007\u0011\u0011A\u0001\u0016I\u0016\u001cXM]5bY&T\u0018\r^5p]\u0006\u001bG/[8o!\u0011\u0001\u00121A:\n\u0007\u0005\u0015\u0011CA\u0005Gk:\u001cG/[8oa!9\u0011\u0011B\u000f\u0007\u0002\u0005-\u0011A\u00034jY\u0016\u001cVM\u001d<feV\u0011\u0011Q\u0002\t\u0004\u0017\u0005=\u0011bAA\t\u0005\t\u0001\"\u000b]2F]Z4\u0015\u000e\\3TKJ4XM\u001d\u0005\b\u0003+ib\u0011AA\f\u0003-y\u0007/\u001a8DQ\u0006tg.\u001a7\u0015\t\u0005e\u0011Q\u0006\t\u0005\u00037\tI#\u0004\u0002\u0002\u001e)!\u0011qDA\u0011\u0003!\u0019\u0007.\u00198oK2\u001c(\u0002BA\u0012\u0003K\t1A\\5p\u0015\t\t9#\u0001\u0003kCZ\f\u0017\u0002BA\u0016\u0003;\u00111CU3bI\u0006\u0014G.\u001a\"zi\u0016\u001c\u0005.\u00198oK2Da\u0001WA\n\u0001\u00041\u0005\"B#\u001a\u0001\u00041\u0005BBA\u001a3\u0001\u0007a)\u0001\u0003i_N$\bbBA\u001c3\u0001\u0007\u0011\u0011H\u0001\u0005a>\u0014H\u000fE\u0002\u0011\u0003wI1!!\u0010\u0012\u0005\rIe\u000e\u001e\u0005\u0006Ae\u0001\r!\t\u0005\b\u0003\u0007J\u0002\u0019AA#\u0003=\u0019XmY;sSRLX*\u00198bO\u0016\u0014\bc\u0001\u0012\u0002H%\u0019\u0011\u0011\n\u0003\u0003\u001fM+7-\u001e:jifl\u0015M\\1hKJD\u0011\"!\u0014\u001a!\u0003\u0005\r!a\u0014\u0002\u0015\rd\u0017.\u001a8u\u001b>$W\rE\u0002\u0011\u0003#J1!a\u0015\u0012\u0005\u001d\u0011un\u001c7fC:DaA\u0007\u0007\u0005\u0002\u0005]C#\u0005\u000f\u0002Z\u0005m\u0013qLA2\u0003K\n9'!\u001b\u0002n!1Q)!\u0016A\u0002\u0019Cq!!\u0018\u0002V\u0001\u0007a)A\u0006cS:$\u0017\t\u001a3sKN\u001c\bbBA1\u0003+\u0002\rAR\u0001\u0011C\u00124XM\u001d;jg\u0016\fE\r\u001a:fgND\u0001\"a\u000e\u0002V\u0001\u0007\u0011\u0011\b\u0005\u0007A\u0005U\u0003\u0019A\u0011\t\u0011\u0005\r\u0013Q\u000ba\u0001\u0003\u000bB\u0001\"a\u001b\u0002V\u0001\u0007\u0011\u0011H\u0001\u000f]VlWk]1cY\u0016\u001cuN]3t\u0011!\ti%!\u0016A\u0002\u0005=\u0003\"CA9\u0019E\u0005I\u0011AA:\u0003A\u0019'/Z1uK\u0012\"WMZ1vYR$c'\u0006\u0002\u0002v)\"\u0011qJA<W\t\tI\b\u0005\u0003\u0002|\u0005\u0015UBAA?\u0015\u0011\ty(!!\u0002\u0013Ut7\r[3dW\u0016$'bAAB#\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005\u001d\u0015Q\u0010\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007"
)
public abstract class RpcEnv {
   private final RpcTimeout defaultLookupTimeout;

   public static boolean create$default$6() {
      return RpcEnv$.MODULE$.create$default$6();
   }

   public static RpcEnv create(String var0, String var1, String var2, int var3, SparkConf var4, SecurityManager var5, int var6, boolean var7) {
      return RpcEnv$.MODULE$.create(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static RpcEnv create(String var0, String var1, int var2, SparkConf var3, SecurityManager var4, boolean var5) {
      return RpcEnv$.MODULE$.create(var0, var1, var2, var3, var4, var5);
   }

   public RpcTimeout defaultLookupTimeout() {
      return this.defaultLookupTimeout;
   }

   public abstract RpcEndpointRef endpointRef(RpcEndpoint var1);

   public abstract RpcAddress address();

   public abstract RpcEndpointRef setupEndpoint(String var1, RpcEndpoint var2);

   public abstract Future<RpcEndpointRef> asyncSetupEndpointRefByURI(String var1);

   public RpcEndpointRef setupEndpointRefByURI(String uri) {
      return (RpcEndpointRef)this.defaultLookupTimeout().awaitResult(this.asyncSetupEndpointRefByURI(uri));
   }

   public RpcEndpointRef setupEndpointRef(RpcAddress address, String endpointName) {
      return this.setupEndpointRefByURI((new RpcEndpointAddress(address, endpointName)).toString());
   }

   public abstract void stop(RpcEndpointRef var1);

   public abstract void shutdown();

   public abstract void awaitTermination();

   public abstract <T> T deserialize(Function0<T> var1);

   public abstract RpcEnvFileServer fileServer();

   public abstract ReadableByteChannel openChannel(String var1);

   public RpcEnv(SparkConf conf) {
      this.defaultLookupTimeout = RpcUtils$.MODULE$.lookupRpcTimeout(conf);
   }
}
