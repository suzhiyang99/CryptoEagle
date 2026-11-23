package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00113Q!\u0001\u0002\u0001\t)\u0011\u0001cQ8He>,\b\u000fU1si&$\u0018n\u001c8\u000b\u0005\r!\u0011a\u0001:eI*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0017E)\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\u0013'5\tA!\u0003\u0002\u0015\t\tI\u0001+\u0019:uSRLwN\u001c\t\u0003\u0019YI!aF\u0007\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011e\u0001!Q1A\u0005Bm\tQ!\u001b8eKb\u001c\u0001!F\u0001\u001d!\taQ$\u0003\u0002\u001f\u001b\t\u0019\u0011J\u001c;\t\u0011\u0001\u0002!\u0011!Q\u0001\nq\ta!\u001b8eKb\u0004\u0003\u0002\u0003\u0012\u0001\u0005\u000b\u0007I\u0011A\u0012\u0002\u00159\f'O]8x\t\u0016\u00048/F\u0001%!\raQeJ\u0005\u0003M5\u0011Q!\u0011:sCf\u00042\u0001\u0004\u0015+\u0013\tISB\u0001\u0004PaRLwN\u001c\t\u0003W1j\u0011AA\u0005\u0003[\t\u0011QCT1se><8i\\$s_V\u00048\u000b\u001d7ji\u0012+\u0007\u000f\u0003\u00050\u0001\t\u0005\t\u0015!\u0003%\u0003-q\u0017M\u001d:po\u0012+\u0007o\u001d\u0011\t\u000bE\u0002A\u0011\u0001\u001a\u0002\rqJg.\u001b;?)\r\u0019D'\u000e\t\u0003W\u0001AQ!\u0007\u0019A\u0002qAQA\t\u0019A\u0002\u0011BQa\u000e\u0001\u0005Ba\n\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u00029!)!\b\u0001C!w\u00051Q-];bYN$\"\u0001P \u0011\u00051i\u0014B\u0001 \u000e\u0005\u001d\u0011un\u001c7fC:DQ\u0001Q\u001dA\u0002\u0005\u000bQa\u001c;iKJ\u0004\"\u0001\u0004\"\n\u0005\rk!aA!os\u0002"
)
public class CoGroupPartition implements Partition {
   private final int index;
   private final Option<NarrowCoGroupSplitDep>[] narrowDeps;

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public int index() {
      return this.index;
   }

   public Option<NarrowCoGroupSplitDep>[] narrowDeps() {
      return this.narrowDeps;
   }

   public int hashCode() {
      return this.index();
   }

   public boolean equals(Object other) {
      return Partition$class.equals(this, other);
   }

   public CoGroupPartition(int index, Option<NarrowCoGroupSplitDep>[] narrowDeps) {
      this.index = index;
      this.narrowDeps = narrowDeps;
      Partition$class.$init$(this);
   }
}
