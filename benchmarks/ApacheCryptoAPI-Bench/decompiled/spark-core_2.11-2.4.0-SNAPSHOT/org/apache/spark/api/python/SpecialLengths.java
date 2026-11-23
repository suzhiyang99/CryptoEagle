package org.apache.spark.api.python;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001]:a!\u0001\u0002\t\u0002\u0019a\u0011AD*qK\u000eL\u0017\r\u001c'f]\u001e$\bn\u001d\u0006\u0003\u0007\u0011\ta\u0001]=uQ>t'BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOB\u0011QBD\u0007\u0002\u0005\u00191qB\u0001E\u0001\rA\u0011ab\u00159fG&\fG\u000eT3oORD7o\u0005\u0002\u000f#A\u0011!#F\u0007\u0002')\tA#A\u0003tG\u0006d\u0017-\u0003\u0002\u0017'\t1\u0011I\\=SK\u001aDQ\u0001\u0007\b\u0005\u0002i\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0019!9AD\u0004b\u0001\n\u0003i\u0012aE#O\t~{ei\u0018#B)\u0006{6+R\"U\u0013>sU#\u0001\u0010\u0011\u0005Iy\u0012B\u0001\u0011\u0014\u0005\rIe\u000e\u001e\u0005\u0007E9\u0001\u000b\u0011\u0002\u0010\u0002)\u0015sEiX(G?\u0012\u000bE+Q0T\u000b\u000e#\u0016j\u0014(!\u0011\u001d!cB1A\u0005\u0002u\tq\u0003U-U\u0011>su,\u0012-D\u000bB#\u0016j\u0014(`)\"\u0013vj\u0016(\t\r\u0019r\u0001\u0015!\u0003\u001f\u0003a\u0001\u0016\f\u0016%P\u001d~+\u0005lQ#Q)&{ej\u0018+I%>;f\n\t\u0005\bQ9\u0011\r\u0011\"\u0001\u001e\u0003-!\u0016*T%O\u000f~#\u0015\tV!\t\r)r\u0001\u0015!\u0003\u001f\u00031!\u0016*T%O\u000f~#\u0015\tV!!\u0011\u001dacB1A\u0005\u0002u\tQ\"\u0012(E?>3ul\u0015+S\u000b\u0006k\u0005B\u0002\u0018\u000fA\u0003%a$\u0001\bF\u001d\u0012{vJR0T)J+\u0015)\u0014\u0011\t\u000fAr!\u0019!C\u0001;\u0005!a*\u0016'M\u0011\u0019\u0011d\u0002)A\u0005=\u0005)a*\u0016'MA!9AG\u0004b\u0001\n\u0003i\u0012AE*U\u0003J#v,\u0011*S\u001f^{6\u000b\u0016*F\u00036CaA\u000e\b!\u0002\u0013q\u0012aE*U\u0003J#v,\u0011*S\u001f^{6\u000b\u0016*F\u00036\u0003\u0003"
)
public final class SpecialLengths {
   public static int START_ARROW_STREAM() {
      return SpecialLengths$.MODULE$.START_ARROW_STREAM();
   }

   public static int NULL() {
      return SpecialLengths$.MODULE$.NULL();
   }

   public static int END_OF_STREAM() {
      return SpecialLengths$.MODULE$.END_OF_STREAM();
   }

   public static int TIMING_DATA() {
      return SpecialLengths$.MODULE$.TIMING_DATA();
   }

   public static int PYTHON_EXCEPTION_THROWN() {
      return SpecialLengths$.MODULE$.PYTHON_EXCEPTION_THROWN();
   }

   public static int END_OF_DATA_SECTION() {
      return SpecialLengths$.MODULE$.END_OF_DATA_SECTION();
   }
}
