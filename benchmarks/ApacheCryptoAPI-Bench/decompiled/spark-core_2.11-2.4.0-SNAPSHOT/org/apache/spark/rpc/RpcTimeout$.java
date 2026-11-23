package org.apache.spark.rpc;

import org.apache.spark.SparkConf;
import org.apache.spark.util.Utils$;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.concurrent.duration.FiniteDuration;
import scala.concurrent.duration.package;
import scala.concurrent.duration.package$;
import scala.runtime.ObjectRef;

public final class RpcTimeout$ implements Serializable {
   public static final RpcTimeout$ MODULE$;

   static {
      new RpcTimeout$();
   }

   public RpcTimeout apply(SparkConf conf, String timeoutProp) {
      FiniteDuration timeout = (new package.DurationLong(package$.MODULE$.DurationLong(conf.getTimeAsSeconds(timeoutProp)))).seconds();
      return new RpcTimeout(timeout, timeoutProp);
   }

   public RpcTimeout apply(SparkConf conf, String timeoutProp, String defaultValue) {
      FiniteDuration timeout = (new package.DurationLong(package$.MODULE$.DurationLong(conf.getTimeAsSeconds(timeoutProp, defaultValue)))).seconds();
      return new RpcTimeout(timeout, timeoutProp);
   }

   public RpcTimeout apply(SparkConf conf, Seq<String> timeoutPropList, String defaultValue) {
      Predef$.MODULE$.require(timeoutPropList.nonEmpty());
      Iterator itr = timeoutPropList.iterator();
      ObjectRef foundProp = ObjectRef.create(None$.MODULE$);

      while(itr.hasNext() && ((Option)foundProp.elem).isEmpty()) {
         String propKey = (String)itr.next();
         conf.getOption(propKey).foreach(new Serializable(foundProp, propKey) {
            public static final long serialVersionUID = 0L;
            private final ObjectRef foundProp$1;
            private final String propKey$1;

            public final void apply(String prop) {
               this.foundProp$1.elem = (T)(new Some(new Tuple2(this.propKey$1, prop)));
            }

            public {
               this.foundProp$1 = foundProp$1;
               this.propKey$1 = propKey$1;
            }
         });
      }

      Tuple2 finalProp = (Tuple2)((Option)foundProp.elem).getOrElse(new Serializable(timeoutPropList, defaultValue) {
         public static final long serialVersionUID = 0L;
         private final Seq timeoutPropList$1;
         private final String defaultValue$1;

         public final Tuple2<String, String> apply() {
            return new Tuple2<String, String>(this.timeoutPropList$1.head(), this.defaultValue$1);
         }

         public {
            this.timeoutPropList$1 = timeoutPropList$1;
            this.defaultValue$1 = defaultValue$1;
         }
      });
      FiniteDuration timeout = (new package.DurationLong(package$.MODULE$.DurationLong(Utils$.MODULE$.timeStringAsSeconds((String)finalProp._2())))).seconds();
      return new RpcTimeout(timeout, (String)finalProp._1());
   }

   private Object readResolve() {
      return MODULE$;
   }

   private RpcTimeout$() {
      MODULE$ = this;
   }
}
