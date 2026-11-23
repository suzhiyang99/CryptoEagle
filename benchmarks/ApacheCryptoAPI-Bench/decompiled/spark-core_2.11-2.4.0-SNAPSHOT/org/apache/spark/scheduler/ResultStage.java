package org.apache.spark.scheduler;

import org.apache.spark.TaskContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.util.CallSite;
import scala.Function2;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.List;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ec!B\u0001\u0003\u0001\u0011Q!a\u0003*fgVdGo\u0015;bO\u0016T!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u000e\u001b\u0005\u0011\u0011B\u0001\b\u0003\u0005\u0015\u0019F/Y4f\u0011%\u0001\u0002A!A!\u0002\u0013\u0011\u0002$\u0001\u0002jI\u000e\u0001\u0001CA\n\u0017\u001b\u0005!\"\"A\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005]!\"aA%oi&\u0011\u0001#\u0004\u0005\n5\u0001\u0011\t\u0011)A\u000571\n1A\u001d3ea\ta2\u0005E\u0002\u001e?\u0005j\u0011A\b\u0006\u00035\u0011I!\u0001\t\u0010\u0003\u0007I#E\t\u0005\u0002#G1\u0001A!\u0003\u0013\u001a\u0003\u0003\u0005\tQ!\u0001&\u0005\ryF%M\t\u0003M%\u0002\"aE\u0014\n\u0005!\"\"a\u0002(pi\"Lgn\u001a\t\u0003')J!a\u000b\u000b\u0003\u0007\u0005s\u00170\u0003\u0002\u001b\u001b!Aa\u0006\u0001BC\u0002\u0013\u0005q&\u0001\u0003gk:\u001cW#\u0001\u00191\u0005E\"\u0006#B\n3ia\u001a\u0016BA\u001a\u0015\u0005%1UO\\2uS>t'\u0007\u0005\u00026m5\tA!\u0003\u00028\t\tYA+Y:l\u0007>tG/\u001a=ua\tId\tE\u0002;\u0005\u0016s!a\u000f!\u000f\u0005qzT\"A\u001f\u000b\u0005y\n\u0012A\u0002\u001fs_>$h(C\u0001\u0016\u0013\t\tE#A\u0004qC\u000e\\\u0017mZ3\n\u0005\r#%\u0001C%uKJ\fGo\u001c:\u000b\u0005\u0005#\u0002C\u0001\u0012G\t%9\u0005*!A\u0001\u0002\u000b\u0005QEA\u0002`IIB\u0001\"\u0013\u0001\u0003\u0002\u0003\u0006IAS\u0001\u0006MVt7\r\t\u0019\u0003\u0017F\u0003Ra\u0005\u001a5\u0019B\u0003$!T(\u0011\u0007i\u0012e\n\u0005\u0002#\u001f\u0012Iq\tSA\u0001\u0002\u0003\u0015\t!\n\t\u0003EE#\u0011B\u0015%\u0002\u0002\u0003\u0005)\u0011A\u0013\u0003\u0007}#3\u0007\u0005\u0002#)\u0012I!\u000bSA\u0001\u0002\u0003\u0015\t!\n\u0005\t-\u0002\u0011)\u0019!C\u0001/\u0006Q\u0001/\u0019:uSRLwN\\:\u0016\u0003a\u00032aE-\u0013\u0013\tQFCA\u0003BeJ\f\u0017\u0010\u0003\u0005]\u0001\t\u0005\t\u0015!\u0003Y\u0003-\u0001\u0018M\u001d;ji&|gn\u001d\u0011\t\u0013y\u0003!\u0011!Q\u0001\n}\u0013\u0017a\u00029be\u0016tGo\u001d\t\u0004u\u0001\\\u0011BA1E\u0005\u0011a\u0015n\u001d;\n\u0005yk\u0001\"\u00033\u0001\u0005\u0003\u0005\u000b\u0011\u0002\nf\u0003)1\u0017N]:u\u0015>\u0014\u0017\nZ\u0005\u0003I6A\u0011b\u001a\u0001\u0003\u0002\u0003\u0006I\u0001\u001b8\u0002\u0011\r\fG\u000e\\*ji\u0016\u0004\"!\u001b7\u000e\u0003)T!a\u001b\u0003\u0002\tU$\u0018\u000e\\\u0005\u0003[*\u0014\u0001bQ1mYNKG/Z\u0005\u0003O6AQ\u0001\u001d\u0001\u0005\u0002E\fa\u0001P5oSRtD\u0003\u0004:tif\f)!a\u0002\u0002\n\u0005-\u0001C\u0001\u0007\u0001\u0011\u0015\u0001r\u000e1\u0001\u0013\u0011\u0015Qr\u000e1\u0001va\t1\b\u0010E\u0002\u001e?]\u0004\"A\t=\u0005\u0013\u0011\"\u0018\u0011!A\u0001\u0006\u0003)\u0003\"\u0002\u0018p\u0001\u0004Q\bgA>\u0002\u0004A11C\r\u001b}\u0003\u0003\u0001$!`@\u0011\u0007i\u0012e\u0010\u0005\u0002#\u007f\u0012Iq)_A\u0001\u0002\u0003\u0015\t!\n\t\u0004E\u0005\rA!\u0003*z\u0003\u0003\u0005\tQ!\u0001&\u0011\u00151v\u000e1\u0001Y\u0011\u0015qv\u000e1\u0001`\u0011\u0015!w\u000e1\u0001\u0013\u0011\u00159w\u000e1\u0001i\u0011!\ty\u0001\u0001Q!\n\u0005E\u0011AC0bGRLg/\u001a&pEB)1#a\u0005\u0002\u0018%\u0019\u0011Q\u0003\u000b\u0003\r=\u0003H/[8o!\ra\u0011\u0011D\u0005\u0004\u00037\u0011!!C!di&4XMS8c\u0011\u001d\ty\u0002\u0001C\u0001\u0003C\t\u0011\"Y2uSZ,'j\u001c2\u0016\u0005\u0005E\u0001bBA\u0013\u0001\u0011\u0005\u0011qE\u0001\rg\u0016$\u0018i\u0019;jm\u0016TuN\u0019\u000b\u0005\u0003S\ty\u0003E\u0002\u0014\u0003WI1!!\f\u0015\u0005\u0011)f.\u001b;\t\u0011\u0005E\u00121\u0005a\u0001\u0003/\t1A[8c\u0011\u001d\t)\u0004\u0001C\u0001\u0003o\tqB]3n_Z,\u0017i\u0019;jm\u0016TuN\u0019\u000b\u0003\u0003SAq!a\u000f\u0001\t\u0003\ni$A\u000bgS:$W*[:tS:<\u0007+\u0019:uSRLwN\\:\u0015\u0005\u0005}\u0002\u0003\u0002\u001e\u0002BII1!a\u0011E\u0005\r\u0019V-\u001d\u0005\b\u0003\u000f\u0002A\u0011IA%\u0003!!xn\u0015;sS:<GCAA&!\u0011\ti%a\u0015\u000f\u0007M\ty%C\u0002\u0002RQ\ta\u0001\u0015:fI\u00164\u0017\u0002BA+\u0003/\u0012aa\u0015;sS:<'bAA))\u0001"
)
public class ResultStage extends Stage {
   private final Function2<TaskContext, Iterator<?>, ?> func;
   private final int[] partitions;
   private Option<ActiveJob> _activeJob;

   public Function2<TaskContext, Iterator<?>, ?> func() {
      return this.func;
   }

   public int[] partitions() {
      return this.partitions;
   }

   public Option<ActiveJob> activeJob() {
      return this._activeJob;
   }

   public void setActiveJob(ActiveJob job) {
      this._activeJob = Option$.MODULE$.<ActiveJob>apply(job);
   }

   public void removeActiveJob() {
      this._activeJob = None$.MODULE$;
   }

   public Seq<Object> findMissingPartitions() {
      ActiveJob job = (ActiveJob)this.activeJob().get();
      return (Seq)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), job.numPartitions()).filter(new Serializable(this, job) {
         public static final long serialVersionUID = 0L;
         private final ActiveJob job$1;

         public final boolean apply(int id) {
            return this.apply$mcZI$sp(id);
         }

         public boolean apply$mcZI$sp(int id) {
            return !this.job$1.finished()[id];
         }

         public {
            this.job$1 = job$1;
         }
      });
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"ResultStage ").append((Object)BoxesRunTime.boxToInteger(super.id())).toString();
   }

   public ResultStage(int id, RDD<?> rdd, Function2<TaskContext, Iterator<?>, ?> func, int[] partitions, List<Stage> parents, int firstJobId, CallSite callSite) {
      super(id, rdd, partitions.length, parents, firstJobId, callSite);
      this.func = func;
      this.partitions = partitions;
      this._activeJob = None$.MODULE$;
   }
}
