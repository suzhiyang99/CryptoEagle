package org.apache.spark.api.python;

import org.apache.hadoop.io.IntWritable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d2Q!\u0001\u0002\u0001\u00051\u0011Q\u0003V3ti&s\u0007/\u001e;LKf\u001cuN\u001c<feR,'O\u0003\u0002\u0004\t\u00051\u0001/\u001f;i_:T!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h'\r\u0001Qb\u0005\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\tQ)rcF\u0007\u0002\u0005%\u0011aC\u0001\u0002\n\u0007>tg/\u001a:uKJ\u0004\"A\u0004\r\n\u0005ey!aA!os\")1\u0004\u0001C\u0001;\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\u001f!\t!\u0002\u0001C\u0003!\u0001\u0011\u0005\u0013%A\u0004d_:4XM\u001d;\u0015\u0005\t*\u0003C\u0001\b$\u0013\t!sB\u0001\u0003DQ\u0006\u0014\b\"\u0002\u0014 \u0001\u00049\u0012aA8cU\u0002"
)
public class TestInputKeyConverter implements Converter<Object, Object> {
   public char convert(Object obj) {
      return (char)((IntWritable)obj).get();
   }
}
