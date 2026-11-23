package org.apache.spark.executor;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001]3Q!\u0001\u0002\u0001\t)\u0011a\u0003V3naNCWO\u001a4mKJ+\u0017\rZ'fiJL7m\u001d\u0006\u0003\u0007\u0011\t\u0001\"\u001a=fGV$xN\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0011\u0001a\u0003\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\t\u000bI\u0001A\u0011\u0001\u000b\u0002\rqJg.\u001b;?\u0007\u0001!\u0012!\u0006\t\u0003-\u0001i\u0011A\u0001\u0005\u00071\u0001\u0001\u000b\u0015B\r\u0002)}\u0013X-\\8uK\ncwnY6t\r\u0016$8\r[3e!\ta!$\u0003\u0002\u001c\u001b\t!Aj\u001c8h\u0011\u0019i\u0002\u0001)Q\u00053\u0005\u0019r\f\\8dC2\u0014En\\2lg\u001a+Go\u00195fI\"1q\u0004\u0001Q!\ne\t\u0001c\u0018:f[>$XMQ=uKN\u0014V-\u00193\t\r\u0005\u0002\u0001\u0015)\u0003\u001a\u0003Yy&/Z7pi\u0016\u0014\u0015\u0010^3t%\u0016\fG\rV8ESN\\\u0007BB\u0012\u0001A\u0003&\u0011$A\b`Y>\u001c\u0017\r\u001c\"zi\u0016\u001c(+Z1e\u0011\u0019)\u0003\u0001)Q\u00053\u0005qqLZ3uG\"<\u0016-\u001b;US6,\u0007BB\u0014\u0001A\u0003&\u0011$\u0001\u0007`e\u0016\u001cwN\u001d3t%\u0016\fG\rC\u0003*\u0001\u0011\u0005!&\u0001\fj]\u000e\u0014V-\\8uK\ncwnY6t\r\u0016$8\r[3e)\tYc\u0006\u0005\u0002\rY%\u0011Q&\u0004\u0002\u0005+:LG\u000fC\u00030Q\u0001\u0007\u0011$A\u0001w\u0011\u0015\t\u0004\u0001\"\u00013\u0003UIgn\u0019'pG\u0006d'\t\\8dWN4U\r^2iK\u0012$\"aK\u001a\t\u000b=\u0002\u0004\u0019A\r\t\u000bU\u0002A\u0011\u0001\u001c\u0002%%t7MU3n_R,')\u001f;fgJ+\u0017\r\u001a\u000b\u0003W]BQa\f\u001bA\u0002eAQ!\u000f\u0001\u0005\u0002i\n\u0001$\u001b8d%\u0016lw\u000e^3CsR,7OU3bIR{G)[:l)\tY3\bC\u00030q\u0001\u0007\u0011\u0004C\u0003>\u0001\u0011\u0005a(A\tj]\u000edunY1m\u0005f$Xm\u001d*fC\u0012$\"aK \t\u000b=b\u0004\u0019A\r\t\u000b\u0005\u0003A\u0011\u0001\"\u0002!%t7MR3uG\"<\u0016-\u001b;US6,GCA\u0016D\u0011\u0015y\u0003\t1\u0001\u001a\u0011\u0015)\u0005\u0001\"\u0001G\u00039Ign\u0019*fG>\u0014Hm\u001d*fC\u0012$\"aK$\t\u000b=\"\u0005\u0019A\r\t\u000b%\u0003A\u0011\u0001&\u0002'I,Wn\u001c;f\u00052|7m[:GKR\u001c\u0007.\u001a3\u0016\u0003eAQ\u0001\u0014\u0001\u0005\u0002)\u000b!\u0003\\8dC2\u0014En\\2lg\u001a+Go\u00195fI\")a\n\u0001C\u0001\u0015\u0006y!/Z7pi\u0016\u0014\u0015\u0010^3t%\u0016\fG\rC\u0003Q\u0001\u0011\u0005!*A\u000bsK6|G/\u001a\"zi\u0016\u001c(+Z1e)>$\u0015n]6\t\u000bI\u0003A\u0011\u0001&\u0002\u001d1|7-\u00197CsR,7OU3bI\")A\u000b\u0001C\u0001\u0015\u0006ia-\u001a;dQ^\u000b\u0017\u000e\u001e+j[\u0016DQA\u0016\u0001\u0005\u0002)\u000b1B]3d_J$7OU3bI\u0002"
)
public class TempShuffleReadMetrics {
   private long _remoteBlocksFetched = 0L;
   private long _localBlocksFetched = 0L;
   private long _remoteBytesRead = 0L;
   private long _remoteBytesReadToDisk = 0L;
   private long _localBytesRead = 0L;
   private long _fetchWaitTime = 0L;
   private long _recordsRead = 0L;

   public void incRemoteBlocksFetched(long v) {
      this._remoteBlocksFetched += v;
   }

   public void incLocalBlocksFetched(long v) {
      this._localBlocksFetched += v;
   }

   public void incRemoteBytesRead(long v) {
      this._remoteBytesRead += v;
   }

   public void incRemoteBytesReadToDisk(long v) {
      this._remoteBytesReadToDisk += v;
   }

   public void incLocalBytesRead(long v) {
      this._localBytesRead += v;
   }

   public void incFetchWaitTime(long v) {
      this._fetchWaitTime += v;
   }

   public void incRecordsRead(long v) {
      this._recordsRead += v;
   }

   public long remoteBlocksFetched() {
      return this._remoteBlocksFetched;
   }

   public long localBlocksFetched() {
      return this._localBlocksFetched;
   }

   public long remoteBytesRead() {
      return this._remoteBytesRead;
   }

   public long remoteBytesReadToDisk() {
      return this._remoteBytesReadToDisk;
   }

   public long localBytesRead() {
      return this._localBytesRead;
   }

   public long fetchWaitTime() {
      return this._fetchWaitTime;
   }

   public long recordsRead() {
      return this._recordsRead;
   }
}
