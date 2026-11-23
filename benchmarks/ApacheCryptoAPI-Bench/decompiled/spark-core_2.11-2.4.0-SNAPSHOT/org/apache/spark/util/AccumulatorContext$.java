package org.apache.spark.util;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.runtime.BoxesRunTime;

public final class AccumulatorContext$ {
   public static final AccumulatorContext$ MODULE$;
   private final ConcurrentHashMap<Object, WeakReference<AccumulatorV2<?, ?>>> originals;
   private final AtomicLong nextId;
   private final String SQL_ACCUM_IDENTIFIER;

   static {
      new AccumulatorContext$();
   }

   private ConcurrentHashMap<Object, WeakReference<AccumulatorV2<?, ?>>> originals() {
      return this.originals;
   }

   public long newId() {
      return this.nextId.getAndIncrement();
   }

   public int numAccums() {
      return this.originals().size();
   }

   public void register(AccumulatorV2<?, ?> a) {
      this.originals().putIfAbsent(BoxesRunTime.boxToLong(a.id()), new WeakReference(a));
   }

   public void remove(long id) {
      this.originals().remove(BoxesRunTime.boxToLong(id));
   }

   public Option<AccumulatorV2<?, ?>> get(long id) {
      return Option$.MODULE$.<AccumulatorV2<?, ?>>apply(this.originals().get(BoxesRunTime.boxToLong(id))).<AccumulatorV2<?, ?>>map(new Serializable(id) {
         public static final long serialVersionUID = 0L;
         private final long id$1;

         public final AccumulatorV2<?, ?> apply(WeakReference<AccumulatorV2<?, ?>> ref) {
            AccumulatorV2 acc = (AccumulatorV2)ref.get();
            if (acc == null) {
               throw new IllegalStateException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Attempted to access garbage collected accumulator ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.id$1)})));
            } else {
               return acc;
            }
         }

         public {
            this.id$1 = id$1;
         }
      });
   }

   public void clear() {
      this.originals().clear();
   }

   public String SQL_ACCUM_IDENTIFIER() {
      return this.SQL_ACCUM_IDENTIFIER;
   }

   private AccumulatorContext$() {
      MODULE$ = this;
      this.originals = new ConcurrentHashMap();
      this.nextId = new AtomicLong(0L);
      this.SQL_ACCUM_IDENTIFIER = "sql";
   }
}
