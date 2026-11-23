package org.apache.spark.scheduler;

import org.apache.spark.MapOutputTrackerMaster;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.rdd.RDD;
import org.apache.spark.util.CallSite;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Range;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}d!B\u0001\u0003\u0001\u0011Q!aD*ik\u001a4G.Z'baN#\u0018mZ3\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taQ\"D\u0001\u0003\u0013\tq!AA\u0003Ti\u0006<W\rC\u0005\u0011\u0001\t\u0005\t\u0015!\u0003\u00131\u0005\u0011\u0011\u000eZ\u0002\u0001!\t\u0019b#D\u0001\u0015\u0015\u0005)\u0012!B:dC2\f\u0017BA\f\u0015\u0005\rIe\u000e^\u0005\u0003!5A\u0011B\u0007\u0001\u0003\u0002\u0003\u0006Ia\u0007\u0017\u0002\u0007I$G\r\r\u0002\u001dGA\u0019QdH\u0011\u000e\u0003yQ!A\u0007\u0003\n\u0005\u0001r\"a\u0001*E\tB\u0011!e\t\u0007\u0001\t%!\u0013$!A\u0001\u0002\u000b\u0005QEA\u0002`IE\n\"AJ\u0015\u0011\u0005M9\u0013B\u0001\u0015\u0015\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"a\u0005\u0016\n\u0005-\"\"aA!os&\u0011!$\u0004\u0005\n]\u0001\u0011\t\u0011)A\u0005%=\n\u0001B\\;n)\u0006\u001c8n]\u0005\u0003]5A\u0011\"\r\u0001\u0003\u0002\u0003\u0006IA\r \u0002\u000fA\f'/\u001a8ugB\u00191gO\u0006\u000f\u0005QJdBA\u001b9\u001b\u00051$BA\u001c\u0012\u0003\u0019a$o\\8u}%\tQ#\u0003\u0002;)\u00059\u0001/Y2lC\u001e,\u0017B\u0001\u001f>\u0005\u0011a\u0015n\u001d;\u000b\u0005i\"\u0012BA\u0019\u000e\u0011%\u0001\u0005A!A!\u0002\u0013\u0011\u0012)\u0001\u0006gSJ\u001cHOS8c\u0013\u0012L!\u0001Q\u0007\t\u0013\r\u0003!\u0011!Q\u0001\n\u0011S\u0015\u0001C2bY2\u001c\u0016\u000e^3\u0011\u0005\u0015CU\"\u0001$\u000b\u0005\u001d#\u0011\u0001B;uS2L!!\u0013$\u0003\u0011\r\u000bG\u000e\\*ji\u0016L!aQ\u0007\t\u00111\u0003!Q1A\u0005\u00025\u000b!b\u001d5vM\u001adW\rR3q+\u0005q\u0005\u0007B(UG\u0016\u0004R\u0001U)TE\u0012l\u0011\u0001B\u0005\u0003%\u0012\u0011\u0011c\u00155vM\u001adW\rR3qK:$WM\\2z!\t\u0011C\u000bB\u0005V-\u0006\u0005\t\u0011!B\u0001K\t\u0019q\f\n\u001a\t\u0011]\u0003!\u0011!Q\u0001\na\u000b1b\u001d5vM\u001adW\rR3qAA\"\u0011lW/a!\u0015\u0001\u0016K\u0017/`!\t\u00113\fB\u0005V-\u0006\u0005\t\u0011!B\u0001KA\u0011!%\u0018\u0003\n=Z\u000b\t\u0011!A\u0003\u0002\u0015\u00121a\u0018\u00134!\t\u0011\u0003\rB\u0005b-\u0006\u0005\t\u0011!B\u0001K\t\u0019q\f\n\u001b\u0011\u0005\t\u001aG!\u00030W\u0003\u0003\u0005\tQ!\u0001&!\t\u0011S\rB\u0005b-\u0006\u0005\t\u0011!B\u0001K!Aq\r\u0001B\u0001B\u0003%\u0001.\u0001\fnCB|U\u000f\u001e9viR\u0013\u0018mY6fe6\u000b7\u000f^3s!\t\u0001\u0016.\u0003\u0002k\t\t1R*\u00199PkR\u0004X\u000f\u001e+sC\u000e\\WM]'bgR,'\u000fC\u0003m\u0001\u0011\u0005Q.\u0001\u0004=S:LGO\u0010\u000b\u000b]>\u0004XO^<ys\u0006\u0015\u0001C\u0001\u0007\u0001\u0011\u0015\u00012\u000e1\u0001\u0013\u0011\u0015Q2\u000e1\u0001ra\t\u0011H\u000fE\u0002\u001e?M\u0004\"A\t;\u0005\u0013\u0011\u0002\u0018\u0011!A\u0001\u0006\u0003)\u0003\"\u0002\u0018l\u0001\u0004\u0011\u0002\"B\u0019l\u0001\u0004\u0011\u0004\"\u0002!l\u0001\u0004\u0011\u0002\"B\"l\u0001\u0004!\u0005\"\u0002'l\u0001\u0004Q\b'B>~\u007f\u0006\r\u0001C\u0002)Ryz\f\t\u0001\u0005\u0002#{\u0012IQ+_A\u0001\u0002\u0003\u0015\t!\n\t\u0003E}$\u0011BX=\u0002\u0002\u0003\u0005)\u0011A\u0013\u0011\u0007\t\n\u0019\u0001B\u0005bs\u0006\u0005\t\u0011!B\u0001K!)qm\u001ba\u0001Q\"A\u0011\u0011\u0002\u0001!B\u0013\tY!A\u0007`[\u0006\u00048\u000b^1hK*{'m\u001d\t\u0005gm\ni\u0001E\u0002\r\u0003\u001fI1!!\u0005\u0003\u0005%\t5\r^5wK*{'\rC\u0005\u0002\u0016\u0001\u0011\r\u0011\"\u0001\u0002\u0018\u0005\t\u0002/\u001a8eS:<\u0007+\u0019:uSRLwN\\:\u0016\u0005\u0005e\u0001#BA\u000e\u0003K\u0011RBAA\u000f\u0015\u0011\ty\"!\t\u0002\u000f5,H/\u00192mK*\u0019\u00111\u0005\u000b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0002(\u0005u!a\u0002%bg\"\u001cV\r\u001e\u0005\t\u0003W\u0001\u0001\u0015!\u0003\u0002\u001a\u0005\u0011\u0002/\u001a8eS:<\u0007+\u0019:uSRLwN\\:!\u0011\u001d\ty\u0003\u0001C!\u0003c\t\u0001\u0002^8TiJLgn\u001a\u000b\u0003\u0003g\u0001B!!\u000e\u0002<9\u00191#a\u000e\n\u0007\u0005eB#\u0001\u0004Qe\u0016$WMZ\u0005\u0005\u0003{\tyD\u0001\u0004TiJLgn\u001a\u0006\u0004\u0003s!\u0002bBA\"\u0001\u0011\u0005\u0011QI\u0001\r[\u0006\u00048\u000b^1hK*{'m]\u000b\u0003\u0003\u000f\u0002RaMA%\u0003\u001bI1!a\u0013>\u0005\r\u0019V-\u001d\u0005\b\u0003\u001f\u0002A\u0011AA)\u00031\tG\rZ!di&4XMS8c)\u0011\t\u0019&!\u0017\u0011\u0007M\t)&C\u0002\u0002XQ\u0011A!\u00168ji\"A\u00111LA'\u0001\u0004\ti!A\u0002k_\nDq!a\u0018\u0001\t\u0003\t\t'A\bsK6|g/Z!di&4XMS8c)\u0011\t\u0019&a\u0019\t\u0011\u0005m\u0013Q\fa\u0001\u0003\u001bAq!a\u001a\u0001\t\u0003\tI'A\nok6\fe/Y5mC\ndWmT;uaV$8/F\u0001\u0013\u0011\u001d\ti\u0007\u0001C\u0001\u0003_\n1\"[:Bm\u0006LG.\u00192mKV\u0011\u0011\u0011\u000f\t\u0004'\u0005M\u0014bAA;)\t9!i\\8mK\u0006t\u0007bBA=\u0001\u0011\u0005\u00131P\u0001\u0016M&tG-T5tg&tw\rU1si&$\u0018n\u001c8t)\t\ti\b\u0005\u00034\u0003\u0013\u0012\u0002"
)
public class ShuffleMapStage extends Stage {
   private final ShuffleDependency<?, ?, ?> shuffleDep;
   private final MapOutputTrackerMaster mapOutputTrackerMaster;
   private List<ActiveJob> _mapStageJobs;
   private final HashSet<Object> pendingPartitions;

   public ShuffleDependency<?, ?, ?> shuffleDep() {
      return this.shuffleDep;
   }

   public HashSet<Object> pendingPartitions() {
      return this.pendingPartitions;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"ShuffleMapStage ").append((Object)BoxesRunTime.boxToInteger(super.id())).toString();
   }

   public Seq<ActiveJob> mapStageJobs() {
      return this._mapStageJobs;
   }

   public void addActiveJob(ActiveJob job) {
      this._mapStageJobs = this._mapStageJobs.<ActiveJob>$colon$colon(job);
   }

   public void removeActiveJob(ActiveJob job) {
      this._mapStageJobs = (List)this._mapStageJobs.filter(new Serializable(this, job) {
         public static final long serialVersionUID = 0L;
         private final ActiveJob job$1;

         public final boolean apply(ActiveJob x$2) {
            boolean var10000;
            label23: {
               ActiveJob var2 = this.job$1;
               if (x$2 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (x$2.equals(var2)) {
                  break label23;
               }

               var10000 = true;
               return var10000;
            }

            var10000 = false;
            return var10000;
         }

         public {
            this.job$1 = job$1;
         }
      });
   }

   public int numAvailableOutputs() {
      return this.mapOutputTrackerMaster.getNumAvailableOutputs(this.shuffleDep().shuffleId());
   }

   public boolean isAvailable() {
      return this.numAvailableOutputs() == this.numPartitions();
   }

   public Seq<Object> findMissingPartitions() {
      return (Seq)this.mapOutputTrackerMaster.findMissingPartitions(this.shuffleDep().shuffleId()).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleMapStage $outer;

         public final Range apply() {
            return RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.$outer.numPartitions());
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

   public ShuffleMapStage(int id, RDD<?> rdd, int numTasks, List<Stage> parents, int firstJobId, CallSite callSite, ShuffleDependency<?, ?, ?> shuffleDep, MapOutputTrackerMaster mapOutputTrackerMaster) {
      super(id, rdd, numTasks, parents, firstJobId, callSite);
      this.shuffleDep = shuffleDep;
      this.mapOutputTrackerMaster = mapOutputTrackerMaster;
      this._mapStageJobs = Nil$.MODULE$;
      this.pendingPartitions = new HashSet<Object>();
   }
}
