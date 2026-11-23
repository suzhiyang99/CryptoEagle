package org.spark_project.jetty.http;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class HttpGenerator {
   private static final Logger LOG = Log.getLogger(HttpGenerator.class);
   public static final boolean __STRICT = Boolean.getBoolean("org.spark_project.jetty.http.HttpGenerator.STRICT");
   private static final byte[] __colon_space = new byte[]{58, 32};
   private static final HttpHeaderValue[] CLOSE;
   public static final MetaData.Response CONTINUE_100_INFO;
   public static final MetaData.Response PROGRESS_102_INFO;
   public static final MetaData.Response RESPONSE_500_INFO;
   public static final int CHUNK_SIZE = 12;
   private State _state;
   private HttpTokens.EndOfContent _endOfContent;
   private long _contentPrepared;
   private boolean _noContent;
   private Boolean _persistent;
   private final int _send;
   private static final int SEND_SERVER = 1;
   private static final int SEND_XPOWEREDBY = 2;
   private static final Set<String> __assumedContentMethods;
   private boolean _needCRLF;
   private static final byte[] LAST_CHUNK;
   private static final byte[] CONTENT_LENGTH_0;
   private static final byte[] CONNECTION_KEEP_ALIVE;
   private static final byte[] CONNECTION_CLOSE;
   private static final byte[] HTTP_1_1_SPACE;
   private static final byte[] CRLF;
   private static final byte[] TRANSFER_ENCODING_CHUNKED;
   private static final byte[][] SEND;
   private static final PreparedResponse[] __preprepared;

   public static void setJettyVersion(String serverVersion) {
      SEND[1] = StringUtil.getBytes("Server: " + serverVersion + "\r\n");
      SEND[2] = StringUtil.getBytes("X-Powered-By: " + serverVersion + "\r\n");
      SEND[3] = StringUtil.getBytes("Server: " + serverVersion + "\r\nX-Powered-By: " + serverVersion + "\r\n");
   }

   public HttpGenerator() {
      this(false, false);
   }

   public HttpGenerator(boolean sendServerVersion, boolean sendXPoweredBy) {
      this._state = HttpGenerator.State.START;
      this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
      this._contentPrepared = 0L;
      this._noContent = false;
      this._persistent = null;
      this._needCRLF = false;
      this._send = (sendServerVersion ? 1 : 0) | (sendXPoweredBy ? 2 : 0);
   }

   public void reset() {
      this._state = HttpGenerator.State.START;
      this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
      this._noContent = false;
      this._persistent = null;
      this._contentPrepared = 0L;
      this._needCRLF = false;
   }

   /** @deprecated */
   @Deprecated
   public boolean getSendServerVersion() {
      return (this._send & 1) != 0;
   }

   /** @deprecated */
   @Deprecated
   public void setSendServerVersion(boolean sendServerVersion) {
      throw new UnsupportedOperationException();
   }

   public State getState() {
      return this._state;
   }

   public boolean isState(State state) {
      return this._state == state;
   }

   public boolean isIdle() {
      return this._state == HttpGenerator.State.START;
   }

   public boolean isEnd() {
      return this._state == HttpGenerator.State.END;
   }

   public boolean isCommitted() {
      return this._state.ordinal() >= HttpGenerator.State.COMMITTED.ordinal();
   }

   public boolean isChunking() {
      return this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT;
   }

   public boolean isNoContent() {
      return this._noContent;
   }

   public void setPersistent(boolean persistent) {
      this._persistent = persistent;
   }

   public boolean isPersistent() {
      return Boolean.TRUE.equals(this._persistent);
   }

   public boolean isWritten() {
      return this._contentPrepared > 0L;
   }

   public long getContentPrepared() {
      return this._contentPrepared;
   }

   public void abort() {
      this._persistent = false;
      this._state = HttpGenerator.State.END;
      this._endOfContent = null;
   }

   public Result generateRequest(MetaData.Request info, ByteBuffer header, ByteBuffer chunk, ByteBuffer content, boolean last) throws IOException {
      switch (this._state) {
         case START:
            if (info == null) {
               return HttpGenerator.Result.NEED_INFO;
            } else if (header == null) {
               return HttpGenerator.Result.NEED_HEADER;
            } else {
               if (this._persistent == null) {
                  this._persistent = info.getHttpVersion().ordinal() > HttpVersion.HTTP_1_0.ordinal();
                  if (!this._persistent && HttpMethod.CONNECT.is(info.getMethod())) {
                     this._persistent = true;
                  }
               }

               int pos = BufferUtil.flipToFill(header);

               Result var16;
               try {
                  this.generateRequestLine(info, header);
                  if (info.getHttpVersion() == HttpVersion.HTTP_0_9) {
                     throw new BadMessageException(500, "HTTP/0.9 not supported");
                  }

                  this.generateHeaders(info, header, content, last);
                  boolean expect100 = info.getFields().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
                  if (expect100) {
                     this._state = HttpGenerator.State.COMMITTED;
                  } else {
                     int len = BufferUtil.length(content);
                     if (len > 0) {
                        this._contentPrepared += (long)len;
                        if (this.isChunking()) {
                           this.prepareChunk(header, len);
                        }
                     }

                     this._state = last ? HttpGenerator.State.COMPLETING : HttpGenerator.State.COMMITTED;
                  }

                  var16 = HttpGenerator.Result.FLUSH;
               } catch (Exception var12) {
                  String message = var12 instanceof BufferOverflowException ? "Request header too large" : var12.getMessage();
                  throw new BadMessageException(500, message, var12);
               } finally {
                  BufferUtil.flipToFlush(header, pos);
               }

               return var16;
            }
         case COMMITTED:
            int len = BufferUtil.length(content);
            if (len > 0) {
               if (this.isChunking()) {
                  if (chunk == null) {
                     return HttpGenerator.Result.NEED_CHUNK;
                  }

                  BufferUtil.clearToFill(chunk);
                  this.prepareChunk(chunk, len);
                  BufferUtil.flipToFlush(chunk, 0);
               }

               this._contentPrepared += (long)len;
            }

            if (last) {
               this._state = HttpGenerator.State.COMPLETING;
            }

            return len > 0 ? HttpGenerator.Result.FLUSH : HttpGenerator.Result.CONTINUE;
         case COMPLETING:
            if (BufferUtil.hasContent(content)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("discarding content in COMPLETING");
               }

               BufferUtil.clear(content);
            }

            if (this.isChunking()) {
               if (chunk == null) {
                  return HttpGenerator.Result.NEED_CHUNK;
               }

               BufferUtil.clearToFill(chunk);
               this.prepareChunk(chunk, 0);
               BufferUtil.flipToFlush(chunk, 0);
               this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
               return HttpGenerator.Result.FLUSH;
            }

            this._state = HttpGenerator.State.END;
            return Boolean.TRUE.equals(this._persistent) ? HttpGenerator.Result.DONE : HttpGenerator.Result.SHUTDOWN_OUT;
         case END:
            if (BufferUtil.hasContent(content)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("discarding content in COMPLETING");
               }

               BufferUtil.clear(content);
            }

            return HttpGenerator.Result.DONE;
         default:
            throw new IllegalStateException();
      }
   }

   public Result generateResponse(MetaData.Response info, ByteBuffer header, ByteBuffer chunk, ByteBuffer content, boolean last) throws IOException {
      return this.generateResponse(info, false, header, chunk, content, last);
   }

   public Result generateResponse(MetaData.Response info, boolean head, ByteBuffer header, ByteBuffer chunk, ByteBuffer content, boolean last) throws IOException {
      switch (this._state) {
         case START:
            if (info == null) {
               return HttpGenerator.Result.NEED_INFO;
            } else {
               HttpVersion version = info.getHttpVersion();
               if (version == null) {
                  throw new BadMessageException(500, "No version");
               } else {
                  switch (version) {
                     case HTTP_1_0:
                        if (this._persistent == null) {
                           this._persistent = Boolean.FALSE;
                        }
                        break;
                     case HTTP_1_1:
                        if (this._persistent == null) {
                           this._persistent = Boolean.TRUE;
                        }
                        break;
                     default:
                        this._persistent = false;
                        this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
                        if (BufferUtil.hasContent(content)) {
                           this._contentPrepared += (long)content.remaining();
                        }

                        this._state = last ? HttpGenerator.State.COMPLETING : HttpGenerator.State.COMMITTED;
                        return HttpGenerator.Result.FLUSH;
                  }

                  if (header == null) {
                     return HttpGenerator.Result.NEED_HEADER;
                  } else {
                     int pos = BufferUtil.flipToFill(header);

                     try {
                        this.generateResponseLine(info, header);
                        int status = info.getStatus();
                        if (status >= 100 && status < 200) {
                           this._noContent = true;
                           if (status != 101) {
                              header.put(HttpTokens.CRLF);
                              this._state = HttpGenerator.State.COMPLETING_1XX;
                              Result var18 = HttpGenerator.Result.FLUSH;
                              return var18;
                           }
                        } else if (status == 204 || status == 304) {
                           this._noContent = true;
                        }

                        this.generateHeaders(info, header, content, last);
                        int len = BufferUtil.length(content);
                        if (len > 0) {
                           this._contentPrepared += (long)len;
                           if (this.isChunking() && !head) {
                              this.prepareChunk(header, len);
                           }
                        }

                        this._state = last ? HttpGenerator.State.COMPLETING : HttpGenerator.State.COMMITTED;
                        return HttpGenerator.Result.FLUSH;
                     } catch (Exception var14) {
                        String message = var14 instanceof BufferOverflowException ? "Response header too large" : var14.getMessage();
                        throw new BadMessageException(500, message, var14);
                     } finally {
                        BufferUtil.flipToFlush(header, pos);
                     }
                  }
               }
            }
         case COMMITTED:
            int len = BufferUtil.length(content);
            if (len > 0) {
               if (this.isChunking()) {
                  if (chunk == null) {
                     return HttpGenerator.Result.NEED_CHUNK;
                  }

                  BufferUtil.clearToFill(chunk);
                  this.prepareChunk(chunk, len);
                  BufferUtil.flipToFlush(chunk, 0);
               }

               this._contentPrepared += (long)len;
            }

            if (last) {
               this._state = HttpGenerator.State.COMPLETING;
               return len > 0 ? HttpGenerator.Result.FLUSH : HttpGenerator.Result.CONTINUE;
            }

            return len > 0 ? HttpGenerator.Result.FLUSH : HttpGenerator.Result.DONE;
         case COMPLETING:
            if (BufferUtil.hasContent(content)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("discarding content in COMPLETING");
               }

               BufferUtil.clear(content);
            }

            if (this.isChunking()) {
               if (chunk == null) {
                  return HttpGenerator.Result.NEED_CHUNK;
               }

               BufferUtil.clearToFill(chunk);
               this.prepareChunk(chunk, 0);
               BufferUtil.flipToFlush(chunk, 0);
               this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
               return HttpGenerator.Result.FLUSH;
            }

            this._state = HttpGenerator.State.END;
            return Boolean.TRUE.equals(this._persistent) ? HttpGenerator.Result.DONE : HttpGenerator.Result.SHUTDOWN_OUT;
         case END:
            if (BufferUtil.hasContent(content)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("discarding content in COMPLETING");
               }

               BufferUtil.clear(content);
            }

            return HttpGenerator.Result.DONE;
         case COMPLETING_1XX:
            this.reset();
            return HttpGenerator.Result.DONE;
         default:
            throw new IllegalStateException();
      }
   }

   private void prepareChunk(ByteBuffer chunk, int remaining) {
      if (this._needCRLF) {
         BufferUtil.putCRLF(chunk);
      }

      if (remaining > 0) {
         BufferUtil.putHexInt(chunk, remaining);
         BufferUtil.putCRLF(chunk);
         this._needCRLF = true;
      } else {
         chunk.put(LAST_CHUNK);
         this._needCRLF = false;
      }

   }

   private void generateRequestLine(MetaData.Request request, ByteBuffer header) {
      header.put(StringUtil.getBytes(request.getMethod()));
      header.put((byte)32);
      header.put(StringUtil.getBytes(request.getURIString()));
      header.put((byte)32);
      header.put(request.getHttpVersion().toBytes());
      header.put(HttpTokens.CRLF);
   }

   private void generateResponseLine(MetaData.Response response, ByteBuffer header) {
      int status = response.getStatus();
      PreparedResponse preprepared = status < __preprepared.length ? __preprepared[status] : null;
      String reason = response.getReason();
      if (preprepared != null) {
         if (reason == null) {
            header.put(preprepared._responseLine);
         } else {
            header.put(preprepared._schemeCode);
            header.put(this.getReasonBytes(reason));
            header.put(HttpTokens.CRLF);
         }
      } else {
         header.put(HTTP_1_1_SPACE);
         header.put((byte)(48 + status / 100));
         header.put((byte)(48 + status % 100 / 10));
         header.put((byte)(48 + status % 10));
         header.put((byte)32);
         if (reason == null) {
            header.put((byte)(48 + status / 100));
            header.put((byte)(48 + status % 100 / 10));
            header.put((byte)(48 + status % 10));
         } else {
            header.put(this.getReasonBytes(reason));
         }

         header.put(HttpTokens.CRLF);
      }

   }

   private byte[] getReasonBytes(String reason) {
      if (reason.length() > 1024) {
         reason = reason.substring(0, 1024);
      }

      byte[] _bytes = StringUtil.getBytes(reason);
      int i = _bytes.length;

      while(i-- > 0) {
         if (_bytes[i] == 13 || _bytes[i] == 10) {
            _bytes[i] = 63;
         }
      }

      return _bytes;
   }

   private void generateHeaders(MetaData _info, ByteBuffer header, ByteBuffer content, boolean last) {
      MetaData.Request request = _info instanceof MetaData.Request ? (MetaData.Request)_info : null;
      MetaData.Response response = _info instanceof MetaData.Response ? (MetaData.Response)_info : null;
      int send = this._send;
      HttpField transfer_encoding = null;
      boolean keep_alive = false;
      boolean close = false;
      boolean content_type = false;
      StringBuilder connection = null;
      long content_length = _info.getContentLength();
      HttpFields fields = _info.getFields();
      if (fields != null) {
         int n = fields.size();

         for(int f = 0; f < n; ++f) {
            HttpField field = fields.getField(f);
            String v = field.getValue();
            if (v != null && v.length() != 0) {
               HttpHeader h = field.getHeader();
               if (h == null) {
                  putTo(field, header);
               } else {
                  switch (h) {
                     case CONTENT_LENGTH:
                        this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
                        if (content_length < 0L) {
                           content_length = Long.valueOf(field.getValue());
                        }
                        break;
                     case CONTENT_TYPE:
                        content_type = true;
                        putTo(field, header);
                        break;
                     case TRANSFER_ENCODING:
                        if (_info.getHttpVersion() == HttpVersion.HTTP_1_1) {
                           transfer_encoding = field;
                        }
                        break;
                     case CONNECTION:
                        if (request != null) {
                           putTo(field, header);
                        }

                        HttpHeaderValue[] values = HttpHeaderValue.CLOSE.is(field.getValue()) ? CLOSE : new HttpHeaderValue[]{(HttpHeaderValue)HttpHeaderValue.CACHE.get(field.getValue())};
                        String[] split = null;
                        if (values[0] == null) {
                           split = StringUtil.csvSplit(field.getValue());
                           if (split.length > 0) {
                              values = new HttpHeaderValue[split.length];

                              for(int i = 0; i < split.length; ++i) {
                                 values[i] = (HttpHeaderValue)HttpHeaderValue.CACHE.get(split[i]);
                              }
                           }
                        }

                        for(int i = 0; i < values.length; ++i) {
                           HttpHeaderValue value = values[i];
                           switch (value == null ? HttpHeaderValue.UNKNOWN : value) {
                              case UPGRADE:
                                 header.put(HttpHeader.CONNECTION.getBytesColonSpace()).put(HttpHeader.UPGRADE.getBytes());
                                 header.put(CRLF);
                                 break;
                              case CLOSE:
                                 close = true;
                                 this._persistent = false;
                                 if (response != null && this._endOfContent == HttpTokens.EndOfContent.UNKNOWN_CONTENT) {
                                    this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
                                 }
                                 break;
                              case KEEP_ALIVE:
                                 if (_info.getHttpVersion() == HttpVersion.HTTP_1_0) {
                                    keep_alive = true;
                                    if (response != null) {
                                       this._persistent = true;
                                    }
                                 }
                                 break;
                              default:
                                 if (connection == null) {
                                    connection = new StringBuilder();
                                 } else {
                                    connection.append(',');
                                 }

                                 connection.append(split == null ? field.getValue() : split[i]);
                           }
                        }
                        break;
                     case SERVER:
                        send &= -2;
                        putTo(field, header);
                        break;
                     default:
                        putTo(field, header);
                  }
               }
            }
         }
      }

      int status = response != null ? response.getStatus() : -1;
      switch (this._endOfContent) {
         case UNKNOWN_CONTENT:
            if (this._contentPrepared == 0L && response != null && this._noContent) {
               this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
            } else if (_info.getContentLength() > 0L) {
               this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
               if ((response != null || content_length > 0L || content_type) && !this._noContent) {
                  header.put(HttpHeader.CONTENT_LENGTH.getBytesColonSpace());
                  BufferUtil.putDecLong(header, content_length);
                  header.put(HttpTokens.CRLF);
               }
            } else if (last) {
               this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
               long actual_length = this._contentPrepared + (long)BufferUtil.length(content);
               if (content_length >= 0L && content_length != actual_length) {
                  throw new BadMessageException(500, "Content-Length header(" + content_length + ") != actual(" + actual_length + ")");
               }

               this.putContentLength(header, actual_length, content_type, request, response);
            } else {
               this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
               if (!this.isPersistent() || _info.getHttpVersion().ordinal() < HttpVersion.HTTP_1_1.ordinal()) {
                  this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
               }
            }
            break;
         case CONTENT_LENGTH:
            this.putContentLength(header, content_length, content_type, request, response);
            break;
         case NO_CONTENT:
            throw new BadMessageException(500);
         case EOF_CONTENT:
            this._persistent = request != null;
         case CHUNKED_CONTENT:
      }

      if (this.isChunking()) {
         if (transfer_encoding != null && !HttpHeaderValue.CHUNKED.toString().equalsIgnoreCase(transfer_encoding.getValue())) {
            String c = transfer_encoding.getValue();
            if (!c.endsWith(HttpHeaderValue.CHUNKED.toString())) {
               throw new BadMessageException(500, "BAD TE");
            }

            putTo(transfer_encoding, header);
         } else {
            header.put(TRANSFER_ENCODING_CHUNKED);
         }
      }

      if (this._endOfContent == HttpTokens.EndOfContent.EOF_CONTENT) {
         keep_alive = false;
         this._persistent = false;
      }

      if (response != null) {
         if (!this.isPersistent() && (close || _info.getHttpVersion().ordinal() > HttpVersion.HTTP_1_0.ordinal())) {
            if (connection == null) {
               header.put(CONNECTION_CLOSE);
            } else {
               header.put(CONNECTION_CLOSE, 0, CONNECTION_CLOSE.length - 2);
               header.put((byte)44);
               header.put(StringUtil.getBytes(connection.toString()));
               header.put(CRLF);
            }
         } else if (keep_alive) {
            if (connection == null) {
               header.put(CONNECTION_KEEP_ALIVE);
            } else {
               header.put(CONNECTION_KEEP_ALIVE, 0, CONNECTION_KEEP_ALIVE.length - 2);
               header.put((byte)44);
               header.put(StringUtil.getBytes(connection.toString()));
               header.put(CRLF);
            }
         } else if (connection != null) {
            header.put(HttpHeader.CONNECTION.getBytesColonSpace());
            header.put(StringUtil.getBytes(connection.toString()));
            header.put(CRLF);
         }
      }

      if (status > 199) {
         header.put(SEND[send]);
      }

      header.put(HttpTokens.CRLF);
   }

   private void putContentLength(ByteBuffer header, long contentLength, boolean contentType, MetaData.Request request, MetaData.Response response) {
      if (contentLength > 0L) {
         header.put(HttpHeader.CONTENT_LENGTH.getBytesColonSpace());
         BufferUtil.putDecLong(header, contentLength);
         header.put(HttpTokens.CRLF);
      } else if (!this._noContent && (contentType || response != null || request != null && __assumedContentMethods.contains(request.getMethod()))) {
         header.put(CONTENT_LENGTH_0);
      }

   }

   public static byte[] getReasonBuffer(int code) {
      PreparedResponse status = code < __preprepared.length ? __preprepared[code] : null;
      return status != null ? status._reason : null;
   }

   public String toString() {
      return String.format("%s@%x{s=%s}", this.getClass().getSimpleName(), this.hashCode(), this._state);
   }

   private static void putSanitisedName(String s, ByteBuffer buffer) {
      int l = s.length();

      for(int i = 0; i < l; ++i) {
         char c = s.charAt(i);
         if (c >= 0 && c <= 255 && c != '\r' && c != '\n' && c != ':') {
            buffer.put((byte)(255 & c));
         } else {
            buffer.put((byte)63);
         }
      }

   }

   private static void putSanitisedValue(String s, ByteBuffer buffer) {
      int l = s.length();

      for(int i = 0; i < l; ++i) {
         char c = s.charAt(i);
         if (c >= 0 && c <= 255 && c != '\r' && c != '\n') {
            buffer.put((byte)(255 & c));
         } else {
            buffer.put((byte)32);
         }
      }

   }

   public static void putTo(HttpField field, ByteBuffer bufferInFillMode) {
      if (field instanceof PreEncodedHttpField) {
         ((PreEncodedHttpField)field).putTo(bufferInFillMode, HttpVersion.HTTP_1_0);
      } else {
         HttpHeader header = field.getHeader();
         if (header != null) {
            bufferInFillMode.put(header.getBytesColonSpace());
            putSanitisedValue(field.getValue(), bufferInFillMode);
         } else {
            putSanitisedName(field.getName(), bufferInFillMode);
            bufferInFillMode.put(__colon_space);
            putSanitisedValue(field.getValue(), bufferInFillMode);
         }

         BufferUtil.putCRLF(bufferInFillMode);
      }

   }

   public static void putTo(HttpFields fields, ByteBuffer bufferInFillMode) {
      for(HttpField field : fields) {
         if (field != null) {
            putTo(field, bufferInFillMode);
         }
      }

      BufferUtil.putCRLF(bufferInFillMode);
   }

   static {
      CLOSE = new HttpHeaderValue[]{HttpHeaderValue.CLOSE};
      CONTINUE_100_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 100, (String)null, (HttpFields)null, -1L);
      PROGRESS_102_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 102, (String)null, (HttpFields)null, -1L);
      RESPONSE_500_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 500, (String)null, new HttpFields() {
         {
            this.put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
         }
      }, 0L);
      __assumedContentMethods = new HashSet(Arrays.asList(HttpMethod.POST.asString(), HttpMethod.PUT.asString()));
      LAST_CHUNK = new byte[]{48, 13, 10, 13, 10};
      CONTENT_LENGTH_0 = StringUtil.getBytes("Content-Length: 0\r\n");
      CONNECTION_KEEP_ALIVE = StringUtil.getBytes("Connection: keep-alive\r\n");
      CONNECTION_CLOSE = StringUtil.getBytes("Connection: close\r\n");
      HTTP_1_1_SPACE = StringUtil.getBytes(HttpVersion.HTTP_1_1 + " ");
      CRLF = StringUtil.getBytes("\r\n");
      TRANSFER_ENCODING_CHUNKED = StringUtil.getBytes("Transfer-Encoding: chunked\r\n");
      SEND = new byte[][]{new byte[0], StringUtil.getBytes("Server: Jetty(9.x.x)\r\n"), StringUtil.getBytes("X-Powered-By: Jetty(9.x.x)\r\n"), StringUtil.getBytes("Server: Jetty(9.x.x)\r\nX-Powered-By: Jetty(9.x.x)\r\n")};
      __preprepared = new PreparedResponse[512];
      int versionLength = HttpVersion.HTTP_1_1.toString().length();

      for(int i = 0; i < __preprepared.length; ++i) {
         HttpStatus.Code code = HttpStatus.getCode(i);
         if (code != null) {
            String reason = code.getMessage();
            byte[] line = new byte[versionLength + 5 + reason.length() + 2];
            HttpVersion.HTTP_1_1.toBuffer().get(line, 0, versionLength);
            line[versionLength + 0] = 32;
            line[versionLength + 1] = (byte)(48 + i / 100);
            line[versionLength + 2] = (byte)(48 + i % 100 / 10);
            line[versionLength + 3] = (byte)(48 + i % 10);
            line[versionLength + 4] = 32;

            for(int j = 0; j < reason.length(); ++j) {
               line[versionLength + 5 + j] = (byte)reason.charAt(j);
            }

            line[versionLength + 5 + reason.length()] = 13;
            line[versionLength + 6 + reason.length()] = 10;
            __preprepared[i] = new PreparedResponse();
            __preprepared[i]._schemeCode = Arrays.copyOfRange(line, 0, versionLength + 5);
            __preprepared[i]._reason = Arrays.copyOfRange(line, versionLength + 5, line.length - 2);
            __preprepared[i]._responseLine = line;
         }
      }

   }

   public static enum State {
      START,
      COMMITTED,
      COMPLETING,
      COMPLETING_1XX,
      END;
   }

   public static enum Result {
      NEED_CHUNK,
      NEED_INFO,
      NEED_HEADER,
      FLUSH,
      CONTINUE,
      SHUTDOWN_OUT,
      DONE;
   }

   private static class PreparedResponse {
      byte[] _reason;
      byte[] _schemeCode;
      byte[] _responseLine;

      private PreparedResponse() {
      }
   }
}
