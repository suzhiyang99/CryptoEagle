package org.apache.spark.deploy.master;

import java.io.ObjectInputStream;
import java.util.Date;
import org.apache.spark.deploy.DriverDescription;
import org.apache.spark.util.Utils$;
import scala.Enumeration;
import scala.None$;
import scala.Option;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Eb!B\u0001\u0003\u0001\u0011a!A\u0003#sSZ,'/\u00138g_*\u00111\u0001B\u0001\u0007[\u0006\u001cH/\u001a:\u000b\u0005\u00151\u0011A\u00023fa2|\u0017P\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\r\u0001Qb\u0005\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00059!\u0012BA\u000b\u0010\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!9\u0002A!b\u0001\n\u0003I\u0012!C:uCJ$H+[7f\u0007\u0001)\u0012A\u0007\t\u0003\u001dmI!\u0001H\b\u0003\t1{gn\u001a\u0005\t=\u0001\u0011\t\u0011)A\u00055\u0005Q1\u000f^1siRKW.\u001a\u0011\t\u0011\u0001\u0002!Q1A\u0005\u0002\u0005\n!!\u001b3\u0016\u0003\t\u0002\"a\t\u0014\u000f\u00059!\u0013BA\u0013\u0010\u0003\u0019\u0001&/\u001a3fM&\u0011q\u0005\u000b\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\u0015z\u0001\u0002\u0003\u0016\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002\u0007%$\u0007\u0005\u0003\u0005-\u0001\t\u0015\r\u0011\"\u0001.\u0003\u0011!Wm]2\u0016\u00039\u0002\"a\f\u0019\u000e\u0003\u0011I!!\r\u0003\u0003#\u0011\u0013\u0018N^3s\t\u0016\u001c8M]5qi&|g\u000e\u0003\u00054\u0001\t\u0005\t\u0015!\u0003/\u0003\u0015!Wm]2!\u0011!)\u0004A!b\u0001\n\u00031\u0014AC:vE6LG\u000fR1uKV\tq\u0007\u0005\u00029{5\t\u0011H\u0003\u0002;w\u0005!Q\u000f^5m\u0015\u0005a\u0014\u0001\u00026bm\u0006L!AP\u001d\u0003\t\u0011\u000bG/\u001a\u0005\t\u0001\u0002\u0011\t\u0011)A\u0005o\u0005Y1/\u001e2nSR$\u0015\r^3!\u0011\u0015\u0011\u0005\u0001\"\u0001D\u0003\u0019a\u0014N\\5u}Q)AIR$I\u0013B\u0011Q\tA\u0007\u0002\u0005!)q#\u0011a\u00015!)\u0001%\u0011a\u0001E!)A&\u0011a\u0001]!)Q'\u0011a\u0001o!91\n\u0001a\u0001\n\u0003a\u0015!B:uCR,W#A'\u0011\u00059\u000bfBA#P\u0013\t\u0001&!A\u0006Ee&4XM]*uCR,\u0017B\u0001*T\u0005\u00151\u0016\r\\;f\u0013\t!vBA\u0006F]VlWM]1uS>t\u0007b\u0002,\u0001\u0001\u0004%\taV\u0001\ngR\fG/Z0%KF$\"\u0001W.\u0011\u00059I\u0016B\u0001.\u0010\u0005\u0011)f.\u001b;\t\u000fq+\u0016\u0011!a\u0001\u001b\u0006\u0019\u0001\u0010J\u0019\t\ry\u0003\u0001\u0015)\u0003N\u0003\u0019\u0019H/\u0019;fA!\u0012Q\f\u0019\t\u0003\u001d\u0005L!AY\b\u0003\u0013Q\u0014\u0018M\\:jK:$\bb\u00023\u0001\u0001\u0004%\t!Z\u0001\nKb\u001cW\r\u001d;j_:,\u0012A\u001a\t\u0004\u001d\u001dL\u0017B\u00015\u0010\u0005\u0019y\u0005\u000f^5p]B\u0011!N\u001d\b\u0003WBt!\u0001\\8\u000e\u00035T!A\u001c\r\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0001\u0012BA9\u0010\u0003\u001d\u0001\u0018mY6bO\u0016L!a\u001d;\u0003\u0013\u0015C8-\u001a9uS>t'BA9\u0010\u0011\u001d1\b\u00011A\u0005\u0002]\fQ\"\u001a=dKB$\u0018n\u001c8`I\u0015\fHC\u0001-y\u0011\u001daV/!AA\u0002\u0019DaA\u001f\u0001!B\u00131\u0017AC3yG\u0016\u0004H/[8oA!\u0012\u0011\u0010\u0019\u0005\b{\u0002\u0001\r\u0011\"\u0001\u007f\u0003\u00199xN]6feV\tq\u0010\u0005\u0003\u000fO\u0006\u0005\u0001cA#\u0002\u0004%\u0019\u0011Q\u0001\u0002\u0003\u0015]{'o[3s\u0013:4w\u000eC\u0005\u0002\n\u0001\u0001\r\u0011\"\u0001\u0002\f\u0005Qqo\u001c:lKJ|F%Z9\u0015\u0007a\u000bi\u0001\u0003\u0005]\u0003\u000f\t\t\u00111\u0001\u0000\u0011\u001d\t\t\u0002\u0001Q!\n}\fqa^8sW\u0016\u0014\b\u0005K\u0002\u0002\u0010\u0001Dq!a\u0006\u0001\t\u0013\tI\"\u0001\u0006sK\u0006$wJ\u00196fGR$2\u0001WA\u000e\u0011!\ti\"!\u0006A\u0002\u0005}\u0011AA5o!\u0011\t\t#a\n\u000e\u0005\u0005\r\"bAA\u0013w\u0005\u0011\u0011n\\\u0005\u0005\u0003S\t\u0019CA\tPE*,7\r^%oaV$8\u000b\u001e:fC6Dq!!\f\u0001\t\u0013\ty#\u0001\u0003j]&$H#\u0001-"
)
public class DriverInfo implements Serializable {
   private final long startTime;
   private final String id;
   private final DriverDescription desc;
   private final Date submitDate;
   private transient Enumeration.Value state;
   private transient Option<Exception> exception;
   private transient Option<WorkerInfo> worker;

   public long startTime() {
      return this.startTime;
   }

   public String id() {
      return this.id;
   }

   public DriverDescription desc() {
      return this.desc;
   }

   public Date submitDate() {
      return this.submitDate;
   }

   public Enumeration.Value state() {
      return this.state;
   }

   public void state_$eq(Enumeration.Value x$1) {
      this.state = x$1;
   }

   public Option<Exception> exception() {
      return this.exception;
   }

   public void exception_$eq(Option<Exception> x$1) {
      this.exception = x$1;
   }

   public Option<WorkerInfo> worker() {
      return this.worker;
   }

   public void worker_$eq(Option<WorkerInfo> x$1) {
      this.worker = x$1;
   }

   private void readObject(ObjectInputStream in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DriverInfo $outer;
         private final ObjectInputStream in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.in$1.defaultReadObject();
            this.$outer.org$apache$spark$deploy$master$DriverInfo$$init();
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

   public void org$apache$spark$deploy$master$DriverInfo$$init() {
      this.state_$eq(DriverState$.MODULE$.SUBMITTED());
      this.worker_$eq(None$.MODULE$);
      this.exception_$eq(None$.MODULE$);
   }

   public DriverInfo(long startTime, String id, DriverDescription desc, Date submitDate) {
      this.startTime = startTime;
      this.id = id;
      this.desc = desc;
      this.submitDate = submitDate;
      this.state = DriverState$.MODULE$.SUBMITTED();
      this.exception = None$.MODULE$;
      this.worker = None$.MODULE$;
      this.org$apache$spark$deploy$master$DriverInfo$$init();
   }
}
