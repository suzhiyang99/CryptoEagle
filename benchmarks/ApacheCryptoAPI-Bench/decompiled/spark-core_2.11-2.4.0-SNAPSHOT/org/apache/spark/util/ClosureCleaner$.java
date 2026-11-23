package org.apache.spark.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.xbean.asm5.ClassReader;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.Seq;
import scala.collection.SetLike;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.Map;
import scala.collection.mutable.Map$;
import scala.collection.mutable.Set;
import scala.collection.mutable.Set$;
import scala.collection.mutable.Stack;
import scala.collection.mutable.Stack$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.ObjectRef;
import scala.runtime.TraitSetter;
import sun.reflect.ReflectionFactory;

public final class ClosureCleaner$ implements Logging {
   public static final ClosureCleaner$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new ClosureCleaner$();
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

   public ClassReader getClassReader(Class<?> cls) {
      String className = (new StringBuilder()).append((Object)cls.getName().replaceFirst("^.*\\.", "")).append((Object)".class").toString();
      InputStream resourceStream = cls.getResourceAsStream(className);
      ClassReader var10000;
      if (resourceStream == null) {
         var10000 = null;
      } else {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
         Utils$.MODULE$.copyStream(resourceStream, baos, true, Utils$.MODULE$.copyStream$default$4());
         var10000 = new ClassReader(new ByteArrayInputStream(baos.toByteArray()));
      }

      return var10000;
   }

   public boolean org$apache$spark$util$ClosureCleaner$$isClosure(Class<?> cls) {
      return cls.getName().contains("$anonfun$");
   }

   public Tuple2<List<Class<?>>, List<Object>> org$apache$spark$util$ClosureCleaner$$getOuterClassesAndObjects(Object obj) {
      Object var2 = new Object();

      Tuple2 var10000;
      try {
         Predef$.MODULE$.refArrayOps((Object[])obj.getClass().getDeclaredFields()).withFilter(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Field f) {
               boolean var3;
               label23: {
                  String var10000 = f.getName();
                  String var2 = "$outer";
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label23;
                     }
                  } else if (var10000.equals(var2)) {
                     break label23;
                  }

                  var3 = false;
                  return var3;
               }

               var3 = true;
               return var3;
            }
         }).foreach(new Serializable(obj, var2) {
            public static final long serialVersionUID = 0L;
            private final Object obj$1;
            private final Object nonLocalReturnKey1$1;

            public final void apply(Field f) {
               f.setAccessible(true);
               Object outer = f.get(this.obj$1);
               if (outer != null) {
                  if (ClosureCleaner$.MODULE$.org$apache$spark$util$ClosureCleaner$$isClosure(f.getType())) {
                     Tuple2 recurRet = ClosureCleaner$.MODULE$.org$apache$spark$util$ClosureCleaner$$getOuterClassesAndObjects(outer);
                     Object var8 = this.nonLocalReturnKey1$1;
                     Class var4 = f.getType();
                     throw new NonLocalReturnControl(var8, new Tuple2(((List)recurRet._1()).$colon$colon(var4), ((List)recurRet._2()).$colon$colon(outer)));
                  } else {
                     Object var10002 = this.nonLocalReturnKey1$1;
                     Class var6 = f.getType();
                     throw new NonLocalReturnControl(var10002, new Tuple2(Nil$.MODULE$.$colon$colon(var6), Nil$.MODULE$.$colon$colon(outer)));
                  }
               }
            }

            public {
               this.obj$1 = obj$1;
               this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
            }
         });
         var10000 = new Tuple2(Nil$.MODULE$, Nil$.MODULE$);
      } catch (NonLocalReturnControl var4) {
         if (var4.key() != var2) {
            throw var4;
         }

         var10000 = (Tuple2)var4.value();
      }

      return var10000;
   }

   private List<Class<?>> getInnerClosureClasses(Object obj) {
      Set seen = (Set)Set$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new Class[]{obj.getClass()})));
      Stack stack = (Stack)Stack$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new Class[]{obj.getClass()})));

      while(!stack.isEmpty()) {
         ClassReader cr = this.getClassReader((Class)stack.pop());
         if (cr != null) {
            Set set = Set$.MODULE$.empty();
            cr.accept(new InnerClosureFinder(set), 0);
            set.$minus$minus(seen).foreach(new Serializable(seen, stack) {
               public static final long serialVersionUID = 0L;
               private final Set seen$1;
               private final Stack stack$1;

               public final Stack<Class<?>> apply(Class<?> cls) {
                  this.seen$1.$plus$eq(cls);
                  return this.stack$1.push(cls);
               }

               public {
                  this.seen$1 = seen$1;
                  this.stack$1 = stack$1;
               }
            });
         }
      }

      return seen.$minus(obj.getClass()).toList();
   }

   private void initAccessedFields(Map<Class<?>, Set<String>> accessedFields, Seq<Class<?>> outerClasses) {
      outerClasses.foreach(new Serializable(accessedFields) {
         public static final long serialVersionUID = 0L;
         private final Map accessedFields$1;

         public final void apply(Class<?> cls) {
            Class currentClass = cls;
            Predef$.MODULE$.assert(cls != null, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "The outer class can't be null.";
               }
            });

            while(currentClass != null) {
               this.accessedFields$1.update(currentClass, Set$.MODULE$.empty());
               currentClass = currentClass.getSuperclass();
            }

         }

         public {
            this.accessedFields$1 = accessedFields$1;
         }
      });
   }

   private void setAccessedFields(Class<?> outerClass, Object clone, Object obj, Map<Class<?>, Set<String>> accessedFields) {
      ((IterableLike)accessedFields.apply(outerClass)).foreach(new Serializable(outerClass, clone, obj) {
         public static final long serialVersionUID = 0L;
         private final Class outerClass$1;
         private final Object clone$1;
         private final Object obj$2;

         public final void apply(String fieldName) {
            Field field = this.outerClass$1.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(this.obj$2);
            field.set(this.clone$1, value);
         }

         public {
            this.outerClass$1 = outerClass$1;
            this.clone$1 = clone$1;
            this.obj$2 = obj$2;
         }
      });
   }

   public Object org$apache$spark$util$ClosureCleaner$$cloneAndSetFields(Object parent, Object obj, Class<?> outerClass, Map<Class<?>, Set<String>> accessedFields) {
      Object clone = this.instantiateClass(outerClass, parent);
      Class currentClass = outerClass;
      Predef$.MODULE$.assert(outerClass != null, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "The outer class can't be null.";
         }
      });

      while(currentClass != null) {
         this.setAccessedFields(currentClass, clone, obj, accessedFields);
         currentClass = currentClass.getSuperclass();
      }

      return clone;
   }

   public void clean(Object closure, boolean checkSerializable, boolean cleanTransitively) {
      this.org$apache$spark$util$ClosureCleaner$$clean(closure, checkSerializable, cleanTransitively, Map$.MODULE$.empty());
   }

   public void org$apache$spark$util$ClosureCleaner$$clean(Object func, boolean checkSerializable, boolean cleanTransitively, Map<Class<?>, Set<String>> accessedFields) {
      if (this.org$apache$spark$util$ClosureCleaner$$isClosure(func.getClass())) {
         if (func != null) {
            this.logDebug(new Serializable(func) {
               public static final long serialVersionUID = 0L;
               private final Object func$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"+++ Cleaning closure ", " (", ") +++"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.func$1, this.func$1.getClass().getName()}));
               }

               public {
                  this.func$1 = func$1;
               }
            });
            List innerClasses = this.getInnerClosureClasses(func);
            Tuple2 var7 = this.org$apache$spark$util$ClosureCleaner$$getOuterClassesAndObjects(func);
            if (var7 != null) {
               List outerClasses = (List)var7._1();
               List outerObjects = (List)var7._2();
               Tuple2 var10 = new Tuple2(outerClasses, outerObjects);
               List outerClasses = (List)var10._1();
               List outerObjects = (List)var10._2();
               Field[] declaredFields = func.getClass().getDeclaredFields();
               Method[] declaredMethods = func.getClass().getDeclaredMethods();
               if (this.log().isDebugEnabled()) {
                  this.logDebug(new Serializable(declaredFields) {
                     public static final long serialVersionUID = 0L;
                     private final Field[] declaredFields$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)" + declared fields: ").append((Object)BoxesRunTime.boxToInteger(Predef$.MODULE$.refArrayOps((Object[])this.declaredFields$1).size())).toString();
                     }

                     public {
                        this.declaredFields$1 = declaredFields$1;
                     }
                  });
                  Predef$.MODULE$.refArrayOps((Object[])declaredFields).foreach(new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final void apply(Field f) {
                        ClosureCleaner$.MODULE$.logDebug(new Serializable(this, f) {
                           public static final long serialVersionUID = 0L;
                           private final Field f$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"     ").append((Object)this.f$1).toString();
                           }

                           public {
                              this.f$1 = f$1;
                           }
                        });
                     }
                  });
                  this.logDebug(new Serializable(declaredMethods) {
                     public static final long serialVersionUID = 0L;
                     private final Method[] declaredMethods$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)" + declared methods: ").append((Object)BoxesRunTime.boxToInteger(Predef$.MODULE$.refArrayOps((Object[])this.declaredMethods$1).size())).toString();
                     }

                     public {
                        this.declaredMethods$1 = declaredMethods$1;
                     }
                  });
                  Predef$.MODULE$.refArrayOps((Object[])declaredMethods).foreach(new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final void apply(Method m) {
                        ClosureCleaner$.MODULE$.logDebug(new Serializable(this, m) {
                           public static final long serialVersionUID = 0L;
                           private final Method m$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"     ").append((Object)this.m$1).toString();
                           }

                           public {
                              this.m$1 = m$1;
                           }
                        });
                     }
                  });
                  this.logDebug(new Serializable(innerClasses) {
                     public static final long serialVersionUID = 0L;
                     private final List innerClasses$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)" + inner classes: ").append((Object)BoxesRunTime.boxToInteger(this.innerClasses$1.size())).toString();
                     }

                     public {
                        this.innerClasses$1 = innerClasses$1;
                     }
                  });
                  innerClasses.foreach(new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final void apply(Class<?> c) {
                        ClosureCleaner$.MODULE$.logDebug(new Serializable(this, c) {
                           public static final long serialVersionUID = 0L;
                           private final Class c$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"     ").append((Object)this.c$1.getName()).toString();
                           }

                           public {
                              this.c$1 = c$1;
                           }
                        });
                     }
                  });
                  this.logDebug(new Serializable(outerClasses) {
                     public static final long serialVersionUID = 0L;
                     private final List outerClasses$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)" + outer classes: ").append((Object)BoxesRunTime.boxToInteger(this.outerClasses$1.size())).toString();
                     }

                     public {
                        this.outerClasses$1 = outerClasses$1;
                     }
                  });
                  outerClasses.foreach(new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final void apply(Class<?> c) {
                        ClosureCleaner$.MODULE$.logDebug(new Serializable(this, c) {
                           public static final long serialVersionUID = 0L;
                           private final Class c$2;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"     ").append((Object)this.c$2.getName()).toString();
                           }

                           public {
                              this.c$2 = c$2;
                           }
                        });
                     }
                  });
                  this.logDebug(new Serializable(outerObjects) {
                     public static final long serialVersionUID = 0L;
                     private final List outerObjects$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)" + outer objects: ").append((Object)BoxesRunTime.boxToInteger(this.outerObjects$1.size())).toString();
                     }

                     public {
                        this.outerObjects$1 = outerObjects$1;
                     }
                  });
                  outerObjects.foreach(new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final void apply(Object o) {
                        ClosureCleaner$.MODULE$.logDebug(new Serializable(this, o) {
                           public static final long serialVersionUID = 0L;
                           private final Object o$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)"     ").append(this.o$1).toString();
                           }

                           public {
                              this.o$1 = o$1;
                           }
                        });
                     }
                  });
               }

               this.getClassReader(func.getClass()).accept(new ReturnStatementFinder(), 0);
               if (accessedFields.isEmpty()) {
                  this.logDebug(new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" + populating accessed fields because this is the starting closure"})))).s(Nil$.MODULE$);
                     }
                  });
                  this.initAccessedFields(accessedFields, outerClasses);
                  Class var15 = func.getClass();
                  innerClasses.$colon$colon(var15).foreach(new Serializable(cleanTransitively, accessedFields) {
                     public static final long serialVersionUID = 0L;
                     private final boolean cleanTransitively$1;
                     private final Map accessedFields$2;

                     public final void apply(Class<?> cls) {
                        ClosureCleaner$.MODULE$.getClassReader(cls).accept(new FieldAccessFinder(this.accessedFields$2, this.cleanTransitively$1, FieldAccessFinder$.MODULE$.$lessinit$greater$default$3(), FieldAccessFinder$.MODULE$.$lessinit$greater$default$4()), 0);
                     }

                     public {
                        this.cleanTransitively$1 = cleanTransitively$1;
                        this.accessedFields$2 = accessedFields$2;
                     }
                  });
               }

               this.logDebug(new Serializable(accessedFields) {
                  public static final long serialVersionUID = 0L;
                  private final Map accessedFields$2;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" + fields accessed by starting closure: "})))).s(Nil$.MODULE$)).append((Object)BoxesRunTime.boxToInteger(this.accessedFields$2.size())).toString();
                  }

                  public {
                     this.accessedFields$2 = accessedFields$2;
                  }
               });
               accessedFields.foreach(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final void apply(Tuple2<Class<?>, Set<String>> f) {
                     ClosureCleaner$.MODULE$.logDebug(new Serializable(this, f) {
                        public static final long serialVersionUID = 0L;
                        private final Tuple2 f$2;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)"     ").append((Object)this.f$2).toString();
                        }

                        public {
                           this.f$2 = f$2;
                        }
                     });
                  }
               });
               ObjectRef outerPairs = ObjectRef.create(((List)outerClasses.zip(outerObjects, List$.MODULE$.canBuildFrom())).reverse());
               ObjectRef parent = ObjectRef.create((Object)null);
               if (((List)outerPairs.elem).size() > 0) {
                  Tuple2 var19 = (Tuple2)((List)outerPairs.elem).head();
                  if (var19 == null) {
                     throw new MatchError(var19);
                  }

                  Class outermostClass = (Class)var19._1();
                  Object outermostObject = var19._2();
                  Tuple2 var22 = new Tuple2(outermostClass, outermostObject);
                  Class outermostClass = (Class)var22._1();
                  Object outermostObject = var22._2();
                  if (this.org$apache$spark$util$ClosureCleaner$$isClosure(outermostClass)) {
                     this.logDebug(new Serializable(outerPairs) {
                        public static final long serialVersionUID = 0L;
                        private final ObjectRef outerPairs$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" + outermost object is a closure, so we clone it: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{((List)this.outerPairs$1.elem).head()}));
                        }

                        public {
                           this.outerPairs$1 = outerPairs$1;
                        }
                     });
                  } else if (outermostClass.getName().startsWith("$line")) {
                     this.logDebug(new Serializable(outerPairs) {
                        public static final long serialVersionUID = 0L;
                        private final ObjectRef outerPairs$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" + outermost object is a REPL line object, so we clone it: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{((List)this.outerPairs$1.elem).head()}));
                        }

                        public {
                           this.outerPairs$1 = outerPairs$1;
                        }
                     });
                  } else {
                     this.logDebug(new Serializable(outerPairs) {
                        public static final long serialVersionUID = 0L;
                        private final ObjectRef outerPairs$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)" + outermost object is not a closure or REPL line object, so do not clone it: ").append(((List)this.outerPairs$1.elem).head()).toString();
                        }

                        public {
                           this.outerPairs$1 = outerPairs$1;
                        }
                     });
                     parent.elem = (T)outermostObject;
                     outerPairs.elem = (T)((List)((List)outerPairs.elem).tail());
                  }
               } else {
                  this.logDebug(new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return " + there are no enclosing objects!";
                     }
                  });
               }

               ((List)outerPairs.elem).withFilter(new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final boolean apply(Tuple2<Class<?>, Object> check$ifrefutable$1) {
                     boolean var3;
                     if (check$ifrefutable$1 != null) {
                        var3 = true;
                     } else {
                        var3 = false;
                     }

                     return var3;
                  }
               }).foreach(new Serializable(cleanTransitively, accessedFields, parent) {
                  public static final long serialVersionUID = 0L;
                  private final boolean cleanTransitively$1;
                  private final Map accessedFields$2;
                  private final ObjectRef parent$1;

                  public final void apply(Tuple2<Class<?>, Object> x$8) {
                     if (x$8 != null) {
                        Class cls = (Class)x$8._1();
                        Object obj = x$8._2();
                        ClosureCleaner$.MODULE$.logDebug(new Serializable(this, cls, obj) {
                           public static final long serialVersionUID = 0L;
                           private final Class cls$1;
                           private final Object obj$3;

                           public final String apply() {
                              return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" + cloning the object ", " of class ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.obj$3, this.cls$1.getName()}));
                           }

                           public {
                              this.cls$1 = cls$1;
                              this.obj$3 = obj$3;
                           }
                        });
                        Object clone = ClosureCleaner$.MODULE$.org$apache$spark$util$ClosureCleaner$$cloneAndSetFields(this.parent$1.elem, obj, cls, this.accessedFields$2);
                        if (this.cleanTransitively$1 && ClosureCleaner$.MODULE$.org$apache$spark$util$ClosureCleaner$$isClosure(clone.getClass())) {
                           ClosureCleaner$.MODULE$.logDebug(new Serializable(this, cls, clone) {
                              public static final long serialVersionUID = 0L;
                              private final Class cls$1;
                              private final Object clone$2;

                              public final String apply() {
                                 return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" + cleaning cloned closure ", " recursively (", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.clone$2, this.cls$1.getName()}));
                              }

                              public {
                                 this.cls$1 = cls$1;
                                 this.clone$2 = clone$2;
                              }
                           });
                           ClosureCleaner$.MODULE$.org$apache$spark$util$ClosureCleaner$$clean(clone, false, this.cleanTransitively$1, this.accessedFields$2);
                        }

                        this.parent$1.elem = (T)clone;
                        BoxedUnit var5 = BoxedUnit.UNIT;
                     } else {
                        throw new MatchError(x$8);
                     }
                  }

                  public {
                     this.cleanTransitively$1 = cleanTransitively$1;
                     this.accessedFields$2 = accessedFields$2;
                     this.parent$1 = parent$1;
                  }
               });
               if (parent.elem != null) {
                  Field field = func.getClass().getDeclaredField("$outer");
                  field.setAccessible(true);
                  if (accessedFields.contains(func.getClass()) && !((SetLike)accessedFields.apply(func.getClass())).contains("$outer")) {
                     this.logDebug(new Serializable(parent) {
                        public static final long serialVersionUID = 0L;
                        private final ObjectRef parent$1;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" + the starting closure doesn't actually need ", ", so we null it out"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.parent$1.elem}));
                        }

                        public {
                           this.parent$1 = parent$1;
                        }
                     });
                     field.set(func, (Object)null);
                  } else {
                     field.set(func, parent.elem);
                  }
               }

               this.logDebug(new Serializable(func) {
                  public static final long serialVersionUID = 0L;
                  private final Object func$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" +++ closure ", " (", ") is now cleaned +++"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.func$1, this.func$1.getClass().getName()}));
                  }

                  public {
                     this.func$1 = func$1;
                  }
               });
               if (checkSerializable) {
                  this.ensureSerializable(func);
               }

            } else {
               throw new MatchError(var7);
            }
         }
      } else {
         this.logDebug(new Serializable(func) {
            public static final long serialVersionUID = 0L;
            private final Object func$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Expected a closure; got ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.func$1.getClass().getName()}));
            }

            public {
               this.func$1 = func$1;
            }
         });
      }
   }

   public boolean clean$default$2() {
      return true;
   }

   public boolean clean$default$3() {
      return true;
   }

   private void ensureSerializable(Object func) {
      try {
         if (SparkEnv$.MODULE$.get() != null) {
            SparkEnv$.MODULE$.get().closureSerializer().newInstance().serialize(func, ClassTag$.MODULE$.AnyRef());
         }

      } catch (Exception var3) {
         throw new SparkException("Task not serializable", var3);
      }
   }

   private Object instantiateClass(Class<?> cls, Object enclosingObject) {
      ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
      Constructor parentCtor = Object.class.getDeclaredConstructor();
      Constructor newCtor = rf.newConstructorForSerialization(cls, parentCtor);
      Object obj = newCtor.newInstance();
      if (enclosingObject != null) {
         Field field = cls.getDeclaredField("$outer");
         field.setAccessible(true);
         field.set(obj, enclosingObject);
      }

      return obj;
   }

   private ClosureCleaner$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
