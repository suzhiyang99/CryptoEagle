package org.apache.spark.metrics.sink;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import java.util.Properties;
import org.apache.spark.SecurityManager;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I3Q!\u0001\u0002\u0001\r1\u0011qAS7y'&t7N\u0003\u0002\u0004\t\u0005!1/\u001b8l\u0015\t)a!A\u0004nKR\u0014\u0018nY:\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0014!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011A#F\u0007\u0002\u0005%\u0011aC\u0001\u0002\u0005'&t7\u000e\u0003\u0005\u0019\u0001\t\u0015\r\u0011\"\u0001\u001b\u0003!\u0001(o\u001c9feRL8\u0001A\u000b\u00027A\u0011A$I\u0007\u0002;)\u0011adH\u0001\u0005kRLGNC\u0001!\u0003\u0011Q\u0017M^1\n\u0005\tj\"A\u0003)s_B,'\u000f^5fg\"AA\u0005\u0001B\u0001B\u0003%1$A\u0005qe>\u0004XM\u001d;zA!Aa\u0005\u0001BC\u0002\u0013\u0005q%\u0001\u0005sK\u001eL7\u000f\u001e:z+\u0005A\u0003CA\u00150\u001b\u0005Q#BA\u0003,\u0015\taS&\u0001\u0005d_\u0012\f\u0007.\u00197f\u0015\u0005q\u0013aA2p[&\u0011\u0001G\u000b\u0002\u000f\u001b\u0016$(/[2SK\u001eL7\u000f\u001e:z\u0011!\u0011\u0004A!A!\u0002\u0013A\u0013!\u0003:fO&\u001cHO]=!\u0011!!\u0004A!A!\u0002\u0013)\u0014aC:fGV\u0014\u0018\u000e^=NOJ\u0004\"AN\u001c\u000e\u0003\u0019I!\u0001\u000f\u0004\u0003\u001fM+7-\u001e:jifl\u0015M\\1hKJDQA\u000f\u0001\u0005\u0002m\na\u0001P5oSRtD\u0003\u0002\u001f>}}\u0002\"\u0001\u0006\u0001\t\u000baI\u0004\u0019A\u000e\t\u000b\u0019J\u0004\u0019\u0001\u0015\t\u000bQJ\u0004\u0019A\u001b\t\u000f\u0005\u0003!\u0019!C\u0001\u0005\u0006A!/\u001a9peR,'/F\u0001D!\tIC)\u0003\u0002FU\tY!*\u001c=SKB|'\u000f^3s\u0011\u00199\u0005\u0001)A\u0005\u0007\u0006I!/\u001a9peR,'\u000f\t\u0005\u0006\u0013\u0002!\tES\u0001\u0006gR\f'\u000f\u001e\u000b\u0002\u0017B\u0011a\u0002T\u0005\u0003\u001b>\u0011A!\u00168ji\")q\n\u0001C!\u0015\u0006!1\u000f^8q\u0011\u0015\t\u0006\u0001\"\u0011K\u0003\u0019\u0011X\r]8si\u0002"
)
public class JmxSink implements Sink {
   private final Properties property;
   private final MetricRegistry registry;
   private final JmxReporter reporter;

   public Properties property() {
      return this.property;
   }

   public MetricRegistry registry() {
      return this.registry;
   }

   public JmxReporter reporter() {
      return this.reporter;
   }

   public void start() {
      this.reporter().start();
   }

   public void stop() {
      this.reporter().stop();
   }

   public void report() {
   }

   public JmxSink(Properties property, MetricRegistry registry, SecurityManager securityMgr) {
      this.property = property;
      this.registry = registry;
      this.reporter = JmxReporter.forRegistry(registry).build();
   }
}
