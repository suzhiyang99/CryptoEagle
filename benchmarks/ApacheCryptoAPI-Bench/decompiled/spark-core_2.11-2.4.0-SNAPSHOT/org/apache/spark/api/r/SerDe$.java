package org.apache.spark.api.r;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import scala.Array$;
import scala.Function2;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.mutable.WrappedArray;
import scala.math.BigDecimal$;
import scala.reflect.ClassTag$;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;
import scala.runtime.ScalaRunTime$;

public final class SerDe$ {
   public static final SerDe$ MODULE$;
   public Function2<DataInputStream, Object, Object> org$apache$spark$api$r$SerDe$$sqlReadObject;
   private Function2<DataOutputStream, Object, Object> sqlWriteObject;

   static {
      new SerDe$();
   }

   public SerDe$ setSQLReadObject(Function2<DataInputStream, Object, Object> value) {
      this.org$apache$spark$api$r$SerDe$$sqlReadObject = value;
      return this;
   }

   public SerDe$ setSQLWriteObject(Function2<DataOutputStream, Object, Object> value) {
      this.sqlWriteObject = value;
      return this;
   }

   public char readObjectType(DataInputStream dis) {
      return (char)dis.readByte();
   }

   public Object readObject(DataInputStream dis, JVMObjectTracker jvmObjectTracker) {
      char dataType = this.readObjectType(dis);
      return this.readTypedObject(dis, dataType, jvmObjectTracker);
   }

   public Object readTypedObject(DataInputStream dis, char dataType, JVMObjectTracker jvmObjectTracker) {
      Object var10000;
      switch (dataType) {
         case 'D':
            var10000 = this.readDate(dis);
            break;
         case 'a':
            var10000 = this.readArray(dis, jvmObjectTracker);
            break;
         case 'b':
            var10000 = new Boolean(this.readBoolean(dis));
            break;
         case 'c':
            var10000 = this.readString(dis);
            break;
         case 'd':
            var10000 = new Double(this.readDouble(dis));
            break;
         case 'e':
            var10000 = this.readMap(dis, jvmObjectTracker);
            break;
         case 'i':
            var10000 = new Integer(this.readInt(dis));
            break;
         case 'j':
            var10000 = jvmObjectTracker.apply(new JVMObjectId(this.readString(dis)));
            break;
         case 'l':
            var10000 = this.readList(dis, jvmObjectTracker);
            break;
         case 'n':
            var10000 = null;
            break;
         case 'r':
            var10000 = this.readBytes(dis);
            break;
         case 't':
            var10000 = this.readTime(dis);
            break;
         default:
            if (this.org$apache$spark$api$r$SerDe$$sqlReadObject == null) {
               throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid type ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToCharacter(dataType)})));
            }

            Object obj = this.org$apache$spark$api$r$SerDe$$sqlReadObject.apply(dis, BoxesRunTime.boxToCharacter(dataType));
            if (obj == null) {
               throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid type ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToCharacter(dataType)})));
            }

            var10000 = obj;
      }

      return var10000;
   }

   public byte[] readBytes(DataInputStream in) {
      int len = this.readInt(in);
      byte[] out = new byte[len];
      in.readFully(out);
      BoxedUnit bytesRead = BoxedUnit.UNIT;
      return out;
   }

   public int readInt(DataInputStream in) {
      return in.readInt();
   }

   public double readDouble(DataInputStream in) {
      return in.readDouble();
   }

   public String readStringBytes(DataInputStream in, int len) {
      byte[] bytes = new byte[len];
      in.readFully(bytes);
      Predef$.MODULE$.assert(bytes[len - 1] == 0);
      String str = new String((byte[])Predef$.MODULE$.byteArrayOps(bytes).dropRight(1), StandardCharsets.UTF_8);
      return str;
   }

   public String readString(DataInputStream in) {
      int len = in.readInt();
      return this.readStringBytes(in, len);
   }

   public boolean readBoolean(DataInputStream in) {
      return in.readInt() != 0;
   }

   public Date readDate(DataInputStream in) {
      Date var10000;
      try {
         label26: {
            String inStr = this.readString(in);
            String var3 = "NA";
            if (inStr == null) {
               if (var3 == null) {
                  break label26;
               }
            } else if (inStr.equals(var3)) {
               break label26;
            }

            var10000 = Date.valueOf(inStr);
            return var10000;
         }

         var10000 = null;
      } catch (NegativeArraySizeException var4) {
         var10000 = null;
      }

      return var10000;
   }

   public Timestamp readTime(DataInputStream in) {
      Timestamp var10000;
      try {
         double seconds = in.readDouble();
         if (Double.isNaN(seconds)) {
            var10000 = null;
         } else {
            long sec = (long)Math.floor(seconds);
            Timestamp t = new Timestamp(sec * 1000L);
            t.setNanos((int)((seconds - (double)sec) * (double)1.0E9F));
            var10000 = t;
         }
      } catch (NegativeArraySizeException var7) {
         var10000 = null;
      }

      return var10000;
   }

   public byte[][] readBytesArr(DataInputStream in) {
      int len = this.readInt(in);
      return (byte[][])((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), len).map(new Serializable(in) {
         public static final long serialVersionUID = 0L;
         private final DataInputStream in$1;

         public final byte[] apply(int x$1) {
            return SerDe$.MODULE$.readBytes(this.in$1);
         }

         public {
            this.in$1 = in$1;
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   public int[] readIntArr(DataInputStream in) {
      int len = this.readInt(in);
      return (int[])((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), len).map(new Serializable(in) {
         public static final long serialVersionUID = 0L;
         private final DataInputStream in$2;

         public final int apply(int x$2) {
            return this.apply$mcII$sp(x$2);
         }

         public int apply$mcII$sp(int x$2) {
            return SerDe$.MODULE$.readInt(this.in$2);
         }

         public {
            this.in$2 = in$2;
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Int());
   }

   public double[] readDoubleArr(DataInputStream in) {
      int len = this.readInt(in);
      return (double[])((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), len).map(new Serializable(in) {
         public static final long serialVersionUID = 0L;
         private final DataInputStream in$3;

         public final double apply(int x$3) {
            return this.apply$mcDI$sp(x$3);
         }

         public double apply$mcDI$sp(int x$3) {
            return SerDe$.MODULE$.readDouble(this.in$3);
         }

         public {
            this.in$3 = in$3;
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Double());
   }

   public boolean[] readBooleanArr(DataInputStream in) {
      int len = this.readInt(in);
      return (boolean[])((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), len).map(new Serializable(in) {
         public static final long serialVersionUID = 0L;
         private final DataInputStream in$4;

         public final boolean apply(int x$4) {
            return this.apply$mcZI$sp(x$4);
         }

         public boolean apply$mcZI$sp(int x$4) {
            return SerDe$.MODULE$.readBoolean(this.in$4);
         }

         public {
            this.in$4 = in$4;
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Boolean());
   }

   public String[] readStringArr(DataInputStream in) {
      int len = this.readInt(in);
      return (String[])((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), len).map(new Serializable(in) {
         public static final long serialVersionUID = 0L;
         private final DataInputStream in$5;

         public final String apply(int x$5) {
            return SerDe$.MODULE$.readString(this.in$5);
         }

         public {
            this.in$5 = in$5;
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(String.class));
   }

   public Object readArray(DataInputStream dis, JVMObjectTracker jvmObjectTracker) {
      char arrType = this.readObjectType(dis);
      Object var10000;
      switch (arrType) {
         case 'a':
            int len = this.readInt(dis);
            var10000 = ((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), len).map(new Serializable(dis, jvmObjectTracker) {
               public static final long serialVersionUID = 0L;
               private final DataInputStream dis$1;
               private final JVMObjectTracker jvmObjectTracker$2;

               public final Object apply(int x$6) {
                  return SerDe$.MODULE$.readArray(this.dis$1, this.jvmObjectTracker$2);
               }

               public {
                  this.dis$1 = dis$1;
                  this.jvmObjectTracker$2 = jvmObjectTracker$2;
               }
            }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Object.class));
            break;
         case 'b':
            var10000 = this.readBooleanArr(dis);
            break;
         case 'c':
            var10000 = this.readStringArr(dis);
            break;
         case 'd':
            var10000 = this.readDoubleArr(dis);
            break;
         case 'i':
            var10000 = this.readIntArr(dis);
            break;
         case 'j':
            var10000 = Predef$.MODULE$.refArrayOps((Object[])this.readStringArr(dis)).map(new Serializable(jvmObjectTracker) {
               public static final long serialVersionUID = 0L;
               private final JVMObjectTracker jvmObjectTracker$2;

               public final Object apply(String x) {
                  return this.jvmObjectTracker$2.apply(new JVMObjectId(x));
               }

               public {
                  this.jvmObjectTracker$2 = jvmObjectTracker$2;
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Object()));
            break;
         case 'l':
            int len = this.readInt(dis);
            var10000 = ((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), len).map(new Serializable(dis, jvmObjectTracker) {
               public static final long serialVersionUID = 0L;
               private final DataInputStream dis$1;
               private final JVMObjectTracker jvmObjectTracker$2;

               public final Object[] apply(int x$7) {
                  return SerDe$.MODULE$.readList(this.dis$1, this.jvmObjectTracker$2);
               }

               public {
                  this.dis$1 = dis$1;
                  this.jvmObjectTracker$2 = jvmObjectTracker$2;
               }
            }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Object.class)));
            break;
         case 'r':
            var10000 = this.readBytesArr(dis);
            break;
         default:
            if (this.org$apache$spark$api$r$SerDe$$sqlReadObject == null) {
               throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid array type ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToCharacter(arrType)})));
            }

            int len = this.readInt(dis);
            var10000 = ((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), len).map(new Serializable(dis, arrType) {
               public static final long serialVersionUID = 0L;
               private final DataInputStream dis$1;
               private final char arrType$1;

               public final Object apply(int x$8) {
                  Object obj = SerDe$.MODULE$.org$apache$spark$api$r$SerDe$$sqlReadObject.apply(this.dis$1, BoxesRunTime.boxToCharacter(this.arrType$1));
                  if (obj == null) {
                     throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid array type ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToCharacter(this.arrType$1)})));
                  } else {
                     return obj;
                  }
               }

               public {
                  this.dis$1 = dis$1;
                  this.arrType$1 = arrType$1;
               }
            }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Object());
      }

      return var10000;
   }

   public Object[] readList(DataInputStream dis, JVMObjectTracker jvmObjectTracker) {
      int len = this.readInt(dis);
      return ((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), len).map(new Serializable(dis, jvmObjectTracker) {
         public static final long serialVersionUID = 0L;
         private final DataInputStream dis$2;
         private final JVMObjectTracker jvmObjectTracker$3;

         public final Object apply(int x$9) {
            return SerDe$.MODULE$.readObject(this.dis$2, this.jvmObjectTracker$3);
         }

         public {
            this.dis$2 = dis$2;
            this.jvmObjectTracker$3 = jvmObjectTracker$3;
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Object());
   }

   public Map<Object, Object> readMap(DataInputStream in, JVMObjectTracker jvmObjectTracker) {
      int len = this.readInt(in);
      Object var10000;
      if (len > 0) {
         Object[] keys = this.readArray(in, jvmObjectTracker);
         Object[] values = this.readList(in, jvmObjectTracker);
         var10000 = (Map)JavaConverters$.MODULE$.mapAsJavaMapConverter(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(keys).zip(Predef$.MODULE$.wrapRefArray(values), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).toMap(Predef$.MODULE$.$conforms())).asJava();
      } else {
         var10000 = new HashMap();
      }

      return (Map<Object, Object>)var10000;
   }

   public void writeType(DataOutputStream dos, String typeStr) {
      if ("void".equals(typeStr)) {
         dos.writeByte(110);
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else if ("character".equals(typeStr)) {
         dos.writeByte(99);
         BoxedUnit var5 = BoxedUnit.UNIT;
      } else if ("double".equals(typeStr)) {
         dos.writeByte(100);
         BoxedUnit var6 = BoxedUnit.UNIT;
      } else if ("integer".equals(typeStr)) {
         dos.writeByte(105);
         BoxedUnit var7 = BoxedUnit.UNIT;
      } else if ("logical".equals(typeStr)) {
         dos.writeByte(98);
         BoxedUnit var8 = BoxedUnit.UNIT;
      } else if ("date".equals(typeStr)) {
         dos.writeByte(68);
         BoxedUnit var9 = BoxedUnit.UNIT;
      } else if ("time".equals(typeStr)) {
         dos.writeByte(116);
         BoxedUnit var10 = BoxedUnit.UNIT;
      } else if ("raw".equals(typeStr)) {
         dos.writeByte(114);
         BoxedUnit var11 = BoxedUnit.UNIT;
      } else if ("array".equals(typeStr)) {
         dos.writeByte(97);
         BoxedUnit var12 = BoxedUnit.UNIT;
      } else if ("list".equals(typeStr)) {
         dos.writeByte(108);
         BoxedUnit var13 = BoxedUnit.UNIT;
      } else if ("map".equals(typeStr)) {
         dos.writeByte(101);
         BoxedUnit var14 = BoxedUnit.UNIT;
      } else {
         if (!"jobj".equals(typeStr)) {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid type ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{typeStr})));
         }

         dos.writeByte(106);
         BoxedUnit var15 = BoxedUnit.UNIT;
      }

   }

   public void org$apache$spark$api$r$SerDe$$writeKeyValue(DataOutputStream dos, Object key, Object value, JVMObjectTracker jvmObjectTracker) {
      if (key == null) {
         throw new IllegalArgumentException("Key in map can't be null.");
      } else if (key instanceof String) {
         this.writeString(dos, (String)key);
         this.writeObject(dos, value, jvmObjectTracker);
      } else {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Invalid map key type: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{key.getClass().getName()})));
      }
   }

   public void writeObject(DataOutputStream dos, Object obj, JVMObjectTracker jvmObjectTracker) {
      if (obj == null) {
         this.writeType(dos, "void");
      } else {
         Object value = obj instanceof WrappedArray ? ((WrappedArray)obj).toArray(ClassTag$.MODULE$.apply(Object.class)) : obj;
         if (value instanceof Character) {
            Character var6 = (Character)value;
            this.writeType(dos, "character");
            this.writeString(dos, var6.toString());
            BoxedUnit var7 = BoxedUnit.UNIT;
         } else if (value instanceof String) {
            String var8 = (String)value;
            this.writeType(dos, "character");
            this.writeString(dos, var8);
            BoxedUnit var36 = BoxedUnit.UNIT;
         } else if (value instanceof Long) {
            Long var9 = (Long)value;
            this.writeType(dos, "double");
            this.writeDouble(dos, (double)Predef$.MODULE$.Long2long(var9));
            BoxedUnit var37 = BoxedUnit.UNIT;
         } else if (value instanceof Float) {
            Float var10 = (Float)value;
            this.writeType(dos, "double");
            this.writeDouble(dos, (double)Predef$.MODULE$.Float2float(var10));
            BoxedUnit var38 = BoxedUnit.UNIT;
         } else if (value instanceof BigDecimal) {
            BigDecimal var11 = (BigDecimal)value;
            this.writeType(dos, "double");
            this.writeDouble(dos, BigDecimal$.MODULE$.apply(var11).toDouble());
            BoxedUnit var39 = BoxedUnit.UNIT;
         } else if (value instanceof Double) {
            Double var12 = (Double)value;
            this.writeType(dos, "double");
            this.writeDouble(dos, Predef$.MODULE$.Double2double(var12));
            BoxedUnit var40 = BoxedUnit.UNIT;
         } else if (value instanceof Byte) {
            Byte var13 = (Byte)value;
            this.writeType(dos, "integer");
            this.writeInt(dos, Predef$.MODULE$.Byte2byte(var13));
            BoxedUnit var41 = BoxedUnit.UNIT;
         } else if (value instanceof Short) {
            Short var14 = (Short)value;
            this.writeType(dos, "integer");
            this.writeInt(dos, Predef$.MODULE$.Short2short(var14));
            BoxedUnit var42 = BoxedUnit.UNIT;
         } else if (value instanceof Integer) {
            Integer var15 = (Integer)value;
            this.writeType(dos, "integer");
            this.writeInt(dos, Predef$.MODULE$.Integer2int(var15));
            BoxedUnit var43 = BoxedUnit.UNIT;
         } else if (value instanceof Boolean) {
            Boolean var16 = (Boolean)value;
            this.writeType(dos, "logical");
            this.writeBoolean(dos, Predef$.MODULE$.Boolean2boolean(var16));
            BoxedUnit var44 = BoxedUnit.UNIT;
         } else if (value instanceof Date) {
            Date var17 = (Date)value;
            this.writeType(dos, "date");
            this.writeDate(dos, var17);
            BoxedUnit var45 = BoxedUnit.UNIT;
         } else if (value instanceof Time) {
            Time var18 = (Time)value;
            this.writeType(dos, "time");
            this.writeTime(dos, var18);
            BoxedUnit var46 = BoxedUnit.UNIT;
         } else if (value instanceof Timestamp) {
            Timestamp var19 = (Timestamp)value;
            this.writeType(dos, "time");
            this.writeTime(dos, var19);
            BoxedUnit var47 = BoxedUnit.UNIT;
         } else if (value instanceof byte[]) {
            byte[] var20 = (byte[])value;
            this.writeType(dos, "raw");
            this.writeBytes(dos, var20);
            BoxedUnit var48 = BoxedUnit.UNIT;
         } else if (value instanceof char[]) {
            char[] var21 = (char[])value;
            this.writeType(dos, "array");
            this.writeStringArr(dos, (String[])Predef$.MODULE$.charArrayOps(var21).map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final String apply(char x$10) {
                  return BoxesRunTime.boxToCharacter(x$10).toString();
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class))));
            BoxedUnit var49 = BoxedUnit.UNIT;
         } else if (value instanceof short[]) {
            short[] var22 = (short[])value;
            this.writeType(dos, "array");
            this.writeIntArr(dos, (int[])Predef$.MODULE$.shortArrayOps(var22).map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final int apply(short x$11) {
                  return x$11;
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Int())));
            BoxedUnit var50 = BoxedUnit.UNIT;
         } else if (value instanceof int[]) {
            int[] var23 = (int[])value;
            this.writeType(dos, "array");
            this.writeIntArr(dos, var23);
            BoxedUnit var51 = BoxedUnit.UNIT;
         } else if (value instanceof long[]) {
            long[] var24 = (long[])value;
            this.writeType(dos, "array");
            this.writeDoubleArr(dos, (double[])Predef$.MODULE$.longArrayOps(var24).map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final double apply(long x$12) {
                  return this.apply$mcDJ$sp(x$12);
               }

               public double apply$mcDJ$sp(long x$12) {
                  return (double)x$12;
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Double())));
            BoxedUnit var52 = BoxedUnit.UNIT;
         } else if (value instanceof float[]) {
            float[] var25 = (float[])value;
            this.writeType(dos, "array");
            this.writeDoubleArr(dos, (double[])Predef$.MODULE$.floatArrayOps(var25).map(new Serializable() {
               public static final long serialVersionUID = 0L;

               public final double apply(float x$13) {
                  return this.apply$mcDF$sp(x$13);
               }

               public double apply$mcDF$sp(float x$13) {
                  return (double)x$13;
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Double())));
            BoxedUnit var53 = BoxedUnit.UNIT;
         } else if (value instanceof double[]) {
            double[] var26 = (double[])value;
            this.writeType(dos, "array");
            this.writeDoubleArr(dos, var26);
            BoxedUnit var54 = BoxedUnit.UNIT;
         } else if (value instanceof boolean[]) {
            boolean[] var27 = (boolean[])value;
            this.writeType(dos, "array");
            this.writeBooleanArr(dos, var27);
            BoxedUnit var55 = BoxedUnit.UNIT;
         } else if (value instanceof Object[]) {
            Object[] var28 = value;
            this.writeType(dos, "list");
            this.writeInt(dos, var28.length);
            Predef$.MODULE$.refArrayOps(var28).foreach(new Serializable(dos, jvmObjectTracker) {
               public static final long serialVersionUID = 0L;
               private final DataOutputStream dos$1;
               private final JVMObjectTracker jvmObjectTracker$1;

               public final void apply(Object elem) {
                  SerDe$.MODULE$.writeObject(this.dos$1, elem, this.jvmObjectTracker$1);
               }

               public {
                  this.dos$1 = dos$1;
                  this.jvmObjectTracker$1 = jvmObjectTracker$1;
               }
            });
            BoxedUnit var56 = BoxedUnit.UNIT;
         } else if (value instanceof Properties) {
            this.writeType(dos, "jobj");
            this.writeJObj(dos, value, jvmObjectTracker);
            BoxedUnit var57 = BoxedUnit.UNIT;
         } else if (value instanceof Map) {
            Map var29 = (Map)value;
            this.writeType(dos, "map");
            this.writeInt(dos, var29.size());

            for(Map.Entry entry : var29.entrySet()) {
               Object key = entry.getKey();
               Object value = entry.getValue();
               this.org$apache$spark$api$r$SerDe$$writeKeyValue(dos, key, value, jvmObjectTracker);
            }

            BoxedUnit var58 = BoxedUnit.UNIT;
         } else if (value instanceof scala.collection.Map) {
            scala.collection.Map var34 = (scala.collection.Map)value;
            this.writeType(dos, "map");
            this.writeInt(dos, var34.size());
            var34.foreach(new Serializable(dos, jvmObjectTracker) {
               public static final long serialVersionUID = 0L;
               private final DataOutputStream dos$1;
               private final JVMObjectTracker jvmObjectTracker$1;

               public final void apply(Tuple2<Object, Object> x0$1) {
                  if (x0$1 != null) {
                     Object k1 = x0$1._1();
                     Object v1 = x0$1._2();
                     SerDe$.MODULE$.org$apache$spark$api$r$SerDe$$writeKeyValue(this.dos$1, k1, v1, this.jvmObjectTracker$1);
                     BoxedUnit var5 = BoxedUnit.UNIT;
                  } else {
                     throw new MatchError(x0$1);
                  }
               }

               public {
                  this.dos$1 = dos$1;
                  this.jvmObjectTracker$1 = jvmObjectTracker$1;
               }
            });
            BoxedUnit var59 = BoxedUnit.UNIT;
         } else {
            boolean sqlWriteSucceeded = this.sqlWriteObject != null && BoxesRunTime.unboxToBoolean(this.sqlWriteObject.apply(dos, value));
            if (sqlWriteSucceeded) {
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               this.writeType(dos, "jobj");
               this.writeJObj(dos, value, jvmObjectTracker);
               BoxedUnit var60 = BoxedUnit.UNIT;
            }
         }
      }

   }

   public void writeInt(DataOutputStream out, int value) {
      out.writeInt(value);
   }

   public void writeDouble(DataOutputStream out, double value) {
      out.writeDouble(value);
   }

   public void writeBoolean(DataOutputStream out, boolean value) {
      int intValue = value ? 1 : 0;
      out.writeInt(intValue);
   }

   public void writeDate(DataOutputStream out, Date value) {
      this.writeString(out, value.toString());
   }

   public void writeTime(DataOutputStream out, Time value) {
      out.writeDouble((double)value.getTime() / (double)1000.0F);
   }

   public void writeTime(DataOutputStream out, Timestamp value) {
      out.writeDouble((double)(value.getTime() / 1000L) + (double)value.getNanos() / (double)1.0E9F);
   }

   public void writeString(DataOutputStream out, String value) {
      byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
      int len = utf8.length;
      out.writeInt(len);
      out.write(utf8, 0, len);
   }

   public void writeBytes(DataOutputStream out, byte[] value) {
      out.writeInt(value.length);
      out.write(value);
   }

   public void writeJObj(DataOutputStream out, Object value, JVMObjectTracker jvmObjectTracker) {
      JVMObjectId var5 = jvmObjectTracker.addAndGetId(value);
      if (var5 != null) {
         String id = var5.id();
         this.writeString(out, id);
      } else {
         throw new MatchError(var5);
      }
   }

   public void writeIntArr(DataOutputStream out, int[] value) {
      this.writeType(out, "integer");
      out.writeInt(value.length);
      Predef$.MODULE$.intArrayOps(value).foreach(new Serializable(out) {
         public static final long serialVersionUID = 0L;
         private final DataOutputStream out$2;

         public final void apply(int v) {
            this.apply$mcVI$sp(v);
         }

         public void apply$mcVI$sp(int v) {
            this.out$2.writeInt(v);
         }

         public {
            this.out$2 = out$2;
         }
      });
   }

   public void writeDoubleArr(DataOutputStream out, double[] value) {
      this.writeType(out, "double");
      out.writeInt(value.length);
      Predef$.MODULE$.doubleArrayOps(value).foreach(new Serializable(out) {
         public static final long serialVersionUID = 0L;
         private final DataOutputStream out$3;

         public final void apply(double v) {
            this.apply$mcVD$sp(v);
         }

         public void apply$mcVD$sp(double v) {
            this.out$3.writeDouble(v);
         }

         public {
            this.out$3 = out$3;
         }
      });
   }

   public void writeBooleanArr(DataOutputStream out, boolean[] value) {
      this.writeType(out, "logical");
      out.writeInt(value.length);
      Predef$.MODULE$.booleanArrayOps(value).foreach(new Serializable(out) {
         public static final long serialVersionUID = 0L;
         private final DataOutputStream out$4;

         public final void apply(boolean v) {
            SerDe$.MODULE$.writeBoolean(this.out$4, v);
         }

         public {
            this.out$4 = out$4;
         }
      });
   }

   public void writeStringArr(DataOutputStream out, String[] value) {
      this.writeType(out, "character");
      out.writeInt(value.length);
      Predef$.MODULE$.refArrayOps((Object[])value).foreach(new Serializable(out) {
         public static final long serialVersionUID = 0L;
         private final DataOutputStream out$1;

         public final void apply(String v) {
            SerDe$.MODULE$.writeString(this.out$1, v);
         }

         public {
            this.out$1 = out$1;
         }
      });
   }

   private SerDe$() {
      MODULE$ = this;
   }
}
