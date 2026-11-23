package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.Option;
import scala.Product$class;
import scala.Product2;
import scala.Product2$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\tmg\u0001B\u0001\u0003\u0001.\u00111\"T;uC\ndW\rU1je*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001)2\u0001\u0004\rE'\u0015\u0001QbE)U!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB!a\u0002\u0006\fD\u0013\t)rB\u0001\u0005Qe>$Wo\u0019;3!\t9\u0002\u0004\u0004\u0001\u0005\u0013e\u0001\u0001\u0015!A\u0001\u0006\u0004Q\"A\u0001+2#\tYb\u0004\u0005\u0002\u000f9%\u0011Qd\u0004\u0002\b\u001d>$\b.\u001b8h!\tqq$\u0003\u0002!\u001f\t\u0019\u0011I\\=)\u000fa\u0011Se\f\u001b:}A\u0011abI\u0005\u0003I=\u00111b\u001d9fG&\fG.\u001b>fIF*1EJ\u0014*Q9\u0011abJ\u0005\u0003Q=\t1!\u00138uc\u0011!#F\f\t\u000f\u0005-rS\"\u0001\u0017\u000b\u00055R\u0011A\u0002\u001fs_>$h(C\u0001\u0011c\u0015\u0019\u0003'M\u001a3\u001d\tq\u0011'\u0003\u00023\u001f\u0005!Aj\u001c8hc\u0011!#F\f\t2\u000b\r*d\u0007O\u001c\u000f\u000591\u0014BA\u001c\u0010\u0003\u0019!u.\u001e2mKF\"AE\u000b\u0018\u0011c\u0015\u0019#hO\u001f=\u001d\tq1(\u0003\u0002=\u001f\u0005!1\t[1sc\u0011!#F\f\t2\u000b\rz\u0004IQ!\u000f\u00059\u0001\u0015BA!\u0010\u0003\u001d\u0011un\u001c7fC:\fD\u0001\n\u0016/!A\u0011q\u0003\u0012\u0003\n\u000b\u0002\u0001\u000b\u0011!AC\u0002i\u0011!\u0001\u0016\u001a)\u000f\u0011\u0013s)S&N\u001fF*1EJ\u0014IQE\"AE\u000b\u0018\u0011c\u0015\u0019\u0003'\r&3c\u0011!#F\f\t2\u000b\r*d\u0007T\u001c2\t\u0011Rc\u0006E\u0019\u0006GiZd\nP\u0019\u0005I)r\u0003#M\u0003$\u007f\u0001\u0003\u0016)\r\u0003%U9\u0002\u0002C\u0001\bS\u0013\t\u0019vBA\u0004Qe>$Wo\u0019;\u0011\u00059)\u0016B\u0001,\u0010\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!A\u0006A!e\u0001\n\u0003I\u0016AA02+\u00051\u0002\u0002C.\u0001\u0005\u0003\u0007I\u0011\u0001/\u0002\r}\u000bt\fJ3r)\ti\u0006\r\u0005\u0002\u000f=&\u0011ql\u0004\u0002\u0005+:LG\u000fC\u0004b5\u0006\u0005\t\u0019\u0001\f\u0002\u0007a$\u0013\u0007\u0003\u0005d\u0001\tE\t\u0015)\u0003\u0017\u0003\ry\u0016\u0007\t\u0005\tK\u0002\u0011\t\u001a!C\u0001M\u0006\u0011qLM\u000b\u0002\u0007\"A\u0001\u000e\u0001BA\u0002\u0013\u0005\u0011.\u0001\u0004`e}#S-\u001d\u000b\u0003;*Dq!Y4\u0002\u0002\u0003\u00071\t\u0003\u0005m\u0001\tE\t\u0015)\u0003D\u0003\ry&\u0007\t\u0005\u0006]\u0002!\ta\\\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007A\u00148\u000f\u0005\u0003r\u0001Y\u0019U\"\u0001\u0002\t\u000bak\u0007\u0019\u0001\f\t\u000b\u0015l\u0007\u0019A\"\t\u000b9\u0004A\u0011A;\u0015\u0003ADQa\u001e\u0001\u0005\u0002a\fa!\u001e9eCR,Gc\u00019zw\")!P\u001ea\u0001-\u0005\u0011a.\r\u0005\u0006yZ\u0004\raQ\u0001\u0003]JBQA \u0001\u0005B}\f\u0001\u0002^8TiJLgn\u001a\u000b\u0003\u0003\u0003\u0001B!a\u0001\u0002\n9\u0019a\"!\u0002\n\u0007\u0005\u001dq\"\u0001\u0004Qe\u0016$WMZ\u0005\u0005\u0003\u0017\tiA\u0001\u0004TiJLgn\u001a\u0006\u0004\u0003\u000fy\u0001bBA\t\u0001\u0011\u0005\u00131C\u0001\tG\u0006tW)];bYR!\u0011QCA\u000e!\rq\u0011qC\u0005\u0004\u00033y!a\u0002\"p_2,\u0017M\u001c\u0005\b\u0003;\ty\u00011\u0001\u001f\u0003\u0011!\b.\u0019;\t\u0013\u0005\u0005\u0002!!A\u0005\u0002\u0005\r\u0012\u0001B2paf,b!!\n\u0002,\u0005\u0015CCBA\u0014\u0003;\ny\u0006\u0005\u0004r\u0001\u0005%\u00121\t\t\u0004/\u0005-BAC\r\u0002 \u0001\u0006\t\u0011!b\u00015!j\u00111\u0006\u0012\u00020\u0005M\u0012qGA\u001e\u0003\u007f\tda\t\u0014(\u0003cA\u0013\u0007\u0002\u0013+]A\tda\t\u00192\u0003k\u0011\u0014\u0007\u0002\u0013+]A\tdaI\u001b7\u0003s9\u0014\u0007\u0002\u0013+]A\tda\t\u001e<\u0003{a\u0014\u0007\u0002\u0013+]A\tdaI A\u0003\u0003\n\u0015\u0007\u0002\u0013+]A\u00012aFA#\t))\u0015q\u0004Q\u0001\u0002\u0003\u0015\rA\u0007\u0015\u000e\u0003\u000b\u0012\u0013\u0011JA'\u0003#\n)&!\u00172\r\r2s%a\u0013)c\u0011!#F\f\t2\r\r\u0002\u0014'a\u00143c\u0011!#F\f\t2\r\r*d'a\u00158c\u0011!#F\f\t2\r\rR4(a\u0016=c\u0011!#F\f\t2\r\rz\u0004)a\u0017Bc\u0011!#F\f\t\t\u0013a\u000by\u0002%AA\u0002\u0005%\u0002\"C3\u0002 A\u0005\t\u0019AA\"\u0011%\t\u0019\u0007AI\u0001\n\u0003\t)'\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\r\u0005\u001d\u0014QPAK+\t\tIGK\u0002\u0017\u0003WZ#!!\u001c\u0011\t\u0005=\u0014\u0011P\u0007\u0003\u0003cRA!a\u001d\u0002v\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003oz\u0011AC1o]>$\u0018\r^5p]&!\u00111PA9\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0003\u000b3\u0005\u0005\u0004\u0015!A\u0001\u0006\u0004Q\u0002&DA?E\u0005\u0005\u0015QQAE\u0003\u001b\u000b\t*\r\u0004$M\u001d\n\u0019\tK\u0019\u0005I)r\u0003#\r\u0004$aE\n9IM\u0019\u0005I)r\u0003#\r\u0004$kY\nYiN\u0019\u0005I)r\u0003#\r\u0004$um\ny\tP\u0019\u0005I)r\u0003#\r\u0004$\u007f\u0001\u000b\u0019*Q\u0019\u0005I)r\u0003\u0003\u0002\u0006F\u0003C\u0002\u000b\u0011!AC\u0002iAS\"!&#\u00033\u000bi*!)\u0002&\u0006%\u0016GB\u0012'O\u0005m\u0005&\r\u0003%U9\u0002\u0012GB\u00121c\u0005}%'\r\u0003%U9\u0002\u0012GB\u00126m\u0005\rv'\r\u0003%U9\u0002\u0012GB\u0012;w\u0005\u001dF(\r\u0003%U9\u0002\u0012GB\u0012@\u0001\u0006-\u0016)\r\u0003%U9\u0002\u0002\"CAX\u0001E\u0005I\u0011AAY\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*b!a-\u00028\u0006=WCAA[U\r\u0019\u00151\u000e\u0003\u000b3\u00055\u0006\u0015!A\u0001\u0006\u0004Q\u0002&DA\\E\u0005m\u0016qXAb\u0003\u000f\fY-\r\u0004$M\u001d\ni\fK\u0019\u0005I)r\u0003#\r\u0004$aE\n\tMM\u0019\u0005I)r\u0003#\r\u0004$kY\n)mN\u0019\u0005I)r\u0003#\r\u0004$um\nI\rP\u0019\u0005I)r\u0003#\r\u0004$\u007f\u0001\u000bi-Q\u0019\u0005I)r\u0003\u0003\u0002\u0006F\u0003[\u0003\u000b\u0011!AC\u0002iAS\"a4#\u0003'\f9.a7\u0002`\u0006\r\u0018GB\u0012'O\u0005U\u0007&\r\u0003%U9\u0002\u0012GB\u00121c\u0005e''\r\u0003%U9\u0002\u0012GB\u00126m\u0005uw'\r\u0003%U9\u0002\u0012GB\u0012;w\u0005\u0005H(\r\u0003%U9\u0002\u0012GB\u0012@\u0001\u0006\u0015\u0018)\r\u0003%U9\u0002\u0002\"CAu\u0001\u0005\u0005I\u0011IAv\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\u0011\u0011Q\u001e\t\u0005\u0003_\fI0\u0004\u0002\u0002r*!\u00111_A{\u0003\u0011a\u0017M\\4\u000b\u0005\u0005]\u0018\u0001\u00026bm\u0006LA!a\u0003\u0002r\"I\u0011Q \u0001\u0002\u0002\u0013\u0005\u0013q`\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011!\u0011\u0001\t\u0006\u0005\u0007\u0011IAH\u0007\u0003\u0005\u000bQ1Aa\u0002\u0010\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0005\u0017\u0011)A\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011%\u0011y\u0001AA\u0001\n\u0003\u0012\t\"\u0001\u0005iCND7i\u001c3f)\t\u0011\u0019\u0002E\u0002\u000f\u0005+I1Aa\u0006\u0010\u0005\rIe\u000e\u001e\u0005\n\u00057\u0001\u0011\u0011!C!\u0005;\ta!Z9vC2\u001cH\u0003BA\u000b\u0005?A\u0001\"\u0019B\r\u0003\u0003\u0005\rA\b\u0015\u0004\u0001\t\r\u0002\u0003\u0002B\u0013\u0005Si!Aa\n\u000b\u0007\u0005]D!\u0003\u0003\u0003,\t\u001d\"\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLw!\u0003B\u0018\u0005\u0005\u0005\t\u0012\u0001B\u0019\u0003-iU\u000f^1cY\u0016\u0004\u0016-\u001b:\u0011\u0007E\u0014\u0019D\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012\u0001B\u001b'\u0011\u0011\u0019$\u0004+\t\u000f9\u0014\u0019\u0004\"\u0001\u0003:Q\u0011!\u0011\u0007\u0005\n}\nM\u0012\u0011!C#\u0005{!\"!!<\t\u0015\t\u0005#1GA\u0001\n\u0003\u0013\u0019%A\u0003baBd\u00170\u0006\u0004\u0003F\t-#Q\r\u000b\u0007\u0005\u000f\u0012iHa \u0011\rE\u0004!\u0011\nB2!\r9\"1\n\u0003\u000b3\t}\u0002\u0015!A\u0001\u0006\u0004Q\u0002&\u0004B&E\t=#1\u000bB,\u00057\u0012y&\r\u0004$M\u001d\u0012\t\u0006K\u0019\u0005I)r\u0003#\r\u0004$aE\u0012)FM\u0019\u0005I)r\u0003#\r\u0004$kY\u0012IfN\u0019\u0005I)r\u0003#\r\u0004$um\u0012i\u0006P\u0019\u0005I)r\u0003#\r\u0004$\u007f\u0001\u0013\t'Q\u0019\u0005I)r\u0003\u0003E\u0002\u0018\u0005K\"!\"\u0012B A\u0003\u0005\tQ1\u0001\u001bQ5\u0011)G\tB5\u0005[\u0012\tH!\u001e\u0003zE21EJ\u0014\u0003l!\nD\u0001\n\u0016/!E21\u0005M\u0019\u0003pI\nD\u0001\n\u0016/!E21%\u000e\u001c\u0003t]\nD\u0001\n\u0016/!E21EO\u001e\u0003xq\nD\u0001\n\u0016/!E21e\u0010!\u0003|\u0005\u000bD\u0001\n\u0016/!!9\u0001La\u0010A\u0002\t%\u0003bB3\u0003@\u0001\u0007!1\r\u0005\u000b\u0005\u0007\u0013\u0019$!A\u0005\u0002\n\u0015\u0015aB;oCB\u0004H._\u000b\u0007\u0005\u000f\u00139J!-\u0015\t\t%%\u0011\u001a\t\u0006\u001d\t-%qR\u0005\u0004\u0005\u001b{!AB(qi&|g\u000eE\u0004\u000f\u0005#\u0013)Ja,\n\u0007\tMuB\u0001\u0004UkBdWM\r\t\u0004/\t]EAC\r\u0003\u0002\u0002\u0006\t\u0011!b\u00015!j!q\u0013\u0012\u0003\u001c\n}%1\u0015BT\u0005W\u000bda\t\u0014(\u0005;C\u0013\u0007\u0002\u0013+]A\tda\t\u00192\u0005C\u0013\u0014\u0007\u0002\u0013+]A\tdaI\u001b7\u0005K;\u0014\u0007\u0002\u0013+]A\tda\t\u001e<\u0005Sc\u0014\u0007\u0002\u0013+]A\tdaI A\u0005[\u000b\u0015\u0007\u0002\u0013+]A\u00012a\u0006BY\t))%\u0011\u0011Q\u0001\u0002\u0003\u0015\rA\u0007\u0015\u000e\u0005c\u0013#Q\u0017B]\u0005{\u0013\tM!22\r\r2sEa.)c\u0011!#F\f\t2\r\r\u0002\u0014Ga/3c\u0011!#F\f\t2\r\r*dGa08c\u0011!#F\f\t2\r\rR4Ha1=c\u0011!#F\f\t2\r\rz\u0004Ia2Bc\u0011!#F\f\t\t\u0015\t-'\u0011QA\u0001\u0002\u0004\u0011i-A\u0002yIA\u0002b!\u001d\u0001\u0003\u0016\n=\u0006B\u0003Bi\u0005g\t\t\u0011\"\u0003\u0003T\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\u0011)\u000e\u0005\u0003\u0002p\n]\u0017\u0002\u0002Bm\u0003c\u0014aa\u00142kK\u000e$\b"
)
public class MutablePair<T1, T2> implements Product2<T1, T2>, Serializable {
   public T1 _1;
   public T2 _2;

   public static <T1, T2> Option<Tuple2<T1, T2>> unapply(MutablePair<T1, T2> var0) {
      return MutablePair$.MODULE$.unapply(var0);
   }

   public static <T1, T2> MutablePair<T1, T2> apply(T1 var0, T2 var1) {
      return MutablePair$.MODULE$.<T1, T2>apply(var0, var1);
   }

   public int productArity() {
      return Product2$class.productArity(this);
   }

   public Object productElement(int n) throws IndexOutOfBoundsException {
      return Product2$class.productElement(this, n);
   }

   public T1 _1() {
      return this._1;
   }

   public void _1_$eq(T1 x$1) {
      this._1 = (T1)x$1;
   }

   public T2 _2() {
      return this._2;
   }

   public void _2_$eq(T2 x$1) {
      this._2 = (T2)x$1;
   }

   public MutablePair<T1, T2> update(T1 n1, T2 n2) {
      this._1_$eq(n1);
      this._2_$eq(n2);
      return this;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"(").append(this._1()).append((Object)",").append(this._2()).append((Object)")").toString();
   }

   public boolean canEqual(Object that) {
      return that instanceof MutablePair;
   }

   public <T1, T2> MutablePair<T1, T2> copy(T1 _1, T2 _2) {
      return new MutablePair<T1, T2>(_1, _2);
   }

   public <T1, T2> T1 copy$default$1() {
      return (T1)this._1();
   }

   public <T1, T2> T2 copy$default$2() {
      return (T2)this._2();
   }

   public String productPrefix() {
      return "MutablePair";
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label50: {
            boolean var3;
            if (x$1 instanceof MutablePair) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               MutablePair var4 = (MutablePair)x$1;
               if (BoxesRunTime.equals(this._1(), var4._1()) && BoxesRunTime.equals(this._2(), var4._2()) && var4.canEqual(this)) {
                  break label50;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public boolean _1$mcZ$sp() {
      return BoxesRunTime.unboxToBoolean(this._1());
   }

   public char _1$mcC$sp() {
      return BoxesRunTime.unboxToChar(this._1());
   }

   public double _1$mcD$sp() {
      return BoxesRunTime.unboxToDouble(this._1());
   }

   public int _1$mcI$sp() {
      return BoxesRunTime.unboxToInt(this._1());
   }

   public long _1$mcJ$sp() {
      return BoxesRunTime.unboxToLong(this._1());
   }

   public void _1$mcZ$sp_$eq(boolean x$1) {
      this._1_$eq(BoxesRunTime.boxToBoolean(x$1));
   }

   public void _1$mcC$sp_$eq(char x$1) {
      this._1_$eq(BoxesRunTime.boxToCharacter(x$1));
   }

   public void _1$mcD$sp_$eq(double x$1) {
      this._1_$eq(BoxesRunTime.boxToDouble(x$1));
   }

   public void _1$mcI$sp_$eq(int x$1) {
      this._1_$eq(BoxesRunTime.boxToInteger(x$1));
   }

   public void _1$mcJ$sp_$eq(long x$1) {
      this._1_$eq(BoxesRunTime.boxToLong(x$1));
   }

   public boolean _2$mcZ$sp() {
      return BoxesRunTime.unboxToBoolean(this._2());
   }

   public char _2$mcC$sp() {
      return BoxesRunTime.unboxToChar(this._2());
   }

   public double _2$mcD$sp() {
      return BoxesRunTime.unboxToDouble(this._2());
   }

   public int _2$mcI$sp() {
      return BoxesRunTime.unboxToInt(this._2());
   }

   public long _2$mcJ$sp() {
      return BoxesRunTime.unboxToLong(this._2());
   }

   public void _2$mcZ$sp_$eq(boolean x$1) {
      this._2_$eq(BoxesRunTime.boxToBoolean(x$1));
   }

   public void _2$mcC$sp_$eq(char x$1) {
      this._2_$eq(BoxesRunTime.boxToCharacter(x$1));
   }

   public void _2$mcD$sp_$eq(double x$1) {
      this._2_$eq(BoxesRunTime.boxToDouble(x$1));
   }

   public void _2$mcI$sp_$eq(int x$1) {
      this._2_$eq(BoxesRunTime.boxToInteger(x$1));
   }

   public void _2$mcJ$sp_$eq(long x$1) {
      this._2_$eq(BoxesRunTime.boxToLong(x$1));
   }

   public MutablePair<Object, Object> update$mcZZ$sp(boolean n1, boolean n2) {
      return this.update(BoxesRunTime.boxToBoolean(n1), BoxesRunTime.boxToBoolean(n2));
   }

   public MutablePair<Object, Object> update$mcZC$sp(boolean n1, char n2) {
      return this.update(BoxesRunTime.boxToBoolean(n1), BoxesRunTime.boxToCharacter(n2));
   }

   public MutablePair<Object, Object> update$mcZD$sp(boolean n1, double n2) {
      return this.update(BoxesRunTime.boxToBoolean(n1), BoxesRunTime.boxToDouble(n2));
   }

   public MutablePair<Object, Object> update$mcZI$sp(boolean n1, int n2) {
      return this.update(BoxesRunTime.boxToBoolean(n1), BoxesRunTime.boxToInteger(n2));
   }

   public MutablePair<Object, Object> update$mcZJ$sp(boolean n1, long n2) {
      return this.update(BoxesRunTime.boxToBoolean(n1), BoxesRunTime.boxToLong(n2));
   }

   public MutablePair<Object, Object> update$mcCZ$sp(char n1, boolean n2) {
      return this.update(BoxesRunTime.boxToCharacter(n1), BoxesRunTime.boxToBoolean(n2));
   }

   public MutablePair<Object, Object> update$mcCC$sp(char n1, char n2) {
      return this.update(BoxesRunTime.boxToCharacter(n1), BoxesRunTime.boxToCharacter(n2));
   }

   public MutablePair<Object, Object> update$mcCD$sp(char n1, double n2) {
      return this.update(BoxesRunTime.boxToCharacter(n1), BoxesRunTime.boxToDouble(n2));
   }

   public MutablePair<Object, Object> update$mcCI$sp(char n1, int n2) {
      return this.update(BoxesRunTime.boxToCharacter(n1), BoxesRunTime.boxToInteger(n2));
   }

   public MutablePair<Object, Object> update$mcCJ$sp(char n1, long n2) {
      return this.update(BoxesRunTime.boxToCharacter(n1), BoxesRunTime.boxToLong(n2));
   }

   public MutablePair<Object, Object> update$mcDZ$sp(double n1, boolean n2) {
      return this.update(BoxesRunTime.boxToDouble(n1), BoxesRunTime.boxToBoolean(n2));
   }

   public MutablePair<Object, Object> update$mcDC$sp(double n1, char n2) {
      return this.update(BoxesRunTime.boxToDouble(n1), BoxesRunTime.boxToCharacter(n2));
   }

   public MutablePair<Object, Object> update$mcDD$sp(double n1, double n2) {
      return this.update(BoxesRunTime.boxToDouble(n1), BoxesRunTime.boxToDouble(n2));
   }

   public MutablePair<Object, Object> update$mcDI$sp(double n1, int n2) {
      return this.update(BoxesRunTime.boxToDouble(n1), BoxesRunTime.boxToInteger(n2));
   }

   public MutablePair<Object, Object> update$mcDJ$sp(double n1, long n2) {
      return this.update(BoxesRunTime.boxToDouble(n1), BoxesRunTime.boxToLong(n2));
   }

   public MutablePair<Object, Object> update$mcIZ$sp(int n1, boolean n2) {
      return this.update(BoxesRunTime.boxToInteger(n1), BoxesRunTime.boxToBoolean(n2));
   }

   public MutablePair<Object, Object> update$mcIC$sp(int n1, char n2) {
      return this.update(BoxesRunTime.boxToInteger(n1), BoxesRunTime.boxToCharacter(n2));
   }

   public MutablePair<Object, Object> update$mcID$sp(int n1, double n2) {
      return this.update(BoxesRunTime.boxToInteger(n1), BoxesRunTime.boxToDouble(n2));
   }

   public MutablePair<Object, Object> update$mcII$sp(int n1, int n2) {
      return this.update(BoxesRunTime.boxToInteger(n1), BoxesRunTime.boxToInteger(n2));
   }

   public MutablePair<Object, Object> update$mcIJ$sp(int n1, long n2) {
      return this.update(BoxesRunTime.boxToInteger(n1), BoxesRunTime.boxToLong(n2));
   }

   public MutablePair<Object, Object> update$mcJZ$sp(long n1, boolean n2) {
      return this.update(BoxesRunTime.boxToLong(n1), BoxesRunTime.boxToBoolean(n2));
   }

   public MutablePair<Object, Object> update$mcJC$sp(long n1, char n2) {
      return this.update(BoxesRunTime.boxToLong(n1), BoxesRunTime.boxToCharacter(n2));
   }

   public MutablePair<Object, Object> update$mcJD$sp(long n1, double n2) {
      return this.update(BoxesRunTime.boxToLong(n1), BoxesRunTime.boxToDouble(n2));
   }

   public MutablePair<Object, Object> update$mcJI$sp(long n1, int n2) {
      return this.update(BoxesRunTime.boxToLong(n1), BoxesRunTime.boxToInteger(n2));
   }

   public MutablePair<Object, Object> update$mcJJ$sp(long n1, long n2) {
      return this.update(BoxesRunTime.boxToLong(n1), BoxesRunTime.boxToLong(n2));
   }

   public MutablePair<Object, Object> copy$mZZc$sp(boolean _1, boolean _2) {
      return new MutablePair$mcZZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mZCc$sp(boolean _1, char _2) {
      return new MutablePair$mcZC$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mZDc$sp(boolean _1, double _2) {
      return new MutablePair$mcZD$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mZIc$sp(boolean _1, int _2) {
      return new MutablePair$mcZI$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mZJc$sp(boolean _1, long _2) {
      return new MutablePair$mcZJ$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mCZc$sp(char _1, boolean _2) {
      return new MutablePair$mcCZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mCCc$sp(char _1, char _2) {
      return new MutablePair$mcCC$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mCDc$sp(char _1, double _2) {
      return new MutablePair$mcCD$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mCIc$sp(char _1, int _2) {
      return new MutablePair$mcCI$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mCJc$sp(char _1, long _2) {
      return new MutablePair$mcCJ$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mDZc$sp(double _1, boolean _2) {
      return new MutablePair$mcDZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mDCc$sp(double _1, char _2) {
      return new MutablePair$mcDC$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mDDc$sp(double _1, double _2) {
      return new MutablePair$mcDD$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mDIc$sp(double _1, int _2) {
      return new MutablePair$mcDI$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mDJc$sp(double _1, long _2) {
      return new MutablePair$mcDJ$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mIZc$sp(int _1, boolean _2) {
      return new MutablePair$mcIZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mICc$sp(int _1, char _2) {
      return new MutablePair$mcIC$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mIDc$sp(int _1, double _2) {
      return new MutablePair$mcID$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mIIc$sp(int _1, int _2) {
      return new MutablePair$mcII$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mIJc$sp(int _1, long _2) {
      return new MutablePair$mcIJ$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mJZc$sp(long _1, boolean _2) {
      return new MutablePair$mcJZ$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mJCc$sp(long _1, char _2) {
      return new MutablePair$mcJC$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mJDc$sp(long _1, double _2) {
      return new MutablePair$mcJD$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mJIc$sp(long _1, int _2) {
      return new MutablePair$mcJI$sp(_1, _2);
   }

   public MutablePair<Object, Object> copy$mJJc$sp(long _1, long _2) {
      return new MutablePair$mcJJ$sp(_1, _2);
   }

   public <T1, T2> boolean copy$default$1$mcZ$sp() {
      return BoxesRunTime.unboxToBoolean(this.copy$default$1());
   }

   public <T1, T2> char copy$default$1$mcC$sp() {
      return BoxesRunTime.unboxToChar(this.copy$default$1());
   }

   public <T1, T2> double copy$default$1$mcD$sp() {
      return BoxesRunTime.unboxToDouble(this.copy$default$1());
   }

   public <T1, T2> int copy$default$1$mcI$sp() {
      return BoxesRunTime.unboxToInt(this.copy$default$1());
   }

   public <T1, T2> long copy$default$1$mcJ$sp() {
      return BoxesRunTime.unboxToLong(this.copy$default$1());
   }

   public <T1, T2> boolean copy$default$2$mcZ$sp() {
      return BoxesRunTime.unboxToBoolean(this.copy$default$2());
   }

   public <T1, T2> char copy$default$2$mcC$sp() {
      return BoxesRunTime.unboxToChar(this.copy$default$2());
   }

   public <T1, T2> double copy$default$2$mcD$sp() {
      return BoxesRunTime.unboxToDouble(this.copy$default$2());
   }

   public <T1, T2> int copy$default$2$mcI$sp() {
      return BoxesRunTime.unboxToInt(this.copy$default$2());
   }

   public <T1, T2> long copy$default$2$mcJ$sp() {
      return BoxesRunTime.unboxToLong(this.copy$default$2());
   }

   public boolean specInstance$() {
      return false;
   }

   public MutablePair(T1 _1, T2 _2) {
      this._1 = (T1)_1;
      this._2 = (T2)_2;
      super();
      Product$class.$init$(this);
      Product2$class.$init$(this);
   }

   public MutablePair() {
      this((Object)null, (Object)null);
   }
}
