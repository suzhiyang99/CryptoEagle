package org.apache.spark;

import java.io.File;
import java.net.URL;
import javax.tools.SimpleJavaFileObject;
import scala.Function0;
import scala.Option;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\t\rrAB\u0001\u0003\u0011\u0003\u0011\u0001\"A\u0005UKN$X\u000b^5mg*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014x\r\u0005\u0002\n\u00155\t!A\u0002\u0004\f\u0005!\u0005!\u0001\u0004\u0002\n)\u0016\u001cH/\u0016;jYN\u001c\"AC\u0007\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g\u0011\u0015!\"\u0002\"\u0001\u0017\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0005\t\u000baQA\u0011A\r\u0002)\r\u0014X-\u0019;f\u0015\u0006\u0014x+\u001b;i\u00072\f7o]3t)\u0015Q\"eN\u001d@!\tY\u0002%D\u0001\u001d\u0015\tib$A\u0002oKRT\u0011aH\u0001\u0005U\u00064\u0018-\u0003\u0002\"9\t\u0019QK\u0015'\t\u000b\r:\u0002\u0019\u0001\u0013\u0002\u0015\rd\u0017m]:OC6,7\u000fE\u0002&[Ar!AJ\u0016\u000f\u0005\u001dRS\"\u0001\u0015\u000b\u0005%*\u0012A\u0002\u001fs_>$h(C\u0001\u0011\u0013\tas\"A\u0004qC\u000e\\\u0017mZ3\n\u00059z#aA*fc*\u0011Af\u0004\t\u0003cQr!A\u0004\u001a\n\u0005Mz\u0011A\u0002)sK\u0012,g-\u0003\u00026m\t11\u000b\u001e:j]\u001eT!aM\b\t\u000fa:\u0002\u0013!a\u0001a\u0005iAo\\*ue&twMV1mk\u0016DqAO\f\u0011\u0002\u0003\u00071(\u0001\ndY\u0006\u001c8OT1nKN<\u0016\u000e\u001e5CCN,\u0007cA\u0013.yA!a\"\u0010\u00191\u0013\tqtB\u0001\u0004UkBdWM\r\u0005\b\u0001^\u0001\n\u00111\u0001B\u00035\u0019G.Y:ta\u0006$\b.\u0016:mgB\u0019Q%\f\u000e\t\u000b\rSA\u0011\u0001#\u0002%\r\u0014X-\u0019;f\u0015\u0006\u0014x+\u001b;i\r&dWm\u001d\u000b\u00045\u0015S\u0005\"\u0002$C\u0001\u00049\u0015!\u00024jY\u0016\u001c\b\u0003B\u0019IaAJ!!\u0013\u001c\u0003\u00075\u000b\u0007\u000fC\u0004L\u0005B\u0005\t\u0019\u0001'\u0002\u0007\u0011L'\u000f\u0005\u0002N!6\taJ\u0003\u0002P=\u0005\u0011\u0011n\\\u0005\u0003#:\u0013AAR5mK\")1K\u0003C\u0001)\u0006I1M]3bi\u0016T\u0015M\u001d\u000b\u00055U;\u0016\fC\u0003G%\u0002\u0007a\u000bE\u0002&[1CQ\u0001\u0017*A\u00021\u000bqA[1s\r&dW\rC\u0004[%B\u0005\t\u0019A.\u0002\u001f\u0011L'/Z2u_JL\bK]3gSb\u00042A\u0004/1\u0013\tivB\u0001\u0004PaRLwN\u001c\u0005\b?*\u0011\r\u0011\"\u0003a\u0003\u0019\u0019v*\u0016*D\u000bV\t\u0011\r\u0005\u0002cS6\t1M\u0003\u0002eK\u0006q!*\u0019<b\r&dWm\u00142kK\u000e$(B\u00014h\u0003\u0015!xn\u001c7t\u0015\u0005A\u0017!\u00026bm\u0006D\u0018B\u00016d\u0005\u0011Y\u0015N\u001c3\t\r1T\u0001\u0015!\u0003b\u0003\u001d\u0019v*\u0016*D\u000b\u0002BQA\u001c\u0006\u0005\n=\f\u0011b\u0019:fCR,WKU%\u0015\u0005A\u001c\bCA\u000er\u0013\t\u0011HDA\u0002V%&CQ\u0001^7A\u0002A\nAA\\1nK\u001a)aO\u0003\u0001\u0003o\n!\"*\u0019<b'>,(oY3Ge>l7\u000b\u001e:j]\u001e\u001c\"!\u001e=\u0011\u0005eTX\"A3\n\u0005m,'\u0001F*j[BdWMS1wC\u001aKG.Z(cU\u0016\u001cG\u000f\u0003\u0005uk\n\u0015\r\u0011\"\u0001~+\u0005\u0001\u0004\u0002C@v\u0005\u0003\u0005\u000b\u0011\u0002\u0019\u0002\u000b9\fW.\u001a\u0011\t\u0013\u0005\rQO!b\u0001\n\u0003i\u0018\u0001B2pI\u0016D\u0011\"a\u0002v\u0005\u0003\u0005\u000b\u0011\u0002\u0019\u0002\u000b\r|G-\u001a\u0011\t\rQ)H\u0011AA\u0006)\u0019\ti!!\u0005\u0002\u0014A\u0019\u0011qB;\u000e\u0003)Aa\u0001^A\u0005\u0001\u0004\u0001\u0004bBA\u0002\u0003\u0013\u0001\r\u0001\r\u0005\b\u0003/)H\u0011IA\r\u000399W\r^\"iCJ\u001cuN\u001c;f]R$2\u0001MA\u000e\u0011!\ti\"!\u0006A\u0002\u0005}\u0011\u0001F5h]>\u0014X-\u00128d_\u0012LgnZ#se>\u00148\u000fE\u0002\u000f\u0003CI1!a\t\u0010\u0005\u001d\u0011un\u001c7fC:Dq!a\n\u000b\t\u0003\tI#A\nde\u0016\fG/Z\"p[BLG.\u001a3DY\u0006\u001c8\u000fF\u0005M\u0003W\ty#a\r\u00028!9\u0011QFA\u0013\u0001\u0004\u0001\u0014!C2mCN\u001ch*Y7f\u0011\u001d\t\t$!\nA\u00021\u000bq\u0001Z3ti\u0012K'\u000f\u0003\u0005\u00026\u0005\u0015\u0002\u0019AA\u0007\u0003)\u0019x.\u001e:dK\u001aKG.\u001a\u0005\u0007\u0001\u0006\u0015\u0002\u0019A!\t\u000f\u0005\u001d\"\u0002\"\u0001\u0002<QYA*!\u0010\u0002@\u0005\u0005\u00131IA$\u0011\u001d\ti#!\u000fA\u0002ABq!!\r\u0002:\u0001\u0007A\n\u0003\u00059\u0003s\u0001\n\u00111\u00011\u0011%\t)%!\u000f\u0011\u0002\u0003\u0007\u0001'A\u0005cCN,7\t\\1tg\"A\u0001)!\u000f\u0011\u0002\u0003\u0007\u0011\tC\u0004\u0002L)!\t!!\u0014\u0002\u001b\u0005\u001c8/\u001a:u'BLG\u000e\\3e+\u0011\ty%a\u001a\u0015\r\u0005E\u0013\u0011PAB)\u0011\t\u0019&!\u0017\u0011\u00079\t)&C\u0002\u0002X=\u0011A!\u00168ji\"I\u00111LA%\t\u0003\u0007\u0011QL\u0001\u0005E>$\u0017\u0010E\u0003\u000f\u0003?\n\u0019'C\u0002\u0002b=\u0011\u0001\u0002\u00102z]\u0006lWM\u0010\t\u0005\u0003K\n9\u0007\u0004\u0001\u0005\u0011\u0005%\u0014\u0011\nb\u0001\u0003W\u0012\u0011\u0001V\t\u0005\u0003[\n\u0019\bE\u0002\u000f\u0003_J1!!\u001d\u0010\u0005\u001dqu\u000e\u001e5j]\u001e\u00042ADA;\u0013\r\t9h\u0004\u0002\u0004\u0003:L\b\u0002CA>\u0003\u0013\u0002\r!! \u0002\u0005M\u001c\u0007cA\u0005\u0002\u0000%\u0019\u0011\u0011\u0011\u0002\u0003\u0019M\u0003\u0018M]6D_:$X\r\u001f;\t\u000f\u0005\u0015\u0015\u0011\na\u0001a\u0005Q\u0011\u000eZ3oi&4\u0017.\u001a:\t\u000f\u0005%%\u0002\"\u0001\u0002\f\u0006\u0001\u0012m]:feRtu\u000e^*qS2dW\rZ\u000b\u0005\u0003\u001b\u000b9\n\u0006\u0004\u0002\u0010\u0006e\u00151\u0014\u000b\u0005\u0003'\n\t\nC\u0005\u0002\\\u0005\u001dE\u00111\u0001\u0002\u0014B)a\"a\u0018\u0002\u0016B!\u0011QMAL\t!\tI'a\"C\u0002\u0005-\u0004\u0002CA>\u0003\u000f\u0003\r!! \t\u000f\u0005\u0015\u0015q\u0011a\u0001a!9\u0011q\u0014\u0006\u0005\u0002\u0005\u0005\u0016\u0001\u0006;fgR\u001cu.\\7b]\u0012\fe/Y5mC\ndW\r\u0006\u0003\u0002 \u0005\r\u0006bBAS\u0003;\u0003\r\u0001M\u0001\bG>lW.\u00198e\u0011\u001d\tIK\u0003C\u0001\u0003W\u000b\u0001\u0003\u001b;uaJ+7\u000f]8og\u0016\u001cu\u000eZ3\u0015\u0011\u00055\u00161WA\\\u0003w\u00032ADAX\u0013\r\t\tl\u0004\u0002\u0004\u0013:$\bbBA[\u0003O\u0003\rAG\u0001\u0004kJd\u0007\"CA]\u0003O\u0003\n\u00111\u00011\u0003\u0019iW\r\u001e5pI\"I\u0011QXAT!\u0003\u0005\raO\u0001\bQ\u0016\fG-\u001a:t\u0011!\t\tM\u0003C\u0001\u0005\u0005\r\u0017\u0001F<bSR,f\u000e^5m\u000bb,7-\u001e;peN,\u0006\u000f\u0006\u0005\u0002T\u0005\u0015\u0017qYAf\u0011!\tY(a0A\u0002\u0005u\u0004\u0002CAe\u0003\u007f\u0003\r!!,\u0002\u00199,X.\u0012=fGV$xN]:\t\u0011\u00055\u0017q\u0018a\u0001\u0003\u001f\fq\u0001^5nK>,H\u000fE\u0002\u000f\u0003#L1!a5\u0010\u0005\u0011auN\\4\t\u0013\u0005]'\"%A\u0005\u0002\u0005e\u0017AH2sK\u0006$XMS1s/&$\bn\u00117bgN,7\u000f\n3fM\u0006,H\u000e\u001e\u00133+\t\tYNK\u00021\u0003;\\#!a8\u0011\t\u0005\u0005\u00181^\u0007\u0003\u0003GTA!!:\u0002h\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003S|\u0011AC1o]>$\u0018\r^5p]&!\u0011Q^Ar\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\n\u0003cT\u0011\u0013!C\u0001\u0003g\fad\u0019:fCR,'*\u0019:XSRD7\t\\1tg\u0016\u001cH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005\u0005U(fA\u001e\u0002^\"I\u0011\u0011 \u0006\u0012\u0002\u0013\u0005\u00111`\u0001\u001fGJ,\u0017\r^3KCJ<\u0016\u000e\u001e5DY\u0006\u001c8/Z:%I\u00164\u0017-\u001e7uIQ*\"!!@+\u0007\u0005\u000bi\u000eC\u0005\u0003\u0002)\t\n\u0011\"\u0001\u0002Z\u0006i2M]3bi\u0016\u001cu.\u001c9jY\u0016$7\t\\1tg\u0012\"WMZ1vYR$3\u0007C\u0005\u0003\u0006)\t\n\u0011\"\u0001\u0002Z\u0006i2M]3bi\u0016\u001cu.\u001c9jY\u0016$7\t\\1tg\u0012\"WMZ1vYR$C\u0007C\u0005\u0003\n)\t\n\u0011\"\u0001\u0002|\u0006i2M]3bi\u0016\u001cu.\u001c9jY\u0016$7\t\\1tg\u0012\"WMZ1vYR$S\u0007C\u0005\u0003\u000e)\t\n\u0011\"\u0001\u0003\u0010\u0005\u00192M]3bi\u0016T\u0015M\u001d\u0013eK\u001a\fW\u000f\u001c;%gU\u0011!\u0011\u0003\u0016\u00047\u0006u\u0007\"\u0003B\u000b\u0015E\u0005I\u0011\u0001B\f\u0003q\u0019'/Z1uK*\u000b'oV5uQ\u001aKG.Z:%I\u00164\u0017-\u001e7uII*\"A!\u0007+\u00071\u000bi\u000eC\u0005\u0003\u001e)\t\n\u0011\"\u0001\u0002Z\u0006Q\u0002\u000e\u001e;q%\u0016\u001c\bo\u001c8tK\u000e{G-\u001a\u0013eK\u001a\fW\u000f\u001c;%e!I!\u0011\u0005\u0006\u0012\u0002\u0013\u0005\u00111_\u0001\u001bQR$\bOU3ta>t7/Z\"pI\u0016$C-\u001a4bk2$He\r"
)
public final class TestUtils {
   public static Seq<Tuple2<String, String>> httpResponseCode$default$3() {
      return TestUtils$.MODULE$.httpResponseCode$default$3();
   }

   public static String httpResponseCode$default$2() {
      return TestUtils$.MODULE$.httpResponseCode$default$2();
   }

   public static File createJarWithFiles$default$2() {
      return TestUtils$.MODULE$.createJarWithFiles$default$2();
   }

   public static Option<String> createJar$default$3() {
      return TestUtils$.MODULE$.createJar$default$3();
   }

   public static Seq<URL> createCompiledClass$default$5() {
      return TestUtils$.MODULE$.createCompiledClass$default$5();
   }

   public static String createCompiledClass$default$4() {
      return TestUtils$.MODULE$.createCompiledClass$default$4();
   }

   public static String createCompiledClass$default$3() {
      return TestUtils$.MODULE$.createCompiledClass$default$3();
   }

   public static Seq<URL> createJarWithClasses$default$4() {
      return TestUtils$.MODULE$.createJarWithClasses$default$4();
   }

   public static Seq<Tuple2<String, String>> createJarWithClasses$default$3() {
      return TestUtils$.MODULE$.createJarWithClasses$default$3();
   }

   public static String createJarWithClasses$default$2() {
      return TestUtils$.MODULE$.createJarWithClasses$default$2();
   }

   public static int httpResponseCode(URL var0, String var1, Seq<Tuple2<String, String>> var2) {
      return TestUtils$.MODULE$.httpResponseCode(var0, var1, var2);
   }

   public static boolean testCommandAvailable(String var0) {
      return TestUtils$.MODULE$.testCommandAvailable(var0);
   }

   public static <T> void assertNotSpilled(SparkContext var0, String var1, Function0<T> var2) {
      TestUtils$.MODULE$.assertNotSpilled(var0, var1, var2);
   }

   public static <T> void assertSpilled(SparkContext var0, String var1, Function0<T> var2) {
      TestUtils$.MODULE$.assertSpilled(var0, var1, var2);
   }

   public static File createCompiledClass(String var0, File var1, String var2, String var3, Seq<URL> var4) {
      return TestUtils$.MODULE$.createCompiledClass(var0, var1, var2, var3, var4);
   }

   public static File createCompiledClass(String var0, File var1, JavaSourceFromString var2, Seq<URL> var3) {
      return TestUtils$.MODULE$.createCompiledClass(var0, var1, var2, var3);
   }

   public static URL createJar(Seq<File> var0, File var1, Option<String> var2) {
      return TestUtils$.MODULE$.createJar(var0, var1, var2);
   }

   public static URL createJarWithFiles(Map<String, String> var0, File var1) {
      return TestUtils$.MODULE$.createJarWithFiles(var0, var1);
   }

   public static URL createJarWithClasses(Seq<String> var0, String var1, Seq<Tuple2<String, String>> var2, Seq<URL> var3) {
      return TestUtils$.MODULE$.createJarWithClasses(var0, var1, var2, var3);
   }

   public static class JavaSourceFromString extends SimpleJavaFileObject {
      private final String name;
      private final String code;

      public String name() {
         return this.name;
      }

      public String code() {
         return this.code;
      }

      public String getCharContent(boolean ignoreEncodingErrors) {
         return this.code();
      }

      public JavaSourceFromString(String name, String code) {
         super(TestUtils$.MODULE$.org$apache$spark$TestUtils$$createURI(name), TestUtils$.MODULE$.org$apache$spark$TestUtils$$SOURCE());
         this.name = name;
         this.code = code;
      }
   }
}
