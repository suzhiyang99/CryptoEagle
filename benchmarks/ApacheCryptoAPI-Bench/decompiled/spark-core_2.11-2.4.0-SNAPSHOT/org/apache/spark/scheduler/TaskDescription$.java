package org.apache.spark.scheduler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.apache.spark.util.ByteBufferInputStream;
import org.apache.spark.util.ByteBufferOutputStream;
import org.apache.spark.util.Utils$;
import scala.MatchError;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.Map;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

public final class TaskDescription$ {
   public static final TaskDescription$ MODULE$;

   static {
      new TaskDescription$();
   }

   private void serializeStringLongMap(Map<String, Object> map, DataOutputStream dataOut) {
      dataOut.writeInt(map.size());
      map.withFilter(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, Object> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).foreach(new Serializable(dataOut) {
         public static final long serialVersionUID = 0L;
         private final DataOutputStream dataOut$1;

         public final void apply(Tuple2<String, Object> x$1) {
            if (x$1 != null) {
               String key = (String)x$1._1();
               long value = x$1._2$mcJ$sp();
               this.dataOut$1.writeUTF(key);
               this.dataOut$1.writeLong(value);
               BoxedUnit var6 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x$1);
            }
         }

         public {
            this.dataOut$1 = dataOut$1;
         }
      });
   }

   public ByteBuffer encode(TaskDescription taskDescription) {
      ByteBufferOutputStream bytesOut = new ByteBufferOutputStream(4096);
      DataOutputStream dataOut = new DataOutputStream(bytesOut);
      dataOut.writeLong(taskDescription.taskId());
      dataOut.writeInt(taskDescription.attemptNumber());
      dataOut.writeUTF(taskDescription.executorId());
      dataOut.writeUTF(taskDescription.name());
      dataOut.writeInt(taskDescription.index());
      this.serializeStringLongMap(taskDescription.addedFiles(), dataOut);
      this.serializeStringLongMap(taskDescription.addedJars(), dataOut);
      dataOut.writeInt(taskDescription.properties().size());
      ((IterableLike)JavaConverters$.MODULE$.propertiesAsScalaMapConverter(taskDescription.properties()).asScala()).foreach(new Serializable(dataOut) {
         public static final long serialVersionUID = 0L;
         private final DataOutputStream dataOut$2;

         public final void apply(Tuple2<String, String> x0$1) {
            if (x0$1 != null) {
               String key = (String)x0$1._1();
               String value = (String)x0$1._2();
               this.dataOut$2.writeUTF(key);
               byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
               this.dataOut$2.writeInt(bytes.length);
               this.dataOut$2.write(bytes);
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.dataOut$2 = dataOut$2;
         }
      });
      Utils$.MODULE$.writeByteBuffer(taskDescription.serializedTask(), (OutputStream)bytesOut);
      dataOut.close();
      bytesOut.close();
      return bytesOut.toByteBuffer();
   }

   private HashMap<String, Object> deserializeStringLongMap(DataInputStream dataIn) {
      HashMap map = new HashMap();
      int mapSize = dataIn.readInt();
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), mapSize).foreach$mVc$sp(new Serializable(dataIn, map) {
         public static final long serialVersionUID = 0L;
         private final DataInputStream dataIn$1;
         private final HashMap map$1;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.map$1.update(this.dataIn$1.readUTF(), BoxesRunTime.boxToLong(this.dataIn$1.readLong()));
         }

         public {
            this.dataIn$1 = dataIn$1;
            this.map$1 = map$1;
         }
      });
      return map;
   }

   public TaskDescription decode(ByteBuffer byteBuffer) {
      DataInputStream dataIn = new DataInputStream(new ByteBufferInputStream(byteBuffer));
      long taskId = dataIn.readLong();
      int attemptNumber = dataIn.readInt();
      String executorId = dataIn.readUTF();
      String name = dataIn.readUTF();
      int index = dataIn.readInt();
      HashMap taskFiles = this.deserializeStringLongMap(dataIn);
      HashMap taskJars = this.deserializeStringLongMap(dataIn);
      Properties properties = new Properties();
      int numProperties = dataIn.readInt();
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), numProperties).foreach(new Serializable(dataIn, properties) {
         public static final long serialVersionUID = 0L;
         private final DataInputStream dataIn$2;
         private final Properties properties$1;

         public final Object apply(int i) {
            String key = this.dataIn$2.readUTF();
            int valueLength = this.dataIn$2.readInt();
            byte[] valueBytes = new byte[valueLength];
            this.dataIn$2.readFully(valueBytes);
            return this.properties$1.setProperty(key, new String(valueBytes, StandardCharsets.UTF_8));
         }

         public {
            this.dataIn$2 = dataIn$2;
            this.properties$1 = properties$1;
         }
      });
      ByteBuffer serializedTask = byteBuffer.slice();
      return new TaskDescription(taskId, attemptNumber, executorId, name, index, taskFiles, taskJars, properties, serializedTask);
   }

   private TaskDescription$() {
      MODULE$ = this;
   }
}
