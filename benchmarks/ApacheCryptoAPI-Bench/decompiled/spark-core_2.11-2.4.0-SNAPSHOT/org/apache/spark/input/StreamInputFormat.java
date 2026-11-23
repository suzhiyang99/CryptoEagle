package org.apache.spark.input;

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001i2Q!\u0001\u0002\u0001\t)\u0011\u0011c\u0015;sK\u0006l\u0017J\u001c9vi\u001a{'/\\1u\u0015\t\u0019A!A\u0003j]B,HO\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\t\u00011\u0002E\u0002\r\u001b=i\u0011AA\u0005\u0003\u001d\t\u0011Qc\u0015;sK\u0006lg)\u001b7f\u0013:\u0004X\u000f\u001e$pe6\fG\u000f\u0005\u0002\r!%\u0011\u0011C\u0001\u0002\u0013!>\u0014H/\u00192mK\u0012\u000bG/Y*ue\u0016\fW\u000eC\u0003\u0014\u0001\u0011\u0005Q#\u0001\u0004=S:LGOP\u0002\u0001)\u00051\u0002C\u0001\u0007\u0001\u0011\u0015A\u0002\u0001\"\u0011\u001a\u0003I\u0019'/Z1uKJ+7m\u001c:e%\u0016\fG-\u001a:\u0015\u0007iyS\u0007\u0005\u0003\u001cG\u0015zQ\"\u0001\u000f\u000b\u0005\ri\"B\u0001\u0010 \u0003\ra\u0017N\u0019\u0006\u0003A\u0005\n\u0011\"\\1qe\u0016$WoY3\u000b\u0005\t2\u0011A\u00025bI>|\u0007/\u0003\u0002%9\t92i\\7cS:,g)\u001b7f%\u0016\u001cwN\u001d3SK\u0006$WM\u001d\t\u0003M1r!a\n\u0016\u000e\u0003!R\u0011!K\u0001\u0006g\u000e\fG.Y\u0005\u0003W!\na\u0001\u0015:fI\u00164\u0017BA\u0017/\u0005\u0019\u0019FO]5oO*\u00111\u0006\u000b\u0005\u0006a]\u0001\r!M\u0001\u0006gBd\u0017\u000e\u001e\t\u0003eMj\u0011aH\u0005\u0003i}\u0011!\"\u00138qkR\u001c\u0006\u000f\\5u\u0011\u00151t\u00031\u00018\u0003%!\u0018mQ8oi\u0016DH\u000f\u0005\u00023q%\u0011\u0011h\b\u0002\u0013)\u0006\u001c8.\u0011;uK6\u0004HoQ8oi\u0016DH\u000f"
)
public class StreamInputFormat extends StreamFileInputFormat<PortableDataStream> {
   public CombineFileRecordReader<String, PortableDataStream> createRecordReader(InputSplit split, TaskAttemptContext taContext) {
      return new CombineFileRecordReader((CombineFileSplit)split, taContext, StreamRecordReader.class);
   }
}
