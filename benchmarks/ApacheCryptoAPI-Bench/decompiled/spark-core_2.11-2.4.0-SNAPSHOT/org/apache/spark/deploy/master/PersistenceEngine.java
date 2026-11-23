package org.apache.spark.deploy.master;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.rpc.RpcEnv;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005]b!B\u0001\u0003\u0003\u0003i!!\u0005)feNL7\u000f^3oG\u0016,enZ5oK*\u00111\u0001B\u0001\u0007[\u0006\u001cH/\u001a:\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\b\u0011\u0005=\u0011R\"\u0001\t\u000b\u0003E\tQa]2bY\u0006L!a\u0005\t\u0003\r\u0005s\u0017PU3g\u0011\u0015)\u0002\u0001\"\u0001\u0017\u0003\u0019a\u0014N\\5u}Q\tq\u0003\u0005\u0002\u0019\u00015\t!\u0001C\u0003\u001b\u0001\u0019\u00051$A\u0004qKJ\u001c\u0018n\u001d;\u0015\u0007qy\u0002\u0006\u0005\u0002\u0010;%\u0011a\u0004\u0005\u0002\u0005+:LG\u000fC\u0003!3\u0001\u0007\u0011%\u0001\u0003oC6,\u0007C\u0001\u0012&\u001d\ty1%\u0003\u0002%!\u00051\u0001K]3eK\u001aL!AJ\u0014\u0003\rM#(/\u001b8h\u0015\t!\u0003\u0003C\u0003*3\u0001\u0007!&A\u0002pE*\u0004\"a\u000b\u0019\u000e\u00031R!!\f\u0018\u0002\t1\fgn\u001a\u0006\u0002_\u0005!!.\u0019<b\u0013\t\tDF\u0001\u0004PE*,7\r\u001e\u0005\u0006g\u00011\t\u0001N\u0001\nk:\u0004XM]:jgR$\"\u0001H\u001b\t\u000b\u0001\u0012\u0004\u0019A\u0011\t\u000b]\u0002a\u0011\u0001\u001d\u0002\tI,\u0017\rZ\u000b\u0003s%#\"A\u000f.\u0015\u0005m\u0012\u0006c\u0001\u001fE\u000f:\u0011QH\u0011\b\u0003}\u0005k\u0011a\u0010\u0006\u0003\u00012\ta\u0001\u0010:p_Rt\u0014\"A\t\n\u0005\r\u0003\u0012a\u00029bG.\fw-Z\u0005\u0003\u000b\u001a\u00131aU3r\u0015\t\u0019\u0005\u0003\u0005\u0002I\u00132\u0001A!\u0002&7\u0005\u0004Y%!\u0001+\u0012\u00051{\u0005CA\bN\u0013\tq\u0005CA\u0004O_RD\u0017N\\4\u0011\u0005=\u0001\u0016BA)\u0011\u0005\r\te.\u001f\u0005\b'Z\n\t\u0011q\u0001U\u0003))g/\u001b3f]\u000e,G%\r\t\u0004+b;U\"\u0001,\u000b\u0005]\u0003\u0012a\u0002:fM2,7\r^\u0005\u00033Z\u0013\u0001b\u00117bgN$\u0016m\u001a\u0005\u00067Z\u0002\r!I\u0001\u0007aJ,g-\u001b=\t\u000bu\u0003AQ\u00010\u0002\u001d\u0005$G-\u00119qY&\u001c\u0017\r^5p]R\u0011Ad\u0018\u0005\u0006Ar\u0003\r!Y\u0001\u0004CB\u0004\bC\u0001\rc\u0013\t\u0019'AA\bBaBd\u0017nY1uS>t\u0017J\u001c4p\u0011\u0015)\u0007\u0001\"\u0002g\u0003E\u0011X-\\8wK\u0006\u0003\b\u000f\\5dCRLwN\u001c\u000b\u00039\u001dDQ\u0001\u00193A\u0002\u0005DQ!\u001b\u0001\u0005\u0006)\f\u0011\"\u00193e/>\u00148.\u001a:\u0015\u0005qY\u0007\"\u00027i\u0001\u0004i\u0017AB<pe.,'\u000f\u0005\u0002\u0019]&\u0011qN\u0001\u0002\u000b/>\u00148.\u001a:J]\u001a|\u0007\"B9\u0001\t\u000b\u0011\u0018\u0001\u0004:f[>4XmV8sW\u0016\u0014HC\u0001\u000ft\u0011\u0015a\u0007\u000f1\u0001n\u0011\u0015)\b\u0001\"\u0002w\u0003%\tG\r\u001a#sSZ,'\u000f\u0006\u0002\u001do\")\u0001\u0010\u001ea\u0001s\u00061AM]5wKJ\u0004\"\u0001\u0007>\n\u0005m\u0014!A\u0003#sSZ,'/\u00138g_\")Q\u0010\u0001C\u0003}\u0006a!/Z7pm\u0016$%/\u001b<feR\u0011Ad \u0005\u0006qr\u0004\r!\u001f\u0005\b\u0003\u0007\u0001AQAA\u0003\u0003E\u0011X-\u00193QKJ\u001c\u0018n\u001d;fI\u0012\u000bG/\u0019\u000b\u0005\u0003\u000f\t\u0019\u0002E\u0005\u0010\u0003\u0013\ti!a\u0004\u0002\u0012%\u0019\u00111\u0002\t\u0003\rQ+\b\u000f\\34!\raD)\u0019\t\u0004y\u0011K\bc\u0001\u001fE[\"A\u0011QCA\u0001\u0001\u0004\t9\"\u0001\u0004sa\u000e,eN\u001e\t\u0005\u00033\ty\"\u0004\u0002\u0002\u001c)\u0019\u0011Q\u0004\u0004\u0002\u0007I\u00048-\u0003\u0003\u0002\"\u0005m!A\u0002*qG\u0016sg\u000fC\u0004\u0002&\u0001!\t!a\n\u0002\u000b\rdwn]3\u0015\u0003qA3\u0001AA\u0016!\u0011\ti#a\r\u000e\u0005\u0005=\"bAA\u0019\r\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005U\u0012q\u0006\u0002\r\t\u00164X\r\\8qKJ\f\u0005/\u001b"
)
public abstract class PersistenceEngine {
   public abstract void persist(String var1, Object var2);

   public abstract void unpersist(String var1);

   public abstract <T> Seq<T> read(String var1, ClassTag<T> var2);

   public final void addApplication(ApplicationInfo app) {
      this.persist((new StringBuilder()).append((Object)"app_").append((Object)app.id()).toString(), app);
   }

   public final void removeApplication(ApplicationInfo app) {
      this.unpersist((new StringBuilder()).append((Object)"app_").append((Object)app.id()).toString());
   }

   public final void addWorker(WorkerInfo worker) {
      this.persist((new StringBuilder()).append((Object)"worker_").append((Object)worker.id()).toString(), worker);
   }

   public final void removeWorker(WorkerInfo worker) {
      this.unpersist((new StringBuilder()).append((Object)"worker_").append((Object)worker.id()).toString());
   }

   public final void addDriver(DriverInfo driver) {
      this.persist((new StringBuilder()).append((Object)"driver_").append((Object)driver.id()).toString(), driver);
   }

   public final void removeDriver(DriverInfo driver) {
      this.unpersist((new StringBuilder()).append((Object)"driver_").append((Object)driver.id()).toString());
   }

   public final Tuple3<Seq<ApplicationInfo>, Seq<DriverInfo>, Seq<WorkerInfo>> readPersistedData(RpcEnv rpcEnv) {
      return (Tuple3)rpcEnv.deserialize(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final PersistenceEngine $outer;

         public final Tuple3<Seq<ApplicationInfo>, Seq<DriverInfo>, Seq<WorkerInfo>> apply() {
            return new Tuple3<Seq<ApplicationInfo>, Seq<DriverInfo>, Seq<WorkerInfo>>(this.$outer.read("app_", ClassTag$.MODULE$.apply(ApplicationInfo.class)), this.$outer.read("driver_", ClassTag$.MODULE$.apply(DriverInfo.class)), this.$outer.read("worker_", ClassTag$.MODULE$.apply(WorkerInfo.class)));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public void close() {
   }
}
