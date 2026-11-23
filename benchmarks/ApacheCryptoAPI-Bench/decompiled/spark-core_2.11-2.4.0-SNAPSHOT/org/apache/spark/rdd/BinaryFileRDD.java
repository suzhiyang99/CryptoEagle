package org.apache.spark.rdd;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.task.JobContextImpl;
import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.input.StreamFileInputFormat;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u000194Q!\u0001\u0002\u0001\t)\u0011QBQ5oCJLh)\u001b7f%\u0012#%BA\u0002\u0005\u0003\r\u0011H\r\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sOV\u00111\u0002H\n\u0003\u00011\u0001B!\u0004\b\u001155\t!!\u0003\u0002\u0010\u0005\taa*Z<IC\u0012|w\u000e\u001d*E\tB\u0011\u0011c\u0006\b\u0003%Ui\u0011a\u0005\u0006\u0002)\u0005)1oY1mC&\u0011acE\u0001\u0007!J,G-\u001a4\n\u0005aI\"AB*ue&twM\u0003\u0002\u0017'A\u00111\u0004\b\u0007\u0001\t\u0015i\u0002A1\u0001 \u0005\u0005!6\u0001A\t\u0003A\r\u0002\"AE\u0011\n\u0005\t\u001a\"a\u0002(pi\"Lgn\u001a\t\u0003%\u0011J!!J\n\u0003\u0007\u0005s\u0017\u0010\u0003\u0005(\u0001\t\u0015\r\u0011\"\u0003)\u0003\t\u00198-F\u0001*!\tQ3&D\u0001\u0005\u0013\taCA\u0001\u0007Ta\u0006\u00148nQ8oi\u0016DH\u000f\u0003\u0005/\u0001\t\u0005\t\u0015!\u0003*\u0003\r\u00198\r\t\u0015\u0003[A\u0002\"AE\u0019\n\u0005I\u001a\"!\u0003;sC:\u001c\u0018.\u001a8u\u0011!!\u0004A!A!\u0002\u0013)\u0014\u0001E5oaV$hi\u001c:nCR\u001cE.Y:ta\t1$\bE\u0002\u0012oeJ!\u0001O\r\u0003\u000b\rc\u0017m]:\u0011\u0005mQD!C\u001e4\u0003\u0003\u0005\tQ!\u0001=\u0005\ryF%M\t\u0003Au\u00022AP!\u001b\u001b\u0005y$B\u0001!\u0005\u0003\u0015Ig\u000e];u\u0013\t\u0011uHA\u000bTiJ,\u0017-\u001c$jY\u0016Le\u000e];u\r>\u0014X.\u0019;\t\u0011\u0011\u0003!\u0011!Q\u0001\n\u0015\u000b\u0001b[3z\u00072\f7o\u001d\t\u0004#]\u0002\u0002\u0002C$\u0001\u0005\u0003\u0005\u000b\u0011\u0002%\u0002\u0015Y\fG.^3DY\u0006\u001c8\u000fE\u0002\u0012oiA\u0001B\u0013\u0001\u0003\u0002\u0003\u0006IaS\u0001\u0005G>tg\r\u0005\u0002M!6\tQJ\u0003\u0002K\u001d*\u0011qJB\u0001\u0007Q\u0006$wn\u001c9\n\u0005Ek%!D\"p]\u001aLw-\u001e:bi&|g\u000e\u0003\u0005T\u0001\t\u0005\t\u0015!\u0003U\u00035i\u0017N\u001c)beRLG/[8ogB\u0011!#V\u0005\u0003-N\u00111!\u00138u\u0011\u0015A\u0006\u0001\"\u0001Z\u0003\u0019a\u0014N\\5u}Q9!l\u0017/bE\u000e$\u0007cA\u0007\u00015!)qe\u0016a\u0001S!)Ag\u0016a\u0001;B\u0012a\f\u0019\t\u0004#]z\u0006CA\u000ea\t%YD,!A\u0001\u0002\u000b\u0005A\bC\u0003E/\u0002\u0007Q\tC\u0003H/\u0002\u0007\u0001\nC\u0003K/\u0002\u00071\nC\u0003T/\u0002\u0007A\u000bC\u0003g\u0001\u0011\u0005s-A\u0007hKR\u0004\u0016M\u001d;ji&|gn]\u000b\u0002QB\u0019!#[6\n\u0005)\u001c\"!B!se\u0006L\bC\u0001\u0016m\u0013\tiGAA\u0005QCJ$\u0018\u000e^5p]\u0002"
)
public class BinaryFileRDD<T> extends NewHadoopRDD<String, T> {
   private final transient SparkContext sc;
   private final Class<? extends StreamFileInputFormat<T>> inputFormatClass;
   private final int minPartitions;

   private SparkContext sc() {
      return this.sc;
   }

   public Partition[] getPartitions() {
      Configuration conf = this.getConf();
      conf.setIfUnset("mapreduce.input.fileinputformat.list-status.num-threads", BoxesRunTime.boxToInteger(Runtime.getRuntime().availableProcessors()).toString());
      StreamFileInputFormat inputFormat = (StreamFileInputFormat)this.inputFormatClass.newInstance();
      if (inputFormat instanceof Configurable) {
         ((Configurable)inputFormat).setConf(conf);
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else {
         BoxedUnit var9 = BoxedUnit.UNIT;
      }

      JobContextImpl jobContext = new JobContextImpl(conf, this.jobId());
      inputFormat.setMinPartitions(this.sc(), jobContext, this.minPartitions);
      Object[] rawSplits = inputFormat.getSplits(jobContext).toArray();
      Partition[] result = new Partition[Predef$.MODULE$.refArrayOps(rawSplits).size()];
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), Predef$.MODULE$.refArrayOps(rawSplits).size()).foreach$mVc$sp(new Serializable(this, rawSplits, result) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BinaryFileRDD $outer;
         private final Object[] rawSplits$1;
         private final Partition[] result$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.result$1[i] = new NewHadoopPartition(this.$outer.id(), i, (InputSplit)this.rawSplits$1[i]);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.rawSplits$1 = rawSplits$1;
               this.result$1 = result$1;
            }
         }
      });
      return result;
   }

   public BinaryFileRDD(SparkContext sc, Class<? extends StreamFileInputFormat<T>> inputFormatClass, Class<String> keyClass, Class<T> valueClass, Configuration conf, int minPartitions) {
      super(sc, inputFormatClass, keyClass, valueClass, conf);
      this.sc = sc;
      this.inputFormatClass = inputFormatClass;
      this.minPartitions = minPartitions;
   }
}
