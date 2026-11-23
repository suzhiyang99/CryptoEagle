package org.apache.spark.rpc.netty;

import java.nio.ByteBuffer;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.client.RpcResponseCallback;
import org.apache.spark.network.client.TransportClient;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Eh!B\u0001\u0003\u0001\na!\u0001\u0005*qG>+HOY8y\u001b\u0016\u001c8/Y4f\u0015\t\u0019A!A\u0003oKR$\u0018P\u0003\u0002\u0006\r\u0005\u0019!\u000f]2\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001cr\u0001A\u0007\u0014/})\u0003\u0006\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003)Ui\u0011AA\u0005\u0003-\t\u0011QbT;uE>DX*Z:tC\u001e,\u0007C\u0001\r\u001e\u001b\u0005I\"B\u0001\u000e\u001c\u0003\u0019\u0019G.[3oi*\u0011ADB\u0001\b]\u0016$xo\u001c:l\u0013\tq\u0012DA\nSa\u000e\u0014Vm\u001d9p]N,7)\u00197mE\u0006\u001c7\u000e\u0005\u0002!G5\t\u0011E\u0003\u0002#\r\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002%C\t9Aj\\4hS:<\u0007C\u0001\b'\u0013\t9sBA\u0004Qe>$Wo\u0019;\u0011\u00059I\u0013B\u0001\u0016\u0010\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!a\u0003A!f\u0001\n\u0003q\u0013aB2p]R,g\u000e^\u0002\u0001+\u0005y\u0003C\u0001\u00196\u001b\u0005\t$B\u0001\u001a4\u0003\rq\u0017n\u001c\u0006\u0002i\u0005!!.\u0019<b\u0013\t1\u0014G\u0001\u0006CsR,')\u001e4gKJD\u0001\u0002\u000f\u0001\u0003\u0012\u0003\u0006IaL\u0001\tG>tG/\u001a8uA!A!\b\u0001BK\u0002\u0013\u00051(\u0001\u0006`_:4\u0015-\u001b7ve\u0016,\u0012\u0001\u0010\t\u0005\u001duz4*\u0003\u0002?\u001f\tIa)\u001e8di&|g.\r\t\u0003\u0001\"s!!\u0011$\u000f\u0005\t+U\"A\"\u000b\u0005\u0011k\u0013A\u0002\u001fs_>$h(C\u0001\u0011\u0013\t9u\"A\u0004qC\u000e\\\u0017mZ3\n\u0005%S%!\u0003+ie><\u0018M\u00197f\u0015\t9u\u0002\u0005\u0002\u000f\u0019&\u0011Qj\u0004\u0002\u0005+:LG\u000f\u0003\u0005P\u0001\tE\t\u0015!\u0003=\u0003-yvN\u001c$bS2,(/\u001a\u0011\t\u0011E\u0003!Q3A\u0005\u0002I\u000b!bX8o'V\u001c7-Z:t+\u0005\u0019\u0006#\u0002\bU->Z\u0015BA+\u0010\u0005%1UO\\2uS>t'\u0007\u0005\u0002\u0019/&\u0011\u0001,\u0007\u0002\u0010)J\fgn\u001d9peR\u001cE.[3oi\"A!\f\u0001B\tB\u0003%1+A\u0006`_:\u001cVoY2fgN\u0004\u0003\"\u0002/\u0001\t\u0003i\u0016A\u0002\u001fj]&$h\b\u0006\u0003_?\u0002\f\u0007C\u0001\u000b\u0001\u0011\u0015a3\f1\u00010\u0011\u0015Q4\f1\u0001=\u0011\u0015\t6\f1\u0001T\u0011%Q\u0002\u00011AA\u0002\u0013%1-F\u0001W\u0011%)\u0007\u00011AA\u0002\u0013%a-\u0001\u0006dY&,g\u000e^0%KF$\"aS4\t\u000f!$\u0017\u0011!a\u0001-\u0006\u0019\u0001\u0010J\u0019\t\r)\u0004\u0001\u0015)\u0003W\u0003\u001d\u0019G.[3oi\u0002B\u0011\u0002\u001c\u0001A\u0002\u0003\u0007I\u0011B7\u0002\u0013I,\u0017/^3ti&#W#\u00018\u0011\u00059y\u0017B\u00019\u0010\u0005\u0011auN\\4\t\u0013I\u0004\u0001\u0019!a\u0001\n\u0013\u0019\u0018!\u0004:fcV,7\u000f^%e?\u0012*\u0017\u000f\u0006\u0002Li\"9\u0001.]A\u0001\u0002\u0004q\u0007B\u0002<\u0001A\u0003&a.\u0001\u0006sKF,Xm\u001d;JI\u0002BQ\u0001\u001f\u0001\u0005Be\f\u0001b]3oI^KG\u000f\u001b\u000b\u0003\u0017jDQAG<A\u0002YCQ\u0001 \u0001\u0005\u0002u\f\u0011b\u001c8US6,w.\u001e;\u0015\u0003-Caa \u0001\u0005B\u0005\u0005\u0011!C8o\r\u0006LG.\u001e:f)\rY\u00151\u0001\u0005\u0007\u0003\u000bq\b\u0019A \u0002\u0003\u0015Dq!!\u0003\u0001\t\u0003\nY!A\u0005p]N+8mY3tgR\u00191*!\u0004\t\u000f\u0005=\u0011q\u0001a\u0001_\u0005A!/Z:q_:\u001cX\rC\u0005\u0002\u0014\u0001\t\t\u0011\"\u0001\u0002\u0016\u0005!1m\u001c9z)\u001dq\u0016qCA\r\u00037A\u0001\u0002LA\t!\u0003\u0005\ra\f\u0005\tu\u0005E\u0001\u0013!a\u0001y!A\u0011+!\u0005\u0011\u0002\u0003\u00071\u000bC\u0005\u0002 \u0001\t\n\u0011\"\u0001\u0002\"\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nTCAA\u0012U\ry\u0013QE\u0016\u0003\u0003O\u0001B!!\u000b\u000245\u0011\u00111\u0006\u0006\u0005\u0003[\ty#A\u0005v]\u000eDWmY6fI*\u0019\u0011\u0011G\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u00026\u0005-\"!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"I\u0011\u0011\b\u0001\u0012\u0002\u0013\u0005\u00111H\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\t\tiDK\u0002=\u0003KA\u0011\"!\u0011\u0001#\u0003%\t!a\u0011\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%gU\u0011\u0011Q\t\u0016\u0004'\u0006\u0015\u0002\"CA%\u0001\u0005\u0005I\u0011IA&\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\u0011\u0011Q\n\t\u0005\u0003\u001f\n)&\u0004\u0002\u0002R)\u0019\u00111K\u001a\u0002\t1\fgnZ\u0005\u0005\u0003/\n\tF\u0001\u0004TiJLgn\u001a\u0005\n\u00037\u0002\u0011\u0011!C\u0001\u0003;\nA\u0002\u001d:pIV\u001cG/\u0011:jif,\"!a\u0018\u0011\u00079\t\t'C\u0002\u0002d=\u00111!\u00138u\u0011%\t9\u0007AA\u0001\n\u0003\tI'\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\t\u0005-\u0014\u0011\u000f\t\u0004\u001d\u00055\u0014bAA8\u001f\t\u0019\u0011I\\=\t\u0013!\f)'!AA\u0002\u0005}\u0003\"CA;\u0001\u0005\u0005I\u0011IA<\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014XCAA=!\u0019\tY(!!\u0002l5\u0011\u0011Q\u0010\u0006\u0004\u0003\u007fz\u0011AC2pY2,7\r^5p]&!\u00111QA?\u0005!IE/\u001a:bi>\u0014\b\"CAD\u0001\u0005\u0005I\u0011AAE\u0003!\u0019\u0017M\\#rk\u0006dG\u0003BAF\u0003#\u00032ADAG\u0013\r\tyi\u0004\u0002\b\u0005>|G.Z1o\u0011%A\u0017QQA\u0001\u0002\u0004\tY\u0007C\u0005\u0002\u0016\u0002\t\t\u0011\"\u0011\u0002\u0018\u0006A\u0001.Y:i\u0007>$W\r\u0006\u0002\u0002`!I\u00111\u0014\u0001\u0002\u0002\u0013\u0005\u0013QT\u0001\ti>\u001cFO]5oOR\u0011\u0011Q\n\u0005\n\u0003C\u0003\u0011\u0011!C!\u0003G\u000ba!Z9vC2\u001cH\u0003BAF\u0003KC\u0011\u0002[AP\u0003\u0003\u0005\r!a\u001b\b\u0015\u0005%&!!A\t\u0002\t\tY+\u0001\tSa\u000e|U\u000f\u001e2pq6+7o]1hKB\u0019A#!,\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\u0005\u0005=6#BAW\u0003cC\u0003\u0003CAZ\u0003s{Ch\u00150\u000e\u0005\u0005U&bAA\\\u001f\u00059!/\u001e8uS6,\u0017\u0002BA^\u0003k\u0013\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c84\u0011\u001da\u0016Q\u0016C\u0001\u0003\u007f#\"!a+\t\u0015\u0005m\u0015QVA\u0001\n\u000b\ni\n\u0003\u0006\u0002F\u00065\u0016\u0011!CA\u0003\u000f\fQ!\u00199qYf$rAXAe\u0003\u0017\fi\r\u0003\u0004-\u0003\u0007\u0004\ra\f\u0005\u0007u\u0005\r\u0007\u0019\u0001\u001f\t\rE\u000b\u0019\r1\u0001T\u0011)\t\t.!,\u0002\u0002\u0013\u0005\u00151[\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\t).!9\u0011\u000b9\t9.a7\n\u0007\u0005ewB\u0001\u0004PaRLwN\u001c\t\u0007\u001d\u0005uw\u0006P*\n\u0007\u0005}wB\u0001\u0004UkBdWm\r\u0005\n\u0003G\fy-!AA\u0002y\u000b1\u0001\u001f\u00131\u0011)\t9/!,\u0002\u0002\u0013%\u0011\u0011^\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002lB!\u0011qJAw\u0013\u0011\ty/!\u0015\u0003\r=\u0013'.Z2u\u0001"
)
public class RpcOutboxMessage implements OutboxMessage, RpcResponseCallback, Logging, Product, Serializable {
   private final ByteBuffer content;
   private final Function1<Throwable, BoxedUnit> _onFailure;
   private final Function2<TransportClient, ByteBuffer, BoxedUnit> _onSuccess;
   private TransportClient client;
   private long requestId;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Option<Tuple3<ByteBuffer, Function1<Throwable, BoxedUnit>, Function2<TransportClient, ByteBuffer, BoxedUnit>>> unapply(RpcOutboxMessage var0) {
      return RpcOutboxMessage$.MODULE$.unapply(var0);
   }

   public static RpcOutboxMessage apply(ByteBuffer var0, Function1<Throwable, BoxedUnit> var1, Function2<TransportClient, ByteBuffer, BoxedUnit> var2) {
      return RpcOutboxMessage$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<ByteBuffer, Function1<Throwable, BoxedUnit>, Function2<TransportClient, ByteBuffer, BoxedUnit>>, RpcOutboxMessage> tupled() {
      return RpcOutboxMessage$.MODULE$.tupled();
   }

   public static Function1<ByteBuffer, Function1<Function1<Throwable, BoxedUnit>, Function1<Function2<TransportClient, ByteBuffer, BoxedUnit>, RpcOutboxMessage>>> curried() {
      return RpcOutboxMessage$.MODULE$.curried();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public ByteBuffer content() {
      return this.content;
   }

   public Function1<Throwable, BoxedUnit> _onFailure() {
      return this._onFailure;
   }

   public Function2<TransportClient, ByteBuffer, BoxedUnit> _onSuccess() {
      return this._onSuccess;
   }

   private TransportClient client() {
      return this.client;
   }

   private void client_$eq(TransportClient x$1) {
      this.client = x$1;
   }

   private long requestId() {
      return this.requestId;
   }

   private void requestId_$eq(long x$1) {
      this.requestId = x$1;
   }

   public void sendWith(TransportClient client) {
      this.client_$eq(client);
      this.requestId_$eq(client.sendRpc(this.content(), this));
   }

   public void onTimeout() {
      if (this.client() == null) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Ask timeout before connecting successfully";
            }
         });
      } else {
         this.client().removeRpcRequest(this.requestId());
      }

   }

   public void onFailure(Throwable e) {
      this._onFailure().apply(e);
   }

   public void onSuccess(ByteBuffer response) {
      this._onSuccess().apply(this.client(), response);
   }

   public RpcOutboxMessage copy(ByteBuffer content, Function1<Throwable, BoxedUnit> _onFailure, Function2<TransportClient, ByteBuffer, BoxedUnit> _onSuccess) {
      return new RpcOutboxMessage(content, _onFailure, _onSuccess);
   }

   public ByteBuffer copy$default$1() {
      return this.content();
   }

   public Function1<Throwable, BoxedUnit> copy$default$2() {
      return this._onFailure();
   }

   public Function2<TransportClient, ByteBuffer, BoxedUnit> copy$default$3() {
      return this._onSuccess();
   }

   public String productPrefix() {
      return "RpcOutboxMessage";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.content();
            break;
         case 1:
            var10000 = this._onFailure();
            break;
         case 2:
            var10000 = this._onSuccess();
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof RpcOutboxMessage;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label71: {
            boolean var3;
            if (x$1 instanceof RpcOutboxMessage) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label53: {
                  label62: {
                     RpcOutboxMessage var4 = (RpcOutboxMessage)x$1;
                     ByteBuffer var10000 = this.content();
                     ByteBuffer var5 = var4.content();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label62;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label62;
                     }

                     Function1 var8 = this._onFailure();
                     Function1 var6 = var4._onFailure();
                     if (var8 == null) {
                        if (var6 != null) {
                           break label62;
                        }
                     } else if (!var8.equals(var6)) {
                        break label62;
                     }

                     Function2 var9 = this._onSuccess();
                     Function2 var7 = var4._onSuccess();
                     if (var9 == null) {
                        if (var7 != null) {
                           break label62;
                        }
                     } else if (!var9.equals(var7)) {
                        break label62;
                     }

                     if (var4.canEqual(this)) {
                        var11 = true;
                        break label53;
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label71;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public RpcOutboxMessage(ByteBuffer content, Function1<Throwable, BoxedUnit> _onFailure, Function2<TransportClient, ByteBuffer, BoxedUnit> _onSuccess) {
      this.content = content;
      this._onFailure = _onFailure;
      this._onSuccess = _onSuccess;
      Logging$class.$init$(this);
      Product$class.$init$(this);
   }
}
