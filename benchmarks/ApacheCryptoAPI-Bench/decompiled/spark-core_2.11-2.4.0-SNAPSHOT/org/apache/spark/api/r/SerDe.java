package org.apache.spark.api.r;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import scala.Function2;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\tEtAB\u0001\u0003\u0011\u00031A\"A\u0003TKJ$UM\u0003\u0002\u0004\t\u0005\t!O\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u0004\"!\u0004\b\u000e\u0003\t1aa\u0004\u0002\t\u0002\u0019\u0001\"!B*fe\u0012+7C\u0001\b\u0012!\t\u0011R#D\u0001\u0014\u0015\u0005!\u0012!B:dC2\f\u0017B\u0001\f\u0014\u0005\u0019\te.\u001f*fM\")\u0001D\u0004C\u00015\u00051A(\u001b8jiz\u001a\u0001\u0001F\u0001\r\u000b\u0011ab\u0002A\u000f\u0003\u001bM\u000bFJU3bI>\u0013'.Z2u!\u0015\u0011b\u0004\t\u0015,\u0013\ty2CA\u0005Gk:\u001cG/[8oeA\u0011\u0011EJ\u0007\u0002E)\u00111\u0005J\u0001\u0003S>T\u0011!J\u0001\u0005U\u00064\u0018-\u0003\u0002(E\tyA)\u0019;b\u0013:\u0004X\u000f^*ue\u0016\fW\u000e\u0005\u0002\u0013S%\u0011!f\u0005\u0002\u0005\u0007\"\f'\u000f\u0005\u0002-_5\tQF\u0003\u0002/I\u0005!A.\u00198h\u0013\t\u0001TF\u0001\u0004PE*,7\r^\u0003\u0005e9\u00011G\u0001\bT#2;&/\u001b;f\u001f\nTWm\u0019;\u0011\u000bIqBgK\u001c\u0011\u0005\u0005*\u0014B\u0001\u001c#\u0005A!\u0015\r^1PkR\u0004X\u000f^*ue\u0016\fW\u000e\u0005\u0002\u0013q%\u0011\u0011h\u0005\u0002\b\u0005>|G.Z1o\u0011%Yd\u00021A\u0001B\u0003&A(A\u0007tc2\u0014V-\u00193PE*,7\r\u001e\t\u0003{mi\u0011A\u0004\u0005\n\u007f9\u0001\r\u0011!Q!\n\u0001\u000bab]9m/JLG/Z(cU\u0016\u001cG\u000f\u0005\u0002>c!)!I\u0004C\u0001\u0007\u0006\u00012/\u001a;T#2\u0013V-\u00193PE*,7\r\u001e\u000b\u0003{\u0011CQ!R!A\u0002q\nQA^1mk\u0016DQa\u0012\b\u0005\u0002!\u000b\u0011c]3u'FcuK]5uK>\u0013'.Z2u)\ti\u0014\nC\u0003F\r\u0002\u0007\u0001\tC\u0003L\u001d\u0011\u0005A*\u0001\bsK\u0006$wJ\u00196fGR$\u0016\u0010]3\u0015\u0005!j\u0005\"\u0002(K\u0001\u0004\u0001\u0013a\u00013jg\")\u0001K\u0004C\u0001#\u0006Q!/Z1e\u001f\nTWm\u0019;\u0015\u0007-\u00126\u000bC\u0003O\u001f\u0002\u0007\u0001\u0005C\u0003U\u001f\u0002\u0007Q+\u0001\tkm6|%M[3diR\u0013\u0018mY6feB\u0011QBV\u0005\u0003/\n\u0011\u0001C\u0013,N\u001f\nTWm\u0019;Ue\u0006\u001c7.\u001a:\t\u000besA\u0011\u0001.\u0002\u001fI,\u0017\r\u001a+za\u0016$wJ\u00196fGR$BaK.]=\")a\n\u0017a\u0001A!)Q\f\u0017a\u0001Q\u0005AA-\u0019;b)f\u0004X\rC\u0003U1\u0002\u0007Q\u000bC\u0003a\u001d\u0011\u0005\u0011-A\u0005sK\u0006$')\u001f;fgR\u0011!\r\u001b\t\u0004%\r,\u0017B\u00013\u0014\u0005\u0015\t%O]1z!\t\u0011b-\u0003\u0002h'\t!!)\u001f;f\u0011\u0015Iw\f1\u0001!\u0003\tIg\u000eC\u0003l\u001d\u0011\u0005A.A\u0004sK\u0006$\u0017J\u001c;\u0015\u00055\u0004\bC\u0001\no\u0013\ty7CA\u0002J]RDQ!\u001b6A\u0002\u0001BQA\u001d\b\u0005\u0002M\f!B]3bI\u0012{WO\u00197f)\t!x\u000f\u0005\u0002\u0013k&\u0011ao\u0005\u0002\u0007\t>,(\r\\3\t\u000b%\f\b\u0019\u0001\u0011\t\u000betA\u0011\u0001>\u0002\u001fI,\u0017\rZ*ue&twMQ=uKN$Ra_A\u0003\u0003\u000f\u0001\"\u0001`@\u000f\u0005Ii\u0018B\u0001@\u0014\u0003\u0019\u0001&/\u001a3fM&!\u0011\u0011AA\u0002\u0005\u0019\u0019FO]5oO*\u0011ap\u0005\u0005\u0006Sb\u0004\r\u0001\t\u0005\u0007\u0003\u0013A\b\u0019A7\u0002\u00071,g\u000eC\u0004\u0002\u000e9!\t!a\u0004\u0002\u0015I,\u0017\rZ*ue&tw\rF\u0002|\u0003#Aa![A\u0006\u0001\u0004\u0001\u0003bBA\u000b\u001d\u0011\u0005\u0011qC\u0001\fe\u0016\fGMQ8pY\u0016\fg\u000eF\u00028\u00033Aa![A\n\u0001\u0004\u0001\u0003bBA\u000f\u001d\u0011\u0005\u0011qD\u0001\te\u0016\fG\rR1uKR!\u0011\u0011EA\u0017!\u0011\t\u0019#!\u000b\u000e\u0005\u0005\u0015\"bAA\u0014I\u0005\u00191/\u001d7\n\t\u0005-\u0012Q\u0005\u0002\u0005\t\u0006$X\r\u0003\u0004j\u00037\u0001\r\u0001\t\u0005\b\u0003cqA\u0011AA\u001a\u0003!\u0011X-\u00193US6,G\u0003BA\u001b\u0003w\u0001B!a\t\u00028%!\u0011\u0011HA\u0013\u0005%!\u0016.\\3ti\u0006l\u0007\u000f\u0003\u0004j\u0003_\u0001\r\u0001\t\u0005\b\u0003\u007fqA\u0011AA!\u00031\u0011X-\u00193CsR,7/\u0011:s)\u0011\t\u0019%!\u0012\u0011\u0007I\u0019'\r\u0003\u0004j\u0003{\u0001\r\u0001\t\u0005\b\u0003\u0013rA\u0011AA&\u0003)\u0011X-\u00193J]R\f%O\u001d\u000b\u0005\u0003\u001b\ny\u0005E\u0002\u0013G6Da![A$\u0001\u0004\u0001\u0003bBA*\u001d\u0011\u0005\u0011QK\u0001\u000ee\u0016\fG\rR8vE2,\u0017I\u001d:\u0015\t\u0005]\u0013\u0011\f\t\u0004%\r$\bBB5\u0002R\u0001\u0007\u0001\u0005C\u0004\u0002^9!\t!a\u0018\u0002\u001dI,\u0017\r\u001a\"p_2,\u0017M\\!seR!\u0011\u0011MA2!\r\u00112m\u000e\u0005\u0007S\u0006m\u0003\u0019\u0001\u0011\t\u000f\u0005\u001dd\u0002\"\u0001\u0002j\u0005i!/Z1e'R\u0014\u0018N\\4BeJ$B!a\u001b\u0002nA\u0019!cY>\t\r%\f)\u00071\u0001!\u0011\u001d\t\tH\u0004C\u0001\u0003g\n\u0011B]3bI\u0006\u0013(/Y=\u0015\r\u0005U\u0014qRAIa\u0011\t9(! \u0011\tI\u0019\u0017\u0011\u0010\t\u0005\u0003w\ni\b\u0004\u0001\u0005\u0019\u0005}\u0014qNA\u0001\u0002\u0003\u0015\t!!!\u0003\u0007}#\u0013'\u0005\u0003\u0002\u0004\u0006%\u0005c\u0001\n\u0002\u0006&\u0019\u0011qQ\n\u0003\u000f9{G\u000f[5oOB\u0019!#a#\n\u0007\u000555CA\u0002B]fDaATA8\u0001\u0004\u0001\u0003B\u0002+\u0002p\u0001\u0007Q\u000bC\u0004\u0002\u0016:!\t!a&\u0002\u0011I,\u0017\r\u001a'jgR$b!!'\u0002\u001c\u0006u\u0005c\u0001\ndW!1a*a%A\u0002\u0001Ba\u0001VAJ\u0001\u0004)\u0006bBAQ\u001d\u0011\u0005\u00111U\u0001\be\u0016\fG-T1q)\u0019\t)+!-\u00024B1\u0011qUAWW-j!!!+\u000b\u0007\u0005-F%\u0001\u0003vi&d\u0017\u0002BAX\u0003S\u00131!T1q\u0011\u0019I\u0017q\u0014a\u0001A!1A+a(A\u0002UCq!a.\u000f\t\u0003\tI,A\u0005xe&$X\rV=qKR1\u00111XAa\u0003\u000b\u00042AEA_\u0013\r\tyl\u0005\u0002\u0005+:LG\u000fC\u0004\u0002D\u0006U\u0006\u0019\u0001\u001b\u0002\u0007\u0011|7\u000fC\u0004\u0002H\u0006U\u0006\u0019A>\u0002\u000fQL\b/Z*ue\"9\u00111\u001a\b\u0005\n\u00055\u0017!D<sSR,7*Z=WC2,X\r\u0006\u0006\u0002<\u0006=\u0017\u0011[Ak\u0003/Dq!a1\u0002J\u0002\u0007A\u0007C\u0004\u0002T\u0006%\u0007\u0019A\u0016\u0002\u0007-,\u0017\u0010\u0003\u0004F\u0003\u0013\u0004\ra\u000b\u0005\u0007)\u0006%\u0007\u0019A+\t\u000f\u0005mg\u0002\"\u0001\u0002^\u0006YqO]5uK>\u0013'.Z2u)!\tY,a8\u0002b\u0006\u0015\bbBAb\u00033\u0004\r\u0001\u000e\u0005\b\u0003G\fI\u000e1\u0001,\u0003\ry'M\u001b\u0005\u0007)\u0006e\u0007\u0019A+\t\u000f\u0005%h\u0002\"\u0001\u0002l\u0006AqO]5uK&sG\u000f\u0006\u0004\u0002<\u00065\u0018\u0011\u001f\u0005\b\u0003_\f9\u000f1\u00015\u0003\ryW\u000f\u001e\u0005\u0007\u000b\u0006\u001d\b\u0019A7\t\u000f\u0005Uh\u0002\"\u0001\u0002x\u0006YqO]5uK\u0012{WO\u00197f)\u0019\tY,!?\u0002|\"9\u0011q^Az\u0001\u0004!\u0004BB#\u0002t\u0002\u0007A\u000fC\u0004\u0002\u0000:!\tA!\u0001\u0002\u0019]\u0014\u0018\u000e^3C_>dW-\u00198\u0015\r\u0005m&1\u0001B\u0003\u0011\u001d\ty/!@A\u0002QBa!RA\u007f\u0001\u00049\u0004b\u0002B\u0005\u001d\u0011\u0005!1B\u0001\noJLG/\u001a#bi\u0016$b!a/\u0003\u000e\t=\u0001bBAx\u0005\u000f\u0001\r\u0001\u000e\u0005\b\u000b\n\u001d\u0001\u0019AA\u0011\u0011\u001d\u0011\u0019B\u0004C\u0001\u0005+\t\u0011b\u001e:ji\u0016$\u0016.\\3\u0015\r\u0005m&q\u0003B\r\u0011\u001d\tyO!\u0005A\u0002QBq!\u0012B\t\u0001\u0004\u0011Y\u0002\u0005\u0003\u0002$\tu\u0011\u0002\u0002B\u0010\u0003K\u0011A\u0001V5nK\"9!1\u0003\b\u0005\u0002\t\rBCBA^\u0005K\u00119\u0003C\u0004\u0002p\n\u0005\u0002\u0019\u0001\u001b\t\u000f\u0015\u0013\t\u00031\u0001\u00026!9!1\u0006\b\u0005\u0002\t5\u0012aC<sSR,7\u000b\u001e:j]\u001e$b!a/\u00030\tE\u0002bBAx\u0005S\u0001\r\u0001\u000e\u0005\u0007\u000b\n%\u0002\u0019A>\t\u000f\tUb\u0002\"\u0001\u00038\u0005QqO]5uK\nKH/Z:\u0015\r\u0005m&\u0011\bB\u001e\u0011\u001d\tyOa\rA\u0002QBa!\u0012B\u001a\u0001\u0004\u0011\u0007b\u0002B \u001d\u0011\u0005!\u0011I\u0001\noJLG/\u001a&PE*$\u0002\"a/\u0003D\t\u0015#q\t\u0005\b\u0003_\u0014i\u00041\u00015\u0011\u0019)%Q\ba\u0001W!1AK!\u0010A\u0002UCqAa\u0013\u000f\t\u0003\u0011i%A\u0006xe&$X-\u00138u\u0003J\u0014HCBA^\u0005\u001f\u0012\t\u0006C\u0004\u0002p\n%\u0003\u0019\u0001\u001b\t\u000f\u0015\u0013I\u00051\u0001\u0002N!9!Q\u000b\b\u0005\u0002\t]\u0013AD<sSR,Gi\\;cY\u0016\f%O\u001d\u000b\u0007\u0003w\u0013IFa\u0017\t\u000f\u0005=(1\u000ba\u0001i!9QIa\u0015A\u0002\u0005]\u0003b\u0002B0\u001d\u0011\u0005!\u0011M\u0001\u0010oJLG/\u001a\"p_2,\u0017M\\!seR1\u00111\u0018B2\u0005KBq!a<\u0003^\u0001\u0007A\u0007C\u0004F\u0005;\u0002\r!!\u0019\t\u000f\t%d\u0002\"\u0001\u0003l\u0005qqO]5uKN#(/\u001b8h\u0003J\u0014HCBA^\u0005[\u0012y\u0007C\u0004\u0002p\n\u001d\u0004\u0019\u0001\u001b\t\u000f\u0015\u00139\u00071\u0001\u0002l\u0001"
)
public final class SerDe {
   public static void writeStringArr(DataOutputStream var0, String[] var1) {
      SerDe$.MODULE$.writeStringArr(var0, var1);
   }

   public static void writeBooleanArr(DataOutputStream var0, boolean[] var1) {
      SerDe$.MODULE$.writeBooleanArr(var0, var1);
   }

   public static void writeDoubleArr(DataOutputStream var0, double[] var1) {
      SerDe$.MODULE$.writeDoubleArr(var0, var1);
   }

   public static void writeIntArr(DataOutputStream var0, int[] var1) {
      SerDe$.MODULE$.writeIntArr(var0, var1);
   }

   public static void writeJObj(DataOutputStream var0, Object var1, JVMObjectTracker var2) {
      SerDe$.MODULE$.writeJObj(var0, var1, var2);
   }

   public static void writeBytes(DataOutputStream var0, byte[] var1) {
      SerDe$.MODULE$.writeBytes(var0, var1);
   }

   public static void writeString(DataOutputStream var0, String var1) {
      SerDe$.MODULE$.writeString(var0, var1);
   }

   public static void writeTime(DataOutputStream var0, Timestamp var1) {
      SerDe$.MODULE$.writeTime(var0, var1);
   }

   public static void writeTime(DataOutputStream var0, Time var1) {
      SerDe$.MODULE$.writeTime(var0, var1);
   }

   public static void writeDate(DataOutputStream var0, Date var1) {
      SerDe$.MODULE$.writeDate(var0, var1);
   }

   public static void writeBoolean(DataOutputStream var0, boolean var1) {
      SerDe$.MODULE$.writeBoolean(var0, var1);
   }

   public static void writeDouble(DataOutputStream var0, double var1) {
      SerDe$.MODULE$.writeDouble(var0, var1);
   }

   public static void writeInt(DataOutputStream var0, int var1) {
      SerDe$.MODULE$.writeInt(var0, var1);
   }

   public static void writeObject(DataOutputStream var0, Object var1, JVMObjectTracker var2) {
      SerDe$.MODULE$.writeObject(var0, var1, var2);
   }

   public static void writeType(DataOutputStream var0, String var1) {
      SerDe$.MODULE$.writeType(var0, var1);
   }

   public static Map<Object, Object> readMap(DataInputStream var0, JVMObjectTracker var1) {
      return SerDe$.MODULE$.readMap(var0, var1);
   }

   public static Object[] readList(DataInputStream var0, JVMObjectTracker var1) {
      return SerDe$.MODULE$.readList(var0, var1);
   }

   public static Object readArray(DataInputStream var0, JVMObjectTracker var1) {
      return SerDe$.MODULE$.readArray(var0, var1);
   }

   public static String[] readStringArr(DataInputStream var0) {
      return SerDe$.MODULE$.readStringArr(var0);
   }

   public static boolean[] readBooleanArr(DataInputStream var0) {
      return SerDe$.MODULE$.readBooleanArr(var0);
   }

   public static double[] readDoubleArr(DataInputStream var0) {
      return SerDe$.MODULE$.readDoubleArr(var0);
   }

   public static int[] readIntArr(DataInputStream var0) {
      return SerDe$.MODULE$.readIntArr(var0);
   }

   public static byte[][] readBytesArr(DataInputStream var0) {
      return SerDe$.MODULE$.readBytesArr(var0);
   }

   public static Timestamp readTime(DataInputStream var0) {
      return SerDe$.MODULE$.readTime(var0);
   }

   public static Date readDate(DataInputStream var0) {
      return SerDe$.MODULE$.readDate(var0);
   }

   public static boolean readBoolean(DataInputStream var0) {
      return SerDe$.MODULE$.readBoolean(var0);
   }

   public static String readString(DataInputStream var0) {
      return SerDe$.MODULE$.readString(var0);
   }

   public static String readStringBytes(DataInputStream var0, int var1) {
      return SerDe$.MODULE$.readStringBytes(var0, var1);
   }

   public static double readDouble(DataInputStream var0) {
      return SerDe$.MODULE$.readDouble(var0);
   }

   public static int readInt(DataInputStream var0) {
      return SerDe$.MODULE$.readInt(var0);
   }

   public static byte[] readBytes(DataInputStream var0) {
      return SerDe$.MODULE$.readBytes(var0);
   }

   public static Object readTypedObject(DataInputStream var0, char var1, JVMObjectTracker var2) {
      return SerDe$.MODULE$.readTypedObject(var0, var1, var2);
   }

   public static Object readObject(DataInputStream var0, JVMObjectTracker var1) {
      return SerDe$.MODULE$.readObject(var0, var1);
   }

   public static char readObjectType(DataInputStream var0) {
      return SerDe$.MODULE$.readObjectType(var0);
   }

   public static SerDe$ setSQLWriteObject(Function2<DataOutputStream, Object, Object> var0) {
      return SerDe$.MODULE$.setSQLWriteObject(var0);
   }

   public static SerDe$ setSQLReadObject(Function2<DataInputStream, Object, Object> var0) {
      return SerDe$.MODULE$.setSQLReadObject(var0);
   }
}
