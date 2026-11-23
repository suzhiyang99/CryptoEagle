package org.apache.spark.rpc.netty;

import java.nio.ByteBuffer;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.client.TransportClient;
import org.apache.spark.rpc.RpcEnvStoppedException;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterator;
import scala.collection.immutable.Nil$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ed!B\u0001\u0003\u0001\na!aE(oK^\u000b\u0017pT;uE>DX*Z:tC\u001e,'BA\u0002\u0005\u0003\u0015qW\r\u001e;z\u0015\t)a!A\u0002sa\u000eT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0007\u00015\u0019r#\b\u0011\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g!\t!R#D\u0001\u0003\u0013\t1\"AA\u0007PkR\u0014w\u000e_'fgN\fw-\u001a\t\u00031mi\u0011!\u0007\u0006\u00035\u0019\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00039e\u0011q\u0001T8hO&tw\r\u0005\u0002\u000f=%\u0011qd\u0004\u0002\b!J|G-^2u!\tq\u0011%\u0003\u0002#\u001f\ta1+\u001a:jC2L'0\u00192mK\"AA\u0005\u0001BK\u0002\u0013\u0005a%A\u0004d_:$XM\u001c;\u0004\u0001U\tq\u0005\u0005\u0002)[5\t\u0011F\u0003\u0002+W\u0005\u0019a.[8\u000b\u00031\nAA[1wC&\u0011a&\u000b\u0002\u000b\u0005f$XMQ;gM\u0016\u0014\b\u0002\u0003\u0019\u0001\u0005#\u0005\u000b\u0011B\u0014\u0002\u0011\r|g\u000e^3oi\u0002BQA\r\u0001\u0005\u0002M\na\u0001P5oSRtDC\u0001\u001b6!\t!\u0002\u0001C\u0003%c\u0001\u0007q\u0005C\u00038\u0001\u0011\u0005\u0003(\u0001\u0005tK:$w+\u001b;i)\tID\b\u0005\u0002\u000fu%\u00111h\u0004\u0002\u0005+:LG\u000fC\u0003>m\u0001\u0007a(\u0001\u0004dY&,g\u000e\u001e\t\u0003\u007f\rk\u0011\u0001\u0011\u0006\u0003{\u0005S!A\u0011\u0004\u0002\u000f9,Go^8sW&\u0011A\t\u0011\u0002\u0010)J\fgn\u001d9peR\u001cE.[3oi\")a\t\u0001C!\u000f\u0006IqN\u001c$bS2,(/\u001a\u000b\u0003s!CQ!S#A\u0002)\u000b\u0011!\u001a\t\u0003\u0017Ns!\u0001T)\u000f\u00055\u0003V\"\u0001(\u000b\u0005=+\u0013A\u0002\u001fs_>$h(C\u0001\u0011\u0013\t\u0011v\"A\u0004qC\u000e\\\u0017mZ3\n\u0005Q+&!\u0003+ie><\u0018M\u00197f\u0015\t\u0011v\u0002C\u0004X\u0001\u0005\u0005I\u0011\u0001-\u0002\t\r|\u0007/\u001f\u000b\u0003ieCq\u0001\n,\u0011\u0002\u0003\u0007q\u0005C\u0004\\\u0001E\u0005I\u0011\u0001/\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\tQL\u000b\u0002(=.\nq\f\u0005\u0002aK6\t\u0011M\u0003\u0002cG\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003I>\t!\"\u00198o_R\fG/[8o\u0013\t1\u0017MA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq\u0001\u001b\u0001\u0002\u0002\u0013\u0005\u0013.A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0002UB\u00111N\\\u0007\u0002Y*\u0011QnK\u0001\u0005Y\u0006tw-\u0003\u0002pY\n11\u000b\u001e:j]\u001eDq!\u001d\u0001\u0002\u0002\u0013\u0005!/\u0001\u0007qe>$Wo\u0019;Be&$\u00180F\u0001t!\tqA/\u0003\u0002v\u001f\t\u0019\u0011J\u001c;\t\u000f]\u0004\u0011\u0011!C\u0001q\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HCA=}!\tq!0\u0003\u0002|\u001f\t\u0019\u0011I\\=\t\u000fu4\u0018\u0011!a\u0001g\u0006\u0019\u0001\u0010J\u0019\t\u0011}\u0004\u0011\u0011!C!\u0003\u0003\tq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0003\u0007\u0001R!!\u0002\u0002\fel!!a\u0002\u000b\u0007\u0005%q\"\u0001\u0006d_2dWm\u0019;j_:LA!!\u0004\u0002\b\tA\u0011\n^3sCR|'\u000fC\u0005\u0002\u0012\u0001\t\t\u0011\"\u0001\u0002\u0014\u0005A1-\u00198FcV\fG\u000e\u0006\u0003\u0002\u0016\u0005m\u0001c\u0001\b\u0002\u0018%\u0019\u0011\u0011D\b\u0003\u000f\t{w\u000e\\3b]\"AQ0a\u0004\u0002\u0002\u0003\u0007\u0011\u0010C\u0005\u0002 \u0001\t\t\u0011\"\u0011\u0002\"\u0005A\u0001.Y:i\u0007>$W\rF\u0001t\u0011%\t)\u0003AA\u0001\n\u0003\n9#\u0001\u0005u_N#(/\u001b8h)\u0005Q\u0007\"CA\u0016\u0001\u0005\u0005I\u0011IA\u0017\u0003\u0019)\u0017/^1mgR!\u0011QCA\u0018\u0011!i\u0018\u0011FA\u0001\u0002\u0004IxACA\u001a\u0005\u0005\u0005\t\u0012\u0001\u0002\u00026\u0005\u0019rJ\\3XCf|U\u000f\u001e2pq6+7o]1hKB\u0019A#a\u000e\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\u0005\u0005e2#BA\u001c\u0003w\u0001\u0003CBA\u001f\u0003\u0007:C'\u0004\u0002\u0002@)\u0019\u0011\u0011I\b\u0002\u000fI,h\u000e^5nK&!\u0011QIA \u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|g.\r\u0005\be\u0005]B\u0011AA%)\t\t)\u0004\u0003\u0006\u0002&\u0005]\u0012\u0011!C#\u0003OA!\"a\u0014\u00028\u0005\u0005I\u0011QA)\u0003\u0015\t\u0007\u000f\u001d7z)\r!\u00141\u000b\u0005\u0007I\u00055\u0003\u0019A\u0014\t\u0015\u0005]\u0013qGA\u0001\n\u0003\u000bI&A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005m\u0013\u0011\r\t\u0005\u001d\u0005us%C\u0002\u0002`=\u0011aa\u00149uS>t\u0007\"CA2\u0003+\n\t\u00111\u00015\u0003\rAH\u0005\r\u0005\u000b\u0003O\n9$!A\u0005\n\u0005%\u0014a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\u001b\u0011\u0007-\fi'C\u0002\u0002p1\u0014aa\u00142kK\u000e$\b"
)
public class OneWayOutboxMessage implements OutboxMessage, Logging, Product, Serializable {
   private final ByteBuffer content;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Option<ByteBuffer> unapply(OneWayOutboxMessage var0) {
      return OneWayOutboxMessage$.MODULE$.unapply(var0);
   }

   public static OneWayOutboxMessage apply(ByteBuffer var0) {
      return OneWayOutboxMessage$.MODULE$.apply(var0);
   }

   public static <A> Function1<ByteBuffer, A> andThen(Function1<OneWayOutboxMessage, A> var0) {
      return OneWayOutboxMessage$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, OneWayOutboxMessage> compose(Function1<A, ByteBuffer> var0) {
      return OneWayOutboxMessage$.MODULE$.compose(var0);
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

   public void sendWith(TransportClient client) {
      client.send(this.content());
   }

   public void onFailure(Throwable e) {
      if (e instanceof RpcEnvStoppedException) {
         RpcEnvStoppedException var3 = (RpcEnvStoppedException)e;
         this.logDebug(new Serializable(this, var3) {
            public static final long serialVersionUID = 0L;
            private final RpcEnvStoppedException x2$1;

            public final String apply() {
               return this.x2$1.getMessage();
            }

            public {
               this.x2$1 = x2$1;
            }
         });
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else {
         if (e == null) {
            throw new MatchError(e);
         }

         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to send one-way RPC."})))).s(Nil$.MODULE$);
            }
         }, e);
         BoxedUnit var6 = BoxedUnit.UNIT;
      }

   }

   public OneWayOutboxMessage copy(ByteBuffer content) {
      return new OneWayOutboxMessage(content);
   }

   public ByteBuffer copy$default$1() {
      return this.content();
   }

   public String productPrefix() {
      return "OneWayOutboxMessage";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.content();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof OneWayOutboxMessage;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof OneWayOutboxMessage) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     OneWayOutboxMessage var4 = (OneWayOutboxMessage)x$1;
                     ByteBuffer var10000 = this.content();
                     ByteBuffer var5 = var4.content();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label34;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label34;
                     }

                     if (var4.canEqual(this)) {
                        var7 = true;
                        break label35;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label52;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public OneWayOutboxMessage(ByteBuffer content) {
      this.content = content;
      Logging$class.$init$(this);
      Product$class.$init$(this);
   }
}
