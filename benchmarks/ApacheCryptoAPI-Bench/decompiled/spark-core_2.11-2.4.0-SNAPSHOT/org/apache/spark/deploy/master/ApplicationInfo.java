package org.apache.spark.deploy.master;

import java.io.ObjectInputStream;
import java.util.Date;
import org.apache.spark.deploy.ApplicationDescription;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.util.Utils$;
import scala.Enumeration;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\t\u0015c!B\u0001\u0003\u0001\u0019a!aD!qa2L7-\u0019;j_:LeNZ8\u000b\u0005\r!\u0011AB7bgR,'O\u0003\u0002\u0006\r\u00051A-\u001a9m_fT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0004\u00015\u0019\u0002C\u0001\b\u0012\u001b\u0005y!\"\u0001\t\u0002\u000bM\u001c\u0017\r\\1\n\u0005Iy!AB!osJ+g\r\u0005\u0002\u000f)%\u0011Qc\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t/\u0001\u0011)\u0019!C\u00013\u0005I1\u000f^1siRKW.Z\u0002\u0001+\u0005Q\u0002C\u0001\b\u001c\u0013\tarB\u0001\u0003M_:<\u0007\u0002\u0003\u0010\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000e\u0002\u0015M$\u0018M\u001d;US6,\u0007\u0005\u0003\u0005!\u0001\t\u0015\r\u0011\"\u0001\"\u0003\tIG-F\u0001#!\t\u0019cE\u0004\u0002\u000fI%\u0011QeD\u0001\u0007!J,G-\u001a4\n\u0005\u001dB#AB*ue&twM\u0003\u0002&\u001f!A!\u0006\u0001B\u0001B\u0003%!%A\u0002jI\u0002B\u0001\u0002\f\u0001\u0003\u0006\u0004%\t!L\u0001\u0005I\u0016\u001c8-F\u0001/!\ty\u0003'D\u0001\u0005\u0013\t\tDA\u0001\fBaBd\u0017nY1uS>tG)Z:de&\u0004H/[8o\u0011!\u0019\u0004A!A!\u0002\u0013q\u0013!\u00023fg\u000e\u0004\u0003\u0002C\u001b\u0001\u0005\u000b\u0007I\u0011\u0001\u001c\u0002\u0015M,(-\\5u\t\u0006$X-F\u00018!\tAT(D\u0001:\u0015\tQ4(\u0001\u0003vi&d'\"\u0001\u001f\u0002\t)\fg/Y\u0005\u0003}e\u0012A\u0001R1uK\"A\u0001\t\u0001B\u0001B\u0003%q'A\u0006tk\nl\u0017\u000e\u001e#bi\u0016\u0004\u0003\u0002\u0003\"\u0001\u0005\u000b\u0007I\u0011A\"\u0002\r\u0011\u0014\u0018N^3s+\u0005!\u0005CA#I\u001b\u00051%BA$\u0007\u0003\r\u0011\boY\u0005\u0003\u0013\u001a\u0013aB\u00159d\u000b:$\u0007o\\5oiJ+g\r\u0003\u0005L\u0001\t\u0005\t\u0015!\u0003E\u0003\u001d!'/\u001b<fe\u0002B\u0001\"\u0014\u0001\u0003\u0002\u0003\u0006IAT\u0001\rI\u00164\u0017-\u001e7u\u0007>\u0014Xm\u001d\t\u0003\u001d=K!\u0001U\b\u0003\u0007%sG\u000fC\u0003S\u0001\u0011\u00051+\u0001\u0004=S:LGO\u0010\u000b\b)Z;\u0006,\u0017.\\!\t)\u0006!D\u0001\u0003\u0011\u00159\u0012\u000b1\u0001\u001b\u0011\u0015\u0001\u0013\u000b1\u0001#\u0011\u0015a\u0013\u000b1\u0001/\u0011\u0015)\u0014\u000b1\u00018\u0011\u0015\u0011\u0015\u000b1\u0001E\u0011\u0015i\u0015\u000b1\u0001O\u0011%i\u0006\u00011AA\u0002\u0013\u0005a,A\u0003ti\u0006$X-F\u0001`!\t\u00017M\u0004\u0002VC&\u0011!MA\u0001\u0011\u0003B\u0004H.[2bi&|gn\u0015;bi\u0016L!\u0001Z3\u0003\u000bY\u000bG.^3\n\u0005\u0019|!aC#ok6,'/\u0019;j_:D\u0011\u0002\u001b\u0001A\u0002\u0003\u0007I\u0011A5\u0002\u0013M$\u0018\r^3`I\u0015\fHC\u00016n!\tq1.\u0003\u0002m\u001f\t!QK\\5u\u0011\u001dqw-!AA\u0002}\u000b1\u0001\u001f\u00132\u0011\u0019\u0001\b\u0001)Q\u0005?\u000611\u000f^1uK\u0002B#a\u001c:\u0011\u00059\u0019\u0018B\u0001;\u0010\u0005%!(/\u00198tS\u0016tG\u000fC\u0005w\u0001\u0001\u0007\t\u0019!C\u0001o\u0006IQ\r_3dkR|'o]\u000b\u0002qB)\u0011P (\u0002\u00025\t!P\u0003\u0002|y\u00069Q.\u001e;bE2,'BA?\u0010\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003\u007fj\u0014q\u0001S1tQ6\u000b\u0007\u000fE\u0002V\u0003\u0007I1!!\u0002\u0003\u00051)\u00050Z2vi>\u0014H)Z:d\u0011-\tI\u0001\u0001a\u0001\u0002\u0004%\t!a\u0003\u0002\u001b\u0015DXmY;u_J\u001cx\fJ3r)\rQ\u0017Q\u0002\u0005\t]\u0006\u001d\u0011\u0011!a\u0001q\"9\u0011\u0011\u0003\u0001!B\u0013A\u0018AC3yK\u000e,Ho\u001c:tA!\u001a\u0011q\u0002:\t\u0017\u0005]\u0001\u00011AA\u0002\u0013\u0005\u0011\u0011D\u0001\u0011e\u0016lwN^3e\u000bb,7-\u001e;peN,\"!a\u0007\u0011\u000be\fi\"!\u0001\n\u0007\u0005}!PA\u0006BeJ\f\u0017PQ;gM\u0016\u0014\bbCA\u0012\u0001\u0001\u0007\t\u0019!C\u0001\u0003K\tAC]3n_Z,G-\u0012=fGV$xN]:`I\u0015\fHc\u00016\u0002(!Ia.!\t\u0002\u0002\u0003\u0007\u00111\u0004\u0005\t\u0003W\u0001\u0001\u0015)\u0003\u0002\u001c\u0005\t\"/Z7pm\u0016$W\t_3dkR|'o\u001d\u0011)\u0007\u0005%\"\u000fC\u0006\u00022\u0001\u0001\r\u00111A\u0005\u0002\u0005M\u0012\u0001D2pe\u0016\u001cxI]1oi\u0016$W#\u0001(\t\u0017\u0005]\u0002\u00011AA\u0002\u0013\u0005\u0011\u0011H\u0001\u0011G>\u0014Xm]$sC:$X\rZ0%KF$2A[A\u001e\u0011!q\u0017QGA\u0001\u0002\u0004q\u0005bBA \u0001\u0001\u0006KAT\u0001\u000eG>\u0014Xm]$sC:$X\r\u001a\u0011)\u0007\u0005u\"\u000f\u0003\u0006\u0002F\u0001\u0001\r\u00111A\u0005\u0002e\tq!\u001a8e)&lW\rC\u0006\u0002J\u0001\u0001\r\u00111A\u0005\u0002\u0005-\u0013aC3oIRKW.Z0%KF$2A[A'\u0011!q\u0017qIA\u0001\u0002\u0004Q\u0002bBA)\u0001\u0001\u0006KAG\u0001\tK:$G+[7fA!\u001a\u0011q\n:\t\u0017\u0005]\u0003\u00011AA\u0002\u0013\u0005\u0011\u0011L\u0001\nCB\u00048k\\;sG\u0016,\"!a\u0017\u0011\u0007U\u000bi&C\u0002\u0002`\t\u0011\u0011#\u00119qY&\u001c\u0017\r^5p]N{WO]2f\u0011-\t\u0019\u0007\u0001a\u0001\u0002\u0004%\t!!\u001a\u0002\u001b\u0005\u0004\boU8ve\u000e,w\fJ3r)\rQ\u0017q\r\u0005\n]\u0006\u0005\u0014\u0011!a\u0001\u00037B\u0001\"a\u001b\u0001A\u0003&\u00111L\u0001\u000bCB\u00048k\\;sG\u0016\u0004\u0003fAA5e\"a\u0011\u0011\u000f\u0001A\u0002\u0003\u0007I\u0011\u0001\u0002\u00024\u0005iQ\r_3dkR|'\u000fT5nSRDA\"!\u001e\u0001\u0001\u0004\u0005\r\u0011\"\u0001\u0003\u0003o\n\u0011#\u001a=fGV$xN\u001d'j[&$x\fJ3r)\rQ\u0017\u0011\u0010\u0005\t]\u0006M\u0014\u0011!a\u0001\u001d\"9\u0011Q\u0010\u0001!B\u0013q\u0015AD3yK\u000e,Ho\u001c:MS6LG\u000f\t\u0015\u0004\u0003w\u0012\bbCAB\u0001\u0001\u0007\t\u0019!C\u0005\u0003g\taB\\3yi\u0016CXmY;u_JLE\rC\u0006\u0002\b\u0002\u0001\r\u00111A\u0005\n\u0005%\u0015A\u00058fqR,\u00050Z2vi>\u0014\u0018\nZ0%KF$2A[AF\u0011!q\u0017QQA\u0001\u0002\u0004q\u0005bBAH\u0001\u0001\u0006KAT\u0001\u0010]\u0016DH/\u0012=fGV$xN]%eA!\u001a\u0011Q\u0012:\t\u000f\u0005U\u0005\u0001\"\u0003\u0002\u0018\u0006Q!/Z1e\u001f\nTWm\u0019;\u0015\u0007)\fI\n\u0003\u0005\u0002\u001c\u0006M\u0005\u0019AAO\u0003\tIg\u000e\u0005\u0003\u0002 \u0006\u0015VBAAQ\u0015\r\t\u0019kO\u0001\u0003S>LA!a*\u0002\"\n\trJ\u00196fGRLe\u000e];u'R\u0014X-Y7\t\u000f\u0005-\u0006\u0001\"\u0003\u0002.\u0006!\u0011N\\5u)\u0005Q\u0007bBAY\u0001\u0011%\u00111W\u0001\u000e]\u0016<X\t_3dkR|'/\u00133\u0015\u00079\u000b)\f\u0003\u0006\u00028\u0006=\u0006\u0013!a\u0001\u0003s\u000bQ!^:f\u0013\u0012\u0003BADA^\u001d&\u0019\u0011QX\b\u0003\r=\u0003H/[8o\u0011!\t\t\r\u0001C\u0001\u0005\u0005\r\u0017aC1eI\u0016CXmY;u_J$\u0002\"!\u0001\u0002F\u0006=\u00171\u001b\u0005\t\u0003\u000f\fy\f1\u0001\u0002J\u00061qo\u001c:lKJ\u00042!VAf\u0013\r\tiM\u0001\u0002\u000b/>\u00148.\u001a:J]\u001a|\u0007bBAi\u0003\u007f\u0003\rAT\u0001\u0006G>\u0014Xm\u001d\u0005\u000b\u0003o\u000by\f%AA\u0002\u0005e\u0006\u0002CAl\u0001\u0011\u0005!!!7\u0002\u001dI,Wn\u001c<f\u000bb,7-\u001e;peR\u0019!.a7\t\u0011\u0005u\u0017Q\u001ba\u0001\u0003\u0003\tA!\u001a=fG\"I\u0011\u0011\u001d\u0001C\u0002\u0013%\u00111G\u0001\u000fe\u0016\fX/Z:uK\u0012\u001cuN]3t\u0011\u001d\t)\u000f\u0001Q\u0001\n9\u000bqB]3rk\u0016\u001cH/\u001a3D_J,7\u000f\t\u0005\t\u0003S\u0004A\u0011\u0001\u0002\u00024\u0005I1m\u001c:fg2+g\r\u001e\u0005\n\u0003[\u0004\u0001\u0019!C\u0005\u0003g\t1b\u0018:fiJL8i\\;oi\"I\u0011\u0011\u001f\u0001A\u0002\u0013%\u00111_\u0001\u0010?J,GO]=D_VtGo\u0018\u0013fcR\u0019!.!>\t\u00119\fy/!AA\u00029Cq!!?\u0001A\u0003&a*\u0001\u0007`e\u0016$(/_\"pk:$\b\u0005\u0003\u0005\u0002~\u0002!\tAAA\u001a\u0003)\u0011X\r\u001e:z\u0007>,h\u000e\u001e\u0005\t\u0005\u0003\u0001A\u0011\u0001\u0002\u0003\u0004\u0005\u0019\u0012N\\2sK6,g\u000e\u001e*fiJL8i\\;oiR\ta\n\u0003\u0005\u0003\b\u0001!\tAAAW\u0003=\u0011Xm]3u%\u0016$(/_\"pk:$\b\u0002\u0003B\u0006\u0001\u0011\u0005!A!\u0004\u0002\u00195\f'o\u001b$j]&\u001c\b.\u001a3\u0015\u0007)\u0014y\u0001C\u0004\u0003\u0012\t%\u0001\u0019A0\u0002\u0011\u0015tGm\u0015;bi\u0016D\u0001B!\u0006\u0001\t\u0003\u0011!qC\u0001\u000bSN4\u0015N\\5tQ\u0016$WC\u0001B\r!\rq!1D\u0005\u0004\u0005;y!a\u0002\"p_2,\u0017M\u001c\u0005\t\u0005C\u0001A\u0011\u0001\u0003\u00024\u0005\u0001r-\u001a;Fq\u0016\u001cW\u000f^8s\u0019&l\u0017\u000e\u001e\u0005\u0007\u0005K\u0001A\u0011A\r\u0002\u0011\u0011,(/\u0019;j_:D\u0011B!\u000b\u0001#\u0003%IAa\u000b\u0002/9,w/\u0012=fGV$xN]%eI\u0011,g-Y;mi\u0012\nTC\u0001B\u0017U\u0011\tILa\f,\u0005\tE\u0002\u0003\u0002B\u001a\u0005{i!A!\u000e\u000b\t\t]\"\u0011H\u0001\nk:\u001c\u0007.Z2lK\u0012T1Aa\u000f\u0010\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0005\u007f\u0011)DA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016D\u0011Ba\u0011\u0001#\u0003%\tAa\u000b\u0002+\u0005$G-\u0012=fGV$xN\u001d\u0013eK\u001a\fW\u000f\u001c;%g\u0001"
)
public class ApplicationInfo implements Serializable {
   private final long startTime;
   private final String id;
   private final ApplicationDescription desc;
   private final Date submitDate;
   private final RpcEndpointRef driver;
   public final int org$apache$spark$deploy$master$ApplicationInfo$$defaultCores;
   private transient Enumeration.Value state;
   private transient HashMap<Object, ExecutorDesc> executors;
   private transient ArrayBuffer<ExecutorDesc> removedExecutors;
   private transient int coresGranted;
   private transient long endTime;
   private transient ApplicationSource appSource;
   private transient int executorLimit;
   private transient int nextExecutorId;
   private final int requestedCores;
   private int _retryCount;

   public long startTime() {
      return this.startTime;
   }

   public String id() {
      return this.id;
   }

   public ApplicationDescription desc() {
      return this.desc;
   }

   public Date submitDate() {
      return this.submitDate;
   }

   public RpcEndpointRef driver() {
      return this.driver;
   }

   public Enumeration.Value state() {
      return this.state;
   }

   public void state_$eq(Enumeration.Value x$1) {
      this.state = x$1;
   }

   public HashMap<Object, ExecutorDesc> executors() {
      return this.executors;
   }

   public void executors_$eq(HashMap<Object, ExecutorDesc> x$1) {
      this.executors = x$1;
   }

   public ArrayBuffer<ExecutorDesc> removedExecutors() {
      return this.removedExecutors;
   }

   public void removedExecutors_$eq(ArrayBuffer<ExecutorDesc> x$1) {
      this.removedExecutors = x$1;
   }

   public int coresGranted() {
      return this.coresGranted;
   }

   public void coresGranted_$eq(int x$1) {
      this.coresGranted = x$1;
   }

   public long endTime() {
      return this.endTime;
   }

   public void endTime_$eq(long x$1) {
      this.endTime = x$1;
   }

   public ApplicationSource appSource() {
      return this.appSource;
   }

   public void appSource_$eq(ApplicationSource x$1) {
      this.appSource = x$1;
   }

   public int executorLimit() {
      return this.executorLimit;
   }

   public void executorLimit_$eq(int x$1) {
      this.executorLimit = x$1;
   }

   private int nextExecutorId() {
      return this.nextExecutorId;
   }

   private void nextExecutorId_$eq(int x$1) {
      this.nextExecutorId = x$1;
   }

   private void readObject(ObjectInputStream in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ApplicationInfo $outer;
         private final ObjectInputStream in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.in$1.defaultReadObject();
            this.$outer.org$apache$spark$deploy$master$ApplicationInfo$$init();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   public void org$apache$spark$deploy$master$ApplicationInfo$$init() {
      this.state_$eq(ApplicationState$.MODULE$.WAITING());
      this.executors_$eq(new HashMap());
      this.coresGranted_$eq(0);
      this.endTime_$eq(-1L);
      this.appSource_$eq(new ApplicationSource(this));
      this.nextExecutorId_$eq(0);
      this.removedExecutors_$eq(new ArrayBuffer());
      this.executorLimit_$eq(BoxesRunTime.unboxToInt(this.desc().initialExecutorLimit().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return Integer.MAX_VALUE;
         }
      })));
   }

   private int newExecutorId(Option<Object> useID) {
      int var5;
      if (useID instanceof Some) {
         Some var3 = (Some)useID;
         int id = BoxesRunTime.unboxToInt(var3.x());
         this.nextExecutorId_$eq(package$.MODULE$.max(this.nextExecutorId(), id + 1));
         var5 = id;
      } else {
         if (!None$.MODULE$.equals(useID)) {
            throw new MatchError(useID);
         }

         int id = this.nextExecutorId();
         this.nextExecutorId_$eq(this.nextExecutorId() + 1);
         var5 = id;
      }

      return var5;
   }

   private Option<Object> newExecutorId$default$1() {
      return None$.MODULE$;
   }

   public ExecutorDesc addExecutor(WorkerInfo worker, int cores, Option<Object> useID) {
      ExecutorDesc exec = new ExecutorDesc(this.newExecutorId(useID), this, worker, cores, this.desc().memoryPerExecutorMB());
      this.executors().update(BoxesRunTime.boxToInteger(exec.id()), exec);
      this.coresGranted_$eq(this.coresGranted() + cores);
      return exec;
   }

   public Option<Object> addExecutor$default$3() {
      return None$.MODULE$;
   }

   public void removeExecutor(ExecutorDesc exec) {
      if (this.executors().contains(BoxesRunTime.boxToInteger(exec.id()))) {
         this.removedExecutors().$plus$eq(this.executors().apply(BoxesRunTime.boxToInteger(exec.id())));
         this.executors().$minus$eq(BoxesRunTime.boxToInteger(exec.id()));
         this.coresGranted_$eq(this.coresGranted() - exec.cores());
      }

   }

   private int requestedCores() {
      return this.requestedCores;
   }

   public int coresLeft() {
      return this.requestedCores() - this.coresGranted();
   }

   private int _retryCount() {
      return this._retryCount;
   }

   private void _retryCount_$eq(int x$1) {
      this._retryCount = x$1;
   }

   public int retryCount() {
      return this._retryCount();
   }

   public int incrementRetryCount() {
      this._retryCount_$eq(this._retryCount() + 1);
      return this._retryCount();
   }

   public void resetRetryCount() {
      this._retryCount_$eq(0);
   }

   public void markFinished(Enumeration.Value endState) {
      this.state_$eq(endState);
      this.endTime_$eq(System.currentTimeMillis());
   }

   public boolean isFinished() {
      boolean var4;
      label32: {
         Enumeration.Value var10000 = this.state();
         Enumeration.Value var1 = ApplicationState$.MODULE$.WAITING();
         if (var10000 == null) {
            if (var1 == null) {
               break label32;
            }
         } else if (var10000.equals(var1)) {
            break label32;
         }

         var10000 = this.state();
         Enumeration.Value var2 = ApplicationState$.MODULE$.RUNNING();
         if (var10000 == null) {
            if (var2 == null) {
               break label32;
            }
         } else if (var10000.equals(var2)) {
            break label32;
         }

         var4 = true;
         return var4;
      }

      var4 = false;
      return var4;
   }

   public int getExecutorLimit() {
      return this.executorLimit();
   }

   public long duration() {
      return this.endTime() != -1L ? this.endTime() - this.startTime() : System.currentTimeMillis() - this.startTime();
   }

   public ApplicationInfo(long startTime, String id, ApplicationDescription desc, Date submitDate, RpcEndpointRef driver, int defaultCores) {
      this.startTime = startTime;
      this.id = id;
      this.desc = desc;
      this.submitDate = submitDate;
      this.driver = driver;
      this.org$apache$spark$deploy$master$ApplicationInfo$$defaultCores = defaultCores;
      this.org$apache$spark$deploy$master$ApplicationInfo$$init();
      this.requestedCores = BoxesRunTime.unboxToInt(desc.maxCores().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ApplicationInfo $outer;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return this.$outer.org$apache$spark$deploy$master$ApplicationInfo$$defaultCores;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      this._retryCount = 0;
   }
}
