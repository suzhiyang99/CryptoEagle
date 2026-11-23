package org.apache.spark.scheduler;

import java.util.Properties;
import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple4;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ef\u0001B\u0001\u0003\u0001.\u0011Qc\u00159be.d\u0015n\u001d;f]\u0016\u0014(j\u001c2Ti\u0006\u0014HO\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011#\u0002\u0001\r%YI\u0002CA\u0007\u0011\u001b\u0005q!\"A\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Eq!AB!osJ+g\r\u0005\u0002\u0014)5\t!!\u0003\u0002\u0016\u0005\t\u00112\u000b]1sW2K7\u000f^3oKJ,e/\u001a8u!\tiq#\u0003\u0002\u0019\u001d\t9\u0001K]8ek\u000e$\bCA\u0007\u001b\u0013\tYbB\u0001\u0007TKJL\u0017\r\\5{C\ndW\r\u0003\u0005\u001e\u0001\tU\r\u0011\"\u0001\u001f\u0003\u0015QwNY%e+\u0005y\u0002CA\u0007!\u0013\t\tcBA\u0002J]RD\u0001b\t\u0001\u0003\u0012\u0003\u0006IaH\u0001\u0007U>\u0014\u0017\n\u001a\u0011\t\u0011\u0015\u0002!Q3A\u0005\u0002\u0019\nA\u0001^5nKV\tq\u0005\u0005\u0002\u000eQ%\u0011\u0011F\u0004\u0002\u0005\u0019>tw\r\u0003\u0005,\u0001\tE\t\u0015!\u0003(\u0003\u0015!\u0018.\\3!\u0011!i\u0003A!f\u0001\n\u0003q\u0013AC:uC\u001e,\u0017J\u001c4pgV\tq\u0006E\u00021qmr!!\r\u001c\u000f\u0005I*T\"A\u001a\u000b\u0005QR\u0011A\u0002\u001fs_>$h(C\u0001\u0010\u0013\t9d\"A\u0004qC\u000e\\\u0017mZ3\n\u0005eR$aA*fc*\u0011qG\u0004\t\u0003'qJ!!\u0010\u0002\u0003\u0013M#\u0018mZ3J]\u001a|\u0007\u0002C \u0001\u0005#\u0005\u000b\u0011B\u0018\u0002\u0017M$\u0018mZ3J]\u001a|7\u000f\t\u0005\t\u0003\u0002\u0011)\u001a!C\u0001\u0005\u0006Q\u0001O]8qKJ$\u0018.Z:\u0016\u0003\r\u0003\"\u0001R%\u000e\u0003\u0015S!AR$\u0002\tU$\u0018\u000e\u001c\u0006\u0002\u0011\u0006!!.\u0019<b\u0013\tQUI\u0001\u0006Qe>\u0004XM\u001d;jKND\u0001\u0002\u0014\u0001\u0003\u0012\u0003\u0006IaQ\u0001\faJ|\u0007/\u001a:uS\u0016\u001c\b\u0005C\u0003O\u0001\u0011\u0005q*\u0001\u0004=S:LGO\u0010\u000b\u0006!F\u00136\u000b\u0016\t\u0003'\u0001AQ!H'A\u0002}AQ!J'A\u0002\u001dBQ!L'A\u0002=Bq!Q'\u0011\u0002\u0003\u00071\tC\u0004W\u0001\t\u0007I\u0011A,\u0002\u0011M$\u0018mZ3JIN,\u0012\u0001\u0017\t\u0004aaz\u0002B\u0002.\u0001A\u0003%\u0001,A\u0005ti\u0006<W-\u00133tA!9A\fAA\u0001\n\u0003i\u0016\u0001B2paf$R\u0001\u00150`A\u0006Dq!H.\u0011\u0002\u0003\u0007q\u0004C\u0004&7B\u0005\t\u0019A\u0014\t\u000f5Z\u0006\u0013!a\u0001_!9\u0011i\u0017I\u0001\u0002\u0004\u0019\u0005bB2\u0001#\u0003%\t\u0001Z\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005)'FA\u0010gW\u00059\u0007C\u00015n\u001b\u0005I'B\u00016l\u0003%)hn\u00195fG.,GM\u0003\u0002m\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u00059L'!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9\u0001\u000fAI\u0001\n\u0003\t\u0018AD2paf$C-\u001a4bk2$HEM\u000b\u0002e*\u0012qE\u001a\u0005\bi\u0002\t\n\u0011\"\u0001v\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIM*\u0012A\u001e\u0016\u0003_\u0019Dq\u0001\u001f\u0001\u0012\u0002\u0013\u0005\u00110\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001b\u0016\u0003iT#a\u00114\t\u000fq\u0004\u0011\u0011!C!{\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012A \t\u0004\u007f\u0006\u0015QBAA\u0001\u0015\r\t\u0019aR\u0001\u0005Y\u0006tw-\u0003\u0003\u0002\b\u0005\u0005!AB*ue&tw\r\u0003\u0005\u0002\f\u0001\t\t\u0011\"\u0001\u001f\u00031\u0001(o\u001c3vGR\f%/\u001b;z\u0011%\ty\u0001AA\u0001\n\u0003\t\t\"\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\t\u0005M\u0011\u0011\u0004\t\u0004\u001b\u0005U\u0011bAA\f\u001d\t\u0019\u0011I\\=\t\u0013\u0005m\u0011QBA\u0001\u0002\u0004y\u0012a\u0001=%c!I\u0011q\u0004\u0001\u0002\u0002\u0013\u0005\u0013\u0011E\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u00111\u0005\t\u0007\u0003K\tY#a\u0005\u000e\u0005\u0005\u001d\"bAA\u0015\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\t\u00055\u0012q\u0005\u0002\t\u0013R,'/\u0019;pe\"I\u0011\u0011\u0007\u0001\u0002\u0002\u0013\u0005\u00111G\u0001\tG\u0006tW)];bYR!\u0011QGA\u001e!\ri\u0011qG\u0005\u0004\u0003sq!a\u0002\"p_2,\u0017M\u001c\u0005\u000b\u00037\ty#!AA\u0002\u0005M\u0001\"CA \u0001\u0005\u0005I\u0011IA!\u0003!A\u0017m\u001d5D_\u0012,G#A\u0010\t\u0013\u0005\u0015\u0003!!A\u0005B\u0005\u001d\u0013\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003yD\u0011\"a\u0013\u0001\u0003\u0003%\t%!\u0014\u0002\r\u0015\fX/\u00197t)\u0011\t)$a\u0014\t\u0015\u0005m\u0011\u0011JA\u0001\u0002\u0004\t\u0019\u0002K\u0002\u0001\u0003'\u0002B!!\u0016\u0002Z5\u0011\u0011q\u000b\u0006\u0003Y\u0012IA!a\u0017\u0002X\taA)\u001a<fY>\u0004XM]!qS\u001eI\u0011q\f\u0002\u0002\u0002#\u0005\u0011\u0011M\u0001\u0016'B\f'o\u001b'jgR,g.\u001a:K_\n\u001cF/\u0019:u!\r\u0019\u00121\r\u0004\t\u0003\t\t\t\u0011#\u0001\u0002fM)\u00111MA43AI\u0011\u0011NA8?\u001dz3\tU\u0007\u0003\u0003WR1!!\u001c\u000f\u0003\u001d\u0011XO\u001c;j[\u0016LA!!\u001d\u0002l\t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\u001c\u001b\t\u000f9\u000b\u0019\u0007\"\u0001\u0002vQ\u0011\u0011\u0011\r\u0005\u000b\u0003\u000b\n\u0019'!A\u0005F\u0005\u001d\u0003BCA>\u0003G\n\t\u0011\"!\u0002~\u0005)\u0011\r\u001d9msRI\u0001+a \u0002\u0002\u0006\r\u0015Q\u0011\u0005\u0007;\u0005e\u0004\u0019A\u0010\t\r\u0015\nI\b1\u0001(\u0011\u0019i\u0013\u0011\u0010a\u0001_!A\u0011)!\u001f\u0011\u0002\u0003\u00071\t\u0003\u0006\u0002\n\u0006\r\u0014\u0011!CA\u0003\u0017\u000bq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002\u000e\u0006e\u0005#B\u0007\u0002\u0010\u0006M\u0015bAAI\u001d\t1q\n\u001d;j_:\u0004r!DAK?\u001dz3)C\u0002\u0002\u0018:\u0011a\u0001V;qY\u0016$\u0004\"CAN\u0003\u000f\u000b\t\u00111\u0001Q\u0003\rAH\u0005\r\u0005\n\u0003?\u000b\u0019'%A\u0005\u0002e\fq\"\u00199qYf$C-\u001a4bk2$H\u0005\u000e\u0005\n\u0003G\u000b\u0019'%A\u0005\u0002e\f1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\"\u0004BCAT\u0003G\n\t\u0011\"\u0003\u0002*\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\tY\u000bE\u0002\u0000\u0003[KA!a,\u0002\u0002\t1qJ\u00196fGR\u0004"
)
public class SparkListenerJobStart implements SparkListenerEvent, Product, Serializable {
   private final int jobId;
   private final long time;
   private final Seq<StageInfo> stageInfos;
   private final Properties properties;
   private final Seq<Object> stageIds;

   public static Properties $lessinit$greater$default$4() {
      return SparkListenerJobStart$.MODULE$.$lessinit$greater$default$4();
   }

   public static Properties apply$default$4() {
      return SparkListenerJobStart$.MODULE$.apply$default$4();
   }

   public static Option<Tuple4<Object, Object, Seq<StageInfo>, Properties>> unapply(SparkListenerJobStart var0) {
      return SparkListenerJobStart$.MODULE$.unapply(var0);
   }

   public static SparkListenerJobStart apply(int var0, long var1, Seq<StageInfo> var3, Properties var4) {
      return SparkListenerJobStart$.MODULE$.apply(var0, var1, var3, var4);
   }

   public static Function1<Tuple4<Object, Object, Seq<StageInfo>, Properties>, SparkListenerJobStart> tupled() {
      return SparkListenerJobStart$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Object, Function1<Seq<StageInfo>, Function1<Properties, SparkListenerJobStart>>>> curried() {
      return SparkListenerJobStart$.MODULE$.curried();
   }

   public boolean logEvent() {
      return SparkListenerEvent$class.logEvent(this);
   }

   public int jobId() {
      return this.jobId;
   }

   public long time() {
      return this.time;
   }

   public Seq<StageInfo> stageInfos() {
      return this.stageInfos;
   }

   public Properties properties() {
      return this.properties;
   }

   public Seq<Object> stageIds() {
      return this.stageIds;
   }

   public SparkListenerJobStart copy(int jobId, long time, Seq<StageInfo> stageInfos, Properties properties) {
      return new SparkListenerJobStart(jobId, time, stageInfos, properties);
   }

   public int copy$default$1() {
      return this.jobId();
   }

   public long copy$default$2() {
      return this.time();
   }

   public Seq<StageInfo> copy$default$3() {
      return this.stageInfos();
   }

   public Properties copy$default$4() {
      return this.properties();
   }

   public String productPrefix() {
      return "SparkListenerJobStart";
   }

   public int productArity() {
      return 4;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.jobId());
            break;
         case 1:
            var10000 = BoxesRunTime.boxToLong(this.time());
            break;
         case 2:
            var10000 = this.stageInfos();
            break;
         case 3:
            var10000 = this.properties();
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SparkListenerJobStart;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.jobId());
      var1 = Statics.mix(var1, Statics.longHash(this.time()));
      var1 = Statics.mix(var1, Statics.anyHash(this.stageInfos()));
      var1 = Statics.mix(var1, Statics.anyHash(this.properties()));
      return Statics.finalizeHash(var1, 4);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label66: {
            boolean var3;
            if (x$1 instanceof SparkListenerJobStart) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label48: {
                  SparkListenerJobStart var4 = (SparkListenerJobStart)x$1;
                  if (this.jobId() == var4.jobId() && this.time() == var4.time()) {
                     label57: {
                        Seq var10000 = this.stageInfos();
                        Seq var5 = var4.stageInfos();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label57;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label57;
                        }

                        Properties var7 = this.properties();
                        Properties var6 = var4.properties();
                        if (var7 == null) {
                           if (var6 != null) {
                              break label57;
                           }
                        } else if (!var7.equals(var6)) {
                           break label57;
                        }

                        if (var4.canEqual(this)) {
                           var9 = true;
                           break label48;
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label66;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public SparkListenerJobStart(int jobId, long time, Seq<StageInfo> stageInfos, Properties properties) {
      this.jobId = jobId;
      this.time = time;
      this.stageInfos = stageInfos;
      this.properties = properties;
      SparkListenerEvent$class.$init$(this);
      Product$class.$init$(this);
      this.stageIds = (Seq)stageInfos.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(StageInfo x$1) {
            return x$1.stageId();
         }
      }, Seq$.MODULE$.canBuildFrom());
   }
}
