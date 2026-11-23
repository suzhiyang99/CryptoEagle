package org.apache.spark.api.r;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import org.apache.spark.SparkConf;
import org.slf4j.Logger;
import scala.Function0;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\r4Q!\u0001\u0002\u0001\r1\u0011\u0001B\u0015\"bG.,g\u000e\u001a\u0006\u0003\u0007\u0011\t\u0011A\u001d\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7C\u0001\u0001\u000e!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fM\")A\u0003\u0001C\u0001-\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u0018!\tA\u0002!D\u0001\u0003\u0011\u0019Q\u0002\u0001)Q\u00057\u0005i1\r[1o]\u0016dg)\u001e;ve\u0016\u0004\"\u0001H\u0012\u000e\u0003uQ!AH\u0010\u0002\u000f\rD\u0017M\u001c8fY*\u0011\u0001%I\u0001\u0006]\u0016$H/\u001f\u0006\u0002E\u0005\u0011\u0011n\\\u0005\u0003Iu\u0011Qb\u00115b]:,GNR;ukJ,\u0007B\u0002\u0014\u0001A\u0003&q%A\u0005c_>$8\u000f\u001e:baB\u0011\u0001FK\u0007\u0002S)\u0011aeH\u0005\u0003W%\u0012qbU3sm\u0016\u0014(i\\8ugR\u0014\u0018\r\u001d\u0005\u0007[\u0001\u0001\u000b\u0015\u0002\u0018\u0002\u0013\t|7o]$s_V\u0004\bC\u0001\u000f0\u0013\t\u0001TD\u0001\bFm\u0016tG\u000fT8pa\u001e\u0013x.\u001e9\t\u0011I\u0002!\u0019!C\u0001\u0005M\n\u0001C\u001b<n\u001f\nTWm\u0019;Ue\u0006\u001c7.\u001a:\u0016\u0003Q\u0002\"\u0001G\u001b\n\u0005Y\u0012!\u0001\u0005&W\u001b>\u0013'.Z2u)J\f7m[3s\u0011\u0019A\u0004\u0001)A\u0005i\u0005\t\"N^7PE*,7\r\u001e+sC\u000e\\WM\u001d\u0011\t\u000bi\u0002A\u0011A\u001e\u0002\t%t\u0017\u000e\u001e\u000b\u0002yA\u0011a\"P\u0005\u0003}=\u00111!\u00138u\u0011\u0015\u0001\u0005\u0001\"\u0001B\u0003\r\u0011XO\u001c\u000b\u0002\u0005B\u0011abQ\u0005\u0003\t>\u0011A!\u00168ji\")a\t\u0001C\u0001\u0003\u0006)1\r\\8tK\u001e1\u0001J\u0001E\u0001\r%\u000b\u0001B\u0015\"bG.,g\u000e\u001a\t\u00031)3a!\u0001\u0002\t\u0002\u0019Y5c\u0001&\u000e\u0019B\u0011Q\nU\u0007\u0002\u001d*\u0011qJB\u0001\tS:$XM\u001d8bY&\u0011\u0011K\u0014\u0002\b\u0019><w-\u001b8h\u0011\u0015!\"\n\"\u0001T)\u0005I\u0005\"B+K\t\u00031\u0016\u0001B7bS:$\"AQ,\t\u000ba#\u0006\u0019A-\u0002\t\u0005\u0014xm\u001d\t\u0004\u001dic\u0016BA.\u0010\u0005\u0015\t%O]1z!\ti\u0006M\u0004\u0002\u000f=&\u0011qlD\u0001\u0007!J,G-\u001a4\n\u0005\u0005\u0014'AB*ue&twM\u0003\u0002`\u001f\u0001"
)
public class RBackend {
   private ChannelFuture channelFuture = null;
   private ServerBootstrap bootstrap = null;
   private EventLoopGroup bossGroup = null;
   private final JVMObjectTracker jvmObjectTracker = new JVMObjectTracker();

   public static boolean initializeLogIfNecessary$default$2() {
      return RBackend$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return RBackend$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      RBackend$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return RBackend$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      RBackend$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      RBackend$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      RBackend$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      RBackend$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      RBackend$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      RBackend$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      RBackend$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      RBackend$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      RBackend$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      RBackend$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return RBackend$.MODULE$.log();
   }

   public static String logName() {
      return RBackend$.MODULE$.logName();
   }

   public static void main(String[] var0) {
      RBackend$.MODULE$.main(var0);
   }

   public JVMObjectTracker jvmObjectTracker() {
      return this.jvmObjectTracker;
   }

   public int init() {
      SparkConf conf = new SparkConf();
      int backendConnectionTimeout = conf.getInt("spark.r.backendConnectionTimeout", SparkRDefaults$.MODULE$.DEFAULT_CONNECTION_TIMEOUT());
      this.bossGroup = new NioEventLoopGroup(conf.getInt("spark.r.numRBackendThreads", SparkRDefaults$.MODULE$.DEFAULT_NUM_RBACKEND_THREADS()));
      EventLoopGroup workerGroup = this.bossGroup;
      RBackendHandler handler = new RBackendHandler(this);
      this.bootstrap = (ServerBootstrap)(new ServerBootstrap()).group(this.bossGroup, workerGroup).channel(NioServerSocketChannel.class);
      this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>(this, backendConnectionTimeout, handler) {
         private final int backendConnectionTimeout$1;
         private final RBackendHandler handler$1;

         public void initChannel(SocketChannel ch) {
            ch.pipeline().addLast("encoder", new ByteArrayEncoder()).addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)).addLast("decoder", new ByteArrayDecoder()).addLast("readTimeoutHandler", new ReadTimeoutHandler(this.backendConnectionTimeout$1)).addLast("handler", this.handler$1);
         }

         public {
            this.backendConnectionTimeout$1 = backendConnectionTimeout$1;
            this.handler$1 = handler$1;
         }
      });
      this.channelFuture = this.bootstrap.bind(new InetSocketAddress("localhost", 0));
      this.channelFuture.syncUninterruptibly();
      return ((InetSocketAddress)this.channelFuture.channel().localAddress()).getPort();
   }

   public void run() {
      this.channelFuture.channel().closeFuture().syncUninterruptibly();
   }

   public void close() {
      if (this.channelFuture != null) {
         this.channelFuture.channel().close().awaitUninterruptibly(10L, TimeUnit.SECONDS);
         this.channelFuture = null;
      }

      if (this.bootstrap != null && this.bootstrap.group() != null) {
         this.bootstrap.group().shutdownGracefully();
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      if (this.bootstrap != null && this.bootstrap.childGroup() != null) {
         this.bootstrap.childGroup().shutdownGracefully();
      } else {
         BoxedUnit var1 = BoxedUnit.UNIT;
      }

      this.bootstrap = null;
      this.jvmObjectTracker().clear();
   }
}
