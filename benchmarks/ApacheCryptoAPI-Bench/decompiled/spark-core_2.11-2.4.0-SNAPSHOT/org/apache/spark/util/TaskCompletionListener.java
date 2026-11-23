package org.apache.spark.util;

import java.util.EventListener;
import org.apache.spark.TaskContext;
import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001=2q!\u0001\u0002\u0011\u0002G\u00051B\u0001\fUCN\\7i\\7qY\u0016$\u0018n\u001c8MSN$XM\\3s\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0019\u0001\u0001\u0004\u000b\u0011\u00055\u0011R\"\u0001\b\u000b\u0005=\u0001\u0012\u0001\u00027b]\u001eT\u0011!E\u0001\u0005U\u00064\u0018-\u0003\u0002\u0014\u001d\t1qJ\u00196fGR\u0004\"!F\f\u000e\u0003YQ!a\u0001\t\n\u0005a1\"!D#wK:$H*[:uK:,'\u000fC\u0003\u001b\u0001\u0019\u00051$\u0001\tp]R\u000b7o[\"p[BdW\r^5p]R\u0011AD\t\t\u0003;\u0001j\u0011A\b\u0006\u0002?\u0005)1oY1mC&\u0011\u0011E\b\u0002\u0005+:LG\u000fC\u0003$3\u0001\u0007A%A\u0004d_:$X\r\u001f;\u0011\u0005\u00152S\"\u0001\u0003\n\u0005\u001d\"!a\u0003+bg.\u001cuN\u001c;fqRD#\u0001A\u0015\u0011\u0005)jS\"A\u0016\u000b\u00051\"\u0011AC1o]>$\u0018\r^5p]&\u0011af\u000b\u0002\r\t\u00164X\r\\8qKJ\f\u0005/\u001b"
)
public interface TaskCompletionListener extends EventListener {
   void onTaskCompletion(TaskContext var1);
}
