package org.apache.spark.input;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import org.spark_project.guava.io.ByteStreams;
import org.spark_project.guava.io.Closeables;
import scala.Predef$;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001E4Q!\u0001\u0002\u0001\t)\u0011\u0011d\u00165pY\u0016$V\r\u001f;GS2,'+Z2pe\u0012\u0014V-\u00193fe*\u00111\u0001B\u0001\u0006S:\u0004X\u000f\u001e\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\r\u0011\t1\t2cE\u0007\u0002\u001b)\u0011abD\u0001\n[\u0006\u0004(/\u001a3vG\u0016T!\u0001\u0005\u0004\u0002\r!\fGm\\8q\u0013\t\u0011RB\u0001\u0007SK\u000e|'\u000f\u001a*fC\u0012,'\u000f\u0005\u0002\u0015/5\tQC\u0003\u0002\u0017\u001f\u0005\u0011\u0011n\\\u0005\u00031U\u0011A\u0001V3yiB\u0011!dG\u0007\u0002\u0005%\u0011AD\u0001\u0002\r\u0007>tg-[4ve\u0006\u0014G.\u001a\u0005\t=\u0001\u0011\t\u0011)A\u0005A\u0005)1\u000f\u001d7ji\u000e\u0001\u0001CA\u0011&\u001b\u0005\u0011#BA\u0002$\u0015\t!S\"A\u0002mS\nL!A\n\u0012\u0003!\r{WNY5oK\u001aKG.Z*qY&$\b\u0002\u0003\u0015\u0001\u0005\u0003\u0005\u000b\u0011B\u0015\u0002\u000f\r|g\u000e^3yiB\u0011ABK\u0005\u0003W5\u0011!\u0003V1tW\u0006#H/Z7qi\u000e{g\u000e^3yi\"AQ\u0006\u0001B\u0001B\u0003%a&A\u0003j]\u0012,\u0007\u0010\u0005\u00020i5\t\u0001G\u0003\u00022e\u0005!A.\u00198h\u0015\u0005\u0019\u0014\u0001\u00026bm\u0006L!!\u000e\u0019\u0003\u000f%sG/Z4fe\")q\u0007\u0001C\u0001q\u00051A(\u001b8jiz\"B!\u000f\u001e<yA\u0011!\u0004\u0001\u0005\u0006=Y\u0002\r\u0001\t\u0005\u0006QY\u0002\r!\u000b\u0005\u0006[Y\u0002\rA\f\u0005\u0007}\u0001\u0001\u000b\u0011B \u0002\tA\fG\u000f\u001b\t\u0003\u0001\u000ek\u0011!\u0011\u0006\u0003\u0005>\t!AZ:\n\u0005\u0011\u000b%\u0001\u0002)bi\"DaA\u0011\u0001!\u0002\u00131\u0005C\u0001!H\u0013\tA\u0015I\u0001\u0006GS2,7+_:uK6DaA\u0013\u0001!B\u0013Y\u0015!\u00039s_\u000e,7o]3e!\tau*D\u0001N\u0015\u0005q\u0015!B:dC2\f\u0017B\u0001)N\u0005\u001d\u0011un\u001c7fC:DaA\u0015\u0001!\u0002\u0013\u0019\u0012aA6fs\"1A\u000b\u0001Q!\nM\tQA^1mk\u0016DQA\u0016\u0001\u0005B]\u000b!\"\u001b8ji&\fG.\u001b>f)\rA6l\u0018\t\u0003\u0019fK!AW'\u0003\tUs\u0017\u000e\u001e\u0005\u0006=U\u0003\r\u0001\u0018\t\u0003\u0019uK!AX\u0007\u0003\u0015%s\u0007/\u001e;Ta2LG\u000fC\u0003)+\u0002\u0007\u0011\u0006C\u0003b\u0001\u0011\u0005#-A\u0003dY>\u001cX\rF\u0001Y\u0011\u0015!\u0007\u0001\"\u0011f\u0003-9W\r\u001e)s_\u001e\u0014Xm]:\u0015\u0003\u0019\u0004\"\u0001T4\n\u0005!l%!\u0002$m_\u0006$\b\"\u00026\u0001\t\u0003Z\u0017!D4fi\u000e+(O]3oi.+\u0017\u0010F\u0001\u0014\u0011\u0015i\u0007\u0001\"\u0011l\u0003=9W\r^\"veJ,g\u000e\u001e,bYV,\u0007\"B8\u0001\t\u0003\u0002\u0018\u0001\u00048fqR\\U-\u001f,bYV,G#A&"
)
public class WholeTextFileRecordReader extends RecordReader<Text, Text> implements Configurable {
   private final Path path;
   private final FileSystem fs;
   private boolean processed;
   private final Text key;
   private Text value;
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

   public void initialize(InputSplit split, TaskAttemptContext context) {
   }

   public void close() {
   }

   public float getProgress() {
      return this.processed ? 1.0F : 0.0F;
   }

   public Text getCurrentKey() {
      return this.key;
   }

   public Text getCurrentValue() {
      return this.value;
   }

   public boolean nextKeyValue() {
      boolean var10000;
      if (this.processed) {
         var10000 = false;
      } else {
         Configuration conf = new Configuration();
         CompressionCodecFactory factory = new CompressionCodecFactory(conf);
         CompressionCodec codec = factory.getCodec(this.path);
         FSDataInputStream fileIn = this.fs.open(this.path);
         byte[] innerBuffer = codec == null ? ByteStreams.toByteArray(fileIn) : ByteStreams.toByteArray(codec.createInputStream(fileIn));
         this.value = new Text(innerBuffer);
         Closeables.close(fileIn, false);
         this.processed = true;
         var10000 = true;
      }

      return var10000;
   }

   public WholeTextFileRecordReader(CombineFileSplit split, TaskAttemptContext context, Integer index) {
      Configurable$class.$init$(this);
      this.path = split.getPath(Predef$.MODULE$.Integer2int(index));
      this.fs = this.path.getFileSystem(context.getConfiguration());
      this.processed = false;
      this.key = new Text(this.path.toString());
      this.value = null;
   }
}
