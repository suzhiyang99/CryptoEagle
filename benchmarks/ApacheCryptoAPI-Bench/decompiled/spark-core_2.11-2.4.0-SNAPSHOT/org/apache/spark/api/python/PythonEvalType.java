package org.apache.spark.api.python;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001}:a!\u0001\u0002\t\u0002\u0019a\u0011A\u0004)zi\"|g.\u0012<bYRK\b/\u001a\u0006\u0003\u0007\u0011\ta\u0001]=uQ>t'BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sOB\u0011QBD\u0007\u0002\u0005\u00191qB\u0001E\u0001\rA\u0011a\u0002U=uQ>tWI^1m)f\u0004Xm\u0005\u0002\u000f#A\u0011!#F\u0007\u0002')\tA#A\u0003tG\u0006d\u0017-\u0003\u0002\u0017'\t1\u0011I\\=SK\u001aDQ\u0001\u0007\b\u0005\u0002i\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0019!9AD\u0004b\u0001\n\u0003i\u0012a\u0002(P\u001d~+FIR\u000b\u0002=A\u0011!cH\u0005\u0003AM\u00111!\u00138u\u0011\u0019\u0011c\u0002)A\u0005=\u0005Aaj\u0014(`+\u00123\u0005\u0005C\u0004%\u001d\t\u0007I\u0011A\u000f\u0002\u001fM\u000bFj\u0018\"B)\u000eCU\tR0V\t\u001aCaA\n\b!\u0002\u0013q\u0012\u0001E*R\u0019~\u0013\u0015\tV\"I\u000b\u0012{V\u000b\u0012$!\u0011\u001dAcB1A\u0005\u0002u\tQcU)M?N\u001b\u0015\tT!S?B\u000be\nR!T?V#e\t\u0003\u0004+\u001d\u0001\u0006IAH\u0001\u0017'FculU\"B\u0019\u0006\u0013v\fU!O\t\u0006\u001bv,\u0016#GA!9AF\u0004b\u0001\n\u0003i\u0012AG*R\u0019~;%kT+Q\u000b\u0012{V*\u0011)`!\u0006sE)Q*`+\u00123\u0005B\u0002\u0018\u000fA\u0003%a$A\u000eT#2{vIU(V!\u0016#u,T!Q?B\u000be\nR!T?V#e\t\t\u0005\ba9\u0011\r\u0011\"\u0001\u001e\u0003i\u0019\u0016\u000bT0H%>+\u0006+\u0012#`\u0003\u001e;u\fU!O\t\u0006\u001bv,\u0016#G\u0011\u0019\u0011d\u0002)A\u0005=\u0005Y2+\u0015'`\u000fJ{U\u000bU#E?\u0006;ui\u0018)B\u001d\u0012\u000b5kX+E\r\u0002BQ\u0001\u000e\b\u0005\u0002U\n\u0001\u0002^8TiJLgn\u001a\u000b\u0003mu\u0002\"a\u000e\u001e\u000f\u0005IA\u0014BA\u001d\u0014\u0003\u0019\u0001&/\u001a3fM&\u00111\b\u0010\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005e\u001a\u0002\"\u0002 4\u0001\u0004q\u0012A\u00049zi\"|g.\u0012<bYRK\b/\u001a"
)
public final class PythonEvalType {
   public static String toString(int var0) {
      return PythonEvalType$.MODULE$.toString(var0);
   }

   public static int SQL_GROUPED_AGG_PANDAS_UDF() {
      return PythonEvalType$.MODULE$.SQL_GROUPED_AGG_PANDAS_UDF();
   }

   public static int SQL_GROUPED_MAP_PANDAS_UDF() {
      return PythonEvalType$.MODULE$.SQL_GROUPED_MAP_PANDAS_UDF();
   }

   public static int SQL_SCALAR_PANDAS_UDF() {
      return PythonEvalType$.MODULE$.SQL_SCALAR_PANDAS_UDF();
   }

   public static int SQL_BATCHED_UDF() {
      return PythonEvalType$.MODULE$.SQL_BATCHED_UDF();
   }

   public static int NON_UDF() {
      return PythonEvalType$.MODULE$.NON_UDF();
   }
}
