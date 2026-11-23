package org.apache.spark.launcher;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.spark.deploy.Command;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\t3Q!\u0001\u0002\u0001\t)\u0011AcV8sW\u0016\u00148i\\7nC:$')^5mI\u0016\u0014(BA\u0002\u0005\u0003!a\u0017-\u001e8dQ\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u000e\u001b\u0005\u0011\u0011B\u0001\b\u0003\u0005Y\t%m\u001d;sC\u000e$8i\\7nC:$')^5mI\u0016\u0014\b\u0002\u0003\t\u0001\u0005\u0003\u0005\u000b\u0011\u0002\n\u0002\u0013M\u0004\u0018M]6I_6,7\u0001\u0001\t\u0003'eq!\u0001F\f\u000e\u0003UQ\u0011AF\u0001\u0006g\u000e\fG.Y\u0005\u00031U\ta\u0001\u0015:fI\u00164\u0017B\u0001\u000e\u001c\u0005\u0019\u0019FO]5oO*\u0011\u0001$\u0006\u0005\t;\u0001\u0011\t\u0011)A\u0005=\u0005AQ.Z7pefl%\r\u0005\u0002\u0015?%\u0011\u0001%\u0006\u0002\u0004\u0013:$\b\u0002\u0003\u0012\u0001\u0005\u0003\u0005\u000b\u0011B\u0012\u0002\u000f\r|W.\\1oIB\u0011AeJ\u0007\u0002K)\u0011a\u0005B\u0001\u0007I\u0016\u0004Hn\\=\n\u0005!*#aB\"p[6\fg\u000e\u001a\u0005\u0006U\u0001!\taK\u0001\u0007y%t\u0017\u000e\u001e \u0015\t1jcf\f\t\u0003\u0019\u0001AQ\u0001E\u0015A\u0002IAQ!H\u0015A\u0002yAQAI\u0015A\u0002\rBQ!\r\u0001\u0005BI\nABY;jY\u0012\u001cu.\\7b]\u0012$\"aM\u001e\u0011\u0007QJ$#D\u00016\u0015\t1t'\u0001\u0003vi&d'\"\u0001\u001d\u0002\t)\fg/Y\u0005\u0003uU\u0012A\u0001T5ti\")A\b\ra\u0001{\u0005\u0019QM\u001c<\u0011\tQr$CE\u0005\u0003\u007fU\u00121!T1q\u0011\u0015\t\u0004\u0001\"\u0001B)\u0005\u0019\u0004"
)
public class WorkerCommandBuilder extends AbstractCommandBuilder {
   private final int memoryMb;
   private final Command command;

   public List<String> buildCommand(Map<String, String> env) {
      List cmd = this.buildJavaCommand(this.command.classPathEntries().mkString(File.pathSeparator));
      cmd.add((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"-Xmx", "M"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.memoryMb)})));
      this.command.javaOpts().foreach(new Serializable(this, cmd) {
         public static final long serialVersionUID = 0L;
         private final List cmd$1;

         public final boolean apply(String x$1) {
            return this.cmd$1.add(x$1);
         }

         public {
            this.cmd$1 = cmd$1;
         }
      });
      return cmd;
   }

   public List<String> buildCommand() {
      return this.buildCommand(new HashMap());
   }

   public WorkerCommandBuilder(String sparkHome, int memoryMb, Command command) {
      this.memoryMb = memoryMb;
      this.command = command;
      this.childEnv.putAll((Map)JavaConverters$.MODULE$.mapAsJavaMapConverter(command.environment()).asJava());
      this.childEnv.put("SPARK_HOME", sparkHome);
   }
}
