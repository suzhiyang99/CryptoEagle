package org.apache.spark.storage;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.memory.MemoryMode;
import org.apache.spark.util.Utils$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\t\u001dd\u0001B\u0001\u0003\u0001-\u0011Ab\u0015;pe\u0006<W\rT3wK2T!a\u0001\u0003\u0002\u000fM$xN]1hK*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001aA\u0003\u0005\u0002\u000e%5\taB\u0003\u0002\u0010!\u0005!A.\u00198h\u0015\u0005\t\u0012\u0001\u00026bm\u0006L!a\u0005\b\u0003\r=\u0013'.Z2u!\t)\u0002$D\u0001\u0017\u0015\t9\u0002#\u0001\u0002j_&\u0011\u0011D\u0006\u0002\u000f\u000bb$XM\u001d8bY&T\u0018M\u00197f\u0011!Y\u0002A!a\u0001\n\u0013a\u0012\u0001C0vg\u0016$\u0015n]6\u0016\u0003u\u0001\"AH\u0011\u000e\u0003}Q\u0011\u0001I\u0001\u0006g\u000e\fG.Y\u0005\u0003E}\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005%\u0001\t\u0005\r\u0011\"\u0003&\u00031yVo]3ESN\\w\fJ3r)\t1\u0013\u0006\u0005\u0002\u001fO%\u0011\u0001f\b\u0002\u0005+:LG\u000fC\u0004+G\u0005\u0005\t\u0019A\u000f\u0002\u0007a$\u0013\u0007\u0003\u0005-\u0001\t\u0005\t\u0015)\u0003\u001e\u0003%yVo]3ESN\\\u0007\u0005\u0003\u0005/\u0001\t\u0005\r\u0011\"\u0003\u001d\u0003)yVo]3NK6|'/\u001f\u0005\ta\u0001\u0011\t\u0019!C\u0005c\u0005qq,^:f\u001b\u0016lwN]=`I\u0015\fHC\u0001\u00143\u0011\u001dQs&!AA\u0002uA\u0001\u0002\u000e\u0001\u0003\u0002\u0003\u0006K!H\u0001\f?V\u001cX-T3n_JL\b\u0005\u0003\u00057\u0001\t\u0005\r\u0011\"\u0003\u001d\u0003-yVo]3PM\u001aDU-\u00199\t\u0011a\u0002!\u00111A\u0005\ne\nqbX;tK>3g\rS3ba~#S-\u001d\u000b\u0003MiBqAK\u001c\u0002\u0002\u0003\u0007Q\u0004\u0003\u0005=\u0001\t\u0005\t\u0015)\u0003\u001e\u00031yVo]3PM\u001aDU-\u00199!\u0011!q\u0004A!a\u0001\n\u0013a\u0012!D0eKN,'/[1mSj,G\r\u0003\u0005A\u0001\t\u0005\r\u0011\"\u0003B\u0003EyF-Z:fe&\fG.\u001b>fI~#S-\u001d\u000b\u0003M\tCqAK \u0002\u0002\u0003\u0007Q\u0004\u0003\u0005E\u0001\t\u0005\t\u0015)\u0003\u001e\u00039yF-Z:fe&\fG.\u001b>fI\u0002B\u0001B\u0012\u0001\u0003\u0002\u0004%IaR\u0001\r?J,\u0007\u000f\\5dCRLwN\\\u000b\u0002\u0011B\u0011a$S\u0005\u0003\u0015~\u00111!\u00138u\u0011!a\u0005A!a\u0001\n\u0013i\u0015\u0001E0sKBd\u0017nY1uS>tw\fJ3r)\t1c\nC\u0004+\u0017\u0006\u0005\t\u0019\u0001%\t\u0011A\u0003!\u0011!Q!\n!\u000bQb\u0018:fa2L7-\u0019;j_:\u0004\u0003\"\u0002*\u0001\t\u0013\u0019\u0016A\u0002\u001fj]&$h\b\u0006\u0004U-^C\u0016L\u0017\t\u0003+\u0002i\u0011A\u0001\u0005\u00067E\u0003\r!\b\u0005\u0006]E\u0003\r!\b\u0005\u0006mE\u0003\r!\b\u0005\u0006}E\u0003\r!\b\u0005\b\rF\u0003\n\u00111\u0001I\u0011\u0015\u0011\u0006\u0001\"\u0003])\r!Vl\u0018\u0005\u0006=n\u0003\r\u0001S\u0001\u0006M2\fwm\u001d\u0005\u0006An\u0003\r\u0001S\u0001\fe\u0016\u0004H.[2bi&|g\u000eC\u0003S\u0001\u0011\u0005!\rF\u0001U\u0011\u0015!\u0007\u0001\"\u0001\u001d\u0003\u001d)8/\u001a#jg.DQA\u001a\u0001\u0005\u0002q\t\u0011\"^:f\u001b\u0016lwN]=\t\u000b!\u0004A\u0011\u0001\u000f\u0002\u0015U\u001cXm\u00144g\u0011\u0016\f\u0007\u000fC\u0003k\u0001\u0011\u0005A$\u0001\u0007eKN,'/[1mSj,G\rC\u0003a\u0001\u0011\u0005q\t\u0003\u0004n\u0001\u0011\u0005AA\\\u0001\u000b[\u0016lwN]=N_\u0012,W#A8\u0011\u0005A\u001cX\"A9\u000b\u0005I$\u0011AB7f[>\u0014\u00180\u0003\u0002uc\nQQ*Z7peflu\u000eZ3\t\u000bY\u0004A\u0011\t2\u0002\u000b\rdwN\\3\t\u000ba\u0004A\u0011I=\u0002\r\u0015\fX/\u00197t)\ti\"\u0010C\u0003|o\u0002\u0007A0A\u0003pi\",'\u000f\u0005\u0002\u001f{&\u0011ap\b\u0002\u0004\u0003:L\bBBA\u0001\u0001\u0011\u0005A$A\u0004jgZ\u000bG.\u001b3\t\r\u0005\u0015\u0001\u0001\"\u0001H\u0003\u0015!x.\u00138u\u0011\u001d\tI\u0001\u0001C!\u0003\u0017\tQb\u001e:ji\u0016,\u0005\u0010^3s]\u0006dGc\u0001\u0014\u0002\u000e!A\u0011qBA\u0004\u0001\u0004\t\t\"A\u0002pkR\u00042!FA\n\u0013\r\t)B\u0006\u0002\r\u001f\nTWm\u0019;PkR\u0004X\u000f\u001e\u0005\b\u00033\u0001A\u0011IA\u000e\u00031\u0011X-\u00193FqR,'O\\1m)\r1\u0013Q\u0004\u0005\t\u0003?\t9\u00021\u0001\u0002\"\u0005\u0011\u0011N\u001c\t\u0004+\u0005\r\u0012bAA\u0013-\tYqJ\u00196fGRLe\u000e];u\u0011\u001d\tI\u0003\u0001C\u0005\u0003W\t1B]3bIJ+7o\u001c7wKR\tA\u0002\u000b\u0004\u0002(\u0005=\u00121\b\t\u0006=\u0005E\u0012QG\u0005\u0004\u0003gy\"A\u0002;ie><8\u000fE\u0002\u0016\u0003oI1!!\u000f\u0017\u0005-Iu*\u0012=dKB$\u0018n\u001c8$\u0005\u0005U\u0002bBA \u0001\u0011\u0005\u0013\u0011I\u0001\ti>\u001cFO]5oOR\u0011\u00111\t\t\u0005\u0003\u000b\nYED\u0002\u001f\u0003\u000fJ1!!\u0013 \u0003\u0019\u0001&/\u001a3fM&!\u0011QJA(\u0005\u0019\u0019FO]5oO*\u0019\u0011\u0011J\u0010\t\u000f\u0005M\u0003\u0001\"\u0011\u0002V\u0005A\u0001.Y:i\u0007>$W\rF\u0001I\u0011\u001d\tI\u0006\u0001C\u0001\u00037\n1\u0002Z3tGJL\u0007\u000f^5p]V\u0011\u00111\t\u0015\u0004\u0001\u0005}\u0003\u0003BA1\u0003Oj!!a\u0019\u000b\u0007\u0005\u0015D!\u0001\u0006b]:|G/\u0019;j_:LA!!\u001b\u0002d\taA)\u001a<fY>\u0004XM]!qS\u001e9\u0011Q\u000e\u0002\t\u0002\u0005=\u0014\u0001D*u_J\fw-\u001a'fm\u0016d\u0007cA+\u0002r\u00191\u0011A\u0001E\u0001\u0003g\u001ab!!\u001d\u0002v\u0005m\u0004c\u0001\u0010\u0002x%\u0019\u0011\u0011P\u0010\u0003\r\u0005s\u0017PU3g!\rq\u0012QP\u0005\u0004\u0003\u007fz\"\u0001D*fe&\fG.\u001b>bE2,\u0007b\u0002*\u0002r\u0011\u0005\u00111\u0011\u000b\u0003\u0003_B!\"a\"\u0002r\t\u0007I\u0011AAE\u0003\u0011quJT#\u0016\u0003QC\u0001\"!$\u0002r\u0001\u0006I\u0001V\u0001\u0006\u001d>sU\t\t\u0005\u000b\u0003#\u000b\tH1A\u0005\u0002\u0005%\u0015!\u0003#J'.{vJ\u0014'Z\u0011!\t)*!\u001d!\u0002\u0013!\u0016A\u0003#J'.{vJ\u0014'ZA!Q\u0011\u0011TA9\u0005\u0004%\t!!#\u0002\u0017\u0011K5kS0P\u001d2KvL\r\u0005\t\u0003;\u000b\t\b)A\u0005)\u0006aA)S*L?>sE*W03A!Q\u0011\u0011UA9\u0005\u0004%\t!!#\u0002\u00175+Uj\u0014*Z?>sE*\u0017\u0005\t\u0003K\u000b\t\b)A\u0005)\u0006aQ*R'P%f{vJ\u0014'ZA!Q\u0011\u0011VA9\u0005\u0004%\t!!#\u0002\u001b5+Uj\u0014*Z?>sE*W03\u0011!\ti+!\u001d!\u0002\u0013!\u0016AD'F\u001b>\u0013\u0016lX(O\u0019f{&\u0007\t\u0005\u000b\u0003c\u000b\tH1A\u0005\u0002\u0005%\u0015aD'F\u001b>\u0013\u0016lX(O\u0019f{6+\u0012*\t\u0011\u0005U\u0016\u0011\u000fQ\u0001\nQ\u000b\u0001#T#N\u001fJKvl\u0014(M3~\u001bVI\u0015\u0011\t\u0015\u0005e\u0016\u0011\u000fb\u0001\n\u0003\tI)A\tN\u000b6{%+W0P\u001d2KvlU#S?JB\u0001\"!0\u0002r\u0001\u0006I\u0001V\u0001\u0013\u001b\u0016kuJU-`\u001f:c\u0015lX*F%~\u0013\u0004\u0005\u0003\u0006\u0002B\u0006E$\u0019!C\u0001\u0003\u0013\u000bq\"T#N\u001fJKv,\u0011(E?\u0012K5k\u0013\u0005\t\u0003\u000b\f\t\b)A\u0005)\u0006\u0001R*R'P%f{\u0016I\u0014#`\t&\u001b6\n\t\u0005\u000b\u0003\u0013\f\tH1A\u0005\u0002\u0005%\u0015!E'F\u001b>\u0013\u0016lX!O\t~#\u0015jU&`e!A\u0011QZA9A\u0003%A+\u0001\nN\u000b6{%+W0B\u001d\u0012{F)S*L?J\u0002\u0003BCAi\u0003c\u0012\r\u0011\"\u0001\u0002\n\u0006\u0019R*R'P%f{\u0016I\u0014#`\t&\u001b6jX*F%\"A\u0011Q[A9A\u0003%A+\u0001\u000bN\u000b6{%+W0B\u001d\u0012{F)S*L?N+%\u000b\t\u0005\u000b\u00033\f\tH1A\u0005\u0002\u0005%\u0015!F'F\u001b>\u0013\u0016lX!O\t~#\u0015jU&`'\u0016\u0013vL\r\u0005\t\u0003;\f\t\b)A\u0005)\u00061R*R'P%f{\u0016I\u0014#`\t&\u001b6jX*F%~\u0013\u0004\u0005\u0003\u0006\u0002b\u0006E$\u0019!C\u0001\u0003\u0013\u000b\u0001b\u0014$G?\"+\u0015\t\u0015\u0005\t\u0003K\f\t\b)A\u0005)\u0006IqJ\u0012$`\u0011\u0016\u000b\u0005\u000b\t\u0005\t\u0003S\f\t\b\"\u0001\u0002l\u0006QaM]8n'R\u0014\u0018N\\4\u0015\u0007Q\u000bi\u000f\u0003\u0005\u0002p\u0006\u001d\b\u0019AA\"\u0003\u0005\u0019\b\u0006BAt\u0003?B\u0001\"!>\u0002r\u0011\u0005\u0011q_\u0001\u0006CB\u0004H.\u001f\u000b\f)\u0006e\u00181`A\u007f\u0003\u007f\u0014\t\u0001\u0003\u0004e\u0003g\u0004\r!\b\u0005\u0007M\u0006M\b\u0019A\u000f\t\r!\f\u0019\u00101\u0001\u001e\u0011\u0019Q\u00171\u001fa\u0001;!1\u0001-a=A\u0002!CC!a=\u0002`!A\u0011Q_A9\t\u0003\u00119\u0001F\u0005U\u0005\u0013\u0011YA!\u0004\u0003\u0010!1AM!\u0002A\u0002uAaA\u001aB\u0003\u0001\u0004i\u0002B\u00026\u0003\u0006\u0001\u0007Q\u0004\u0003\u0005a\u0005\u000b\u0001\n\u00111\u0001IQ\u0011\u0011)!a\u0018\t\u0011\u0005U\u0018\u0011\u000fC\u0001\u0005+!R\u0001\u0016B\f\u00053AaA\u0018B\n\u0001\u0004A\u0005B\u00021\u0003\u0014\u0001\u0007\u0001\n\u000b\u0003\u0003\u0014\u0005}\u0003\u0002CA{\u0003c\"\tAa\b\u0015\u0007Q\u0013\t\u0003\u0003\u0005\u0002 \tu\u0001\u0019AA\u0011Q\u0011\u0011i\"a\u0018\t\u0017\t\u001d\u0012\u0011\u000fb\u0001\n\u0003!!\u0011F\u0001\u0012gR|'/Y4f\u0019\u00164X\r\\\"bG\",WC\u0001B\u0016!\u0019\u0011iCa\u000eU)6\u0011!q\u0006\u0006\u0005\u0005c\u0011\u0019$\u0001\u0006d_:\u001cWO\u001d:f]RT1A!\u000e\u0011\u0003\u0011)H/\u001b7\n\t\te\"q\u0006\u0002\u0012\u0007>t7-\u001e:sK:$\b*Y:i\u001b\u0006\u0004\b\"\u0003B\u001f\u0003c\u0002\u000b\u0011\u0002B\u0016\u0003I\u0019Ho\u001c:bO\u0016dUM^3m\u0007\u0006\u001c\u0007.\u001a\u0011\t\u0013\t\u0005\u0013\u0011\u000fC\u0001\t\t\r\u0013!F4fi\u000e\u000b7\r[3e'R|'/Y4f\u0019\u00164X\r\u001c\u000b\u0004)\n\u0015\u0003b\u0002B$\u0005\u007f\u0001\r\u0001V\u0001\u0006Y\u00164X\r\u001c\u0005\u000b\u0005\u0017\n\t(%A\u0005\n\t5\u0013a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$S'\u0006\u0002\u0003P)\u001a\u0001J!\u0015,\u0005\tM\u0003\u0003\u0002B+\u0005;j!Aa\u0016\u000b\t\te#1L\u0001\nk:\u001c\u0007.Z2lK\u0012T1!!\u001a \u0013\u0011\u0011yFa\u0016\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r\u0003\u0006\u0003d\u0005E\u0014\u0013!C\u0001\u0005\u001b\nq\"\u00199qYf$C-\u001a4bk2$H\u0005\u000e\u0005\u000b\u0003S\t\t(!A\u0005\n\u0005-\u0002"
)
public class StorageLevel implements Externalizable {
   private boolean org$apache$spark$storage$StorageLevel$$_useDisk;
   private boolean org$apache$spark$storage$StorageLevel$$_useMemory;
   private boolean org$apache$spark$storage$StorageLevel$$_useOffHeap;
   private boolean org$apache$spark$storage$StorageLevel$$_deserialized;
   private int org$apache$spark$storage$StorageLevel$$_replication;

   public static int apply$default$4() {
      return StorageLevel$.MODULE$.apply$default$4();
   }

   @DeveloperApi
   public static StorageLevel apply(ObjectInput var0) {
      return StorageLevel$.MODULE$.apply(var0);
   }

   @DeveloperApi
   public static StorageLevel apply(int var0, int var1) {
      return StorageLevel$.MODULE$.apply(var0, var1);
   }

   @DeveloperApi
   public static StorageLevel apply(boolean var0, boolean var1, boolean var2, int var3) {
      return StorageLevel$.MODULE$.apply(var0, var1, var2, var3);
   }

   @DeveloperApi
   public static StorageLevel apply(boolean var0, boolean var1, boolean var2, boolean var3, int var4) {
      return StorageLevel$.MODULE$.apply(var0, var1, var2, var3, var4);
   }

   @DeveloperApi
   public static StorageLevel fromString(String var0) {
      return StorageLevel$.MODULE$.fromString(var0);
   }

   public static StorageLevel OFF_HEAP() {
      return StorageLevel$.MODULE$.OFF_HEAP();
   }

   public static StorageLevel MEMORY_AND_DISK_SER_2() {
      return StorageLevel$.MODULE$.MEMORY_AND_DISK_SER_2();
   }

   public static StorageLevel MEMORY_AND_DISK_SER() {
      return StorageLevel$.MODULE$.MEMORY_AND_DISK_SER();
   }

   public static StorageLevel MEMORY_AND_DISK_2() {
      return StorageLevel$.MODULE$.MEMORY_AND_DISK_2();
   }

   public static StorageLevel MEMORY_AND_DISK() {
      return StorageLevel$.MODULE$.MEMORY_AND_DISK();
   }

   public static StorageLevel MEMORY_ONLY_SER_2() {
      return StorageLevel$.MODULE$.MEMORY_ONLY_SER_2();
   }

   public static StorageLevel MEMORY_ONLY_SER() {
      return StorageLevel$.MODULE$.MEMORY_ONLY_SER();
   }

   public static StorageLevel MEMORY_ONLY_2() {
      return StorageLevel$.MODULE$.MEMORY_ONLY_2();
   }

   public static StorageLevel MEMORY_ONLY() {
      return StorageLevel$.MODULE$.MEMORY_ONLY();
   }

   public static StorageLevel DISK_ONLY_2() {
      return StorageLevel$.MODULE$.DISK_ONLY_2();
   }

   public static StorageLevel DISK_ONLY() {
      return StorageLevel$.MODULE$.DISK_ONLY();
   }

   public static StorageLevel NONE() {
      return StorageLevel$.MODULE$.NONE();
   }

   private boolean org$apache$spark$storage$StorageLevel$$_useDisk() {
      return this.org$apache$spark$storage$StorageLevel$$_useDisk;
   }

   public void org$apache$spark$storage$StorageLevel$$_useDisk_$eq(boolean x$1) {
      this.org$apache$spark$storage$StorageLevel$$_useDisk = x$1;
   }

   private boolean org$apache$spark$storage$StorageLevel$$_useMemory() {
      return this.org$apache$spark$storage$StorageLevel$$_useMemory;
   }

   public void org$apache$spark$storage$StorageLevel$$_useMemory_$eq(boolean x$1) {
      this.org$apache$spark$storage$StorageLevel$$_useMemory = x$1;
   }

   private boolean org$apache$spark$storage$StorageLevel$$_useOffHeap() {
      return this.org$apache$spark$storage$StorageLevel$$_useOffHeap;
   }

   public void org$apache$spark$storage$StorageLevel$$_useOffHeap_$eq(boolean x$1) {
      this.org$apache$spark$storage$StorageLevel$$_useOffHeap = x$1;
   }

   private boolean org$apache$spark$storage$StorageLevel$$_deserialized() {
      return this.org$apache$spark$storage$StorageLevel$$_deserialized;
   }

   public void org$apache$spark$storage$StorageLevel$$_deserialized_$eq(boolean x$1) {
      this.org$apache$spark$storage$StorageLevel$$_deserialized = x$1;
   }

   public int org$apache$spark$storage$StorageLevel$$_replication() {
      return this.org$apache$spark$storage$StorageLevel$$_replication;
   }

   public void org$apache$spark$storage$StorageLevel$$_replication_$eq(int x$1) {
      this.org$apache$spark$storage$StorageLevel$$_replication = x$1;
   }

   public boolean useDisk() {
      return this.org$apache$spark$storage$StorageLevel$$_useDisk();
   }

   public boolean useMemory() {
      return this.org$apache$spark$storage$StorageLevel$$_useMemory();
   }

   public boolean useOffHeap() {
      return this.org$apache$spark$storage$StorageLevel$$_useOffHeap();
   }

   public boolean deserialized() {
      return this.org$apache$spark$storage$StorageLevel$$_deserialized();
   }

   public int replication() {
      return this.org$apache$spark$storage$StorageLevel$$_replication();
   }

   public MemoryMode memoryMode() {
      return this.useOffHeap() ? MemoryMode.OFF_HEAP : MemoryMode.ON_HEAP;
   }

   public StorageLevel clone() {
      return new StorageLevel(this.useDisk(), this.useMemory(), this.useOffHeap(), this.deserialized(), this.replication());
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof StorageLevel) {
         StorageLevel var3 = (StorageLevel)other;
         var4 = var3.useDisk() == this.useDisk() && var3.useMemory() == this.useMemory() && var3.useOffHeap() == this.useOffHeap() && var3.deserialized() == this.deserialized() && var3.replication() == this.replication();
      } else {
         var4 = false;
      }

      return var4;
   }

   public boolean isValid() {
      return (this.useMemory() || this.useDisk()) && this.replication() > 0;
   }

   public int toInt() {
      int ret = 0;
      if (this.org$apache$spark$storage$StorageLevel$$_useDisk()) {
         ret |= 8;
      }

      if (this.org$apache$spark$storage$StorageLevel$$_useMemory()) {
         ret |= 4;
      }

      if (this.org$apache$spark$storage$StorageLevel$$_useOffHeap()) {
         ret |= 2;
      }

      if (this.org$apache$spark$storage$StorageLevel$$_deserialized()) {
         ret |= 1;
      }

      return ret;
   }

   public void writeExternal(ObjectOutput out) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, out) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageLevel $outer;
         private final ObjectOutput out$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            this.out$1.writeByte(this.$outer.toInt());
            this.out$1.writeByte(this.$outer.org$apache$spark$storage$StorageLevel$$_replication());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.out$1 = out$1;
            }
         }
      });
   }

   public void readExternal(ObjectInput in) {
      Utils$.MODULE$.tryOrIOException(new Serializable(this, in) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageLevel $outer;
         private final ObjectInput in$1;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            byte flags = this.in$1.readByte();
            this.$outer.org$apache$spark$storage$StorageLevel$$_useDisk_$eq((flags & 8) != 0);
            this.$outer.org$apache$spark$storage$StorageLevel$$_useMemory_$eq((flags & 4) != 0);
            this.$outer.org$apache$spark$storage$StorageLevel$$_useOffHeap_$eq((flags & 2) != 0);
            this.$outer.org$apache$spark$storage$StorageLevel$$_deserialized_$eq((flags & 1) != 0);
            this.$outer.org$apache$spark$storage$StorageLevel$$_replication_$eq(this.in$1.readByte());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.in$1 = in$1;
            }
         }
      });
   }

   private Object readResolve() throws IOException {
      return StorageLevel$.MODULE$.getCachedStorageLevel(this);
   }

   public String toString() {
      String disk = this.useDisk() ? "disk" : "";
      String memory = this.useMemory() ? "memory" : "";
      String heap = this.useOffHeap() ? "offheap" : "";
      String deserialize = this.deserialized() ? "deserialized" : "";
      Seq output = (Seq)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{disk, memory, heap, deserialize, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " replicas"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.replication())}))})))).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$1) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$1))).nonEmpty();
         }
      });
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"StorageLevel(", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{output.mkString(", ")}));
   }

   public int hashCode() {
      return this.toInt() * 41 + this.replication();
   }

   public String description() {
      String result = "";
      result = (new StringBuilder()).append((Object)result).append((Object)(this.useDisk() ? "Disk " : "")).toString();
      if (this.useMemory()) {
         result = (new StringBuilder()).append((Object)result).append((Object)(this.useOffHeap() ? "Memory (off heap) " : "Memory ")).toString();
      }

      result = (new StringBuilder()).append((Object)result).append((Object)(this.deserialized() ? "Deserialized " : "Serialized ")).toString();
      result = (new StringBuilder()).append((Object)result).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "x Replicated"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.replication())}))).toString();
      return result;
   }

   public StorageLevel(boolean _useDisk, boolean _useMemory, boolean _useOffHeap, boolean _deserialized, int _replication) {
      this.org$apache$spark$storage$StorageLevel$$_useDisk = _useDisk;
      this.org$apache$spark$storage$StorageLevel$$_useMemory = _useMemory;
      this.org$apache$spark$storage$StorageLevel$$_useOffHeap = _useOffHeap;
      this.org$apache$spark$storage$StorageLevel$$_deserialized = _deserialized;
      this.org$apache$spark$storage$StorageLevel$$_replication = _replication;
      super();
      Predef$.MODULE$.assert(this.replication() < 40, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Replication restricted to be less than 40 for calculating hash codes";
         }
      });
      if (this.useOffHeap()) {
         Predef$.MODULE$.require(!this.deserialized(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Off-heap storage level does not support deserialized storage";
            }
         });
      }

   }

   public StorageLevel(int flags, int replication) {
      this((flags & 8) != 0, (flags & 4) != 0, (flags & 2) != 0, (flags & 1) != 0, replication);
   }

   public StorageLevel() {
      this(false, true, false, false, StorageLevel$.MODULE$.org$apache$spark$storage$StorageLevel$$$lessinit$greater$default$5());
   }
}
