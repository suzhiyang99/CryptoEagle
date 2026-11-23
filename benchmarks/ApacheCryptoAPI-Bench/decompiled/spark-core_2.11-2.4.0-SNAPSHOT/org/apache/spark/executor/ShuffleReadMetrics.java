package org.apache.spark.executor;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.util.LongAccumulator;
import scala.Serializable;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005mc\u0001B\u0001\u0003\u0001-\u0011!c\u00155vM\u001adWMU3bI6+GO]5dg*\u00111\u0001B\u0001\tKb,7-\u001e;pe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001bMI!\u0001\u0006\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\rY\u0001A\u0011\u0001\u0003\u0018\u0003\u0019a\u0014N\\5u}Q\t\u0001\u0004\u0005\u0002\u001a\u00015\t!\u0001\u0003\u0005\u001c\u0001\t\u0007I\u0011\u0001\u0002\u001d\u0003Qy&/Z7pi\u0016\u0014En\\2lg\u001a+Go\u00195fIV\tQ\u0004\u0005\u0002\u001fC5\tqD\u0003\u0002!\t\u0005!Q\u000f^5m\u0013\t\u0011sDA\bM_:<\u0017iY2v[Vd\u0017\r^8s\u0011\u0019!\u0003\u0001)A\u0005;\u0005)rL]3n_R,'\t\\8dWN4U\r^2iK\u0012\u0004\u0003\u0002\u0003\u0014\u0001\u0005\u0004%\tA\u0001\u000f\u0002'}cwnY1m\u00052|7m[:GKR\u001c\u0007.\u001a3\t\r!\u0002\u0001\u0015!\u0003\u001e\u0003QyFn\\2bY\ncwnY6t\r\u0016$8\r[3eA!A!\u0006\u0001b\u0001\n\u0003\u0011A$\u0001\t`e\u0016lw\u000e^3CsR,7OU3bI\"1A\u0006\u0001Q\u0001\nu\t\u0011c\u0018:f[>$XMQ=uKN\u0014V-\u00193!\u0011!q\u0003A1A\u0005\u0002\ta\u0012AF0sK6|G/\u001a\"zi\u0016\u001c(+Z1e)>$\u0015n]6\t\rA\u0002\u0001\u0015!\u0003\u001e\u0003]y&/Z7pi\u0016\u0014\u0015\u0010^3t%\u0016\fG\rV8ESN\\\u0007\u0005\u0003\u00053\u0001\t\u0007I\u0011\u0001\u0002\u001d\u0003=yFn\\2bY\nKH/Z:SK\u0006$\u0007B\u0002\u001b\u0001A\u0003%Q$\u0001\t`Y>\u001c\u0017\r\u001c\"zi\u0016\u001c(+Z1eA!Aa\u0007\u0001b\u0001\n\u0003\u0011A$\u0001\b`M\u0016$8\r[,bSR$\u0016.\\3\t\ra\u0002\u0001\u0015!\u0003\u001e\u0003=yf-\u001a;dQ^\u000b\u0017\u000e\u001e+j[\u0016\u0004\u0003\u0002\u0003\u001e\u0001\u0005\u0004%\tA\u0001\u000f\u0002\u0019}\u0013XmY8sIN\u0014V-\u00193\t\rq\u0002\u0001\u0015!\u0003\u001e\u00035y&/Z2pe\u0012\u001c(+Z1eA!)a\b\u0001C\u0001\u007f\u0005\u0019\"/Z7pi\u0016\u0014En\\2lg\u001a+Go\u00195fIV\t\u0001\t\u0005\u0002\u000e\u0003&\u0011!I\u0004\u0002\u0005\u0019>tw\rC\u0003E\u0001\u0011\u0005q(\u0001\nm_\u000e\fGN\u00117pG.\u001ch)\u001a;dQ\u0016$\u0007\"\u0002$\u0001\t\u0003y\u0014a\u0004:f[>$XMQ=uKN\u0014V-\u00193\t\u000b!\u0003A\u0011A \u0002+I,Wn\u001c;f\u0005f$Xm\u001d*fC\u0012$v\u000eR5tW\")!\n\u0001C\u0001\u007f\u0005qAn\\2bY\nKH/Z:SK\u0006$\u0007\"\u0002'\u0001\t\u0003y\u0014!\u00044fi\u000eDw+Y5u)&lW\rC\u0003O\u0001\u0011\u0005q(A\u0006sK\u000e|'\u000fZ:SK\u0006$\u0007\"\u0002)\u0001\t\u0003y\u0014A\u0004;pi\u0006d')\u001f;fgJ+\u0017\r\u001a\u0005\u0006%\u0002!\taP\u0001\u0013i>$\u0018\r\u001c\"m_\u000e\\7OR3uG\",G\r\u0003\u0004U\u0001\u0011\u0005A!V\u0001\u0017S:\u001c'+Z7pi\u0016\u0014En\\2lg\u001a+Go\u00195fIR\u0011a+\u0017\t\u0003\u001b]K!\u0001\u0017\b\u0003\tUs\u0017\u000e\u001e\u0005\u00065N\u0003\r\u0001Q\u0001\u0002m\"1A\f\u0001C\u0001\tu\u000bQ#\u001b8d\u0019>\u001c\u0017\r\u001c\"m_\u000e\\7OR3uG\",G\r\u0006\u0002W=\")!l\u0017a\u0001\u0001\"1\u0001\r\u0001C\u0001\t\u0005\f!#\u001b8d%\u0016lw\u000e^3CsR,7OU3bIR\u0011aK\u0019\u0005\u00065~\u0003\r\u0001\u0011\u0005\u0007I\u0002!\t\u0001B3\u00021%t7MU3n_R,')\u001f;fgJ+\u0017\r\u001a+p\t&\u001c8\u000e\u0006\u0002WM\")!l\u0019a\u0001\u0001\"1\u0001\u000e\u0001C\u0001\t%\f\u0011#\u001b8d\u0019>\u001c\u0017\r\u001c\"zi\u0016\u001c(+Z1e)\t1&\u000eC\u0003[O\u0002\u0007\u0001\t\u0003\u0004m\u0001\u0011\u0005A!\\\u0001\u0011S:\u001cg)\u001a;dQ^\u000b\u0017\u000e\u001e+j[\u0016$\"A\u00168\t\u000bi[\u0007\u0019\u0001!\t\rA\u0004A\u0011\u0001\u0003r\u00039Ign\u0019*fG>\u0014Hm\u001d*fC\u0012$\"A\u0016:\t\u000bi{\u0007\u0019\u0001!\t\rQ\u0004A\u0011\u0001\u0003v\u0003Y\u0019X\r\u001e*f[>$XM\u00117pG.\u001ch)\u001a;dQ\u0016$GC\u0001,w\u0011\u0015Q6\u000f1\u0001x!\ti\u00010\u0003\u0002z\u001d\t\u0019\u0011J\u001c;\t\rm\u0004A\u0011\u0001\u0003}\u0003U\u0019X\r\u001e'pG\u0006d'\t\\8dWN4U\r^2iK\u0012$\"AV?\t\u000biS\b\u0019A<\t\u000f}\u0004A\u0011\u0001\u0003\u0002\u0002\u0005\u00112/\u001a;SK6|G/\u001a\"zi\u0016\u001c(+Z1e)\r1\u00161\u0001\u0005\u00065z\u0004\r\u0001\u0011\u0005\t\u0003\u000f\u0001A\u0011\u0001\u0003\u0002\n\u0005A2/\u001a;SK6|G/\u001a\"zi\u0016\u001c(+Z1e)>$\u0015n]6\u0015\u0007Y\u000bY\u0001\u0003\u0004[\u0003\u000b\u0001\r\u0001\u0011\u0005\t\u0003\u001f\u0001A\u0011\u0001\u0003\u0002\u0012\u0005\t2/\u001a;M_\u000e\fGNQ=uKN\u0014V-\u00193\u0015\u0007Y\u000b\u0019\u0002\u0003\u0004[\u0003\u001b\u0001\r\u0001\u0011\u0005\t\u0003/\u0001A\u0011\u0001\u0003\u0002\u001a\u0005\u00012/\u001a;GKR\u001c\u0007nV1jiRKW.\u001a\u000b\u0004-\u0006m\u0001B\u0002.\u0002\u0016\u0001\u0007\u0001\t\u0003\u0005\u0002 \u0001!\t\u0001BA\u0011\u00039\u0019X\r\u001e*fG>\u0014Hm\u001d*fC\u0012$2AVA\u0012\u0011\u0019Q\u0016Q\u0004a\u0001\u0001\"A\u0011q\u0005\u0001\u0005\u0002\u0011\tI#\u0001\btKRlUM]4f-\u0006dW/Z:\u0015\u0007Y\u000bY\u0003\u0003\u0005\u0002.\u0005\u0015\u0002\u0019AA\u0018\u0003\u001diW\r\u001e:jGN\u0004b!!\r\u0002B\u0005\u001dc\u0002BA\u001a\u0003{qA!!\u000e\u0002<5\u0011\u0011q\u0007\u0006\u0004\u0003sQ\u0011A\u0002\u001fs_>$h(C\u0001\u0010\u0013\r\tyDD\u0001\ba\u0006\u001c7.Y4f\u0013\u0011\t\u0019%!\u0012\u0003\u0007M+\u0017OC\u0002\u0002@9\u00012!GA%\u0013\r\tYE\u0001\u0002\u0017)\u0016l\u0007o\u00155vM\u001adWMU3bI6+GO]5dg\"\u001a\u0001!a\u0014\u0011\t\u0005E\u0013qK\u0007\u0003\u0003'R1!!\u0016\u0005\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u00033\n\u0019F\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018\u000e"
)
public class ShuffleReadMetrics implements Serializable {
   private final LongAccumulator _remoteBlocksFetched = new LongAccumulator();
   private final LongAccumulator _localBlocksFetched = new LongAccumulator();
   private final LongAccumulator _remoteBytesRead = new LongAccumulator();
   private final LongAccumulator _remoteBytesReadToDisk = new LongAccumulator();
   private final LongAccumulator _localBytesRead = new LongAccumulator();
   private final LongAccumulator _fetchWaitTime = new LongAccumulator();
   private final LongAccumulator _recordsRead = new LongAccumulator();

   public LongAccumulator _remoteBlocksFetched() {
      return this._remoteBlocksFetched;
   }

   public LongAccumulator _localBlocksFetched() {
      return this._localBlocksFetched;
   }

   public LongAccumulator _remoteBytesRead() {
      return this._remoteBytesRead;
   }

   public LongAccumulator _remoteBytesReadToDisk() {
      return this._remoteBytesReadToDisk;
   }

   public LongAccumulator _localBytesRead() {
      return this._localBytesRead;
   }

   public LongAccumulator _fetchWaitTime() {
      return this._fetchWaitTime;
   }

   public LongAccumulator _recordsRead() {
      return this._recordsRead;
   }

   public long remoteBlocksFetched() {
      return this._remoteBlocksFetched().sum();
   }

   public long localBlocksFetched() {
      return this._localBlocksFetched().sum();
   }

   public long remoteBytesRead() {
      return this._remoteBytesRead().sum();
   }

   public long remoteBytesReadToDisk() {
      return this._remoteBytesReadToDisk().sum();
   }

   public long localBytesRead() {
      return this._localBytesRead().sum();
   }

   public long fetchWaitTime() {
      return this._fetchWaitTime().sum();
   }

   public long recordsRead() {
      return this._recordsRead().sum();
   }

   public long totalBytesRead() {
      return this.remoteBytesRead() + this.localBytesRead();
   }

   public long totalBlocksFetched() {
      return this.remoteBlocksFetched() + this.localBlocksFetched();
   }

   public void incRemoteBlocksFetched(long v) {
      this._remoteBlocksFetched().add(v);
   }

   public void incLocalBlocksFetched(long v) {
      this._localBlocksFetched().add(v);
   }

   public void incRemoteBytesRead(long v) {
      this._remoteBytesRead().add(v);
   }

   public void incRemoteBytesReadToDisk(long v) {
      this._remoteBytesReadToDisk().add(v);
   }

   public void incLocalBytesRead(long v) {
      this._localBytesRead().add(v);
   }

   public void incFetchWaitTime(long v) {
      this._fetchWaitTime().add(v);
   }

   public void incRecordsRead(long v) {
      this._recordsRead().add(v);
   }

   public void setRemoteBlocksFetched(int v) {
      this._remoteBlocksFetched().setValue((long)v);
   }

   public void setLocalBlocksFetched(int v) {
      this._localBlocksFetched().setValue((long)v);
   }

   public void setRemoteBytesRead(long v) {
      this._remoteBytesRead().setValue(v);
   }

   public void setRemoteBytesReadToDisk(long v) {
      this._remoteBytesReadToDisk().setValue(v);
   }

   public void setLocalBytesRead(long v) {
      this._localBytesRead().setValue(v);
   }

   public void setFetchWaitTime(long v) {
      this._fetchWaitTime().setValue(v);
   }

   public void setRecordsRead(long v) {
      this._recordsRead().setValue(v);
   }

   public void setMergeValues(Seq<TempShuffleReadMetrics> metrics) {
      this._remoteBlocksFetched().setValue(0L);
      this._localBlocksFetched().setValue(0L);
      this._remoteBytesRead().setValue(0L);
      this._remoteBytesReadToDisk().setValue(0L);
      this._localBytesRead().setValue(0L);
      this._fetchWaitTime().setValue(0L);
      this._recordsRead().setValue(0L);
      metrics.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleReadMetrics $outer;

         public final void apply(TempShuffleReadMetrics metric) {
            this.$outer._remoteBlocksFetched().add(metric.remoteBlocksFetched());
            this.$outer._localBlocksFetched().add(metric.localBlocksFetched());
            this.$outer._remoteBytesRead().add(metric.remoteBytesRead());
            this.$outer._remoteBytesReadToDisk().add(metric.remoteBytesReadToDisk());
            this.$outer._localBytesRead().add(metric.localBytesRead());
            this.$outer._fetchWaitTime().add(metric.fetchWaitTime());
            this.$outer._recordsRead().add(metric.recordsRead());
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
}
