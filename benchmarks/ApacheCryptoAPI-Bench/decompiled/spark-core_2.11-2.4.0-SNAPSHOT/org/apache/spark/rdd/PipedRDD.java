package org.apache.spark.rdd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.spark.Partition;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.util.Utils$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.MatchError;
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
import scala.collection.JavaConverters$;
import scala.collection.Map;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.StringBuilder;
import scala.io.Codec$;
import scala.io.Source$;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BooleanRef;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.util.control.Exception$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ed!B\u0001\u0003\u0001\u0011Q!\u0001\u0003)ja\u0016$'\u000b\u0012#\u000b\u0005\r!\u0011a\u0001:eI*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x-\u0006\u0002\fAM\u0011\u0001\u0001\u0004\t\u0004\u001b9\u0001R\"\u0001\u0002\n\u0005=\u0011!a\u0001*E\tB\u0011\u0011c\u0006\b\u0003%Ui\u0011a\u0005\u0006\u0002)\u0005)1oY1mC&\u0011acE\u0001\u0007!J,G-\u001a4\n\u0005aI\"AB*ue&twM\u0003\u0002\u0017'!A1\u0004\u0001B\u0001B\u0003%Q$\u0001\u0003qe\u001648\u0001\u0001\t\u0004\u001b9q\u0002CA\u0010!\u0019\u0001!Q!\t\u0001C\u0002\t\u0012\u0011\u0001V\t\u0003G\u0019\u0002\"A\u0005\u0013\n\u0005\u0015\u001a\"a\u0002(pi\"Lgn\u001a\t\u0003%\u001dJ!\u0001K\n\u0003\u0007\u0005s\u0017\u0010\u0003\u0005+\u0001\t\u0005\t\u0015!\u0003,\u0003\u001d\u0019w.\\7b]\u0012\u00042\u0001\f\u001b\u0011\u001d\ti#G\u0004\u0002/c5\tqF\u0003\u000219\u00051AH]8pizJ\u0011\u0001F\u0005\u0003gM\tq\u0001]1dW\u0006<W-\u0003\u00026m\t\u00191+Z9\u000b\u0005M\u001a\u0002\u0002\u0003\u001d\u0001\u0005\u0003\u0005\u000b\u0011B\u001d\u0002\u000f\u0015tgOV1sgB!!(\u0010\t\u0011\u001b\u0005Y$B\u0001\u001f\u0014\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003}m\u00121!T1q\u0011!\u0001\u0005A!A!\u0002\u0013\t\u0015\u0001\u00059sS:$\b+\u001b9f\u0007>tG/\u001a=u!\u0011\u0011\"\tR#\n\u0005\r\u001b\"!\u0003$v]\u000e$\u0018n\u001c82!\u0011\u0011\"\tE#\u0011\u0005I1\u0015BA$\u0014\u0005\u0011)f.\u001b;\t\u0011%\u0003!\u0011!Q\u0001\n)\u000bq\u0002\u001d:j]R\u0014F\tR#mK6,g\u000e\u001e\t\u0006%-sB)R\u0005\u0003\u0019N\u0011\u0011BR;oGRLwN\u001c\u001a\t\u00119\u0003!\u0011!Q\u0001\n=\u000b!c]3qCJ\fG/Z,pe.Lgn\u001a#jeB\u0011!\u0003U\u0005\u0003#N\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005T\u0001\t\u0005\t\u0015!\u0003U\u0003)\u0011WO\u001a4feNK'0\u001a\t\u0003%UK!AV\n\u0003\u0007%sG\u000f\u0003\u0005Y\u0001\t\u0005\t\u0015!\u0003\u0011\u0003!)gnY8eS:<\u0007\u0002\u0003.\u0001\u0005\u0007\u0005\u000b1B.\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007E\u0002]?zi\u0011!\u0018\u0006\u0003=N\tqA]3gY\u0016\u001cG/\u0003\u0002a;\nA1\t\\1tgR\u000bw\rC\u0003c\u0001\u0011\u00051-\u0001\u0004=S:LGO\u0010\u000b\nI\u001eD\u0017N[6m[:$\"!\u001a4\u0011\u00075\u0001a\u0004C\u0003[C\u0002\u000f1\fC\u0003\u001cC\u0002\u0007Q\u0004C\u0003+C\u0002\u00071\u0006C\u00039C\u0002\u0007\u0011\bC\u0003AC\u0002\u0007\u0011\tC\u0003JC\u0002\u0007!\nC\u0003OC\u0002\u0007q\nC\u0003TC\u0002\u0007A\u000bC\u0003YC\u0002\u0007\u0001\u0003C\u0003q\u0001\u0011\u0005\u0013/A\u0007hKR\u0004\u0016M\u001d;ji&|gn]\u000b\u0002eB\u0019!c];\n\u0005Q\u001c\"!B!se\u0006L\bC\u0001<x\u001b\u0005!\u0011B\u0001=\u0005\u0005%\u0001\u0016M\u001d;ji&|gN\u0002\u0003{\u0001\u0001Y(a\u0006(pi\u0016\u000bX/\u00197t\r&dWMT1nK\u001aKG\u000e^3s'\u0011IH0!\u0003\u0011\u0007u\f)!D\u0001\u007f\u0015\ry\u0018\u0011A\u0001\u0005Y\u0006twM\u0003\u0002\u0002\u0004\u0005!!.\u0019<b\u0013\r\t9A \u0002\u0007\u001f\nTWm\u0019;\u0011\t\u0005-\u0011\u0011C\u0007\u0003\u0003\u001bQA!a\u0004\u0002\u0002\u0005\u0011\u0011n\\\u0005\u0005\u0003'\tiA\u0001\bGS2,g.Y7f\r&dG/\u001a:\t\u0013\u0005]\u0011P!A!\u0002\u0013\u0001\u0012A\u00034jYR,'OT1nK\"1!-\u001fC\u0001\u00037!B!!\b\u0002\"A\u0019\u0011qD=\u000e\u0003\u0001Aq!a\u0006\u0002\u001a\u0001\u0007\u0001\u0003C\u0004\u0002&e$\t!a\n\u0002\r\u0005\u001c7-\u001a9u)\u0015y\u0015\u0011FA\u001a\u0011!\tY#a\tA\u0002\u00055\u0012a\u00013jeB!\u00111BA\u0018\u0013\u0011\t\t$!\u0004\u0003\t\u0019KG.\u001a\u0005\b\u0003k\t\u0019\u00031\u0001\u0011\u0003\u0011q\u0017-\\3\t\u000f\u0005e\u0002\u0001\"\u0011\u0002<\u000591m\\7qkR,GCBA\u001f\u0003\u0007\n9\u0005\u0005\u0003-\u0003\u007f\u0001\u0012bAA!m\tA\u0011\n^3sCR|'\u000fC\u0004\u0002F\u0005]\u0002\u0019A;\u0002\u000bM\u0004H.\u001b;\t\u0011\u0005%\u0013q\u0007a\u0001\u0003\u0017\nqaY8oi\u0016DH\u000fE\u0002w\u0003\u001bJ1!a\u0014\u0005\u0005-!\u0016m]6D_:$X\r\u001f;\b\u000f\u0005M#\u0001#\u0003\u0002V\u0005A\u0001+\u001b9fIJ#E\tE\u0002\u000e\u0003/2a!\u0001\u0002\t\n\u0005e3CBA,\u00037\n\t\u0007E\u0002\u0013\u0003;J1!a\u0018\u0014\u0005\u0019\te.\u001f*fMB\u0019!#a\u0019\n\u0007\u0005\u00154C\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0004c\u0003/\"\t!!\u001b\u0015\u0005\u0005U\u0003\u0002CA7\u0003/\"\t!a\u001c\u0002\u0011Q|7.\u001a8ju\u0016$2aKA9\u0011\u0019Q\u00131\u000ea\u0001!!Q\u0011QOA,\u0003\u0003%I!a\u001e\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0002y\u0002"
)
public class PipedRDD<T> extends RDD<String> {
   public final Seq<String> org$apache$spark$rdd$PipedRDD$$command;
   private final Map<String, String> envVars;
   public final Function1<Function1<String, BoxedUnit>, BoxedUnit> org$apache$spark$rdd$PipedRDD$$printPipeContext;
   public final Function2<T, Function1<String, BoxedUnit>, BoxedUnit> org$apache$spark$rdd$PipedRDD$$printRDDElement;
   private final boolean separateWorkingDir;
   public final int org$apache$spark$rdd$PipedRDD$$bufferSize;
   public final String org$apache$spark$rdd$PipedRDD$$encoding;
   public final ClassTag<T> org$apache$spark$rdd$PipedRDD$$evidence$1;

   public static Seq<String> tokenize(String var0) {
      return PipedRDD$.MODULE$.tokenize(var0);
   }

   public Partition[] getPartitions() {
      return this.firstParent(this.org$apache$spark$rdd$PipedRDD$$evidence$1).partitions();
   }

   public Iterator<String> compute(Partition split, TaskContext context) {
      ProcessBuilder pb = new ProcessBuilder((List)JavaConverters$.MODULE$.seqAsJavaListConverter(this.org$apache$spark$rdd$PipedRDD$$command).asJava());
      java.util.Map currentEnvVars = pb.environment();
      this.envVars.foreach(new Serializable(this, currentEnvVars) {
         public static final long serialVersionUID = 0L;
         private final java.util.Map currentEnvVars$1;

         public final String apply(Tuple2<String, String> x0$1) {
            if (x0$1 != null) {
               String variable = (String)x0$1._1();
               String value = (String)x0$1._2();
               String var5 = (String)this.currentEnvVars$1.put(variable, value);
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.currentEnvVars$1 = currentEnvVars$1;
         }
      });
      if (split instanceof HadoopPartition) {
         HadoopPartition hadoopSplit = (HadoopPartition)split;
         currentEnvVars.putAll((java.util.Map)JavaConverters$.MODULE$.mapAsJavaMapConverter(hadoopSplit.getPipeEnvVars()).asJava());
      }

      String taskDirectory = (new StringBuilder()).append((Object)"tasks").append((Object)File.separator).append((Object)UUID.randomUUID().toString()).toString();
      BooleanRef workInTaskDirectory = BooleanRef.create(false);
      this.logDebug(new Serializable(this, taskDirectory) {
         public static final long serialVersionUID = 0L;
         private final String taskDirectory$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"taskDirectory = ").append((Object)this.taskDirectory$1).toString();
         }

         public {
            this.taskDirectory$1 = taskDirectory$1;
         }
      });
      if (this.separateWorkingDir) {
         File currentDir = new File(".");
         this.logDebug(new Serializable(this, currentDir) {
            public static final long serialVersionUID = 0L;
            private final File currentDir$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"currentDir = ").append((Object)this.currentDir$1.getAbsolutePath()).toString();
            }

            public {
               this.currentDir$1 = currentDir$1;
            }
         });
         File taskDirFile = new File(taskDirectory);
         taskDirFile.mkdirs();

         try {
            NotEqualsFileNameFilter tasksDirFilter = new NotEqualsFileNameFilter("tasks");
            Predef$.MODULE$.refArrayOps((Object[])currentDir.list(tasksDirFilter)).foreach(new Serializable(this, taskDirectory, currentDir) {
               public static final long serialVersionUID = 0L;
               private final String taskDirectory$1;
               private final File currentDir$1;

               public final void apply(String file) {
                  File fileWithDir = new File(this.currentDir$1, file);
                  Utils$.MODULE$.symlink(new File(fileWithDir.getAbsolutePath()), new File((new StringBuilder()).append((Object)this.taskDirectory$1).append((Object)File.separator).append((Object)fileWithDir.getName()).toString()));
               }

               public {
                  this.taskDirectory$1 = taskDirectory$1;
                  this.currentDir$1 = currentDir$1;
               }
            });
            pb.directory(taskDirFile);
            workInTaskDirectory.elem = true;
         } catch (Exception var16) {
            this.logError(new Serializable(this, taskDirectory, var16) {
               public static final long serialVersionUID = 0L;
               private final String taskDirectory$1;
               private final Exception e$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Unable to setup task working directory: ").append((Object)this.e$1.getMessage()).append((Object)" (").append((Object)this.taskDirectory$1).append((Object)")").toString();
               }

               public {
                  this.taskDirectory$1 = taskDirectory$1;
                  this.e$1 = e$1;
               }
            }, var16);
         }
      }

      Process proc = pb.start();
      SparkEnv env = SparkEnv$.MODULE$.get();
      AtomicReference childThreadException = new AtomicReference((Object)null);
      (new Thread(this, proc, childThreadException) {
         // $FF: synthetic field
         private final PipedRDD $outer;
         private final Process proc$1;
         private final AtomicReference childThreadException$1;

         public void run() {
            InputStream err = this.proc$1.getErrorStream();

            try {
               try {
                  Source$.MODULE$.fromInputStream(err, Codec$.MODULE$.string2codec(this.$outer.org$apache$spark$rdd$PipedRDD$$encoding)).getLines().foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final void apply(String line) {
                        System.err.println(line);
                     }
                  });
               } catch (Throwable var8) {
                  this.childThreadException$1.set(var8);
                  return;
               }
            } finally {
               err.close();
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.proc$1 = proc$1;
               this.childThreadException$1 = childThreadException$1;
            }
         }
      }).start();
      (new Thread(this, split, context, proc, childThreadException) {
         // $FF: synthetic field
         private final PipedRDD $outer;
         private final Partition split$1;
         private final TaskContext context$1;
         private final Process proc$1;
         private final AtomicReference childThreadException$1;

         public void run() {
            TaskContext$.MODULE$.setTaskContext(this.context$1);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.proc$1.getOutputStream(), this.$outer.org$apache$spark$rdd$PipedRDD$$encoding), this.$outer.org$apache$spark$rdd$PipedRDD$$bufferSize));

            try {
               try {
                  if (this.$outer.org$apache$spark$rdd$PipedRDD$$printPipeContext == null) {
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     this.$outer.org$apache$spark$rdd$PipedRDD$$printPipeContext.apply(new Serializable(this, out) {
                        public static final long serialVersionUID = 0L;
                        private final PrintWriter out$1;

                        public final void apply(String x$1) {
                           this.out$1.println(x$1);
                        }

                        public {
                           this.out$1 = out$1;
                        }
                     });
                  }

                  this.$outer.firstParent(this.$outer.org$apache$spark$rdd$PipedRDD$$evidence$1).iterator(this.split$1, this.context$1).foreach(new Serializable(this, out) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     public final PrintWriter out$1;

                     public final void apply(T elem) {
                        if (this.$outer.org$apache$spark$rdd$PipedRDD$$anon$$$outer().org$apache$spark$rdd$PipedRDD$$printRDDElement == null) {
                           this.out$1.println(elem);
                        } else {
                           this.$outer.org$apache$spark$rdd$PipedRDD$$anon$$$outer().org$apache$spark$rdd$PipedRDD$$printRDDElement.apply(elem, new Serializable(this) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;

                              public final void apply(String x$1) {
                                 this.$outer.out$1.println(x$1);
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                 }
                              }
                           });
                        }

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
               } catch (Throwable var8) {
                  this.childThreadException$1.set(var8);
                  return;
               }
            } finally {
               out.close();
            }

         }

         // $FF: synthetic method
         public PipedRDD org$apache$spark$rdd$PipedRDD$$anon$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.split$1 = split$1;
               this.context$1 = context$1;
               this.proc$1 = proc$1;
               this.childThreadException$1 = childThreadException$1;
            }
         }
      }).start();
      Iterator lines = Source$.MODULE$.fromInputStream(proc.getInputStream(), Codec$.MODULE$.string2codec(this.org$apache$spark$rdd$PipedRDD$$encoding)).getLines();
      return new Iterator<String>(this, taskDirectory, workInTaskDirectory, proc, childThreadException, lines) {
         // $FF: synthetic field
         private final PipedRDD $outer;
         public final String taskDirectory$1;
         private final BooleanRef workInTaskDirectory$1;
         private final Process proc$1;
         private final AtomicReference childThreadException$1;
         private final Iterator lines$1;

         public Iterator<String> seq() {
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

         public Iterator<String> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<String> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<String> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<String, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<String, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<String> filter(Function1<String, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<String, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<String> withFilter(Function1<String, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<String> filterNot(Function1<String, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<String, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, String, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<String, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<String> takeWhile(Function1<String, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<String>, Iterator<String>> partition(Function1<String, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<String>, Iterator<String>> span(Function1<String, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<String> dropWhile(Function1<String, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<String, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<String, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<String, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<String, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<String, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<String> find(Function1<String, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<String, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<String> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<String>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<String>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<String>, Iterator<String>> duplicate() {
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

         public Traversable<String> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<String> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<String> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public scala.collection.immutable.List<String> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<String, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<String, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, String, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<String, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, String, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<String, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, String, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<String, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, String, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<String, B, B> op) {
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

         public <B> B aggregate(Function0<B> z, Function2<B, String, B> seqop, Function2<B, B, B> combop) {
            return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
         }

         public <B> B sum(Numeric<B> num) {
            return (B)TraversableOnce$class.sum(this, num);
         }

         public <B> B product(Numeric<B> num) {
            return (B)TraversableOnce$class.product(this, num);
         }

         public Object min(Ordering cmp) {
            return TraversableOnce$class.min(this, cmp);
         }

         public Object max(Ordering cmp) {
            return TraversableOnce$class.max(this, cmp);
         }

         public Object maxBy(Function1 f, Ordering cmp) {
            return TraversableOnce$class.maxBy(this, f, cmp);
         }

         public Object minBy(Function1 f, Ordering cmp) {
            return TraversableOnce$class.minBy(this, f, cmp);
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

         public scala.collection.immutable.List<String> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<String> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<String> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<String> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<String> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, String, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> scala.collection.immutable.Map<T, U> toMap(Predef.$less$colon$less<String, Tuple2<T, U>> ev) {
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

         public String next() {
            if (this.hasNext()) {
               return (String)this.lines$1.next();
            } else {
               throw new NoSuchElementException();
            }
         }

         public boolean hasNext() {
            boolean var10000;
            if (this.lines$1.hasNext()) {
               var10000 = true;
            } else {
               int exitStatus = this.proc$1.waitFor();
               this.cleanup();
               if (exitStatus != 0) {
                  throw new IllegalStateException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Subprocess exited with status ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(exitStatus)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Command ran: "})))).s(Nil$.MODULE$)).append((Object)this.$outer.org$apache$spark$rdd$PipedRDD$$command.mkString(" ")).toString());
               }

               var10000 = false;
            }

            boolean result = var10000;
            this.propagateChildException();
            return result;
         }

         private void cleanup() {
            if (this.workInTaskDirectory$1.elem) {
               Exception$.MODULE$.ignoring(Predef$.MODULE$.wrapRefArray((Object[])(new Class[]{IOException.class}))).apply(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply() {
                     this.apply$mcV$sp();
                  }

                  public void apply$mcV$sp() {
                     Utils$.MODULE$.deleteRecursively(new File(this.$outer.taskDirectory$1));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               this.$outer.logDebug(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Removed task working directory ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.taskDirectory$1}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
            }

         }

         private void propagateChildException() {
            Throwable t = (Throwable)this.childThreadException$1.get();
            if (t != null) {
               String commandRan = this.$outer.org$apache$spark$rdd$PipedRDD$$command.mkString(" ");
               this.$outer.logError(new Serializable(this, t, commandRan) {
                  public static final long serialVersionUID = 0L;
                  private final Throwable t$1;
                  private final String commandRan$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Caught exception while running pipe() operator. Command ran: ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.commandRan$1}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.t$1.getMessage()}))).toString();
                  }

                  public {
                     this.t$1 = t$1;
                     this.commandRan$1 = commandRan$1;
                  }
               });
               this.proc$1.destroy();
               this.cleanup();
               throw t;
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.taskDirectory$1 = taskDirectory$1;
               this.workInTaskDirectory$1 = workInTaskDirectory$1;
               this.proc$1 = proc$1;
               this.childThreadException$1 = childThreadException$1;
               this.lines$1 = lines$1;
               TraversableOnce$class.$init$(this);
               Iterator$class.$init$(this);
            }
         }
      };
   }

   public PipedRDD(RDD<T> prev, Seq<String> command, Map<String, String> envVars, Function1<Function1<String, BoxedUnit>, BoxedUnit> printPipeContext, Function2<T, Function1<String, BoxedUnit>, BoxedUnit> printRDDElement, boolean separateWorkingDir, int bufferSize, String encoding, ClassTag<T> evidence$1) {
      super(prev, ClassTag$.MODULE$.apply(String.class));
      this.org$apache$spark$rdd$PipedRDD$$command = command;
      this.envVars = envVars;
      this.org$apache$spark$rdd$PipedRDD$$printPipeContext = printPipeContext;
      this.org$apache$spark$rdd$PipedRDD$$printRDDElement = printRDDElement;
      this.separateWorkingDir = separateWorkingDir;
      this.org$apache$spark$rdd$PipedRDD$$bufferSize = bufferSize;
      this.org$apache$spark$rdd$PipedRDD$$encoding = encoding;
      this.org$apache$spark$rdd$PipedRDD$$evidence$1 = evidence$1;
   }

   public class NotEqualsFileNameFilter implements FilenameFilter {
      private final String filterName;
      // $FF: synthetic field
      public final PipedRDD $outer;

      public boolean accept(File dir, String name) {
         return !name.equals(this.filterName);
      }

      // $FF: synthetic method
      public PipedRDD org$apache$spark$rdd$PipedRDD$NotEqualsFileNameFilter$$$outer() {
         return this.$outer;
      }

      public NotEqualsFileNameFilter(PipedRDD<T> filterName) {
         this.filterName = filterName;
         if (PipedRDD.this == null) {
            throw null;
         } else {
            this.$outer = PipedRDD.this;
            super();
         }
      }
   }
}
