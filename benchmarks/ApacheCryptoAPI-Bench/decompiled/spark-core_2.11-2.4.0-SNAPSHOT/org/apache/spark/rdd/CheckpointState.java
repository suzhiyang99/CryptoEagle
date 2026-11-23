package org.apache.spark.rdd;

import scala.Enumeration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-:a!\u0001\u0002\t\u0002\u0011Q\u0011aD\"iK\u000e\\\u0007o\\5oiN#\u0018\r^3\u000b\u0005\r!\u0011a\u0001:eI*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x\r\u0005\u0002\f\u00195\t!A\u0002\u0004\u000e\u0005!\u0005AA\u0004\u0002\u0010\u0007\",7m\u001b9pS:$8\u000b^1uKN\u0011Ab\u0004\t\u0003!Mi\u0011!\u0005\u0006\u0002%\u0005)1oY1mC&\u0011A#\u0005\u0002\f\u000b:,X.\u001a:bi&|g\u000eC\u0003\u0017\u0019\u0011\u0005\u0001$\u0001\u0004=S:LGOP\u0002\u0001)\u0005QQ\u0001B\u0007\r\u0001i\u0001\"a\u0007\u000f\u000e\u00031I!!H\n\u0003\u000bY\u000bG.^3\t\u000f}a!\u0019!C\u0001A\u0005Y\u0011J\\5uS\u0006d\u0017N_3e+\u0005Q\u0002B\u0002\u0012\rA\u0003%!$\u0001\u0007J]&$\u0018.\u00197ju\u0016$\u0007\u0005C\u0004%\u0019\t\u0007I\u0011\u0001\u0011\u0002/\rCWmY6q_&tG/\u001b8h\u0013:\u0004&o\\4sKN\u001c\bB\u0002\u0014\rA\u0003%!$\u0001\rDQ\u0016\u001c7\u000e]8j]RLgnZ%o!J|wM]3tg\u0002Bq\u0001\u000b\u0007C\u0002\u0013\u0005\u0001%\u0001\u0007DQ\u0016\u001c7\u000e]8j]R,G\r\u0003\u0004+\u0019\u0001\u0006IAG\u0001\u000e\u0007\",7m\u001b9pS:$X\r\u001a\u0011"
)
public final class CheckpointState {
   public static Enumeration.Value Checkpointed() {
      return CheckpointState$.MODULE$.Checkpointed();
   }

   public static Enumeration.Value CheckpointingInProgress() {
      return CheckpointState$.MODULE$.CheckpointingInProgress();
   }

   public static Enumeration.Value Initialized() {
      return CheckpointState$.MODULE$.Initialized();
   }

   public static Enumeration.Value withName(String var0) {
      return CheckpointState$.MODULE$.withName(var0);
   }

   public static Enumeration.Value apply(int var0) {
      return CheckpointState$.MODULE$.apply(var0);
   }

   public static int maxId() {
      return CheckpointState$.MODULE$.maxId();
   }

   public static Enumeration.ValueSet values() {
      return CheckpointState$.MODULE$.values();
   }

   public static String toString() {
      return CheckpointState$.MODULE$.toString();
   }
}
