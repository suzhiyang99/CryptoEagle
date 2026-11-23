package org.apache.spark.api.python;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.spark.SparkEnv;
import org.apache.spark.TaskContext;
import scala.Array$;
import scala.MatchError;
import scala.PartialFunction;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-rAB\u0001\u0003\u0011\u00031A\"\u0001\u0007QsRDwN\u001c*v]:,'O\u0003\u0002\u0004\t\u00051\u0001/\u001f;i_:T!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h!\tia\"D\u0001\u0003\r\u0019y!\u0001#\u0001\u0007!\ta\u0001+\u001f;i_:\u0014VO\u001c8feN\u0011a\"\u0005\t\u0003%Ui\u0011a\u0005\u0006\u0002)\u0005)1oY1mC&\u0011ac\u0005\u0002\u0007\u0003:L(+\u001a4\t\u000baqA\u0011\u0001\u000e\u0002\rqJg.\u001b;?\u0007\u0001!\u0012\u0001\u0004\u0005\u000699!\t!H\u0001\u0006CB\u0004H.\u001f\u000b\b=\u0005u\u0011qEA\u0015!\tiqDB\u0003\u0010\u0005\u00011\u0001e\u0005\u0002 CA!QB\t\u0013%\u0013\t\u0019#A\u0001\tCCN,\u0007+\u001f;i_:\u0014VO\u001c8feB\u0019!#J\u0014\n\u0005\u0019\u001a\"!B!se\u0006L\bC\u0001\n)\u0013\tI3C\u0001\u0003CsR,\u0007\u0002C\u0016 \u0005\u0003\u0005\u000b\u0011\u0002\u0017\u0002\u000b\u0019,hnY:\u0011\u00075*\u0004H\u0004\u0002/g9\u0011qFM\u0007\u0002a)\u0011\u0011'G\u0001\u0007yI|w\u000e\u001e \n\u0003QI!\u0001N\n\u0002\u000fA\f7m[1hK&\u0011ag\u000e\u0002\u0004'\u0016\f(B\u0001\u001b\u0014!\ti\u0011(\u0003\u0002;\u0005\t12\t[1j]\u0016$\u0007+\u001f;i_:4UO\\2uS>t7\u000f\u0003\u0005=?\t\u0005\t\u0015!\u0003>\u0003)\u0011WO\u001a4feNK'0\u001a\t\u0003%yJ!aP\n\u0003\u0007%sG\u000f\u0003\u0005B?\t\u0005\t\u0015!\u0003C\u0003-\u0011X-^:f/>\u00148.\u001a:\u0011\u0005I\u0019\u0015B\u0001#\u0014\u0005\u001d\u0011un\u001c7fC:DQ\u0001G\u0010\u0005\u0002\u0019#BAH$I\u0013\")1&\u0012a\u0001Y!)A(\u0012a\u0001{!)\u0011)\u0012a\u0001\u0005\")1j\bC)\u0019\u0006ya.Z<Xe&$XM\u001d+ie\u0016\fG\r\u0006\u0004N#^\u000bg\r\u001b\t\u0003\u001d>k\u0011aH\u0005\u0003!\n\u0012Ab\u0016:ji\u0016\u0014H\u000b\u001b:fC\u0012DQA\u0015&A\u0002M\u000b1!\u001a8w!\t!V+D\u0001\u0007\u0013\t1fA\u0001\u0005Ta\u0006\u00148.\u00128w\u0011\u0015A&\n1\u0001Z\u0003\u00199xN]6feB\u0011!lX\u0007\u00027*\u0011A,X\u0001\u0004]\u0016$(\"\u00010\u0002\t)\fg/Y\u0005\u0003An\u0013aaU8dW\u0016$\b\"\u00022K\u0001\u0004\u0019\u0017!D5oaV$\u0018\n^3sCR|'\u000fE\u0002.I\u0012J!!Z\u001c\u0003\u0011%#XM]1u_JDQa\u001a&A\u0002u\na\u0002]1si&$\u0018n\u001c8J]\u0012,\u0007\u0010C\u0003j\u0015\u0002\u0007!.A\u0004d_:$X\r\u001f;\u0011\u0005Q[\u0017B\u00017\u0007\u0005-!\u0016m]6D_:$X\r\u001f;\t\u000b9|B\u0011K8\u0002#9,wOU3bI\u0016\u0014\u0018\n^3sCR|'\u000fF\u0006dabTx0!\u0001\u0002\u0004\u0005m\u0001\"B9n\u0001\u0004\u0011\u0018AB:ue\u0016\fW\u000e\u0005\u0002tm6\tAO\u0003\u0002v;\u0006\u0011\u0011n\\\u0005\u0003oR\u0014q\u0002R1uC&s\u0007/\u001e;TiJ,\u0017-\u001c\u0005\u0006s6\u0004\r!T\u0001\roJLG/\u001a:UQJ,\u0017\r\u001a\u0005\u0006w6\u0004\r\u0001`\u0001\ngR\f'\u000f\u001e+j[\u0016\u0004\"AE?\n\u0005y\u001c\"\u0001\u0002'p]\u001eDQAU7A\u0002MCQ\u0001W7A\u0002eCq!!\u0002n\u0001\u0004\t9!\u0001\u0005sK2,\u0017m]3e!\u0011\tI!a\u0006\u000e\u0005\u0005-!\u0002BA\u0007\u0003\u001f\ta!\u0019;p[&\u001c'\u0002BA\t\u0003'\t!bY8oGV\u0014(/\u001a8u\u0015\r\t)\"X\u0001\u0005kRLG.\u0003\u0003\u0002\u001a\u0005-!!D!u_6L7MQ8pY\u0016\fg\u000eC\u0003j[\u0002\u0007!\u000eC\u0004\u0002 m\u0001\r!!\t\u0002\t\u0019,hn\u0019\t\u0004\u001b\u0005\r\u0012bAA\u0013\u0005\tq\u0001+\u001f;i_:4UO\\2uS>t\u0007\"\u0002\u001f\u001c\u0001\u0004i\u0004\"B!\u001c\u0001\u0004\u0011\u0005"
)
public class PythonRunner extends BasePythonRunner<byte[], byte[]> {
   public final Seq<ChainedPythonFunctions> org$apache$spark$api$python$PythonRunner$$funcs;

   public static PythonRunner apply(PythonFunction var0, int var1, boolean var2) {
      return PythonRunner$.MODULE$.apply(var0, var1, var2);
   }

   public BasePythonRunner<byte[], byte[]>.WriterThread newWriterThread(SparkEnv env, Socket worker, Iterator<byte[]> inputIterator, int partitionIndex, TaskContext context) {
      return new BasePythonRunner<byte[], byte[]>.WriterThread(this, env, worker, inputIterator, partitionIndex, context) {
         // $FF: synthetic field
         private final PythonRunner $outer;
         private final Iterator inputIterator$1;

         public void writeCommand(DataOutputStream dataOut) {
            byte[] command = ((PythonFunction)((ChainedPythonFunctions)this.$outer.org$apache$spark$api$python$PythonRunner$$funcs.head()).funcs().head()).command();
            dataOut.writeInt(command.length);
            dataOut.write(command);
         }

         public void writeIteratorToStream(DataOutputStream dataOut) {
            PythonRDD$.MODULE$.writeIteratorToStream(this.inputIterator$1, dataOut);
            dataOut.writeInt(SpecialLengths$.MODULE$.END_OF_DATA_SECTION());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.inputIterator$1 = inputIterator$1;
            }
         }
      };
   }

   public Iterator<byte[]> newReaderIterator(DataInputStream stream, BasePythonRunner<byte[], byte[]>.WriterThread writerThread, long startTime, SparkEnv env, Socket worker, AtomicBoolean released, TaskContext context) {
      return new BasePythonRunner<byte[], byte[]>.ReaderIterator(this, stream, writerThread, startTime, env, worker, released, context) {
         private final DataInputStream stream$1;
         private final BasePythonRunner.WriterThread writerThread$2;

         public byte[] read() {
            if (this.writerThread$2.exception().isDefined()) {
               throw (Throwable)this.writerThread$2.exception().get();
            } else {
               byte[] var10000;
               try {
                  int var3 = this.stream$1.readInt();
                  byte[] var4;
                  if (var3 > 0) {
                     byte[] obj = new byte[var3];
                     this.stream$1.readFully(obj);
                     var4 = obj;
                  } else if (0 == var3) {
                     var4 = (byte[])Array$.MODULE$.empty(ClassTag$.MODULE$.Byte());
                  } else if (SpecialLengths$.MODULE$.TIMING_DATA() == var3) {
                     this.handleTimingData();
                     var4 = this.read();
                  } else {
                     if (SpecialLengths$.MODULE$.PYTHON_EXCEPTION_THROWN() == var3) {
                        throw this.handlePythonException();
                     }

                     if (SpecialLengths$.MODULE$.END_OF_DATA_SECTION() != var3) {
                        throw new MatchError(BoxesRunTime.boxToInteger(var3));
                     }

                     this.handleEndOfDataSection();
                     var4 = null;
                  }

                  var10000 = var4;
               } catch (Throwable var7) {
                  PartialFunction catchExpr1 = this.handleException();
                  if (!catchExpr1.isDefinedAt(var7)) {
                     throw var7;
                  }

                  var10000 = (byte[])catchExpr1.apply(var7);
                  return var10000;
               }

               return var10000;
            }
         }

         public {
            this.stream$1 = stream$1;
            this.writerThread$2 = writerThread$2;
         }
      };
   }

   public PythonRunner(Seq<ChainedPythonFunctions> funcs, int bufferSize, boolean reuseWorker) {
      super(funcs, bufferSize, reuseWorker, PythonEvalType$.MODULE$.NON_UDF(), (int[][])((Object[])(new int[][]{{0}})));
      this.org$apache$spark$api$python$PythonRunner$$funcs = funcs;
   }
}
