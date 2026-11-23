package org.apache.spark;

import java.lang.reflect.Method;
import scala.Function1;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.util.Try;

public final class FutureAction$ {
   public static final FutureAction$ MODULE$;
   private final Method transformTryMethod;
   private final Method transformWithTryMethod;

   static {
      new FutureAction$();
   }

   private Method transformTryMethod() {
      return this.transformTryMethod;
   }

   private Method transformWithTryMethod() {
      return this.transformWithTryMethod;
   }

   public <T, S> Future<S> transform(Future<T> future, Function1<Try<T>, Try<S>> f, ExecutionContext executor) {
      return (Future)this.transformTryMethod().invoke(future, f, executor);
   }

   public <T, S> Future<S> transformWith(Future<T> future, Function1<Try<T>, Future<S>> f, ExecutionContext executor) {
      return (Future)this.transformWithTryMethod().invoke(future, f, executor);
   }

   private final Method liftedTree1$1() {
      Method var10000;
      try {
         var10000 = Future.class.getMethod("transform", Function1.class, ExecutionContext.class);
      } catch (NoSuchMethodException var1) {
         var10000 = null;
      }

      return var10000;
   }

   private final Method liftedTree2$1() {
      Method var10000;
      try {
         var10000 = Future.class.getMethod("transformWith", Function1.class, ExecutionContext.class);
      } catch (NoSuchMethodException var1) {
         var10000 = null;
      }

      return var10000;
   }

   private FutureAction$() {
      MODULE$ = this;
      this.transformTryMethod = this.liftedTree1$1();
      this.transformWithTryMethod = this.liftedTree2$1();
   }
}
