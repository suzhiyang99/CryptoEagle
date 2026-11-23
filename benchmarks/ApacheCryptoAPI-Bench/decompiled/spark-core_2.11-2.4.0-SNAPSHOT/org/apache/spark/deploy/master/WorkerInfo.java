package org.apache.spark.deploy.master;

import java.io.ObjectInputStream;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.util.Utils$;
import scala.Enumeration;
import scala.Predef$;
import scala.Serializable;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=g!B\u0001\u0003\u0001\u0019a!AC,pe.,'/\u00138g_*\u00111\u0001B\u0001\u0007[\u0006\u001cH/\u001a:\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\r\u0001Qb\u0005\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00059!\u0012BA\u000b\u0010\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!9\u0002A!b\u0001\n\u0003I\u0012AA5e\u0007\u0001)\u0012A\u0007\t\u00037yq!A\u0004\u000f\n\u0005uy\u0011A\u0002)sK\u0012,g-\u0003\u0002 A\t11\u000b\u001e:j]\u001eT!!H\b\t\u0011\t\u0002!\u0011!Q\u0001\ni\t1!\u001b3!\u0011!!\u0003A!b\u0001\n\u0003I\u0012\u0001\u00025pgRD\u0001B\n\u0001\u0003\u0002\u0003\u0006IAG\u0001\u0006Q>\u001cH\u000f\t\u0005\tQ\u0001\u0011)\u0019!C\u0001S\u0005!\u0001o\u001c:u+\u0005Q\u0003C\u0001\b,\u0013\tasBA\u0002J]RD\u0001B\f\u0001\u0003\u0002\u0003\u0006IAK\u0001\u0006a>\u0014H\u000f\t\u0005\ta\u0001\u0011)\u0019!C\u0001S\u0005)1m\u001c:fg\"A!\u0007\u0001B\u0001B\u0003%!&\u0001\u0004d_J,7\u000f\t\u0005\ti\u0001\u0011)\u0019!C\u0001S\u00051Q.Z7pefD\u0001B\u000e\u0001\u0003\u0002\u0003\u0006IAK\u0001\b[\u0016lwN]=!\u0011!A\u0004A!b\u0001\n\u0003I\u0014\u0001C3oIB|\u0017N\u001c;\u0016\u0003i\u0002\"a\u000f \u000e\u0003qR!!\u0010\u0004\u0002\u0007I\u00048-\u0003\u0002@y\tq!\u000b]2F]\u0012\u0004x.\u001b8u%\u00164\u0007\u0002C!\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001e\u0002\u0013\u0015tG\r]8j]R\u0004\u0003\u0002C\"\u0001\u0005\u000b\u0007I\u0011A\r\u0002\u0019],'-V5BI\u0012\u0014Xm]:\t\u0011\u0015\u0003!\u0011!Q\u0001\ni\tQb^3c+&\fE\r\u001a:fgN\u0004\u0003\"B$\u0001\t\u0003A\u0015A\u0002\u001fj]&$h\b\u0006\u0005J\u00172kej\u0014)R!\tQ\u0005!D\u0001\u0003\u0011\u00159b\t1\u0001\u001b\u0011\u0015!c\t1\u0001\u001b\u0011\u0015Ac\t1\u0001+\u0011\u0015\u0001d\t1\u0001+\u0011\u0015!d\t1\u0001+\u0011\u0015Ad\t1\u0001;\u0011\u0015\u0019e\t1\u0001\u001b\u0011%\u0019\u0006\u00011AA\u0002\u0013\u0005A+A\u0005fq\u0016\u001cW\u000f^8sgV\tQ\u000b\u0005\u0003W7jiV\"A,\u000b\u0005aK\u0016aB7vi\u0006\u0014G.\u001a\u0006\u00035>\t!bY8mY\u0016\u001cG/[8o\u0013\tavKA\u0004ICNDW*\u00199\u0011\u0005)s\u0016BA0\u0003\u00051)\u00050Z2vi>\u0014H)Z:d\u0011%\t\u0007\u00011AA\u0002\u0013\u0005!-A\u0007fq\u0016\u001cW\u000f^8sg~#S-\u001d\u000b\u0003G\u001a\u0004\"A\u00043\n\u0005\u0015|!\u0001B+oSRDqa\u001a1\u0002\u0002\u0003\u0007Q+A\u0002yIEBa!\u001b\u0001!B\u0013)\u0016AC3yK\u000e,Ho\u001c:tA!\u0012\u0001n\u001b\t\u0003\u001d1L!!\\\b\u0003\u0013Q\u0014\u0018M\\:jK:$\b\"C8\u0001\u0001\u0004\u0005\r\u0011\"\u0001q\u0003\u001d!'/\u001b<feN,\u0012!\u001d\t\u0005-nS\"\u000f\u0005\u0002Kg&\u0011AO\u0001\u0002\u000b\tJLg/\u001a:J]\u001a|\u0007\"\u0003<\u0001\u0001\u0004\u0005\r\u0011\"\u0001x\u0003-!'/\u001b<feN|F%Z9\u0015\u0005\rD\bbB4v\u0003\u0003\u0005\r!\u001d\u0005\u0007u\u0002\u0001\u000b\u0015B9\u0002\u0011\u0011\u0014\u0018N^3sg\u0002B#!_6\t\u0013u\u0004\u0001\u0019!a\u0001\n\u0003q\u0018!B:uCR,W#A@\u0011\t\u0005\u0005\u0011q\u0001\b\u0004\u0015\u0006\r\u0011bAA\u0003\u0005\u0005Yqk\u001c:lKJ\u001cF/\u0019;f\u0013\u0011\tI!a\u0003\u0003\u000bY\u000bG.^3\n\u0007\u00055qBA\u0006F]VlWM]1uS>t\u0007bCA\t\u0001\u0001\u0007\t\u0019!C\u0001\u0003'\t\u0011b\u001d;bi\u0016|F%Z9\u0015\u0007\r\f)\u0002\u0003\u0005h\u0003\u001f\t\t\u00111\u0001\u0000\u0011\u001d\tI\u0002\u0001Q!\n}\faa\u001d;bi\u0016\u0004\u0003fAA\fW\"Q\u0011q\u0004\u0001A\u0002\u0003\u0007I\u0011A\u0015\u0002\u0013\r|'/Z:Vg\u0016$\u0007bCA\u0012\u0001\u0001\u0007\t\u0019!C\u0001\u0003K\tQbY8sKN,6/\u001a3`I\u0015\fHcA2\u0002(!Aq-!\t\u0002\u0002\u0003\u0007!\u0006C\u0004\u0002,\u0001\u0001\u000b\u0015\u0002\u0016\u0002\u0015\r|'/Z:Vg\u0016$\u0007\u0005K\u0002\u0002*-D!\"!\r\u0001\u0001\u0004\u0005\r\u0011\"\u0001*\u0003)iW-\\8ssV\u001bX\r\u001a\u0005\f\u0003k\u0001\u0001\u0019!a\u0001\n\u0003\t9$\u0001\bnK6|'/_+tK\u0012|F%Z9\u0015\u0007\r\fI\u0004\u0003\u0005h\u0003g\t\t\u00111\u0001+\u0011\u001d\ti\u0004\u0001Q!\n)\n1\"\\3n_JLXk]3eA!\u001a\u00111H6\t\u0017\u0005\r\u0003\u00011AA\u0002\u0013\u0005\u0011QI\u0001\u000eY\u0006\u001cH\u000fS3beR\u0014W-\u0019;\u0016\u0005\u0005\u001d\u0003c\u0001\b\u0002J%\u0019\u00111J\b\u0003\t1{gn\u001a\u0005\f\u0003\u001f\u0002\u0001\u0019!a\u0001\n\u0003\t\t&A\tmCN$\b*Z1si\n,\u0017\r^0%KF$2aYA*\u0011%9\u0017QJA\u0001\u0002\u0004\t9\u0005\u0003\u0005\u0002X\u0001\u0001\u000b\u0015BA$\u00039a\u0017m\u001d;IK\u0006\u0014HOY3bi\u0002B3!!\u0016l\u0011\u0019\ti\u0006\u0001C\u0001S\u0005I1m\u001c:fg\u001a\u0013X-\u001a\u0005\u0007\u0003C\u0002A\u0011A\u0015\u0002\u00155,Wn\u001c:z\rJ,W\rC\u0004\u0002f\u0001!I!a\u001a\u0002\u0015I,\u0017\rZ(cU\u0016\u001cG\u000fF\u0002d\u0003SB\u0001\"a\u001b\u0002d\u0001\u0007\u0011QN\u0001\u0003S:\u0004B!a\u001c\u0002z5\u0011\u0011\u0011\u000f\u0006\u0005\u0003g\n)(\u0001\u0002j_*\u0011\u0011qO\u0001\u0005U\u00064\u0018-\u0003\u0003\u0002|\u0005E$!E(cU\u0016\u001cG/\u00138qkR\u001cFO]3b[\"9\u0011q\u0010\u0001\u0005\n\u0005\u0005\u0015\u0001B5oSR$\u0012a\u0019\u0005\u0007\u0003\u000b\u0003A\u0011A\r\u0002\u0011!|7\u000f\u001e)peRDq!!#\u0001\t\u0003\tY)A\u0006bI\u0012,\u00050Z2vi>\u0014HcA2\u0002\u000e\"9\u0011qRAD\u0001\u0004i\u0016\u0001B3yK\u000eDq!a%\u0001\t\u0003\t)*\u0001\bsK6|g/Z#yK\u000e,Ho\u001c:\u0015\u0007\r\f9\nC\u0004\u0002\u0010\u0006E\u0005\u0019A/\t\u000f\u0005m\u0005\u0001\"\u0001\u0002\u001e\u0006Y\u0001.Y:Fq\u0016\u001cW\u000f^8s)\u0011\ty*!*\u0011\u00079\t\t+C\u0002\u0002$>\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005\u0002(\u0006e\u0005\u0019AAU\u0003\r\t\u0007\u000f\u001d\t\u0004\u0015\u0006-\u0016bAAW\u0005\ty\u0011\t\u001d9mS\u000e\fG/[8o\u0013:4w\u000eC\u0004\u00022\u0002!\t!a-\u0002\u0013\u0005$G\r\u0012:jm\u0016\u0014HcA2\u00026\"9\u0011qWAX\u0001\u0004\u0011\u0018A\u00023sSZ,'\u000fC\u0004\u0002<\u0002!\t!!0\u0002\u0019I,Wn\u001c<f\tJLg/\u001a:\u0015\u0007\r\fy\fC\u0004\u00028\u0006e\u0006\u0019\u0001:\t\u000f\u0005\r\u0007\u0001\"\u0001\u0002F\u0006A1/\u001a;Ti\u0006$X\rF\u0002d\u0003\u000fDa!`Aa\u0001\u0004y\bbBAf\u0001\u0011\u0005\u0011QZ\u0001\bSN\fE.\u001b<f)\t\ty\n"
)
public class WorkerInfo implements Serializable {
   private final String id;
   private final String host;
   private final int port;
   private final int cores;
   private final int memory;
   private final RpcEndpointRef endpoint;
   private final String webUiAddress;
   private transient HashMap<String, ExecutorDesc> executors;
   private transient HashMap<String, DriverInfo> drivers;
   private transient Enumeration.Value state;
   private transient int coresUsed;
   private transient int memoryUsed;
   private transient long lastHeartbeat;

   public String id() {
      return this.id;
   }

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }

   public int cores() {
      return this.cores;
   }

   public int memory() {
      return this.memory;
   }

   public RpcEndpointRef endpoint() {
      return this.endpoint;
   }

   public String webUiAddress() {
      return this.webUiAddress;
   }

   public HashMap<String, ExecutorDesc> executors() {
      return this.executors;
   }

   public void executors_$eq(HashMap<String, ExecutorDesc> x$1) {
      this.executors = x$1;
   }

   public HashMap<String, DriverInfo> drivers() {
      return this.drivers;
   }

   public void drivers_$eq(HashMap<String, DriverInfo> x$1) {
      this.drivers = x$1;
   }

   public Enumeration.Value state() {
      return this.state;
   }

   public void state_$eq(Enumeration.Value x$1) {
      this.state = x$1;
   }

   public int coresUsed() {
      return this.coresUsed;
   }

   public void coresUsed_$eq(int x$1) {
      this.coresUsed = x$1;
   }

   public int memoryUsed() {
      return this.memoryUsed;
   }

   public void memoryUsed_$eq(int x$1) {
      this.memoryUsed = x$1;
   }

   public long lastHeartbeat() {
      return this.lastHeartbeat;
   }

   public void lastHeartbeat_$eq(long x$1) {
      this.lastHeartbeat = x$1;
   }

   public int coresFree() {
      return this.cores() - this.coresUsed();
   }

   public int memoryFree() {
      return this.memory() - this.memoryUsed();
   }

   private void readObject(ObjectInputStream in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WorkerInfo $outer;
         private final ObjectInputStream in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.in$1.defaultReadObject();
            this.$outer.org$apache$spark$deploy$master$WorkerInfo$$init();
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

   public void org$apache$spark$deploy$master$WorkerInfo$$init() {
      this.executors_$eq(new HashMap());
      this.drivers_$eq(new HashMap());
      this.state_$eq(WorkerState$.MODULE$.ALIVE());
      this.coresUsed_$eq(0);
      this.memoryUsed_$eq(0);
      this.lastHeartbeat_$eq(System.currentTimeMillis());
   }

   public String hostPort() {
      Predef$.MODULE$.assert(this.port() > 0);
      return (new StringBuilder()).append((Object)this.host()).append((Object)":").append((Object)BoxesRunTime.boxToInteger(this.port())).toString();
   }

   public void addExecutor(ExecutorDesc exec) {
      this.executors().update(exec.fullId(), exec);
      this.coresUsed_$eq(this.coresUsed() + exec.cores());
      this.memoryUsed_$eq(this.memoryUsed() + exec.memory());
   }

   public void removeExecutor(ExecutorDesc exec) {
      if (this.executors().contains(exec.fullId())) {
         this.executors().$minus$eq(exec.fullId());
         this.coresUsed_$eq(this.coresUsed() - exec.cores());
         this.memoryUsed_$eq(this.memoryUsed() - exec.memory());
      }

   }

   public boolean hasExecutor(ApplicationInfo app) {
      return this.executors().values().exists(new Serializable(this, app) {
         public static final long serialVersionUID = 0L;
         private final ApplicationInfo app$1;

         public final boolean apply(ExecutorDesc x$1) {
            boolean var3;
            label23: {
               ApplicationInfo var10000 = x$1.application();
               ApplicationInfo var2 = this.app$1;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = false;
               return var3;
            }

            var3 = true;
            return var3;
         }

         public {
            this.app$1 = app$1;
         }
      });
   }

   public void addDriver(DriverInfo driver) {
      this.drivers().update(driver.id(), driver);
      this.memoryUsed_$eq(this.memoryUsed() + driver.desc().mem());
      this.coresUsed_$eq(this.coresUsed() + driver.desc().cores());
   }

   public void removeDriver(DriverInfo driver) {
      this.drivers().$minus$eq(driver.id());
      this.memoryUsed_$eq(this.memoryUsed() - driver.desc().mem());
      this.coresUsed_$eq(this.coresUsed() - driver.desc().cores());
   }

   public void setState(Enumeration.Value state) {
      this.state_$eq(state);
   }

   public boolean isAlive() {
      boolean var2;
      label23: {
         Enumeration.Value var10000 = this.state();
         Enumeration.Value var1 = WorkerState$.MODULE$.ALIVE();
         if (var10000 == null) {
            if (var1 == null) {
               break label23;
            }
         } else if (var10000.equals(var1)) {
            break label23;
         }

         var2 = false;
         return var2;
      }

      var2 = true;
      return var2;
   }

   public WorkerInfo(String id, String host, int port, int cores, int memory, RpcEndpointRef endpoint, String webUiAddress) {
      this.id = id;
      this.host = host;
      this.port = port;
      this.cores = cores;
      this.memory = memory;
      this.endpoint = endpoint;
      this.webUiAddress = webUiAddress;
      Utils$.MODULE$.checkHost(host);
      Predef$.MODULE$.assert(port > 0);
      this.org$apache$spark$deploy$master$WorkerInfo$$init();
   }
}
