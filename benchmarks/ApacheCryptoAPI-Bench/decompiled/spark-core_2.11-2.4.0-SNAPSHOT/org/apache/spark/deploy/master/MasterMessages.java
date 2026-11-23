package org.apache.spark.deploy.master;

import scala.None$;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction2;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\t-gaB\u0001\u0003!\u0003\r\n#\u0004\u0002\u000f\u001b\u0006\u001cH/\u001a:NKN\u001c\u0018mZ3t\u0015\t\u0019A!\u0001\u0004nCN$XM\u001d\u0006\u0003\u000b\u0019\ta\u0001Z3qY>L(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001M\u0019\u0001A\u0004\u000b\u0011\u0005=\u0011R\"\u0001\t\u000b\u0003E\tQa]2bY\u0006L!a\u0005\t\u0003\r\u0005s\u0017PU3g!\tyQ#\u0003\u0002\u0017!\ta1+\u001a:jC2L'0\u00192mK\u001e1\u0001D\u0001E\u0001\u0005e\ta\"T1ti\u0016\u0014X*Z:tC\u001e,7\u000f\u0005\u0002\u001b75\t!A\u0002\u0004\u0002\u0005!\u0005!\u0001H\n\u000479!\u0002\"\u0002\u0010\u001c\t\u0003y\u0012A\u0002\u001fj]&$h\bF\u0001\u001a\u000f\u0015\t3\u0004#!#\u00035)E.Z2uK\u0012dU-\u00193feB\u00111\u0005J\u0007\u00027\u0019)Qe\u0007EAM\tiQ\t\\3di\u0016$G*Z1eKJ\u001cB\u0001\n\b()A\u0011q\u0002K\u0005\u0003SA\u0011q\u0001\u0015:pIV\u001cG\u000fC\u0003\u001fI\u0011\u00051\u0006F\u0001#\u0011\u001diC%!A\u0005B9\nQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A\u0018\u0011\u0005A*T\"A\u0019\u000b\u0005I\u001a\u0014\u0001\u00027b]\u001eT\u0011\u0001N\u0001\u0005U\u00064\u0018-\u0003\u00027c\t11\u000b\u001e:j]\u001eDq\u0001\u000f\u0013\u0002\u0002\u0013\u0005\u0011(\u0001\u0007qe>$Wo\u0019;Be&$\u00180F\u0001;!\ty1(\u0003\u0002=!\t\u0019\u0011J\u001c;\t\u000fy\"\u0013\u0011!C\u0001\u007f\u0005q\u0001O]8ek\u000e$X\t\\3nK:$HC\u0001!D!\ty\u0011)\u0003\u0002C!\t\u0019\u0011I\\=\t\u000f\u0011k\u0014\u0011!a\u0001u\u0005\u0019\u0001\u0010J\u0019\t\u000f\u0019#\u0013\u0011!C!\u000f\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001I!\rIE\nQ\u0007\u0002\u0015*\u00111\nE\u0001\u000bG>dG.Z2uS>t\u0017BA'K\u0005!IE/\u001a:bi>\u0014\bbB(%\u0003\u0003%\t\u0001U\u0001\tG\u0006tW)];bYR\u0011\u0011\u000b\u0016\t\u0003\u001fIK!a\u0015\t\u0003\u000f\t{w\u000e\\3b]\"9AITA\u0001\u0002\u0004\u0001\u0005b\u0002,%\u0003\u0003%\teV\u0001\tQ\u0006\u001c\bnQ8eKR\t!\bC\u0004ZI\u0005\u0005I\u0011\t.\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012a\f\u0005\b9\u0012\n\t\u0011\"\u0003^\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0003y\u0003\"\u0001M0\n\u0005\u0001\f$AB(cU\u0016\u001cGoB\u0003c7!\u00055-A\tSKZ|7.\u001a3MK\u0006$WM]:iSB\u0004\"a\t3\u0007\u000b\u0015\\\u0002\u0012\u00114\u0003#I+go\\6fI2+\u0017\rZ3sg\"L\u0007o\u0005\u0003e\u001d\u001d\"\u0002\"\u0002\u0010e\t\u0003AG#A2\t\u000f5\"\u0017\u0011!C!]!9\u0001\bZA\u0001\n\u0003I\u0004b\u0002 e\u0003\u0003%\t\u0001\u001c\u000b\u0003\u00016Dq\u0001R6\u0002\u0002\u0003\u0007!\bC\u0004GI\u0006\u0005I\u0011I$\t\u000f=#\u0017\u0011!C\u0001aR\u0011\u0011+\u001d\u0005\b\t>\f\t\u00111\u0001A\u0011\u001d1F-!A\u0005B]Cq!\u00173\u0002\u0002\u0013\u0005#\fC\u0004]I\u0006\u0005I\u0011B/\b\u000bY\\\u0002\u0012Q<\u0002+\rCWmY6G_J<vN]6feRKW.Z(viB\u00111\u0005\u001f\u0004\u0006snA\tI\u001f\u0002\u0016\u0007\",7m\u001b$pe^{'o[3s)&lWmT;u'\u0011Ahb\n\u000b\t\u000byAH\u0011\u0001?\u0015\u0003]Dq!\f=\u0002\u0002\u0013\u0005c\u0006C\u00049q\u0006\u0005I\u0011A\u001d\t\u0011yB\u0018\u0011!C\u0001\u0003\u0003!2\u0001QA\u0002\u0011\u001d!u0!AA\u0002iBqA\u0012=\u0002\u0002\u0013\u0005s\t\u0003\u0005Pq\u0006\u0005I\u0011AA\u0005)\r\t\u00161\u0002\u0005\t\t\u0006\u001d\u0011\u0011!a\u0001\u0001\"9a\u000b_A\u0001\n\u0003:\u0006bB-y\u0003\u0003%\tE\u0017\u0005\b9b\f\t\u0011\"\u0003^\r\u0019\t)b\u0007!\u0002\u0018\ti!)Z4j]J+7m\u001c<fef\u001cR!a\u0005\u000fOQA1\"a\u0007\u0002\u0014\tU\r\u0011\"\u0001\u0002\u001e\u0005Q1\u000f^8sK\u0012\f\u0005\u000f]:\u0016\u0005\u0005}\u0001CBA\u0011\u0003c\t9D\u0004\u0003\u0002$\u00055b\u0002BA\u0013\u0003Wi!!a\n\u000b\u0007\u0005%B\"\u0001\u0004=e>|GOP\u0005\u0002#%\u0019\u0011q\u0006\t\u0002\u000fA\f7m[1hK&!\u00111GA\u001b\u0005\r\u0019V-\u001d\u0006\u0004\u0003_\u0001\u0002c\u0001\u000e\u0002:%\u0019\u00111\b\u0002\u0003\u001f\u0005\u0003\b\u000f\\5dCRLwN\\%oM>D1\"a\u0010\u0002\u0014\tE\t\u0015!\u0003\u0002 \u0005Y1\u000f^8sK\u0012\f\u0005\u000f]:!\u0011-\t\u0019%a\u0005\u0003\u0016\u0004%\t!!\u0012\u0002\u001bM$xN]3e/>\u00148.\u001a:t+\t\t9\u0005\u0005\u0004\u0002\"\u0005E\u0012\u0011\n\t\u00045\u0005-\u0013bAA'\u0005\tQqk\u001c:lKJLeNZ8\t\u0017\u0005E\u00131\u0003B\tB\u0003%\u0011qI\u0001\u000fgR|'/\u001a3X_J\\WM]:!\u0011\u001dq\u00121\u0003C\u0001\u0003+\"b!a\u0016\u0002Z\u0005m\u0003cA\u0012\u0002\u0014!A\u00111DA*\u0001\u0004\ty\u0002\u0003\u0005\u0002D\u0005M\u0003\u0019AA$\u0011)\ty&a\u0005\u0002\u0002\u0013\u0005\u0011\u0011M\u0001\u0005G>\u0004\u0018\u0010\u0006\u0004\u0002X\u0005\r\u0014Q\r\u0005\u000b\u00037\ti\u0006%AA\u0002\u0005}\u0001BCA\"\u0003;\u0002\n\u00111\u0001\u0002H!Q\u0011\u0011NA\n#\u0003%\t!a\u001b\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\u0011\u0011Q\u000e\u0016\u0005\u0003?\tyg\u000b\u0002\u0002rA!\u00111OA?\u001b\t\t)H\u0003\u0003\u0002x\u0005e\u0014!C;oG\",7m[3e\u0015\r\tY\bE\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002BA@\u0003k\u0012\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011)\t\u0019)a\u0005\u0012\u0002\u0013\u0005\u0011QQ\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\t\t9I\u000b\u0003\u0002H\u0005=\u0004\u0002C\u0017\u0002\u0014\u0005\u0005I\u0011\t\u0018\t\u0011a\n\u0019\"!A\u0005\u0002eB\u0011BPA\n\u0003\u0003%\t!a$\u0015\u0007\u0001\u000b\t\n\u0003\u0005E\u0003\u001b\u000b\t\u00111\u0001;\u0011!1\u00151CA\u0001\n\u0003:\u0005\"C(\u0002\u0014\u0005\u0005I\u0011AAL)\r\t\u0016\u0011\u0014\u0005\t\t\u0006U\u0015\u0011!a\u0001\u0001\"Aa+a\u0005\u0002\u0002\u0013\u0005s\u000b\u0003\u0005Z\u0003'\t\t\u0011\"\u0011[\u0011)\t\t+a\u0005\u0002\u0002\u0013\u0005\u00131U\u0001\u0007KF,\u0018\r\\:\u0015\u0007E\u000b)\u000b\u0003\u0005E\u0003?\u000b\t\u00111\u0001A\u000f%\tIkGA\u0001\u0012\u0003\tY+A\u0007CK\u001eLgNU3d_Z,'/\u001f\t\u0004G\u00055f!CA\u000b7\u0005\u0005\t\u0012AAX'\u0015\ti+!-\u0015!)\t\u0019,!/\u0002 \u0005\u001d\u0013qK\u0007\u0003\u0003kS1!a.\u0011\u0003\u001d\u0011XO\u001c;j[\u0016LA!a/\u00026\n\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\u001c\u001a\t\u000fy\ti\u000b\"\u0001\u0002@R\u0011\u00111\u0016\u0005\t3\u00065\u0016\u0011!C#5\"Q\u0011QYAW\u0003\u0003%\t)a2\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\r\u0005]\u0013\u0011ZAf\u0011!\tY\"a1A\u0002\u0005}\u0001\u0002CA\"\u0003\u0007\u0004\r!a\u0012\t\u0015\u0005=\u0017QVA\u0001\n\u0003\u000b\t.A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005M\u0017q\u001c\t\u0006\u001f\u0005U\u0017\u0011\\\u0005\u0004\u0003/\u0004\"AB(qi&|g\u000eE\u0004\u0010\u00037\fy\"a\u0012\n\u0007\u0005u\u0007C\u0001\u0004UkBdWM\r\u0005\u000b\u0003C\fi-!AA\u0002\u0005]\u0013a\u0001=%a!AA,!,\u0002\u0002\u0013%QlB\u0004\u0002hnA\t)!;\u0002!\r{W\u000e\u001d7fi\u0016\u0014VmY8wKJL\bcA\u0012\u0002l\u001a9\u0011Q^\u000e\t\u0002\u0006=(\u0001E\"p[BdW\r^3SK\u000e|g/\u001a:z'\u0015\tYOD\u0014\u0015\u0011\u001dq\u00121\u001eC\u0001\u0003g$\"!!;\t\u00115\nY/!A\u0005B9B\u0001\u0002OAv\u0003\u0003%\t!\u000f\u0005\n}\u0005-\u0018\u0011!C\u0001\u0003w$2\u0001QA\u007f\u0011!!\u0015\u0011`A\u0001\u0002\u0004Q\u0004\u0002\u0003$\u0002l\u0006\u0005I\u0011I$\t\u0013=\u000bY/!A\u0005\u0002\t\rAcA)\u0003\u0006!AAI!\u0001\u0002\u0002\u0003\u0007\u0001\t\u0003\u0005W\u0003W\f\t\u0011\"\u0011X\u0011!I\u00161^A\u0001\n\u0003R\u0006\u0002\u0003/\u0002l\u0006\u0005I\u0011B/\b\u000f\t=1\u0004#!\u0003\u0012\u0005\t\"i\\;oIB{'\u000f^:SKF,Xm\u001d;\u0011\u0007\r\u0012\u0019BB\u0004\u0003\u0016mA\tIa\u0006\u0003#\t{WO\u001c3Q_J$8OU3rk\u0016\u001cHoE\u0003\u0003\u001499C\u0003C\u0004\u001f\u0005'!\tAa\u0007\u0015\u0005\tE\u0001\u0002C\u0017\u0003\u0014\u0005\u0005I\u0011\t\u0018\t\u0011a\u0012\u0019\"!A\u0005\u0002eB\u0011B\u0010B\n\u0003\u0003%\tAa\t\u0015\u0007\u0001\u0013)\u0003\u0003\u0005E\u0005C\t\t\u00111\u0001;\u0011!1%1CA\u0001\n\u0003:\u0005\"C(\u0003\u0014\u0005\u0005I\u0011\u0001B\u0016)\r\t&Q\u0006\u0005\t\t\n%\u0012\u0011!a\u0001\u0001\"AaKa\u0005\u0002\u0002\u0013\u0005s\u000b\u0003\u0005Z\u0005'\t\t\u0011\"\u0011[\u0011!a&1CA\u0001\n\u0013ifA\u0002B\u001c7\u0001\u0013ID\u0001\nC_VtG\rU8siN\u0014Vm\u001d9p]N,7#\u0002B\u001b\u001d\u001d\"\u0002B\u0003B\u001f\u0005k\u0011)\u001a!C\u0001s\u0005y!\u000f]2F]\u0012\u0004x.\u001b8u!>\u0014H\u000f\u0003\u0006\u0003B\tU\"\u0011#Q\u0001\ni\n\u0001C\u001d9d\u000b:$\u0007o\\5oiB{'\u000f\u001e\u0011\t\u0015\t\u0015#Q\u0007BK\u0002\u0013\u0005\u0011(A\u0005xK\n,\u0016\nU8si\"Q!\u0011\nB\u001b\u0005#\u0005\u000b\u0011\u0002\u001e\u0002\u0015],'-V%Q_J$\b\u0005C\u0006\u0003N\tU\"Q3A\u0005\u0002\t=\u0013\u0001\u0003:fgR\u0004vN\u001d;\u0016\u0005\tE\u0003\u0003B\b\u0002VjB1B!\u0016\u00036\tE\t\u0015!\u0003\u0003R\u0005I!/Z:u!>\u0014H\u000f\t\u0005\b=\tUB\u0011\u0001B-)!\u0011YF!\u0018\u0003`\t\u0005\u0004cA\u0012\u00036!9!Q\bB,\u0001\u0004Q\u0004b\u0002B#\u0005/\u0002\rA\u000f\u0005\t\u0005\u001b\u00129\u00061\u0001\u0003R!Q\u0011q\fB\u001b\u0003\u0003%\tA!\u001a\u0015\u0011\tm#q\rB5\u0005WB\u0011B!\u0010\u0003dA\u0005\t\u0019\u0001\u001e\t\u0013\t\u0015#1\rI\u0001\u0002\u0004Q\u0004B\u0003B'\u0005G\u0002\n\u00111\u0001\u0003R!Q\u0011\u0011\u000eB\u001b#\u0003%\tAa\u001c\u0016\u0005\tE$f\u0001\u001e\u0002p!Q\u00111\u0011B\u001b#\u0003%\tAa\u001c\t\u0015\t]$QGI\u0001\n\u0003\u0011I(\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005\tm$\u0006\u0002B)\u0003_B\u0001\"\fB\u001b\u0003\u0003%\tE\f\u0005\tq\tU\u0012\u0011!C\u0001s!IaH!\u000e\u0002\u0002\u0013\u0005!1\u0011\u000b\u0004\u0001\n\u0015\u0005\u0002\u0003#\u0003\u0002\u0006\u0005\t\u0019\u0001\u001e\t\u0011\u0019\u0013)$!A\u0005B\u001dC\u0011b\u0014B\u001b\u0003\u0003%\tAa#\u0015\u0007E\u0013i\t\u0003\u0005E\u0005\u0013\u000b\t\u00111\u0001A\u0011!1&QGA\u0001\n\u0003:\u0006\u0002C-\u00036\u0005\u0005I\u0011\t.\t\u0015\u0005\u0005&QGA\u0001\n\u0003\u0012)\nF\u0002R\u0005/C\u0001\u0002\u0012BJ\u0003\u0003\u0005\r\u0001Q\u0004\n\u00057[\u0012\u0011!E\u0001\u0005;\u000b!CQ8v]\u0012\u0004vN\u001d;t%\u0016\u001c\bo\u001c8tKB\u00191Ea(\u0007\u0013\t]2$!A\t\u0002\t\u00056#\u0002BP\u0005G#\u0002CCAZ\u0005KS$H!\u0015\u0003\\%!!qUA[\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|gn\r\u0005\b=\t}E\u0011\u0001BV)\t\u0011i\n\u0003\u0005Z\u0005?\u000b\t\u0011\"\u0012[\u0011)\t)Ma(\u0002\u0002\u0013\u0005%\u0011\u0017\u000b\t\u00057\u0012\u0019L!.\u00038\"9!Q\bBX\u0001\u0004Q\u0004b\u0002B#\u0005_\u0003\rA\u000f\u0005\t\u0005\u001b\u0012y\u000b1\u0001\u0003R!Q\u0011q\u001aBP\u0003\u0003%\tIa/\u0015\t\tu&Q\u0019\t\u0006\u001f\u0005U'q\u0018\t\b\u001f\t\u0005'H\u000fB)\u0013\r\u0011\u0019\r\u0005\u0002\u0007)V\u0004H.Z\u001a\t\u0015\u0005\u0005(\u0011XA\u0001\u0002\u0004\u0011Y\u0006\u0003\u0005]\u0005?\u000b\t\u0011\"\u0003^\u0011\u001da6$!A\u0005\nu\u0003"
)
public interface MasterMessages extends Serializable {
   public static class ElectedLeader$ implements Product, Serializable {
      public static final ElectedLeader$ MODULE$;

      static {
         new ElectedLeader$();
      }

      public String productPrefix() {
         return "ElectedLeader";
      }

      public int productArity() {
         return 0;
      }

      public Object productElement(int x$1) {
         throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof ElectedLeader$;
      }

      public int hashCode() {
         return 454631671;
      }

      public String toString() {
         return "ElectedLeader";
      }

      private Object readResolve() {
         return MODULE$;
      }

      public ElectedLeader$() {
         MODULE$ = this;
         Product$class.$init$(this);
      }
   }

   public static class RevokedLeadership$ implements Product, Serializable {
      public static final RevokedLeadership$ MODULE$;

      static {
         new RevokedLeadership$();
      }

      public String productPrefix() {
         return "RevokedLeadership";
      }

      public int productArity() {
         return 0;
      }

      public Object productElement(int x$1) {
         throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof RevokedLeadership$;
      }

      public int hashCode() {
         return -799595549;
      }

      public String toString() {
         return "RevokedLeadership";
      }

      private Object readResolve() {
         return MODULE$;
      }

      public RevokedLeadership$() {
         MODULE$ = this;
         Product$class.$init$(this);
      }
   }

   public static class CheckForWorkerTimeOut$ implements Product, Serializable {
      public static final CheckForWorkerTimeOut$ MODULE$;

      static {
         new CheckForWorkerTimeOut$();
      }

      public String productPrefix() {
         return "CheckForWorkerTimeOut";
      }

      public int productArity() {
         return 0;
      }

      public Object productElement(int x$1) {
         throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof CheckForWorkerTimeOut$;
      }

      public int hashCode() {
         return 561556130;
      }

      public String toString() {
         return "CheckForWorkerTimeOut";
      }

      private Object readResolve() {
         return MODULE$;
      }

      public CheckForWorkerTimeOut$() {
         MODULE$ = this;
         Product$class.$init$(this);
      }
   }

   public static class BeginRecovery implements Product, Serializable {
      private final Seq<ApplicationInfo> storedApps;
      private final Seq<WorkerInfo> storedWorkers;

      public Seq<ApplicationInfo> storedApps() {
         return this.storedApps;
      }

      public Seq<WorkerInfo> storedWorkers() {
         return this.storedWorkers;
      }

      public BeginRecovery copy(Seq<ApplicationInfo> storedApps, Seq<WorkerInfo> storedWorkers) {
         return new BeginRecovery(storedApps, storedWorkers);
      }

      public Seq<ApplicationInfo> copy$default$1() {
         return this.storedApps();
      }

      public Seq<WorkerInfo> copy$default$2() {
         return this.storedWorkers();
      }

      public String productPrefix() {
         return "BeginRecovery";
      }

      public int productArity() {
         return 2;
      }

      public Object productElement(int x$1) {
         Seq var10000;
         switch (x$1) {
            case 0:
               var10000 = this.storedApps();
               break;
            case 1:
               var10000 = this.storedWorkers();
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
         return x$1 instanceof BeginRecovery;
      }

      public int hashCode() {
         return ScalaRunTime$.MODULE$._hashCode(this);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var9;
         if (this != x$1) {
            label62: {
               boolean var3;
               if (x$1 instanceof BeginRecovery) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label44: {
                     label53: {
                        BeginRecovery var4 = (BeginRecovery)x$1;
                        Seq var10000 = this.storedApps();
                        Seq var5 = var4.storedApps();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label53;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label53;
                        }

                        var10000 = this.storedWorkers();
                        Seq var6 = var4.storedWorkers();
                        if (var10000 == null) {
                           if (var6 != null) {
                              break label53;
                           }
                        } else if (!var10000.equals(var6)) {
                           break label53;
                        }

                        if (var4.canEqual(this)) {
                           var9 = true;
                           break label44;
                        }
                     }

                     var9 = false;
                  }

                  if (var9) {
                     break label62;
                  }
               }

               var9 = false;
               return var9;
            }
         }

         var9 = true;
         return var9;
      }

      public BeginRecovery(Seq<ApplicationInfo> storedApps, Seq<WorkerInfo> storedWorkers) {
         this.storedApps = storedApps;
         this.storedWorkers = storedWorkers;
         Product$class.$init$(this);
      }
   }

   public static class BeginRecovery$ extends AbstractFunction2<Seq<ApplicationInfo>, Seq<WorkerInfo>, BeginRecovery> implements Serializable {
      public static final BeginRecovery$ MODULE$;

      static {
         new BeginRecovery$();
      }

      public final String toString() {
         return "BeginRecovery";
      }

      public BeginRecovery apply(Seq<ApplicationInfo> storedApps, Seq<WorkerInfo> storedWorkers) {
         return new BeginRecovery(storedApps, storedWorkers);
      }

      public Option<Tuple2<Seq<ApplicationInfo>, Seq<WorkerInfo>>> unapply(BeginRecovery x$0) {
         return (Option<Tuple2<Seq<ApplicationInfo>, Seq<WorkerInfo>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple2(x$0.storedApps(), x$0.storedWorkers())));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public BeginRecovery$() {
         MODULE$ = this;
      }
   }

   public static class CompleteRecovery$ implements Product, Serializable {
      public static final CompleteRecovery$ MODULE$;

      static {
         new CompleteRecovery$();
      }

      public String productPrefix() {
         return "CompleteRecovery";
      }

      public int productArity() {
         return 0;
      }

      public Object productElement(int x$1) {
         throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof CompleteRecovery$;
      }

      public int hashCode() {
         return -2051141618;
      }

      public String toString() {
         return "CompleteRecovery";
      }

      private Object readResolve() {
         return MODULE$;
      }

      public CompleteRecovery$() {
         MODULE$ = this;
         Product$class.$init$(this);
      }
   }

   public static class BoundPortsRequest$ implements Product, Serializable {
      public static final BoundPortsRequest$ MODULE$;

      static {
         new BoundPortsRequest$();
      }

      public String productPrefix() {
         return "BoundPortsRequest";
      }

      public int productArity() {
         return 0;
      }

      public Object productElement(int x$1) {
         throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof BoundPortsRequest$;
      }

      public int hashCode() {
         return 551533211;
      }

      public String toString() {
         return "BoundPortsRequest";
      }

      private Object readResolve() {
         return MODULE$;
      }

      public BoundPortsRequest$() {
         MODULE$ = this;
         Product$class.$init$(this);
      }
   }

   public static class BoundPortsResponse implements Product, Serializable {
      private final int rpcEndpointPort;
      private final int webUIPort;
      private final Option<Object> restPort;

      public int rpcEndpointPort() {
         return this.rpcEndpointPort;
      }

      public int webUIPort() {
         return this.webUIPort;
      }

      public Option<Object> restPort() {
         return this.restPort;
      }

      public BoundPortsResponse copy(int rpcEndpointPort, int webUIPort, Option<Object> restPort) {
         return new BoundPortsResponse(rpcEndpointPort, webUIPort, restPort);
      }

      public int copy$default$1() {
         return this.rpcEndpointPort();
      }

      public int copy$default$2() {
         return this.webUIPort();
      }

      public Option<Object> copy$default$3() {
         return this.restPort();
      }

      public String productPrefix() {
         return "BoundPortsResponse";
      }

      public int productArity() {
         return 3;
      }

      public Object productElement(int x$1) {
         Object var10000;
         switch (x$1) {
            case 0:
               var10000 = BoxesRunTime.boxToInteger(this.rpcEndpointPort());
               break;
            case 1:
               var10000 = BoxesRunTime.boxToInteger(this.webUIPort());
               break;
            case 2:
               var10000 = this.restPort();
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
         return x$1 instanceof BoundPortsResponse;
      }

      public int hashCode() {
         int var1 = -889275714;
         var1 = Statics.mix(var1, this.rpcEndpointPort());
         var1 = Statics.mix(var1, this.webUIPort());
         var1 = Statics.mix(var1, Statics.anyHash(this.restPort()));
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
               if (x$1 instanceof BoundPortsResponse) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label39: {
                     BoundPortsResponse var4 = (BoundPortsResponse)x$1;
                     if (this.rpcEndpointPort() == var4.rpcEndpointPort() && this.webUIPort() == var4.webUIPort()) {
                        label36: {
                           Option var10000 = this.restPort();
                           Option var5 = var4.restPort();
                           if (var10000 == null) {
                              if (var5 != null) {
                                 break label36;
                              }
                           } else if (!var10000.equals(var5)) {
                              break label36;
                           }

                           if (var4.canEqual(this)) {
                              var7 = true;
                              break label39;
                           }
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

      public BoundPortsResponse(int rpcEndpointPort, int webUIPort, Option<Object> restPort) {
         this.rpcEndpointPort = rpcEndpointPort;
         this.webUIPort = webUIPort;
         this.restPort = restPort;
         Product$class.$init$(this);
      }
   }

   public static class BoundPortsResponse$ extends AbstractFunction3<Object, Object, Option<Object>, BoundPortsResponse> implements Serializable {
      public static final BoundPortsResponse$ MODULE$;

      static {
         new BoundPortsResponse$();
      }

      public final String toString() {
         return "BoundPortsResponse";
      }

      public BoundPortsResponse apply(int rpcEndpointPort, int webUIPort, Option<Object> restPort) {
         return new BoundPortsResponse(rpcEndpointPort, webUIPort, restPort);
      }

      public Option<Tuple3<Object, Object, Option<Object>>> unapply(BoundPortsResponse x$0) {
         return (Option<Tuple3<Object, Object, Option<Object>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToInteger(x$0.rpcEndpointPort()), BoxesRunTime.boxToInteger(x$0.webUIPort()), x$0.restPort())));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public BoundPortsResponse$() {
         MODULE$ = this;
      }
   }
}
