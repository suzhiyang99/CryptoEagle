package org.apache.spark.scheduler;

import java.util.Properties;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001E2\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\u0013'\u000eDW\rZ;mC\ndWMQ;jY\u0012,'O\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0011\u0001a\u0003\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\t\u000bI\u0001a\u0011\u0001\u000b\u0002\u0011I|w\u000e\u001e)p_2\u001c\u0001!F\u0001\u0016!\t1r#D\u0001\u0003\u0013\tA\"A\u0001\u0003Q_>d\u0007\"\u0002\u000e\u0001\r\u0003Y\u0012A\u00032vS2$\u0007k\\8mgR\tA\u0004\u0005\u0002\r;%\u0011a$\u0004\u0002\u0005+:LG\u000fC\u0003!\u0001\u0019\u0005\u0011%A\tbI\u0012$\u0016m]6TKRl\u0015M\\1hKJ$2\u0001\b\u0012(\u0011\u0015\u0019s\u00041\u0001%\u0003\u001di\u0017M\\1hKJ\u0004\"AF\u0013\n\u0005\u0019\u0012!aC*dQ\u0016$W\u000f\\1cY\u0016DQ\u0001K\u0010A\u0002%\n!\u0002\u001d:pa\u0016\u0014H/[3t!\tQs&D\u0001,\u0015\taS&\u0001\u0003vi&d'\"\u0001\u0018\u0002\t)\fg/Y\u0005\u0003a-\u0012!\u0002\u0015:pa\u0016\u0014H/[3t\u0001"
)
public interface SchedulableBuilder {
   Pool rootPool();

   void buildPools();

   void addTaskSetManager(Schedulable var1, Properties var2);
}
