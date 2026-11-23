package org.apache.spark.scheduler;

import java.util.concurrent.ConcurrentLinkedQueue;
import scala.Enumeration;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]a\u0001C\u0001\u0003!\u0003\r\n\u0001\u0002\u0006\u0003\u0017M\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\"\u0001A\u0006\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g\u0011\u001d\u0011\u0002\u00011A\u0007\u0002Q\ta\u0001]1sK:$8\u0001A\u000b\u0002+A\u0011acF\u0007\u0002\u0005%\u0011\u0001D\u0001\u0002\u0005!>|G\u000eC\u0004\u001b\u0001\u0001\u0007i\u0011A\u000e\u0002\u0015A\f'/\u001a8u?\u0012*\u0017\u000f\u0006\u0002\u001d?A\u0011A\"H\u0005\u0003=5\u0011A!\u00168ji\"9\u0001%GA\u0001\u0002\u0004)\u0012a\u0001=%c!)!\u0005\u0001D\u0001G\u0005\u00012o\u00195fIVd\u0017M\u00197f#V,W/Z\u000b\u0002IA\u0019Q\u0005\f\u0018\u000e\u0003\u0019R!a\n\u0015\u0002\u0015\r|gnY;se\u0016tGO\u0003\u0002*U\u0005!Q\u000f^5m\u0015\u0005Y\u0013\u0001\u00026bm\u0006L!!\f\u0014\u0003+\r{gnY;se\u0016tG\u000fT5oW\u0016$\u0017+^3vKB\u0011a\u0003\u0001\u0005\u0006a\u00011\t!M\u0001\u000fg\u000eDW\rZ;mS:<Wj\u001c3f+\u0005\u0011\u0004CA\u001aB\u001d\t!tH\u0004\u00026}9\u0011a'\u0010\b\u0003oqr!\u0001O\u001e\u000e\u0003eR!AO\n\u0002\rq\u0012xn\u001c;?\u0013\u0005I\u0011BA\u0004\t\u0013\t)a!\u0003\u0002\u0004\t%\u0011\u0001IA\u0001\u000f'\u000eDW\rZ;mS:<Wj\u001c3f\u0013\t\u00115I\u0001\bTG\",G-\u001e7j]\u001elu\u000eZ3\u000b\u0005\u0001\u0013\u0001\"B#\u0001\r\u00031\u0015AB<fS\u001eDG/F\u0001H!\ta\u0001*\u0003\u0002J\u001b\t\u0019\u0011J\u001c;\t\u000b-\u0003a\u0011\u0001$\u0002\u00115Lgn\u00155be\u0016DQ!\u0014\u0001\u0007\u0002\u0019\u000bAB];o]&tw\rV1tWNDQa\u0014\u0001\u0007\u0002\u0019\u000b\u0001\u0002\u001d:j_JLG/\u001f\u0005\u0006#\u00021\tAR\u0001\bgR\fw-Z%e\u0011\u0015\u0019\u0006A\"\u0001U\u0003\u0011q\u0017-\\3\u0016\u0003U\u0003\"AV-\u000f\u000519\u0016B\u0001-\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011!l\u0017\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005ak\u0001\"B/\u0001\r\u0003q\u0016AD1eIN\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\u000b\u00039}CQ\u0001\u0019/A\u00029\n1b]2iK\u0012,H.\u00192mK\")!\r\u0001D\u0001G\u0006\t\"/Z7pm\u0016\u001c6\r[3ek2\f'\r\\3\u0015\u0005q!\u0007\"\u00021b\u0001\u0004q\u0003\"\u00024\u0001\r\u00039\u0017\u0001F4fiN\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\"z\u001d\u0006lW\r\u0006\u0002/Q\")1+\u001aa\u0001+\")!\u000e\u0001D\u0001W\u0006aQ\r_3dkR|'\u000fT8tiR!A\u0004\u001c8q\u0011\u0015i\u0017\u000e1\u0001V\u0003))\u00070Z2vi>\u0014\u0018\n\u001a\u0005\u0006_&\u0004\r!V\u0001\u0005Q>\u001cH\u000fC\u0003rS\u0002\u0007!/\u0001\u0004sK\u0006\u001cxN\u001c\t\u0003-ML!\u0001\u001e\u0002\u0003%\u0015CXmY;u_Jdun]:SK\u0006\u001cxN\u001c\u0005\u0006m\u00021\ta^\u0001\u0017G\",7m[*qK\u000e,H.\u0019;bE2,G+Y:lgR\u0011\u0001p\u001f\t\u0003\u0019eL!A_\u0007\u0003\u000f\t{w\u000e\\3b]\")A0\u001ea\u0001\u000f\u0006!R.\u001b8US6,Gk\\*qK\u000e,H.\u0019;j_:DQA \u0001\u0007\u0002}\fQcZ3u'>\u0014H/\u001a3UCN\\7+\u001a;Rk\u0016,X-\u0006\u0002\u0002\u0002A1\u00111AA\u0007\u0003#i!!!\u0002\u000b\t\u0005\u001d\u0011\u0011B\u0001\b[V$\u0018M\u00197f\u0015\r\tY!D\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA\b\u0003\u000b\u00111\"\u0011:sCf\u0014UO\u001a4feB\u0019a#a\u0005\n\u0007\u0005U!A\u0001\bUCN\\7+\u001a;NC:\fw-\u001a:"
)
public interface Schedulable {
   Pool parent();

   void parent_$eq(Pool var1);

   ConcurrentLinkedQueue<Schedulable> schedulableQueue();

   Enumeration.Value schedulingMode();

   int weight();

   int minShare();

   int runningTasks();

   int priority();

   int stageId();

   String name();

   void addSchedulable(Schedulable var1);

   void removeSchedulable(Schedulable var1);

   Schedulable getSchedulableByName(String var1);

   void executorLost(String var1, String var2, ExecutorLossReason var3);

   boolean checkSpeculatableTasks(int var1);

   ArrayBuffer<TaskSetManager> getSortedTaskSetQueue();
}
