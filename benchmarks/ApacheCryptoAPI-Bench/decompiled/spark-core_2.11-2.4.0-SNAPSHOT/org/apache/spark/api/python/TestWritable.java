package org.apache.spark.api.python;

import java.io.DataInput;
import java.io.DataOutput;
import org.apache.hadoop.io.Writable;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055g\u0001B\u0001\u0003\u00016\u0011A\u0002V3ti^\u0013\u0018\u000e^1cY\u0016T!a\u0001\u0003\u0002\rALH\u000f[8o\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001'\u0015\u0001aB\u0006\u0010%!\tyA#D\u0001\u0011\u0015\t\t\"#\u0001\u0003mC:<'\"A\n\u0002\t)\fg/Y\u0005\u0003+A\u0011aa\u00142kK\u000e$\bCA\f\u001d\u001b\u0005A\"BA\r\u001b\u0003\tIwN\u0003\u0002\u001c\u0011\u00051\u0001.\u00193p_BL!!\b\r\u0003\u0011]\u0013\u0018\u000e^1cY\u0016\u0004\"a\b\u0012\u000e\u0003\u0001R\u0011!I\u0001\u0006g\u000e\fG.Y\u0005\u0003G\u0001\u0012q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002 K%\u0011a\u0005\t\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\tQ\u0001\u0011\t\u001a!C\u0001S\u0005\u00191\u000f\u001e:\u0016\u0003)\u0002\"a\u000b\u0018\u000f\u0005}a\u0013BA\u0017!\u0003\u0019\u0001&/\u001a3fM&\u0011q\u0006\r\u0002\u0007'R\u0014\u0018N\\4\u000b\u00055\u0002\u0003\u0002\u0003\u001a\u0001\u0005\u0003\u0007I\u0011A\u001a\u0002\u000fM$(o\u0018\u0013fcR\u0011Ag\u000e\t\u0003?UJ!A\u000e\u0011\u0003\tUs\u0017\u000e\u001e\u0005\bqE\n\t\u00111\u0001+\u0003\rAH%\r\u0005\tu\u0001\u0011\t\u0012)Q\u0005U\u0005!1\u000f\u001e:!\u0011!a\u0004A!e\u0001\n\u0003i\u0014aA5oiV\ta\b\u0005\u0002 \u007f%\u0011\u0001\t\t\u0002\u0004\u0013:$\b\u0002\u0003\"\u0001\u0005\u0003\u0007I\u0011A\"\u0002\u000f%tGo\u0018\u0013fcR\u0011A\u0007\u0012\u0005\bq\u0005\u000b\t\u00111\u0001?\u0011!1\u0005A!E!B\u0013q\u0014\u0001B5oi\u0002B\u0001\u0002\u0013\u0001\u0003\u0012\u0004%\t!S\u0001\u0007I>,(\r\\3\u0016\u0003)\u0003\"aH&\n\u00051\u0003#A\u0002#pk\ndW\r\u0003\u0005O\u0001\t\u0005\r\u0011\"\u0001P\u0003)!w.\u001e2mK~#S-\u001d\u000b\u0003iACq\u0001O'\u0002\u0002\u0003\u0007!\n\u0003\u0005S\u0001\tE\t\u0015)\u0003K\u0003\u001d!w.\u001e2mK\u0002BQ\u0001\u0016\u0001\u0005\u0002U\u000ba\u0001P5oSRtD\u0003\u0002,Y3j\u0003\"a\u0016\u0001\u000e\u0003\tAQ\u0001K*A\u0002)BQ\u0001P*A\u0002yBQ\u0001S*A\u0002)CQ\u0001\u0016\u0001\u0005\u0002q#\u0012A\u0016\u0005\u0006=\u0002!\t!K\u0001\u0007O\u0016$8\u000b\u001e:\t\u000b\u0001\u0004A\u0011A1\u0002\rM,Go\u0015;s)\t!$\rC\u0003)?\u0002\u0007!\u0006C\u0003e\u0001\u0011\u0005Q(\u0001\u0004hKRLe\u000e\u001e\u0005\u0006M\u0002!\taZ\u0001\u0007g\u0016$\u0018J\u001c;\u0015\u0005QB\u0007\"\u0002\u001ff\u0001\u0004q\u0004\"\u00026\u0001\t\u0003I\u0015!C4fi\u0012{WO\u00197f\u0011\u0015a\u0007\u0001\"\u0001n\u0003%\u0019X\r\u001e#pk\ndW\r\u0006\u00025]\")\u0001j\u001ba\u0001\u0015\")\u0001\u000f\u0001C\u0001c\u0006)qO]5uKR\u0011AG\u001d\u0005\u0006g>\u0004\r\u0001^\u0001\u0004_V$\bCA;x\u001b\u00051(BA\r\u0013\u0013\tAhO\u0001\u0006ECR\fw*\u001e;qkRDQA\u001f\u0001\u0005\u0002m\f!B]3bI\u001aKW\r\u001c3t)\t!D\u0010C\u0003~s\u0002\u0007a0\u0001\u0002j]B\u0011Qo`\u0005\u0004\u0003\u00031(!\u0003#bi\u0006Le\u000e];u\u0011%\t)\u0001AA\u0001\n\u0003\t9!\u0001\u0003d_BLHc\u0002,\u0002\n\u0005-\u0011Q\u0002\u0005\tQ\u0005\r\u0001\u0013!a\u0001U!AA(a\u0001\u0011\u0002\u0003\u0007a\b\u0003\u0005I\u0003\u0007\u0001\n\u00111\u0001K\u0011%\t\t\u0002AI\u0001\n\u0003\t\u0019\"\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0005\u0005U!f\u0001\u0016\u0002\u0018-\u0012\u0011\u0011\u0004\t\u0005\u00037\t)#\u0004\u0002\u0002\u001e)!\u0011qDA\u0011\u0003%)hn\u00195fG.,GMC\u0002\u0002$\u0001\n!\"\u00198o_R\fG/[8o\u0013\u0011\t9#!\b\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0005\u0002,\u0001\t\n\u0011\"\u0001\u0002.\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012TCAA\u0018U\rq\u0014q\u0003\u0005\n\u0003g\u0001\u0011\u0013!C\u0001\u0003k\tabY8qs\u0012\"WMZ1vYR$3'\u0006\u0002\u00028)\u001a!*a\u0006\t\u0013\u0005m\u0002!!A\u0005B\u0005u\u0012!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070\u0006\u0002\u0002@A\u0019q\"!\u0011\n\u0005=\u0002\u0002\u0002CA#\u0001\u0005\u0005I\u0011A\u001f\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\t\u0013\u0005%\u0003!!A\u0005\u0002\u0005-\u0013A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0005\u0003\u001b\n\u0019\u0006E\u0002 \u0003\u001fJ1!!\u0015!\u0005\r\te.\u001f\u0005\tq\u0005\u001d\u0013\u0011!a\u0001}!I\u0011q\u000b\u0001\u0002\u0002\u0013\u0005\u0013\u0011L\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u00111\f\t\u0007\u0003;\n\u0019'!\u0014\u000e\u0005\u0005}#bAA1A\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\t\u0005\u0015\u0014q\f\u0002\t\u0013R,'/\u0019;pe\"I\u0011\u0011\u000e\u0001\u0002\u0002\u0013\u0005\u00111N\u0001\tG\u0006tW)];bYR!\u0011QNA:!\ry\u0012qN\u0005\u0004\u0003c\u0002#a\u0002\"p_2,\u0017M\u001c\u0005\nq\u0005\u001d\u0014\u0011!a\u0001\u0003\u001bB\u0011\"a\u001e\u0001\u0003\u0003%\t%!\u001f\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012A\u0010\u0005\n\u0003{\u0002\u0011\u0011!C!\u0003\u007f\n\u0001\u0002^8TiJLgn\u001a\u000b\u0003\u0003\u007fA\u0011\"a!\u0001\u0003\u0003%\t%!\"\u0002\r\u0015\fX/\u00197t)\u0011\ti'a\"\t\u0013a\n\t)!AA\u0002\u00055s!CAF\u0005\u0005\u0005\t\u0012AAG\u00031!Vm\u001d;Xe&$\u0018M\u00197f!\r9\u0016q\u0012\u0004\t\u0003\t\t\t\u0011#\u0001\u0002\u0012N)\u0011qRAJIAA\u0011QSANUyRe+\u0004\u0002\u0002\u0018*\u0019\u0011\u0011\u0014\u0011\u0002\u000fI,h\u000e^5nK&!\u0011QTAL\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|gn\r\u0005\b)\u0006=E\u0011AAQ)\t\ti\t\u0003\u0006\u0002~\u0005=\u0015\u0011!C#\u0003\u007fB!\"a*\u0002\u0010\u0006\u0005I\u0011QAU\u0003\u0015\t\u0007\u000f\u001d7z)\u001d1\u00161VAW\u0003_Ca\u0001KAS\u0001\u0004Q\u0003B\u0002\u001f\u0002&\u0002\u0007a\b\u0003\u0004I\u0003K\u0003\rA\u0013\u0005\u000b\u0003g\u000by)!A\u0005\u0002\u0006U\u0016aB;oCB\u0004H.\u001f\u000b\u0005\u0003o\u000b\u0019\rE\u0003 \u0003s\u000bi,C\u0002\u0002<\u0002\u0012aa\u00149uS>t\u0007CB\u0010\u0002@*r$*C\u0002\u0002B\u0002\u0012a\u0001V;qY\u0016\u001c\u0004\"CAc\u0003c\u000b\t\u00111\u0001W\u0003\rAH\u0005\r\u0005\u000b\u0003\u0013\fy)!A\u0005\n\u0005-\u0017a\u0003:fC\u0012\u0014Vm]8mm\u0016$\u0012A\u0004"
)
public class TestWritable implements Writable, Product, Serializable {
   private String str;
   private int int;
   private double double;

   public static Option<Tuple3<String, Object, Object>> unapply(TestWritable var0) {
      return TestWritable$.MODULE$.unapply(var0);
   }

   public static TestWritable apply(String var0, int var1, double var2) {
      return TestWritable$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<String, Object, Object>, TestWritable> tupled() {
      return TestWritable$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Object, Function1<Object, TestWritable>>> curried() {
      return TestWritable$.MODULE$.curried();
   }

   public String str() {
      return this.str;
   }

   public void str_$eq(String x$1) {
      this.str = x$1;
   }

   public int int() {
      return this.int;
   }

   public void int_$eq(int x$1) {
      this.int = x$1;
   }

   public double double() {
      return this.double;
   }

   public void double_$eq(double x$1) {
      this.double = x$1;
   }

   public String getStr() {
      return this.str();
   }

   public void setStr(String str) {
      this.str_$eq(str);
   }

   public int getInt() {
      return this.int();
   }

   public void setInt(int var1) {
      this.int_$eq(var1);
   }

   public double getDouble() {
      return this.double();
   }

   public void setDouble(double var1) {
      this.double_$eq(var1);
   }

   public void write(DataOutput out) {
      out.writeUTF(this.str());
      out.writeInt(this.int());
      out.writeDouble(this.double());
   }

   public void readFields(DataInput in) {
      this.str_$eq(in.readUTF());
      this.int_$eq(in.readInt());
      this.double_$eq(in.readDouble());
   }

   public TestWritable copy(String str, int var2, double var3) {
      return new TestWritable(str, var2, var3);
   }

   public String copy$default$1() {
      return this.str();
   }

   public int copy$default$2() {
      return this.int();
   }

   public double copy$default$3() {
      return this.double();
   }

   public String productPrefix() {
      return "TestWritable";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.str();
            break;
         case 1:
            var10000 = BoxesRunTime.boxToInteger(this.int());
            break;
         case 2:
            var10000 = BoxesRunTime.boxToDouble(this.double());
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof TestWritable;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.anyHash(this.str()));
      var1 = Statics.mix(var1, this.int());
      var1 = Statics.mix(var1, Statics.doubleHash(this.double()));
      return Statics.finalizeHash(var1, 3);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label56: {
            boolean var3;
            if (x$1 instanceof TestWritable) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label39: {
                  label38: {
                     TestWritable var4 = (TestWritable)x$1;
                     String var10000 = this.str();
                     String var5 = var4.str();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label38;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label38;
                     }

                     if (this.int() == var4.int() && this.double() == var4.double() && var4.canEqual(this)) {
                        var7 = true;
                        break label39;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label56;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public TestWritable(String str, int var2, double var3) {
      this.str = str;
      this.int = var2;
      this.double = var3;
      super();
      Product$class.$init$(this);
   }

   public TestWritable() {
      this("", 0, (double)0.0F);
   }
}
