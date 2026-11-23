package org.apache.spark.launcher;

import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ThreadFactory;
import org.apache.spark.SparkConf;
import org.apache.spark.util.Utils$;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.sys.package$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005UdAB\u0001\u0003\u0003\u0003!!BA\bMCVt7\r[3s\u0005\u0006\u001c7.\u001a8e\u0015\t\u0019A!\u0001\u0005mCVt7\r[3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fM\")!\u0003\u0001C\u0001)\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u0016!\t1\u0002!D\u0001\u0003\u0011%A\u0002\u00011AA\u0002\u0013%\u0011$\u0001\u0007dY&,g\u000e\u001e+ie\u0016\fG-F\u0001\u001b!\tY\u0002%D\u0001\u001d\u0015\tib$\u0001\u0003mC:<'\"A\u0010\u0002\t)\fg/Y\u0005\u0003Cq\u0011a\u0001\u00165sK\u0006$\u0007\"C\u0012\u0001\u0001\u0004\u0005\r\u0011\"\u0003%\u0003A\u0019G.[3oiRC'/Z1e?\u0012*\u0017\u000f\u0006\u0002&QA\u0011ABJ\u0005\u0003O5\u0011A!\u00168ji\"9\u0011FIA\u0001\u0002\u0004Q\u0012a\u0001=%c!11\u0006\u0001Q!\ni\tQb\u00197jK:$H\u000b\u001b:fC\u0012\u0004\u0003\"C\u0017\u0001\u0001\u0004\u0005\r\u0011\"\u0003/\u0003)\u0019wN\u001c8fGRLwN\\\u000b\u0002_A\u0011\u0001'M\u0007\u0002\u0001\u0019!!\u0007\u0001\u00034\u0005E\u0011\u0015mY6f]\u0012\u001cuN\u001c8fGRLwN\\\n\u0003cQ\u0002\"AF\u001b\n\u0005Y\u0012!A\u0005'bk:\u001c\u0007.\u001a:D_:tWm\u0019;j_:D\u0001\u0002O\u0019\u0003\u0002\u0003\u0006I!O\u0001\u0002gB\u0011!(P\u0007\u0002w)\u0011AHH\u0001\u0004]\u0016$\u0018B\u0001 <\u0005\u0019\u0019vnY6fi\")!#\rC\u0001\u0001R\u0011q&\u0011\u0005\u0006q}\u0002\r!\u000f\u0005\u0006\u0007F\"\t\u0006R\u0001\u0007Q\u0006tG\r\\3\u0015\u0005\u0015*\u0005\"\u0002$C\u0001\u00049\u0015!A7\u0011\u0005!3fBA%U\u001d\tQ5K\u0004\u0002L%:\u0011A*\u0015\b\u0003\u001bBk\u0011A\u0014\u0006\u0003\u001fN\ta\u0001\u0010:p_Rt\u0014\"A\u0005\n\u0005\u001dA\u0011BA\u0003\u0007\u0013\t\u0019A!\u0003\u0002V\u0005\u0005\u0001B*Y;oG\",'\u000f\u0015:pi>\u001cw\u000e\\\u0005\u0003/b\u0013q!T3tg\u0006<WM\u0003\u0002V\u0005!)!,\rC!7\u0006)1\r\\8tKR\tQ\u0005C\u0005^\u0001\u0001\u0007\t\u0019!C\u0005=\u0006q1m\u001c8oK\u000e$\u0018n\u001c8`I\u0015\fHCA\u0013`\u0011\u001dIC,!AA\u0002=Ba!\u0019\u0001!B\u0013y\u0013aC2p]:,7\r^5p]\u0002B\u0011b\u0019\u0001A\u0002\u0003\u0007I\u0011\u00023\u0002\u00131\f7\u000f^*uCR,W#A3\u0011\u0005\u0019LgB\u0001\fh\u0013\tA'!\u0001\bTa\u0006\u00148.\u00119q\u0011\u0006tG\r\\3\n\u0005)\\'!B*uCR,'B\u00015\u0003\u0011%i\u0007\u00011AA\u0002\u0013%a.A\u0007mCN$8\u000b^1uK~#S-\u001d\u000b\u0003K=Dq!\u000b7\u0002\u0002\u0003\u0007Q\r\u0003\u0004r\u0001\u0001\u0006K!Z\u0001\u000bY\u0006\u001cHo\u0015;bi\u0016\u0004\u0003bB:\u0001\u0001\u0004%I\u0001^\u0001\r?&\u001c8i\u001c8oK\u000e$X\rZ\u000b\u0002kB\u0011AB^\u0005\u0003o6\u0011qAQ8pY\u0016\fg\u000eC\u0004z\u0001\u0001\u0007I\u0011\u0002>\u0002!}K7oQ8o]\u0016\u001cG/\u001a3`I\u0015\fHCA\u0013|\u0011\u001dI\u00030!AA\u0002UDa! \u0001!B\u0013)\u0018!D0jg\u000e{gN\\3di\u0016$\u0007\u0005\u000b\u0002}\u007fB\u0019A\"!\u0001\n\u0007\u0005\rQB\u0001\u0005w_2\fG/\u001b7f\u0011\u001d\t9\u0001\u0001D\t\u0003\u0013\tAaY8oMV\u0011\u00111\u0002\t\u0005\u0003\u001b\ty!D\u0001\u0005\u0013\r\t\t\u0002\u0002\u0002\n'B\f'o[\"p]\u001aDa!!\u0006\u0001\t\u0003Y\u0016aB2p]:,7\r\u001e\u0005\u00065\u0002!\ta\u0017\u0005\b\u00037\u0001A\u0011AA\u000f\u0003!\u0019X\r^!qa&#GcA\u0013\u0002 !A\u0011\u0011EA\r\u0001\u0004\t\u0019#A\u0003baBLE\r\u0005\u0003\u0002&\u0005-bb\u0001\u0007\u0002(%\u0019\u0011\u0011F\u0007\u0002\rA\u0013X\rZ3g\u0013\u0011\ti#a\f\u0003\rM#(/\u001b8h\u0015\r\tI#\u0004\u0005\b\u0003g\u0001A\u0011AA\u001b\u0003!\u0019X\r^*uCR,GcA\u0013\u00028!9\u0011\u0011HA\u0019\u0001\u0004)\u0017!B:uCR,\u0007bBA\u001f\u0001\u0011\u0005\u0011qH\u0001\fSN\u001cuN\u001c8fGR,G\rF\u0001v\u0011\u0019\t\u0019\u0005\u0001D\t7\u0006iqN\\*u_B\u0014V-];fgRDa!a\u0012\u0001\t#Y\u0016AD8o\t&\u001c8m\u001c8oK\u000e$X\r\u001a\u0005\u0007\u0003\u0017\u0002A\u0011B.\u0002\u001f\u0019L'/Z*u_B\u0014V-];fgR<q!a\u0014\u0003\u0011\u0013\t\t&A\bMCVt7\r[3s\u0005\u0006\u001c7.\u001a8e!\r1\u00121\u000b\u0004\u0007\u0003\tAI!!\u0016\u0014\u0007\u0005M3\u0002C\u0004\u0013\u0003'\"\t!!\u0017\u0015\u0005\u0005E\u0003BCA/\u0003'\u0012\r\u0011\"\u0001\u0002`\u0005iA\u000f\u001b:fC\u00124\u0015m\u0019;pef,\"!!\u0019\u0011\t\u0005\r\u0014QN\u0007\u0003\u0003KRA!a\u001a\u0002j\u0005Q1m\u001c8dkJ\u0014XM\u001c;\u000b\u0007\u0005-d$\u0001\u0003vi&d\u0017\u0002BA8\u0003K\u0012Q\u0002\u00165sK\u0006$g)Y2u_JL\b\"CA:\u0003'\u0002\u000b\u0011BA1\u00039!\bN]3bI\u001a\u000b7\r^8ss\u0002\u0002"
)
public abstract class LauncherBackend {
   private Thread clientThread;
   private BackendConnection connection;
   private SparkAppHandle.State lastState;
   private volatile boolean org$apache$spark$launcher$LauncherBackend$$_isConnected = false;

   public static ThreadFactory threadFactory() {
      return LauncherBackend$.MODULE$.threadFactory();
   }

   private Thread clientThread() {
      return this.clientThread;
   }

   private void clientThread_$eq(Thread x$1) {
      this.clientThread = x$1;
   }

   private BackendConnection connection() {
      return this.connection;
   }

   private void connection_$eq(BackendConnection x$1) {
      this.connection = x$1;
   }

   private SparkAppHandle.State lastState() {
      return this.lastState;
   }

   private void lastState_$eq(SparkAppHandle.State x$1) {
      this.lastState = x$1;
   }

   private boolean org$apache$spark$launcher$LauncherBackend$$_isConnected() {
      return this.org$apache$spark$launcher$LauncherBackend$$_isConnected;
   }

   public void org$apache$spark$launcher$LauncherBackend$$_isConnected_$eq(boolean x$1) {
      this.org$apache$spark$launcher$LauncherBackend$$_isConnected = x$1;
   }

   public abstract SparkConf conf();

   public void connect() {
      Option port = this.conf().getOption("spark.launcher.port").orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply() {
            return package$.MODULE$.env().get("_SPARK_LAUNCHER_PORT");
         }
      }).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$1) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toInt();
         }
      });
      Option secret = this.conf().getOption("spark.launcher.secret").orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Option<String> apply() {
            return package$.MODULE$.env().get("_SPARK_LAUNCHER_SECRET");
         }
      });
      None$ var3 = None$.MODULE$;
      if (port == null) {
         if (var3 == null) {
            return;
         }
      } else if (port.equals(var3)) {
         return;
      }

      None$ var4 = None$.MODULE$;
      if (secret == null) {
         if (var4 == null) {
            return;
         }
      } else if (secret.equals(var4)) {
         return;
      }

      Socket s = new Socket(InetAddress.getLoopbackAddress(), BoxesRunTime.unboxToInt(port.get()));
      this.connection_$eq(new BackendConnection(s));
      this.connection().send(new LauncherProtocol.Hello((String)secret.get(), org.apache.spark.package$.MODULE$.SPARK_VERSION()));
      this.clientThread_$eq(LauncherBackend$.MODULE$.threadFactory().newThread(this.connection()));
      this.clientThread().start();
      this.org$apache$spark$launcher$LauncherBackend$$_isConnected_$eq(true);
   }

   public void close() {
      if (this.connection() != null) {
         try {
            this.connection().close();
         } finally {
            if (this.clientThread() != null) {
               this.clientThread().join();
            }

         }
      }

   }

   public void setAppId(String appId) {
      if (this.connection() != null) {
         this.connection().send(new LauncherProtocol.SetAppId(appId));
      }

   }

   public void setState(SparkAppHandle.State state) {
      if (this.connection() != null) {
         SparkAppHandle.State var10000 = this.lastState();
         if (var10000 == null) {
            if (state == null) {
               return;
            }
         } else if (var10000.equals(state)) {
            return;
         }

         this.connection().send(new LauncherProtocol.SetState(state));
         this.lastState_$eq(state);
      }

   }

   public boolean isConnected() {
      return this.org$apache$spark$launcher$LauncherBackend$$_isConnected();
   }

   public abstract void onStopRequest();

   public void onDisconnected() {
   }

   public void org$apache$spark$launcher$LauncherBackend$$fireStopRequest() {
      Thread thread = LauncherBackend$.MODULE$.threadFactory().newThread(new Runnable(this) {
         // $FF: synthetic field
         private final LauncherBackend $outer;

         public void run() {
            Utils$.MODULE$.tryLogNonFatalError(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.org$apache$spark$launcher$LauncherBackend$$anon$$$outer().onStopRequest();
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

         // $FF: synthetic method
         public LauncherBackend org$apache$spark$launcher$LauncherBackend$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      thread.start();
   }

   public class BackendConnection extends LauncherConnection {
      // $FF: synthetic field
      public final LauncherBackend $outer;

      public void handle(LauncherProtocol.Message m) {
         if (m instanceof LauncherProtocol.Stop) {
            this.org$apache$spark$launcher$LauncherBackend$BackendConnection$$$outer().org$apache$spark$launcher$LauncherBackend$$fireStopRequest();
            BoxedUnit var3 = BoxedUnit.UNIT;
         } else {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unexpected message type: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{m.getClass().getName()})));
         }
      }

      public void close() {
         try {
            super.close();
         } finally {
            this.org$apache$spark$launcher$LauncherBackend$BackendConnection$$$outer().onDisconnected();
            this.org$apache$spark$launcher$LauncherBackend$BackendConnection$$$outer().org$apache$spark$launcher$LauncherBackend$$_isConnected_$eq(false);
         }

      }

      // $FF: synthetic method
      public LauncherBackend org$apache$spark$launcher$LauncherBackend$BackendConnection$$$outer() {
         return this.$outer;
      }

      public BackendConnection(Socket s) {
         if (LauncherBackend.this == null) {
            throw null;
         } else {
            this.$outer = LauncherBackend.this;
            super(s);
         }
      }
   }
}
