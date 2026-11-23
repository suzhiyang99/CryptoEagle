package org.apache.spark.scheduler;

import org.apache.hadoop.mapred.InputSplit;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.mutable.ArrayBuffer;

public final class SplitInfo$ {
   public static final SplitInfo$ MODULE$;

   static {
      new SplitInfo$();
   }

   public Seq<SplitInfo> toSplitInfo(Class<?> inputFormatClazz, String path, InputSplit mapredSplit) {
      ArrayBuffer retval = new ArrayBuffer();
      long length = mapredSplit.getLength();
      Predef$.MODULE$.refArrayOps((Object[])mapredSplit.getLocations()).foreach(new Serializable(inputFormatClazz, path, mapredSplit, retval, length) {
         public static final long serialVersionUID = 0L;
         private final Class inputFormatClazz$2;
         private final String path$2;
         private final InputSplit mapredSplit$1;
         private final ArrayBuffer retval$1;
         private final long length$1;

         public final ArrayBuffer<SplitInfo> apply(String host) {
            return this.retval$1.$plus$eq(new SplitInfo(this.inputFormatClazz$2, host, this.path$2, this.length$1, this.mapredSplit$1));
         }

         public {
            this.inputFormatClazz$2 = inputFormatClazz$2;
            this.path$2 = path$2;
            this.mapredSplit$1 = mapredSplit$1;
            this.retval$1 = retval$1;
            this.length$1 = length$1;
         }
      });
      return retval;
   }

   public Seq<SplitInfo> toSplitInfo(Class<?> inputFormatClazz, String path, org.apache.hadoop.mapreduce.InputSplit mapreduceSplit) {
      ArrayBuffer retval = new ArrayBuffer();
      long length = mapreduceSplit.getLength();
      Predef$.MODULE$.refArrayOps((Object[])mapreduceSplit.getLocations()).foreach(new Serializable(inputFormatClazz, path, mapreduceSplit, retval, length) {
         public static final long serialVersionUID = 0L;
         private final Class inputFormatClazz$1;
         private final String path$1;
         private final org.apache.hadoop.mapreduce.InputSplit mapreduceSplit$1;
         private final ArrayBuffer retval$2;
         private final long length$2;

         public final ArrayBuffer<SplitInfo> apply(String host) {
            return this.retval$2.$plus$eq(new SplitInfo(this.inputFormatClazz$1, host, this.path$1, this.length$2, this.mapreduceSplit$1));
         }

         public {
            this.inputFormatClazz$1 = inputFormatClazz$1;
            this.path$1 = path$1;
            this.mapreduceSplit$1 = mapreduceSplit$1;
            this.retval$2 = retval$2;
            this.length$2 = length$2;
         }
      });
      return retval;
   }

   private SplitInfo$() {
      MODULE$ = this;
   }
}
