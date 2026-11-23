package org.spark_project.jetty.io.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import org.spark_project.jetty.io.AbstractConnection;
import org.spark_project.jetty.io.AbstractEndPoint;
import org.spark_project.jetty.io.ByteBufferPool;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.io.EofException;
import org.spark_project.jetty.io.WriteFlusher;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Callback;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.Scheduler;

public class SslConnection extends AbstractConnection {
   private static final Logger LOG = Log.getLogger(SslConnection.class);
   private final List<SslHandshakeListener> handshakeListeners = new ArrayList();
   private final ByteBufferPool _bufferPool;
   private final SSLEngine _sslEngine;
   private final DecryptedEndPoint _decryptedEndPoint;
   private ByteBuffer _decryptedInput;
   private ByteBuffer _encryptedInput;
   private ByteBuffer _encryptedOutput;
   private final boolean _encryptedDirectBuffers = true;
   private final boolean _decryptedDirectBuffers = false;
   private boolean _renegotiationAllowed;
   private int _renegotiationLimit = -1;
   private boolean _closedOutbound;
   private boolean _allowMissingCloseMessage = true;
   private final Runnable _runCompletWrite = new Runnable() {
      public void run() {
         SslConnection.this._decryptedEndPoint.getWriteFlusher().completeWrite();
      }
   };
   private final Runnable _runFillable = new Runnable() {
      public void run() {
         SslConnection.this._decryptedEndPoint.getFillInterest().fillable();
      }
   };
   private final Callback _nonBlockingReadCallback = new Callback.NonBlocking() {
      public void succeeded() {
         SslConnection.this.onFillable();
      }

      public void failed(Throwable x) {
         SslConnection.this.onFillInterestedFailed(x);
      }

      public String toString() {
         return String.format("SSLC.NBReadCB@%x{%s}", SslConnection.this.hashCode(), SslConnection.this);
      }
   };

   public SslConnection(ByteBufferPool byteBufferPool, Executor executor, EndPoint endPoint, SSLEngine sslEngine) {
      super(endPoint, executor);
      this._bufferPool = byteBufferPool;
      this._sslEngine = sslEngine;
      this._decryptedEndPoint = this.newDecryptedEndPoint();
   }

   public void addHandshakeListener(SslHandshakeListener listener) {
      this.handshakeListeners.add(listener);
   }

   public boolean removeHandshakeListener(SslHandshakeListener listener) {
      return this.handshakeListeners.remove(listener);
   }

   protected DecryptedEndPoint newDecryptedEndPoint() {
      return new DecryptedEndPoint();
   }

   public SSLEngine getSSLEngine() {
      return this._sslEngine;
   }

   public DecryptedEndPoint getDecryptedEndPoint() {
      return this._decryptedEndPoint;
   }

   public boolean isRenegotiationAllowed() {
      return this._renegotiationAllowed;
   }

   public void setRenegotiationAllowed(boolean renegotiationAllowed) {
      this._renegotiationAllowed = renegotiationAllowed;
   }

   public int getRenegotiationLimit() {
      return this._renegotiationLimit;
   }

   public void setRenegotiationLimit(int renegotiationLimit) {
      this._renegotiationLimit = renegotiationLimit;
   }

   public boolean isAllowMissingCloseMessage() {
      return this._allowMissingCloseMessage;
   }

   public void setAllowMissingCloseMessage(boolean allowMissingCloseMessage) {
      this._allowMissingCloseMessage = allowMissingCloseMessage;
   }

   public void onOpen() {
      super.onOpen();
      this.getDecryptedEndPoint().getConnection().onOpen();
   }

   public void onClose() {
      this._decryptedEndPoint.getConnection().onClose();
      super.onClose();
   }

   public void close() {
      this.getDecryptedEndPoint().getConnection().close();
   }

   public boolean onIdleExpired() {
      return this.getDecryptedEndPoint().getConnection().onIdleExpired();
   }

   public void onFillable() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onFillable enter {}", this._decryptedEndPoint);
      }

      if (this._decryptedEndPoint.isInputShutdown()) {
         this._decryptedEndPoint.close();
      }

      this._decryptedEndPoint.getFillInterest().fillable();
      boolean runComplete = false;
      synchronized(this._decryptedEndPoint) {
         if (this._decryptedEndPoint._flushRequiresFillToProgress) {
            this._decryptedEndPoint._flushRequiresFillToProgress = false;
            runComplete = true;
         }
      }

      if (runComplete) {
         this._runCompletWrite.run();
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("onFillable exit {}", this._decryptedEndPoint);
      }

   }

   public void onFillInterestedFailed(Throwable cause) {
      this._decryptedEndPoint.getFillInterest().onFail(cause);
      boolean failFlusher = false;
      synchronized(this._decryptedEndPoint) {
         if (this._decryptedEndPoint._flushRequiresFillToProgress) {
            this._decryptedEndPoint._flushRequiresFillToProgress = false;
            failFlusher = true;
         }
      }

      if (failFlusher) {
         this._decryptedEndPoint.getWriteFlusher().onFail(cause);
      }

   }

   public String toString() {
      ByteBuffer b = this._encryptedInput;
      int ei = b == null ? -1 : b.remaining();
      b = this._encryptedOutput;
      int eo = b == null ? -1 : b.remaining();
      b = this._decryptedInput;
      int di = b == null ? -1 : b.remaining();
      return String.format("SslConnection@%x{%s,eio=%d/%d,di=%d} -> %s", this.hashCode(), this._sslEngine.getHandshakeStatus(), ei, eo, di, this._decryptedEndPoint.getConnection());
   }

   public class DecryptedEndPoint extends AbstractEndPoint {
      private boolean _fillRequiresFlushToProgress;
      private boolean _flushRequiresFillToProgress;
      private boolean _cannotAcceptMoreAppDataToFlush;
      private boolean _handshaken;
      private boolean _underFlown;
      private final Callback _writeCallback = new Callback() {
         public void succeeded() {
            boolean fillable = false;
            synchronized(DecryptedEndPoint.this) {
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("write.complete {}", SslConnection.this.getEndPoint());
               }

               DecryptedEndPoint.this.releaseEncryptedOutputBuffer();
               DecryptedEndPoint.this._cannotAcceptMoreAppDataToFlush = false;
               if (DecryptedEndPoint.this._fillRequiresFlushToProgress) {
                  DecryptedEndPoint.this._fillRequiresFlushToProgress = false;
                  fillable = true;
               }
            }

            if (fillable) {
               DecryptedEndPoint.this.getFillInterest().fillable();
            }

            SslConnection.this._runCompletWrite.run();
         }

         public void failed(Throwable x) {
            final boolean fail_filler;
            synchronized(DecryptedEndPoint.this) {
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("write failed {}", SslConnection.this, x);
               }

               BufferUtil.clear(SslConnection.this._encryptedOutput);
               DecryptedEndPoint.this.releaseEncryptedOutputBuffer();
               DecryptedEndPoint.this._cannotAcceptMoreAppDataToFlush = false;
               fail_filler = DecryptedEndPoint.this._fillRequiresFlushToProgress;
               if (DecryptedEndPoint.this._fillRequiresFlushToProgress) {
                  DecryptedEndPoint.this._fillRequiresFlushToProgress = false;
               }
            }

            SslConnection.this.failedCallback(new Callback() {
               public void failed(Throwable x) {
                  if (fail_filler) {
                     DecryptedEndPoint.this.getFillInterest().onFail(x);
                  }

                  DecryptedEndPoint.this.getWriteFlusher().onFail(x);
               }
            }, x);
         }

         public boolean isNonBlocking() {
            return DecryptedEndPoint.this.getWriteFlusher().isCallbackNonBlocking();
         }
      };

      public DecryptedEndPoint() {
         super((Scheduler)null, SslConnection.this.getEndPoint().getLocalAddress(), SslConnection.this.getEndPoint().getRemoteAddress());
         super.setIdleTimeout(-1L);
      }

      public long getIdleTimeout() {
         return SslConnection.this.getEndPoint().getIdleTimeout();
      }

      public void setIdleTimeout(long idleTimeout) {
         SslConnection.this.getEndPoint().setIdleTimeout(idleTimeout);
      }

      public boolean isOpen() {
         return SslConnection.this.getEndPoint().isOpen();
      }

      protected WriteFlusher getWriteFlusher() {
         return super.getWriteFlusher();
      }

      protected void onIncompleteFlush() {
         boolean try_again = false;
         boolean write = false;
         boolean need_fill_interest = false;
         synchronized(this) {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug("onIncompleteFlush {}", SslConnection.this);
            }

            if (BufferUtil.hasContent(SslConnection.this._encryptedOutput)) {
               this._cannotAcceptMoreAppDataToFlush = true;
               write = true;
            } else if (SslConnection.this._sslEngine.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP) {
               this._flushRequiresFillToProgress = true;
               need_fill_interest = !SslConnection.this.isFillInterested();
            } else {
               try_again = true;
            }
         }

         if (write) {
            SslConnection.this.getEndPoint().write(this._writeCallback, SslConnection.this._encryptedOutput);
         } else if (need_fill_interest) {
            this.ensureFillInterested();
         } else if (try_again) {
            if (this.isOutputShutdown()) {
               this.getWriteFlusher().onClose();
            } else {
               SslConnection.this.getExecutor().execute(SslConnection.this._runCompletWrite);
            }
         }

      }

      protected void needsFillInterest() throws IOException {
         boolean write = false;
         boolean fillable;
         synchronized(this) {
            fillable = BufferUtil.hasContent(SslConnection.this._decryptedInput) || BufferUtil.hasContent(SslConnection.this._encryptedInput) && !this._underFlown;
            if (!fillable && this._fillRequiresFlushToProgress) {
               if (BufferUtil.hasContent(SslConnection.this._encryptedOutput)) {
                  this._cannotAcceptMoreAppDataToFlush = true;
                  write = true;
               } else {
                  this._fillRequiresFlushToProgress = false;
                  fillable = true;
               }
            }
         }

         if (write) {
            SslConnection.this.getEndPoint().write(this._writeCallback, SslConnection.this._encryptedOutput);
         } else if (fillable) {
            SslConnection.this.getExecutor().execute(SslConnection.this._runFillable);
         } else {
            this.ensureFillInterested();
         }

      }

      public void setConnection(Connection connection) {
         if (connection instanceof AbstractConnection) {
            AbstractConnection a = (AbstractConnection)connection;
            if (a.getInputBufferSize() < SslConnection.this._sslEngine.getSession().getApplicationBufferSize()) {
               a.setInputBufferSize(SslConnection.this._sslEngine.getSession().getApplicationBufferSize());
            }
         }

         super.setConnection(connection);
      }

      public SslConnection getSslConnection() {
         return SslConnection.this;
      }

      public int fill(ByteBuffer buffer) throws IOException {
         try {
            synchronized(this) {
               Throwable failure = null;

               int var36;
               try {
                  if (!BufferUtil.hasContent(SslConnection.this._decryptedInput)) {
                     if (SslConnection.this._encryptedInput == null) {
                        SslConnection.this._encryptedInput = SslConnection.this._bufferPool.acquire(SslConnection.this._sslEngine.getSession().getPacketBufferSize(), true);
                     } else {
                        BufferUtil.compact(SslConnection.this._encryptedInput);
                     }

                     ByteBuffer app_in;
                     if (BufferUtil.space(buffer) > SslConnection.this._sslEngine.getSession().getApplicationBufferSize()) {
                        app_in = buffer;
                     } else if (SslConnection.this._decryptedInput == null) {
                        app_in = SslConnection.this._decryptedInput = SslConnection.this._bufferPool.acquire(SslConnection.this._sslEngine.getSession().getApplicationBufferSize(), false);
                     } else {
                        app_in = SslConnection.this._decryptedInput;
                     }

                     label902:
                     while(true) {
                        int net_filled = SslConnection.this.getEndPoint().fill(SslConnection.this._encryptedInput);
                        if (net_filled > 0 && !this._handshaken && SslConnection.this._sslEngine.isOutboundDone()) {
                           throw new SSLHandshakeException("Closed during handshake");
                        }

                        while(true) {
                           int pos = BufferUtil.flipToFill(app_in);

                           SSLEngineResult unwrapResult;
                           try {
                              unwrapResult = SslConnection.this._sslEngine.unwrap(SslConnection.this._encryptedInput, app_in);
                           } finally {
                              BufferUtil.flipToFlush(app_in, pos);
                           }

                           if (SslConnection.LOG.isDebugEnabled()) {
                              SslConnection.LOG.debug("net={} unwrap {} {}", net_filled, unwrapResult.toString().replace('\n', ' '), SslConnection.this);
                              SslConnection.LOG.debug("filled {} {}", BufferUtil.toHexSummary(buffer), SslConnection.this);
                           }

                           SSLEngineResult.HandshakeStatus handshakeStatus = SslConnection.this._sslEngine.getHandshakeStatus();
                           SSLEngineResult.HandshakeStatus unwrapHandshakeStatus = unwrapResult.getHandshakeStatus();
                           SSLEngineResult.Status unwrapResultStatus = unwrapResult.getStatus();
                           this._underFlown = unwrapResultStatus == Status.BUFFER_UNDERFLOW || unwrapResultStatus == Status.OK && unwrapResult.bytesConsumed() == 0 && unwrapResult.bytesProduced() == 0;
                           if (this._underFlown) {
                              if (net_filled < 0 && SslConnection.this._sslEngine.getUseClientMode()) {
                                 this.closeInbound();
                              }

                              if (net_filled <= 0) {
                                 int var44 = net_filled;
                                 return var44;
                              }
                           }

                           switch (unwrapResultStatus) {
                              case CLOSED:
                                 switch (handshakeStatus) {
                                    case NOT_HANDSHAKING:
                                       byte var43 = -1;
                                       return var43;
                                    case NEED_TASK:
                                       SslConnection.this._sslEngine.getDelegatedTask().run();
                                       continue;
                                    case NEED_WRAP:
                                       byte var42 = -1;
                                       return var42;
                                    case NEED_UNWRAP:
                                       byte var41 = -1;
                                       return var41;
                                    default:
                                       throw new IllegalStateException();
                                 }
                              case BUFFER_UNDERFLOW:
                              case OK:
                                 if (unwrapHandshakeStatus == HandshakeStatus.FINISHED) {
                                    this.handshakeFinished();
                                 }

                                 if (!this.allowRenegotiate(handshakeStatus)) {
                                    byte var40 = -1;
                                    return var40;
                                 }

                                 if (unwrapResult.bytesProduced() > 0) {
                                    if (app_in == buffer) {
                                       int var39 = unwrapResult.bytesProduced();
                                       return var39;
                                    }

                                    int var38 = BufferUtil.append(buffer, SslConnection.this._decryptedInput);
                                    return var38;
                                 }

                                 switch (handshakeStatus) {
                                    case NOT_HANDSHAKING:
                                       if (this._underFlown) {
                                          continue label902;
                                       }
                                       continue;
                                    case NEED_TASK:
                                       SslConnection.this._sslEngine.getDelegatedTask().run();
                                       continue;
                                    case NEED_WRAP:
                                       if (this._flushRequiresFillToProgress) {
                                          byte var37 = 0;
                                          return var37;
                                       }

                                       this._fillRequiresFlushToProgress = true;
                                       this.flush(BufferUtil.EMPTY_BUFFER);
                                       if (!BufferUtil.isEmpty(SslConnection.this._encryptedOutput)) {
                                          byte var11 = 0;
                                          return var11;
                                       }

                                       this._fillRequiresFlushToProgress = false;
                                       if (this._underFlown) {
                                          continue label902;
                                       }
                                       continue;
                                    case NEED_UNWRAP:
                                       if (this._underFlown) {
                                          continue label902;
                                       }
                                       continue;
                                    default:
                                       throw new IllegalStateException();
                                 }
                              default:
                                 throw new IllegalStateException();
                           }
                        }
                     }
                  }

                  var36 = BufferUtil.append(buffer, SslConnection.this._decryptedInput);
               } catch (SSLHandshakeException x) {
                  this.notifyHandshakeFailed(SslConnection.this._sslEngine, x);
                  failure = x;
                  throw x;
               } catch (SSLException var30) {
                  SSLException x = var30;
                  if (!this._handshaken) {
                     x = (SSLException)(new SSLHandshakeException(var30.getMessage())).initCause(var30);
                     this.notifyHandshakeFailed(SslConnection.this._sslEngine, x);
                  }

                  failure = x;
                  throw x;
               } catch (Throwable x) {
                  failure = x;
                  throw x;
               } finally {
                  if (this._flushRequiresFillToProgress) {
                     this._flushRequiresFillToProgress = false;
                     SslConnection.this.getExecutor().execute((Runnable)(failure == null ? SslConnection.this._runCompletWrite : new FailWrite(failure)));
                  }

                  if (SslConnection.this._encryptedInput != null && !SslConnection.this._encryptedInput.hasRemaining()) {
                     SslConnection.this._bufferPool.release(SslConnection.this._encryptedInput);
                     SslConnection.this._encryptedInput = null;
                  }

                  if (SslConnection.this._decryptedInput != null && !SslConnection.this._decryptedInput.hasRemaining()) {
                     SslConnection.this._bufferPool.release(SslConnection.this._decryptedInput);
                     SslConnection.this._decryptedInput = null;
                  }

               }

               return var36;
            }
         } catch (Throwable x) {
            this.close(x);
            throw x;
         }
      }

      private void handshakeFinished() {
         if (this._handshaken) {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug("Renegotiated {}", SslConnection.this);
            }

            if (SslConnection.this._renegotiationLimit > 0) {
               SslConnection.this._renegotiationLimit--;
            }
         } else {
            this._handshaken = true;
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug("{} handshake succeeded {}/{} {}", SslConnection.this._sslEngine.getUseClientMode() ? "client" : "resumed server", SslConnection.this._sslEngine.getSession().getProtocol(), SslConnection.this._sslEngine.getSession().getCipherSuite(), SslConnection.this);
            }

            this.notifyHandshakeSucceeded(SslConnection.this._sslEngine);
         }

      }

      private boolean allowRenegotiate(SSLEngineResult.HandshakeStatus handshakeStatus) {
         if (this._handshaken && handshakeStatus != HandshakeStatus.NOT_HANDSHAKING) {
            if (!SslConnection.this.isRenegotiationAllowed()) {
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("Renegotiation denied {}", SslConnection.this);
               }

               this.shutdownInput();
               return false;
            } else if (SslConnection.this.getRenegotiationLimit() == 0) {
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("Renegotiation limit exceeded {}", SslConnection.this);
               }

               this.shutdownInput();
               return false;
            } else {
               return true;
            }
         } else {
            return true;
         }
      }

      private void shutdownInput() {
         try {
            SslConnection.this._sslEngine.closeInbound();
         } catch (Throwable x) {
            SslConnection.LOG.ignore(x);
         }

      }

      private void closeInbound() throws SSLException {
         SSLEngineResult.HandshakeStatus handshakeStatus = SslConnection.this._sslEngine.getHandshakeStatus();

         try {
            SslConnection.this._sslEngine.closeInbound();
         } catch (SSLException x) {
            if (handshakeStatus == HandshakeStatus.NOT_HANDSHAKING && !SslConnection.this.isAllowMissingCloseMessage()) {
               throw x;
            }

            SslConnection.LOG.ignore(x);
         }

      }

      public boolean flush(ByteBuffer... appOuts) throws IOException {
         if (SslConnection.LOG.isDebugEnabled()) {
            for(ByteBuffer b : appOuts) {
               SslConnection.LOG.debug("flush {} {}", BufferUtil.toHexSummary(b), SslConnection.this);
            }
         }

         try {
            synchronized(this) {
               int pos;
               try {
                  if (!this._cannotAcceptMoreAppDataToFlush) {
                     if (SslConnection.this._encryptedOutput == null) {
                        SslConnection.this._encryptedOutput = SslConnection.this._bufferPool.acquire(SslConnection.this._sslEngine.getSession().getPacketBufferSize(), true);
                     }

                     boolean allConsumed;
                     label507:
                     while(true) {
                        BufferUtil.compact(SslConnection.this._encryptedOutput);
                        pos = BufferUtil.flipToFill(SslConnection.this._encryptedOutput);

                        SSLEngineResult wrapResult;
                        try {
                           wrapResult = SslConnection.this._sslEngine.wrap(appOuts, SslConnection.this._encryptedOutput);
                        } finally {
                           BufferUtil.flipToFlush(SslConnection.this._encryptedOutput, pos);
                        }

                        if (SslConnection.LOG.isDebugEnabled()) {
                           SslConnection.LOG.debug("wrap {} {}", wrapResult.toString().replace('\n', ' '), SslConnection.this);
                        }

                        SSLEngineResult.Status wrapResultStatus = wrapResult.getStatus();
                        allConsumed = true;

                        for(ByteBuffer b : appOuts) {
                           if (BufferUtil.hasContent(b)) {
                              allConsumed = false;
                           }
                        }

                        switch (wrapResultStatus) {
                           case CLOSED:
                              if (BufferUtil.hasContent(SslConnection.this._encryptedOutput)) {
                                 this._cannotAcceptMoreAppDataToFlush = true;
                                 SslConnection.this.getEndPoint().flush(SslConnection.this._encryptedOutput);
                                 SslConnection.this.getEndPoint().shutdownOutput();
                                 if (BufferUtil.hasContent(SslConnection.this._encryptedOutput)) {
                                    boolean var32 = false;
                                    return var32;
                                 }
                              } else {
                                 SslConnection.this.getEndPoint().shutdownOutput();
                              }

                              boolean var33 = allConsumed;
                              return var33;
                           case BUFFER_UNDERFLOW:
                              throw new IllegalStateException();
                           default:
                              if (SslConnection.LOG.isDebugEnabled()) {
                                 SslConnection.LOG.debug("wrap {} {} {}", wrapResultStatus, BufferUtil.toHexSummary(SslConnection.this._encryptedOutput), SslConnection.this);
                              }

                              if (wrapResult.getHandshakeStatus() == HandshakeStatus.FINISHED) {
                                 this.handshakeFinished();
                              }

                              SSLEngineResult.HandshakeStatus handshakeStatus = SslConnection.this._sslEngine.getHandshakeStatus();
                              if (!this.allowRenegotiate(handshakeStatus)) {
                                 SslConnection.this.getEndPoint().shutdownOutput();
                                 boolean var37 = allConsumed;
                                 return var37;
                              }

                              if (BufferUtil.hasContent(SslConnection.this._encryptedOutput) && !SslConnection.this.getEndPoint().flush(SslConnection.this._encryptedOutput)) {
                                 SslConnection.this.getEndPoint().flush(SslConnection.this._encryptedOutput);
                              }

                              switch (handshakeStatus) {
                                 case NOT_HANDSHAKING:
                                    if (allConsumed || wrapResult.getHandshakeStatus() != HandshakeStatus.FINISHED || !BufferUtil.isEmpty(SslConnection.this._encryptedOutput)) {
                                       boolean var36 = allConsumed && BufferUtil.isEmpty(SslConnection.this._encryptedOutput);
                                       return var36;
                                    }
                                    break;
                                 case NEED_TASK:
                                    SslConnection.this._sslEngine.getDelegatedTask().run();
                                 case NEED_WRAP:
                                 default:
                                    break;
                                 case NEED_UNWRAP:
                                    if (this._fillRequiresFlushToProgress || this.getFillInterest().isInterested()) {
                                       break label507;
                                    }

                                    this._flushRequiresFillToProgress = true;
                                    this.fill(BufferUtil.EMPTY_BUFFER);
                                    if (SslConnection.this._sslEngine.getHandshakeStatus() != HandshakeStatus.NEED_WRAP) {
                                       break label507;
                                    }
                                    break;
                                 case FINISHED:
                                    throw new IllegalStateException();
                              }
                        }
                     }

                     boolean var35 = allConsumed && BufferUtil.isEmpty(SslConnection.this._encryptedOutput);
                     return var35;
                  }

                  if (SslConnection.this._sslEngine.isOutboundDone()) {
                     throw new EofException(new ClosedChannelException());
                  }

                  pos = 0;
               } catch (SSLHandshakeException x) {
                  this.notifyHandshakeFailed(SslConnection.this._sslEngine, x);
                  throw x;
               } finally {
                  this.releaseEncryptedOutputBuffer();
               }

               return (boolean)pos;
            }
         } catch (Throwable x) {
            this.close(x);
            throw x;
         }
      }

      private void releaseEncryptedOutputBuffer() {
         if (!Thread.holdsLock(this)) {
            throw new IllegalStateException();
         } else {
            if (SslConnection.this._encryptedOutput != null && !SslConnection.this._encryptedOutput.hasRemaining()) {
               SslConnection.this._bufferPool.release(SslConnection.this._encryptedOutput);
               SslConnection.this._encryptedOutput = null;
            }

         }
      }

      public void shutdownOutput() {
         try {
            boolean flush = false;
            boolean close = false;
            synchronized(SslConnection.this._decryptedEndPoint) {
               boolean ishut = this.isInputShutdown();
               boolean oshut = this.isOutputShutdown();
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("shutdownOutput: oshut={}, ishut={} {}", oshut, ishut, SslConnection.this);
               }

               if (oshut) {
                  return;
               }

               if (!SslConnection.this._closedOutbound) {
                  SslConnection.this._closedOutbound = true;
                  SslConnection.this._sslEngine.closeOutbound();
                  flush = true;
               }

               if (ishut) {
                  close = true;
               }
            }

            if (flush) {
               this.flush(BufferUtil.EMPTY_BUFFER);
            }

            if (close) {
               SslConnection.this.getEndPoint().close();
            } else {
               this.ensureFillInterested();
            }
         } catch (Throwable x) {
            SslConnection.LOG.ignore(x);
            SslConnection.this.getEndPoint().close();
         }

      }

      private void ensureFillInterested() {
         if (this.getFillInterest().isCallbackNonBlocking()) {
            SslConnection.this.tryFillInterested(SslConnection.this._nonBlockingReadCallback);
         } else {
            SslConnection.this.tryFillInterested();
         }

      }

      public boolean isOutputShutdown() {
         return SslConnection.this._sslEngine.isOutboundDone() || SslConnection.this.getEndPoint().isOutputShutdown();
      }

      public void close() {
         this.shutdownOutput();
         SslConnection.this.getEndPoint().close();
         super.close();
      }

      protected void close(Throwable failure) {
         this.shutdownOutput();
         SslConnection.this.getEndPoint().close();
         super.close(failure);
      }

      public Object getTransport() {
         return SslConnection.this.getEndPoint();
      }

      public boolean isInputShutdown() {
         return SslConnection.this._sslEngine.isInboundDone();
      }

      private void notifyHandshakeSucceeded(SSLEngine sslEngine) {
         SslHandshakeListener.Event event = null;

         for(SslHandshakeListener listener : SslConnection.this.handshakeListeners) {
            if (event == null) {
               event = new SslHandshakeListener.Event(sslEngine);
            }

            try {
               listener.handshakeSucceeded(event);
            } catch (Throwable x) {
               SslConnection.LOG.info("Exception while notifying listener " + listener, x);
            }
         }

      }

      private void notifyHandshakeFailed(SSLEngine sslEngine, Throwable failure) {
         SslHandshakeListener.Event event = null;

         for(SslHandshakeListener listener : SslConnection.this.handshakeListeners) {
            if (event == null) {
               event = new SslHandshakeListener.Event(sslEngine);
            }

            try {
               listener.handshakeFailed(event, failure);
            } catch (Throwable x) {
               SslConnection.LOG.info("Exception while notifying listener " + listener, x);
            }
         }

      }

      public String toString() {
         return super.toString() + "->" + SslConnection.this.getEndPoint().toString();
      }

      private class FailWrite implements Runnable {
         private final Throwable failure;

         private FailWrite(Throwable failure) {
            this.failure = failure;
         }

         public void run() {
            DecryptedEndPoint.this.getWriteFlusher().onFail(this.failure);
         }
      }
   }
}
