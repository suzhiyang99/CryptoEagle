package org.apache.spark.deploy;

import org.apache.spark.deploy.master.ApplicationInfo;
import org.apache.spark.deploy.master.DriverInfo;
import org.apache.spark.deploy.master.WorkerInfo;
import org.apache.spark.deploy.worker.ExecutorRunner;
import org.json4s.JsonAST;
import org.json4s.JsonDSL.;
import scala.Array$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Traversable;
import scala.collection.immutable.List$;
import scala.math.Numeric;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;

public final class JsonProtocol$ {
   public static final JsonProtocol$ MODULE$;

   static {
      new JsonProtocol$();
   }

   public JsonAST.JObject writeWorkerInfo(WorkerInfo obj) {
      return .MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("id"), obj.id()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("host"), obj.host()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      })).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("port"), BoxesRunTime.boxToInteger(obj.port())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("webuiaddress"), obj.webUiAddress()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("cores"), BoxesRunTime.boxToInteger(obj.cores())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("coresused"), BoxesRunTime.boxToInteger(obj.coresUsed())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("coresfree"), BoxesRunTime.boxToInteger(obj.coresFree())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memory"), BoxesRunTime.boxToInteger(obj.memory())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memoryused"), BoxesRunTime.boxToInteger(obj.memoryUsed())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memoryfree"), BoxesRunTime.boxToInteger(obj.memoryFree())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("state"), obj.state().toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("lastheartbeat"), BoxesRunTime.boxToLong(obj.lastHeartbeat())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return .MODULE$.long2jvalue(x);
         }
      }));
   }

   public JsonAST.JObject writeApplicationInfo(ApplicationInfo obj) {
      return .MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("id"), obj.id()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("starttime"), BoxesRunTime.boxToLong(obj.startTime())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return .MODULE$.long2jvalue(x);
         }
      })).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("name"), obj.desc().name()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("cores"), BoxesRunTime.boxToInteger(obj.coresGranted())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("user"), obj.desc().user()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memoryperslave"), BoxesRunTime.boxToInteger(obj.desc().memoryPerExecutorMB())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("submitdate"), obj.submitDate().toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("state"), obj.state().toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("duration"), BoxesRunTime.boxToLong(obj.duration())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(long x) {
            return .MODULE$.long2jvalue(x);
         }
      }));
   }

   public JsonAST.JObject writeApplicationDescription(ApplicationDescription obj) {
      return .MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("name"), obj.name()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("cores"), obj.maxCores().getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      })), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      })).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memoryperslave"), BoxesRunTime.boxToInteger(obj.memoryPerExecutorMB())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("user"), obj.user()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("command"), obj.command().toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }));
   }

   public JsonAST.JObject writeExecutorRunner(ExecutorRunner obj) {
      return .MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("id"), BoxesRunTime.boxToInteger(obj.execId())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memory"), BoxesRunTime.boxToInteger(obj.memory())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      })).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("appid"), obj.appId()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("appdesc"), this.writeApplicationDescription(obj.appDesc())));
   }

   public JsonAST.JObject writeDriverInfo(DriverInfo obj) {
      return .MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("id"), obj.id()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("starttime"), BoxesRunTime.boxToLong(obj.startTime()).toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      })).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("state"), obj.state().toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("cores"), BoxesRunTime.boxToInteger(obj.desc().cores())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memory"), BoxesRunTime.boxToInteger(obj.desc().mem())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("submitdate"), obj.submitDate().toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("worker"), obj.worker().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(WorkerInfo x$1) {
            return x$1.id();
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "None";
         }
      })), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("mainclass"), obj.desc().command().arguments().apply(2)), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }));
   }

   public JsonAST.JObject writeMasterState(DeployMessages.MasterStateResponse obj) {
      WorkerInfo[] aliveWorkers = (WorkerInfo[])Predef$.MODULE$.refArrayOps(obj.workers()).filter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(WorkerInfo x$2) {
            return x$2.isAlive();
         }
      });
      return .MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("url"), obj.uri()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("workers"), Predef$.MODULE$.refArrayOps(obj.workers()).toList().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(WorkerInfo obj) {
            return JsonProtocol$.MODULE$.writeWorkerInfo(obj);
         }
      }, List$.MODULE$.canBuildFrom())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<JsonAST.JObject> s) {
            return .MODULE$.seq2jvalue(s, Predef$.MODULE$.$conforms());
         }
      })).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("aliveworkers"), BoxesRunTime.boxToInteger(aliveWorkers.length)), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("cores"), Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(aliveWorkers).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerInfo x$3) {
            return x$3.cores();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).sum(Numeric.IntIsIntegral$.MODULE$)), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("coresused"), Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(aliveWorkers).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerInfo x$4) {
            return x$4.coresUsed();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).sum(Numeric.IntIsIntegral$.MODULE$)), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memory"), Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(aliveWorkers).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerInfo x$5) {
            return x$5.memory();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).sum(Numeric.IntIsIntegral$.MODULE$)), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memoryused"), Predef$.MODULE$.intArrayOps((int[])Predef$.MODULE$.refArrayOps(aliveWorkers).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(WorkerInfo x$6) {
            return x$6.memoryUsed();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int()))).sum(Numeric.IntIsIntegral$.MODULE$)), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("activeapps"), Predef$.MODULE$.refArrayOps(obj.activeApps()).toList().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(ApplicationInfo obj) {
            return JsonProtocol$.MODULE$.writeApplicationInfo(obj);
         }
      }, List$.MODULE$.canBuildFrom())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<JsonAST.JObject> s) {
            return .MODULE$.seq2jvalue(s, Predef$.MODULE$.$conforms());
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("completedapps"), Predef$.MODULE$.refArrayOps(obj.completedApps()).toList().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(ApplicationInfo obj) {
            return JsonProtocol$.MODULE$.writeApplicationInfo(obj);
         }
      }, List$.MODULE$.canBuildFrom())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<JsonAST.JObject> s) {
            return .MODULE$.seq2jvalue(s, Predef$.MODULE$.$conforms());
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("activedrivers"), Predef$.MODULE$.refArrayOps(obj.activeDrivers()).toList().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(DriverInfo obj) {
            return JsonProtocol$.MODULE$.writeDriverInfo(obj);
         }
      }, List$.MODULE$.canBuildFrom())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<JsonAST.JObject> s) {
            return .MODULE$.seq2jvalue(s, Predef$.MODULE$.$conforms());
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("completeddrivers"), Predef$.MODULE$.refArrayOps(obj.completedDrivers()).toList().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(DriverInfo obj) {
            return JsonProtocol$.MODULE$.writeDriverInfo(obj);
         }
      }, List$.MODULE$.canBuildFrom())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<JsonAST.JObject> s) {
            return .MODULE$.seq2jvalue(s, Predef$.MODULE$.$conforms());
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("status"), obj.status().toString()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }));
   }

   public JsonAST.JObject writeWorkerState(DeployMessages.WorkerStateResponse obj) {
      return .MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.jobject2assoc(.MODULE$.pair2Assoc(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("id"), obj.workerId()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }).$tilde(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("masterurl"), obj.masterUrl()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      })).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("masterwebuiurl"), obj.masterWebUiUrl()), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(String x) {
            return .MODULE$.string2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("cores"), BoxesRunTime.boxToInteger(obj.cores())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("coresused"), BoxesRunTime.boxToInteger(obj.coresUsed())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memory"), BoxesRunTime.boxToInteger(obj.memory())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("memoryused"), BoxesRunTime.boxToInteger(obj.memoryUsed())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JValue apply(int x) {
            return .MODULE$.int2jvalue(x);
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("executors"), obj.executors().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(ExecutorRunner obj) {
            return JsonProtocol$.MODULE$.writeExecutorRunner(obj);
         }
      }, List$.MODULE$.canBuildFrom())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<JsonAST.JObject> s) {
            return .MODULE$.seq2jvalue(s, Predef$.MODULE$.$conforms());
         }
      }))).$tilde(.MODULE$.pair2jvalue(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("finishedexecutors"), obj.finishedExecutors().map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JObject apply(ExecutorRunner obj) {
            return JsonProtocol$.MODULE$.writeExecutorRunner(obj);
         }
      }, List$.MODULE$.canBuildFrom())), new Serializable() {
         public static final long serialVersionUID = 0L;

         public final JsonAST.JArray apply(Traversable<JsonAST.JObject> s) {
            return .MODULE$.seq2jvalue(s, Predef$.MODULE$.$conforms());
         }
      }));
   }

   private JsonProtocol$() {
      MODULE$ = this;
   }
}
