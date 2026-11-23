package org.apache.spark.broadcast;

import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001}3Q!\u0001\u0002\u0001\t)\u0011q\u0003V8se\u0016tGO\u0011:pC\u0012\u001c\u0017m\u001d;GC\u000e$xN]=\u000b\u0005\r!\u0011!\u00032s_\u0006$7-Y:u\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7c\u0001\u0001\f#A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003!\t\u0013x.\u00193dCN$h)Y2u_JL\b\"\u0002\f\u0001\t\u0003A\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003e\u0001\"A\u0005\u0001\t\u000bm\u0001A\u0011\t\u000f\u0002\u0015%t\u0017\u000e^5bY&TX\r\u0006\u0003\u001eA\u0015Z\u0003C\u0001\u0007\u001f\u0013\tyRB\u0001\u0003V]&$\b\"B\u0011\u001b\u0001\u0004\u0011\u0013\u0001C5t\tJLg/\u001a:\u0011\u00051\u0019\u0013B\u0001\u0013\u000e\u0005\u001d\u0011un\u001c7fC:DQA\n\u000eA\u0002\u001d\nAaY8oMB\u0011\u0001&K\u0007\u0002\t%\u0011!\u0006\u0002\u0002\n'B\f'o[\"p]\u001aDQ\u0001\f\u000eA\u00025\n1b]3dkJLG/_'heB\u0011\u0001FL\u0005\u0003_\u0011\u0011qbU3dkJLG/_'b]\u0006<WM\u001d\u0005\u0006c\u0001!\tEM\u0001\r]\u0016<(I]8bI\u000e\f7\u000f^\u000b\u0003gi\"B\u0001N&N\u001fR\u0011Qg\u0011\t\u0004%YB\u0014BA\u001c\u0003\u0005%\u0011%o\\1eG\u0006\u001cH\u000f\u0005\u0002:u1\u0001A!B\u001e1\u0005\u0004a$!\u0001+\u0012\u0005u\u0002\u0005C\u0001\u0007?\u0013\tyTBA\u0004O_RD\u0017N\\4\u0011\u00051\t\u0015B\u0001\"\u000e\u0005\r\te.\u001f\u0005\b\tB\n\t\u0011q\u0001F\u0003))g/\u001b3f]\u000e,G%\r\t\u0004\r&CT\"A$\u000b\u0005!k\u0011a\u0002:fM2,7\r^\u0005\u0003\u0015\u001e\u0013\u0001b\u00117bgN$\u0016m\u001a\u0005\u0006\u0019B\u0002\r\u0001O\u0001\u0007m\u0006dW/Z0\t\u000b9\u0003\u0004\u0019\u0001\u0012\u0002\u000f%\u001cHj\\2bY\")\u0001\u000b\ra\u0001#\u0006\u0011\u0011\u000e\u001a\t\u0003\u0019IK!aU\u0007\u0003\t1{gn\u001a\u0005\u0006+\u0002!\tEV\u0001\u0005gR|\u0007\u000fF\u0001\u001e\u0011\u0015A\u0006\u0001\"\u0011Z\u0003-)hN\u0019:pC\u0012\u001c\u0017m\u001d;\u0015\tuQ6,\u0018\u0005\u0006!^\u0003\r!\u0015\u0005\u00069^\u0003\rAI\u0001\u0011e\u0016lwN^3Ge>lGI]5wKJDQAX,A\u0002\t\n\u0001B\u00197pG.Lgn\u001a"
)
public class TorrentBroadcastFactory implements BroadcastFactory {
   public void initialize(boolean isDriver, SparkConf conf, SecurityManager securityMgr) {
   }

   public <T> Broadcast<T> newBroadcast(T value_, boolean isLocal, long id, ClassTag<T> evidence$1) {
      return new TorrentBroadcast<T>(value_, id, evidence$1);
   }

   public void stop() {
   }

   public void unbroadcast(long id, boolean removeFromDriver, boolean blocking) {
      TorrentBroadcast$.MODULE$.unpersist(id, removeFromDriver, blocking);
   }
}
