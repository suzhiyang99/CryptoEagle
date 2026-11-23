package org.apache.spark.util;

import java.io.PrintStream;
import scala.Function1;
import scala.Tuple2;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001e3\u0001\"\u0001\u0002\u0011\u0002\u0007\u0005AA\u0003\u0002\u0011\u0007>lW.\u00198e\u0019&tW-\u0016;jYNT!a\u0001\u0003\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0011\u0001a\u0003\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\t\u000bI\u0001A\u0011\u0001\u000b\u0002\r\u0011Jg.\u001b;%\u0007\u0001!\u0012!\u0006\t\u0003\u0019YI!aF\u0007\u0003\tUs\u0017\u000e\u001e\u0005\t3\u0001\u0001\r\u0011\"\u0001\u00055\u00051Q\r_5u\r:,\u0012a\u0007\t\u0005\u0019qqR#\u0003\u0002\u001e\u001b\tIa)\u001e8di&|g.\r\t\u0003\u0019}I!\u0001I\u0007\u0003\u0007%sG\u000f\u0003\u0005#\u0001\u0001\u0007I\u0011\u0001\u0003$\u0003))\u00070\u001b;G]~#S-\u001d\u000b\u0003+\u0011Bq!J\u0011\u0002\u0002\u0003\u00071$A\u0002yIEBaa\n\u0001!B\u0013Y\u0012aB3ySR4e\u000e\t\u0005\tS\u0001\u0001\r\u0011\"\u0001\u0005U\u0005Y\u0001O]5oiN#(/Z1n+\u0005Y\u0003C\u0001\u00172\u001b\u0005i#B\u0001\u00180\u0003\tIwNC\u00011\u0003\u0011Q\u0017M^1\n\u0005Ij#a\u0003)sS:$8\u000b\u001e:fC6D\u0001\u0002\u000e\u0001A\u0002\u0013\u0005A!N\u0001\u0010aJLg\u000e^*ue\u0016\fWn\u0018\u0013fcR\u0011QC\u000e\u0005\bKM\n\t\u00111\u0001,\u0011\u0019A\u0004\u0001)Q\u0005W\u0005a\u0001O]5oiN#(/Z1nA!1!\b\u0001C\u0001\tm\nA\u0002\u001d:j]R<\u0016M\u001d8j]\u001e$\"!\u0006\u001f\t\u000buJ\u0004\u0019\u0001 \u0002\u0007M$(\u000f\u0005\u0002@\u0005:\u0011A\u0002Q\u0005\u0003\u00036\ta\u0001\u0015:fI\u00164\u0017BA\"E\u0005\u0019\u0019FO]5oO*\u0011\u0011)\u0004\u0005\u0007\r\u0002!\t\u0001B$\u0002#A\u0014\u0018N\u001c;FeJ|'/\u00118e\u000bbLG\u000f\u0006\u0002\u0016\u0011\")Q(\u0012a\u0001}!1!\n\u0001C\u0001\t-\u000ba\u0003]1sg\u0016\u001c\u0006/\u0019:l\u0007>tg\r\u0015:pa\u0016\u0014H/\u001f\u000b\u0003\u0019>\u0003B\u0001D'?}%\u0011a*\u0004\u0002\u0007)V\u0004H.\u001a\u001a\t\u000bAK\u0005\u0019\u0001 \u0002\tA\f\u0017N\u001d\u0005\u0006%\u00021\taU\u0001\u0005[\u0006Lg\u000e\u0006\u0002\u0016)\")Q+\u0015a\u0001-\u0006!\u0011M]4t!\raqKP\u0005\u000316\u0011Q!\u0011:sCf\u0004"
)
public interface CommandLineUtils {
   Function1<Object, BoxedUnit> exitFn();

   @TraitSetter
   void exitFn_$eq(Function1<Object, BoxedUnit> var1);

   PrintStream printStream();

   @TraitSetter
   void printStream_$eq(PrintStream var1);

   void printWarning(String var1);

   void printErrorAndExit(String var1);

   Tuple2<String, String> parseSparkConfProperty(String var1);

   void main(String[] var1);
}
