package org.apache.spark;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001q2A!\u0001\u0002\u0005\u0013\t)2\u000b]1sW\u0016CXmY;u_JLeNZ8J[Bd'BA\u0002\u0005\u0003\u0015\u0019\b/\u0019:l\u0015\t)a!\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u000f\u0005\u0019qN]4\u0004\u0001M\u0019\u0001A\u0003\t\u0011\u0005-qQ\"\u0001\u0007\u000b\u00035\tQa]2bY\u0006L!a\u0004\u0007\u0003\r\u0005s\u0017PU3g!\t\t\"#D\u0001\u0003\u0013\t\u0019\"AA\tTa\u0006\u00148.\u0012=fGV$xN]%oM>D\u0001\"\u0006\u0001\u0003\u0006\u0004%\tAF\u0001\u0005Q>\u001cH/F\u0001\u0018!\tA2D\u0004\u0002\f3%\u0011!\u0004D\u0001\u0007!J,G-\u001a4\n\u0005qi\"AB*ue&twM\u0003\u0002\u001b\u0019!Aq\u0004\u0001B\u0001B\u0003%q#A\u0003i_N$\b\u0005\u0003\u0005\"\u0001\t\u0015\r\u0011\"\u0001#\u0003\u0011\u0001xN\u001d;\u0016\u0003\r\u0002\"a\u0003\u0013\n\u0005\u0015b!aA%oi\"Aq\u0005\u0001B\u0001B\u0003%1%A\u0003q_J$\b\u0005\u0003\u0005*\u0001\t\u0015\r\u0011\"\u0001+\u0003%\u0019\u0017m\u00195f'&TX-F\u0001,!\tYA&\u0003\u0002.\u0019\t!Aj\u001c8h\u0011!y\u0003A!A!\u0002\u0013Y\u0013AC2bG\",7+\u001b>fA!A\u0011\u0007\u0001BC\u0002\u0013\u0005!%A\bok6\u0014VO\u001c8j]\u001e$\u0016m]6t\u0011!\u0019\u0004A!A!\u0002\u0013\u0019\u0013\u0001\u00058v[J+hN\\5oOR\u000b7o[:!\u0011\u0015)\u0004\u0001\"\u00017\u0003\u0019a\u0014N\\5u}Q)q\u0007O\u001d;wA\u0011\u0011\u0003\u0001\u0005\u0006+Q\u0002\ra\u0006\u0005\u0006CQ\u0002\ra\t\u0005\u0006SQ\u0002\ra\u000b\u0005\u0006cQ\u0002\ra\t"
)
public class SparkExecutorInfoImpl implements SparkExecutorInfo {
   private final String host;
   private final int port;
   private final long cacheSize;
   private final int numRunningTasks;

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }

   public long cacheSize() {
      return this.cacheSize;
   }

   public int numRunningTasks() {
      return this.numRunningTasks;
   }

   public SparkExecutorInfoImpl(String host, int port, long cacheSize, int numRunningTasks) {
      this.host = host;
      this.port = port;
      this.cacheSize = cacheSize;
      this.numRunningTasks = numRunningTasks;
   }
}
