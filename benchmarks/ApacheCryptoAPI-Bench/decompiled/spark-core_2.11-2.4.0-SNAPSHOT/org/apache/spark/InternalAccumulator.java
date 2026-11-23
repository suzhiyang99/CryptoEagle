package org.apache.spark;

import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=tAB\u0001\u0003\u0011\u0003\u0011\u0001\"A\nJ]R,'O\\1m\u0003\u000e\u001cW/\\;mCR|'O\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h!\tI!\"D\u0001\u0003\r\u0019Y!\u0001#\u0001\u0003\u0019\t\u0019\u0012J\u001c;fe:\fG.Q2dk6,H.\u0019;peN\u0011!\"\u0004\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\t\u000bQQA\u0011\u0001\f\u0002\rqJg.\u001b;?\u0007\u0001!\u0012\u0001\u0003\u0005\b1)\u0011\r\u0011\"\u0001\u001a\u00039iU\t\u0016*J\u0007N{\u0006KU#G\u0013b+\u0012A\u0007\t\u00037\u0001j\u0011\u0001\b\u0006\u0003;y\tA\u0001\\1oO*\tq$\u0001\u0003kCZ\f\u0017BA\u0011\u001d\u0005\u0019\u0019FO]5oO\"11E\u0003Q\u0001\ni\tq\"T#U%&\u001b5k\u0018)S\u000b\u001aK\u0005\f\t\u0005\bK)\u0011\r\u0011\"\u0001\u001a\u0003m\u0019\u0006*\u0016$G\u0019\u0016{&+R!E?6+EKU%D'~\u0003&+\u0012$J1\"1qE\u0003Q\u0001\ni\tAd\u0015%V\r\u001acUi\u0018*F\u0003\u0012{V*\u0012+S\u0013\u000e\u001bv\f\u0015*F\r&C\u0006\u0005C\u0004*\u0015\t\u0007I\u0011A\r\u00029MCUK\u0012$M\u000b~;&+\u0013+F?6+EKU%D'~\u0003&+\u0012$J1\"11F\u0003Q\u0001\ni\tQd\u0015%V\r\u001acUiX,S\u0013R+u,T#U%&\u001b5k\u0018)S\u000b\u001aK\u0005\f\t\u0005\b[)\u0011\r\u0011\"\u0001\u001a\u0003UyU\u000b\u0016)V)~kU\t\u0016*J\u0007N{\u0006KU#G\u0013bCaa\f\u0006!\u0002\u0013Q\u0012AF(V)B+FkX'F)JK5iU0Q%\u00163\u0015\n\u0017\u0011\t\u000fER!\u0019!C\u00013\u0005!\u0012J\u0014)V)~kU\t\u0016*J\u0007N{\u0006KU#G\u0013bCaa\r\u0006!\u0002\u0013Q\u0012!F%O!V#v,T#U%&\u001b5k\u0018)S\u000b\u001aK\u0005\f\t\u0005\bk)\u0011\r\u0011\"\u0001\u001a\u0003e)\u0005,R\"V)>\u0013v\fR#T\u000bJK\u0015\tT%[\u000b~#\u0016*T#\t\r]R\u0001\u0015!\u0003\u001b\u0003i)\u0005,R\"V)>\u0013v\fR#T\u000bJK\u0015\tT%[\u000b~#\u0016*T#!\u0011\u001dI$B1A\u0005\u0002e\tQ$\u0012-F\u0007V#vJU0E\u000bN+%+S!M\u0013j+ul\u0011)V?RKU*\u0012\u0005\u0007w)\u0001\u000b\u0011\u0002\u000e\u0002=\u0015CViQ+U\u001fJ{F)R*F%&\u000bE*\u0013.F?\u000e\u0003Vk\u0018+J\u001b\u0016\u0003\u0003bB\u001f\u000b\u0005\u0004%\t!G\u0001\u0012\u000bb+5)\u0016+P%~\u0013VKT0U\u00136+\u0005BB \u000bA\u0003%!$\u0001\nF1\u0016\u001bU\u000bV(S?J+fj\u0018+J\u001b\u0016\u0003\u0003bB!\u000b\u0005\u0004%\t!G\u0001\u0012\u000bb+5)\u0016+P%~\u001b\u0005+V0U\u00136+\u0005BB\"\u000bA\u0003%!$\u0001\nF1\u0016\u001bU\u000bV(S?\u000e\u0003Vk\u0018+J\u001b\u0016\u0003\u0003bB#\u000b\u0005\u0004%\t!G\u0001\f%\u0016\u001bV\u000b\u0014+`'&SV\t\u0003\u0004H\u0015\u0001\u0006IAG\u0001\r%\u0016\u001bV\u000b\u0014+`'&SV\t\t\u0005\b\u0013*\u0011\r\u0011\"\u0001\u001a\u0003-Qe+T0H\u0007~#\u0016*T#\t\r-S\u0001\u0015!\u0003\u001b\u00031Qe+T0H\u0007~#\u0016*T#!\u0011\u001di%B1A\u0005\u0002e\t\u0011DU#T+2#vlU#S\u0013\u0006c\u0015JW!U\u0013>su\fV%N\u000b\"1qJ\u0003Q\u0001\ni\t!DU#T+2#vlU#S\u0013\u0006c\u0015JW!U\u0013>su\fV%N\u000b\u0002Bq!\u0015\u0006C\u0002\u0013\u0005\u0011$\u0001\u000bN\u000b6{%+W0C3R+5kX*Q\u00132cU\t\u0012\u0005\u0007'*\u0001\u000b\u0011\u0002\u000e\u0002+5+Uj\u0014*Z?\nKF+R*`'BKE\nT#EA!9QK\u0003b\u0001\n\u0003I\u0012A\u0005#J'.{&)\u0017+F'~\u001b\u0006+\u0013'M\u000b\u0012Caa\u0016\u0006!\u0002\u0013Q\u0012a\u0005#J'.{&)\u0017+F'~\u001b\u0006+\u0013'M\u000b\u0012\u0003\u0003bB-\u000b\u0005\u0004%\t!G\u0001\u0016!\u0016\u000b5jX#Y\u000b\u000e+F+S(O?6+Uj\u0014*Z\u0011\u0019Y&\u0002)A\u00055\u00051\u0002+R!L?\u0016CViQ+U\u0013>su,T#N\u001fJK\u0006\u0005C\u0004^\u0015\t\u0007I\u0011A\r\u0002-U\u0003F)\u0011+F\t~\u0013EjT\"L?N#\u0016\tV+T\u000bNCaa\u0018\u0006!\u0002\u0013Q\u0012aF+Q\t\u0006#V\tR0C\u0019>\u001b5jX*U\u0003R+6+R*!\u0011\u001d\t'B1A\u0005\u0002e\t!\u0002V#T)~\u000b5iQ+N\u0011\u0019\u0019'\u0002)A\u00055\u0005YA+R*U?\u0006\u001b5)V'!\u000f\u0015)'\u0002#\u0001g\u0003-\u0019\b.\u001e4gY\u0016\u0014V-\u00193\u0011\u0005\u001dDW\"\u0001\u0006\u0007\u000b%T\u0001\u0012\u00016\u0003\u0017MDWO\u001a4mKJ+\u0017\rZ\n\u0003Q6AQ\u0001\u00065\u0005\u00021$\u0012A\u001a\u0005\b]\"\u0014\r\u0011\"\u0001\u001a\u0003U\u0011V)T(U\u000b~\u0013EjT\"L'~3U\tV\"I\u000b\u0012Ca\u0001\u001d5!\u0002\u0013Q\u0012A\u0006*F\u001b>#Vi\u0018\"M\u001f\u000e[5k\u0018$F)\u000eCU\t\u0012\u0011\t\u000fID'\u0019!C\u00013\u0005!BjT\"B\u0019~\u0013EjT\"L'~3U\tV\"I\u000b\u0012Ca\u0001\u001e5!\u0002\u0013Q\u0012!\u0006'P\u0007\u0006cuL\u0011'P\u0007.\u001bvLR#U\u0007\"+E\t\t\u0005\bm\"\u0014\r\u0011\"\u0001\u001a\u0003E\u0011V)T(U\u000b~\u0013\u0015\fV#T?J+\u0015\t\u0012\u0005\u0007q\"\u0004\u000b\u0011\u0002\u000e\u0002%I+Uj\u0014+F?\nKF+R*`%\u0016\u000bE\t\t\u0005\bu\"\u0014\r\u0011\"\u0001\u001a\u0003e\u0011V)T(U\u000b~\u0013\u0015\fV#T?J+\u0015\tR0U\u001f~#\u0015jU&\t\rqD\u0007\u0015!\u0003\u001b\u0003i\u0011V)T(U\u000b~\u0013\u0015\fV#T?J+\u0015\tR0U\u001f~#\u0015jU&!\u0011\u001dq\bN1A\u0005\u0002e\t\u0001\u0003T(D\u00032{&)\u0017+F'~\u0013V)\u0011#\t\u000f\u0005\u0005\u0001\u000e)A\u00055\u0005\tBjT\"B\u0019~\u0013\u0015\fV#T?J+\u0015\t\u0012\u0011\t\u0011\u0005\u0015\u0001N1A\u0005\u0002e\tqBR#U\u0007\"{v+Q%U?RKU*\u0012\u0005\b\u0003\u0013A\u0007\u0015!\u0003\u001b\u0003A1U\tV\"I?^\u000b\u0015\nV0U\u00136+\u0005\u0005\u0003\u0005\u0002\u000e!\u0014\r\u0011\"\u0001\u001a\u00031\u0011ViQ(S\tN{&+R!E\u0011\u001d\t\t\u0002\u001bQ\u0001\ni\tQBU#D\u001fJ#5k\u0018*F\u0003\u0012\u0003saBA\u000b\u0015!\u0005\u0011qC\u0001\rg\",hM\u001a7f/JLG/\u001a\t\u0004O\u0006eaaBA\u000e\u0015!\u0005\u0011Q\u0004\u0002\rg\",hM\u001a7f/JLG/Z\n\u0004\u00033i\u0001b\u0002\u000b\u0002\u001a\u0011\u0005\u0011\u0011\u0005\u000b\u0003\u0003/A\u0011\"!\n\u0002\u001a\t\u0007I\u0011A\r\u0002\u001b\tKF+R*`/JKE\u000bV#O\u0011!\tI#!\u0007!\u0002\u0013Q\u0012A\u0004\"Z)\u0016\u001bvl\u0016*J)R+e\n\t\u0005\n\u0003[\tIB1A\u0005\u0002e\tqBU#D\u001fJ#5kX,S\u0013R#VI\u0014\u0005\t\u0003c\tI\u0002)A\u00055\u0005\u0001\"+R\"P%\u0012\u001bvl\u0016*J)R+e\n\t\u0005\n\u0003k\tIB1A\u0005\u0002e\t!b\u0016*J)\u0016{F+S'F\u0011!\tI$!\u0007!\u0002\u0013Q\u0012aC,S\u0013R+u\fV%N\u000b\u0002:q!!\u0010\u000b\u0011\u0003\ty$\u0001\u0004pkR\u0004X\u000f\u001e\t\u0004O\u0006\u0005caBA\"\u0015!\u0005\u0011Q\t\u0002\u0007_V$\b/\u001e;\u0014\u0007\u0005\u0005S\u0002C\u0004\u0015\u0003\u0003\"\t!!\u0013\u0015\u0005\u0005}\u0002\"CA\u0013\u0003\u0003\u0012\r\u0011\"\u0001\u001a\u0011!\tI#!\u0011!\u0002\u0013Q\u0002\"CA\u0017\u0003\u0003\u0012\r\u0011\"\u0001\u001a\u0011!\t\t$!\u0011!\u0002\u0013QraBA+\u0015!\u0005\u0011qK\u0001\u0006S:\u0004X\u000f\u001e\t\u0004O\u0006ecaBA.\u0015!\u0005\u0011Q\f\u0002\u0006S:\u0004X\u000f^\n\u0004\u00033j\u0001b\u0002\u000b\u0002Z\u0011\u0005\u0011\u0011\r\u000b\u0003\u0003/B\u0011\"!\u001a\u0002Z\t\u0007I\u0011A\r\u0002\u0015\tKF+R*`%\u0016\u000bE\t\u0003\u0005\u0002j\u0005e\u0003\u0015!\u0003\u001b\u0003-\u0011\u0015\fV#T?J+\u0015\t\u0012\u0011\t\u0013\u00055\u0011\u0011\fb\u0001\n\u0003I\u0002\u0002CA\t\u00033\u0002\u000b\u0011\u0002\u000e"
)
public final class InternalAccumulator {
   public static String TEST_ACCUM() {
      return InternalAccumulator$.MODULE$.TEST_ACCUM();
   }

   public static String UPDATED_BLOCK_STATUSES() {
      return InternalAccumulator$.MODULE$.UPDATED_BLOCK_STATUSES();
   }

   public static String PEAK_EXECUTION_MEMORY() {
      return InternalAccumulator$.MODULE$.PEAK_EXECUTION_MEMORY();
   }

   public static String DISK_BYTES_SPILLED() {
      return InternalAccumulator$.MODULE$.DISK_BYTES_SPILLED();
   }

   public static String MEMORY_BYTES_SPILLED() {
      return InternalAccumulator$.MODULE$.MEMORY_BYTES_SPILLED();
   }

   public static String RESULT_SERIALIZATION_TIME() {
      return InternalAccumulator$.MODULE$.RESULT_SERIALIZATION_TIME();
   }

   public static String JVM_GC_TIME() {
      return InternalAccumulator$.MODULE$.JVM_GC_TIME();
   }

   public static String RESULT_SIZE() {
      return InternalAccumulator$.MODULE$.RESULT_SIZE();
   }

   public static String EXECUTOR_CPU_TIME() {
      return InternalAccumulator$.MODULE$.EXECUTOR_CPU_TIME();
   }

   public static String EXECUTOR_RUN_TIME() {
      return InternalAccumulator$.MODULE$.EXECUTOR_RUN_TIME();
   }

   public static String EXECUTOR_DESERIALIZE_CPU_TIME() {
      return InternalAccumulator$.MODULE$.EXECUTOR_DESERIALIZE_CPU_TIME();
   }

   public static String EXECUTOR_DESERIALIZE_TIME() {
      return InternalAccumulator$.MODULE$.EXECUTOR_DESERIALIZE_TIME();
   }

   public static String INPUT_METRICS_PREFIX() {
      return InternalAccumulator$.MODULE$.INPUT_METRICS_PREFIX();
   }

   public static String OUTPUT_METRICS_PREFIX() {
      return InternalAccumulator$.MODULE$.OUTPUT_METRICS_PREFIX();
   }

   public static String SHUFFLE_WRITE_METRICS_PREFIX() {
      return InternalAccumulator$.MODULE$.SHUFFLE_WRITE_METRICS_PREFIX();
   }

   public static String SHUFFLE_READ_METRICS_PREFIX() {
      return InternalAccumulator$.MODULE$.SHUFFLE_READ_METRICS_PREFIX();
   }

   public static String METRICS_PREFIX() {
      return InternalAccumulator$.MODULE$.METRICS_PREFIX();
   }

   public static class shuffleRead$ {
      public static final shuffleRead$ MODULE$;
      private final String REMOTE_BLOCKS_FETCHED;
      private final String LOCAL_BLOCKS_FETCHED;
      private final String REMOTE_BYTES_READ;
      private final String REMOTE_BYTES_READ_TO_DISK;
      private final String LOCAL_BYTES_READ;
      private final String FETCH_WAIT_TIME;
      private final String RECORDS_READ;

      static {
         new shuffleRead$();
      }

      public String REMOTE_BLOCKS_FETCHED() {
         return this.REMOTE_BLOCKS_FETCHED;
      }

      public String LOCAL_BLOCKS_FETCHED() {
         return this.LOCAL_BLOCKS_FETCHED;
      }

      public String REMOTE_BYTES_READ() {
         return this.REMOTE_BYTES_READ;
      }

      public String REMOTE_BYTES_READ_TO_DISK() {
         return this.REMOTE_BYTES_READ_TO_DISK;
      }

      public String LOCAL_BYTES_READ() {
         return this.LOCAL_BYTES_READ;
      }

      public String FETCH_WAIT_TIME() {
         return this.FETCH_WAIT_TIME;
      }

      public String RECORDS_READ() {
         return this.RECORDS_READ;
      }

      public shuffleRead$() {
         MODULE$ = this;
         this.REMOTE_BLOCKS_FETCHED = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_READ_METRICS_PREFIX()).append((Object)"remoteBlocksFetched").toString();
         this.LOCAL_BLOCKS_FETCHED = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_READ_METRICS_PREFIX()).append((Object)"localBlocksFetched").toString();
         this.REMOTE_BYTES_READ = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_READ_METRICS_PREFIX()).append((Object)"remoteBytesRead").toString();
         this.REMOTE_BYTES_READ_TO_DISK = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_READ_METRICS_PREFIX()).append((Object)"remoteBytesReadToDisk").toString();
         this.LOCAL_BYTES_READ = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_READ_METRICS_PREFIX()).append((Object)"localBytesRead").toString();
         this.FETCH_WAIT_TIME = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_READ_METRICS_PREFIX()).append((Object)"fetchWaitTime").toString();
         this.RECORDS_READ = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_READ_METRICS_PREFIX()).append((Object)"recordsRead").toString();
      }
   }

   public static class shuffleWrite$ {
      public static final shuffleWrite$ MODULE$;
      private final String BYTES_WRITTEN;
      private final String RECORDS_WRITTEN;
      private final String WRITE_TIME;

      static {
         new shuffleWrite$();
      }

      public String BYTES_WRITTEN() {
         return this.BYTES_WRITTEN;
      }

      public String RECORDS_WRITTEN() {
         return this.RECORDS_WRITTEN;
      }

      public String WRITE_TIME() {
         return this.WRITE_TIME;
      }

      public shuffleWrite$() {
         MODULE$ = this;
         this.BYTES_WRITTEN = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_WRITE_METRICS_PREFIX()).append((Object)"bytesWritten").toString();
         this.RECORDS_WRITTEN = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_WRITE_METRICS_PREFIX()).append((Object)"recordsWritten").toString();
         this.WRITE_TIME = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.SHUFFLE_WRITE_METRICS_PREFIX()).append((Object)"writeTime").toString();
      }
   }

   public static class output$ {
      public static final output$ MODULE$;
      private final String BYTES_WRITTEN;
      private final String RECORDS_WRITTEN;

      static {
         new output$();
      }

      public String BYTES_WRITTEN() {
         return this.BYTES_WRITTEN;
      }

      public String RECORDS_WRITTEN() {
         return this.RECORDS_WRITTEN;
      }

      public output$() {
         MODULE$ = this;
         this.BYTES_WRITTEN = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.OUTPUT_METRICS_PREFIX()).append((Object)"bytesWritten").toString();
         this.RECORDS_WRITTEN = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.OUTPUT_METRICS_PREFIX()).append((Object)"recordsWritten").toString();
      }
   }

   public static class input$ {
      public static final input$ MODULE$;
      private final String BYTES_READ;
      private final String RECORDS_READ;

      static {
         new input$();
      }

      public String BYTES_READ() {
         return this.BYTES_READ;
      }

      public String RECORDS_READ() {
         return this.RECORDS_READ;
      }

      public input$() {
         MODULE$ = this;
         this.BYTES_READ = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.INPUT_METRICS_PREFIX()).append((Object)"bytesRead").toString();
         this.RECORDS_READ = (new StringBuilder()).append((Object)InternalAccumulator$.MODULE$.INPUT_METRICS_PREFIX()).append((Object)"recordsRead").toString();
      }
   }
}
