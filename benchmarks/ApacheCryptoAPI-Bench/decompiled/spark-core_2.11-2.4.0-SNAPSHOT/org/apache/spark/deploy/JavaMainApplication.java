package org.apache.spark.deploy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.spark.SparkConf;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.sys.package$;

@ScalaSignature(
   bytes = "\u0006\u0001!3Q!\u0001\u0002\u0001\u0005)\u00111CS1wC6\u000b\u0017N\\!qa2L7-\u0019;j_:T!a\u0001\u0003\u0002\r\u0011,\u0007\u000f\\8z\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7c\u0001\u0001\f#A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003!M\u0003\u0018M]6BaBd\u0017nY1uS>t\u0007\u0002\u0003\f\u0001\u0005\u0003\u0005\u000b\u0011\u0002\r\u0002\u000b-d\u0017m]:\u0004\u0001A\u0012\u0011D\t\t\u00045u\u0001cB\u0001\u0007\u001c\u0013\taR\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003=}\u0011Qa\u00117bgNT!\u0001H\u0007\u0011\u0005\u0005\u0012C\u0002\u0001\u0003\nGU\t\t\u0011!A\u0003\u0002\u0011\u00121a\u0018\u00132#\t)\u0003\u0006\u0005\u0002\rM%\u0011q%\u0004\u0002\b\u001d>$\b.\u001b8h!\ta\u0011&\u0003\u0002+\u001b\t\u0019\u0011I\\=\t\u000b1\u0002A\u0011A\u0017\u0002\rqJg.\u001b;?)\tqs\u0006\u0005\u0002\u0013\u0001!)ac\u000ba\u0001aA\u0012\u0011g\r\t\u00045u\u0011\u0004CA\u00114\t%\u0019s&!A\u0001\u0002\u000b\u0005A\u0005C\u00036\u0001\u0011\u0005c'A\u0003ti\u0006\u0014H\u000fF\u00028u\t\u0003\"\u0001\u0004\u001d\n\u0005ej!\u0001B+oSRDQa\u000f\u001bA\u0002q\nA!\u0019:hgB\u0019A\"P \n\u0005yj!!B!se\u0006L\bC\u0001\u000eA\u0013\t\tuD\u0001\u0004TiJLgn\u001a\u0005\u0006\u0007R\u0002\r\u0001R\u0001\u0005G>tg\r\u0005\u0002F\r6\tA!\u0003\u0002H\t\tI1\u000b]1sW\u000e{gN\u001a"
)
public class JavaMainApplication implements SparkApplication {
   private final Class<?> klass;

   public void start(String[] args, SparkConf conf) {
      Method mainMethod = this.klass.getMethod("main", (new String[0]).getClass());
      if (Modifier.isStatic(mainMethod.getModifiers())) {
         Map sysProps = Predef$.MODULE$.refArrayOps(conf.getAll()).toMap(Predef$.MODULE$.$conforms());
         sysProps.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(Tuple2<String, String> x0$1) {
               if (x0$1 != null) {
                  String k = (String)x0$1._1();
                  String v = (String)x0$1._2();
                  package$.MODULE$.props().update(k, v);
                  BoxedUnit var5 = BoxedUnit.UNIT;
               } else {
                  throw new MatchError(x0$1);
               }
            }
         });
         mainMethod.invoke((Object)null, args);
      } else {
         throw new IllegalStateException("The main method in the given main class must be static");
      }
   }

   public JavaMainApplication(Class<?> klass) {
      this.klass = klass;
   }
}
