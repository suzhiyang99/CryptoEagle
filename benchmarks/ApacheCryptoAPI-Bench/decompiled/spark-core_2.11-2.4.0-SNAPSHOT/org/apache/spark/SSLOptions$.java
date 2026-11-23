package org.apache.spark;

import java.io.File;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple12;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;

public final class SSLOptions$ implements Logging, Serializable {
   public static final SSLOptions$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SSLOptions$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

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

   public SSLOptions parse(SparkConf conf, String ns, Option<SSLOptions> defaults) {
      boolean enabled = conf.getBoolean((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".enabled"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns})), defaults.exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(SSLOptions x$3) {
            return x$3.enabled();
         }
      }));
      Option port = conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".port"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(String x$4) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$4))).toInt();
         }
      });
      port.foreach(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final void apply(int p) {
            this.apply$mcVI$sp(p);
         }

         public void apply$mcVI$sp(int p) {
            Predef$.MODULE$.require(p >= 0, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Port number must be a non-negative value.";
               }
            });
         }
      });
      Option keyStore = conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".keyStore"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final File apply(String x$5) {
            return new File(x$5);
         }
      }).orElse(new Serializable(defaults) {
         public static final long serialVersionUID = 0L;
         private final Option defaults$1;

         public final Option<File> apply() {
            return this.defaults$1.<File>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<File> apply(SSLOptions x$6) {
                  return x$6.keyStore();
               }
            });
         }

         public {
            this.defaults$1 = defaults$1;
         }
      });
      Option keyStorePassword = conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".keyStorePassword"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).orElse(new Serializable(defaults) {
         public static final long serialVersionUID = 0L;
         private final Option defaults$1;

         public final Option<String> apply() {
            return this.defaults$1.<String>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<String> apply(SSLOptions x$7) {
                  return x$7.keyStorePassword();
               }
            });
         }

         public {
            this.defaults$1 = defaults$1;
         }
      });
      Option keyPassword = conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".keyPassword"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).orElse(new Serializable(defaults) {
         public static final long serialVersionUID = 0L;
         private final Option defaults$1;

         public final Option<String> apply() {
            return this.defaults$1.<String>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<String> apply(SSLOptions x$8) {
                  return x$8.keyPassword();
               }
            });
         }

         public {
            this.defaults$1 = defaults$1;
         }
      });
      Option keyStoreType = conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".keyStoreType"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).orElse(new Serializable(defaults) {
         public static final long serialVersionUID = 0L;
         private final Option defaults$1;

         public final Option<String> apply() {
            return this.defaults$1.<String>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<String> apply(SSLOptions x$9) {
                  return x$9.keyStoreType();
               }
            });
         }

         public {
            this.defaults$1 = defaults$1;
         }
      });
      boolean needClientAuth = conf.getBoolean((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".needClientAuth"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns})), defaults.exists(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(SSLOptions x$10) {
            return x$10.needClientAuth();
         }
      }));
      Option trustStore = conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".trustStore"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final File apply(String x$11) {
            return new File(x$11);
         }
      }).orElse(new Serializable(defaults) {
         public static final long serialVersionUID = 0L;
         private final Option defaults$1;

         public final Option<File> apply() {
            return this.defaults$1.<File>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<File> apply(SSLOptions x$12) {
                  return x$12.trustStore();
               }
            });
         }

         public {
            this.defaults$1 = defaults$1;
         }
      });
      Option trustStorePassword = conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".trustStorePassword"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).orElse(new Serializable(defaults) {
         public static final long serialVersionUID = 0L;
         private final Option defaults$1;

         public final Option<String> apply() {
            return this.defaults$1.<String>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<String> apply(SSLOptions x$13) {
                  return x$13.trustStorePassword();
               }
            });
         }

         public {
            this.defaults$1 = defaults$1;
         }
      });
      Option trustStoreType = conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".trustStoreType"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).orElse(new Serializable(defaults) {
         public static final long serialVersionUID = 0L;
         private final Option defaults$1;

         public final Option<String> apply() {
            return this.defaults$1.<String>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<String> apply(SSLOptions x$14) {
                  return x$14.trustStoreType();
               }
            });
         }

         public {
            this.defaults$1 = defaults$1;
         }
      });
      Option protocol = conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".protocol"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).orElse(new Serializable(defaults) {
         public static final long serialVersionUID = 0L;
         private final Option defaults$1;

         public final Option<String> apply() {
            return this.defaults$1.<String>flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Option<String> apply(SSLOptions x$15) {
                  return x$15.protocol();
               }
            });
         }

         public {
            this.defaults$1 = defaults$1;
         }
      });
      Set enabledAlgorithms = (Set)conf.getWithSubstitution((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".enabledAlgorithms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{ns}))).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Set<String> apply(String x$16) {
            return Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])x$16.split(",")).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(String x$17) {
                  return x$17.trim();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).filter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(String x$18) {
                  return (new StringOps(Predef$.MODULE$.augmentString(x$18))).nonEmpty();
               }
            })).toSet();
         }
      }).orElse(new Serializable(defaults) {
         public static final long serialVersionUID = 0L;
         private final Option defaults$1;

         public final Option<Set<String>> apply() {
            return this.defaults$1.<Set<String>>map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Set<String> apply(SSLOptions x$19) {
                  return x$19.enabledAlgorithms();
               }
            });
         }

         public {
            this.defaults$1 = defaults$1;
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Set<String> apply() {
            return Predef$.MODULE$.Set().empty();
         }
      });
      return new SSLOptions(enabled, port, keyStore, keyStorePassword, keyPassword, keyStoreType, needClientAuth, trustStore, trustStorePassword, trustStoreType, protocol, enabledAlgorithms);
   }

   public Option<SSLOptions> parse$default$3() {
      return None$.MODULE$;
   }

   public SSLOptions apply(boolean enabled, Option<Object> port, Option<File> keyStore, Option<String> keyStorePassword, Option<String> keyPassword, Option<String> keyStoreType, boolean needClientAuth, Option<File> trustStore, Option<String> trustStorePassword, Option<String> trustStoreType, Option<String> protocol, Set<String> enabledAlgorithms) {
      return new SSLOptions(enabled, port, keyStore, keyStorePassword, keyPassword, keyStoreType, needClientAuth, trustStore, trustStorePassword, trustStoreType, protocol, enabledAlgorithms);
   }

   public Option<Tuple12<Object, Option<Object>, Option<File>, Option<String>, Option<String>, Option<String>, Object, Option<File>, Option<String>, Option<String>, Option<String>, Set<String>>> unapply(SSLOptions x$0) {
      return (Option<Tuple12<Object, Option<Object>, Option<File>, Option<String>, Option<String>, Option<String>, Object, Option<File>, Option<String>, Option<String>, Option<String>, Set<String>>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple12(BoxesRunTime.boxToBoolean(x$0.enabled()), x$0.port(), x$0.keyStore(), x$0.keyStorePassword(), x$0.keyPassword(), x$0.keyStoreType(), BoxesRunTime.boxToBoolean(x$0.needClientAuth()), x$0.trustStore(), x$0.trustStorePassword(), x$0.trustStoreType(), x$0.protocol(), x$0.enabledAlgorithms())));
   }

   public boolean $lessinit$greater$default$1() {
      return false;
   }

   public Option<Object> $lessinit$greater$default$2() {
      return None$.MODULE$;
   }

   public Option<File> $lessinit$greater$default$3() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$4() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$5() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$6() {
      return None$.MODULE$;
   }

   public boolean $lessinit$greater$default$7() {
      return false;
   }

   public Option<File> $lessinit$greater$default$8() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$9() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$10() {
      return None$.MODULE$;
   }

   public Option<String> $lessinit$greater$default$11() {
      return None$.MODULE$;
   }

   public Set<String> $lessinit$greater$default$12() {
      return Predef$.MODULE$.Set().empty();
   }

   public boolean apply$default$1() {
      return false;
   }

   public Option<Object> apply$default$2() {
      return None$.MODULE$;
   }

   public Option<File> apply$default$3() {
      return None$.MODULE$;
   }

   public Option<String> apply$default$4() {
      return None$.MODULE$;
   }

   public Option<String> apply$default$5() {
      return None$.MODULE$;
   }

   public Option<String> apply$default$6() {
      return None$.MODULE$;
   }

   public boolean apply$default$7() {
      return false;
   }

   public Option<File> apply$default$8() {
      return None$.MODULE$;
   }

   public Option<String> apply$default$9() {
      return None$.MODULE$;
   }

   public Option<String> apply$default$10() {
      return None$.MODULE$;
   }

   public Option<String> apply$default$11() {
      return None$.MODULE$;
   }

   public Set<String> apply$default$12() {
      return Predef$.MODULE$.Set().empty();
   }

   private Object readResolve() {
      return MODULE$;
   }

   private SSLOptions$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
