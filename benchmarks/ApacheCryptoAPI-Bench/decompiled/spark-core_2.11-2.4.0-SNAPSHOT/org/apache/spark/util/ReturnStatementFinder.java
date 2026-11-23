package org.apache.spark.util;

import org.apache.xbean.asm5.ClassVisitor;
import org.apache.xbean.asm5.MethodVisitor;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001e2A!\u0001\u0002\u0005\u0017\t)\"+\u001a;ve:\u001cF/\u0019;f[\u0016tGOR5oI\u0016\u0014(BA\u0002\u0005\u0003\u0011)H/\u001b7\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0002\u0001\u0019A\u0011QBE\u0007\u0002\u001d)\u0011q\u0002E\u0001\u0005CNlWG\u0003\u0002\u0012\r\u0005)\u0001PY3b]&\u00111C\u0004\u0002\r\u00072\f7o\u001d,jg&$xN\u001d\u0005\u0006+\u0001!\tAF\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003]\u0001\"\u0001\u0007\u0001\u000e\u0003\tAQA\u0007\u0001\u0005Bm\t1B^5tSRlU\r\u001e5pIR1AdH\u00141eQ\u0002\"!D\u000f\n\u0005yq!!D'fi\"|GMV5tSR|'\u000fC\u0003!3\u0001\u0007\u0011%\u0001\u0004bG\u000e,7o\u001d\t\u0003E\u0015j\u0011a\t\u0006\u0002I\u0005)1oY1mC&\u0011ae\t\u0002\u0004\u0013:$\b\"\u0002\u0015\u001a\u0001\u0004I\u0013\u0001\u00028b[\u0016\u0004\"AK\u0017\u000f\u0005\tZ\u0013B\u0001\u0017$\u0003\u0019\u0001&/\u001a3fM&\u0011af\f\u0002\u0007'R\u0014\u0018N\\4\u000b\u00051\u001a\u0003\"B\u0019\u001a\u0001\u0004I\u0013\u0001\u00023fg\u000eDQaM\rA\u0002%\n1a]5h\u0011\u0015)\u0014\u00041\u00017\u0003))\u0007pY3qi&|gn\u001d\t\u0004E]J\u0013B\u0001\u001d$\u0005\u0015\t%O]1z\u0001"
)
public class ReturnStatementFinder extends ClassVisitor {
   public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
      return !name.contains("apply") && !name.contains("$anonfun$") ? new MethodVisitor(this) {
      } : new MethodVisitor(this) {
         public void visitTypeInsn(int op, String tp) {
            if (op == 187 && tp.contains("scala/runtime/NonLocalReturnControl")) {
               throw new ReturnStatementInClosureException();
            }
         }
      };
   }

   public ReturnStatementFinder() {
      super(327680);
   }
}
