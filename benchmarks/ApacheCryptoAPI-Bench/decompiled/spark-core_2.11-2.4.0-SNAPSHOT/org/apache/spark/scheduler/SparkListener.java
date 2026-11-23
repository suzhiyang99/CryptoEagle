package org.apache.spark.scheduler;

import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005eg!B\u0001\u0003\u0003\u0003Y!!D*qCJ\\G*[:uK:,'O\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011c\u0001\u0001\r%A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"a\u0005\u000b\u000e\u0003\tI!!\u0006\u0002\u0003-M\u0003\u0018M]6MSN$XM\\3s\u0013:$XM\u001d4bG\u0016DQa\u0006\u0001\u0005\u0002a\ta\u0001P5oSRtD#A\r\u0011\u0005M\u0001\u0001\"B\u000e\u0001\t\u0003b\u0012\u0001E8o'R\fw-Z\"p[BdW\r^3e)\ti\u0002\u0005\u0005\u0002\u000e=%\u0011qD\u0004\u0002\u0005+:LG\u000fC\u0003\"5\u0001\u0007!%\u0001\bti\u0006<WmQ8na2,G/\u001a3\u0011\u0005M\u0019\u0013B\u0001\u0013\u0003\u0005m\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8feN#\u0018mZ3D_6\u0004H.\u001a;fI\")a\u0005\u0001C!O\u0005\u0001rN\\*uC\u001e,7+\u001e2nSR$X\r\u001a\u000b\u0003;!BQ!K\u0013A\u0002)\nab\u001d;bO\u0016\u001cVOY7jiR,G\r\u0005\u0002\u0014W%\u0011AF\u0001\u0002\u001c'B\f'o\u001b'jgR,g.\u001a:Ti\u0006<WmU;c[&$H/\u001a3\t\u000b9\u0002A\u0011I\u0018\u0002\u0017=tG+Y:l'R\f'\u000f\u001e\u000b\u0003;ABQ!M\u0017A\u0002I\n\u0011\u0002^1tWN#\u0018M\u001d;\u0011\u0005M\u0019\u0014B\u0001\u001b\u0003\u0005Y\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8feR\u000b7o[*uCJ$\b\"\u0002\u001c\u0001\t\u0003:\u0014aE8o)\u0006\u001c8nR3ui&twMU3tk2$HCA\u000f9\u0011\u0015IT\u00071\u0001;\u0003E!\u0018m]6HKR$\u0018N\\4SKN,H\u000e\u001e\t\u0003'mJ!\u0001\u0010\u0002\u0003=M\u0003\u0018M]6MSN$XM\\3s)\u0006\u001c8nR3ui&twMU3tk2$\b\"\u0002 \u0001\t\u0003z\u0014!C8o)\u0006\u001c8.\u00128e)\ti\u0002\tC\u0003B{\u0001\u0007!)A\u0004uCN\\WI\u001c3\u0011\u0005M\u0019\u0015B\u0001#\u0003\u0005Q\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8feR\u000b7o[#oI\")a\t\u0001C!\u000f\u0006QqN\u001c&pEN#\u0018M\u001d;\u0015\u0005uA\u0005\"B%F\u0001\u0004Q\u0015\u0001\u00036pEN#\u0018M\u001d;\u0011\u0005MY\u0015B\u0001'\u0003\u0005U\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe*{'m\u0015;beRDQA\u0014\u0001\u0005B=\u000b\u0001b\u001c8K_\n,e\u000e\u001a\u000b\u0003;ACQ!U'A\u0002I\u000baA[8c\u000b:$\u0007CA\nT\u0013\t!&AA\nTa\u0006\u00148\u000eT5ti\u0016tWM\u001d&pE\u0016sG\rC\u0003W\u0001\u0011\u0005s+A\np]\u0016sg/\u001b:p]6,g\u000e^+qI\u0006$X\r\u0006\u0002\u001e1\")\u0011,\u0016a\u00015\u0006\tRM\u001c<je>tW.\u001a8u+B$\u0017\r^3\u0011\u0005MY\u0016B\u0001/\u0003\u0005y\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u0016sg/\u001b:p]6,g\u000e^+qI\u0006$X\rC\u0003_\u0001\u0011\u0005s,A\np]\ncwnY6NC:\fw-\u001a:BI\u0012,G\r\u0006\u0002\u001eA\")\u0011-\u0018a\u0001E\u0006\t\"\r\\8dW6\u000bg.Y4fe\u0006#G-\u001a3\u0011\u0005M\u0019\u0017B\u00013\u0003\u0005y\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\ncwnY6NC:\fw-\u001a:BI\u0012,G\rC\u0003g\u0001\u0011\u0005s-A\u000bp]\ncwnY6NC:\fw-\u001a:SK6|g/\u001a3\u0015\u0005uA\u0007\"B5f\u0001\u0004Q\u0017a\u00052m_\u000e\\W*\u00198bO\u0016\u0014(+Z7pm\u0016$\u0007CA\nl\u0013\ta'A\u0001\u0011Ta\u0006\u00148\u000eT5ti\u0016tWM\u001d\"m_\u000e\\W*\u00198bO\u0016\u0014(+Z7pm\u0016$\u0007\"\u00028\u0001\t\u0003z\u0017AD8o+:\u0004XM]:jgR\u0014F\t\u0012\u000b\u0003;ADQ!]7A\u0002I\fA\"\u001e8qKJ\u001c\u0018n\u001d;S\t\u0012\u0003\"aE:\n\u0005Q\u0014!!G*qCJ\\G*[:uK:,'/\u00168qKJ\u001c\u0018n\u001d;S\t\u0012CQA\u001e\u0001\u0005B]\f!c\u001c8BaBd\u0017nY1uS>t7\u000b^1siR\u0011Q\u0004\u001f\u0005\u0006sV\u0004\rA_\u0001\u0011CB\u0004H.[2bi&|gn\u0015;beR\u0004\"aE>\n\u0005q\u0014!!H*qCJ\\G*[:uK:,'/\u00119qY&\u001c\u0017\r^5p]N#\u0018M\u001d;\t\u000by\u0004A\u0011I@\u0002!=t\u0017\t\u001d9mS\u000e\fG/[8o\u000b:$GcA\u000f\u0002\u0002!9\u00111A?A\u0002\u0005\u0015\u0011AD1qa2L7-\u0019;j_:,e\u000e\u001a\t\u0004'\u0005\u001d\u0011bAA\u0005\u0005\tY2\u000b]1sW2K7\u000f^3oKJ\f\u0005\u000f\u001d7jG\u0006$\u0018n\u001c8F]\u0012Dq!!\u0004\u0001\t\u0003\ny!A\fp]\u0016CXmY;u_JlU\r\u001e:jGN,\u0006\u000fZ1uKR\u0019Q$!\u0005\t\u0011\u0005M\u00111\u0002a\u0001\u0003+\tQ#\u001a=fGV$xN]'fiJL7m]+qI\u0006$X\rE\u0002\u0014\u0003/I1!!\u0007\u0003\u0005\t\u001a\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u0016CXmY;u_JlU\r\u001e:jGN,\u0006\u000fZ1uK\"9\u0011Q\u0004\u0001\u0005B\u0005}\u0011aD8o\u000bb,7-\u001e;pe\u0006#G-\u001a3\u0015\u0007u\t\t\u0003\u0003\u0005\u0002$\u0005m\u0001\u0019AA\u0013\u00035)\u00070Z2vi>\u0014\u0018\t\u001a3fIB\u00191#a\n\n\u0007\u0005%\"A\u0001\u000eTa\u0006\u00148\u000eT5ti\u0016tWM]#yK\u000e,Ho\u001c:BI\u0012,G\rC\u0004\u0002.\u0001!\t%a\f\u0002#=tW\t_3dkR|'OU3n_Z,G\rF\u0002\u001e\u0003cA\u0001\"a\r\u0002,\u0001\u0007\u0011QG\u0001\u0010Kb,7-\u001e;peJ+Wn\u001c<fIB\u00191#a\u000e\n\u0007\u0005e\"A\u0001\u000fTa\u0006\u00148\u000eT5ti\u0016tWM]#yK\u000e,Ho\u001c:SK6|g/\u001a3\t\u000f\u0005u\u0002\u0001\"\u0011\u0002@\u0005)rN\\#yK\u000e,Ho\u001c:CY\u0006\u001c7\u000e\\5ti\u0016$GcA\u000f\u0002B!A\u00111IA\u001e\u0001\u0004\t)%A\nfq\u0016\u001cW\u000f^8s\u00052\f7m\u001b7jgR,G\rE\u0002\u0014\u0003\u000fJ1!!\u0013\u0003\u0005\u0001\u001a\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u0016CXmY;u_J\u0014E.Y2lY&\u001cH/\u001a3\t\u000f\u00055\u0003\u0001\"\u0001\u0002P\u0005irN\\#yK\u000e,Ho\u001c:CY\u0006\u001c7\u000e\\5ti\u0016$gi\u001c:Ti\u0006<W\rF\u0002\u001e\u0003#B\u0001\"a\u0015\u0002L\u0001\u0007\u0011QK\u0001\u001cKb,7-\u001e;pe\nc\u0017mY6mSN$X\r\u001a$peN#\u0018mZ3\u0011\u0007M\t9&C\u0002\u0002Z\t\u0011\u0001f\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'O\u00117bG.d\u0017n\u001d;fI\u001a{'o\u0015;bO\u0016Dq!!\u0018\u0001\t\u0003\ty&A\rp]:{G-\u001a\"mC\u000e\\G.[:uK\u00124uN]*uC\u001e,GcA\u000f\u0002b!A\u00111MA.\u0001\u0004\t)'A\fo_\u0012,'\t\\1dW2L7\u000f^3e\r>\u00148\u000b^1hKB\u00191#a\u001a\n\u0007\u0005%$A\u0001\u0013Ta\u0006\u00148\u000eT5ti\u0016tWM\u001d(pI\u0016\u0014E.Y2lY&\u001cH/\u001a3G_J\u001cF/Y4f\u0011\u001d\ti\u0007\u0001C!\u0003_\nqc\u001c8Fq\u0016\u001cW\u000f^8s+:\u0014G.Y2lY&\u001cH/\u001a3\u0015\u0007u\t\t\b\u0003\u0005\u0002t\u0005-\u0004\u0019AA;\u0003U)\u00070Z2vi>\u0014XK\u001c2mC\u000e\\G.[:uK\u0012\u00042aEA<\u0013\r\tIH\u0001\u0002#'B\f'o\u001b'jgR,g.\u001a:Fq\u0016\u001cW\u000f^8s+:\u0014G.Y2lY&\u001cH/\u001a3\t\u000f\u0005u\u0004\u0001\"\u0011\u0002\u0000\u0005\trN\u001c(pI\u0016\u0014E.Y2lY&\u001cH/\u001a3\u0015\u0007u\t\t\t\u0003\u0005\u0002\u0004\u0006m\u0004\u0019AAC\u0003=qw\u000eZ3CY\u0006\u001c7\u000e\\5ti\u0016$\u0007cA\n\u0002\b&\u0019\u0011\u0011\u0012\u0002\u00039M\u0003\u0018M]6MSN$XM\\3s\u001d>$WM\u00117bG.d\u0017n\u001d;fI\"9\u0011Q\u0012\u0001\u0005B\u0005=\u0015aE8o\u001d>$W-\u00168cY\u0006\u001c7\u000e\\5ti\u0016$GcA\u000f\u0002\u0012\"A\u00111SAF\u0001\u0004\t)*A\to_\u0012,WK\u001c2mC\u000e\\G.[:uK\u0012\u00042aEAL\u0013\r\tIJ\u0001\u0002\u001f'B\f'o\u001b'jgR,g.\u001a:O_\u0012,WK\u001c2mC\u000e\\G.[:uK\u0012Dq!!(\u0001\t\u0003\ny*\u0001\bp]\ncwnY6Va\u0012\fG/\u001a3\u0015\u0007u\t\t\u000b\u0003\u0005\u0002$\u0006m\u0005\u0019AAS\u00031\u0011Gn\\2l+B$\u0017\r^3e!\r\u0019\u0012qU\u0005\u0004\u0003S\u0013!!G*qCJ\\G*[:uK:,'O\u00117pG.,\u0006\u000fZ1uK\u0012Dq!!,\u0001\t\u0003\ny+\u0001\u000ep]N\u0003XmY;mCRLg/\u001a+bg.\u001cVOY7jiR,G\rF\u0002\u001e\u0003cC\u0001\"a-\u0002,\u0002\u0007\u0011QW\u0001\u0010gB,7-\u001e7bi&4X\rV1tWB\u00191#a.\n\u0007\u0005e&AA\u0013Ta\u0006\u00148\u000eT5ti\u0016tWM]*qK\u000e,H.\u0019;jm\u0016$\u0016m]6Tk\nl\u0017\u000e\u001e;fI\"9\u0011Q\u0018\u0001\u0005B\u0005}\u0016\u0001D8o\u001fRDWM]#wK:$HcA\u000f\u0002B\"A\u00111YA^\u0001\u0004\t)-A\u0003fm\u0016tG\u000fE\u0002\u0014\u0003\u000fL1!!3\u0003\u0005I\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u00163XM\u001c;)\u0007\u0001\ti\r\u0005\u0003\u0002P\u0006UWBAAi\u0015\r\t\u0019\u000eB\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002BAl\u0003#\u0014A\u0002R3wK2|\u0007/\u001a:Ba&\u0004"
)
public abstract class SparkListener implements SparkListenerInterface {
   public void onStageCompleted(SparkListenerStageCompleted stageCompleted) {
   }

   public void onStageSubmitted(SparkListenerStageSubmitted stageSubmitted) {
   }

   public void onTaskStart(SparkListenerTaskStart taskStart) {
   }

   public void onTaskGettingResult(SparkListenerTaskGettingResult taskGettingResult) {
   }

   public void onTaskEnd(SparkListenerTaskEnd taskEnd) {
   }

   public void onJobStart(SparkListenerJobStart jobStart) {
   }

   public void onJobEnd(SparkListenerJobEnd jobEnd) {
   }

   public void onEnvironmentUpdate(SparkListenerEnvironmentUpdate environmentUpdate) {
   }

   public void onBlockManagerAdded(SparkListenerBlockManagerAdded blockManagerAdded) {
   }

   public void onBlockManagerRemoved(SparkListenerBlockManagerRemoved blockManagerRemoved) {
   }

   public void onUnpersistRDD(SparkListenerUnpersistRDD unpersistRDD) {
   }

   public void onApplicationStart(SparkListenerApplicationStart applicationStart) {
   }

   public void onApplicationEnd(SparkListenerApplicationEnd applicationEnd) {
   }

   public void onExecutorMetricsUpdate(SparkListenerExecutorMetricsUpdate executorMetricsUpdate) {
   }

   public void onExecutorAdded(SparkListenerExecutorAdded executorAdded) {
   }

   public void onExecutorRemoved(SparkListenerExecutorRemoved executorRemoved) {
   }

   public void onExecutorBlacklisted(SparkListenerExecutorBlacklisted executorBlacklisted) {
   }

   public void onExecutorBlacklistedForStage(SparkListenerExecutorBlacklistedForStage executorBlacklistedForStage) {
   }

   public void onNodeBlacklistedForStage(SparkListenerNodeBlacklistedForStage nodeBlacklistedForStage) {
   }

   public void onExecutorUnblacklisted(SparkListenerExecutorUnblacklisted executorUnblacklisted) {
   }

   public void onNodeBlacklisted(SparkListenerNodeBlacklisted nodeBlacklisted) {
   }

   public void onNodeUnblacklisted(SparkListenerNodeUnblacklisted nodeUnblacklisted) {
   }

   public void onBlockUpdated(SparkListenerBlockUpdated blockUpdated) {
   }

   public void onSpeculativeTaskSubmitted(SparkListenerSpeculativeTaskSubmitted speculativeTask) {
   }

   public void onOtherEvent(SparkListenerEvent event) {
   }
}
