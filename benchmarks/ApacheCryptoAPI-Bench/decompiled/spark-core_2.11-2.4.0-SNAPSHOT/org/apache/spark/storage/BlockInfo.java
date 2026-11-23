package org.apache.spark.storage;

import scala.Predef$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\ra!B\u0001\u0003\u0001\tQ!!\u0003\"m_\u000e\\\u0017J\u001c4p\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\"\u0001A\u0006\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g\u0011!\u0011\u0002A!b\u0001\n\u0003!\u0012!\u00027fm\u0016d7\u0001A\u000b\u0002+A\u0011acF\u0007\u0002\u0005%\u0011\u0001D\u0001\u0002\r'R|'/Y4f\u0019\u00164X\r\u001c\u0005\t5\u0001\u0011\t\u0011)A\u0005+\u00051A.\u001a<fY\u0002B\u0001\u0002\b\u0001\u0003\u0006\u0004%\t!H\u0001\tG2\f7o\u001d+bOV\ta\u0004\r\u0002 OA\u0019\u0001eI\u0013\u000e\u0003\u0005R!AI\u0007\u0002\u000fI,g\r\\3di&\u0011A%\t\u0002\t\u00072\f7o\u001d+bOB\u0011ae\n\u0007\u0001\t%A\u0013&!A\u0001\u0002\u000b\u0005qFA\u0002`IEB\u0001B\u000b\u0001\u0003\u0002\u0003\u0006IaK\u0001\nG2\f7o\u001d+bO\u0002\u0002$\u0001\f\u0018\u0011\u0007\u0001\u001aS\u0006\u0005\u0002']\u0011I\u0001&KA\u0001\u0002\u0003\u0015\taL\t\u0003aM\u0002\"\u0001D\u0019\n\u0005Ij!a\u0002(pi\"Lgn\u001a\t\u0003\u0019QJ!!N\u0007\u0003\u0007\u0005s\u0017\u0010\u0003\u00058\u0001\t\u0015\r\u0011\"\u00019\u0003)!X\r\u001c7NCN$XM]\u000b\u0002sA\u0011ABO\u0005\u0003w5\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005>\u0001\t\u0005\t\u0015!\u0003:\u0003-!X\r\u001c7NCN$XM\u001d\u0011\t\u000b}\u0002A\u0011\u0001!\u0002\rqJg.\u001b;?)\u0011\t%i\u0011%\u0011\u0005Y\u0001\u0001\"\u0002\n?\u0001\u0004)\u0002\"\u0002\u000f?\u0001\u0004!\u0005GA#H!\r\u00013E\u0012\t\u0003M\u001d#\u0011\u0002K\"\u0002\u0002\u0003\u0005)\u0011A\u0018\t\u000b]r\u0004\u0019A\u001d\t\u000b)\u0003A\u0011A&\u0002\tML'0Z\u000b\u0002\u0019B\u0011A\"T\u0005\u0003\u001d6\u0011A\u0001T8oO\")\u0001\u000b\u0001C\u0001#\u0006A1/\u001b>f?\u0012*\u0017\u000f\u0006\u0002S+B\u0011AbU\u0005\u0003)6\u0011A!\u00168ji\")ak\u0014a\u0001\u0019\u0006\t1\u000f\u0003\u0004Y\u0001\u0001\u0006K\u0001T\u0001\u0006?NL'0\u001a\u0005\u00065\u0002!\taW\u0001\fe\u0016\fG-\u001a:D_VtG/F\u0001]!\taQ,\u0003\u0002_\u001b\t\u0019\u0011J\u001c;\t\u000b\u0001\u0004A\u0011A1\u0002\u001fI,\u0017\rZ3s\u0007>,h\u000e^0%KF$\"A\u00152\t\u000b\r|\u0006\u0019\u0001/\u0002\u0003\rDa!\u001a\u0001!B\u0013a\u0016\u0001D0sK\u0006$WM]\"pk:$\b\"B4\u0001\t\u0003Y\u0015AC<sSR,'\u000fV1tW\")\u0011\u000e\u0001C\u0001U\u0006qqO]5uKJ$\u0016m]6`I\u0015\fHC\u0001*l\u0011\u0015a\u0007\u000e1\u0001M\u0003\u0005!\bB\u00028\u0001A\u0003&A*A\u0006`oJLG/\u001a:UCN\\\u0007\"\u00029\u0001\t\u0013\t\u0018aD2iK\u000e\\\u0017J\u001c<be&\fg\u000e^:\u0015\u0003I;aa\u001d\u0002\t\u0002\t!\u0018!\u0003\"m_\u000e\\\u0017J\u001c4p!\t1RO\u0002\u0004\u0002\u0005!\u0005!A^\n\u0003k.AQaP;\u0005\u0002a$\u0012\u0001\u001e\u0005\buV\u0014\r\u0011\"\u0001L\u0003%qujX,S\u0013R+%\u000b\u0003\u0004}k\u0002\u0006I\u0001T\u0001\u000b\u001d>{vKU%U\u000bJ\u0003\u0003b\u0002@v\u0005\u0004%\taS\u0001\u0010\u001d>su\fV!T\u0017~;&+\u0013+F%\"9\u0011\u0011A;!\u0002\u0013a\u0015\u0001\u0005(P\u001d~#\u0016iU&`/JKE+\u0012*!\u0001"
)
public class BlockInfo {
   private final StorageLevel level;
   private final ClassTag<?> classTag;
   private final boolean tellMaster;
   private long _size;
   private int _readerCount;
   private long _writerTask;

   public static long NON_TASK_WRITER() {
      return BlockInfo$.MODULE$.NON_TASK_WRITER();
   }

   public static long NO_WRITER() {
      return BlockInfo$.MODULE$.NO_WRITER();
   }

   public StorageLevel level() {
      return this.level;
   }

   public ClassTag<?> classTag() {
      return this.classTag;
   }

   public boolean tellMaster() {
      return this.tellMaster;
   }

   public long size() {
      return this._size;
   }

   public void size_$eq(long s) {
      this._size = s;
      this.checkInvariants();
   }

   public int readerCount() {
      return this._readerCount;
   }

   public void readerCount_$eq(int c) {
      this._readerCount = c;
      this.checkInvariants();
   }

   public long writerTask() {
      return this._writerTask;
   }

   public void writerTask_$eq(long t) {
      this._writerTask = t;
      this.checkInvariants();
   }

   private void checkInvariants() {
      Predef$.MODULE$.assert(this._readerCount >= 0);
      Predef$.MODULE$.assert(this._readerCount == 0 || this._writerTask == BlockInfo$.MODULE$.NO_WRITER());
   }

   public BlockInfo(StorageLevel level, ClassTag<?> classTag, boolean tellMaster) {
      this.level = level;
      this.classTag = classTag;
      this.tellMaster = tellMaster;
      this._size = 0L;
      this._readerCount = 0;
      this._writerTask = BlockInfo$.MODULE$.NO_WRITER();
      this.checkInvariants();
   }
}
