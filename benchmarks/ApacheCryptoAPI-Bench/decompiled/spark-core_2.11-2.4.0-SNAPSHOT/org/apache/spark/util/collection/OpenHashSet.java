package org.apache.spark.util.collection;

import org.apache.spark.annotation.Private;
import org.spark_project.guava.hash.Hashing;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.BufferedIterator;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.reflect.package$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ScalaRunTime$;

@Private
@ScalaSignature(
   bytes = "\u0006\u0001\t\u0005g\u0001B\u0001\u0003\u00015\u00111b\u00149f]\"\u000b7\u000f[*fi*\u00111\u0001B\u0001\u000bG>dG.Z2uS>t'BA\u0003\u0007\u0003\u0011)H/\u001b7\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001!\u0006\u0002\u000fYM\u0019\u0001aD\u000b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g!\t\u0001b#\u0003\u0002\u0018#\ta1+\u001a:jC2L'0\u00192mK\"A\u0011\u0004\u0001B\u0001B\u0003%!$A\bj]&$\u0018.\u00197DCB\f7-\u001b;z!\t\u00012$\u0003\u0002\u001d#\t\u0019\u0011J\u001c;\t\u0011y\u0001!\u0011!Q\u0001\n}\t!\u0002\\8bI\u001a\u000b7\r^8s!\t\u0001\u0002%\u0003\u0002\"#\t1Ai\\;cY\u0016D\u0001b\t\u0001\u0003\u0004\u0003\u0006Y\u0001J\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004cA\u0013)U5\taE\u0003\u0002(#\u00059!/\u001a4mK\u000e$\u0018BA\u0015'\u0005!\u0019E.Y:t)\u0006<\u0007CA\u0016-\u0019\u0001!\u0011\"\f\u0001!\u0002\u0003\u0005)\u0019\u0001\u0018\u0003\u0003Q\u000b\"a\f\u001a\u0011\u0005A\u0001\u0014BA\u0019\u0012\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"\u0001E\u001a\n\u0005Q\n\"aA!os\"\"AFN\u001dD!\t\u0001r'\u0003\u00029#\tY1\u000f]3dS\u0006d\u0017N_3ec\u0015\u0019#hO\u001f=\u001d\t\u00012(\u0003\u0002=#\u0005!Aj\u001c8hc\u0011!cH\u0011\n\u000f\u0005}\u0012U\"\u0001!\u000b\u0005\u0005c\u0011A\u0002\u001fs_>$h(C\u0001\u0013c\u0015\u0019C)R$G\u001d\t\u0001R)\u0003\u0002G#\u0005\u0019\u0011J\u001c;2\t\u0011r$I\u0005\u0005\u0006\u0013\u0002!\tAS\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007-{\u0005\u000b\u0006\u0002M\u001dB\u0019Q\n\u0001\u0016\u000e\u0003\tAQa\t%A\u0004\u0011BQ!\u0007%A\u0002iAQA\b%A\u0002}AQ!\u0013\u0001\u0005\u0002I#\"a\u0015,\u0015\u00051#\u0006bB+R\u0003\u0003\u0005\u001d\u0001J\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004\"B\rR\u0001\u0004Q\u0002\"B%\u0001\t\u0003AF#A-\u0015\u00051S\u0006bB.X\u0003\u0003\u0005\u001d\u0001J\u0001\u000bKZLG-\u001a8dK\u0012\u001a\u0004bB/\u0001\u0005\u0004%\tBX\u0001\u0007Q\u0006\u001c\b.\u001a:\u0016\u0003}\u00032\u0001Y=+\u001d\ti\u0015m\u0002\u0004c\u0005!\u0005aaY\u0001\f\u001fB,g\u000eS1tQN+G\u000f\u0005\u0002NI\u001a1\u0011A\u0001E\u0001\r\u0015\u001c2\u0001Z\b\u0016\u0011\u0015IE\r\"\u0001h)\u0005\u0019\u0007bB5e\u0005\u0004%\tA[\u0001\r\u001b\u0006CvlQ!Q\u0003\u000eKE+W\u000b\u00025!1A\u000e\u001aQ\u0001\ni\tQ\"T!Y?\u000e\u000b\u0005+Q\"J)f\u0003\u0003b\u00028e\u0005\u0004%\tA[\u0001\f\u0013:3\u0016\tT%E?B{5\u000b\u0003\u0004qI\u0002\u0006IAG\u0001\r\u0013:3\u0016\tT%E?B{5\u000b\t\u0005\be\u0012\u0014\r\u0011\"\u0001k\u0003EquJT#Y\u0013N#VIT\"F?6\u000b5k\u0013\u0005\u0007i\u0012\u0004\u000b\u0011\u0002\u000e\u0002%9{e*\u0012-J'R+ejQ#`\u001b\u0006\u001b6\n\t\u0005\bm\u0012\u0014\r\u0011\"\u0001k\u00035\u0001vjU%U\u0013>su,T!T\u0017\"1\u0001\u0010\u001aQ\u0001\ni\ta\u0002U(T\u0013RKuJT0N\u0003N[\u0005E\u0002\u0003{IBY(A\u0002%bg\",'/F\u0002}\u0003\u000b\u00192!_\b\u0016\u0011\u0015I\u0015\u0010\"\u0001\u007f)\u0005y\b#BA\u0001s\u0006\rQ\"\u00013\u0011\u0007-\n)\u0001B\u0005.s\u0002\u0006\t\u0011!b\u0001]!:\u0011Q\u0001\u001c\u0002\n\u00055\u0011GB\u0012;w\u0005-A(\r\u0003%}\t\u0013\u0012GB\u0012E\u000b\u0006=a)\r\u0003%}\t\u0013\u0002bBA\ns\u0012\u0005\u0011QC\u0001\u0005Q\u0006\u001c\b\u000eF\u0002\u001b\u0003/A\u0001\"!\u0007\u0002\u0012\u0001\u0007\u00111A\u0001\u0002_&*\u00110!\b\u00022\u00191\u0011q\u00043\u0001\u0003C\u0011\u0011\"\u00138u\u0011\u0006\u001c\b.\u001a:\u0014\t\u0005u\u00111\u0005\t\u0005\u0003\u0003I(\u0004C\u0004J\u0003;!\t!a\n\u0015\u0005\u0005%\u0002\u0003BA\u0001\u0003;A\u0001\"a\u0005\u0002\u001e\u0011\u0005\u0013Q\u0006\u000b\u00045\u0005=\u0002bBA\r\u0003W\u0001\rA\u0007\u0004\u0007\u0003g!\u0007!!\u000e\u0003\u00151{gn\u001a%bg\",'o\u0005\u0003\u00022\u0005]\u0002#BA\u0001s\u0006e\u0002c\u0001\t\u0002<%\u0019\u0011QH\t\u0003\t1{gn\u001a\u0005\b\u0013\u0006EB\u0011AA!)\t\t\u0019\u0005\u0005\u0003\u0002\u0002\u0005E\u0002\u0002CA\n\u0003c!\t%a\u0012\u0015\u0007i\tI\u0005\u0003\u0005\u0002\u001a\u0005\u0015\u0003\u0019AA\u001d\u0011\u001d\ti\u0005\u001aC\u0005\u0003\u001f\nQa\u001a:poF\"B!!\u0015\u0002XA\u0019\u0001#a\u0015\n\u0007\u0005U\u0013C\u0001\u0003V]&$\bbBA-\u0003\u0017\u0002\rAG\u0001\b]\u0016<8+\u001b>f\u0011\u001d\ti\u0006\u001aC\u0005\u0003?\nQ!\\8wKF\"b!!\u0015\u0002b\u0005\u0015\u0004bBA2\u00037\u0002\rAG\u0001\u0007_2$\u0007k\\:\t\u000f\u0005\u001d\u00141\fa\u00015\u00051a.Z<Q_ND\u0011\"a\u001be\u0005\u0004%I!!\u001c\u0002\t\u001d\u0014xn^\u000b\u0003\u0003_\u0002b\u0001EA95\u0005E\u0013bAA:#\tIa)\u001e8di&|g.\r\u0005\t\u0003o\"\u0007\u0015!\u0003\u0002p\u0005)qM]8xA!I\u00111\u00103C\u0002\u0013%\u0011QP\u0001\u0005[>4X-\u0006\u0002\u0002\u0000A9\u0001#!!\u001b5\u0005E\u0013bAAB#\tIa)\u001e8di&|gN\r\u0005\t\u0003\u000f#\u0007\u0015!\u0003\u0002\u0000\u0005)Qn\u001c<fA!I\u00111\u00123\u0002\u0002\u0013%\u0011QR\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002\u0010B!\u0011\u0011SAN\u001b\t\t\u0019J\u0003\u0003\u0002\u0016\u0006]\u0015\u0001\u00027b]\u001eT!!!'\u0002\t)\fg/Y\u0005\u0005\u0003;\u000b\u0019J\u0001\u0004PE*,7\r\u001e\u0005\b\u0003C\u0003\u0001\u0015!\u0003`\u0003\u001dA\u0017m\u001d5fe\u0002B\u0001\"!*\u0001\u0001\u0004%\tB[\u0001\n?\u000e\f\u0007/Y2jifD\u0011\"!+\u0001\u0001\u0004%\t\"a+\u0002\u001b}\u001b\u0017\r]1dSRLx\fJ3r)\u0011\t\t&!,\t\u0013\u0005=\u0016qUA\u0001\u0002\u0004Q\u0012a\u0001=%c!9\u00111\u0017\u0001!B\u0013Q\u0012AC0dCB\f7-\u001b;zA!A\u0011q\u0017\u0001A\u0002\u0013E!.A\u0003`[\u0006\u001c8\u000eC\u0005\u0002<\u0002\u0001\r\u0011\"\u0005\u0002>\u0006Iq,\\1tW~#S-\u001d\u000b\u0005\u0003#\ny\fC\u0005\u00020\u0006e\u0016\u0011!a\u00015!9\u00111\u0019\u0001!B\u0013Q\u0012AB0nCN\\\u0007\u0005\u0003\u0005\u0002H\u0002\u0001\r\u0011\"\u0005k\u0003\u0015y6/\u001b>f\u0011%\tY\r\u0001a\u0001\n#\ti-A\u0005`g&TXm\u0018\u0013fcR!\u0011\u0011KAh\u0011%\ty+!3\u0002\u0002\u0003\u0007!\u0004C\u0004\u0002T\u0002\u0001\u000b\u0015\u0002\u000e\u0002\r}\u001b\u0018N_3!\u0011!\t9\u000e\u0001a\u0001\n#Q\u0017AD0he><H\u000b\u001b:fg\"|G\u000e\u001a\u0005\n\u00037\u0004\u0001\u0019!C\t\u0003;\f!cX4s_^$\u0006N]3tQ>dGm\u0018\u0013fcR!\u0011\u0011KAp\u0011%\ty+!7\u0002\u0002\u0003\u0007!\u0004C\u0004\u0002d\u0002\u0001\u000b\u0015\u0002\u000e\u0002\u001f};'o\\<UQJ,7\u000f[8mI\u0002B\u0011\"a:\u0001\u0001\u0004%\t\"!;\u0002\u000f}\u0013\u0017\u000e^:fiV\u0011\u00111\u001e\t\u0004\u001b\u00065\u0018bAAx\u0005\t1!)\u001b;TKRD\u0011\"a=\u0001\u0001\u0004%\t\"!>\u0002\u0017}\u0013\u0017\u000e^:fi~#S-\u001d\u000b\u0005\u0003#\n9\u0010\u0003\u0006\u00020\u0006E\u0018\u0011!a\u0001\u0003WD\u0001\"a?\u0001A\u0003&\u00111^\u0001\t?\nLGo]3uA!9\u0011q \u0001\u0005\u0002\u0005%\u0018!C4fi\nKGoU3u\u0011-\u0011\u0019\u0001\u0001a\u0001\u0002\u0004%\tB!\u0002\u0002\u000b}#\u0017\r^1\u0016\u0005\t\u001d\u0001\u0003\u0002\t\u0003\n)J1Aa\u0003\u0012\u0005\u0015\t%O]1z\u0011-\u0011y\u0001\u0001a\u0001\u0002\u0004%\tB!\u0005\u0002\u0013}#\u0017\r^1`I\u0015\fH\u0003BA)\u0005'A!\"a,\u0003\u000e\u0005\u0005\t\u0019\u0001B\u0004\u0011!\u00119\u0002\u0001Q!\n\t\u001d\u0011AB0eCR\f\u0007\u0005\u0003\u0004\u0003\u001c\u0001!\tA[\u0001\u0005g&TX\r\u0003\u0004\u0003 \u0001!\tA[\u0001\tG\u0006\u0004\u0018mY5us\"9!1\u0005\u0001\u0005\u0002\t\u0015\u0012\u0001C2p]R\f\u0017N\\:\u0015\t\t\u001d\"Q\u0006\t\u0004!\t%\u0012b\u0001B\u0016#\t9!i\\8mK\u0006t\u0007b\u0002B\u0018\u0005C\u0001\rAK\u0001\u0002W\"9!1\u0007\u0001\u0005\u0002\tU\u0012aA1eIR!\u0011\u0011\u000bB\u001c\u0011\u001d\u0011yC!\rA\u0002)BqAa\u000f\u0001\t\u0003\u0011i$A\u0003v]&|g\u000eF\u0002M\u0005\u007fAqA!\u0011\u0003:\u0001\u0007A*A\u0003pi\",'\u000fC\u0004\u0003F\u0001!\tAa\u0012\u0002!\u0005$GmV5uQ>,HOU3tSj,Gc\u0001\u000e\u0003J!9!q\u0006B\"\u0001\u0004Q\u0003b\u0002B'\u0001\u0011\u0005!qJ\u0001\u000fe\u0016D\u0017m\u001d5JM:+W\rZ3e)!\t\tF!\u0015\u0003T\t]\u0003b\u0002B\u0018\u0005\u0017\u0002\rA\u000b\u0005\t\u0005+\u0012Y\u00051\u0001\u0002p\u0005a\u0011\r\u001c7pG\u0006$XMR;oG\"A!\u0011\fB&\u0001\u0004\ty(\u0001\u0005n_Z,g)\u001e8d\u0011\u001d\u0011i\u0006\u0001C\u0001\u0005?\naaZ3u!>\u001cHc\u0001\u000e\u0003b!9!q\u0006B.\u0001\u0004Q\u0003b\u0002B3\u0001\u0011\u0005!qM\u0001\tO\u0016$h+\u00197vKR\u0019!F!\u001b\t\u000f\t-$1\ra\u00015\u0005\u0019\u0001o\\:\t\u000f\t=\u0004\u0001\"\u0001\u0003r\u0005A\u0011\u000e^3sCR|'/\u0006\u0002\u0003tA)!Q\u000fB>U9\u0019aHa\u001e\n\u0007\te\u0014#A\u0004qC\u000e\\\u0017mZ3\n\t\tu$q\u0010\u0002\t\u0013R,'/\u0019;pe*\u0019!\u0011P\t\t\u000f\t\r\u0005\u0001\"\u0001\u0003\u0006\u0006aq-\u001a;WC2,XmU1gKR\u0019!Fa\"\t\u000f\t-$\u0011\u0011a\u00015!9!1\u0012\u0001\u0005\u0002\t5\u0015a\u00028fqR\u0004vn\u001d\u000b\u00045\t=\u0005b\u0002BI\u0005\u0013\u0003\rAG\u0001\bMJ|W\u000eU8t\u0011\u001d\u0011)\n\u0001C\u0005\u0005/\u000baA]3iCNDG\u0003CA)\u00053\u0013YJ!(\t\u000f\t=\"1\u0013a\u0001U!A!Q\u000bBJ\u0001\u0004\ty\u0007\u0003\u0005\u0003Z\tM\u0005\u0019AA@\u0011\u001d\u0011\t\u000b\u0001C\u0005\u0005G\u000b\u0001\u0002[1tQ\u000e|G-\u001a\u000b\u00045\t\u0015\u0006b\u0002BT\u0005?\u0003\rAG\u0001\u0002Q\"9!1\u0016\u0001\u0005\n\t5\u0016\u0001\u00048fqR\u0004vn^3s\u001f\u001a\u0014Dc\u0001\u000e\u00030\"9!\u0011\u0017BU\u0001\u0004Q\u0012!\u00018)\u0007\u0001\u0011)\f\u0005\u0003\u00038\nuVB\u0001B]\u0015\r\u0011YLB\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002\u0002B`\u0005s\u0013q\u0001\u0015:jm\u0006$X\r"
)
public class OpenHashSet<T> implements Serializable {
   public final double org$apache$spark$util$collection$OpenHashSet$$loadFactor;
   public final ClassTag<T> org$apache$spark$util$collection$OpenHashSet$$evidence$1;
   public final Hasher<T> hasher;
   public int org$apache$spark$util$collection$OpenHashSet$$_capacity;
   public int org$apache$spark$util$collection$OpenHashSet$$_mask;
   public int org$apache$spark$util$collection$OpenHashSet$$_size;
   public int org$apache$spark$util$collection$OpenHashSet$$_growThreshold;
   public BitSet org$apache$spark$util$collection$OpenHashSet$$_bitset;
   public Object _data;

   public static int POSITION_MASK() {
      return OpenHashSet$.MODULE$.POSITION_MASK();
   }

   public static int NONEXISTENCE_MASK() {
      return OpenHashSet$.MODULE$.NONEXISTENCE_MASK();
   }

   public static int INVALID_POS() {
      return OpenHashSet$.MODULE$.INVALID_POS();
   }

   public static int MAX_CAPACITY() {
      return OpenHashSet$.MODULE$.MAX_CAPACITY();
   }

   public Hasher<T> hasher() {
      return this.hasher;
   }

   public int org$apache$spark$util$collection$OpenHashSet$$_capacity() {
      return this.org$apache$spark$util$collection$OpenHashSet$$_capacity;
   }

   public void org$apache$spark$util$collection$OpenHashSet$$_capacity_$eq(int x$1) {
      this.org$apache$spark$util$collection$OpenHashSet$$_capacity = x$1;
   }

   public int org$apache$spark$util$collection$OpenHashSet$$_mask() {
      return this.org$apache$spark$util$collection$OpenHashSet$$_mask;
   }

   public void org$apache$spark$util$collection$OpenHashSet$$_mask_$eq(int x$1) {
      this.org$apache$spark$util$collection$OpenHashSet$$_mask = x$1;
   }

   public int org$apache$spark$util$collection$OpenHashSet$$_size() {
      return this.org$apache$spark$util$collection$OpenHashSet$$_size;
   }

   public void org$apache$spark$util$collection$OpenHashSet$$_size_$eq(int x$1) {
      this.org$apache$spark$util$collection$OpenHashSet$$_size = x$1;
   }

   public int org$apache$spark$util$collection$OpenHashSet$$_growThreshold() {
      return this.org$apache$spark$util$collection$OpenHashSet$$_growThreshold;
   }

   public void org$apache$spark$util$collection$OpenHashSet$$_growThreshold_$eq(int x$1) {
      this.org$apache$spark$util$collection$OpenHashSet$$_growThreshold = x$1;
   }

   public BitSet org$apache$spark$util$collection$OpenHashSet$$_bitset() {
      return this.org$apache$spark$util$collection$OpenHashSet$$_bitset;
   }

   public void org$apache$spark$util$collection$OpenHashSet$$_bitset_$eq(BitSet x$1) {
      this.org$apache$spark$util$collection$OpenHashSet$$_bitset = x$1;
   }

   public BitSet getBitSet() {
      return this.org$apache$spark$util$collection$OpenHashSet$$_bitset();
   }

   public Object _data() {
      return this._data;
   }

   public void _data_$eq(Object x$1) {
      this._data = x$1;
   }

   public int size() {
      return this.org$apache$spark$util$collection$OpenHashSet$$_size();
   }

   public int capacity() {
      return this.org$apache$spark$util$collection$OpenHashSet$$_capacity();
   }

   public boolean contains(T k) {
      return this.getPos(k) != OpenHashSet$.MODULE$.INVALID_POS();
   }

   public void add(T k) {
      this.addWithoutResize(k);
      this.rehashIfNeeded(k, OpenHashSet$.MODULE$.org$apache$spark$util$collection$OpenHashSet$$grow(), OpenHashSet$.MODULE$.org$apache$spark$util$collection$OpenHashSet$$move());
   }

   public OpenHashSet<T> union(OpenHashSet<T> other) {
      Iterator iterator = other.iterator();

      while(iterator.hasNext()) {
         this.add(iterator.next());
      }

      return this;
   }

   public int addWithoutResize(T k) {
      int pos = this.org$apache$spark$util$collection$OpenHashSet$$hashcode(this.hasher().hash(k)) & this.org$apache$spark$util$collection$OpenHashSet$$_mask();

      for(int delta = 1; this.org$apache$spark$util$collection$OpenHashSet$$_bitset().get(pos); ++delta) {
         if (BoxesRunTime.equals(ScalaRunTime$.MODULE$.array_apply(this._data(), pos), k)) {
            return pos;
         }

         pos = pos + delta & this.org$apache$spark$util$collection$OpenHashSet$$_mask();
      }

      ScalaRunTime$.MODULE$.array_update(this._data(), pos, k);
      this.org$apache$spark$util$collection$OpenHashSet$$_bitset().set(pos);
      this.org$apache$spark$util$collection$OpenHashSet$$_size_$eq(this.org$apache$spark$util$collection$OpenHashSet$$_size() + 1);
      return pos | OpenHashSet$.MODULE$.NONEXISTENCE_MASK();
   }

   public void rehashIfNeeded(T k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      if (this.org$apache$spark$util$collection$OpenHashSet$$_size() > this.org$apache$spark$util$collection$OpenHashSet$$_growThreshold()) {
         this.rehash(k, allocateFunc, moveFunc);
      }

   }

   public int getPos(T k) {
      int pos = this.org$apache$spark$util$collection$OpenHashSet$$hashcode(this.hasher().hash(k)) & this.org$apache$spark$util$collection$OpenHashSet$$_mask();

      for(int delta = 1; this.org$apache$spark$util$collection$OpenHashSet$$_bitset().get(pos); ++delta) {
         if (BoxesRunTime.equals(k, ScalaRunTime$.MODULE$.array_apply(this._data(), pos))) {
            return pos;
         }

         pos = pos + delta & this.org$apache$spark$util$collection$OpenHashSet$$_mask();
      }

      return OpenHashSet$.MODULE$.INVALID_POS();
   }

   public T getValue(int pos) {
      return (T)ScalaRunTime$.MODULE$.array_apply(this._data(), pos);
   }

   public Iterator<T> iterator() {
      return new Iterator<T>(this) {
         private int pos;
         // $FF: synthetic field
         private final OpenHashSet $outer;

         public Iterator<T> seq() {
            return Iterator$class.seq(this);
         }

         public boolean isEmpty() {
            return Iterator$class.isEmpty(this);
         }

         public boolean isTraversableAgain() {
            return Iterator$class.isTraversableAgain(this);
         }

         public boolean hasDefiniteSize() {
            return Iterator$class.hasDefiniteSize(this);
         }

         public Iterator<T> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<T> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<T> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<T, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<T, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<T> filter(Function1<T, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<T, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<T> withFilter(Function1<T, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<T> filterNot(Function1<T, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<T, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, T, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<T, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<T> takeWhile(Function1<T, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<T>, Iterator<T>> partition(Function1<T, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<T>, Iterator<T>> span(Function1<T, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<T> dropWhile(Function1<T, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<T, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<T, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<T, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<T, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<T, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<T> find(Function1<T, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<T, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<T> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<T>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<T>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<T>, Iterator<T>> duplicate() {
            return Iterator$class.duplicate(this);
         }

         public <B> Iterator<B> patch(int from, Iterator<B> patchElems, int replaced) {
            return Iterator$class.patch(this, from, patchElems, replaced);
         }

         public <B> void copyToArray(Object xs, int start, int len) {
            Iterator$class.copyToArray(this, xs, start, len);
         }

         public boolean sameElements(Iterator<?> that) {
            return Iterator$class.sameElements(this, that);
         }

         public Traversable<T> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<T> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<T> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<T> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<T, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<T, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, T, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<T, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, T, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<T, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, T, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<T, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, T, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<T, B, B> op) {
            return TraversableOnce$class.reduceRightOption(this, op);
         }

         public <A1> A1 reduce(Function2<A1, A1, A1> op) {
            return (A1)TraversableOnce$class.reduce(this, op);
         }

         public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
            return TraversableOnce$class.reduceOption(this, op);
         }

         public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
            return (A1)TraversableOnce$class.fold(this, z, op);
         }

         public <B> B aggregate(Function0<B> z, Function2<B, T, B> seqop, Function2<B, B, B> combop) {
            return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
         }

         public <B> B sum(Numeric<B> num) {
            return (B)TraversableOnce$class.sum(this, num);
         }

         public <B> B product(Numeric<B> num) {
            return (B)TraversableOnce$class.product(this, num);
         }

         public <B> T min(Ordering<B> cmp) {
            return (T)TraversableOnce$class.min(this, cmp);
         }

         public <B> T max(Ordering<B> cmp) {
            return (T)TraversableOnce$class.max(this, cmp);
         }

         public <B> T maxBy(Function1<T, B> f, Ordering<B> cmp) {
            return (T)TraversableOnce$class.maxBy(this, f, cmp);
         }

         public <B> T minBy(Function1<T, B> f, Ordering<B> cmp) {
            return (T)TraversableOnce$class.minBy(this, f, cmp);
         }

         public <B> void copyToBuffer(Buffer<B> dest) {
            TraversableOnce$class.copyToBuffer(this, dest);
         }

         public <B> void copyToArray(Object xs, int start) {
            TraversableOnce$class.copyToArray(this, xs, start);
         }

         public <B> void copyToArray(Object xs) {
            TraversableOnce$class.copyToArray(this, xs);
         }

         public <B> Object toArray(ClassTag<B> evidence$1) {
            return TraversableOnce$class.toArray(this, evidence$1);
         }

         public List<T> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<T> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<T> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<T> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<T> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, T, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<T, Tuple2<T, U>> ev) {
            return TraversableOnce$class.toMap(this, ev);
         }

         public String mkString(String start, String sep, String end) {
            return TraversableOnce$class.mkString(this, start, sep, end);
         }

         public String mkString(String sep) {
            return TraversableOnce$class.mkString(this, sep);
         }

         public String mkString() {
            return TraversableOnce$class.mkString(this);
         }

         public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
            return TraversableOnce$class.addString(this, b, start, sep, end);
         }

         public StringBuilder addString(StringBuilder b, String sep) {
            return TraversableOnce$class.addString(this, b, sep);
         }

         public StringBuilder addString(StringBuilder b) {
            return TraversableOnce$class.addString(this, b);
         }

         private int pos() {
            return this.pos;
         }

         private void pos_$eq(int x$1) {
            this.pos = x$1;
         }

         public boolean hasNext() {
            return this.pos() != OpenHashSet$.MODULE$.INVALID_POS();
         }

         public T next() {
            Object tmp = this.$outer.getValue(this.pos());
            this.pos_$eq(this.$outer.nextPos(this.pos() + 1));
            return (T)tmp;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               TraversableOnce$class.$init$(this);
               Iterator$class.$init$(this);
               this.pos = $outer.nextPos(0);
            }
         }
      };
   }

   public T getValueSafe(int pos) {
      Predef$.MODULE$.assert(this.org$apache$spark$util$collection$OpenHashSet$$_bitset().get(pos));
      return (T)ScalaRunTime$.MODULE$.array_apply(this._data(), pos);
   }

   public int nextPos(int fromPos) {
      return this.org$apache$spark$util$collection$OpenHashSet$$_bitset().nextSetBit(fromPos);
   }

   public void rehash(T k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      int newCapacity = this.org$apache$spark$util$collection$OpenHashSet$$_capacity() * 2;
      Predef$.MODULE$.require(newCapacity > 0 && newCapacity <= OpenHashSet$.MODULE$.MAX_CAPACITY(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final OpenHashSet $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't contain more than ", " elements"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger((int)(this.$outer.org$apache$spark$util$collection$OpenHashSet$$loadFactor * (double)OpenHashSet$.MODULE$.MAX_CAPACITY()))}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      allocateFunc.apply$mcVI$sp(newCapacity);
      BitSet newBitset = new BitSet(newCapacity);
      Object newData = this.org$apache$spark$util$collection$OpenHashSet$$evidence$1.newArray(newCapacity);
      int newMask = newCapacity - 1;

      for(int oldPos = 0; oldPos < this.capacity(); ++oldPos) {
         if (this.org$apache$spark$util$collection$OpenHashSet$$_bitset().get(oldPos)) {
            Object key = ScalaRunTime$.MODULE$.array_apply(this._data(), oldPos);
            int newPos = this.org$apache$spark$util$collection$OpenHashSet$$hashcode(this.hasher().hash(key)) & newMask;
            int i = 1;
            boolean keepGoing = true;

            while(keepGoing) {
               if (newBitset.get(newPos)) {
                  newPos = newPos + i & newMask;
                  ++i;
               } else {
                  ScalaRunTime$.MODULE$.array_update(newData, newPos, key);
                  newBitset.set(newPos);
                  moveFunc.apply$mcVII$sp(oldPos, newPos);
                  keepGoing = false;
               }
            }
         }
      }

      this.org$apache$spark$util$collection$OpenHashSet$$_bitset_$eq(newBitset);
      this._data_$eq(newData);
      this.org$apache$spark$util$collection$OpenHashSet$$_capacity_$eq(newCapacity);
      this.org$apache$spark$util$collection$OpenHashSet$$_mask_$eq(newMask);
      this.org$apache$spark$util$collection$OpenHashSet$$_growThreshold_$eq((int)(this.org$apache$spark$util$collection$OpenHashSet$$loadFactor * (double)newCapacity));
   }

   public int org$apache$spark$util$collection$OpenHashSet$$hashcode(int h) {
      return Hashing.murmur3_32().hashInt(h).asInt();
   }

   public int org$apache$spark$util$collection$OpenHashSet$$nextPowerOf2(int n) {
      int var10000;
      if (n == 0) {
         var10000 = 1;
      } else {
         int highBit = Integer.highestOneBit(n);
         var10000 = highBit == n ? n : highBit << 1;
      }

      return var10000;
   }

   public Hasher<Object> hasher$mcI$sp() {
      return this.hasher();
   }

   public Hasher<Object> hasher$mcJ$sp() {
      return this.hasher();
   }

   public int[] _data$mcI$sp() {
      return (int[])this._data();
   }

   public long[] _data$mcJ$sp() {
      return (long[])this._data();
   }

   public void _data$mcI$sp_$eq(int[] x$1) {
      this._data_$eq(x$1);
   }

   public void _data$mcJ$sp_$eq(long[] x$1) {
      this._data_$eq(x$1);
   }

   public boolean contains$mcI$sp(int k) {
      return this.contains(BoxesRunTime.boxToInteger(k));
   }

   public boolean contains$mcJ$sp(long k) {
      return this.contains(BoxesRunTime.boxToLong(k));
   }

   public void add$mcI$sp(int k) {
      this.add(BoxesRunTime.boxToInteger(k));
   }

   public void add$mcJ$sp(long k) {
      this.add(BoxesRunTime.boxToLong(k));
   }

   public OpenHashSet<Object> union$mcI$sp(OpenHashSet<Object> other) {
      return this.union(other);
   }

   public OpenHashSet<Object> union$mcJ$sp(OpenHashSet<Object> other) {
      return this.union(other);
   }

   public int addWithoutResize$mcI$sp(int k) {
      return this.addWithoutResize(BoxesRunTime.boxToInteger(k));
   }

   public int addWithoutResize$mcJ$sp(long k) {
      return this.addWithoutResize(BoxesRunTime.boxToLong(k));
   }

   public void rehashIfNeeded$mcI$sp(int k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      this.rehashIfNeeded(BoxesRunTime.boxToInteger(k), allocateFunc, moveFunc);
   }

   public void rehashIfNeeded$mcJ$sp(long k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      this.rehashIfNeeded(BoxesRunTime.boxToLong(k), allocateFunc, moveFunc);
   }

   public int getPos$mcI$sp(int k) {
      return this.getPos(BoxesRunTime.boxToInteger(k));
   }

   public int getPos$mcJ$sp(long k) {
      return this.getPos(BoxesRunTime.boxToLong(k));
   }

   public int getValue$mcI$sp(int pos) {
      return BoxesRunTime.unboxToInt(this.getValue(pos));
   }

   public long getValue$mcJ$sp(int pos) {
      return BoxesRunTime.unboxToLong(this.getValue(pos));
   }

   public int getValueSafe$mcI$sp(int pos) {
      return BoxesRunTime.unboxToInt(this.getValueSafe(pos));
   }

   public long getValueSafe$mcJ$sp(int pos) {
      return BoxesRunTime.unboxToLong(this.getValueSafe(pos));
   }

   public void rehash$mcI$sp(int k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      this.rehash(BoxesRunTime.boxToInteger(k), allocateFunc, moveFunc);
   }

   public void rehash$mcJ$sp(long k, Function1<Object, BoxedUnit> allocateFunc, Function2<Object, Object, BoxedUnit> moveFunc) {
      this.rehash(BoxesRunTime.boxToLong(k), allocateFunc, moveFunc);
   }

   public boolean specInstance$() {
      return false;
   }

   public OpenHashSet(int initialCapacity, double loadFactor, ClassTag<T> evidence$1) {
      this.org$apache$spark$util$collection$OpenHashSet$$loadFactor = loadFactor;
      this.org$apache$spark$util$collection$OpenHashSet$$evidence$1 = evidence$1;
      if (!this.specInstance$()) {
         Object var10001;
         label50: {
            label56: {
               Predef$.MODULE$.require(initialCapacity <= OpenHashSet$.MODULE$.MAX_CAPACITY(), new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't make capacity bigger than ", " elements"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(OpenHashSet$.MODULE$.MAX_CAPACITY())}));
                  }
               });
               Predef$.MODULE$.require(initialCapacity >= 0, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Invalid initial capacity";
                  }
               });
               Predef$.MODULE$.require(loadFactor < (double)1.0F, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Load factor must be less than 1.0";
                  }
               });
               Predef$.MODULE$.require(loadFactor > (double)0.0F, new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Load factor must be greater than 0.0";
                  }
               });
               ClassTag mt = package$.MODULE$.classTag(evidence$1);
               ClassTag var6 = ClassTag$.MODULE$.Long();
               if (mt == null) {
                  if (var6 == null) {
                     break label56;
                  }
               } else if (mt.equals(var6)) {
                  break label56;
               }

               label43: {
                  ClassTag var7 = ClassTag$.MODULE$.Int();
                  if (mt == null) {
                     if (var7 == null) {
                        break label43;
                     }
                  } else if (mt.equals(var7)) {
                     break label43;
                  }

                  var10001 = new Hasher();
                  break label50;
               }

               var10001 = new IntHasher();
               break label50;
            }

            var10001 = new LongHasher();
         }

         this.hasher = (Hasher<T>)var10001;
         this.org$apache$spark$util$collection$OpenHashSet$$_capacity = this.org$apache$spark$util$collection$OpenHashSet$$nextPowerOf2(initialCapacity);
         this.org$apache$spark$util$collection$OpenHashSet$$_mask = this.org$apache$spark$util$collection$OpenHashSet$$_capacity() - 1;
         this.org$apache$spark$util$collection$OpenHashSet$$_size = 0;
         this.org$apache$spark$util$collection$OpenHashSet$$_growThreshold = (int)(loadFactor * (double)this.org$apache$spark$util$collection$OpenHashSet$$_capacity());
         this.org$apache$spark$util$collection$OpenHashSet$$_bitset = new BitSet(this.org$apache$spark$util$collection$OpenHashSet$$_capacity());
         this._data_$eq(evidence$1.newArray(this.org$apache$spark$util$collection$OpenHashSet$$_capacity()));
      }

   }

   public OpenHashSet(int initialCapacity, ClassTag<T> evidence$2) {
      this(initialCapacity, 0.7, evidence$2);
   }

   public OpenHashSet(ClassTag<T> evidence$3) {
      this(64, evidence$3);
   }

   public static class Hasher<T> implements Serializable {
      public int hash(T o) {
         return o.hashCode();
      }

      public int hash$mcI$sp(int o) {
         return this.hash(BoxesRunTime.boxToInteger(o));
      }

      public int hash$mcJ$sp(long o) {
         return this.hash(BoxesRunTime.boxToLong(o));
      }
   }

   public static class LongHasher extends OpenHashSet$Hasher$mcJ$sp {
      public int hash(long o) {
         return this.hash$mcJ$sp(o);
      }

      public int hash$mcJ$sp(long o) {
         return (int)(o ^ o >>> 32);
      }
   }

   public static class IntHasher extends OpenHashSet$Hasher$mcI$sp {
      public int hash(int o) {
         return this.hash$mcI$sp(o);
      }

      public int hash$mcI$sp(int o) {
         return o;
      }
   }
}
