package org.apache.spark.deploy.rest;

import org.apache.spark.SparkConf;
import org.apache.spark.rpc.RpcEndpointRef;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001]3Q!\u0001\u0002\u0001\t1\u0011Ac\u0015;b]\u0012\fGn\u001c8f%\u0016\u001cHoU3sm\u0016\u0014(BA\u0002\u0005\u0003\u0011\u0011Xm\u001d;\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\t\u0001Q\u0002\u0005\u0002\u000f\u001f5\t!!\u0003\u0002\u0011\u0005\t!\"+Z:u'V\u0014W.[:tS>t7+\u001a:wKJD\u0011B\u0005\u0001\u0003\u0002\u0003\u0006I\u0001\u0006\u0010\u0002\t!|7\u000f^\u0002\u0001!\t)2D\u0004\u0002\u001735\tqCC\u0001\u0019\u0003\u0015\u00198-\u00197b\u0013\tQr#\u0001\u0004Qe\u0016$WMZ\u0005\u00039u\u0011aa\u0015;sS:<'B\u0001\u000e\u0018\u0013\t\u0011r\u0002C\u0005!\u0001\t\u0005\t\u0015!\u0003\"I\u0005i!/Z9vKN$X\r\u001a)peR\u0004\"A\u0006\u0012\n\u0005\r:\"aA%oi&\u0011\u0001e\u0004\u0005\nM\u0001\u0011\t\u0011)A\u0005O-\n!\"\\1ti\u0016\u00148i\u001c8g!\tA\u0013&D\u0001\u0007\u0013\tQcAA\u0005Ta\u0006\u00148nQ8oM&\u0011ae\u0004\u0005\t[\u0001\u0011\t\u0011)A\u0005]\u0005qQ.Y:uKJ,e\u000e\u001a9pS:$\bCA\u00183\u001b\u0005\u0001$BA\u0019\u0007\u0003\r\u0011\boY\u0005\u0003gA\u0012aB\u00159d\u000b:$\u0007o\\5oiJ+g\r\u0003\u00056\u0001\t\u0005\t\u0015!\u0003\u0015\u0003%i\u0017m\u001d;feV\u0013H\u000eC\u00038\u0001\u0011\u0005\u0001(\u0001\u0004=S:LGO\u0010\u000b\u0007siZD(\u0010 \u0011\u00059\u0001\u0001\"\u0002\n7\u0001\u0004!\u0002\"\u0002\u00117\u0001\u0004\t\u0003\"\u0002\u00147\u0001\u00049\u0003\"B\u00177\u0001\u0004q\u0003\"B\u001b7\u0001\u0004!\u0002b\u0002!\u0001\u0005\u0004%\t&Q\u0001\u0015gV\u0014W.\u001b;SKF,Xm\u001d;TKJ4H.\u001a;\u0016\u0003\t\u0003\"AD\"\n\u0005\u0011\u0013!AH*uC:$\u0017\r\\8oKN+(-\\5u%\u0016\fX/Z:u'\u0016\u0014h\u000f\\3u\u0011\u00191\u0005\u0001)A\u0005\u0005\u0006)2/\u001e2nSR\u0014V-];fgR\u001cVM\u001d<mKR\u0004\u0003b\u0002%\u0001\u0005\u0004%\t&S\u0001\u0013W&dGNU3rk\u0016\u001cHoU3sm2,G/F\u0001K!\tq1*\u0003\u0002M\u0005\ta2\u000b^1oI\u0006dwN\\3LS2d'+Z9vKN$8+\u001a:wY\u0016$\bB\u0002(\u0001A\u0003%!*A\nlS2d'+Z9vKN$8+\u001a:wY\u0016$\b\u0005C\u0004Q\u0001\t\u0007I\u0011K)\u0002)M$\u0018\r^;t%\u0016\fX/Z:u'\u0016\u0014h\u000f\\3u+\u0005\u0011\u0006C\u0001\bT\u0013\t!&A\u0001\u0010Ti\u0006tG-\u00197p]\u0016\u001cF/\u0019;vgJ+\u0017/^3tiN+'O\u001e7fi\"1a\u000b\u0001Q\u0001\nI\u000bQc\u001d;biV\u001c(+Z9vKN$8+\u001a:wY\u0016$\b\u0005"
)
public class StandaloneRestServer extends RestSubmissionServer {
   private final StandaloneSubmitRequestServlet submitRequestServlet;
   private final StandaloneKillRequestServlet killRequestServlet;
   private final StandaloneStatusRequestServlet statusRequestServlet;

   public StandaloneSubmitRequestServlet submitRequestServlet() {
      return this.submitRequestServlet;
   }

   public StandaloneKillRequestServlet killRequestServlet() {
      return this.killRequestServlet;
   }

   public StandaloneStatusRequestServlet statusRequestServlet() {
      return this.statusRequestServlet;
   }

   public StandaloneRestServer(String host, int requestedPort, SparkConf masterConf, RpcEndpointRef masterEndpoint, String masterUrl) {
      super(host, requestedPort, masterConf);
      this.submitRequestServlet = new StandaloneSubmitRequestServlet(masterEndpoint, masterUrl, super.masterConf());
      this.killRequestServlet = new StandaloneKillRequestServlet(masterEndpoint, super.masterConf());
      this.statusRequestServlet = new StandaloneStatusRequestServlet(masterEndpoint, super.masterConf());
   }
}
