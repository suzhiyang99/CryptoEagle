package org.apache.spark.deploy.master;

import org.apache.spark.deploy.ExecutorDescription;
import org.apache.spark.deploy.ExecutorState$;
import scala.Enumeration;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001]4Q!\u0001\u0002\u0001\u00051\u0011A\"\u0012=fGV$xN\u001d#fg\u000eT!a\u0001\u0003\u0002\r5\f7\u000f^3s\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011Q\u0001!Q1A\u0005\u0002Y\t!!\u001b3\u0004\u0001U\tq\u0003\u0005\u0002\u000f1%\u0011\u0011d\u0004\u0002\u0004\u0013:$\b\u0002C\u000e\u0001\u0005\u0003\u0005\u000b\u0011B\f\u0002\u0007%$\u0007\u0005\u0003\u0005\u001e\u0001\t\u0015\r\u0011\"\u0001\u001f\u0003-\t\u0007\u000f\u001d7jG\u0006$\u0018n\u001c8\u0016\u0003}\u0001\"\u0001I\u0011\u000e\u0003\tI!A\t\u0002\u0003\u001f\u0005\u0003\b\u000f\\5dCRLwN\\%oM>D\u0001\u0002\n\u0001\u0003\u0002\u0003\u0006IaH\u0001\rCB\u0004H.[2bi&|g\u000e\t\u0005\tM\u0001\u0011)\u0019!C\u0001O\u00051qo\u001c:lKJ,\u0012\u0001\u000b\t\u0003A%J!A\u000b\u0002\u0003\u0015]{'o[3s\u0013:4w\u000e\u0003\u0005-\u0001\t\u0005\t\u0015!\u0003)\u0003\u001d9xN]6fe\u0002B\u0001B\f\u0001\u0003\u0006\u0004%\tAF\u0001\u0006G>\u0014Xm\u001d\u0005\ta\u0001\u0011\t\u0011)A\u0005/\u000511m\u001c:fg\u0002B\u0001B\r\u0001\u0003\u0006\u0004%\tAF\u0001\u0007[\u0016lwN]=\t\u0011Q\u0002!\u0011!Q\u0001\n]\tq!\\3n_JL\b\u0005C\u00037\u0001\u0011\u0005q'\u0001\u0004=S:LGO\u0010\u000b\u0007qeR4\bP\u001f\u0011\u0005\u0001\u0002\u0001\"\u0002\u000b6\u0001\u00049\u0002\"B\u000f6\u0001\u0004y\u0002\"\u0002\u00146\u0001\u0004A\u0003\"\u0002\u00186\u0001\u00049\u0002\"\u0002\u001a6\u0001\u00049\u0002bB \u0001\u0001\u0004%\t\u0001Q\u0001\u0006gR\fG/Z\u000b\u0002\u0003B\u0011!I\u0012\b\u0003\u0007\u0012k\u0011\u0001B\u0005\u0003\u000b\u0012\tQ\"\u0012=fGV$xN]*uCR,\u0017BA$I\u0005\u00151\u0016\r\\;f\u0013\tIuBA\u0006F]VlWM]1uS>t\u0007bB&\u0001\u0001\u0004%\t\u0001T\u0001\ngR\fG/Z0%KF$\"!\u0014)\u0011\u00059q\u0015BA(\u0010\u0005\u0011)f.\u001b;\t\u000fES\u0015\u0011!a\u0001\u0003\u0006\u0019\u0001\u0010J\u0019\t\rM\u0003\u0001\u0015)\u0003B\u0003\u0019\u0019H/\u0019;fA!)Q\u000b\u0001C\u0001-\u0006I1m\u001c9z'R\fG/\u001a\u000b\u0003\u001b^CQ\u0001\u0017+A\u0002e\u000b\u0001\"\u001a=fG\u0012+7o\u0019\t\u0003\u0007jK!a\u0017\u0003\u0003'\u0015CXmY;u_J$Um]2sSB$\u0018n\u001c8\t\u000bu\u0003A\u0011\u00010\u0002\r\u0019,H\u000e\\%e+\u0005y\u0006C\u00011d\u001d\tq\u0011-\u0003\u0002c\u001f\u00051\u0001K]3eK\u001aL!\u0001Z3\u0003\rM#(/\u001b8h\u0015\t\u0011w\u0002C\u0003h\u0001\u0011\u0005\u0003.\u0001\u0004fcV\fGn\u001d\u000b\u0003S2\u0004\"A\u00046\n\u0005-|!a\u0002\"p_2,\u0017M\u001c\u0005\u0006[\u001a\u0004\rA\\\u0001\u0006_RDWM\u001d\t\u0003\u001d=L!\u0001]\b\u0003\u0007\u0005s\u0017\u0010C\u0003s\u0001\u0011\u00053/\u0001\u0005u_N#(/\u001b8h)\u0005y\u0006\"B;\u0001\t\u00032\u0018\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003]\u0001"
)
public class ExecutorDesc {
   private final int id;
   private final ApplicationInfo application;
   private final WorkerInfo worker;
   private final int cores;
   private final int memory;
   private Enumeration.Value state;

   public int id() {
      return this.id;
   }

   public ApplicationInfo application() {
      return this.application;
   }

   public WorkerInfo worker() {
      return this.worker;
   }

   public int cores() {
      return this.cores;
   }

   public int memory() {
      return this.memory;
   }

   public Enumeration.Value state() {
      return this.state;
   }

   public void state_$eq(Enumeration.Value x$1) {
      this.state = x$1;
   }

   public void copyState(ExecutorDescription execDesc) {
      this.state_$eq(execDesc.state());
   }

   public String fullId() {
      return (new StringBuilder()).append((Object)this.application().id()).append((Object)"/").append((Object)BoxesRunTime.boxToInteger(this.id())).toString();
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof ExecutorDesc) {
         boolean var8;
         label33: {
            label37: {
               ExecutorDesc var3 = (ExecutorDesc)other;
               String var10000 = this.fullId();
               String var5 = var3.fullId();
               if (var10000 == null) {
                  if (var5 != null) {
                     break label37;
                  }
               } else if (!var10000.equals(var5)) {
                  break label37;
               }

               var10000 = this.worker().id();
               String var6 = var3.worker().id();
               if (var10000 == null) {
                  if (var6 != null) {
                     break label37;
                  }
               } else if (!var10000.equals(var6)) {
                  break label37;
               }

               if (this.cores() == var3.cores() && this.memory() == var3.memory()) {
                  var8 = true;
                  break label33;
               }
            }

            var8 = false;
         }

         var4 = var8;
      } else {
         var4 = false;
      }

      return var4;
   }

   public String toString() {
      return this.fullId();
   }

   public int hashCode() {
      return this.toString().hashCode();
   }

   public ExecutorDesc(int id, ApplicationInfo application, WorkerInfo worker, int cores, int memory) {
      this.id = id;
      this.application = application;
      this.worker = worker;
      this.cores = cores;
      this.memory = memory;
      this.state = ExecutorState$.MODULE$.LAUNCHING();
   }
}
