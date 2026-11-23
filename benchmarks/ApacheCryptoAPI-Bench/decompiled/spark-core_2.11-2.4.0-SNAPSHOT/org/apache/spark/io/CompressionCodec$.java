package org.apache.spark.io;

import java.lang.reflect.Constructor;
import java.util.Locale;
import org.apache.spark.SparkConf;
import org.apache.spark.util.Utils$;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.collection.mutable.StringBuilder;
import scala.runtime.Nothing$;

public final class CompressionCodec$ {
   public static final CompressionCodec$ MODULE$;
   private final String org$apache$spark$io$CompressionCodec$$configKey;
   private final Map<String, String> shortCompressionCodecNames;
   private final String FALLBACK_COMPRESSION_CODEC;
   private final String DEFAULT_COMPRESSION_CODEC;
   private final Seq<String> ALL_COMPRESSION_CODECS;

   static {
      new CompressionCodec$();
   }

   public String org$apache$spark$io$CompressionCodec$$configKey() {
      return this.org$apache$spark$io$CompressionCodec$$configKey;
   }

   public boolean supportsConcatenationOfSerializedStreams(CompressionCodec codec) {
      return codec instanceof SnappyCompressionCodec || codec instanceof LZFCompressionCodec || codec instanceof LZ4CompressionCodec || codec instanceof ZStdCompressionCodec;
   }

   private Map<String, String> shortCompressionCodecNames() {
      return this.shortCompressionCodecNames;
   }

   public String getCodecName(SparkConf conf) {
      return conf.get(this.org$apache$spark$io$CompressionCodec$$configKey(), this.DEFAULT_COMPRESSION_CODEC());
   }

   public CompressionCodec createCodec(SparkConf conf) {
      return this.createCodec(conf, this.getCodecName(conf));
   }

   public CompressionCodec createCodec(SparkConf conf, String codecName) {
      String codecClass = (String)this.shortCompressionCodecNames().getOrElse(codecName.toLowerCase(Locale.ROOT), new Serializable(codecName) {
         public static final long serialVersionUID = 0L;
         private final String codecName$2;

         public final String apply() {
            return this.codecName$2;
         }

         public {
            this.codecName$2 = codecName$2;
         }
      });

      Object var10000;
      try {
         Constructor ctor = Utils$.MODULE$.classForName(codecClass).getConstructor(SparkConf.class);
         var10000 = new Some((CompressionCodec)ctor.newInstance(conf));
      } catch (Throwable var11) {
         label57: {
            boolean var7;
            if (var11 instanceof ClassNotFoundException) {
               var7 = true;
            } else if (var11 instanceof IllegalArgumentException) {
               var7 = true;
            } else {
               var7 = false;
            }

            if (!var7) {
               throw var11;
            }

            None$ var8 = None$.MODULE$;
            var10000 = var8;
            break label57;
         }
      }

      Option codec = (Option)var10000;
      return (CompressionCodec)codec.getOrElse(new Serializable(codecName) {
         public static final long serialVersionUID = 0L;
         private final String codecName$2;

         public final Nothing$ apply() {
            throw new IllegalArgumentException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Codec [", "] is not available. "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.codecName$2}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Consider setting ", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{CompressionCodec$.MODULE$.org$apache$spark$io$CompressionCodec$$configKey(), CompressionCodec$.MODULE$.FALLBACK_COMPRESSION_CODEC()}))).toString());
         }

         public {
            this.codecName$2 = codecName$2;
         }
      });
   }

   public String getShortName(String codecName) {
      return this.shortCompressionCodecNames().contains(codecName) ? codecName : (String)this.shortCompressionCodecNames().collectFirst(new Serializable(codecName) {
         public static final long serialVersionUID = 0L;
         private final String codecName$1;

         public final <A1 extends Tuple2<String, String>, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            String k;
            Object var7;
            label25: {
               if (x1 != null) {
                  k = (String)x1._1();
                  String v = (String)x1._2();
                  String var6 = this.codecName$1;
                  if (v == null) {
                     if (var6 == null) {
                        break label25;
                     }
                  } else if (v.equals(var6)) {
                     break label25;
                  }
               }

               var7 = var2.apply(x1);
               return (B1)var7;
            }

            var7 = k;
            return (B1)var7;
         }

         public final boolean isDefinedAt(Tuple2<String, String> x1) {
            boolean var5;
            label25: {
               if (x1 != null) {
                  String v = (String)x1._2();
                  String var4 = this.codecName$1;
                  if (v == null) {
                     if (var4 == null) {
                        break label25;
                     }
                  } else if (v.equals(var4)) {
                     break label25;
                  }
               }

               var5 = false;
               return var5;
            }

            var5 = true;
            return var5;
         }

         public {
            this.codecName$1 = codecName$1;
         }
      }).getOrElse(new Serializable(codecName) {
         public static final long serialVersionUID = 0L;
         private final String codecName$1;

         public final Nothing$ apply() {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No short name for codec ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.codecName$1})));
         }

         public {
            this.codecName$1 = codecName$1;
         }
      });
   }

   public String FALLBACK_COMPRESSION_CODEC() {
      return this.FALLBACK_COMPRESSION_CODEC;
   }

   public String DEFAULT_COMPRESSION_CODEC() {
      return this.DEFAULT_COMPRESSION_CODEC;
   }

   public Seq<String> ALL_COMPRESSION_CODECS() {
      return this.ALL_COMPRESSION_CODECS;
   }

   private CompressionCodec$() {
      MODULE$ = this;
      this.org$apache$spark$io$CompressionCodec$$configKey = "spark.io.compression.codec";
      this.shortCompressionCodecNames = (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("lz4"), LZ4CompressionCodec.class.getName()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("lzf"), LZFCompressionCodec.class.getName()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("snappy"), SnappyCompressionCodec.class.getName()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("zstd"), ZStdCompressionCodec.class.getName())}));
      this.FALLBACK_COMPRESSION_CODEC = "snappy";
      this.DEFAULT_COMPRESSION_CODEC = "lz4";
      this.ALL_COMPRESSION_CODECS = this.shortCompressionCodecNames().values().toSeq();
   }
}
