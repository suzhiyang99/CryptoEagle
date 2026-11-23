package org.apache.spark.ui.jobs;

import java.util.Date;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.api.v1.StageData;
import org.apache.spark.ui.PagedDataSource;
import org.apache.spark.ui.UIUtils$;
import org.apache.spark.util.Utils$;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.package$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Set;
import scala.math.Ordering;
import scala.math.Ordering$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ea!B\u0001\u0003\u0001\u0011a!aD*uC\u001e,G)\u0019;b'>,(oY3\u000b\u0005\r!\u0011\u0001\u00026pENT!!\u0002\u0004\u0002\u0005UL'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0014\u0005\u0001i\u0001c\u0001\b\u0010#5\tA!\u0003\u0002\u0011\t\ty\u0001+Y4fI\u0012\u000bG/Y*pkJ\u001cW\r\u0005\u0002\u0013'5\t!!\u0003\u0002\u0015\u0005\t\t2\u000b^1hKR\u000b'\r\\3S_^$\u0015\r^1\t\u0011Y\u0001!\u0011!Q\u0001\na\tQa\u001d;pe\u0016\u001c\u0001\u0001\u0005\u0002\u001a95\t!D\u0003\u0002\u001c\r\u000511\u000f^1ukNL!!\b\u000e\u0003\u001d\u0005\u0003\bo\u0015;biV\u001c8\u000b^8sK\"Aq\u0004\u0001B\u0001B\u0003%\u0001%\u0001\u0004ti\u0006<Wm\u001d\t\u0004C-rcB\u0001\u0012)\u001d\t\u0019c%D\u0001%\u0015\t)s#\u0001\u0004=e>|GOP\u0005\u0002O\u0005)1oY1mC&\u0011\u0011FK\u0001\ba\u0006\u001c7.Y4f\u0015\u00059\u0013B\u0001\u0017.\u0005\r\u0019V-\u001d\u0006\u0003S)\u0002\"a\f\u001b\u000e\u0003AR!!\r\u001a\u0002\u0005Y\f$BA\u001a\u001b\u0003\r\t\u0007/[\u0005\u0003kA\u0012\u0011b\u0015;bO\u0016$\u0015\r^1\t\u0011]\u0002!\u0011!Q\u0001\na\n1bY;se\u0016tG\u000fV5nKB\u0011\u0011HO\u0007\u0002U%\u00111H\u000b\u0002\u0005\u0019>tw\rC\u0005>\u0001\t\u0005\t\u0015!\u0003?\u0003\u0006A\u0001/Y4f'&TX\r\u0005\u0002:\u007f%\u0011\u0001I\u000b\u0002\u0004\u0013:$\u0018BA\u001f\u0010\u0011!\u0019\u0005A!A!\u0002\u0013!\u0015AC:peR\u001cu\u000e\\;n]B\u0011Q\t\u0013\b\u0003s\u0019K!a\u0012\u0016\u0002\rA\u0013X\rZ3g\u0013\tI%J\u0001\u0004TiJLgn\u001a\u0006\u0003\u000f*B\u0001\u0002\u0014\u0001\u0003\u0002\u0003\u0006I!T\u0001\u0005I\u0016\u001c8\r\u0005\u0002:\u001d&\u0011qJ\u000b\u0002\b\u0005>|G.Z1o\u0011\u0015\t\u0006\u0001\"\u0001S\u0003\u0019a\u0014N\\5u}Q91\u000bV+W/bK\u0006C\u0001\n\u0001\u0011\u00151\u0002\u000b1\u0001\u0019\u0011\u0015y\u0002\u000b1\u0001!\u0011\u00159\u0004\u000b1\u00019\u0011\u0015i\u0004\u000b1\u0001?\u0011\u0015\u0019\u0005\u000b1\u0001E\u0011\u0015a\u0005\u000b1\u0001N\u0011\u001dY\u0006A1A\u0005\nq\u000bA\u0001Z1uCV\tQ\fE\u0002_CFi\u0011a\u0018\u0006\u0003A*\n!bY8mY\u0016\u001cG/[8o\u0013\tas\f\u0003\u0004d\u0001\u0001\u0006I!X\u0001\u0006I\u0006$\u0018\r\t\u0005\nK\u0002\u0001\r\u00111A\u0005\n\u0019\fqbX:mS\u000e,Gm\u0015;bO\u0016LEm]\u000b\u0002OB\u0019Q\t\u001b \n\u0005%T%aA*fi\"I1\u000e\u0001a\u0001\u0002\u0004%I\u0001\\\u0001\u0014?Nd\u0017nY3e'R\fw-Z%eg~#S-\u001d\u000b\u0003[B\u0004\"!\u000f8\n\u0005=T#\u0001B+oSRDq!\u001d6\u0002\u0002\u0003\u0007q-A\u0002yIEBaa\u001d\u0001!B\u00139\u0017\u0001E0tY&\u001cW\rZ*uC\u001e,\u0017\nZ:!\u0011\u0015)\b\u0001\"\u0011w\u0003!!\u0017\r^1TSj,W#\u0001 \t\u000ba\u0004A\u0011I=\u0002\u0013Md\u0017nY3ECR\fGc\u0001>|{B\u0019\u0011eK\t\t\u000bq<\b\u0019\u0001 \u0002\t\u0019\u0014x.\u001c\u0005\u0006}^\u0004\rAP\u0001\u0003i>Dq!!\u0001\u0001\t\u0013\t\u0019!\u0001\u0005ti\u0006<WMU8x)\r\t\u0012Q\u0001\u0005\u0007\u0003\u000fy\b\u0019\u0001\u0018\u0002\u0013M$\u0018mZ3ECR\f\u0007bBA\u0006\u0001\u0011%\u0011QB\u0001\t_J$WM]5oOR1\u0011qBA\u000b\u0003/\u0001B!IA\t#%\u0019\u00111C\u0017\u0003\u0011=\u0013H-\u001a:j]\u001eDaaQA\u0005\u0001\u0004!\u0005B\u0002'\u0002\n\u0001\u0007Q\n"
)
public class StageDataSource extends PagedDataSource<StageTableRowData> {
   public final long org$apache$spark$ui$jobs$StageDataSource$$currentTime;
   private final Seq<StageTableRowData> data;
   private Set<Object> _slicedStageIds;

   private Seq<StageTableRowData> data() {
      return this.data;
   }

   private Set<Object> _slicedStageIds() {
      return this._slicedStageIds;
   }

   private void _slicedStageIds_$eq(Set<Object> x$1) {
      this._slicedStageIds = x$1;
   }

   public int dataSize() {
      return this.data().size();
   }

   public Seq<StageTableRowData> sliceData(int from, int to) {
      Seq r = (Seq)this.data().slice(from, to);
      this._slicedStageIds_$eq(((TraversableOnce)r.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(StageTableRowData x$10) {
            return x$10.stageId();
         }
      }, Seq$.MODULE$.canBuildFrom())).toSet());
      return r;
   }

   public StageTableRowData org$apache$spark$ui$jobs$StageDataSource$$stageRow(StageData stageData) {
      String description = (String)stageData.description().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      Option var4 = stageData.submissionTime();
      String formattedSubmissionTime;
      if (var4 instanceof Some) {
         Some var5 = (Some)var4;
         Date t = (Date)var5.x();
         formattedSubmissionTime = UIUtils$.MODULE$.formatDate(t);
      } else {
         if (!None$.MODULE$.equals(var4)) {
            throw new MatchError(var4);
         }

         formattedSubmissionTime = "Unknown";
      }

      long finishTime = BoxesRunTime.unboxToLong(stageData.completionTime().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(Date x$11) {
            return x$11.getTime();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StageDataSource $outer;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.$outer.org$apache$spark$ui$jobs$StageDataSource$$currentTime;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      Option duration = stageData.firstTaskLaunchedTime().map(new Serializable(this, finishTime) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StageDataSource $outer;
         private final long finishTime$1;

         public final long apply(Date date) {
            long time = date.getTime();
            long var10000;
            if (this.finishTime$1 > time) {
               var10000 = this.finishTime$1 - time;
            } else {
               None$ var4 = None$.MODULE$;
               var10000 = this.$outer.org$apache$spark$ui$jobs$StageDataSource$$currentTime - time;
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.finishTime$1 = finishTime$1;
            }
         }
      });
      String formattedDuration = (String)duration.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(long d) {
            return UIUtils$.MODULE$.formatDuration(d);
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Unknown";
         }
      });
      long inputRead = stageData.inputBytes();
      String inputReadWithUnit = inputRead > 0L ? Utils$.MODULE$.bytesToString(inputRead) : "";
      long outputWrite = stageData.outputBytes();
      String outputWriteWithUnit = outputWrite > 0L ? Utils$.MODULE$.bytesToString(outputWrite) : "";
      long shuffleRead = stageData.shuffleReadBytes();
      String shuffleReadWithUnit = shuffleRead > 0L ? Utils$.MODULE$.bytesToString(shuffleRead) : "";
      long shuffleWrite = stageData.shuffleWriteBytes();
      String shuffleWriteWithUnit = shuffleWrite > 0L ? Utils$.MODULE$.bytesToString(shuffleWrite) : "";
      return new StageTableRowData(stageData, new Some(stageData), stageData.stageId(), stageData.attemptId(), stageData.schedulingPool(), stageData.description(), (Date)stageData.submissionTime().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Date apply() {
            return new Date(0L);
         }
      }), formattedSubmissionTime, BoxesRunTime.unboxToLong(duration.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return -1L;
         }
      })), formattedDuration, inputRead, inputReadWithUnit, outputWrite, outputWriteWithUnit, shuffleRead, shuffleReadWithUnit, shuffleWrite, shuffleWriteWithUnit);
   }

   private Ordering<StageTableRowData> ordering(String sortColumn, boolean desc) {
      Ordering ordering;
      if ("Stage Id".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final int apply(StageTableRowData x$12) {
               return x$12.stageId();
            }
         }, Ordering.Int$.MODULE$);
      } else if ("Pool Name".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(StageTableRowData x$13) {
               return x$13.schedulingPool();
            }
         }, Ordering.String$.MODULE$);
      } else if ("Description".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Tuple2<Option<String>, String> apply(StageTableRowData x) {
               return new Tuple2<Option<String>, String>(x.descriptionOption(), x.stage().name());
            }
         }, Ordering$.MODULE$.Tuple2(Ordering$.MODULE$.Option(Ordering.String$.MODULE$), Ordering.String$.MODULE$));
      } else if ("Submitted".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Date apply(StageTableRowData x$14) {
               return x$14.submissionTime();
            }
         }, Ordering$.MODULE$.ordered(Predef$.MODULE$.$conforms()));
      } else if ("Duration".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(StageTableRowData x$15) {
               return x$15.duration();
            }
         }, Ordering.Long$.MODULE$);
      } else if ("Input".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(StageTableRowData x$16) {
               return x$16.inputRead();
            }
         }, Ordering.Long$.MODULE$);
      } else if ("Output".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(StageTableRowData x$17) {
               return x$17.outputWrite();
            }
         }, Ordering.Long$.MODULE$);
      } else if ("Shuffle Read".equals(sortColumn)) {
         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(StageTableRowData x$18) {
               return x$18.shuffleRead();
            }
         }, Ordering.Long$.MODULE$);
      } else {
         if (!"Shuffle Write".equals(sortColumn)) {
            if ("Tasks: Succeeded/Total".equals(sortColumn)) {
               throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unsortable column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{sortColumn})));
            }

            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unknown column: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{sortColumn})));
         }

         ordering = package$.MODULE$.Ordering().by(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final long apply(StageTableRowData x$19) {
               return x$19.shuffleWrite();
            }
         }, Ordering.Long$.MODULE$);
      }

      return desc ? ordering.reverse() : ordering;
   }

   public StageDataSource(AppStatusStore store, Seq<StageData> stages, long currentTime, int pageSize, String sortColumn, boolean desc) {
      super(pageSize);
      this.org$apache$spark$ui$jobs$StageDataSource$$currentTime = currentTime;
      this.data = (Seq)((SeqLike)stages.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StageDataSource $outer;

         public final StageTableRowData apply(StageData stageData) {
            return this.$outer.org$apache$spark$ui$jobs$StageDataSource$$stageRow(stageData);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom())).sorted(this.ordering(sortColumn, desc));
   }
}
