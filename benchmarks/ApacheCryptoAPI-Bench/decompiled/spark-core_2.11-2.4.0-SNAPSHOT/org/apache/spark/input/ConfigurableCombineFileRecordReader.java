package org.apache.spark.input;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u00154Q!\u0001\u0002\u0001\t)\u00111eQ8oM&<WO]1cY\u0016\u001cu.\u001c2j]\u00164\u0015\u000e\\3SK\u000e|'\u000f\u001a*fC\u0012,'O\u0003\u0002\u0004\t\u0005)\u0011N\u001c9vi*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x-F\u0002\f3\u001d\u001a2\u0001\u0001\u0007*!\u0011iQc\u0006\u0014\u000e\u00039Q!aA\b\u000b\u0005A\t\u0012a\u00017jE*\u0011!cE\u0001\n[\u0006\u0004(/\u001a3vG\u0016T!\u0001\u0006\u0004\u0002\r!\fGm\\8q\u0013\t1bBA\fD_6\u0014\u0017N\\3GS2,'+Z2pe\u0012\u0014V-\u00193feB\u0011\u0001$\u0007\u0007\u0001\t\u0015Q\u0002A1\u0001\u001d\u0005\u0005Y5\u0001A\t\u0003;\r\u0002\"AH\u0011\u000e\u0003}Q\u0011\u0001I\u0001\u0006g\u000e\fG.Y\u0005\u0003E}\u0011qAT8uQ&tw\r\u0005\u0002\u001fI%\u0011Qe\b\u0002\u0004\u0003:L\bC\u0001\r(\t\u0015A\u0003A1\u0001\u001d\u0005\u00051\u0006C\u0001\u0016,\u001b\u0005\u0011\u0011B\u0001\u0017\u0003\u00051\u0019uN\u001c4jOV\u0014\u0018M\u00197f\u0011!q\u0003A!A!\u0002\u0013y\u0013!B:qY&$\bC\u0001\u00192\u001b\u0005\t\u0012B\u0001\u001a\u0012\u0005)Ie\u000e];u'Bd\u0017\u000e\u001e\u0005\ti\u0001\u0011\t\u0011)A\u0005k\u000591m\u001c8uKb$\bC\u0001\u00197\u0013\t9\u0014C\u0001\nUCN\\\u0017\t\u001e;f[B$8i\u001c8uKb$\b\u0002C\u001d\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001e\u0002#I,7m\u001c:e%\u0016\fG-\u001a:DY\u0006\u001c8\u000f\r\u0002<\u0007B\u0019Ah\u0010\"\u000f\u0005yi\u0014B\u0001  \u0003\u0019\u0001&/\u001a3fM&\u0011\u0001)\u0011\u0002\u0006\u00072\f7o\u001d\u0006\u0003}}\u0001\"\u0001G\"\u0005\u0013\u0011C\u0014\u0011!A\u0001\u0006\u0003)%aA0%cE\u0011QD\u0012\n\u0004\u000f&ce\u0001\u0002%\u0001\u0001\u0019\u0013A\u0002\u0010:fM&tW-\\3oiz\u0002B\u0001\r&\u0018M%\u00111*\u0005\u0002\r%\u0016\u001cwN\u001d3SK\u0006$WM\u001d\t\u0003\u001bBk\u0011A\u0014\u0006\u0003\u001fN\tAaY8oM&\u0011AF\u0014\u0005\u0006%\u0002!\taU\u0001\u0007y%t\u0017\u000e\u001e \u0015\tQ+fk\u0016\t\u0005U\u00019b\u0005C\u0003/#\u0002\u0007q\u0006C\u00035#\u0002\u0007Q\u0007C\u0003:#\u0002\u0007\u0001\f\r\u0002Z7B\u0019Ah\u0010.\u0011\u0005aYF!\u0003#X\u0003\u0003\u0005\tQ!\u0001]#\tiRLE\u0002_\u001323A\u0001\u0013\u0001\u0001;\")\u0001\r\u0001C!C\u0006!\u0012N\\5u\u001d\u0016DHOU3d_J$'+Z1eKJ$\u0012A\u0019\t\u0003=\rL!\u0001Z\u0010\u0003\u000f\t{w\u000e\\3b]\u0002"
)
public class ConfigurableCombineFileRecordReader<K, V> extends CombineFileRecordReader<K, V> implements Configurable {
   private Configuration org$apache$spark$input$Configurable$$conf;

   public Configuration org$apache$spark$input$Configurable$$conf() {
      return this.org$apache$spark$input$Configurable$$conf;
   }

   @TraitSetter
   public void org$apache$spark$input$Configurable$$conf_$eq(Configuration x$1) {
      this.org$apache$spark$input$Configurable$$conf = x$1;
   }

   public void setConf(Configuration c) {
      Configurable$class.setConf(this, c);
   }

   public Configuration getConf() {
      return Configurable$class.getConf(this);
   }

   public boolean initNextRecordReader() {
      boolean r = super.initNextRecordReader();
      if (r) {
         ((org.apache.hadoop.conf.Configurable)this.curReader).setConf(this.getConf());
      }

      return r;
   }

   public ConfigurableCombineFileRecordReader(InputSplit split, TaskAttemptContext context, Class<? extends RecordReader<K, V>> recordReaderClass) {
      super((CombineFileSplit)split, context, recordReaderClass);
      Configurable$class.$init$(this);
   }
}
