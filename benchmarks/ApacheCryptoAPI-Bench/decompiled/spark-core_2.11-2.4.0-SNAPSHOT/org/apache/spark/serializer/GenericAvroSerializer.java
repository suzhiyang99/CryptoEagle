package org.apache.spark.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.commons.io.IOUtils;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.io.CompressionCodec;
import org.apache.spark.io.CompressionCodec$;
import org.apache.spark.util.Utils$;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.immutable.Map;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}e!B\u0001\u0003\u0001\tQ!!F$f]\u0016\u0014\u0018nY!we>\u001cVM]5bY&TXM\u001d\u0006\u0003\u0007\u0011\t!b]3sS\u0006d\u0017N_3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\ra1#F\u0007\u0002\u001b)\u0011abD\u0001\u0005WJLxN\u0003\u0002\u0011#\u0005\u0001Rm]8uKJL7m]8gi^\f'/\u001a\u0006\u0002%\u0005\u00191m\\7\n\u0005Qi!AC*fe&\fG.\u001b>feB\u0011acG\u0007\u0002/)\u0011\u0001$G\u0001\bO\u0016tWM]5d\u0015\tQb!\u0001\u0003bmJ|\u0017B\u0001\u000f\u0018\u000559UM\\3sS\u000e\u0014VmY8sI\"Aa\u0004\u0001B\u0001B\u0003%\u0001%A\u0004tG\",W.Y:\u0004\u0001A!\u0011e\n\u0016.\u001d\t\u0011S%D\u0001$\u0015\u0005!\u0013!B:dC2\f\u0017B\u0001\u0014$\u0003\u0019\u0001&/\u001a3fM&\u0011\u0001&\u000b\u0002\u0004\u001b\u0006\u0004(B\u0001\u0014$!\t\u00113&\u0003\u0002-G\t!Aj\u001c8h!\t\tc&\u0003\u00020S\t11\u000b\u001e:j]\u001eDQ!\r\u0001\u0005\u0002I\na\u0001P5oSRtDCA\u001a6!\t!\u0004!D\u0001\u0003\u0011\u0015q\u0002\u00071\u0001!\u0011\u001d9\u0004A1A\u0005\na\nQbY8naJ,7o]\"bG\",W#A\u001d\u0011\tiz\u0014)R\u0007\u0002w)\u0011A(P\u0001\b[V$\u0018M\u00197f\u0015\tq4%\u0001\u0006d_2dWm\u0019;j_:L!\u0001Q\u001e\u0003\u000f!\u000b7\u000f['baB\u0011!iQ\u0007\u00023%\u0011A)\u0007\u0002\u0007'\u000eDW-\\1\u0011\u0007\t2\u0005*\u0003\u0002HG\t)\u0011I\u001d:bsB\u0011!%S\u0005\u0003\u0015\u000e\u0012AAQ=uK\"1A\n\u0001Q\u0001\ne\nabY8naJ,7o]\"bG\",\u0007\u0005C\u0004O\u0001\t\u0007I\u0011B(\u0002\u001f\u0011,7m\\7qe\u0016\u001c8oQ1dQ\u0016,\u0012\u0001\u0015\t\u0005u}\n\u0016\t\u0005\u0002S/6\t1K\u0003\u0002U+\u0006\u0019a.[8\u000b\u0003Y\u000bAA[1wC&\u0011\u0001l\u0015\u0002\u000b\u0005f$XMQ;gM\u0016\u0014\bB\u0002.\u0001A\u0003%\u0001+\u0001\teK\u000e|W\u000e\u001d:fgN\u001c\u0015m\u00195fA!9A\f\u0001b\u0001\n\u0013i\u0016aC<sSR,'oQ1dQ\u0016,\u0012A\u0018\t\u0005u}\nu\f\r\u0002aQB\u0019\u0011\r\u001a4\u000e\u0003\tT!aY\r\u0002\u0005%|\u0017BA3c\u0005-!\u0015\r^;n/JLG/\u001a:\u0011\u0005\u001dDG\u0002\u0001\u0003\nS*\f\t\u0011!A\u0003\u00021\u00141a\u0018\u00132\u0011\u0019Y\u0007\u0001)A\u0005=\u0006aqO]5uKJ\u001c\u0015m\u00195fAE\u0011Q\u000e\u001d\t\u0003E9L!a\\\u0012\u0003\u000f9{G\u000f[5oOB\u0011!%]\u0005\u0003e\u000e\u00121!\u00118z\u0011\u001d!\bA1A\u0005\nU\f1B]3bI\u0016\u00148)Y2iKV\ta\u000f\u0005\u0003;\u007f\u0005;\bG\u0001=}!\r\t\u0017p_\u0005\u0003u\n\u00141\u0002R1uk6\u0014V-\u00193feB\u0011q\r \u0003\n{z\f\t\u0011!A\u0003\u00021\u00141a\u0018\u00133\u0011\u0019y\b\u0001)A\u0005m\u0006a!/Z1eKJ\u001c\u0015m\u00195fA!I\u00111\u0001\u0001C\u0002\u0013%\u0011QA\u0001\u0011M&tw-\u001a:qe&tGoQ1dQ\u0016,\"!a\u0002\u0011\tiz\u0014I\u000b\u0005\t\u0003\u0017\u0001\u0001\u0015!\u0003\u0002\b\u0005\tb-\u001b8hKJ\u0004(/\u001b8u\u0007\u0006\u001c\u0007.\u001a\u0011\t\u0013\u0005=\u0001A1A\u0005\n\u0005E\u0011aC:dQ\u0016l\u0017mQ1dQ\u0016,\"!a\u0005\u0011\tiz$&\u0011\u0005\t\u0003/\u0001\u0001\u0015!\u0003\u0002\u0014\u0005a1o\u00195f[\u0006\u001c\u0015m\u00195fA!Q\u00111\u0004\u0001\t\u0006\u0004%I!!\b\u0002\u000b\r|G-Z2\u0016\u0005\u0005}\u0001\u0003BA\u0011\u0003Ki!!a\t\u000b\u0005\r$\u0011\u0002BA\u0014\u0003G\u0011\u0001cQ8naJ,7o]5p]\u000e{G-Z2\t\u0015\u0005-\u0002\u0001#A!B\u0013\ty\"\u0001\u0004d_\u0012,7\r\t\u0005\b\u0003_\u0001A\u0011AA\u0019\u0003!\u0019w.\u001c9sKN\u001cHcA#\u00024!9\u0011QGA\u0017\u0001\u0004\t\u0015AB:dQ\u0016l\u0017\rC\u0004\u0002:\u0001!\t!a\u000f\u0002\u0015\u0011,7m\\7qe\u0016\u001c8\u000fF\u0002B\u0003{Aq!a\u0010\u00028\u0001\u0007\u0011+A\u0006tG\",W.\u0019\"zi\u0016\u001c\bbBA\"\u0001\u0011\u0005\u0011QI\u0001\u000fg\u0016\u0014\u0018.\u00197ju\u0016$\u0015\r^;n+\u0011\t9%!\u0016\u0015\r\u0005%\u0013qJA.!\r\u0011\u00131J\u0005\u0004\u0003\u001b\u001a#\u0001B+oSRD\u0001\"!\u0015\u0002B\u0001\u0007\u00111K\u0001\u0006I\u0006$X/\u001c\t\u0004O\u0006UC\u0001CA,\u0003\u0003\u0012\r!!\u0017\u0003\u0003I\u000b\"!\\\u000b\t\u0011\u0005u\u0013\u0011\ta\u0001\u0003?\naa\\;uaV$\b\u0003BA1\u0003Kj!!a\u0019\u000b\u0005\rl\u0011\u0002BA4\u0003G\u0012aaT;uaV$\bbBA6\u0001\u0011\u0005\u0011QN\u0001\u0011I\u0016\u001cXM]5bY&TX\rR1uk6$2!FA8\u0011!\t\t(!\u001bA\u0002\u0005M\u0014!B5oaV$\b\u0003BA1\u0003kJA!a\u001e\u0002d\t)\u0011J\u001c9vi\"9\u00111\u0010\u0001\u0005B\u0005u\u0014!B<sSR,G\u0003CA%\u0003\u007f\n9)!#\t\u000f9\tI\b1\u0001\u0002\u0002B\u0019A\"a!\n\u0007\u0005\u0015UB\u0001\u0003Lef|\u0007\u0002CA/\u0003s\u0002\r!a\u0018\t\u000f\u0005E\u0013\u0011\u0010a\u0001+!9\u0011Q\u0012\u0001\u0005B\u0005=\u0015\u0001\u0002:fC\u0012$r!FAI\u0003'\u000b)\nC\u0004\u000f\u0003\u0017\u0003\r!!!\t\u0011\u0005E\u00141\u0012a\u0001\u0003gB\u0001\"a&\u0002\f\u0002\u0007\u0011\u0011T\u0001\u000bI\u0006$X/\\\"mCN\u001c\b\u0003B\u0011\u0002\u001cVI1!!(*\u0005\u0015\u0019E.Y:t\u0001"
)
public class GenericAvroSerializer extends com.esotericsoftware.kryo.Serializer<GenericRecord> {
   public final Map<Object, String> org$apache$spark$serializer$GenericAvroSerializer$$schemas;
   private final HashMap<Schema, byte[]> compressCache;
   private final HashMap<ByteBuffer, Schema> decompressCache;
   private final HashMap<Schema, DatumWriter<?>> writerCache;
   private final HashMap<Schema, DatumReader<?>> readerCache;
   private final HashMap<Schema, Object> fingerprintCache;
   private final HashMap<Object, Schema> schemaCache;
   private CompressionCodec org$apache$spark$serializer$GenericAvroSerializer$$codec;
   private volatile boolean bitmap$0;

   private CompressionCodec org$apache$spark$serializer$GenericAvroSerializer$$codec$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            this.org$apache$spark$serializer$GenericAvroSerializer$$codec = CompressionCodec$.MODULE$.createCodec(SparkEnv$.MODULE$.get().conf());
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.org$apache$spark$serializer$GenericAvroSerializer$$codec;
   }

   private HashMap<Schema, byte[]> compressCache() {
      return this.compressCache;
   }

   private HashMap<ByteBuffer, Schema> decompressCache() {
      return this.decompressCache;
   }

   private HashMap<Schema, DatumWriter<?>> writerCache() {
      return this.writerCache;
   }

   private HashMap<Schema, DatumReader<?>> readerCache() {
      return this.readerCache;
   }

   private HashMap<Schema, Object> fingerprintCache() {
      return this.fingerprintCache;
   }

   private HashMap<Object, Schema> schemaCache() {
      return this.schemaCache;
   }

   public CompressionCodec org$apache$spark$serializer$GenericAvroSerializer$$codec() {
      return this.bitmap$0 ? this.org$apache$spark$serializer$GenericAvroSerializer$$codec : this.org$apache$spark$serializer$GenericAvroSerializer$$codec$lzycompute();
   }

   public byte[] compress(Schema schema) {
      return (byte[])this.compressCache().getOrElseUpdate(schema, new Serializable(this, schema) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final GenericAvroSerializer $outer;
         public final Schema schema$1;

         public final byte[] apply() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            OutputStream out = this.$outer.org$apache$spark$serializer$GenericAvroSerializer$$codec().compressedOutputStream(bos);
            Utils$.MODULE$.tryWithSafeFinally(new Serializable(this, out) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final OutputStream out$1;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.out$1.write(this.$outer.schema$1.toString().getBytes(StandardCharsets.UTF_8));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.out$1 = out$1;
                  }
               }
            }, new Serializable(this, out) {
               public static final long serialVersionUID = 0L;
               private final OutputStream out$1;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.out$1.close();
               }

               public {
                  this.out$1 = out$1;
               }
            });
            return bos.toByteArray();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.schema$1 = schema$1;
            }
         }
      });
   }

   public Schema decompress(ByteBuffer schemaBytes) {
      return (Schema)this.decompressCache().getOrElseUpdate(schemaBytes, new Serializable(this, schemaBytes) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final GenericAvroSerializer $outer;
         private final ByteBuffer schemaBytes$1;

         public final Schema apply() {
            ByteArrayInputStream bis = new ByteArrayInputStream(this.schemaBytes$1.array(), this.schemaBytes$1.arrayOffset() + this.schemaBytes$1.position(), this.schemaBytes$1.remaining());
            InputStream in = this.$outer.org$apache$spark$serializer$GenericAvroSerializer$$codec().compressedInputStream(bis);
            byte[] bytes = (byte[])Utils$.MODULE$.tryWithSafeFinally(new Serializable(this, in) {
               public static final long serialVersionUID = 0L;
               private final InputStream in$1;

               public final byte[] apply() {
                  return IOUtils.toByteArray(this.in$1);
               }

               public {
                  this.in$1 = in$1;
               }
            }, new Serializable(this, in) {
               public static final long serialVersionUID = 0L;
               private final InputStream in$1;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.in$1.close();
               }

               public {
                  this.in$1 = in$1;
               }
            });
            return (new Schema.Parser()).parse(new String(bytes, StandardCharsets.UTF_8));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.schemaBytes$1 = schemaBytes$1;
            }
         }
      });
   }

   public <R extends GenericRecord> void serializeDatum(R datum, Output output) {
      BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, (BinaryEncoder)null);
      Schema schema = datum.getSchema();
      long fingerprint = BoxesRunTime.unboxToLong(this.fingerprintCache().getOrElseUpdate(schema, new Serializable(this, schema) {
         public static final long serialVersionUID = 0L;
         private final Schema schema$2;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return SchemaNormalization.parsingFingerprint64(this.schema$2);
         }

         public {
            this.schema$2 = schema$2;
         }
      }));
      Option var7 = this.org$apache$spark$serializer$GenericAvroSerializer$$schemas.get(BoxesRunTime.boxToLong(fingerprint));
      if (var7 instanceof Some) {
         output.writeBoolean(true);
         output.writeLong(fingerprint);
         BoxedUnit var8 = BoxedUnit.UNIT;
      } else {
         if (!None$.MODULE$.equals(var7)) {
            throw new MatchError(var7);
         }

         output.writeBoolean(false);
         byte[] compressedSchema = this.compress(schema);
         output.writeInt(compressedSchema.length);
         output.writeBytes(compressedSchema);
         BoxedUnit var10 = BoxedUnit.UNIT;
      }

      ((DatumWriter)this.writerCache().getOrElseUpdate(schema, new Serializable(this, schema) {
         public static final long serialVersionUID = 0L;
         private final Schema schema$2;

         public final DatumWriter<Object> apply() {
            return GenericData.get().createDatumWriter(this.schema$2);
         }

         public {
            this.schema$2 = schema$2;
         }
      })).write(datum, encoder);
      encoder.flush();
   }

   public GenericRecord deserializeDatum(Input input) {
      Schema var10000;
      if (input.readBoolean()) {
         long fingerprint = input.readLong();
         var10000 = (Schema)this.schemaCache().getOrElseUpdate(BoxesRunTime.boxToLong(fingerprint), new Serializable(this, fingerprint) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final GenericAvroSerializer $outer;
            private final long fingerprint$1;

            public final Schema apply() {
               Option var1 = this.$outer.org$apache$spark$serializer$GenericAvroSerializer$$schemas.get(BoxesRunTime.boxToLong(this.fingerprint$1));
               if (var1 instanceof Some) {
                  Some var2 = (Some)var1;
                  String s = (String)var2.x();
                  Schema var4 = (new Schema.Parser()).parse(s);
                  return var4;
               } else if (None$.MODULE$.equals(var1)) {
                  throw new SparkException((new StringBuilder()).append((Object)"Error reading attempting to read avro data -- encountered an unknown ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"fingerprint: ", ", not sure what schema to use.  This could happen "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.fingerprint$1)}))).append((Object)"if you registered additional schemas after starting your spark context.").toString());
               } else {
                  throw new MatchError(var1);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.fingerprint$1 = fingerprint$1;
               }
            }
         });
      } else {
         int length = input.readInt();
         var10000 = this.decompress(ByteBuffer.wrap(input.readBytes(length)));
      }

      Schema schema = var10000;
      BinaryDecoder decoder = DecoderFactory.get().directBinaryDecoder(input, (BinaryDecoder)null);
      return (GenericRecord)((DatumReader)this.readerCache().getOrElseUpdate(schema, new Serializable(this, schema) {
         public static final long serialVersionUID = 0L;
         private final Schema schema$3;

         public final DatumReader<Object> apply() {
            return GenericData.get().createDatumReader(this.schema$3);
         }

         public {
            this.schema$3 = schema$3;
         }
      })).read((Object)null, decoder);
   }

   public void write(Kryo kryo, Output output, GenericRecord datum) {
      this.serializeDatum(datum, output);
   }

   public GenericRecord read(Kryo kryo, Input input, Class<GenericRecord> datumClass) {
      return this.deserializeDatum(input);
   }

   public GenericAvroSerializer(Map<Object, String> schemas) {
      this.org$apache$spark$serializer$GenericAvroSerializer$$schemas = schemas;
      this.compressCache = new HashMap<Schema, byte[]>();
      this.decompressCache = new HashMap<ByteBuffer, Schema>();
      this.writerCache = new HashMap<Schema, DatumWriter<?>>();
      this.readerCache = new HashMap<Schema, DatumReader<?>>();
      this.fingerprintCache = new HashMap<Schema, Object>();
      this.schemaCache = new HashMap<Object, Schema>();
   }
}
