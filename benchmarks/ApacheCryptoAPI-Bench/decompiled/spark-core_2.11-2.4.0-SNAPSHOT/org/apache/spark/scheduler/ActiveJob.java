package org.apache.spark.scheduler;

import java.util.Properties;
import org.apache.spark.util.CallSite;
import scala.Array$;
import scala.MatchError;
import scala.Serializable;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00194Q!\u0001\u0002\u0001\t)\u0011\u0011\"Q2uSZ,'j\u001c2\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fM\"A!\u0003\u0001BC\u0002\u0013\u0005A#A\u0003k_\nLEm\u0001\u0001\u0016\u0003U\u0001\"\u0001\u0004\f\n\u0005]i!aA%oi\"A\u0011\u0004\u0001B\u0001B\u0003%Q#\u0001\u0004k_\nLE\r\t\u0005\t7\u0001\u0011)\u0019!C\u00019\u0005Qa-\u001b8bYN#\u0018mZ3\u0016\u0003u\u0001\"AH\u0010\u000e\u0003\tI!\u0001\t\u0002\u0003\u000bM#\u0018mZ3\t\u0011\t\u0002!\u0011!Q\u0001\nu\t1BZ5oC2\u001cF/Y4fA!AA\u0005\u0001BC\u0002\u0013\u0005Q%\u0001\u0005dC2d7+\u001b;f+\u00051\u0003CA\u0014+\u001b\u0005A#BA\u0015\u0005\u0003\u0011)H/\u001b7\n\u0005-B#\u0001C\"bY2\u001c\u0016\u000e^3\t\u00115\u0002!\u0011!Q\u0001\n\u0019\n\u0011bY1mYNKG/\u001a\u0011\t\u0011=\u0002!Q1A\u0005\u0002A\n\u0001\u0002\\5ti\u0016tWM]\u000b\u0002cA\u0011aDM\u0005\u0003g\t\u00111BS8c\u0019&\u001cH/\u001a8fe\"AQ\u0007\u0001B\u0001B\u0003%\u0011'A\u0005mSN$XM\\3sA!Aq\u0007\u0001BC\u0002\u0013\u0005\u0001(\u0001\u0006qe>\u0004XM\u001d;jKN,\u0012!\u000f\t\u0003uyj\u0011a\u000f\u0006\u0003SqR\u0011!P\u0001\u0005U\u00064\u0018-\u0003\u0002@w\tQ\u0001K]8qKJ$\u0018.Z:\t\u0011\u0005\u0003!\u0011!Q\u0001\ne\n1\u0002\u001d:pa\u0016\u0014H/[3tA!)1\t\u0001C\u0001\t\u00061A(\u001b8jiz\"b!\u0012$H\u0011&S\u0005C\u0001\u0010\u0001\u0011\u0015\u0011\"\t1\u0001\u0016\u0011\u0015Y\"\t1\u0001\u001e\u0011\u0015!#\t1\u0001'\u0011\u0015y#\t1\u00012\u0011\u00159$\t1\u0001:\u0011\u001da\u0005A1A\u0005\u0002Q\tQB\\;n!\u0006\u0014H/\u001b;j_:\u001c\bB\u0002(\u0001A\u0003%Q#\u0001\bok6\u0004\u0016M\u001d;ji&|gn\u001d\u0011\t\u000fA\u0003!\u0019!C\u0001#\u0006Aa-\u001b8jg\",G-F\u0001S!\ra1+V\u0005\u0003)6\u0011Q!\u0011:sCf\u0004\"\u0001\u0004,\n\u0005]k!a\u0002\"p_2,\u0017M\u001c\u0005\u00073\u0002\u0001\u000b\u0011\u0002*\u0002\u0013\u0019Lg.[:iK\u0012\u0004\u0003bB.\u0001\u0001\u0004%\t\u0001F\u0001\f]Vlg)\u001b8jg\",G\rC\u0004^\u0001\u0001\u0007I\u0011\u00010\u0002\u001f9,XNR5oSNDW\rZ0%KF$\"a\u00182\u0011\u00051\u0001\u0017BA1\u000e\u0005\u0011)f.\u001b;\t\u000f\rd\u0016\u0011!a\u0001+\u0005\u0019\u0001\u0010J\u0019\t\r\u0015\u0004\u0001\u0015)\u0003\u0016\u00031qW/\u001c$j]&\u001c\b.\u001a3!\u0001"
)
public class ActiveJob {
   private final int jobId;
   private final Stage finalStage;
   private final CallSite callSite;
   private final JobListener listener;
   private final Properties properties;
   private final int numPartitions;
   private final boolean[] finished;
   private int numFinished;

   public int jobId() {
      return this.jobId;
   }

   public Stage finalStage() {
      return this.finalStage;
   }

   public CallSite callSite() {
      return this.callSite;
   }

   public JobListener listener() {
      return this.listener;
   }

   public Properties properties() {
      return this.properties;
   }

   public int numPartitions() {
      return this.numPartitions;
   }

   public boolean[] finished() {
      return this.finished;
   }

   public int numFinished() {
      return this.numFinished;
   }

   public void numFinished_$eq(int x$1) {
      this.numFinished = x$1;
   }

   public ActiveJob(int jobId, Stage finalStage, CallSite callSite, JobListener listener, Properties properties) {
      this.jobId = jobId;
      this.finalStage = finalStage;
      this.callSite = callSite;
      this.listener = listener;
      this.properties = properties;
      int var8;
      if (finalStage instanceof ResultStage) {
         ResultStage var7 = (ResultStage)finalStage;
         var8 = var7.partitions().length;
      } else {
         if (!(finalStage instanceof ShuffleMapStage)) {
            throw new MatchError(finalStage);
         }

         ShuffleMapStage var9 = (ShuffleMapStage)finalStage;
         var8 = var9.rdd().partitions().length;
      }

      this.numPartitions = var8;
      this.finished = (boolean[])Array$.MODULE$.fill(this.numPartitions(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply() {
            return this.apply$mcZ$sp();
         }

         public boolean apply$mcZ$sp() {
            return false;
         }
      }, ClassTag$.MODULE$.Boolean());
      this.numFinished = 0;
   }
}
