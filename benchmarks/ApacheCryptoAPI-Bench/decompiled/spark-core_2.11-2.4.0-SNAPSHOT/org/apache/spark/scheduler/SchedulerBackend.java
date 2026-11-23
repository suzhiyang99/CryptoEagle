package org.apache.spark.scheduler;

import scala.Option;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001m3\u0001\"\u0001\u0002\u0011\u0002\u0007\u0005AA\u0003\u0002\u0011'\u000eDW\rZ;mKJ\u0014\u0015mY6f]\u0012T!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\rC\u0003\u0013\u0001\u0011\u0005A#\u0001\u0004%S:LG\u000fJ\u0002\u0001)\u0005)\u0002C\u0001\u0007\u0017\u0013\t9RB\u0001\u0003V]&$\bbB\r\u0001\u0005\u0004%IAG\u0001\u0006CB\u0004\u0018\nZ\u000b\u00027A\u0011A$I\u0007\u0002;)\u0011adH\u0001\u0005Y\u0006twMC\u0001!\u0003\u0011Q\u0017M^1\n\u0005\tj\"AB*ue&tw\r\u0003\u0004%\u0001\u0001\u0006IaG\u0001\u0007CB\u0004\u0018\n\u001a\u0011\t\u000b\u0019\u0002a\u0011\u0001\u000b\u0002\u000bM$\u0018M\u001d;\t\u000b!\u0002a\u0011\u0001\u000b\u0002\tM$x\u000e\u001d\u0005\u0006U\u00011\t\u0001F\u0001\re\u00164\u0018N^3PM\u001a,'o\u001d\u0005\u0006Y\u00011\t!L\u0001\u0013I\u00164\u0017-\u001e7u!\u0006\u0014\u0018\r\u001c7fY&\u001cX\u000eF\u0001/!\taq&\u0003\u00021\u001b\t\u0019\u0011J\u001c;\t\u000bI\u0002A\u0011A\u001a\u0002\u0011-LG\u000e\u001c+bg.$R!\u0006\u001b:\u0003\u001aCQ!N\u0019A\u0002Y\na\u0001^1tW&#\u0007C\u0001\u00078\u0013\tATB\u0001\u0003M_:<\u0007\"\u0002\u001e2\u0001\u0004Y\u0014AC3yK\u000e,Ho\u001c:JIB\u0011Ah\u0010\b\u0003\u0019uJ!AP\u0007\u0002\rA\u0013X\rZ3g\u0013\t\u0011\u0003I\u0003\u0002?\u001b!)!)\ra\u0001\u0007\u0006y\u0011N\u001c;feJ,\b\u000f\u001e+ie\u0016\fG\r\u0005\u0002\r\t&\u0011Q)\u0004\u0002\b\u0005>|G.Z1o\u0011\u00159\u0015\u00071\u0001<\u0003\u0019\u0011X-Y:p]\")\u0011\n\u0001C\u0001\u0015\u00069\u0011n\u001d*fC\u0012LH#A\"\t\u000b1\u0003A\u0011A'\u0002\u001b\u0005\u0004\b\u000f\\5dCRLwN\\%e)\u0005Y\u0004\"B(\u0001\t\u0003\u0001\u0016\u0001F1qa2L7-\u0019;j_:\fE\u000f^3naRLE\rF\u0001R!\ra!kO\u0005\u0003'6\u0011aa\u00149uS>t\u0007\"B+\u0001\t\u00031\u0016\u0001E4fi\u0012\u0013\u0018N^3s\u0019><WK\u001d7t+\u00059\u0006c\u0001\u0007S1B!A(W\u001e<\u0013\tQ\u0006IA\u0002NCB\u0004"
)
public interface SchedulerBackend {
   void org$apache$spark$scheduler$SchedulerBackend$_setter_$org$apache$spark$scheduler$SchedulerBackend$$appId_$eq(String var1);

   String org$apache$spark$scheduler$SchedulerBackend$$appId();

   void start();

   void stop();

   void reviveOffers();

   int defaultParallelism();

   void killTask(long var1, String var3, boolean var4, String var5);

   boolean isReady();

   String applicationId();

   Option<String> applicationAttemptId();

   Option<Map<String, String>> getDriverLogUrls();
}
