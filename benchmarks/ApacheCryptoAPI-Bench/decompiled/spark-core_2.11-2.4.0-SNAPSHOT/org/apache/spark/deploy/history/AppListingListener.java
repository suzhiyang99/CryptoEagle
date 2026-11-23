package org.apache.spark.deploy.history;

import java.util.Date;
import org.apache.hadoop.fs.FileStatus;
import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.scheduler.SparkListenerApplicationEnd;
import org.apache.spark.scheduler.SparkListenerApplicationStart;
import org.apache.spark.scheduler.SparkListenerEnvironmentUpdate;
import org.apache.spark.scheduler.SparkListenerEvent;
import org.apache.spark.scheduler.SparkListenerLogStart;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo;
import org.apache.spark.status.api.v1.ApplicationAttemptInfo$;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.status.api.v1.ApplicationInfo$;
import org.apache.spark.util.Clock;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Some;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\t=d!B\u0001\u0003\u0001\ta!AE!qa2K7\u000f^5oO2K7\u000f^3oKJT!a\u0001\u0003\u0002\u000f!L7\u000f^8ss*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\"\u0001A\u0007\u0011\u00059\tR\"A\b\u000b\u0005A1\u0011!C:dQ\u0016$W\u000f\\3s\u0013\t\u0011rBA\u0007Ta\u0006\u00148\u000eT5ti\u0016tWM\u001d\u0005\t)\u0001\u0011\t\u0011)A\u0005-\u0005\u0019An\\4\u0004\u0001A\u0011q\u0003H\u0007\u00021)\u0011\u0011DG\u0001\u0003MNT!a\u0007\u0005\u0002\r!\fGm\\8q\u0013\ti\u0002D\u0001\u0006GS2,7\u000b^1ukND\u0001b\b\u0001\u0003\u0002\u0003\u0006I\u0001I\u0001\u0006G2|7m\u001b\t\u0003C\u0011j\u0011A\t\u0006\u0003G\u0019\tA!\u001e;jY&\u0011QE\t\u0002\u0006\u00072|7m\u001b\u0005\u0006O\u0001!\t\u0001K\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007%ZC\u0006\u0005\u0002+\u00015\t!\u0001C\u0003\u0015M\u0001\u0007a\u0003C\u0003 M\u0001\u0007\u0001\u0005C\u0004/\u0001\t\u0007I\u0011B\u0018\u0002\u0007\u0005\u0004\b/F\u00011!\t\t$'D\u0001\u0001\r\u0011\u0019\u0004\u0001\u0002\u001b\u0003-5+H/\u00192mK\u0006\u0003\b\u000f\\5dCRLwN\\%oM>\u001c\"AM\u001b\u0011\u0005YJT\"A\u001c\u000b\u0003a\nQa]2bY\u0006L!AO\u001c\u0003\r\u0005s\u0017PU3g\u0011\u00159#\u0007\"\u0001=)\u0005\u0001\u0004b\u0002 3\u0001\u0004%\taP\u0001\u0003S\u0012,\u0012\u0001\u0011\t\u0003\u0003\u0012s!A\u000e\"\n\u0005\r;\u0014A\u0002)sK\u0012,g-\u0003\u0002F\r\n11\u000b\u001e:j]\u001eT!aQ\u001c\t\u000f!\u0013\u0004\u0019!C\u0001\u0013\u00061\u0011\u000eZ0%KF$\"AS'\u0011\u0005YZ\u0015B\u0001'8\u0005\u0011)f.\u001b;\t\u000f9;\u0015\u0011!a\u0001\u0001\u0006\u0019\u0001\u0010J\u0019\t\rA\u0013\u0004\u0015)\u0003A\u0003\rIG\r\t\u0005\b%J\u0002\r\u0011\"\u0001@\u0003\u0011q\u0017-\\3\t\u000fQ\u0013\u0004\u0019!C\u0001+\u0006Aa.Y7f?\u0012*\u0017\u000f\u0006\u0002K-\"9ajUA\u0001\u0002\u0004\u0001\u0005B\u0002-3A\u0003&\u0001)A\u0003oC6,\u0007\u0005C\u0004[e\u0001\u0007I\u0011A.\u0002\u0019\r|'/Z:He\u0006tG/\u001a3\u0016\u0003q\u00032AN/`\u0013\tqvG\u0001\u0004PaRLwN\u001c\t\u0003m\u0001L!!Y\u001c\u0003\u0007%sG\u000fC\u0004de\u0001\u0007I\u0011\u00013\u0002!\r|'/Z:He\u0006tG/\u001a3`I\u0015\fHC\u0001&f\u0011\u001dq%-!AA\u0002qCaa\u001a\u001a!B\u0013a\u0016!D2pe\u0016\u001cxI]1oi\u0016$\u0007\u0005C\u0004je\u0001\u0007I\u0011A.\u0002\u00115\f\u0007pQ8sKNDqa\u001b\u001aA\u0002\u0013\u0005A.\u0001\u0007nCb\u001cuN]3t?\u0012*\u0017\u000f\u0006\u0002K[\"9aJ[A\u0001\u0002\u0004a\u0006BB83A\u0003&A,A\u0005nCb\u001cuN]3tA!9\u0011O\ra\u0001\n\u0003Y\u0016\u0001E2pe\u0016\u001c\b+\u001a:Fq\u0016\u001cW\u000f^8s\u0011\u001d\u0019(\u00071A\u0005\u0002Q\fAcY8sKN\u0004VM]#yK\u000e,Ho\u001c:`I\u0015\fHC\u0001&v\u0011\u001dq%/!AA\u0002qCaa\u001e\u001a!B\u0013a\u0016!E2pe\u0016\u001c\b+\u001a:Fq\u0016\u001cW\u000f^8sA!9\u0011P\ra\u0001\n\u0003Y\u0016aE7f[>\u0014\u0018\u0010U3s\u000bb,7-\u001e;pe6\u0013\u0005bB>3\u0001\u0004%\t\u0001`\u0001\u0018[\u0016lwN]=QKJ,\u00050Z2vi>\u0014XJQ0%KF$\"AS?\t\u000f9S\u0018\u0011!a\u00019\"1qP\rQ!\nq\u000bA#\\3n_JL\b+\u001a:Fq\u0016\u001cW\u000f^8s\u001b\n\u0003\u0003bBA\u0002e\u0011\u0005\u0011QA\u0001\u0007i>4\u0016.Z<\u0015\u0005\u0005\u001d\u0001c\u0001\u0016\u0002\n%\u0019\u00111\u0002\u0002\u0003-\u0005\u0003\b\u000f\\5dCRLwN\\%oM><&/\u00199qKJDq!a\u0004\u0001A\u0003%\u0001'\u0001\u0003baB\u0004\u0003\"CA\n\u0001\t\u0007I\u0011BA\u000b\u0003\u001d\tG\u000f^3naR,\"!a\u0006\u0011\u0007E\nIB\u0002\u0004\u0002\u001c\u0001!\u0011Q\u0004\u0002\u0013\u001bV$\u0018M\u00197f\u0003R$X-\u001c9u\u0013:4wnE\u0002\u0002\u001aUB!\"!\t\u0002\u001a\t\u0005\t\u0015!\u0003A\u0003\u001dawn\u001a)bi\"D1\"!\n\u0002\u001a\t\u0005\t\u0015!\u0003\u0002(\u0005Aa-\u001b7f'&TX\rE\u00027\u0003SI1!a\u000b8\u0005\u0011auN\\4\t\u000f\u001d\nI\u0002\"\u0001\u00020Q1\u0011qCA\u0019\u0003gAq!!\t\u0002.\u0001\u0007\u0001\t\u0003\u0005\u0002&\u00055\u0002\u0019AA\u0014\u0011)\t9$!\u0007A\u0002\u0013\u0005\u0011\u0011H\u0001\nCR$X-\u001c9u\u0013\u0012,\"!a\u000f\u0011\u0007Yj\u0006\t\u0003\u0006\u0002@\u0005e\u0001\u0019!C\u0001\u0003\u0003\nQ\"\u0019;uK6\u0004H/\u00133`I\u0015\fHc\u0001&\u0002D!Ia*!\u0010\u0002\u0002\u0003\u0007\u00111\b\u0005\n\u0003\u000f\nI\u0002)Q\u0005\u0003w\t!\"\u0019;uK6\u0004H/\u00133!\u0011)\tY%!\u0007A\u0002\u0013\u0005\u0011QJ\u0001\ngR\f'\u000f\u001e+j[\u0016,\"!a\u0014\u0011\t\u0005E\u0013\u0011L\u0007\u0003\u0003'R1aIA+\u0015\t\t9&\u0001\u0003kCZ\f\u0017\u0002BA.\u0003'\u0012A\u0001R1uK\"Q\u0011qLA\r\u0001\u0004%\t!!\u0019\u0002\u001bM$\u0018M\u001d;US6,w\fJ3r)\rQ\u00151\r\u0005\n\u001d\u0006u\u0013\u0011!a\u0001\u0003\u001fB\u0011\"a\u001a\u0002\u001a\u0001\u0006K!a\u0014\u0002\u0015M$\u0018M\u001d;US6,\u0007\u0005\u0003\u0006\u0002l\u0005e\u0001\u0019!C\u0001\u0003\u001b\nq!\u001a8e)&lW\r\u0003\u0006\u0002p\u0005e\u0001\u0019!C\u0001\u0003c\n1\"\u001a8e)&lWm\u0018\u0013fcR\u0019!*a\u001d\t\u00139\u000bi'!AA\u0002\u0005=\u0003\"CA<\u00033\u0001\u000b\u0015BA(\u0003!)g\u000e\u001a+j[\u0016\u0004\u0003BCA>\u00033\u0001\r\u0011\"\u0001\u0002N\u0005YA.Y:u+B$\u0017\r^3e\u0011)\ty(!\u0007A\u0002\u0013\u0005\u0011\u0011Q\u0001\u0010Y\u0006\u001cH/\u00169eCR,Gm\u0018\u0013fcR\u0019!*a!\t\u00139\u000bi(!AA\u0002\u0005=\u0003\"CAD\u00033\u0001\u000b\u0015BA(\u00031a\u0017m\u001d;Va\u0012\fG/\u001a3!\u0011)\tY)!\u0007A\u0002\u0013\u0005\u0011QR\u0001\tIV\u0014\u0018\r^5p]V\u0011\u0011q\u0005\u0005\u000b\u0003#\u000bI\u00021A\u0005\u0002\u0005M\u0015\u0001\u00043ve\u0006$\u0018n\u001c8`I\u0015\fHc\u0001&\u0002\u0016\"Ia*a$\u0002\u0002\u0003\u0007\u0011q\u0005\u0005\n\u00033\u000bI\u0002)Q\u0005\u0003O\t\u0011\u0002Z;sCRLwN\u001c\u0011\t\u0013\u0005u\u0015\u0011\u0004a\u0001\n\u0003y\u0014!C:qCJ\\Wk]3s\u0011)\t\t+!\u0007A\u0002\u0013\u0005\u00111U\u0001\u000egB\f'o[+tKJ|F%Z9\u0015\u0007)\u000b)\u000b\u0003\u0005O\u0003?\u000b\t\u00111\u0001A\u0011!\tI+!\u0007!B\u0013\u0001\u0015AC:qCJ\\Wk]3sA!Q\u0011QVA\r\u0001\u0004%\t!a,\u0002\u0013\r|W\u000e\u001d7fi\u0016$WCAAY!\r1\u00141W\u0005\u0004\u0003k;$a\u0002\"p_2,\u0017M\u001c\u0005\u000b\u0003s\u000bI\u00021A\u0005\u0002\u0005m\u0016!D2p[BdW\r^3e?\u0012*\u0017\u000fF\u0002K\u0003{C\u0011BTA\\\u0003\u0003\u0005\r!!-\t\u0013\u0005\u0005\u0017\u0011\u0004Q!\n\u0005E\u0016AC2p[BdW\r^3eA!Q\u0011QYA\r\u0001\u0004%\t!a2\u0002\u001f\u0005\u0004\bo\u00159be.4VM]:j_:,\"!!3\u0011\t\u0005-\u0017\u0011[\u0007\u0003\u0003\u001bTA!a4\u0002V\u0005!A.\u00198h\u0013\r)\u0015Q\u001a\u0005\u000b\u0003+\fI\u00021A\u0005\u0002\u0005]\u0017aE1qaN\u0003\u0018M]6WKJ\u001c\u0018n\u001c8`I\u0015\fHc\u0001&\u0002Z\"Ia*a5\u0002\u0002\u0003\u0007\u0011\u0011\u001a\u0005\n\u0003;\fI\u0002)Q\u0005\u0003\u0013\f\u0001#\u00199q'B\f'o\u001b,feNLwN\u001c\u0011\t\u0015\u0005\u0005\u0018\u0011\u0004a\u0001\n\u0003\tI$A\u0005bI6Lg.Q2mg\"Q\u0011Q]A\r\u0001\u0004%\t!a:\u0002\u001b\u0005$W.\u001b8BG2\u001cx\fJ3r)\rQ\u0015\u0011\u001e\u0005\n\u001d\u0006\r\u0018\u0011!a\u0001\u0003wA\u0011\"!<\u0002\u001a\u0001\u0006K!a\u000f\u0002\u0015\u0005$W.\u001b8BG2\u001c\b\u0005\u0003\u0006\u0002r\u0006e\u0001\u0019!C\u0001\u0003s\t\u0001B^5fo\u0006\u001bGn\u001d\u0005\u000b\u0003k\fI\u00021A\u0005\u0002\u0005]\u0018\u0001\u0004<jK^\f5\r\\:`I\u0015\fHc\u0001&\u0002z\"Ia*a=\u0002\u0002\u0003\u0007\u00111\b\u0005\n\u0003{\fI\u0002)Q\u0005\u0003w\t\u0011B^5fo\u0006\u001bGn\u001d\u0011\t\u0015\t\u0005\u0011\u0011\u0004a\u0001\n\u0003\tI$A\bbI6Lg.Q2mg\u001e\u0013x.\u001e9t\u0011)\u0011)!!\u0007A\u0002\u0013\u0005!qA\u0001\u0014C\u0012l\u0017N\\!dYN<%o\\;qg~#S-\u001d\u000b\u0004\u0015\n%\u0001\"\u0003(\u0003\u0004\u0005\u0005\t\u0019AA\u001e\u0011%\u0011i!!\u0007!B\u0013\tY$\u0001\tbI6Lg.Q2mg\u001e\u0013x.\u001e9tA!Q!\u0011CA\r\u0001\u0004%\t!!\u000f\u0002\u001dYLWm^!dYN<%o\\;qg\"Q!QCA\r\u0001\u0004%\tAa\u0006\u0002%YLWm^!dYN<%o\\;qg~#S-\u001d\u000b\u0004\u0015\ne\u0001\"\u0003(\u0003\u0014\u0005\u0005\t\u0019AA\u001e\u0011%\u0011i\"!\u0007!B\u0013\tY$A\bwS\u0016<\u0018i\u00197t\u000fJ|W\u000f]:!\u0011!\t\u0019!!\u0007\u0005\u0002\t\u0005BC\u0001B\u0012!\rQ#QE\u0005\u0004\u0005O\u0011!AE!ui\u0016l\u0007\u000f^%oM><&/\u00199qKJD\u0001Ba\u000b\u0001A\u0003%\u0011qC\u0001\tCR$X-\u001c9uA!9!q\u0006\u0001\u0005B\tE\u0012AE8o\u0003B\u0004H.[2bi&|gn\u0015;beR$2A\u0013B\u001a\u0011!\u0011)D!\fA\u0002\t]\u0012!B3wK:$\bc\u0001\b\u0003:%\u0019!1H\b\u0003;M\u0003\u0018M]6MSN$XM\\3s\u0003B\u0004H.[2bi&|gn\u0015;beRDqAa\u0010\u0001\t\u0003\u0012\t%\u0001\tp]\u0006\u0003\b\u000f\\5dCRLwN\\#oIR\u0019!Ja\u0011\t\u0011\tU\"Q\ba\u0001\u0005\u000b\u00022A\u0004B$\u0013\r\u0011Ie\u0004\u0002\u001c'B\f'o\u001b'jgR,g.\u001a:BaBd\u0017nY1uS>tWI\u001c3\t\u000f\t5\u0003\u0001\"\u0011\u0003P\u0005\u0019rN\\#om&\u0014xN\\7f]R,\u0006\u000fZ1uKR\u0019!J!\u0015\t\u0011\tU\"1\na\u0001\u0005'\u00022A\u0004B+\u0013\r\u00119f\u0004\u0002\u001f'B\f'o\u001b'jgR,g.\u001a:F]ZL'o\u001c8nK:$X\u000b\u001d3bi\u0016DqAa\u0017\u0001\t\u0003\u0012i&\u0001\u0007p]>#\b.\u001a:Fm\u0016tG\u000fF\u0002K\u0005?B\u0001B!\u000e\u0003Z\u0001\u0007!\u0011\r\t\u0004\u001d\t\r\u0014b\u0001B3\u001f\t\u00112\u000b]1sW2K7\u000f^3oKJ,e/\u001a8u\u0011\u001d\u0011I\u0007\u0001C\u0001\u0005W\nq\"\u00199qY&\u001c\u0017\r^5p]&sgm\\\u000b\u0003\u0005[\u0002BAN/\u0002\b\u0001"
)
public class AppListingListener extends SparkListener {
   private final FileStatus log;
   private final Clock clock;
   private final MutableApplicationInfo app;
   private final MutableAttemptInfo org$apache$spark$deploy$history$AppListingListener$$attempt;

   private MutableApplicationInfo app() {
      return this.app;
   }

   public MutableAttemptInfo org$apache$spark$deploy$history$AppListingListener$$attempt() {
      return this.org$apache$spark$deploy$history$AppListingListener$$attempt;
   }

   public void onApplicationStart(SparkListenerApplicationStart event) {
      this.app().id_$eq((String)event.appId().orNull(Predef$.MODULE$.$conforms()));
      this.app().name_$eq(event.appName());
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().attemptId_$eq(event.appAttemptId());
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().startTime_$eq(new Date(event.time()));
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().lastUpdated_$eq(new Date(this.clock.getTimeMillis()));
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().sparkUser_$eq(event.sparkUser());
   }

   public void onApplicationEnd(SparkListenerApplicationEnd event) {
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().endTime_$eq(new Date(event.time()));
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().lastUpdated_$eq(new Date(this.log.getModificationTime()));
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().duration_$eq(event.time() - this.org$apache$spark$deploy$history$AppListingListener$$attempt().startTime().getTime());
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().completed_$eq(true);
   }

   public void onEnvironmentUpdate(SparkListenerEnvironmentUpdate event) {
      Map allProperties = ((TraversableOnce)event.environmentDetails().apply("Spark Properties")).toMap(Predef$.MODULE$.$conforms());
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().viewAcls_$eq(allProperties.get("spark.ui.view.acls"));
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().adminAcls_$eq(allProperties.get("spark.admin.acls"));
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().viewAclsGroups_$eq(allProperties.get("spark.ui.view.acls.groups"));
      this.org$apache$spark$deploy$history$AppListingListener$$attempt().adminAclsGroups_$eq(allProperties.get("spark.admin.acls.groups"));
   }

   public void onOtherEvent(SparkListenerEvent event) {
      if (event instanceof SparkListenerLogStart) {
         SparkListenerLogStart var3 = (SparkListenerLogStart)event;
         String sparkVersion = var3.sparkVersion();
         this.org$apache$spark$deploy$history$AppListingListener$$attempt().appSparkVersion_$eq(sparkVersion);
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else {
         BoxedUnit var6 = BoxedUnit.UNIT;
      }

   }

   public Option<ApplicationInfoWrapper> applicationInfo() {
      return (Option<ApplicationInfoWrapper>)(this.app().id() == null ? None$.MODULE$ : new Some(this.app().toView()));
   }

   public AppListingListener(FileStatus log, Clock clock) {
      this.log = log;
      this.clock = clock;
      this.app = new MutableApplicationInfo();
      this.org$apache$spark$deploy$history$AppListingListener$$attempt = new MutableAttemptInfo(log.getPath().getName(), log.getLen());
   }

   public class MutableApplicationInfo {
      private String id;
      private String name;
      private Option<Object> coresGranted;
      private Option<Object> maxCores;
      private Option<Object> coresPerExecutor;
      private Option<Object> memoryPerExecutorMB;
      // $FF: synthetic field
      public final AppListingListener $outer;

      public String id() {
         return this.id;
      }

      public void id_$eq(String x$1) {
         this.id = x$1;
      }

      public String name() {
         return this.name;
      }

      public void name_$eq(String x$1) {
         this.name = x$1;
      }

      public Option<Object> coresGranted() {
         return this.coresGranted;
      }

      public void coresGranted_$eq(Option<Object> x$1) {
         this.coresGranted = x$1;
      }

      public Option<Object> maxCores() {
         return this.maxCores;
      }

      public void maxCores_$eq(Option<Object> x$1) {
         this.maxCores = x$1;
      }

      public Option<Object> coresPerExecutor() {
         return this.coresPerExecutor;
      }

      public void coresPerExecutor_$eq(Option<Object> x$1) {
         this.coresPerExecutor = x$1;
      }

      public Option<Object> memoryPerExecutorMB() {
         return this.memoryPerExecutorMB;
      }

      public void memoryPerExecutorMB_$eq(Option<Object> x$1) {
         this.memoryPerExecutorMB = x$1;
      }

      public ApplicationInfoWrapper toView() {
         ApplicationInfo apiInfo = ApplicationInfo$.MODULE$.apply((String)this.id(), (String)this.name(), this.coresGranted(), this.maxCores(), this.coresPerExecutor(), this.memoryPerExecutorMB(), Nil$.MODULE$);
         return new ApplicationInfoWrapper(apiInfo, List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new AttemptInfoWrapper[]{this.org$apache$spark$deploy$history$AppListingListener$MutableApplicationInfo$$$outer().org$apache$spark$deploy$history$AppListingListener$$attempt().toView()})));
      }

      // $FF: synthetic method
      public AppListingListener org$apache$spark$deploy$history$AppListingListener$MutableApplicationInfo$$$outer() {
         return this.$outer;
      }

      public MutableApplicationInfo() {
         if (AppListingListener.this == null) {
            throw null;
         } else {
            this.$outer = AppListingListener.this;
            super();
            this.id = null;
            this.name = null;
            this.coresGranted = None$.MODULE$;
            this.maxCores = None$.MODULE$;
            this.coresPerExecutor = None$.MODULE$;
            this.memoryPerExecutorMB = None$.MODULE$;
         }
      }
   }

   public class MutableAttemptInfo {
      private final String logPath;
      private final long fileSize;
      private Option<String> attemptId;
      private Date startTime;
      private Date endTime;
      private Date lastUpdated;
      private long duration;
      private String sparkUser;
      private boolean completed;
      private String appSparkVersion;
      private Option<String> adminAcls;
      private Option<String> viewAcls;
      private Option<String> adminAclsGroups;
      private Option<String> viewAclsGroups;
      // $FF: synthetic field
      public final AppListingListener $outer;

      public Option<String> attemptId() {
         return this.attemptId;
      }

      public void attemptId_$eq(Option<String> x$1) {
         this.attemptId = x$1;
      }

      public Date startTime() {
         return this.startTime;
      }

      public void startTime_$eq(Date x$1) {
         this.startTime = x$1;
      }

      public Date endTime() {
         return this.endTime;
      }

      public void endTime_$eq(Date x$1) {
         this.endTime = x$1;
      }

      public Date lastUpdated() {
         return this.lastUpdated;
      }

      public void lastUpdated_$eq(Date x$1) {
         this.lastUpdated = x$1;
      }

      public long duration() {
         return this.duration;
      }

      public void duration_$eq(long x$1) {
         this.duration = x$1;
      }

      public String sparkUser() {
         return this.sparkUser;
      }

      public void sparkUser_$eq(String x$1) {
         this.sparkUser = x$1;
      }

      public boolean completed() {
         return this.completed;
      }

      public void completed_$eq(boolean x$1) {
         this.completed = x$1;
      }

      public String appSparkVersion() {
         return this.appSparkVersion;
      }

      public void appSparkVersion_$eq(String x$1) {
         this.appSparkVersion = x$1;
      }

      public Option<String> adminAcls() {
         return this.adminAcls;
      }

      public void adminAcls_$eq(Option<String> x$1) {
         this.adminAcls = x$1;
      }

      public Option<String> viewAcls() {
         return this.viewAcls;
      }

      public void viewAcls_$eq(Option<String> x$1) {
         this.viewAcls = x$1;
      }

      public Option<String> adminAclsGroups() {
         return this.adminAclsGroups;
      }

      public void adminAclsGroups_$eq(Option<String> x$1) {
         this.adminAclsGroups = x$1;
      }

      public Option<String> viewAclsGroups() {
         return this.viewAclsGroups;
      }

      public void viewAclsGroups_$eq(Option<String> x$1) {
         this.viewAclsGroups = x$1;
      }

      public AttemptInfoWrapper toView() {
         ApplicationAttemptInfo apiInfo = ApplicationAttemptInfo$.MODULE$.apply(this.attemptId(), this.startTime(), this.endTime(), this.lastUpdated(), this.duration(), this.sparkUser(), this.completed(), this.appSparkVersion());
         return new AttemptInfoWrapper(apiInfo, this.logPath, this.fileSize, this.adminAcls(), this.viewAcls(), this.adminAclsGroups(), this.viewAclsGroups());
      }

      // $FF: synthetic method
      public AppListingListener org$apache$spark$deploy$history$AppListingListener$MutableAttemptInfo$$$outer() {
         return this.$outer;
      }

      public MutableAttemptInfo(String logPath, long fileSize) {
         this.logPath = logPath;
         this.fileSize = fileSize;
         if (AppListingListener.this == null) {
            throw null;
         } else {
            this.$outer = AppListingListener.this;
            super();
            this.attemptId = None$.MODULE$;
            this.startTime = new Date(-1L);
            this.endTime = new Date(-1L);
            this.lastUpdated = new Date(-1L);
            this.duration = 0L;
            this.sparkUser = null;
            this.completed = false;
            this.appSparkVersion = "";
            this.adminAcls = None$.MODULE$;
            this.viewAcls = None$.MODULE$;
            this.adminAclsGroups = None$.MODULE$;
            this.viewAclsGroups = None$.MODULE$;
         }
      }
   }
}
