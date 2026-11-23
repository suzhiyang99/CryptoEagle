package org.spark_project.jetty.util;

import java.util.concurrent.atomic.AtomicReference;

public abstract class CompletableCallback implements Callback {
   private final AtomicReference<State> state;

   public CompletableCallback() {
      this.state = new AtomicReference(CompletableCallback.State.IDLE);
   }

   public void succeeded() {
      while(true) {
         State current = (State)this.state.get();
         switch (current) {
            case IDLE:
               if (!this.state.compareAndSet(current, CompletableCallback.State.SUCCEEDED)) {
                  break;
               }

               return;
            case COMPLETED:
               if (!this.state.compareAndSet(current, CompletableCallback.State.SUCCEEDED)) {
                  break;
               }

               this.resume();
               return;
            case FAILED:
               return;
            default:
               throw new IllegalStateException(current.toString());
         }
      }
   }

   public void failed(Throwable x) {
      while(true) {
         State current = (State)this.state.get();
         switch (current) {
            case IDLE:
            case COMPLETED:
               if (!this.state.compareAndSet(current, CompletableCallback.State.FAILED)) {
                  break;
               }

               this.abort(x);
               return;
            case FAILED:
               return;
            default:
               throw new IllegalStateException(current.toString());
         }
      }
   }

   public abstract void resume();

   public abstract void abort(Throwable var1);

   public boolean tryComplete() {
      while(true) {
         State current = (State)this.state.get();
         switch (current) {
            case IDLE:
               if (!this.state.compareAndSet(current, CompletableCallback.State.COMPLETED)) {
                  break;
               }

               return true;
            case COMPLETED:
            default:
               throw new IllegalStateException(current.toString());
            case FAILED:
            case SUCCEEDED:
               return false;
         }
      }
   }

   private static enum State {
      IDLE,
      SUCCEEDED,
      FAILED,
      COMPLETED;
   }
}
