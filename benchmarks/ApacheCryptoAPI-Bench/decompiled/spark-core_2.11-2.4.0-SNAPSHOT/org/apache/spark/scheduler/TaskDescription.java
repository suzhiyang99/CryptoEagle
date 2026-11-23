package org.apache.spark.scheduler;

import java.nio.ByteBuffer;
import java.util.Properties;
import scala.Predef$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.Map;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005eb!B\u0001\u0003\u0001\u0011Q!a\u0004+bg.$Um]2sSB$\u0018n\u001c8\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fM\"A!\u0003\u0001BC\u0002\u0013\u0005A#\u0001\u0004uCN\\\u0017\nZ\u0002\u0001+\u0005)\u0002C\u0001\u0007\u0017\u0013\t9RB\u0001\u0003M_:<\u0007\u0002C\r\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002\u000fQ\f7o[%eA!A1\u0004\u0001BC\u0002\u0013\u0005A$A\u0007biR,W\u000e\u001d;Ok6\u0014WM]\u000b\u0002;A\u0011ABH\u0005\u0003?5\u00111!\u00138u\u0011!\t\u0003A!A!\u0002\u0013i\u0012AD1ui\u0016l\u0007\u000f\u001e(v[\n,'\u000f\t\u0005\tG\u0001\u0011)\u0019!C\u0001I\u0005QQ\r_3dkR|'/\u00133\u0016\u0003\u0015\u0002\"AJ\u0015\u000f\u000519\u0013B\u0001\u0015\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011!f\u000b\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005!j\u0001\u0002C\u0017\u0001\u0005\u0003\u0005\u000b\u0011B\u0013\u0002\u0017\u0015DXmY;u_JLE\r\t\u0005\t_\u0001\u0011)\u0019!C\u0001I\u0005!a.Y7f\u0011!\t\u0004A!A!\u0002\u0013)\u0013!\u00028b[\u0016\u0004\u0003\u0002C\u001a\u0001\u0005\u000b\u0007I\u0011\u0001\u000f\u0002\u000b%tG-\u001a=\t\u0011U\u0002!\u0011!Q\u0001\nu\ta!\u001b8eKb\u0004\u0003\u0002C\u001c\u0001\u0005\u000b\u0007I\u0011\u0001\u001d\u0002\u0015\u0005$G-\u001a3GS2,7/F\u0001:!\u0011Qt(J\u000b\u000e\u0003mR!\u0001P\u001f\u0002\u000f5,H/\u00192mK*\u0011a(D\u0001\u000bG>dG.Z2uS>t\u0017B\u0001!<\u0005\ri\u0015\r\u001d\u0005\t\u0005\u0002\u0011\t\u0011)A\u0005s\u0005Y\u0011\r\u001a3fI\u001aKG.Z:!\u0011!!\u0005A!b\u0001\n\u0003A\u0014!C1eI\u0016$'*\u0019:t\u0011!1\u0005A!A!\u0002\u0013I\u0014AC1eI\u0016$'*\u0019:tA!A\u0001\n\u0001BC\u0002\u0013\u0005\u0011*\u0001\u0006qe>\u0004XM\u001d;jKN,\u0012A\u0013\t\u0003\u0017Bk\u0011\u0001\u0014\u0006\u0003\u001b:\u000bA!\u001e;jY*\tq*\u0001\u0003kCZ\f\u0017BA)M\u0005)\u0001&o\u001c9feRLWm\u001d\u0005\t'\u0002\u0011\t\u0011)A\u0005\u0015\u0006Y\u0001O]8qKJ$\u0018.Z:!\u0011!)\u0006A!b\u0001\n\u00031\u0016AD:fe&\fG.\u001b>fIR\u000b7o[\u000b\u0002/B\u0011\u0001lW\u0007\u00023*\u0011!LT\u0001\u0004]&|\u0017B\u0001/Z\u0005)\u0011\u0015\u0010^3Ck\u001a4WM\u001d\u0005\t=\u0002\u0011\t\u0011)A\u0005/\u0006y1/\u001a:jC2L'0\u001a3UCN\\\u0007\u0005C\u0003a\u0001\u0011\u0005\u0011-\u0001\u0004=S:LGO\u0010\u000b\u000bE\u0012,gm\u001a5jU.d\u0007CA2\u0001\u001b\u0005\u0011\u0001\"\u0002\n`\u0001\u0004)\u0002\"B\u000e`\u0001\u0004i\u0002\"B\u0012`\u0001\u0004)\u0003\"B\u0018`\u0001\u0004)\u0003\"B\u001a`\u0001\u0004i\u0002\"B\u001c`\u0001\u0004I\u0004\"\u0002#`\u0001\u0004I\u0004\"\u0002%`\u0001\u0004Q\u0005\"B+`\u0001\u00049\u0006\"\u00028\u0001\t\u0003z\u0017\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\u0015:a!\u001d\u0002\t\u0002\u0011\u0011\u0018a\u0004+bg.$Um]2sSB$\u0018n\u001c8\u0011\u0005\r\u001chAB\u0001\u0003\u0011\u0003!Ao\u0005\u0002t\u0017!)\u0001m\u001dC\u0001mR\t!\u000fC\u0003yg\u0012%\u00110\u0001\ftKJL\u0017\r\\5{KN#(/\u001b8h\u0019>tw-T1q)\rQXp \t\u0003\u0019mL!\u0001`\u0007\u0003\tUs\u0017\u000e\u001e\u0005\u0006}^\u0004\r!O\u0001\u0004[\u0006\u0004\bbBA\u0001o\u0002\u0007\u00111A\u0001\bI\u0006$\u0018mT;u!\u0011\t)!a\u0003\u000e\u0005\u0005\u001d!bAA\u0005\u001d\u0006\u0011\u0011n\\\u0005\u0005\u0003\u001b\t9A\u0001\tECR\fw*\u001e;qkR\u001cFO]3b[\"9\u0011\u0011C:\u0005\u0002\u0005M\u0011AB3oG>$W\rF\u0002X\u0003+Aq!a\u0006\u0002\u0010\u0001\u0007!-A\buCN\\G)Z:de&\u0004H/[8o\u0011\u001d\tYb\u001dC\u0005\u0003;\t\u0001\u0004Z3tKJL\u0017\r\\5{KN#(/\u001b8h\u0019>tw-T1q)\u0011\ty\"!\n\u0011\u000bi\n\t#J\u000b\n\u0007\u0005\r2HA\u0004ICNDW*\u00199\t\u0011\u0005\u001d\u0012\u0011\u0004a\u0001\u0003S\ta\u0001Z1uC&s\u0007\u0003BA\u0003\u0003WIA!!\f\u0002\b\tyA)\u0019;b\u0013:\u0004X\u000f^*ue\u0016\fW\u000eC\u0004\u00022M$\t!a\r\u0002\r\u0011,7m\u001c3f)\r\u0011\u0017Q\u0007\u0005\b\u0003o\ty\u00031\u0001X\u0003)\u0011\u0017\u0010^3Ck\u001a4WM\u001d"
)
public class TaskDescription {
   private final long taskId;
   private final int attemptNumber;
   private final String executorId;
   private final String name;
   private final int index;
   private final Map<String, Object> addedFiles;
   private final Map<String, Object> addedJars;
   private final Properties properties;
   private final ByteBuffer serializedTask;

   public static TaskDescription decode(ByteBuffer var0) {
      return TaskDescription$.MODULE$.decode(var0);
   }

   public static ByteBuffer encode(TaskDescription var0) {
      return TaskDescription$.MODULE$.encode(var0);
   }

   public long taskId() {
      return this.taskId;
   }

   public int attemptNumber() {
      return this.attemptNumber;
   }

   public String executorId() {
      return this.executorId;
   }

   public String name() {
      return this.name;
   }

   public int index() {
      return this.index;
   }

   public Map<String, Object> addedFiles() {
      return this.addedFiles;
   }

   public Map<String, Object> addedJars() {
      return this.addedJars;
   }

   public Properties properties() {
      return this.properties;
   }

   public ByteBuffer serializedTask() {
      return this.serializedTask;
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString("TaskDescription(TID=%d, index=%d)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.taskId()), BoxesRunTime.boxToInteger(this.index())}));
   }

   public TaskDescription(long taskId, int attemptNumber, String executorId, String name, int index, Map<String, Object> addedFiles, Map<String, Object> addedJars, Properties properties, ByteBuffer serializedTask) {
      this.taskId = taskId;
      this.attemptNumber = attemptNumber;
      this.executorId = executorId;
      this.name = name;
      this.index = index;
      this.addedFiles = addedFiles;
      this.addedJars = addedJars;
      this.properties = properties;
      this.serializedTask = serializedTask;
   }
}
