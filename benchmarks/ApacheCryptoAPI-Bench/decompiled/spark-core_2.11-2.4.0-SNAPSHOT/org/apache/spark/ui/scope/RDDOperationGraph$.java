package org.apache.spark.ui.scope;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rdd.RDDOperationScope;
import org.apache.spark.scheduler.StageInfo;
import org.apache.spark.storage.RDDInfo;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.storage.StorageLevel$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple2$mcZZ$sp;
import scala.Tuple4;
import scala.collection.IterableLike;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.Nothing$;
import scala.runtime.TraitSetter;

public final class RDDOperationGraph$ implements Logging, Serializable {
   public static final RDDOperationGraph$ MODULE$;
   private final String STAGE_CLUSTER_PREFIX;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new RDDOperationGraph$();
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

   public String STAGE_CLUSTER_PREFIX() {
      return this.STAGE_CLUSTER_PREFIX;
   }

   public RDDOperationGraph makeOperationGraph(StageInfo stage, int retainedNodes) {
      ListBuffer edges = new ListBuffer();
      HashMap nodes = new HashMap();
      HashMap clusters = new HashMap();
      String stageClusterId = (new StringBuilder()).append((Object)this.STAGE_CLUSTER_PREFIX()).append((Object)BoxesRunTime.boxToInteger(stage.stageId())).toString();
      String stageClusterName = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Stage ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(stage.stageId())}))).append((Object)(stage.attemptNumber() == 0 ? "" : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" (attempt ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(stage.attemptNumber())})))).toString();
      RDDOperationCluster rootCluster = new RDDOperationCluster(stageClusterId, stageClusterName);
      IntRef rootNodeCount = IntRef.create(0);
      HashSet addRDDIds = new HashSet();
      HashSet dropRDDIds = new HashSet();
      ((IterableLike)stage.rddInfos().sortBy(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(RDDInfo x$3) {
            return x$3.id();
         }
      }, Ordering.Int$.MODULE$)).foreach(new Serializable(retainedNodes, edges, nodes, clusters, rootCluster, rootNodeCount, addRDDIds, dropRDDIds) {
         public static final long serialVersionUID = 0L;
         private final int retainedNodes$1;
         private final ListBuffer edges$1;
         private final HashMap nodes$1;
         public final HashMap clusters$1;
         public final RDDOperationCluster rootCluster$1;
         private final IntRef rootNodeCount$1;
         public final HashSet addRDDIds$1;
         public final HashSet dropRDDIds$1;

         public final void apply(RDDInfo rdd) {
            Seq parentIds = rdd.parentIds();
            boolean var10000;
            if (parentIds.isEmpty()) {
               ++this.rootNodeCount$1.elem;
               var10000 = this.rootNodeCount$1.elem <= this.retainedNodes$1;
            } else {
               var10000 = parentIds.exists(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final boolean apply(int id) {
                     return this.apply$mcZI$sp(id);
                  }

                  public boolean apply$mcZI$sp(int id) {
                     return this.$outer.addRDDIds$1.contains(BoxesRunTime.boxToInteger(id)) || !this.$outer.dropRDDIds$1.contains(BoxesRunTime.boxToInteger(id));
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

            boolean isAllowed = var10000;
            if (isAllowed) {
               this.addRDDIds$1.$plus$eq(BoxesRunTime.boxToInteger(rdd.id()));
               this.edges$1.$plus$plus$eq((TraversableOnce)((TraversableLike)parentIds.filter(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final boolean apply(int id) {
                     return this.apply$mcZI$sp(id);
                  }

                  public boolean apply$mcZI$sp(int id) {
                     return !this.$outer.dropRDDIds$1.contains(BoxesRunTime.boxToInteger(id));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               })).map(new Serializable(this, rdd) {
                  public static final long serialVersionUID = 0L;
                  private final RDDInfo rdd$1;

                  public final RDDOperationEdge apply(int x$4) {
                     return new RDDOperationEdge(x$4, this.rdd$1.id());
                  }

                  public {
                     this.rdd$1 = rdd$1;
                  }
               }, Seq$.MODULE$.canBuildFrom()));
            } else {
               this.dropRDDIds$1.$plus$eq(BoxesRunTime.boxToInteger(rdd.id()));
            }

            RDDOperationNode node = (RDDOperationNode)this.nodes$1.getOrElseUpdate(BoxesRunTime.boxToInteger(rdd.id()), new Serializable(this, rdd) {
               public static final long serialVersionUID = 0L;
               private final RDDInfo rdd$1;

               public final RDDOperationNode apply() {
                  boolean var2;
                  RDDOperationNode var10000;
                  int var10002;
                  String var10003;
                  label17: {
                     label16: {
                        var10000 = new RDDOperationNode;
                        var10002 = this.rdd$1.id();
                        var10003 = this.rdd$1.name();
                        StorageLevel var10004 = this.rdd$1.storageLevel();
                        StorageLevel var1 = StorageLevel$.MODULE$.NONE();
                        if (var10004 == null) {
                           if (var1 == null) {
                              break label16;
                           }
                        } else if (var10004.equals(var1)) {
                           break label16;
                        }

                        var2 = true;
                        break label17;
                     }

                     var2 = false;
                  }

                  var10000.<init>(var10002, var10003, var2, this.rdd$1.callSite());
                  return var10000;
               }

               public {
                  this.rdd$1 = rdd$1;
               }
            });
            if (rdd.scope().isEmpty()) {
               if (isAllowed) {
                  this.rootCluster$1.attachChildNode(node);
               }
            } else {
               Seq rddScopes = (Seq)rdd.scope().map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Seq<RDDOperationScope> apply(RDDOperationScope scope) {
                     return scope.getAllScopes();
                  }
               }).getOrElse(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final Seq<Nothing$> apply() {
                     return (Seq)Seq$.MODULE$.empty();
                  }
               });
               Seq rddClusters = (Seq)rddScopes.map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final RDDOperationCluster apply(RDDOperationScope scope) {
                     String clusterId = scope.id();
                     String clusterName = scope.name().replaceAll("\\n", "\\\\n");
                     return (RDDOperationCluster)this.$outer.clusters$1.getOrElseUpdate(clusterId, new Serializable(this, clusterId, clusterName) {
                        public static final long serialVersionUID = 0L;
                        private final String clusterId$1;
                        private final String clusterName$1;

                        public final RDDOperationCluster apply() {
                           return new RDDOperationCluster(this.clusterId$1, this.clusterName$1);
                        }

                        public {
                           this.clusterId$1 = clusterId$1;
                           this.clusterName$1 = clusterName$1;
                        }
                     });
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, Seq$.MODULE$.canBuildFrom());
               rddClusters.sliding(2).foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final void apply(Seq<RDDOperationCluster> pc) {
                     if (pc.size() == 2) {
                        RDDOperationCluster parentCluster = (RDDOperationCluster)pc.apply(0);
                        RDDOperationCluster childCluster = (RDDOperationCluster)pc.apply(1);
                        parentCluster.attachChildCluster(childCluster);
                     }

                  }
               });
               rddClusters.headOption().foreach(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(RDDOperationCluster cluster) {
                     if (!this.$outer.rootCluster$1.childClusters().contains(cluster)) {
                        this.$outer.rootCluster$1.attachChildCluster(cluster);
                     }

                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               if (isAllowed) {
                  rddClusters.lastOption().foreach(new Serializable(this, node) {
                     public static final long serialVersionUID = 0L;
                     private final RDDOperationNode node$1;

                     public final void apply(RDDOperationCluster cluster) {
                        cluster.attachChildNode(this.node$1);
                     }

                     public {
                        this.node$1 = node$1;
                     }
                  });
               }
            }

         }

         public {
            this.retainedNodes$1 = retainedNodes$1;
            this.edges$1 = edges$1;
            this.nodes$1 = nodes$1;
            this.clusters$1 = clusters$1;
            this.rootCluster$1 = rootCluster$1;
            this.rootNodeCount$1 = rootNodeCount$1;
            this.addRDDIds$1 = addRDDIds$1;
            this.dropRDDIds$1 = dropRDDIds$1;
         }
      });
      ListBuffer internalEdges = new ListBuffer();
      ListBuffer outgoingEdges = new ListBuffer();
      ListBuffer incomingEdges = new ListBuffer();
      edges.foreach(new Serializable(stage, nodes, internalEdges, outgoingEdges, incomingEdges) {
         public static final long serialVersionUID = 0L;
         public final StageInfo stage$1;
         private final HashMap nodes$1;
         private final ListBuffer internalEdges$1;
         private final ListBuffer outgoingEdges$1;
         private final ListBuffer incomingEdges$1;

         public final Object apply(RDDOperationEdge x0$1) {
            if (x0$1 == null) {
               throw new MatchError(x0$1);
            } else {
               boolean fromThisGraph = this.nodes$1.contains(BoxesRunTime.boxToInteger(x0$1.fromId()));
               boolean toThisGraph = this.nodes$1.contains(BoxesRunTime.boxToInteger(x0$1.toId()));
               Tuple2$mcZZ$sp var7 = new Tuple2$mcZZ$sp(fromThisGraph, toThisGraph);
               Object var10;
               if (var7 != null) {
                  boolean var8 = ((Tuple2)var7)._1$mcZ$sp();
                  boolean var9 = ((Tuple2)var7)._2$mcZ$sp();
                  if (var8 && var9) {
                     var10 = this.internalEdges$1.$plus$eq(x0$1);
                     return var10;
                  }
               }

               if (var7 != null) {
                  boolean var11 = ((Tuple2)var7)._1$mcZ$sp();
                  boolean var12 = ((Tuple2)var7)._2$mcZ$sp();
                  if (var11 && !var12) {
                     var10 = this.outgoingEdges$1.$plus$eq(x0$1);
                     return var10;
                  }
               }

               if (var7 != null) {
                  boolean var13 = ((Tuple2)var7)._1$mcZ$sp();
                  boolean var14 = ((Tuple2)var7)._2$mcZ$sp();
                  if (!var13 && var14) {
                     var10 = this.incomingEdges$1.$plus$eq(x0$1);
                     return var10;
                  }
               }

               RDDOperationGraph$.MODULE$.logWarning(new Serializable(this, x0$1) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final RDDOperationEdge x2$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Found an orphan edge in stage ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.stage$1.stageId()), this.x2$1}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.x2$1 = x2$1;
                     }
                  }
               });
               var10 = BoxedUnit.UNIT;
               return var10;
            }
         }

         public {
            this.stage$1 = stage$1;
            this.nodes$1 = nodes$1;
            this.internalEdges$1 = internalEdges$1;
            this.outgoingEdges$1 = outgoingEdges$1;
            this.incomingEdges$1 = incomingEdges$1;
         }
      });
      return new RDDOperationGraph(internalEdges, outgoingEdges, incomingEdges, rootCluster);
   }

   public String makeDotFile(RDDOperationGraph graph) {
      StringBuilder dotFile = new StringBuilder();
      dotFile.append("digraph G {\n");
      this.org$apache$spark$ui$scope$RDDOperationGraph$$makeDotSubgraph(dotFile, graph.rootCluster(), "  ");
      graph.edges().foreach(new Serializable(dotFile) {
         public static final long serialVersionUID = 0L;
         private final StringBuilder dotFile$1;

         public final StringBuilder apply(RDDOperationEdge edge) {
            return this.dotFile$1.append((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"  ", "->", ";\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(edge.fromId()), BoxesRunTime.boxToInteger(edge.toId())})));
         }

         public {
            this.dotFile$1 = dotFile$1;
         }
      });
      dotFile.append("}");
      String result = dotFile.toString();
      this.logDebug(new Serializable(result) {
         public static final long serialVersionUID = 0L;
         private final String result$1;

         public final String apply() {
            return this.result$1;
         }

         public {
            this.result$1 = result$1;
         }
      });
      return result;
   }

   public String org$apache$spark$ui$scope$RDDOperationGraph$$makeDotNode(RDDOperationNode node) {
      String isCached = node.cached() ? " [Cached]" : "";
      String label = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " [", "]", "\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{node.name(), BoxesRunTime.boxToInteger(node.id()), isCached, node.callsite()}));
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " [label=\"", "\"]"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(node.id()), StringEscapeUtils.escapeJava(label)}));
   }

   public void org$apache$spark$ui$scope$RDDOperationGraph$$makeDotSubgraph(StringBuilder subgraph, RDDOperationCluster cluster, String indent) {
      subgraph.append(indent).append((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"subgraph cluster", " {\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{cluster.id()}))).append(indent).append((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"  label=\"", "\";\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{StringEscapeUtils.escapeJava(cluster.name())})));
      cluster.childNodes().foreach(new Serializable(subgraph, indent) {
         public static final long serialVersionUID = 0L;
         private final StringBuilder subgraph$1;
         private final String indent$1;

         public final StringBuilder apply(RDDOperationNode node) {
            return this.subgraph$1.append(this.indent$1).append((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"  ", ";\\n"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{RDDOperationGraph$.MODULE$.org$apache$spark$ui$scope$RDDOperationGraph$$makeDotNode(node)})));
         }

         public {
            this.subgraph$1 = subgraph$1;
            this.indent$1 = indent$1;
         }
      });
      cluster.childClusters().foreach(new Serializable(subgraph, indent) {
         public static final long serialVersionUID = 0L;
         private final StringBuilder subgraph$1;
         private final String indent$1;

         public final void apply(RDDOperationCluster cscope) {
            RDDOperationGraph$.MODULE$.org$apache$spark$ui$scope$RDDOperationGraph$$makeDotSubgraph(this.subgraph$1, cscope, (new StringBuilder()).append((Object)this.indent$1).append((Object)"  ").toString());
         }

         public {
            this.subgraph$1 = subgraph$1;
            this.indent$1 = indent$1;
         }
      });
      subgraph.append(indent).append("}\n");
   }

   public RDDOperationGraph apply(Seq<RDDOperationEdge> edges, Seq<RDDOperationEdge> outgoingEdges, Seq<RDDOperationEdge> incomingEdges, RDDOperationCluster rootCluster) {
      return new RDDOperationGraph(edges, outgoingEdges, incomingEdges, rootCluster);
   }

   public Option<Tuple4<Seq<RDDOperationEdge>, Seq<RDDOperationEdge>, Seq<RDDOperationEdge>, RDDOperationCluster>> unapply(RDDOperationGraph x$0) {
      return (Option<Tuple4<Seq<RDDOperationEdge>, Seq<RDDOperationEdge>, Seq<RDDOperationEdge>, RDDOperationCluster>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple4(x$0.edges(), x$0.outgoingEdges(), x$0.incomingEdges(), x$0.rootCluster())));
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RDDOperationGraph$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.STAGE_CLUSTER_PREFIX = "stage_";
   }
}
