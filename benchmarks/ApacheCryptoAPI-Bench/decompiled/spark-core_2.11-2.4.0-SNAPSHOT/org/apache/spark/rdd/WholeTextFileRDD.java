package org.apache.spark.rdd;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.task.JobContextImpl;
import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.input.WholeTextFileInputFormat;
import scala.Predef$;
import scala.Serializable;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u00054Q!\u0001\u0002\u0001\t)\u0011\u0001c\u00165pY\u0016$V\r\u001f;GS2,'\u000b\u0012#\u000b\u0005\r!\u0011a\u0001:eI*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0002\u0001\u0017A!A\"D\b\u0010\u001b\u0005\u0011\u0011B\u0001\b\u0003\u00051qUm\u001e%bI>|\u0007O\u0015#E!\t\u0001R#D\u0001\u0012\u0015\t\u00112#\u0001\u0002j_*\u0011ACB\u0001\u0007Q\u0006$wn\u001c9\n\u0005Y\t\"\u0001\u0002+fqRD\u0001\u0002\u0007\u0001\u0003\u0002\u0003\u0006IAG\u0001\u0003g\u000e\u001c\u0001\u0001\u0005\u0002\u001c95\tA!\u0003\u0002\u001e\t\ta1\u000b]1sW\u000e{g\u000e^3yi\"Aq\u0004\u0001B\u0001B\u0003%\u0001%\u0001\tj]B,HOR8s[\u0006$8\t\\1tgB\u0012\u0011%\f\t\u0004E!ZcBA\u0012'\u001b\u0005!#\"A\u0013\u0002\u000bM\u001c\u0017\r\\1\n\u0005\u001d\"\u0013A\u0002)sK\u0012,g-\u0003\u0002*U\t)1\t\\1tg*\u0011q\u0005\n\t\u0003Y5b\u0001\u0001B\u0005/=\u0005\u0005\t\u0011!B\u0001_\t\u0019q\fJ\u0019\u0012\u0005A\u001a\u0004CA\u00122\u0013\t\u0011DEA\u0004O_RD\u0017N\\4\u0011\u0005Q:T\"A\u001b\u000b\u0005Y\"\u0011!B5oaV$\u0018B\u0001\u001d6\u0005a9\u0006n\u001c7f)\u0016DHOR5mK&s\u0007/\u001e;G_Jl\u0017\r\u001e\u0005\tu\u0001\u0011\t\u0011)A\u0005w\u0005A1.Z=DY\u0006\u001c8\u000fE\u0002#Q=A\u0001\"\u0010\u0001\u0003\u0002\u0003\u0006IaO\u0001\u000bm\u0006dW/Z\"mCN\u001c\b\u0002C \u0001\u0005\u0003\u0005\u000b\u0011\u0002!\u0002\t\r|gN\u001a\t\u0003\u0003\u000ek\u0011A\u0011\u0006\u0003\u007fMI!\u0001\u0012\"\u0003\u001b\r{gNZ5hkJ\fG/[8o\u0011!1\u0005A!A!\u0002\u00139\u0015!D7j]B\u000b'\u000f^5uS>t7\u000f\u0005\u0002$\u0011&\u0011\u0011\n\n\u0002\u0004\u0013:$\b\"B&\u0001\t\u0003a\u0015A\u0002\u001fj]&$h\bF\u0004N\u001d>#VKV,\u0011\u00051\u0001\u0001\"\u0002\rK\u0001\u0004Q\u0002\"B\u0010K\u0001\u0004\u0001\u0006GA)T!\r\u0011\u0003F\u0015\t\u0003YM#\u0011BL(\u0002\u0002\u0003\u0005)\u0011A\u0018\t\u000biR\u0005\u0019A\u001e\t\u000buR\u0005\u0019A\u001e\t\u000b}R\u0005\u0019\u0001!\t\u000b\u0019S\u0005\u0019A$\t\u000be\u0003A\u0011\t.\u0002\u001b\u001d,G\u000fU1si&$\u0018n\u001c8t+\u0005Y\u0006cA\u0012]=&\u0011Q\f\n\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u00037}K!\u0001\u0019\u0003\u0003\u0013A\u000b'\u000f^5uS>t\u0007"
)
public class WholeTextFileRDD extends NewHadoopRDD<Text, Text> {
   private final Class<? extends WholeTextFileInputFormat> inputFormatClass;
   private final int minPartitions;

   public Partition[] getPartitions() {
      Configuration conf = this.getConf();
      conf.setIfUnset("mapreduce.input.fileinputformat.list-status.num-threads", BoxesRunTime.boxToInteger(Runtime.getRuntime().availableProcessors()).toString());
      WholeTextFileInputFormat inputFormat = (WholeTextFileInputFormat)this.inputFormatClass.newInstance();
      if (inputFormat != null) {
         inputFormat.setConf(conf);
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else {
         BoxedUnit var9 = BoxedUnit.UNIT;
      }

      JobContextImpl jobContext = new JobContextImpl(conf, this.jobId());
      inputFormat.setMinPartitions(jobContext, this.minPartitions);
      Object[] rawSplits = inputFormat.getSplits(jobContext).toArray();
      Partition[] result = new Partition[Predef$.MODULE$.refArrayOps(rawSplits).size()];
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), Predef$.MODULE$.refArrayOps(rawSplits).size()).foreach$mVc$sp(new Serializable(this, rawSplits, result) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final WholeTextFileRDD $outer;
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

   public WholeTextFileRDD(SparkContext sc, Class<? extends WholeTextFileInputFormat> inputFormatClass, Class<Text> keyClass, Class<Text> valueClass, Configuration conf, int minPartitions) {
      super(sc, inputFormatClass, keyClass, valueClass, conf);
      this.inputFormatClass = inputFormatClass;
      this.minPartitions = minPartitions;
   }
}
