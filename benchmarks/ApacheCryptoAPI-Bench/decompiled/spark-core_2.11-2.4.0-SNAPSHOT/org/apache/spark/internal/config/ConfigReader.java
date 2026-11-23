package org.apache.spark.internal.config;

import java.util.Map;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.Set;
import scala.collection.mutable.HashMap;
import scala.reflect.ScalaSignature;
import scala.util.matching.Regex;
import scala.util.matching.Regex$;

@ScalaSignature(
   bytes = "\u0006\u0001}<Q!\u0001\u0002\t\n5\tAbQ8oM&<'+Z1eKJT!a\u0001\u0003\u0002\r\r|gNZ5h\u0015\t)a!\u0001\u0005j]R,'O\\1m\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001\u0001\t\u0003\u001d=i\u0011A\u0001\u0004\u0006!\tAI!\u0005\u0002\r\u0007>tg-[4SK\u0006$WM]\n\u0003\u001fI\u0001\"a\u0005\f\u000e\u0003QQ\u0011!F\u0001\u0006g\u000e\fG.Y\u0005\u0003/Q\u0011a!\u00118z%\u00164\u0007\"B\r\u0010\t\u0003Q\u0012A\u0002\u001fj]&$h\bF\u0001\u000e\u0011\u001darB1A\u0005\nu\taAU#G?J+U#\u0001\u0010\u0011\u0005}!S\"\u0001\u0011\u000b\u0005\u0005\u0012\u0013\u0001C7bi\u000eD\u0017N\\4\u000b\u0005\r\"\u0012\u0001B;uS2L!!\n\u0011\u0003\u000bI+w-\u001a=\t\r\u001dz\u0001\u0015!\u0003\u001f\u0003\u001d\u0011VIR0S\u000b\u00022Q\u0001\u0005\u0002\u0001\r%\u001a\"\u0001\u000b\n\t\u0011-B#\u0011!Q\u0001\n1\nAaY8oMB\u0011a\"L\u0005\u0003]\t\u0011abQ8oM&<\u0007K]8wS\u0012,'\u000fC\u0003\u001aQ\u0011\u0005\u0001\u0007\u0006\u00022eA\u0011a\u0002\u000b\u0005\u0006W=\u0002\r\u0001\f\u0005\u00063!\"\t\u0001\u000e\u000b\u0003cUBQaK\u001aA\u0002Y\u0002BaN\u001e>{5\t\u0001H\u0003\u0002$s)\t!(\u0001\u0003kCZ\f\u0017B\u0001\u001f9\u0005\ri\u0015\r\u001d\t\u0003}\u0005s!aE \n\u0005\u0001#\u0012A\u0002)sK\u0012,g-\u0003\u0002C\u0007\n11\u000b\u001e:j]\u001eT!\u0001\u0011\u000b\t\u000f\u0015C#\u0019!C\u0005\r\u0006A!-\u001b8eS:<7/F\u0001H!\u0011AU*\u0010\u0017\u000e\u0003%S!AS&\u0002\u000f5,H/\u00192mK*\u0011A\nF\u0001\u000bG>dG.Z2uS>t\u0017B\u0001(J\u0005\u001dA\u0015m\u001d5NCBDa\u0001\u0015\u0015!\u0002\u00139\u0015!\u00032j]\u0012LgnZ:!\u0011\u0015\u0011\u0006\u0006\"\u0001T\u0003\u0011\u0011\u0017N\u001c3\u0015\u0007E\"f\u000bC\u0003V#\u0002\u0007Q(\u0001\u0004qe\u00164\u0017\u000e\u001f\u0005\u0006/F\u0003\r\u0001L\u0001\taJ|g/\u001b3fe\")!\u000b\u000bC\u00013R\u0019\u0011GW.\t\u000bUC\u0006\u0019A\u001f\t\u000bqC\u0006\u0019\u0001\u001c\u0002\rY\fG.^3t\u0011\u0015q\u0006\u0006\"\u0001`\u0003\u001d\u0011\u0017N\u001c3F]Z$\"!\r1\t\u000b]k\u0006\u0019\u0001\u0017\t\u000b\tDC\u0011A2\u0002\u0015\tLg\u000eZ*zgR,W\u000e\u0006\u00022I\")q+\u0019a\u0001Y!)a\r\u000bC\u0001O\u0006\u0019q-\u001a;\u0015\u0005!\\\u0007cA\nj{%\u0011!\u000e\u0006\u0002\u0007\u001fB$\u0018n\u001c8\t\u000b1,\u0007\u0019A\u001f\u0002\u0007-,\u0017\u0010C\u0003oQ\u0011\u0005q.\u0001\u0006tk\n\u001cH/\u001b;vi\u0016$\"!\u00109\t\u000bEl\u0007\u0019A\u001f\u0002\u000b%t\u0007/\u001e;\t\u000b9DC\u0011B:\u0015\u0007u\"X\u000fC\u0003re\u0002\u0007Q\bC\u0003we\u0002\u0007q/\u0001\u0005vg\u0016$'+\u001a4t!\rq\u00040P\u0005\u0003s\u000e\u00131aU3u\u0011\u0015Y\b\u0006\"\u0003}\u000319W\r^(s\t\u00164\u0017-\u001e7u)\rAWP \u0005\u0006Wi\u0004\r\u0001\f\u0005\u0006Yj\u0004\r!\u0010"
)
public class ConfigReader {
   private final ConfigProvider conf;
   private final HashMap<String, ConfigProvider> org$apache$spark$internal$config$ConfigReader$$bindings;

   public HashMap<String, ConfigProvider> org$apache$spark$internal$config$ConfigReader$$bindings() {
      return this.org$apache$spark$internal$config$ConfigReader$$bindings;
   }

   public ConfigReader bind(String prefix, ConfigProvider provider) {
      this.org$apache$spark$internal$config$ConfigReader$$bindings().update(prefix, provider);
      return this;
   }

   public ConfigReader bind(String prefix, Map<String, String> values) {
      return this.bind(prefix, new MapProvider(values));
   }

   public ConfigReader bindEnv(ConfigProvider provider) {
      return this.bind("env", provider);
   }

   public ConfigReader bindSystem(ConfigProvider provider) {
      return this.bind("system", provider);
   }

   public Option<String> get(String key) {
      return this.conf.get(key).<String>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigReader $outer;

         public final String apply(String input) {
            return this.$outer.substitute(input);
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

   public String substitute(String input) {
      return this.org$apache$spark$internal$config$ConfigReader$$substitute(input, (Set)Predef$.MODULE$.Set().apply(Nil$.MODULE$));
   }

   public String org$apache$spark$internal$config$ConfigReader$$substitute(String input, Set<String> usedRefs) {
      return input == null ? input : ConfigReader$.MODULE$.org$apache$spark$internal$config$ConfigReader$$REF_RE().replaceAllIn(input, (Function1)(new Serializable(this, input, usedRefs) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigReader $outer;
         public final String input$1;
         public final Set usedRefs$1;

         public final String apply(Regex.Match m) {
            String prefix = m.group(1);
            String name = m.group(2);
            String ref = prefix == null ? name : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{prefix, name}));
            Predef$.MODULE$.require(!this.usedRefs$1.contains(ref), new Serializable(this, ref) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final String ref$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Circular reference in ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.input$1, this.ref$1}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.ref$1 = ref$1;
                  }
               }
            });
            String replacement = (String)this.$outer.org$apache$spark$internal$config$ConfigReader$$bindings().get(prefix).flatMap(new Serializable(this, name) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final String name$1;

               public final Option<String> apply(ConfigProvider x$1) {
                  return this.$outer.org$apache$spark$internal$config$ConfigReader$$anonfun$$$outer().org$apache$spark$internal$config$ConfigReader$$getOrDefault(x$1, this.name$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.name$1 = name$1;
                  }
               }
            }).map(new Serializable(this, ref) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final String ref$1;

               public final String apply(String v) {
                  return this.$outer.org$apache$spark$internal$config$ConfigReader$$anonfun$$$outer().org$apache$spark$internal$config$ConfigReader$$substitute(v, (Set)this.$outer.usedRefs$1.$plus(this.ref$1));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.ref$1 = ref$1;
                  }
               }
            }).getOrElse(new Serializable(this, m) {
               public static final long serialVersionUID = 0L;
               private final Regex.Match m$1;

               public final String apply() {
                  return this.m$1.matched();
               }

               public {
                  this.m$1 = m$1;
               }
            });
            return Regex$.MODULE$.quoteReplacement(replacement);
         }

         // $FF: synthetic method
         public ConfigReader org$apache$spark$internal$config$ConfigReader$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.input$1 = input$1;
               this.usedRefs$1 = usedRefs$1;
            }
         }
      }));
   }

   public Option<String> org$apache$spark$internal$config$ConfigReader$$getOrDefault(ConfigProvider conf, String key) {
      return conf.get(key).<String>orElse(new Serializable(this, conf, key) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigReader $outer;
         private final ConfigProvider conf$1;
         private final String key$1;

         public final Option<String> apply() {
            ConfigEntry var1 = ConfigEntry$.MODULE$.findEntry(this.key$1);
            Object var3;
            if (var1 instanceof ConfigEntryWithDefault) {
               ConfigEntryWithDefault var2 = (ConfigEntryWithDefault)var1;
               var3 = Option$.MODULE$.apply(var2.defaultValueString());
            } else if (var1 instanceof ConfigEntryWithDefaultString) {
               ConfigEntryWithDefaultString var4 = (ConfigEntryWithDefaultString)var1;
               var3 = Option$.MODULE$.apply(var4.defaultValueString());
            } else if (var1 instanceof ConfigEntryWithDefaultFunction) {
               ConfigEntryWithDefaultFunction var5 = (ConfigEntryWithDefaultFunction)var1;
               var3 = Option$.MODULE$.apply(var5.defaultValueString());
            } else if (var1 instanceof FallbackConfigEntry) {
               FallbackConfigEntry var6 = (FallbackConfigEntry)var1;
               var3 = this.$outer.org$apache$spark$internal$config$ConfigReader$$getOrDefault(this.conf$1, var6.fallback().key());
            } else {
               var3 = None$.MODULE$;
            }

            return (Option<String>)var3;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.conf$1 = conf$1;
               this.key$1 = key$1;
            }
         }
      });
   }

   public ConfigReader(ConfigProvider conf) {
      this.conf = conf;
      this.org$apache$spark$internal$config$ConfigReader$$bindings = new HashMap<String, ConfigProvider>();
      this.bind((String)null, conf);
      this.bindEnv(new EnvProvider());
      this.bindSystem(new SystemProvider());
   }

   public ConfigReader(Map<String, String> conf) {
      this(new MapProvider(conf));
   }
}
