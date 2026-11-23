package org.apache.spark.input;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import scala.Serializable;
import scala.collection.JavaConverters$;
import scala.collection.TraversableOnce;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Buffer$;
import scala.math.Numeric;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001]3Q!\u0001\u0002\u0001\t)\u0011\u0001d\u00165pY\u0016$V\r\u001f;GS2,\u0017J\u001c9vi\u001a{'/\\1u\u0015\t\u0019A!A\u0003j]B,HO\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\r\u00011\u0002\b\t\u0005\u0019Q1b#D\u0001\u000e\u0015\t\u0019aB\u0003\u0002\u0010!\u0005\u0019A.\u001b2\u000b\u0005E\u0011\u0012!C7baJ,G-^2f\u0015\t\u0019b!\u0001\u0004iC\u0012|w\u000e]\u0005\u0003+5\u0011acQ8nE&tWMR5mK&s\u0007/\u001e;G_Jl\u0017\r\u001e\t\u0003/ii\u0011\u0001\u0007\u0006\u00033I\t!![8\n\u0005mA\"\u0001\u0002+fqR\u0004\"!\b\u0010\u000e\u0003\tI!a\b\u0002\u0003\u0019\r{gNZ5hkJ\f'\r\\3\t\u000b\u0005\u0002A\u0011A\u0012\u0002\rqJg.\u001b;?\u0007\u0001!\u0012\u0001\n\t\u0003;\u0001AQA\n\u0001\u0005R\u001d\n1\"[:Ta2LG/\u00192mKR\u0019\u0001F\f\u001b\u0011\u0005%bS\"\u0001\u0016\u000b\u0003-\nQa]2bY\u0006L!!\f\u0016\u0003\u000f\t{w\u000e\\3b]\")q&\na\u0001a\u000591m\u001c8uKb$\bCA\u00193\u001b\u0005\u0001\u0012BA\u001a\u0011\u0005)QuNY\"p]R,\u0007\u0010\u001e\u0005\u0006k\u0015\u0002\rAN\u0001\u0005M&dW\r\u0005\u00028u5\t\u0001H\u0003\u0002:%\u0005\u0011am]\u0005\u0003wa\u0012A\u0001U1uQ\")Q\b\u0001C!}\u0005\u00112M]3bi\u0016\u0014VmY8sIJ+\u0017\rZ3s)\ry$i\u0012\t\u0005c\u00013b#\u0003\u0002B!\ta!+Z2pe\u0012\u0014V-\u00193fe\")1\t\u0010a\u0001\t\u0006)1\u000f\u001d7jiB\u0011\u0011'R\u0005\u0003\rB\u0011!\"\u00138qkR\u001c\u0006\u000f\\5u\u0011\u0015yC\b1\u0001I!\t\t\u0014*\u0003\u0002K!\t\u0011B+Y:l\u0003R$X-\u001c9u\u0007>tG/\u001a=u\u0011\u0015a\u0005\u0001\"\u0001N\u0003A\u0019X\r^'j]B\u000b'\u000f^5uS>t7\u000fF\u0002O#J\u0003\"!K(\n\u0005AS#\u0001B+oSRDQaL&A\u0002ABQaU&A\u0002Q\u000bQ\"\\5o!\u0006\u0014H/\u001b;j_:\u001c\bCA\u0015V\u0013\t1&FA\u0002J]R\u0004"
)
public class WholeTextFileInputFormat extends CombineFileInputFormat<Text, Text> implements Configurable {
   private Configuration org$apache$spark$input$Configurable$$conf;

   public Configuration org$apache$spark$input$Configurable$$conf() {
      return this.org$apache$spark$input$Configurable$$conf;
   }

   public void org$apache$spark$input$Configurable$$conf_$eq(Configuration x$1) {
      this.org$apache$spark$input$Configurable$$conf = x$1;
   }

   public void setConf(Configuration c) {
      Configurable$class.setConf(this, c);
   }

   public Configuration getConf() {
      return Configurable$class.getConf(this);
   }

   public boolean isSplitable(JobContext context, Path file) {
      return false;
   }

   public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context) {
      ConfigurableCombineFileRecordReader reader = new ConfigurableCombineFileRecordReader(split, context, WholeTextFileRecordReader.class);
      reader.setConf(this.getConf());
      return reader;
   }

   public void setMinPartitions(JobContext context, int minPartitions) {
      Buffer files = (Buffer)JavaConverters$.MODULE$.asScalaBufferConverter(this.listStatus(context)).asScala();
      long totalLen = BoxesRunTime.unboxToLong(((TraversableOnce)files.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(FileStatus file) {
            return file.isDirectory() ? 0L : file.getLen();
         }
      }, Buffer$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
      long maxSplitSize = (long)Math.ceil((double)totalLen * (double)1.0F / (double)(minPartitions == 0 ? 1 : minPartitions));
      super.setMaxSplitSize(maxSplitSize);
   }

   public WholeTextFileInputFormat() {
      Configurable$class.$init$(this);
   }
}
