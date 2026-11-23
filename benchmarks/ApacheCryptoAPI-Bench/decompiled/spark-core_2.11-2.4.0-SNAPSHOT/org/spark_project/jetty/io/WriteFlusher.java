package org.spark_project.jetty.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritePendingException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class WriteFlusher {
   private static final Logger LOG = Log.getLogger(WriteFlusher.class);
   private static final boolean DEBUG;
   private static final ByteBuffer[] EMPTY_BUFFERS;
   private static final EnumMap<StateType, Set<StateType>> __stateTransitions;
   private static final State __IDLE;
   private static final State __WRITING;
   private static final State __COMPLETING;
   private final EndPoint _endPoint;
   private final AtomicReference<State> _state = new AtomicReference();

   protected WriteFlusher(EndPoint endPoint) {
      this._state.set(__IDLE);
      this._endPoint = endPoint;
   }

   private boolean updateState(State previous, State next) {
      if (!this.isTransitionAllowed(previous, next)) {
         throw new IllegalStateException();
      } else {
         boolean updated = this._state.compareAndSet(previous, next);
         if (DEBUG) {
            LOG.debug("update {}:{}{}{}", this, previous, updated ? "-->" : "!->", next);
         }

         return updated;
      }
   }

   private void fail(PendingState pending) {
      State current = (State)this._state.get();
      if (current.getType() == WriteFlusher.StateType.FAILED) {
         FailedState failed = (FailedState)current;
         if (this.updateState(failed, __IDLE)) {
            pending.fail(failed.getCause());
            return;
         }
      }

      throw new IllegalStateException();
   }

   private void ignoreFail() {
      for(State current = (State)this._state.get(); current.getType() == WriteFlusher.StateType.FAILED; current = (State)this._state.get()) {
         if (this.updateState(current, __IDLE)) {
            return;
         }
      }

   }

   private boolean isTransitionAllowed(State currentState, State newState) {
      Set<StateType> allowedNewStateTypes = (Set)__stateTransitions.get(currentState.getType());
      if (!allowedNewStateTypes.contains(newState.getType())) {
         LOG.warn("{}: {} -> {} not allowed", this, currentState, newState);
         return false;
      } else {
         return true;
      }
   }

   public boolean isCallbackNonBlocking() {
      State s = (State)this._state.get();
      return s instanceof PendingState && ((PendingState)s).isCallbackNonBlocking();
   }

   protected abstract void onIncompleteFlush();

   public void write(Callback callback, ByteBuffer... buffers) throws WritePendingException {
      if (DEBUG) {
         LOG.debug("write: {} {}", this, BufferUtil.toDetailString(buffers));
      }

      if (!this.updateState(__IDLE, __WRITING)) {
         throw new WritePendingException();
      } else {
         try {
            buffers = this.flush(buffers);
            if (buffers != null) {
               if (DEBUG) {
                  LOG.debug("flushed incomplete");
               }

               PendingState pending = new PendingState(buffers, callback);
               if (this.updateState(__WRITING, pending)) {
                  this.onIncompleteFlush();
               } else {
                  this.fail(pending);
               }

               return;
            }

            if (!this.updateState(__WRITING, __IDLE)) {
               this.ignoreFail();
            }

            if (callback != null) {
               callback.succeeded();
            }
         } catch (IOException var4) {
            if (DEBUG) {
               LOG.debug("write exception", (Throwable)var4);
            }

            if (this.updateState(__WRITING, __IDLE)) {
               if (callback != null) {
                  callback.failed(var4);
               }
            } else {
               this.fail(new PendingState(buffers, callback));
            }
         }

      }
   }

   public void completeWrite() {
      if (DEBUG) {
         LOG.debug("completeWrite: {}", this);
      }

      State previous = (State)this._state.get();
      if (previous.getType() == WriteFlusher.StateType.PENDING) {
         PendingState pending = (PendingState)previous;
         if (this.updateState(pending, __COMPLETING)) {
            try {
               ByteBuffer[] buffers = pending.getBuffers();
               buffers = this.flush(buffers);
               if (buffers != null) {
                  if (DEBUG) {
                     LOG.debug("flushed incomplete {}", BufferUtil.toDetailString(buffers));
                  }

                  if (buffers != pending.getBuffers()) {
                     pending = new PendingState(buffers, pending._callback);
                  }

                  if (this.updateState(__COMPLETING, pending)) {
                     this.onIncompleteFlush();
                  } else {
                     this.fail(pending);
                  }

                  return;
               }

               if (!this.updateState(__COMPLETING, __IDLE)) {
                  this.ignoreFail();
               }

               pending.complete();
            } catch (IOException var4) {
               if (DEBUG) {
                  LOG.debug("completeWrite exception", (Throwable)var4);
               }

               if (this.updateState(__COMPLETING, __IDLE)) {
                  pending.fail(var4);
               } else {
                  this.fail(pending);
               }
            }

         }
      }
   }

   protected ByteBuffer[] flush(ByteBuffer[] buffers) throws IOException {
      boolean progress = true;

      while(progress && buffers != null) {
         int before = buffers.length == 0 ? 0 : buffers[0].remaining();
         boolean flushed = this._endPoint.flush(buffers);
         int r = buffers.length == 0 ? 0 : buffers[0].remaining();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Flushed={} {}/{}+{} {}", flushed, before - r, before, buffers.length - 1, this);
         }

         if (flushed) {
            return null;
         }

         progress = before != r;

         int not_empty;
         for(not_empty = 0; r == 0; r = buffers[not_empty].remaining()) {
            ++not_empty;
            if (not_empty == buffers.length) {
               buffers = null;
               not_empty = 0;
               break;
            }

            progress = true;
         }

         if (not_empty > 0) {
            buffers = (ByteBuffer[])Arrays.copyOfRange(buffers, not_empty, buffers.length);
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("!fully flushed {}", this);
      }

      return buffers == null ? EMPTY_BUFFERS : buffers;
   }

   public boolean onFail(Throwable cause) {
      while(true) {
         State current = (State)this._state.get();
         switch (current.getType()) {
            case IDLE:
            case FAILED:
               if (DEBUG) {
                  LOG.debug("ignored: {} {}", this, cause);
               }

               return false;
            case PENDING:
               if (DEBUG) {
                  LOG.debug("failed: {} {}", this, cause);
               }

               PendingState pending = (PendingState)current;
               if (!this.updateState(pending, __IDLE)) {
                  break;
               }

               return pending.fail(cause);
            default:
               if (DEBUG) {
                  LOG.debug("failed: {} {}", this, cause);
               }

               if (this.updateState(current, new FailedState(cause))) {
                  return false;
               }
         }
      }
   }

   public void onClose() {
      this.onFail(new ClosedChannelException());
   }

   boolean isIdle() {
      return ((State)this._state.get()).getType() == WriteFlusher.StateType.IDLE;
   }

   public boolean isInProgress() {
      switch (((State)this._state.get()).getType()) {
         case PENDING:
         case WRITING:
         case COMPLETING:
            return true;
         default:
            return false;
      }
   }

   public String toString() {
      return String.format("WriteFlusher@%x{%s}", this.hashCode(), this._state.get());
   }

   public String toStateString() {
      switch (((State)this._state.get()).getType()) {
         case IDLE:
            return "-";
         case FAILED:
            return "F";
         case PENDING:
            return "P";
         case WRITING:
            return "W";
         case COMPLETING:
            return "C";
         default:
            return "?";
      }
   }

   static {
      DEBUG = LOG.isDebugEnabled();
      EMPTY_BUFFERS = new ByteBuffer[]{BufferUtil.EMPTY_BUFFER};
      __stateTransitions = new EnumMap(StateType.class);
      __IDLE = new IdleState();
      __WRITING = new WritingState();
      __COMPLETING = new CompletingState();
      __stateTransitions.put(WriteFlusher.StateType.IDLE, EnumSet.of(WriteFlusher.StateType.WRITING));
      __stateTransitions.put(WriteFlusher.StateType.WRITING, EnumSet.of(WriteFlusher.StateType.IDLE, WriteFlusher.StateType.PENDING, WriteFlusher.StateType.FAILED));
      __stateTransitions.put(WriteFlusher.StateType.PENDING, EnumSet.of(WriteFlusher.StateType.COMPLETING, WriteFlusher.StateType.IDLE));
      __stateTransitions.put(WriteFlusher.StateType.COMPLETING, EnumSet.of(WriteFlusher.StateType.IDLE, WriteFlusher.StateType.PENDING, WriteFlusher.StateType.FAILED));
      __stateTransitions.put(WriteFlusher.StateType.FAILED, EnumSet.of(WriteFlusher.StateType.IDLE));
   }

   private static enum StateType {
      IDLE,
      WRITING,
      PENDING,
      COMPLETING,
      FAILED;
   }

   private static class State {
      private final StateType _type;

      private State(StateType stateType) {
         this._type = stateType;
      }

      public StateType getType() {
         return this._type;
      }

      public String toString() {
         return String.format("%s", this._type);
      }
   }

   private static class IdleState extends State {
      private IdleState() {
         super(WriteFlusher.StateType.IDLE, null);
      }
   }

   private static class WritingState extends State {
      private WritingState() {
         super(WriteFlusher.StateType.WRITING, null);
      }
   }

   private static class FailedState extends State {
      private final Throwable _cause;

      private FailedState(Throwable cause) {
         super(WriteFlusher.StateType.FAILED, null);
         this._cause = cause;
      }

      public Throwable getCause() {
         return this._cause;
      }
   }

   private static class CompletingState extends State {
      private CompletingState() {
         super(WriteFlusher.StateType.COMPLETING, null);
      }
   }

   private class PendingState extends State {
      private final Callback _callback;
      private final ByteBuffer[] _buffers;

      private PendingState(ByteBuffer[] buffers, Callback callback) {
         super(WriteFlusher.StateType.PENDING, null);
         this._buffers = buffers;
         this._callback = callback;
      }

      public ByteBuffer[] getBuffers() {
         return this._buffers;
      }

      protected boolean fail(Throwable cause) {
         if (this._callback != null) {
            this._callback.failed(cause);
            return true;
         } else {
            return false;
         }
      }

      protected void complete() {
         if (this._callback != null) {
            this._callback.succeeded();
         }

      }

      boolean isCallbackNonBlocking() {
         return this._callback != null && this._callback.isNonBlocking();
      }
   }
}
