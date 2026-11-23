package org.apache.spark.deploy.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.util.kvstore.KVIndex;
import scala.Option;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.math.Ordering;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001e4Q!\u0001\u0002\u0001\u00051\u0011a#\u00119qY&\u001c\u0017\r^5p]&sgm\\,sCB\u0004XM\u001d\u0006\u0003\u0007\u0011\tq\u0001[5ti>\u0014\u0018P\u0003\u0002\u0006\r\u00051A-\u001a9m_fT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007\u0002\u0003\u000b\u0001\u0005\u000b\u0007I\u0011\u0001\f\u0002\t%tgm\\\u0002\u0001+\u00059\u0002C\u0001\r \u001b\u0005I\"B\u0001\u000e\u001c\u0003\t1\u0018G\u0003\u0002\u001d;\u0005\u0019\u0011\r]5\u000b\u0005y1\u0011AB:uCR,8/\u0003\u0002!3\ty\u0011\t\u001d9mS\u000e\fG/[8o\u0013:4w\u000e\u0003\u0005#\u0001\t\u0005\t\u0015!\u0003\u0018\u0003\u0015IgNZ8!\u0011!!\u0003A!b\u0001\n\u0003)\u0013\u0001C1ui\u0016l\u0007\u000f^:\u0016\u0003\u0019\u00022aJ\u00183\u001d\tASF\u0004\u0002*Y5\t!F\u0003\u0002,+\u00051AH]8pizJ\u0011\u0001E\u0005\u0003]=\tq\u0001]1dW\u0006<W-\u0003\u00021c\t!A*[:u\u0015\tqs\u0002\u0005\u00024i5\t!!\u0003\u00026\u0005\t\u0011\u0012\t\u001e;f[B$\u0018J\u001c4p/J\f\u0007\u000f]3s\u0011!9\u0004A!A!\u0002\u00131\u0013!C1ui\u0016l\u0007\u000f^:!\u0011\u0015I\u0004\u0001\"\u0001;\u0003\u0019a\u0014N\\5u}Q\u00191\bP\u001f\u0011\u0005M\u0002\u0001\"\u0002\u000b9\u0001\u00049\u0002\"\u0002\u00139\u0001\u00041\u0003\"B \u0001\t\u0003\u0001\u0015AA5e+\u0005\t\u0005C\u0001\"F\u001d\tq1)\u0003\u0002E\u001f\u00051\u0001K]3eK\u001aL!AR$\u0003\rM#(/\u001b8h\u0015\t!u\u0002\u000b\u0002?\u0013*\u0012!J\u0015\t\u0003\u0017Bk\u0011\u0001\u0014\u0006\u0003\u001b:\u000bqa\u001b<ti>\u0014XM\u0003\u0002P\r\u0005!Q\u000f^5m\u0013\t\tFJA\u0004L-&sG-\u001a=,\u0003M\u0003\"\u0001V-\u000e\u0003US!AV,\u0002\t5,G/\u0019\u0006\u00031>\t!\"\u00198o_R\fG/[8o\u0013\tQVK\u0001\u0004hKR$XM\u001d\u0015\u0003}q\u0003\"!X3\u000e\u0003yS!\u0001W0\u000b\u0005\u0001\f\u0017a\u00026bG.\u001cxN\u001c\u0006\u0003E\u000e\f\u0011BZ1ti\u0016\u0014\b0\u001c7\u000b\u0003\u0011\f1aY8n\u0013\t1gL\u0001\u0006Kg>t\u0017j\u001a8pe\u0016DQ\u0001\u001b\u0001\u0005\u0002%\fq!\u001a8e)&lW\rF\u0001k!\tq1.\u0003\u0002m\u001f\t!Aj\u001c8hQ\u00119\u0017J\\8\u0002\u000bY\fG.^3\"\u0003!D#a\u001a/\t\u000bI\u0004A\u0011A5\u0002\u001b=dG-Z:u\u0003R$X-\u001c9uQ\u0011\t\u0018J\u001c;\"\u0003ID#!\u001d/\t\u000b]\u0004A\u0011\u0001=\u0002#Q|\u0017\t\u001d9mS\u000e\fG/[8o\u0013:4w\u000eF\u0001\u0018\u0001"
)
public class ApplicationInfoWrapper {
   private final ApplicationInfo info;
   private final List<AttemptInfoWrapper> attempts;

   public ApplicationInfo info() {
      return this.info;
   }

   public List<AttemptInfoWrapper> attempts() {
      return this.attempts;
   }

   @JsonIgnore
   @KVIndex
   public String id() {
      return this.info().id();
   }

   @JsonIgnore
   @KVIndex("endTime")
   public long endTime() {
      return ((AttemptInfoWrapper)this.attempts().head()).info().endTime().getTime();
   }

   @JsonIgnore
   @KVIndex("oldestAttempt")
   public long oldestAttempt() {
      return BoxesRunTime.unboxToLong(((TraversableOnce)this.attempts().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(AttemptInfoWrapper x$18) {
            return x$18.info().lastUpdated().getTime();
         }
      }, List$.MODULE$.canBuildFrom())).min(Ordering.Long$.MODULE$));
   }

   public ApplicationInfo toApplicationInfo() {
      Seq x$25 = (Seq)this.attempts().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ApplicationAttemptInfo apply(AttemptInfoWrapper x$19) {
            return x$19.info();
         }
      }, List$.MODULE$.canBuildFrom());
      String x$26 = this.info().copy$default$1();
      String x$27 = this.info().copy$default$2();
      Option x$28 = this.info().copy$default$3();
      Option x$29 = this.info().copy$default$4();
      Option x$30 = this.info().copy$default$5();
      Option x$31 = this.info().copy$default$6();
      return this.info().copy(x$26, x$27, x$28, x$29, x$30, x$31, x$25);
   }

   public ApplicationInfoWrapper(ApplicationInfo info, List<AttemptInfoWrapper> attempts) {
      this.info = info;
      this.attempts = attempts;
   }
}
