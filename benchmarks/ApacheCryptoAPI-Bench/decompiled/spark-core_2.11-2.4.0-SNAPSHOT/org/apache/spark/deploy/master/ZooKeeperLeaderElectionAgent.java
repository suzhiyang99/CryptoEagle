package org.apache.spark.deploy.master;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkCuratorUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Enumeration;
import scala.Function0;
import scala.Serializable;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mb!B\u0001\u0003\u0001\ta!\u0001\b.p_.+W\r]3s\u0019\u0016\fG-\u001a:FY\u0016\u001cG/[8o\u0003\u001e,g\u000e\u001e\u0006\u0003\u0007\u0011\ta!\\1ti\u0016\u0014(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0003\u0001\u001bU\tS\u0005\u0005\u0002\u000f'5\tqB\u0003\u0002\u0011#\u0005!A.\u00198h\u0015\u0005\u0011\u0012\u0001\u00026bm\u0006L!\u0001F\b\u0003\r=\u0013'.Z2u!\t1r$D\u0001\u0018\u0015\tA\u0012$\u0001\u0004mK\u0006$WM\u001d\u0006\u00035m\tqA]3dSB,7O\u0003\u0002\u001d;\u0005IaM]1nK^|'o\u001b\u0006\u0003=!\tqaY;sCR|'/\u0003\u0002!/\t\u0019B*Z1eKJd\u0015\r^2i\u0019&\u001cH/\u001a8feB\u0011!eI\u0007\u0002\u0005%\u0011AE\u0001\u0002\u0014\u0019\u0016\fG-\u001a:FY\u0016\u001cG/[8o\u0003\u001e,g\u000e\u001e\t\u0003M%j\u0011a\n\u0006\u0003Q\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003U\u001d\u0012q\u0001T8hO&tw\r\u0003\u0005-\u0001\t\u0015\r\u0011\"\u0001/\u00039i\u0017m\u001d;fe&s7\u000f^1oG\u0016\u001c\u0001!F\u00010!\t\u0011\u0003'\u0003\u00022\u0005\tyA*Z1eKJ,E.Z2uC\ndW\r\u0003\u00054\u0001\t\u0005\t\u0015!\u00030\u0003=i\u0017m\u001d;fe&s7\u000f^1oG\u0016\u0004\u0003\u0002C\u001b\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001c\u0002\t\r|gN\u001a\t\u0003oaj\u0011AB\u0005\u0003s\u0019\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u000bm\u0002A\u0011\u0001\u001f\u0002\rqJg.\u001b;?)\ridh\u0010\t\u0003E\u0001AQ\u0001\f\u001eA\u0002=BQ!\u000e\u001eA\u0002YBq!\u0011\u0001C\u0002\u0013\u0005!)A\u0006X\u001fJ[\u0015JT$`\t&\u0013V#A\"\u0011\u00059!\u0015BA#\u0010\u0005\u0019\u0019FO]5oO\"1q\t\u0001Q\u0001\n\r\u000bAbV(S\u0017&sui\u0018#J%\u0002B\u0011\"\u0013\u0001A\u0002\u0003\u0007I\u0011\u0002&\u0002\u0005i\\W#A&\u0011\u00051kU\"A\u000e\n\u00059[\"\u0001E\"ve\u0006$xN\u001d$sC6,wo\u001c:l\u0011%\u0001\u0006\u00011AA\u0002\u0013%\u0011+\u0001\u0004{W~#S-\u001d\u000b\u0003%b\u0003\"a\u0015,\u000e\u0003QS\u0011!V\u0001\u0006g\u000e\fG.Y\u0005\u0003/R\u0013A!\u00168ji\"9\u0011lTA\u0001\u0002\u0004Y\u0015a\u0001=%c!11\f\u0001Q!\n-\u000b1A_6!\u0011%i\u0006\u00011AA\u0002\u0013%a,A\u0006mK\u0006$WM\u001d'bi\u000eDW#A0\u0011\u0005Y\u0001\u0017BA1\u0018\u0005-aU-\u00193fe2\u000bGo\u00195\t\u0013\r\u0004\u0001\u0019!a\u0001\n\u0013!\u0017a\u00047fC\u0012,'\u000fT1uG\"|F%Z9\u0015\u0005I+\u0007bB-c\u0003\u0003\u0005\ra\u0018\u0005\u0007O\u0002\u0001\u000b\u0015B0\u0002\u00191,\u0017\rZ3s\u0019\u0006$8\r\u001b\u0011\t\u000f%\u0004\u0001\u0019!C\u0005U\u000611\u000f^1ukN,\u0012a\u001b\t\u0003Yrt!!\u001c8\u000e\u0003\u00019Qa\u001c\u0001\t\nA\f\u0001\u0003T3bI\u0016\u00148\u000f[5q'R\fG/^:\u0011\u00055\fh!\u0002:\u0001\u0011\u0013\u0019(\u0001\u0005'fC\u0012,'o\u001d5jaN#\u0018\r^;t'\t\tH\u000f\u0005\u0002Tk&\u0011a\u000f\u0016\u0002\f\u000b:,X.\u001a:bi&|g\u000eC\u0003<c\u0012\u0005\u0001\u0010F\u0001q\u000b\u0011\u0011\u0018\u000f\u0001>\u0011\u0005mdX\"A9\n\u0005u,(!\u0002,bYV,\u0007\u0002C@r\u0005\u0004%\t!!\u0001\u0002\r1+\u0015\tR#S+\u0005Q\bbBA\u0003c\u0002\u0006IA_\u0001\b\u0019\u0016\u000bE)\u0012*!\u0011%\tI!\u001db\u0001\n\u0003\t\t!\u0001\u0006O\u001fR{F*R!E\u000bJCq!!\u0004rA\u0003%!0A\u0006O\u001fR{F*R!E\u000bJ\u0003\u0003\"CA\t\u0001\u0001\u0007I\u0011BA\n\u0003)\u0019H/\u0019;vg~#S-\u001d\u000b\u0004%\u0006U\u0001\u0002C-\u0002\u0010\u0005\u0005\t\u0019A6\t\u000f\u0005e\u0001\u0001)Q\u0005W\u000691\u000f^1ukN\u0004\u0003bBA\u000f\u0001\u0011%\u0011qD\u0001\u0006gR\f'\u000f\u001e\u000b\u0002%\"9\u00111\u0005\u0001\u0005B\u0005}\u0011\u0001B:u_BDq!a\n\u0001\t\u0003\ny\"\u0001\u0005jg2+\u0017\rZ3s\u0011\u001d\tY\u0003\u0001C!\u0003?\t\u0011B\\8u\u0019\u0016\fG-\u001a:\t\u000f\u0005=\u0002\u0001\"\u0003\u00022\u00051R\u000f\u001d3bi\u0016dU-\u00193feND\u0017\u000e]*uCR,8\u000fF\u0002S\u0003gA\u0001\"a\n\u0002.\u0001\u0007\u0011Q\u0007\t\u0004'\u0006]\u0012bAA\u001d)\n9!i\\8mK\u0006t\u0007"
)
public class ZooKeeperLeaderElectionAgent implements LeaderLatchListener, LeaderElectionAgent, Logging {
   private final LeaderElectable masterInstance;
   private final SparkConf conf;
   private final String WORKING_DIR;
   private CuratorFramework zk;
   private LeaderLatch leaderLatch;
   private Enumeration.Value status;
   private volatile LeadershipStatus$ LeadershipStatus$module;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   private LeadershipStatus$ LeadershipStatus$lzycompute() {
      synchronized(this) {
         if (this.LeadershipStatus$module == null) {
            this.LeadershipStatus$module = new LeadershipStatus$();
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      Object var3 = null;
      return this.LeadershipStatus$module;
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public LeaderElectable masterInstance() {
      return this.masterInstance;
   }

   public String WORKING_DIR() {
      return this.WORKING_DIR;
   }

   private CuratorFramework zk() {
      return this.zk;
   }

   private void zk_$eq(CuratorFramework x$1) {
      this.zk = x$1;
   }

   private LeaderLatch leaderLatch() {
      return this.leaderLatch;
   }

   private void leaderLatch_$eq(LeaderLatch x$1) {
      this.leaderLatch = x$1;
   }

   private Enumeration.Value status() {
      return this.status;
   }

   private void status_$eq(Enumeration.Value x$1) {
      this.status = x$1;
   }

   private void start() {
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Starting ZooKeeper LeaderElection agent";
         }
      });
      this.zk_$eq(SparkCuratorUtil$.MODULE$.newClient(this.conf, SparkCuratorUtil$.MODULE$.newClient$default$2()));
      this.leaderLatch_$eq(new LeaderLatch(this.zk(), this.WORKING_DIR()));
      this.leaderLatch().addListener(this);
      this.leaderLatch().start();
   }

   public void stop() {
      this.leaderLatch().close();
      this.zk().close();
   }

   public synchronized void isLeader() {
      if (this.leaderLatch().hasLeadership()) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "We have gained leadership";
            }
         });
         this.updateLeadershipStatus(true);
      }
   }

   public synchronized void notLeader() {
      if (!this.leaderLatch().hasLeadership()) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "We have lost leadership";
            }
         });
         this.updateLeadershipStatus(false);
      }
   }

   private void updateLeadershipStatus(boolean isLeader) {
      label40: {
         if (isLeader) {
            Enumeration.Value var10000 = this.status();
            Enumeration.Value var2 = this.LeadershipStatus().NOT_LEADER();
            if (var10000 == null) {
               if (var2 == null) {
                  break label40;
               }
            } else if (var10000.equals(var2)) {
               break label40;
            }
         }

         if (!isLeader) {
            Enumeration.Value var4 = this.status();
            Enumeration.Value var3 = this.LeadershipStatus().LEADER();
            if (var4 == null) {
               if (var3 != null) {
                  return;
               }
            } else if (!var4.equals(var3)) {
               return;
            }

            this.status_$eq(this.LeadershipStatus().NOT_LEADER());
            this.masterInstance().revokedLeadership();
         }

         return;
      }

      this.status_$eq(this.LeadershipStatus().LEADER());
      this.masterInstance().electedLeader();
   }

   private LeadershipStatus$ LeadershipStatus() {
      return this.LeadershipStatus$module == null ? this.LeadershipStatus$lzycompute() : this.LeadershipStatus$module;
   }

   public ZooKeeperLeaderElectionAgent(LeaderElectable masterInstance, SparkConf conf) {
      this.masterInstance = masterInstance;
      this.conf = conf;
      LeaderElectionAgent$class.$init$(this);
      Logging$class.$init$(this);
      this.WORKING_DIR = (new StringBuilder()).append((Object)conf.get("spark.deploy.zookeeper.dir", "/spark")).append((Object)"/leader_election").toString();
      this.status = this.LeadershipStatus().NOT_LEADER();
      this.start();
   }

   public class LeadershipStatus$ extends Enumeration {
      private final Enumeration.Value LEADER = this.Value();
      private final Enumeration.Value NOT_LEADER = this.Value();

      public Enumeration.Value LEADER() {
         return this.LEADER;
      }

      public Enumeration.Value NOT_LEADER() {
         return this.NOT_LEADER;
      }
   }
}
