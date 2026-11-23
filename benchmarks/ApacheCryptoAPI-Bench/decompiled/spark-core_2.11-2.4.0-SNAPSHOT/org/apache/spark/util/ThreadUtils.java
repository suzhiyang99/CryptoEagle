package org.apache.spark.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.spark.SparkException;
import scala.Function0;
import scala.concurrent.Awaitable;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;
import scala.concurrent.forkjoin.ForkJoinPool;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%uAB\u0001\u0003\u0011\u0003!!\"A\u0006UQJ,\u0017\rZ+uS2\u001c(BA\u0002\u0005\u0003\u0011)H/\u001b7\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u0004\"a\u0003\u0007\u000e\u0003\t1a!\u0004\u0002\t\u0002\u0011q!a\u0003+ie\u0016\fG-\u0016;jYN\u001c\"\u0001D\b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g\u0011\u00151B\u0002\"\u0001\u0019\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0006\t\u000fia!\u0019!C\u00057\u0005Q2/Y7f)\"\u0014X-\u00193Fq\u0016\u001cW\u000f^5p]\u000e{g\u000e^3yiV\tA\u0004\u0005\u0002\u001eA5\taD\u0003\u0002 #\u0005Q1m\u001c8dkJ\u0014XM\u001c;\n\u0005\u0005r\"aH#yK\u000e,H/[8o\u0007>tG/\u001a=u\u000bb,7-\u001e;peN+'O^5dK\"11\u0005\u0004Q\u0001\nq\t1d]1nKRC'/Z1e\u000bb,7-\u001e;j_:\u001cuN\u001c;fqR\u0004\u0003\"B\u0013\r\t\u00031\u0013AC:b[\u0016$\u0006N]3bIV\tq\u0005\u0005\u0002\u001eQ%\u0011\u0011F\b\u0002\u0019\u000bb,7-\u001e;j_:\u001cuN\u001c;fqR,\u00050Z2vi>\u0014\b\"B\u0016\r\t\u0003a\u0013A\u00058b[\u0016$G\u000b\u001b:fC\u00124\u0015m\u0019;pef$\"!L\u001b\u0011\u00059\u001aT\"A\u0018\u000b\u0005}\u0001$BA\u00022\u0015\u0005\u0011\u0014\u0001\u00026bm\u0006L!\u0001N\u0018\u0003\u001bQC'/Z1e\r\u0006\u001cGo\u001c:z\u0011\u00151$\u00061\u00018\u0003\u0019\u0001(/\u001a4jqB\u0011\u0001h\u000f\b\u0003!eJ!AO\t\u0002\rA\u0013X\rZ3g\u0013\taTH\u0001\u0004TiJLgn\u001a\u0006\u0003uEAQa\u0010\u0007\u0005\u0002\u0001\u000b\u0011D\\3x\t\u0006,Wn\u001c8DC\u000eDW\r\u001a+ie\u0016\fG\rU8pYR\u0011\u0011\t\u0012\t\u0003]\tK!aQ\u0018\u0003%QC'/Z1e!>|G.\u0012=fGV$xN\u001d\u0005\u0006my\u0002\ra\u000e\u0005\u0006\u007f1!\tA\u0012\u000b\u0005\u0003\u001eCU\nC\u00037\u000b\u0002\u0007q\u0007C\u0003J\u000b\u0002\u0007!*A\bnCb$\u0006N]3bI:+XNY3s!\t\u00012*\u0003\u0002M#\t\u0019\u0011J\u001c;\t\u000f9+\u0005\u0013!a\u0001\u0015\u0006\u00012.Z3q\u00032Lg/Z*fG>tGm\u001d\u0005\u0006!2!\t!U\u0001\u0019]\u0016<H)Y3n_:4\u0015\u000e_3e)\"\u0014X-\u00193Q_>dGcA!S)\")1k\u0014a\u0001\u0015\u0006Aa\u000e\u00165sK\u0006$7\u000fC\u00037\u001f\u0002\u0007q\u0007C\u0003W\u0019\u0011\u0005q+A\u000foK^$\u0015-Z7p]NKgn\u001a7f)\"\u0014X-\u00193Fq\u0016\u001cW\u000f^8s)\tA6\f\u0005\u0002/3&\u0011!l\f\u0002\u0010\u000bb,7-\u001e;peN+'O^5dK\")A,\u0016a\u0001o\u0005QA\u000f\u001b:fC\u0012t\u0015-\\3\t\u000bycA\u0011A0\u0002M9,w\u000fR1f[>t7+\u001b8hY\u0016$\u0006N]3bIN\u001b\u0007.\u001a3vY\u0016$W\t_3dkR|'\u000f\u0006\u0002aGB\u0011a&Y\u0005\u0003E>\u0012\u0001dU2iK\u0012,H.\u001a3Fq\u0016\u001cW\u000f^8s'\u0016\u0014h/[2f\u0011\u0015aV\f1\u00018\u0011\u0015)G\u0002\"\u0001g\u00039\u0011XO\\%o\u001d\u0016<H\u000b\u001b:fC\u0012,\"aZ6\u0015\u0007!L(\u0010\u0006\u0002jiB\u0011!n\u001b\u0007\u0001\t\u0015aGM1\u0001n\u0005\u0005!\u0016C\u00018r!\t\u0001r.\u0003\u0002q#\t9aj\u001c;iS:<\u0007C\u0001\ts\u0013\t\u0019\u0018CA\u0002B]fDa!\u001e3\u0005\u0002\u00041\u0018\u0001\u00022pIf\u00042\u0001E<j\u0013\tA\u0018C\u0001\u0005=Eft\u0017-\\3?\u0011\u0015aF\r1\u00018\u0011\u001dYH\r%AA\u0002q\f\u0001\"[:EC\u0016lwN\u001c\t\u0003!uL!A`\t\u0003\u000f\t{w\u000e\\3b]\"9\u0011\u0011\u0001\u0007\u0005\u0002\u0005\r\u0011a\u00048fo\u001a{'o\u001b&pS:\u0004vn\u001c7\u0015\r\u0005\u0015\u0011\u0011CA\n!\u0011\t9!!\u0004\u000e\u0005\u0005%!bAA\u0006=\u0005Aam\u001c:lU>Lg.\u0003\u0003\u0002\u0010\u0005%!\u0001\u0004$pe.Tu.\u001b8Q_>d\u0007\"\u0002\u001c\u0000\u0001\u00049\u0004\"B%\u0000\u0001\u0004Q\u0005bBA\f\u0019\u0011\u0005\u0011\u0011D\u0001\fC^\f\u0017\u000e\u001e*fgVdG/\u0006\u0003\u0002\u001c\u0005}ACBA\u000f\u0003C\tY\u0003E\u0002k\u0003?!a\u0001\\A\u000b\u0005\u0004i\u0007\u0002CA\u0012\u0003+\u0001\r!!\n\u0002\u0013\u0005<\u0018-\u001b;bE2,\u0007#B\u000f\u0002(\u0005u\u0011bAA\u0015=\tI\u0011i^1ji\u0006\u0014G.\u001a\u0005\t\u0003[\t)\u00021\u0001\u00020\u00051\u0011\r^'pgR\u0004B!!\r\u000285\u0011\u00111\u0007\u0006\u0004\u0003kq\u0012\u0001\u00033ve\u0006$\u0018n\u001c8\n\t\u0005e\u00121\u0007\u0002\t\tV\u0014\u0018\r^5p]\"2\u0011QCA\u001f\u0003\u0017\u0002R\u0001EA \u0003\u0007J1!!\u0011\u0012\u0005\u0019!\bN]8xgB!\u0011QIA$\u001b\u0005!\u0011bAA%\t\tq1\u000b]1sW\u0016C8-\u001a9uS>t7EAA\"\u0011\u001d\ty\u0005\u0004C\u0001\u0003#\n!\"Y<bSR\u0014V-\u00193z+\u0011\t\u0019&!\u0018\u0015\r\u0005U\u0013qKA0\u001d\rQ\u0017q\u000b\u0005\t\u0003G\ti\u00051\u0001\u0002ZA)Q$a\n\u0002\\A\u0019!.!\u0018\u0005\r1\fiE1\u0001n\u0011!\ti#!\u0014A\u0002\u0005=\u0002FBA'\u0003{\tY\u0005C\u0005\u0002f1\t\n\u0011\"\u0001\u0002h\u0005\u0019c.Z<EC\u0016lwN\\\"bG\",G\r\u00165sK\u0006$\u0007k\\8mI\u0011,g-Y;mi\u0012\u001aTCAA5U\rQ\u00151N\u0016\u0003\u0003[\u0002B!a\u001c\u0002z5\u0011\u0011\u0011\u000f\u0006\u0005\u0003g\n)(A\u0005v]\u000eDWmY6fI*\u0019\u0011qO\t\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002|\u0005E$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"I\u0011q\u0010\u0007\u0012\u0002\u0013\u0005\u0011\u0011Q\u0001\u0019eVt\u0017J\u001c(foRC'/Z1eI\u0011,g-Y;mi\u0012\u0012T\u0003BAB\u0003\u000f+\"!!\"+\u0007q\fY\u0007\u0002\u0004m\u0003{\u0012\r!\u001c"
)
public final class ThreadUtils {
   public static <T> boolean runInNewThread$default$2() {
      return ThreadUtils$.MODULE$.runInNewThread$default$2();
   }

   public static int newDaemonCachedThreadPool$default$3() {
      return ThreadUtils$.MODULE$.newDaemonCachedThreadPool$default$3();
   }

   public static <T> Awaitable<T> awaitReady(Awaitable<T> var0, Duration var1) throws SparkException {
      return ThreadUtils$.MODULE$.<T>awaitReady(var0, var1);
   }

   public static <T> T awaitResult(Awaitable<T> var0, Duration var1) throws SparkException {
      return (T)ThreadUtils$.MODULE$.awaitResult(var0, var1);
   }

   public static ForkJoinPool newForkJoinPool(String var0, int var1) {
      return ThreadUtils$.MODULE$.newForkJoinPool(var0, var1);
   }

   public static <T> T runInNewThread(String var0, boolean var1, Function0<T> var2) {
      return (T)ThreadUtils$.MODULE$.runInNewThread(var0, var1, var2);
   }

   public static ScheduledExecutorService newDaemonSingleThreadScheduledExecutor(String var0) {
      return ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor(var0);
   }

   public static ExecutorService newDaemonSingleThreadExecutor(String var0) {
      return ThreadUtils$.MODULE$.newDaemonSingleThreadExecutor(var0);
   }

   public static ThreadPoolExecutor newDaemonFixedThreadPool(int var0, String var1) {
      return ThreadUtils$.MODULE$.newDaemonFixedThreadPool(var0, var1);
   }

   public static ThreadPoolExecutor newDaemonCachedThreadPool(String var0, int var1, int var2) {
      return ThreadUtils$.MODULE$.newDaemonCachedThreadPool(var0, var1, var2);
   }

   public static ThreadPoolExecutor newDaemonCachedThreadPool(String var0) {
      return ThreadUtils$.MODULE$.newDaemonCachedThreadPool(var0);
   }

   public static ThreadFactory namedThreadFactory(String var0) {
      return ThreadUtils$.MODULE$.namedThreadFactory(var0);
   }

   public static ExecutionContextExecutor sameThread() {
      return ThreadUtils$.MODULE$.sameThread();
   }
}
