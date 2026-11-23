package org.apache.spark.rpc;

import scala.PartialFunction;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001y3\u0001\"\u0001\u0002\u0011\u0002\u0007\u0005AA\u0003\u0002\f%B\u001cWI\u001c3q_&tGO\u0003\u0002\u0004\t\u0005\u0019!\u000f]2\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\"\u0001A\u0006\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g\u0011\u0015\u0011\u0002\u0001\"\u0001\u0015\u0003\u0019!\u0013N\\5uI\r\u0001A#A\u000b\u0011\u000511\u0012BA\f\u000e\u0005\u0011)f.\u001b;\t\u000fe\u0001!\u0019!D\u00015\u00051!\u000f]2F]Z,\u0012a\u0007\t\u00039ui\u0011AA\u0005\u0003=\t\u0011aA\u00159d\u000b:4\b\"\u0002\u0011\u0001\t\u000b\t\u0013\u0001B:fY\u001a,\u0012A\t\t\u00039\rJ!\u0001\n\u0002\u0003\u001dI\u00038-\u00128ea>Lg\u000e\u001e*fM\")a\u0005\u0001C\u0001O\u00059!/Z2fSZ,W#\u0001\u0015\u0011\t1I3&F\u0005\u0003U5\u0011q\u0002U1si&\fGNR;oGRLwN\u001c\t\u0003\u00191J!!L\u0007\u0003\u0007\u0005s\u0017\u0010C\u00030\u0001\u0011\u0005\u0001'A\bsK\u000e,\u0017N^3B]\u0012\u0014V\r\u001d7z)\tA\u0013\u0007C\u00033]\u0001\u00071'A\u0004d_:$X\r\u001f;\u0011\u0005q!\u0014BA\u001b\u0003\u00059\u0011\u0006oY\"bY2\u001cuN\u001c;fqRDQa\u000e\u0001\u0005\u0002a\nqa\u001c8FeJ|'\u000f\u0006\u0002\u0016s!)!H\u000ea\u0001w\u0005)1-Y;tKB\u0011A\b\u0012\b\u0003{\ts!AP!\u000e\u0003}R!\u0001Q\n\u0002\rq\u0012xn\u001c;?\u0013\u0005q\u0011BA\"\u000e\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u0012$\u0003\u0013QC'o\\<bE2,'BA\"\u000e\u0011\u0015A\u0005\u0001\"\u0001J\u0003-ygnQ8o]\u0016\u001cG/\u001a3\u0015\u0005UQ\u0005\"B&H\u0001\u0004a\u0015!\u0004:f[>$X-\u00113ee\u0016\u001c8\u000f\u0005\u0002\u001d\u001b&\u0011aJ\u0001\u0002\u000b%B\u001c\u0017\t\u001a3sKN\u001c\b\"\u0002)\u0001\t\u0003\t\u0016AD8o\t&\u001c8m\u001c8oK\u000e$X\r\u001a\u000b\u0003+ICQaS(A\u00021CQ\u0001\u0016\u0001\u0005\u0002U\u000bab\u001c8OKR<xN]6FeJ|'\u000fF\u0002\u0016-^CQAO*A\u0002mBQaS*A\u00021CQ!\u0017\u0001\u0005\u0002Q\tqa\u001c8Ti\u0006\u0014H\u000fC\u0003\\\u0001\u0011\u0005A#\u0001\u0004p]N#x\u000e\u001d\u0005\u0006;\u0002!)\u0001F\u0001\u0005gR|\u0007\u000f"
)
public interface RpcEndpoint {
   RpcEnv rpcEnv();

   RpcEndpointRef self();

   PartialFunction<Object, BoxedUnit> receive();

   PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext var1);

   void onError(Throwable var1);

   void onConnected(RpcAddress var1);

   void onDisconnected(RpcAddress var1);

   void onNetworkError(Throwable var1, RpcAddress var2);

   void onStart();

   void onStop();

   void stop();
}
