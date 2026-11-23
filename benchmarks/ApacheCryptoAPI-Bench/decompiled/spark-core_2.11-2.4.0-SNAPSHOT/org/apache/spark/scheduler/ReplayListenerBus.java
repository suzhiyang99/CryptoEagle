package org.apache.spark.scheduler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.JsonProtocol$;
import org.apache.spark.util.ListenerBus$class;
import org.json4s.jackson.JsonMethods.;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.StringBuilder;
import scala.io.Codec$;
import scala.io.Source$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.ObjectRef;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\ra!B\u0001\u0003\u0001\u0011Q!!\u0005*fa2\f\u0017\u0010T5ti\u0016tWM\u001d\"vg*\u00111\u0001B\u0001\ng\u000eDW\rZ;mKJT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0005\u0001-\tR\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003%Mi\u0011AA\u0005\u0003)\t\u0011\u0001c\u00159be.d\u0015n\u001d;f]\u0016\u0014()^:\u0011\u0005YIR\"A\f\u000b\u0005a!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005i9\"a\u0002'pO\u001eLgn\u001a\u0005\u00069\u0001!\tAH\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\tq\u0004\u0005\u0002\u0013\u0001!)\u0011\u0005\u0001C\u0001E\u00051!/\u001a9mCf$Ra\t\u00141sy\u0002\"\u0001\u0004\u0013\n\u0005\u0015j!\u0001B+oSRDQa\n\u0011A\u0002!\nq\u0001\\8h\t\u0006$\u0018\r\u0005\u0002*]5\t!F\u0003\u0002,Y\u0005\u0011\u0011n\u001c\u0006\u0002[\u0005!!.\u0019<b\u0013\ty#FA\u0006J]B,Ho\u0015;sK\u0006l\u0007\"B\u0019!\u0001\u0004\u0011\u0014AC:pkJ\u001cWMT1nKB\u00111G\u000e\b\u0003\u0019QJ!!N\u0007\u0002\rA\u0013X\rZ3g\u0013\t9\u0004H\u0001\u0004TiJLgn\u001a\u0006\u0003k5AqA\u000f\u0011\u0011\u0002\u0003\u00071(\u0001\bnCf\u0014W\r\u0016:v]\u000e\fG/\u001a3\u0011\u00051a\u0014BA\u001f\u000e\u0005\u001d\u0011un\u001c7fC:Dqa\u0010\u0011\u0011\u0002\u0003\u0007\u0001)\u0001\u0007fm\u0016tGo\u001d$jYR,'\u000f\u0005\u0002B):\u0011!)\u0014\b\u0003\u00072s!\u0001R&\u000f\u0005\u0015SeB\u0001$J\u001b\u00059%B\u0001%\u001e\u0003\u0019a$o\\8u}%\t\u0011\"\u0003\u0002\b\u0011%\u0011QAB\u0005\u0003\u0007\u00119aA\u0014\u0002\t\u0002\u0011y\u0015!\u0005*fa2\f\u0017\u0010T5ti\u0016tWM\u001d\"vgB\u0011!\u0003\u0015\u0004\u0007\u0003\tA\t\u0001B)\u0014\u0005A[\u0001\"\u0002\u000fQ\t\u0003\u0019F#A(\u0006\tU\u0003\u0006A\u0016\u0002\u0013%\u0016\u0004H.Y=Fm\u0016tGo\u001d$jYR,'\u000f\u0005\u0003\r/JZ\u0014B\u0001-\u000e\u0005%1UO\\2uS>t\u0017\u0007C\u0004[!\n\u0007I\u0011A.\u0002#M+E*R\"U?\u0006cEj\u0018$J\u0019R+%+F\u0001]!\tiF+D\u0001Q\u0011\u0019y\u0006\u000b)A\u00059\u0006\u00112+\u0012'F\u0007R{\u0016\t\u0014'`\r&cE+\u0012*!\u0011\u0015\t\u0003\u0001\"\u0001b)\u0015\u0019#-\u001c8p\u0011\u0015\u0019\u0007\r1\u0001e\u0003\u0015a\u0017N\\3t!\r)'N\r\b\u0003M\"t!AR4\n\u00039I!![\u0007\u0002\u000fA\f7m[1hK&\u00111\u000e\u001c\u0002\t\u0013R,'/\u0019;pe*\u0011\u0011.\u0004\u0005\u0006c\u0001\u0004\rA\r\u0005\u0006u\u0001\u0004\ra\u000f\u0005\u0006\u007f\u0001\u0004\r\u0001\u0011\u0005\bc\u0002\t\n\u0011\"\u0001s\u0003A\u0011X\r\u001d7bs\u0012\"WMZ1vYR$3'F\u0001tU\tYDoK\u0001v!\t180D\u0001x\u0015\tA\u00180A\u0005v]\u000eDWmY6fI*\u0011!0D\u0001\u000bC:tw\u000e^1uS>t\u0017B\u0001?x\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b}\u0002\t\n\u0011\"\u0001\u0000\u0003A\u0011X\r\u001d7bs\u0012\"WMZ1vYR$C'\u0006\u0002\u0002\u0002)\u0012\u0001\t\u001e"
)
public class ReplayListenerBus implements SparkListenerBus {
   private final CopyOnWriteArrayList org$apache$spark$util$ListenerBus$$listenersPlusTimers;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Function1<String, Object> SELECT_ALL_FILTER() {
      return ReplayListenerBus$.MODULE$.SELECT_ALL_FILTER();
   }

   public void doPostEvent(SparkListenerInterface listener, SparkListenerEvent event) {
      SparkListenerBus$class.doPostEvent(this, listener, event);
   }

   public CopyOnWriteArrayList org$apache$spark$util$ListenerBus$$listenersPlusTimers() {
      return this.org$apache$spark$util$ListenerBus$$listenersPlusTimers;
   }

   public void org$apache$spark$util$ListenerBus$_setter_$org$apache$spark$util$ListenerBus$$listenersPlusTimers_$eq(CopyOnWriteArrayList x$1) {
      this.org$apache$spark$util$ListenerBus$$listenersPlusTimers = x$1;
   }

   public List<SparkListenerInterface> listeners() {
      return ListenerBus$class.listeners(this);
   }

   public Option getTimer(Object listener) {
      return ListenerBus$class.getTimer(this, listener);
   }

   public final void addListener(Object listener) {
      ListenerBus$class.addListener(this, listener);
   }

   public final void removeListener(Object listener) {
      ListenerBus$class.removeListener(this, listener);
   }

   public void postToAll(Object event) {
      ListenerBus$class.postToAll(this, event);
   }

   public <T extends SparkListenerInterface> Seq<T> findListenersByClass(ClassTag<T> evidence$1) {
      return ListenerBus$class.findListenersByClass(this, evidence$1);
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public void replay(InputStream logData, String sourceName, boolean maybeTruncated, Function1<String, Object> eventsFilter) {
      Iterator lines = Source$.MODULE$.fromInputStream(logData, Codec$.MODULE$.fallbackSystemCodec()).getLines();
      this.replay(lines, sourceName, maybeTruncated, eventsFilter);
   }

   public void replay(Iterator<String> lines, String sourceName, boolean maybeTruncated, Function1<String, Object> eventsFilter) {
      ObjectRef currentLine = ObjectRef.create((Object)null);
      IntRef lineNumber = IntRef.create(0);
      HashSet unrecognizedEvents = new HashSet();
      HashSet unrecognizedProperties = new HashSet();

      label125:
      try {
         Iterator lineEntries = lines.zipWithIndex().filter(new Serializable(this, eventsFilter) {
            public static final long serialVersionUID = 0L;
            private final Function1 eventsFilter$1;

            public final boolean apply(Tuple2<String, Object> x0$1) {
               if (x0$1 != null) {
                  String line = (String)x0$1._1();
                  boolean var4 = BoxesRunTime.unboxToBoolean(this.eventsFilter$1.apply(line));
                  return var4;
               } else {
                  throw new MatchError(x0$1);
               }
            }

            public {
               this.eventsFilter$1 = eventsFilter$1;
            }
         });

         while(lineEntries.hasNext()) {
            try {
               Tuple2 entry = (Tuple2)lineEntries.next();
               currentLine.elem = (T)((String)entry._1());
               lineNumber.elem = entry._2$mcI$sp() + 1;
               this.postToAll(JsonProtocol$.MODULE$.sparkEventFromJson(.MODULE$.parse(org.json4s.package..MODULE$.string2JsonInput((String)currentLine.elem), .MODULE$.parse$default$2(), .MODULE$.parse$default$3())));
            } catch (ClassNotFoundException var23) {
               if (unrecognizedEvents.contains(var23.getMessage())) {
                  BoxedUnit var28 = BoxedUnit.UNIT;
               } else {
                  this.logWarning(new Serializable(this, var23) {
                     public static final long serialVersionUID = 0L;
                     private final ClassNotFoundException e$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Drop unrecognized event: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.e$1.getMessage()}));
                     }

                     public {
                        this.e$1 = e$1;
                     }
                  });
                  BoxesRunTime.boxToBoolean(unrecognizedEvents.add(var23.getMessage()));
               }

               this.logDebug(new Serializable(this, currentLine) {
                  public static final long serialVersionUID = 0L;
                  private final ObjectRef currentLine$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Drop incompatible event log: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{(String)this.currentLine$1.elem}));
                  }

                  public {
                     this.currentLine$1 = currentLine$1;
                  }
               });
            } catch (UnrecognizedPropertyException var24) {
               if (unrecognizedProperties.contains(var24.getMessage())) {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               } else {
                  this.logWarning(new Serializable(this, var24) {
                     public static final long serialVersionUID = 0L;
                     private final UnrecognizedPropertyException e$2;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Drop unrecognized property: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.e$2.getMessage()}));
                     }

                     public {
                        this.e$2 = e$2;
                     }
                  });
                  BoxesRunTime.boxToBoolean(unrecognizedProperties.add(var24.getMessage()));
               }

               this.logDebug(new Serializable(this, currentLine) {
                  public static final long serialVersionUID = 0L;
                  private final ObjectRef currentLine$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Drop incompatible event log: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{(String)this.currentLine$1.elem}));
                  }

                  public {
                     this.currentLine$1 = currentLine$1;
                  }
               });
            } catch (JsonParseException var25) {
               if (!maybeTruncated || lineEntries.hasNext()) {
                  throw var25;
               }

               this.logWarning(new Serializable(this, sourceName, lineNumber) {
                  public static final long serialVersionUID = 0L;
                  private final String sourceName$1;
                  private final IntRef lineNumber$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Got JsonParseException from log file ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.sourceName$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" at line ", ", the file might not have finished writing cleanly."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.lineNumber$1.elem)}))).toString();
                  }

                  public {
                     this.sourceName$1 = sourceName$1;
                     this.lineNumber$1 = lineNumber$1;
                  }
               });
            }
         }
      } catch (Throwable var26) {
         if (var26 instanceof EOFException && maybeTruncated) {
            BoxedUnit var27 = BoxedUnit.UNIT;
         } else {
            if (var26 instanceof IOException) {
               IOException var12 = (IOException)var26;
               throw var12;
            }

            if (!(var26 instanceof Exception)) {
               throw var26;
            }

            Exception var13 = (Exception)var26;
            this.logError(new Serializable(this, sourceName) {
               public static final long serialVersionUID = 0L;
               private final String sourceName$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception parsing Spark event log: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.sourceName$1}));
               }

               public {
                  this.sourceName$1 = sourceName$1;
               }
            }, var13);
            this.logError(new Serializable(this, currentLine, lineNumber) {
               public static final long serialVersionUID = 0L;
               private final ObjectRef currentLine$1;
               private final IntRef lineNumber$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Malformed line #", ": ", "\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.lineNumber$1.elem), (String)this.currentLine$1.elem}));
               }

               public {
                  this.currentLine$1 = currentLine$1;
                  this.lineNumber$1 = lineNumber$1;
               }
            });
            BoxedUnit var11 = BoxedUnit.UNIT;
         }
         break label125;
      }

   }

   public boolean replay$default$3() {
      return false;
   }

   public Function1<String, Object> replay$default$4() {
      return ReplayListenerBus$.MODULE$.SELECT_ALL_FILTER();
   }

   public ReplayListenerBus() {
      Logging$class.$init$(this);
      ListenerBus$class.$init$(this);
      SparkListenerBus$class.$init$(this);
   }
}
